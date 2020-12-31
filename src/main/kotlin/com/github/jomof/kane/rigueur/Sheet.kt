package com.github.jomof.kane.rigueur

import com.github.jomof.kane.rigueur.Sheet.Companion.coordinateToCellTag
import com.github.jomof.kane.rigueur.types.AlgebraicType
import com.github.jomof.kane.rigueur.types.StringType
import com.github.jomof.kane.rigueur.types.algebraicType
import java.lang.Integer.max

interface CellReferenceExpr<E:Number> : ScalarExpr<E>

data class AbsoluteCellReferenceExpr<E:Number>(
    private val coordinate : Coordinate,
    override val type: AlgebraicType<E>
) : CellReferenceExpr<E> {
    override fun toString() = coordinateToCellTag(coordinate)
}

data class RelativeCellReferenceExpr(val coordinate : Coordinate) : Expr {
    fun above(offset : Int = 1) = copy(coordinate = coordinate.copy(row = coordinate.row - offset))
    fun below(offset : Int = 1) = copy(coordinate = coordinate.copy(row = coordinate.row + offset))
    fun left(offset : Int = 1) = copy(coordinate = coordinate.copy(column = coordinate.column - offset))
    fun right(offset : Int = 1) = copy(coordinate = coordinate.copy(column = coordinate.column + offset))
    override fun toString() = "ref$coordinate"
}

data class CoerceScalar<T:Number>(
    val value : Expr,
    override val type : AlgebraicType<T>
) : ScalarExpr<T> {
    override fun toString() = "$value"
    fun mapChildren(f : ExprFunction) = copy(value = f(value))
}

