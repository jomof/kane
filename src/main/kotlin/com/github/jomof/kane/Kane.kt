package com.github.jomof.kane

import kotlin.reflect.KProperty
import com.github.jomof.kane.Expr.*
import java.math.BigDecimal
import java.math.RoundingMode


sealed class Expr {
    abstract val columns : Int
    abstract val rows : Int

    open operator fun getValue(thisRef: Any?, property: KProperty<*>) = run {
        Named(property.name, nameCells(property.name)).reduce()
    }

    open operator fun get(column : Int, row : Int) : Expr = asDataFrame()[column, row]
    override fun toString() = render()

    class DataFrame(
        override val columns : Int,
        override val rows : Int,
        init:(Coordinate) -> Expr) : Expr() {

        val elements : List<Expr> = (0 until rows * columns).map { index ->
            val (col,row) = indexToColRow(index)
            init(Coordinate(col, row))
        }

        override operator fun get(column : Int, row : Int) : Expr {
            assert(column < columns) { "column $column is >= $columns" }
            assert(row < rows)
            if (elements == null) {
                // Inside construction
                return Element(this, column, row)
            }
            return elements[row * columns + column]
        }
        fun indexToColRow(index : Int) = index % columns to index / columns
    }

    data class Element(val expr : Expr, val column : Int, val row : Int) : Expr() {
        override val columns = 1
        override val rows = 1
        val value : Expr get() = expr[column, row]
        override fun toString() = render()
    }

    data class Cell(val column : Int, val row : Int) : Expr() {
        override val columns = 1
        override val rows = 1
        override fun toString() = render()
    }

    data class NamedCell(val name : String, val column : Int, val row : Int) : Expr() {
        override val columns = 1
        override val rows = 1
        override fun toString() = render()
    }

    class Plus(val elements : List<Expr>) : Expr() {
        override val columns = 1
        override val rows = 1
    }

    class Times(val left : Expr, val right : Expr) : Expr() {
        override val columns = right.columns
        override val rows = left.rows
    }

    class Logistic(val expr : Expr) : Expr() {
        override val columns = expr.columns
        override val rows = expr.rows
    }

    class Power(val expr : Expr, val power : Double) : Expr() {
        override val columns = expr.columns
        override val rows = expr.rows
    }

    class Minus(val left : Expr, val right : Expr) : Expr() {
        init {
            assert(left.columns == right.columns)
            assert(left.rows == right.rows)
        }
        override val columns = left.columns
        override val rows = left.rows
    }

    class AppendRow(val top : Expr, val bottom : Expr) : Expr() {
        init {
            assert(top.columns == bottom.columns)
        }
        override val columns = top.columns
        override val rows = top.rows + bottom.rows
    }

    class Summation(val expr : Expr) : Expr() {
        override val columns = 1
        override val rows = 1
    }

    class Constant(val value : Double, val format : Format? = null) : Expr() {
        init {
            "hello"
        }
        override val columns = 1
        override val rows = 1
    }

    class Differential(val expr : Expr) : Expr() {
        override val columns = expr.columns
        override val rows = expr.rows
    }

    class Transpose(val expr : Expr) : Expr() {
        override val columns = expr.rows
        override val rows = expr.columns
    }

    class Divide(val left : Expr, val right : Expr) : Expr() {
        override val columns = right.columns
        override val rows = right.rows
    }

    class Named(val name : String, val data: Expr) : Expr() {
        override val columns = data.columns
        override val rows = data.rows
        override fun get(column: Int, row: Int) = run {
            when(val value = data[column, row]) {
                is Hole -> Element(this, column, row)
                else -> value
            }
        }
    }

    class Hole : Expr() {
        override val columns = 1
        override val rows = 1
    }

    class NamedHole(val name : String, val column : Int, val row : Int) : Expr() {
        override val columns = 1
        override val rows = 1
    }
}


fun Expr.nameCells(name : String, column : Int = 0, row : Int = 0) : Expr {
    fun Expr.name() = nameCells(name, column, row)
    return when(this) {
        is Expr.DataFrame -> DataFrame(columns, rows) {
            this[it.column, it.row].nameCells(name, it.column, it.row)
        }
        is Named -> this
        is Times -> left.name() * right.name()
        is Minus -> left.name() - right.name()
        is Divide -> left.name() / right.name()
        is Plus -> Plus(elements.map { it.name() })
        is AppendRow -> AppendRow(top.name(), bottom.name())
        is Summation -> summation(expr.name())
        is Differential -> d(expr.name())
        is Constant -> this
        is Cell -> NamedCell(name, this.column, this.row)
        is Hole -> NamedHole(name, column, row)
        is Logistic -> Logistic(expr.name())
        is Power -> Power(expr.name(), power)
        is Transpose -> Transpose(expr.name())
        else ->
            error("$javaClass")
    }
}

