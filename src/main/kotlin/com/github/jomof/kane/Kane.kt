package com.github.jomof.kane

import kotlin.reflect.KProperty
import com.github.jomof.kane.Expr.*
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.pow

interface Binary {
    val left : Expr
    val right : Expr
}

interface Unary {
    val expr : Expr
}

interface Terminal

sealed class Expr {
    abstract val columns : Int
    abstract val rows : Int

    open operator fun getValue(thisRef: Any?, property: KProperty<*>) = run {
        when(this) {
            is CommonSubexpressions ->
                CommonSubexpressions(exprs, Named(property.name, common))
                    .nameCells(property.name)
            is Variable -> Named(property.name, NamedVariable(property.name))
            else -> Named(property.name, nameCells(property.name)).reduce()
        }
    }
    open operator fun get(column : Int, row : Int) : Expr = asDataFrame()[column, row]
    override fun toString() = render()

    data class DataFrame(override val columns : Int, override val rows : Int, val elements : List<Expr>) : Expr() {
        //val elements : List<Expr> = (0 until rows * columns).map { index -> init(indexToColRow(index)) }
        override operator fun get(column : Int, row : Int) : Expr {
            assert(column < columns) { "column $column is >= $columns" }
            assert(row < rows)
            return elements[row * columns + column]
        }
        private fun indexToColRow(index : Int) = Coordinate(index % columns, index / columns, index)
    }

    data class Element(val expr : Expr, val column : Int, val row : Int) : Expr() {
        override val columns = 1
        override val rows = 1
        val value : Expr get() = expr[column, row]
        override fun toString() = render()
    }

    data class Cell(val column : Int, val row : Int) : Expr(), Terminal {
        override val columns = 1
        override val rows = 1
        override fun toString() = render()
    }

    data class NamedCell(val name : String, val column : Int, val row : Int) : Expr(), Terminal {
        override val columns = 1
        override val rows = 1
        override fun toString() = render()
    }

    data class Plus(override val left : Expr, override val right : Expr) : Expr(), Binary {
        override val columns = 1
        override val rows = 1
        override fun toString() = render()
    }

    data class Times(override val left : Expr, override val right : Expr) : Expr(), Binary {
        override val columns = right.columns
        override val rows = left.rows
        override fun toString() = render()
    }

    data class Logistic(override val expr : Expr) : Expr(), Unary {
        override val columns = expr.columns
        override val rows = expr.rows
        override fun toString() = render()
    }

    data class Power(override val left : Expr, override val right : Expr) : Expr(), Binary {
        override val columns = left.columns
        override val rows = left.rows
        override fun toString() = render()
    }

    data class Minus(override val left : Expr, override val right : Expr) : Expr(), Binary {
        init {
            assert(left.columns == right.columns)
            assert(left.rows == right.rows)
        }
        override val columns = left.columns
        override val rows = left.rows
        override fun toString() = render()
    }

    data class AppendRow(val top : Expr, val bottom : Expr) : Expr() {
        init {
            assert(top.columns == bottom.columns) {
                "top has ${top.columns} columns but bottom has ${bottom.columns} columns"
            }
        }
        override val columns = top.columns
        override val rows = top.rows + bottom.rows
        override fun toString() = render()
    }

    data class Summation(val expr : Expr) : Expr() {
        override val columns = 1
        override val rows = 1
        override fun toString() = render()
    }

    data class Constant(val value : Double, val format : Format? = Format(precision = 5)) : Expr(),Terminal {
        override val columns = 1
        override val rows = 1
        override fun toString() = render()
    }

    data class Differential(val expr : Expr) : Expr() {
        override val columns = expr.columns
        override val rows = expr.rows
        override fun toString() = render()
    }

    data class Transpose(val expr : Expr) : Expr() {
        override val columns = expr.rows
        override val rows = expr.columns
        override fun toString() = render()
    }

    data class Divide(val left : Expr, val right : Expr) : Expr() {
        override val columns = right.columns
        override val rows = right.rows
        override fun toString() = render()
    }