data class Sheet(
    val name : String = "",
    val cells : Map<String, TypedExpr<*>> = mapOf()) {

    operator fun get(cell : String) = cells[cell]

    fun with(vararg assign : Pair<String, Any>) : Sheet {
        val result = cells.toMutableMap()
        for((cell,value) in assign) {
            result[cell] = coerceToTypedExpr(cell, value)
        }
        return copy(cells = result)
    }
    private val columns get() = cells.map { cellTagToCoordinate(it.key).column }.max()!! + 1
    private val rows get() = cells.map { cellTagToCoordinate(it.key).row }.max()!! + 1

    fun eval() : Sheet {
        val cells = cells.toMutableMap()
        for((cell, expr) in cells) {
            val replaced = expr.replaceTypedExprTopDown {
                when {
                    it is CellReferenceExpr<*> -> cells["$it"] ?: it
                    else -> it
                }
            }
            cells[cell] = replaced
        }
        cells.forEach { (cell, expr) ->
            cells[cell] = (expr as AlgebraicExpr<*>).memoizeAndReduceArithmetic()
        }
        return copy(cells = cells)
    }

    fun minimize(target : String, vararg variables : String) : Sheet {
        val expressions = mutableMapOf<String, NamedScalarExpr<Double>>()

        // Turn each 'variable' into a NamedScalarVariable
        variables.forEach { cell ->
            when(val expr = (cells.getValue(cell) as AlgebraicExpr<Double>).memoizeAndReduceArithmetic()) {
                is ConstantScalar -> expressions[cell] = NamedScalarVariable(cell, expr.value)
                else -> error("$cell ($expr) is not a constant")
            }
        }

        // Follow all expressions from 'target'
        val remaining = mutableSetOf(target)
        while(remaining.isNotEmpty()) {
            val next = remaining.first()
            remaining -= next
            if (expressions.containsKey(next)) continue
            val expr = cells.getValue(next) as ScalarExpr<Double>

            val replaced = expr.replace {
                when(it) {
                    is CoerceScalar -> {
                        val result = it.value as ScalarExpr<Double>
                        result
                    }
                    is AbsoluteCellReferenceExpr -> {
                        val ref = "$it"
                        remaining += ref
                        val result = expressions.getValue(ref)
                        result
                    }
                    else -> it
                }
            }
            expressions[next] = NamedScalar(next, replaced)
        }

        val tab = Tableau(expressions.values.toList()).linearize()
        println(tab)

        return this
    }

    private fun render() : String {
        val sb = StringBuilder()
        val widths = Array(columns + 1) { 0 }

        // Calculate widths
        for(row in 0 until rows) {
            widths[0] = max(widths[0], "$row".length)
            for(column in 0 until columns) {
                val cell = coordinateToCellTag(column, row)
                val value = cells[cell] ?: ""
                widths[column + 1] = max(widths[column + 1], "$value".length)
            }
        }

        // Column headers
        widths.indices.forEach { index ->
            val width = widths[index]
            if (index == 0) sb.append(" ".repeat(width) + " ")
            else sb.append(padCenter(indexToColumnTag(index - 1), width) + " ")
        }
        sb.append("\n")

        // Dashes headers
        widths.indices.forEach { index ->
            val width = widths[index]
            if (index == 0) sb.append(" ".repeat(width) + " ")
            else sb.append("-".repeat(widths[index]) + " ")
        }
        sb.append("\n")

        // Data
        for(row in 0 until rows) {
            sb.append(padLeft("${row+1}", widths[0]) + " ")
            for(column in 0 until columns) {
                val cell = coordinateToCellTag(column, row)
                val value = cells[cell] ?: ""
                sb.append(padLeft("$value", widths[column + 1]) + " ")
            }
            if (row != rows - 1) sb.append("\n")
        }
        return "$sb"
    }

    override fun toString() = render()

    companion object {
        val CURRENT = RelativeCellReferenceExpr(Coordinate(0, 0))
        val ABOVE = CURRENT.above()
        val BELOW = CURRENT.below()
        val LEFT = CURRENT.left()
        val RIGHT = CURRENT.right()

        fun indexToColumnTag(column : Int) : String {
            assert(column >= 0)
            return when (column) {
                in 0..25 -> ('A' + column).toString()
                else -> indexToColumnTag(column / 26 - 1) + indexToColumnTag(column % 26)
            }
        }

        fun columnTagToIndex(column : String) : Int {
            var result = 0
            for(c in column) {
                assert(c in 'A'..'Z')
                result *= 26
                result += (c - 'A' + 1)
            }
            return result - 1
        }

        fun cellTagToCoordinate(tag : String) : Coordinate {
            val column = StringBuilder()
            val row = StringBuilder()
            for(i in tag.indices) {
                val c = tag[i]
                if (c !in 'A'..'Z') break
                column.append(c)
            }
            for(i in column.length until tag.length) {
                val c = tag[i]
                assert(c in '0'..'9')
                row.append(c)
            }
            return Coordinate(
                column = columnTagToIndex(column.toString()),
                row = row.toString().toInt() - 1
            )
        }

        fun coordinateToCellTag(column : Int, row : Int) : String {
            if (column < 0) return "#REF!"
            if (row < 0) return "#REF!"
            return "${indexToColumnTag(column)}${row+1}"
        }

        fun coordinateToCellTag(coord : Coordinate) = coordinateToCellTag(coord.column, coord.row)
    }
}

// Coercion
private fun coerceToTypedExpr(cell : String, value : Any) : TypedExpr<*> {
    return when (value) {
        is TypedExpr<*> -> value.replaceTypedExprTopDown {
            when(it) {
                is CoerceScalar<*> -> when(it.value) {
                    is RelativeCellReferenceExpr ->
                        it.copy(value = AbsoluteCellReferenceExpr(
                            Sheet.cellTagToCoordinate(cell) + it.value.coordinate, it.type))
                    else -> it
                }
                else -> it
            }
        }
        is Number -> constant(value)
        is String -> ValueExpr(value, StringType.kaneType)
        else -> error("${value.javaClass}")
    }
}

// Times
inline operator fun <reified E:Number> RelativeCellReferenceExpr.plus(right : ScalarExpr<E>) = CoerceScalar(this, right.type) + right
inline operator fun <reified E:Number> RelativeCellReferenceExpr.plus(right : E) = CoerceScalar(this, E::class.java.algebraicType) + right
// Pow
inline fun <reified E:Number> pow(left : RelativeCellReferenceExpr, right : ScalarExpr<E>) = pow(CoerceScalar(left, right.type), right)
inline fun <reified E:Number> pow(left : RelativeCellReferenceExpr, right : E) : ScalarExpr<E> = pow(CoerceScalar(left, E::class.java.algebraicType), right)