fun Expr.reduce(outer : Expr? = null) : Expr {
    val outer = outer ?: this
    fun Expr.reduce() = reduce(outer)
    return when(this) {
        is Constant -> this
        is Hole -> this
        is NamedHole -> this
        is Element -> this
        is Cell -> this
        is NamedCell -> this
        is Named -> {
            if (this == outer) Named(name, data.reduce())
            else data.reduce()
        }
        is DataFrame -> {
            if (rows == 1 && columns == 1) get(0,0).reduce()
            else DataFrame(columns, rows) { get(it.column, it.row).reduce() }
        }
        is Logistic -> expr.reduce().map { logistic(it.reduce()) }
        is Divide -> {
            val left = left.reduce()
            val right = right.reduce()
            DataFrame(right.columns, right.rows) { left / right[it.column, it.row] }
        }
        is Power -> {
            val reduced = expr.reduce()
            DataFrame(columns, rows) { pow(reduced[it.column, it.row], power) }
        }
        is Differential -> expr.reduce().map { d(it.reduce()) }
        is Summation -> Plus(expr.flatten()).reduce()
        is Minus -> run {
            val leftReduced = left.reduce()
            val rightReduced = right.reduce()
            DataFrame(left.columns, left.rows) { leftReduced[it.column, it.row] - rightReduced[it.column, it.row] }
        }
        is AppendRow -> {
            val top = top.reduce()
            val bottom = bottom.reduce()
            DataFrame(columns, rows) {
                if (it.row < top.rows) top[it.column, it.row]
                else bottom[it.column, it.row-top.rows]
            }
        }
        is Plus -> {
            val reduced = elements.map { it.reduce() }
            val (constants, variants) = reduced.partition { it is Constant }
            return if (constants.isEmpty()) {
                Plus(variants)
            } else {
                var current = Constant(1.0)
                constants
                    .filterIsInstance<Constant>()
                    .forEach { constant ->
                        current = Constant(current.value * constant.value, current.format ?: constant.format )
                    }
                Plus(listOf(current) + variants)
            }
        }
        is Transpose -> {
            val expr = expr.reduce()
            DataFrame(columns = expr.rows, rows = expr.columns) { expr[it.row, it.column] }
        }

        is Times -> {
            val left = left.reduce()
            val right = right.reduce()
            return when {
                left is Constant && right is Constant -> Constant(left.value * right.value, left.format ?: right.format)
                left is Constant && left.value == 1.0 -> right.reduce()
                right is Constant && right.value == 1.0 -> left.reduce()
                left.scalar && right.scalar -> left * right
                left.scalar -> right.map { left[0,0] * it.reduce() }.reduce()
                right.scalar -> left.map { right[0,0] * it.reduce() }.reduce()
                else -> {
                    DataFrame(columns = right.columns, rows = left.rows) { cell ->
                        Plus((0 until left.columns).map {
                            val leftElement = left[it, cell.row]
                            val rightElement = right[cell.column, it]
                            (leftElement * rightElement).reduce()
                        })
                    }
                }
            }
        }
    }
}
data class Format(val precision : Int = 0, val prefix : String = "")

fun dataFrameOf(columns : Int, rows : Int) = DataFrame(columns, rows) { Hole() }
fun Expr.asDataFrame() : DataFrame =
    when {
        this is DataFrame -> this
        this is Named -> data.asDataFrame()
        columns == 1 && rows == 1 -> DataFrame(1,1) { this }
        else ->
            error("$javaClass $columns $rows")
    }
fun List<Int>.toColumn() = DataFrame(columns = 1, rows = size)  { Constant(this[it.row].toDouble(), Format(precision = 0)) }
fun logistic(expr : Expr) = Logistic(expr)
fun pow(expr : Expr, power : Double) = Power(expr, power)
fun summation(expr : Expr) = Summation(expr)