    data class Named(val name : String, val data: Expr) : Expr() {
        override val columns = data.columns
        override val rows = data.rows
        override fun get(column: Int, row: Int) = run {
            when(val value = data[column, row]) {
                is Variable -> Element(this, column, row)
                is Constant -> value
                else -> NameScope(name, data, value)
            }
        }
        override fun toString() = render()
    }

    class Variable : Expr(), Terminal {
        override val columns = 1
        override val rows = 1
        override fun toString() = render()
    }

    data class NamedHole(val name : String, val column : Int, val row : Int) : Expr(), Comparable<NamedHole>, Terminal {
        override val columns = 1
        override val rows = 1
        override fun toString() = render()
        override fun compareTo(other: NamedHole): Int {
            if (name > other.name) return 1
            if (name < other.name) return -1
            if (row > other.row) return 1
            if (row < other.row) return -1
            if (column > other.column) return 1
            if (column < other.column) return -1
            return 0
        }
    }

    data class NamedVariable(val name : String) : Expr(), Comparable<NamedVariable>, Terminal {
        override val columns = 1
        override val rows = 1
        override fun toString() = render()
        override fun compareTo(other: NamedVariable): Int {
            if (name > other.name) return 1
            if (name < other.name) return -1
            return 0
        }
    }

    data class NameScope(val name : String, val data : Expr, val expr : Expr) : Expr() {
        override val columns = expr.columns
        override val rows = expr.rows
        override fun toString() = render()
    }

    data class CommonSubexpressions(val exprs : List<Expr>, val common : Expr) : Expr() {
        override val columns = 1
        override val rows = 1
        override fun toString() = render()
    }
}

fun Expr.holes(top : Boolean = true) : List<NamedHole> {
    if (top) {
        return holes(false).distinct().sorted()
    }
    fun Expr.holes() = holes(false)
    return when(this) {
        is Named -> data.holes()
        is DataFrame -> elements.flatMap { it.holes() }
        is Times -> left.holes() + right.holes()
        is Plus -> left.holes() + right.holes()
        is Minus -> left.holes() + right.holes()
        is Divide -> left.holes() + right.holes()
        is Constant -> listOf()
        is Logistic -> expr.holes()
        is Power -> left.holes()
        is NamedHole -> listOf(this)
        is NamedCell -> listOf()
        is NameScope -> expr.holes()
        else -> error("$javaClass")
    }
}


fun Expr.nameCells(name : String, column : Int = 0, row : Int = 0) : Expr {
    fun Expr.name() = nameCells(name, column, row)
    return when(this) {
        is DataFrame -> dataFrameOf(columns, rows) {
            this[it.column, it.row].nameCells(name, it.column, it.row)
        }
        is Named -> Named(this.name, data.name())
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
        is Cell ->
            NamedCell(name, this.column, this.row)
        is Variable -> NamedHole(name, column, row)
        is Logistic -> Logistic(expr.name())
        is Power -> Power(left.name(), right)
        is Transpose -> Transpose(expr.name())
        is Element -> Element(expr.name(), column, row)
        is NameScope -> NameScope(this.name, data.name(), expr.name())
        is CommonSubexpressions -> {
            val namedExprs = exprs.map { it.name() }
            val namedCommon = common.name()
            CommonSubexpressions(namedExprs, namedCommon)
        }
        else -> error("$javaClass")
    }
}

