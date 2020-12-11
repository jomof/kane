package com.github.jomof.kane

import kotlin.reflect.KProperty
import com.github.jomof.kane.Expr.*
import java.awt.image.DataBufferDouble
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

    data class Plus(val left : Expr, val right : Expr) : Expr() {
        override val columns = 1
        override val rows = 1
        override fun toString() = render()
    }

    class Times(val left : Expr, val right : Expr) : Expr() {
        override val columns = right.columns
        override val rows = left.rows
    }

    data class Logistic(val expr : Expr) : Expr() {
        override val columns = expr.columns
        override val rows = expr.rows
        override fun toString() = render()
    }

    data class LogisticPrime(val expr : Expr) : Expr() {
        override val columns = expr.columns
        override val rows = expr.rows
        override fun toString() = render()
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

    data class NamedHole(val name : String, val column : Int, val row : Int) : Expr() {
        override val columns = 1
        override val rows = 1
        override fun toString() = render()
    }
}


fun Expr.nameCells(name : String, column : Int = 0, row : Int = 0) : Expr {
    fun Expr.name() = nameCells(name, column, row)
    return when(this) {
        is DataFrame -> DataFrame(columns, rows) {
            this[it.column, it.row].nameCells(name, it.column, it.row)
        }
        is Named -> this
        is NamedCell -> this
        is NamedHole -> this
        is Times -> left.name() * right.name()
        is Minus -> left.name() - right.name()
        is Divide -> left.name() / right.name()
        is Plus -> left.name() + right.name()
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

fun Expr.differentiate(wrt : Expr) : Expr {
    return when(this) {
        is Plus -> left.differentiate(wrt) + right.differentiate(wrt)
        is DataFrame -> map { it.differentiate(wrt) }
        is Times -> left * right.differentiate(wrt) + left.differentiate(wrt) * right
        is Minus -> left.differentiate(wrt) - right.differentiate(wrt)
        is Power -> power * pow(expr, power - 1.0) * expr.differentiate(wrt)
        is Logistic -> LogisticPrime(expr) * expr.differentiate(wrt)
        is NamedHole -> {
            if (this == wrt) Constant(1.0)
            else Constant(0.0)
        }
        is Constant -> Constant(0.0)
        else ->
            error("$javaClass")
    }
}

fun Expr.unbox() : Expr = run {
    if (this is DataFrame && scalar) this[0,0]
    else this
}

fun Expr.getAllTimesElements() : List<Expr> {
    return when(this) {
        is Times -> left.getAllTimesElements() + right.getAllTimesElements()
        else -> listOf(this)
    }
}

fun Expr.getAllPlusElements() : List<Expr> {
    return when(this) {
        is Plus -> left.getAllPlusElements() + right.getAllPlusElements()
        else -> listOf(this)
    }
}

fun List<Expr>.sum() = run {
    val result = fold(Constant(0.0) as Expr) { sum, element -> sum + element}
    result
}

fun List<Expr>.product() = run {
    val result = fold(Constant(1.0) as Expr) { product, element -> product * element}
    result
}

fun Expr.reduce(outer : Expr? = null) : Expr {
    val outer = outer ?: this
    fun Expr.reduce() = reduce(outer).unbox()
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
        is LogisticPrime -> expr.reduce().map { LogisticPrime(it.reduce()) }
        is Logistic -> expr.reduce().map { logistic(it.reduce()) }
        is Divide -> {
            val left = left.reduce()
            val right = right.reduce()
            if (left is Differential && right is Differential) {
                val differentiated = left.expr.differentiate(right.expr)
                differentiated.reduce()
            } else {
                DataFrame(right.columns, right.rows) { (left / right[it.column, it.row]).reduce() }
            }
        }
        is Power -> {
            val reduced = expr.reduce()
            if (power == 1.0) expr.reduce()
            else DataFrame(columns, rows) { pow(reduced[it.column, it.row], power) }.unbox()
        }
        is Differential -> {
            val reduced = expr.reduce()
            reduced.map { d(it.reduce()) }
        }
        is Summation -> {
            val result = expr.reduce().flatten().sum().reduce()
            result

        }
        is Minus -> run {
            val leftReduced = left.reduce()
            val rightReduced = right.reduce()
            if (leftReduced is Constant && rightReduced is Constant) {
                Constant(leftReduced.value - rightReduced.value)
            } else {
                DataFrame(left.columns, left.rows) { leftReduced[it.column, it.row] - rightReduced[it.column, it.row] }.unbox()
            }
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
            val allPlus = (left.reduce() + right.reduce()).getAllPlusElements().map { it.unbox() }.sortedBy { it.order() }
            if (allPlus.size == 1) allPlus[0].reduce()
            else {
                val (left, right) = allPlus
                val remainder = allPlus.drop(2).fold(Constant(0.0) as Expr) { sum, element -> sum + element }
                val result = when {
                    left.scalar && right.scalar -> left + right
                    left.scalar && right is DataFrame -> right.map { left + it }
                    else ->
                        error("")
                } + remainder
                result
            }
        }
        is Transpose -> {
            val expr = expr.reduce()
            DataFrame(columns = expr.rows, rows = expr.columns) { expr[it.row, it.column] }
        }
        is Times -> {
            val allTimes = (left.reduce() * right.reduce()).getAllTimesElements().map { it.unbox() }.sortedBy { it.order() }
            if (allTimes.size == 1) allTimes[0]
            else {
                val (left, right) = allTimes
                val remainder = allTimes.drop(2).product()
                val result = when {
                    left.scalar && right.scalar -> left * right
                    left.scalar && right is DataFrame -> right.map { left * it }
                    left is DataFrame && right is DataFrame -> {
                        val result = DataFrame(columns = right.columns, rows = left.rows) { cell ->
                            (0 until left.columns).map {
                                val leftElement = left[it, cell.row]
                                val rightElement = right[cell.column, it]
                                (leftElement * rightElement).reduce()
                            }.sum()
                        }
                        result
                    }
                    else ->
                        error("")
                } * remainder
                result
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

fun Expr.order() : Int {
    return when(this) {
        is Constant -> 0
        else -> 1
    }
}

operator fun Double.plus(right : Expr) = Constant(this) + right
operator fun Expr.plus(right : Double) = this + Constant(right)
operator fun Expr.plus(expr : Expr) = run {
    val (left, right) = listOf(this, expr).sortedBy { it.order() }
    when {
        left is Constant && right is Constant -> Constant(left.value + right.value)
        left is Constant && left.value == 0.0 -> right
        else -> Plus(left, right)
    }
}
operator fun Expr.minus(right : Expr) = Minus(this, right)
operator fun Expr.times(expr : Expr) = run {
    val (left, right) = listOf(this, expr).sortedBy { it.order() }
    when {
        left is Constant && right is Constant -> Constant(left.value * right.value)
        left is Constant && left.value == 1.0 -> right
        left is Constant && left.value == 0.0 -> left
        else -> Times(left, right)
    }
}
operator fun Double.times(right : Expr) = Constant(this) * right
operator fun Expr.times(right : Double) = this * Constant(right)
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
fun Expr.map(mapping : (Expr) -> Expr) = run {
    if (columns == 1 && rows == 1) mapping(this)
    else DataFrame(columns, rows) { mapping(this[it.column, it.row]) }
}
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

fun Expr.precedence() = when(this) {
        is Constant,
        is NamedCell,
        is NamedHole -> 0
        is Times -> 1
        is Divide -> 2
        is Plus -> 3
        is Minus -> 4
        else -> 99
    }

fun Expr.render(outer : Expr? = null) : String {
    if (outer == null) return render(this)
    else if (this is Named && this != outer) return data.render()
    else {
        val thiz = this
        fun Expr.render() = run {
            val parentPrecedence = thiz.precedence()
            val precedence = precedence()
            val result = render(outer)
            if (parentPrecedence < precedence && this !is Named)
                "($result)"
            else
                result

        }
        return when (this) {
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
                when {
                    columns == 1 -> "$name = ${data.render()}"
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
            is Minus -> "${left.render()}-${right.render()}"
            is Plus -> "${left.render()}+${right.render()}"
            is Logistic -> "logit(${expr.render()})"
            is LogisticPrime -> "logit'(${expr.render()})"
            is Differential -> "d(${expr.render()})"
            is Constant ->
                if (format == null) "$value"
                else format.prefix + BigDecimal(value).setScale(format.precision, RoundingMode.HALF_EVEN).toString()
            is NamedCell ->
                if (outer is Named && outer.name == name) rowColName(column, row)
                else "$name:" + rowColName(column, row)
            is Hole -> "0.0"
            else ->
                error("$javaClass")
        }
    }
}

fun rowColName(column:Int, row:Int) = "[$column,$row]"

fun Expr.renderStructure() : String {
    return when(this) {
        is Differential -> "Differential(${expr.renderStructure()})"
        is Constant -> "Constant($value)"
        is Plus -> "Plus(${left.renderStructure()}, ${right.renderStructure()})"
        is Minus -> "Minus(${left.renderStructure()}, ${right.renderStructure()})"
        is Cell -> "Cell($column, $row)"
        is Power -> "Power(${expr.renderStructure()}, $power)"
        is Logistic -> "Logistic(${expr.renderStructure()})"
        is Times -> "Times(${left.renderStructure()}, ${right.renderStructure()})"
        is Divide -> "Divide(${left.renderStructure()}, ${right.renderStructure()})"
        is Named -> "Named($name,${data.renderStructure()}"
        is NamedHole -> "NamedHole($name,$column,$row)}"
        is DataFrame -> "DataFrame($columns,$rows,${elements.joinToString(",") { it.renderStructure() }})"
        else -> error("$javaClass")
    }
}