operator fun Double.plus(right : Expr) = Plus(listOf(Constant(this), right))
operator fun Expr.plus(right : Double) = Plus(listOf(this, Constant(right)))
operator fun Expr.plus(right : Expr) = Plus(listOf(this, right))
operator fun Expr.minus(right : Expr) = Minus(this, right)
operator fun Expr.times(right : Expr) = Times(this, right)
operator fun Double.times(right : Expr) = Times(Constant(this), right)
operator fun Expr.times(right : Double) = Times(this, Constant(right))
operator fun Expr.div(right : Expr) = Divide(this, right)
infix fun Expr.appendrow(value : Double) = AppendRow(this, Constant(value))
fun Expr.addColumn(value : (Coordinate) -> Any) = addColumns(1, value)
fun Expr.addColumns(count : Int, value : (Coordinate) -> Any) = DataFrame(columns + count, rows) {
    if (it.column >= columns) {
        when(val result = value(it)) {
            is Double -> Constant(result)
            else -> result as Expr
        }
    }
    else this[it.column, it.row]
}
fun transpose(expr: Expr) : Expr = Transpose(expr)
data class Coordinate(val column : Int, val row : Int) {
    operator fun get(column : Int, row : Int) = Cell(column, row)
    val left : Expr get() = get(column = column - 1, row)
    val right : Expr get() = get(column = column + 1, row)
    val above : Expr get() = get(column, row = row - 1)
    val below : Expr get() = get(column, row = row + 1)
}
fun Expr.map(mapping : (Expr) -> Expr) = DataFrame(columns, rows) { mapping(this[it.column, it.row]) }
fun d(expr : Expr) = Differential(expr)
val Expr.scalar get() = columns == 1 && rows == 1

fun Expr.flatten() : List<Expr> {
    val result = mutableListOf<Expr>()
    for(row in 0 until rows) {
        for(column in 0 until columns) {
            result.add(get(column, row))
        }
    }
    return result
}

fun dollars(value : Double) = Constant(value, Format(precision = 2, prefix = "$"))

fun Expr.render(outer : Expr? = null) : String {
    return if(outer == null) reduce().render(this) else {
        fun Expr.render() = render(outer)
        when (this) {
            is Summation -> "∑(${expr.render()})"
            is Power -> "(${expr.render()})^$power"
            is Divide -> "${left.render()}/${right.render()}"
            is AppendRow -> "${top.render()} appendrow ${bottom.render()}"
            is Element ->
                if (outer is Named && outer.data == expr) "[$column,$row]"
                else expr[column, row].render()
            is NamedHole ->
                if (outer is Named && outer.name == name) "0.0"
                else "$name[$column,$row]"
            is Named -> {
                when (columns) {
                    1 -> "$name = ${data.render()}"
                    else -> "$name\n-----\n${data.render()}"
                }
            }
            is DataFrame -> {
                fun render(column: Int, row: Int) = this[column, row].render()
                if (rows == 1 && columns == 1) {
                    return elements[0].render()
                } else if (columns == 1) {
                    val sb = StringBuilder()
                    sb.append("col ")
                    for (row in 0 until rows) {
                        sb.append(render(0, row))
                        if (row != rows - 1) sb.append("|")
                    }
                    return sb.toString()
                } else {
                    val sb = StringBuilder()
                    for (row in 0 until rows) {
                        sb.append("")
                        for (column in 0 until columns) {
                            sb.append(render(column, row))
                            if (column != columns - 1) sb.append("|")
                        }
                        if (row != rows - 1) sb.append("\n")
                    }
                    return sb.toString()
                }
            }
            is Times -> "${left.render()}*${right.render()}"
            is Minus -> "(${left.render()}-${right.render()})"
            is Plus -> "(" + elements.joinToString(" + ") { it.render() } + ")"
            is Logistic -> "logit(${expr.render()})"
            is Differential -> "d(${expr.render()})"
            is Constant ->
                if (format == null) "$value"
                else format.prefix + BigDecimal(value).setScale(format.precision, RoundingMode.HALF_EVEN).toString()
            is NamedCell ->
                if (outer is Named && outer.name == name) rowColName(column, row)
                else "$name:" + rowColName(column, row)
            else ->
                error("$javaClass")
        }
    }
}

fun rowColName(column:Int, row:Int) = "[$column,$row]"

fun Expr.renderStructure() : String {
    return when(this) {
        is Constant -> "Constant($value)"
        is Minus -> "Minus(${left.renderStructure()}, ${right.renderStructure()})"
        is Cell -> "Cell($column, $row)"
        is Times -> "Times(${left.renderStructure()}, ${right.renderStructure()})"
        is Named -> "Named($name,${data.renderStructure()}"
        is NamedHole -> "NamedHole($name,$column,$row)}"
        is DataFrame -> "DataFrame($columns,$rows,${elements.joinToString(",") { it.renderStructure() }})"
        else -> error("$javaClass")
    }
}