fun Expr.differentiate(wrt : Expr) : Expr {
    return when(this) {
        is Plus -> left.differentiate(wrt) + right.differentiate(wrt)
        is DataFrame -> map { it.differentiate(wrt) }
        is Times -> left * right.differentiate(wrt) + left.differentiate(wrt) * right
        is Minus -> left.differentiate(wrt) - right.differentiate(wrt)
        is Power ->
            right * pow(left, right - 1.0) * left.differentiate(wrt)
        is Logistic -> this * (1.0 - this) * expr.differentiate(wrt)
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



fun List<Expr>.sum() =  fold(Constant(0.0) as Expr) { sum, element -> sum + element}
fun List<Expr>.product() = fold(Constant(1.0) as Expr) { product, element -> product * element }

fun Expr.referencesName(name : String) : Boolean = run {
    fun Expr.self() = referencesName(name)
    when(this) {
        is Constant -> false
        is Times -> left.self() || right.self()
        is Plus -> left.self() || right.self()
        is Minus -> left.self() || right.self()
        is Divide -> left.self() || right.self()
        is NamedHole -> this.name == name
        is NamedCell -> this.name == name
        else -> error("$javaClass")
    }
}

fun Expr.reduce(outer : String? = null, check : Boolean = true, memo : MutableMap<Expr,Expr> = mutableMapOf()) : Expr {
    if (memo.contains(this)) return memo.getValue(this)
    val outer = outer ?: if (this is Named) name else "anonymous"
    fun Expr.reduce() = run {
        val result = reduce(outer, check, memo).unbox()
        memo[this] = result
        result
    }
    val result = when(this) {

        is NameScope -> {
            val reduced = expr.reduce()
            if (reduced.referencesName(name))
                NameScope(name, data.reduce(), reduced)
            else
                reduced
        }
        is Constant -> this
        is Variable -> this
        is NamedHole -> this
        is Element -> this
        is Cell -> this
        is NamedCell -> this
        is NamedVariable -> this
        is Named -> {
            if (this.name == outer) Named(name, data.reduce())
            else data.reduce()
        }
        is DataFrame -> {
            if (rows == 1 && columns == 1) get(0,0).reduce()
            else dataFrameOf(columns, rows) { get(it.column, it.row).reduce() }
        }
        is Logistic -> {
            val expr = expr.reduce()
            when {
                expr is Constant -> Constant(1.0 / (1.0 + Math.E.pow(-expr.value)))
                expr.scalar -> logistic(expr)
                else -> expr.map { logistic(it).reduce() }
            }
        }
        is Divide -> {
            val left = left.reduce()
            val right = right.reduce()
            if (left is Differential && right is Differential) {
                val differentiated = left.expr.differentiate(right.expr)
                differentiated.reduce()
            } else {
                dataFrameOf(right.columns, right.rows) { (left / right[it.column, it.row]).reduce() }
            }
        }
        is Power -> {
            val reduced = left.reduce()
            dataFrameOf(columns, rows) { pow(reduced[it.column, it.row], right) }.unbox()
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
                dataFrameOf(left.columns, left.rows) { leftReduced[it.column, it.row] - rightReduced[it.column, it.row] }.unbox()
            }
        }
        is AppendRow -> {
            val top = top.reduce()
            val bottom = bottom.reduce()
            dataFrameOf(columns, rows) {
                if (it.row < top.rows) top[it.column, it.row]
                else bottom[it.column, it.row-top.rows]
            }
        }
        is Plus -> {
            val allPlus = (left.reduce() + right.reduce()).getAllPlusElements().map { it.unbox() }
            val mapped = allPlus
                .map { when {
                    it is Times && it.left is Constant -> {
                        val result = it.right to it.left.value
                        result
                    }
                    else -> it to 1.0
                } }
            val grouped = mapped.groupBy { it.first }
            val collapsed = grouped.map { it.value.sumByDouble { element -> element.second } * it.key }
            val result = collapsed.sum()
            result
        }
        is Transpose -> {
            val expr = expr.reduce()
            dataFrameOf(columns = expr.rows, rows = expr.columns) { expr[it.row, it.column] }
        }
        is Times -> {
            val allTimes = (left.reduce() * right.reduce()).getAllTimesElements().map { it.unbox() }.sortedBy { it.order() }
            val mapped = allTimes
                .map { when {
                    it is Power && it.right is Constant -> {
                        val result = it.left to it.right.value
                        result
                    }
                    else -> it to 1.0
                } }
            val grouped = mapped.groupBy { it.first }
            val collapsed = grouped.map { pow(it.key, it.value.sumByDouble { element -> element.second }) }
                .sortedBy { it.order() }
            val result = collapsed.product()
            result
        }
        is CommonSubexpressions -> CommonSubexpressions(exprs.map { it.reduce() }, common.reduce())
    }
    if (check) {
        checkReductionIdempotent(this, outer)
    }
    memo[this] = result
    return result
}

fun checkReductionIdempotent(original : Expr, outer : String) {
    val first = original.reduce(outer = outer, check = false)
    val second = first.reduce(outer = outer, check = false)
    if("$first" != "$second") {
        first.reduce(outer = outer, check = false)
        val sb = StringBuilder()
        sb.append("@Test\n")
        sb.append("fun test() {\n")
        sb.append("    // original ${original.renderStructure()}\n")
        sb.append("    //          $original\n")
        sb.append("    // first    ${first.renderStructure()}\n")
        sb.append("    //          $first\n")
        sb.append("    // second   ${second.renderStructure()}\n")
        sb.append("    //          $second\n")
        sb.append("    val original = ${original.renderStructure()}\n")
        sb.append("    val first = original.reduce(\"$outer\")\n")
        sb.append("    val second = first.reduce(\"$outer\")\n")
        sb.append("    assert(\"\$first\" == \"\$second\")\n")
        sb.append("}\n")
        println(sb)

        assert(false) { "second reduction of '$original'->'$first'->'$second' had effect" }
    }

}

data class Format(val precision : Int = 0, val prefix : String = "")

fun dataFrameOf(columns : Int, rows : Int, init : (Coordinate) -> Expr) = run {
    fun indexToColRow(index : Int) = Coordinate(index % columns, index / columns, index)
    DataFrame(columns, rows, (0 until columns * rows).map { init(indexToColRow(it)) })
}
fun dataFrameOf(columns : Int, rows : Int) = dataFrameOf(columns, rows) { Variable() }
fun dataFrameOf(columns : Int, rows : Int, vararg expr: Expr) = DataFrame(columns, rows, expr.toList())

fun Expr.asDataFrame() : DataFrame =
    when {
        this is DataFrame -> this
        this is Named -> data.asDataFrame()
        columns == 1 && rows == 1 -> dataFrameOf(1,1) { this }
        else ->
            error("$javaClass $columns $rows")
    }
fun List<Int>.toColumn() = dataFrameOf(columns = 1, rows = size)  { Constant(this[it.row].toDouble(), Format(precision = 0)) }
fun List<Expr>.toRow() = dataFrameOf(size, 1) { this[it.column] }
fun logistic(expr : Expr) = Logistic(expr)
fun pow(expr : Expr, power : Double) = pow(expr, Constant(power))
fun pow(expr : Expr, power : Expr) = when {
    expr is Constant && power is Constant -> Constant(Math.pow(expr.value, power.value))
    power == Constant(1.0) -> expr
    power == Constant(0.0) -> Constant(1.0)
    expr == Constant(1.0) -> Constant(1.0)
    expr == Constant(0.0) -> Constant(0.0)
    else -> Power(expr, power)
}
fun summation(expr : Expr) = Summation(expr)

fun Expr.order() = precedence()

operator fun Double.plus(right : Expr) = Constant(this) + right
operator fun Expr.plus(right : Double) = this + Constant(right)
operator fun Expr.plus(expr : Expr) = run {
    val (left, right) = this to expr
    when {
        left is Constant && right is Constant -> Constant(left.value + right.value, format = left.format ?: right.format)
        left is Constant && left.value == 0.0 -> right
        right is Constant && right.value == 0.0 -> left
        else -> Plus(left, right)
    }
}
operator fun Double.minus(right : Expr) = Constant(this) - right
operator fun Expr.minus(right : Double) = this - Constant(right)
operator fun Expr.minus(right : Expr) = when {
    this is Constant && right is Constant -> Constant(value - right.value)
    this == Constant(0.0) -> Constant(-1.0) * right
    right == Constant(0.0) -> this
    else -> Minus(this, right)
}
operator fun Expr.times(value : Expr) : Expr = run {
    val (left, right) = this to value
    when {
        left is Constant && right is Constant -> Constant(left.value * right.value, format = left.format ?: right.format)
        left == Constant(1.0) -> right
        right == Constant(1.0) -> left
        left == Constant(0.0) -> left
        right == Constant(0.0) -> right
        left.scalar && right is DataFrame -> right.map { left * it }
        right.scalar && left is DataFrame -> left.map { it * right }
        left is DataFrame && right is DataFrame -> {
            assert(left.columns == right.rows)
            val rows = left.rows
            val columns = right.columns
            dataFrameOf(columns, rows) { coord ->
                val sum = (0 until left.columns).map {
                        val lv = left[it, coord.row]
                        val rv = right[coord.column, it]
                        lv * rv
                    }.sum()
                sum
            }

        }
        else -> Times(left, right)
    }
}
operator fun Double.times(right : Expr) = Constant(this) * right
operator fun Expr.times(right : Double) = this * Constant(right)
operator fun Expr.times(right : Int) = this * Constant(right.toDouble())
operator fun Expr.div(right : Expr) = Divide(this, right)
infix fun Expr.appendrow(value : Double) = this appendrow Constant(value)
infix fun Expr.appendrow(expr : Expr) = AppendRow(this, expr)
fun Expr.addColumn(value : (Coordinate) -> Any) = addColumns(1, value)
fun Expr.addColumns(count : Int, value : (Coordinate) -> Any) = dataFrameOf(columns + count, rows) {
    if (it.column >= columns) {
        when(val result = value(it)) {
            is Double -> Constant(result)
            else -> result as Expr
        }
    }
    else this[it.column, it.row]
}
fun transpose(expr: Expr) : Expr = Transpose(expr)
data class Coordinate(val column : Int, val row : Int, val index : Int) {
    operator fun get(column : Int, row : Int) = Cell(column, row)
    val left : Expr get() = get(column = column - 1, row)
    val right : Expr get() = get(column = column + 1, row)
    val above : Expr get() = get(column, row = row - 1)
    val below : Expr get() = get(column, row = row + 1)
}
fun Expr.map(mapping : (Expr) -> Expr) = run {
    if (columns == 1 && rows == 1) mapping(this)
    else dataFrameOf(columns, rows) { mapping(this[it.column, it.row]) }
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
fun const(value : Int) = Constant(value.toDouble(), Format(precision = 0))

fun Expr.precedence() = when(this) {
        is Constant -> 0
        is NamedCell -> 1
        is Cell -> 2
        is NameScope -> 3
        is Logistic -> 4
        is NamedHole -> 10
        is Power -> 11
        is Times -> 12
        is Divide -> 13
        is Plus -> 14
        is Minus -> 15
        else -> 99
    }



fun Expr.render(outer : Expr? = null) : String {
    if (outer == null) return render(this)
    else if (this is Named && this != outer) return data.render()
    else {
        val parent = this
        fun Expr.render() = run {
            val parentPrecedence = parent.precedence()
            val childPrecedence = precedence()
            val result = render(outer)
            if (parentPrecedence < childPrecedence && this !is Named && parent !is Unary)
                "($result)"
            else
                result

        }
        return when (this) {
            is Cell -> rowColName(column, row)
            is Summation -> "∑(${expr.render()})"
            is Power -> "${left.render()}^$right"
            is Divide -> "${left.render()}/${right.render()}"
            is AppendRow -> "${top.render()} appendrow ${bottom.render()}"
            is Element ->
                if (outer is Named && outer.data == expr) "[$column,$row]"
                else expr[column, row].render()
            is NamedHole ->
                if (outer is Named && outer.name == name) "0"
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
            is Differential -> "d(${expr.render()})"
            is Constant ->
                if (format == null) "$value"
                else {
                    val result = format.prefix + BigDecimal(value).setScale(format.precision, RoundingMode.HALF_EVEN).toString()
                    if (result.contains(".")) result.trimEnd('0').trimEnd('.')
                    else result
                }
            is NamedCell ->
                if (outer is Named && outer.name == name) rowColName(column, row)
                else "$name" + rowColName(column, row)
            is Variable -> "0"
            is NameScope -> expr.render(outer)
            is CommonSubexpressions -> {
                (exprs + common).joinToString("\n\n") { it.render(it) }
            }
            else ->
                error("$javaClass")
        }
    }
}

fun rowColName(column:Int, row:Int) = "[$column,$row]"

fun Expr.renderStructure() : String {
    return when(this) {
        is AppendRow -> "AppendRow(${top.renderStructure()}, ${bottom.renderStructure()})"
        is Differential -> "Differential(${expr.renderStructure()})"
        is Constant -> "Constant($value)"
        is Plus -> "Plus(${left.renderStructure()}, ${right.renderStructure()})"
        is Minus -> "Minus(${left.renderStructure()}, ${right.renderStructure()})"
        is Cell -> "Cell($column, $row)"
        is Power -> "Power(${left.renderStructure()}, $right)"
        is Logistic -> "Logistic(${expr.renderStructure()})"
        is Times -> "Times(${left.renderStructure()}, ${right.renderStructure()})"
        is Divide -> "Divide(${left.renderStructure()}, ${right.renderStructure()})"
        is Named -> "Named(\"$name\",${data.renderStructure()})"
        is NamedHole -> "NamedHole(\"$name\",$column,$row)"
        is NamedCell -> "NamedCell($name,$column,$row)}"
        is DataFrame -> "dataFrameOf($columns,$rows,${elements.joinToString(",") { it.renderStructure() }})"
        is Element -> "Element(${javaClass},$column,$row)"
        is CommonSubexpressions -> "CommonSubexpressions(${exprs.map { it.renderStructure()}.joinToString(",") }},${common.renderStructure()})"
        else -> error("$javaClass")
    }
}

fun Expr.expandCellReferences(names : List<Pair<String, Expr>> = listOf()) : Expr {
    fun Expr.self() = expandCellReferences(names)
    return when(this) {
        is Named -> Named(name, data.expandCellReferences(listOf(name to data) + names))
        is DataFrame -> map { it.self() }
        is Times -> left.self() * right.self()
        is Plus -> left.self() + right.self()
        is Minus -> left.self() - right.self()
        is Divide -> left.self() / right.self()
        is NamedCell -> names.find { it.first == name }!!.second[column, row].self()
        is NamedHole -> this
        is Constant -> this
        is Element -> Element(expr.self(), column, row)
        is Logistic -> Logistic(expr.self())
        is NameScope -> expr.expandCellReferences(listOf(name to data) + names)
        else -> error("$javaClass")
    }
}

fun Expr.eval(top : Boolean = true, prior : MutableMap<NamedCell, Expr> = mutableMapOf(), resolve : (NamedHole) -> Any) : Expr {
    if (top) return expandCellReferences().eval(false, prior, resolve).reduce()
    fun Expr.self() = eval(false, prior, resolve)
    return when(this) {
        is NamedHole -> prior.computeIfAbsent(NamedCell(name, column,row)) {
            when(val result = resolve(this)) {
                is Double -> Constant(result)
                is Expr -> result
                else -> error("$result")
            }
        }
        is DataFrame -> map { it.self() }
        is NamedCell -> prior[this] ?: this
        is Named -> Named(name, data.self())
        is Times -> left.self() * right.self()
        is Plus -> left.self() + right.self()
        is Minus -> left.self() - right.self()
        is Divide -> left.self() / right.self()
        is Logistic -> Logistic(expr.self())
        is Constant -> this

        else -> error("$javaClass")
    }
}

class ExprTable(val exprs : Map<Expr, Int>) {
    override fun toString() = run {
        val sb = StringBuilder()
        exprs.entries.sortedByDescending { it.value }.forEach { (expr, count) ->
            sb.append("$count : $expr\n")
        }
        sb.toString()
    }
    val first : Expr? get() = exprs.filter { it.value > 1}.maxByOrNull { it.value }?.key
    fun isEmpty() = exprs.isEmpty()
    companion object {
        val empty = ExprTable(mapOf())
    }
}
operator fun ExprTable.plus(value : ExprTable) = run {
    val map = exprs.toMutableMap()
    for((key, count) in value.exprs) {
        map[key] = map.computeIfAbsent(key) { 0 } + count
    }
    ExprTable(map)
}

operator fun ExprTable.plus(right : Expr) = run {
    fun append() = run {
        val map = exprs.toMutableMap()
        map[right] = map.computeIfAbsent(right) { 0 } + 1
        ExprTable(map)
    }
    when {
        right is Binary && right.left is Terminal && right.right is Terminal -> append()
        right is Constant -> this
        else -> append()
    }
}

fun Expr.commonSubExpressions() : ExprTable {
    val empty = ExprTable.empty
    fun Expr.self() = commonSubExpressions()
    return when(this) {
        is Cell -> empty
        is NamedHole -> empty
        is Constant -> empty
        is Logistic -> expr.self() + this
        is Named -> data.commonSubExpressions()
        is Minus -> left.self() + right.self() + this
        is DataFrame -> elements.fold(empty) { initial, element -> initial + element.self() }
        is Power -> left.self() + this
        is Plus -> {
            val elements = getAllPlusElements().sortedBy { it.order() }
            var result = elements.fold(empty) { initial, element -> initial + element.self() }
            elements.indices.forEach { outer ->
                elements.indices.forEach { inner ->
                    if (inner != outer) {
                        result += elements[outer] + elements[inner]
                    }
                }
            }
            result
        }
        is Times -> {
            val elements = getAllTimesElements().sortedBy { it.order() }
            var result = elements.fold(empty) { initial, element -> initial + element.self() }
            elements.indices.forEach { outer ->
                elements.indices.forEach { inner ->
                    if (inner != outer) {
                        result += elements[outer] * elements[inner]
                    }
                }
            }
            result
        }
        else -> error("$javaClass")
    }
}

fun Expr.replaceSubExpression(find : Expr, replace : Expr) : Expr = run {
    fun Expr.self() = replaceSubExpression(find, replace)
    when {
        this == find -> replace
        this is Named -> Named(name, data.self())
        this is Cell -> this
        this is Constant -> this
        this is NamedHole -> this
        this is Power -> pow(left.self(), right.self())
        this is Minus -> left.self() - right.self()
        this is Logistic -> logistic(expr.self())
        this is DataFrame -> map { it.self() }
        this is Plus -> {
            val left = left.self()
            val right = right.self()
            if (find !is Plus) left + right
            else if (find == left + right || find == right + left) {
                replace
            }
            else {
                val elements = getAllPlusElements()
                val foundLeft = elements.indices.firstOrNull { elements[it] == find.left }
                val foundRight = elements.indices.firstOrNull { elements[it] == find.right }
                if (foundLeft == null || foundRight == null) left + right
                else {
                    val result = (elements
                        .indices
                        .filter { it!=foundLeft && it != foundRight }
                        .map { elements[it] } ).sum().self()
                    result + replace
                }
            }
        }
        this is Times -> {
            val left = left.self()
            val right = right.self()
            if (find !is Times) left * right
            else if (find == left * right || find == right * left)
                replace
            else {
                val elements = getAllTimesElements()
                val foundLeft = elements.indices.firstOrNull { elements[it] == find.left }
                val foundRight = elements.indices.firstOrNull { elements[it] == find.right }
                if (foundLeft == null || foundRight == null) left * right
                else {
                    val result = (elements
                        .indices
                        .filter { it!=foundLeft && it != foundRight }
                        .map { elements[it] } ).product().self()
                    result * replace
                }
            }
        }
        else -> error("$javaClass")
    }
}


fun CommonSubexpressions.decompose() : CommonSubexpressions {
    val table = exprs.map { it.commonSubExpressions() }.fold(ExprTable.empty) { initial, element -> initial + element }
    if (table.isEmpty()) return this
    val find = table.first
    if (find == this || find == null) return this

    val replace = Cell(0, common.rows)
    val replaced = exprs.map { it.replaceSubExpression(find, replace).reduce() }
    val append = listOf(const(common.rows), find).toRow()
    val lookup = (common appendrow append).reduce()
    return CommonSubexpressions(replaced, lookup).decompose()
}


fun Expr.decompose() = CommonSubexpressions(listOf(this), dataFrameOf(2,0)).decompose()
fun List<Expr>.decompose() = CommonSubexpressions(this, dataFrameOf(2,0)).decompose()
