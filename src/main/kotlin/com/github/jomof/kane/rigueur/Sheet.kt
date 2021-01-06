package com.github.jomof.kane.rigueur

import com.github.jomof.kane.rigueur.Direction.*
import com.github.jomof.kane.rigueur.functions.d
import com.github.jomof.kane.rigueur.functions.div
import com.github.jomof.kane.rigueur.functions.minus
import com.github.jomof.kane.rigueur.functions.multiply
import com.github.jomof.kane.rigueur.types.AlgebraicType
import com.github.jomof.kane.rigueur.types.KaneType
import com.github.jomof.kane.rigueur.types.StringType
import java.lang.Integer.max
import kotlin.math.abs
import kotlin.reflect.KProperty

interface CellReferenceExpr : Expr

data class AbsoluteCellReferenceExpr(
    private val coordinate : Coordinate
) : CellReferenceExpr {
    override fun toString() = coordinateToCellName(coordinate)
}

data class UntypedRelativeCellReference(val offset : Coordinate) : UntypedScalar {
    override fun toString() = "ref$offset"
    fun up(move : Int = 1) = UntypedRelativeCellReference(offset + Coordinate(column = 0, row = -move))
    fun down(move : Int = 1) = UntypedRelativeCellReference(offset + Coordinate(column = 0, row = move))
    fun left(move : Int = 1) = UntypedRelativeCellReference(offset + Coordinate(column = -move, row = 0))
    fun right(move : Int = 1) = UntypedRelativeCellReference(offset + Coordinate(column = move, row = 0))
    val up get() = up()
    val down get() = down()
    val left get() = left()
    val right get() = right()
    operator fun getValue(nothing: Nothing?, property: KProperty<*>) = run {
        val name = property.name.toUpperCase()
        val source = cellNameToCoordinate(name)
        NamedUntypedAbsoluteCellReference(name, source + offset)
    }

    fun toAbsolute(source : Coordinate) = AbsoluteCellReferenceExpr(source + offset)
}

data class NamedUntypedAbsoluteCellReference(
    override val name : String,
    val coordinate : Coordinate) : UntypedScalar, NamedExpr {
    override fun toString() = "$name=${coordinateToCellName(coordinate)}"
}

data class CoerceScalar<T:Number>(
    val value : Expr,
    override val type : AlgebraicType<T>
) : ScalarExpr<T> {
    override fun toString() = "$value"
    fun mapChildren(f : ExprFunction) = copy(value = f(value))
}

interface Sheet {
    val name : String
    val cells : Map<String, Expr>
    val columns : Int
    val rows : Int
    operator fun get(cell : String) = cells[cell]
}

interface MutableSheet : Sheet

data class SheetBuilder(
    override var name : String = "",
    override val cells : MutableMap<String, Expr> = mutableMapOf()
) : MutableSheet {
    override val columns get() = cells.map { cellNameToCoordinate(it.key).column }.maxOrNull()!! + 1
    override val rows get() = cells.map { cellNameToCoordinate(it.key).row }.maxOrNull()!! + 1
    fun up(offset : Int = 1) = UntypedRelativeCellReference(Coordinate(column = 0, row = -offset))
    fun down(offset : Int = 1) = UntypedRelativeCellReference(Coordinate(column = 0, row = offset))
    fun left(offset : Int = 1) = UntypedRelativeCellReference(Coordinate(column = -offset, row = 0))
    fun right(offset : Int = 1) = UntypedRelativeCellReference(Coordinate(column = offset, row = 0))
    val up get() = up()
    val down get() = down()
    val left get() = left()
    val right get() = right()

    fun add(vararg added : NamedExpr) {
        val remaining = added.toList().toMutableSet()
        while(remaining.isNotEmpty()) {
            val named = remaining.first()
            remaining -= named
            val name = named.name.toUpperCase()
            if (cells.containsKey(name)) continue
            val coordinate = cellNameToCoordinate(name)
            when(named) {
                is NamedScalar<*> -> {
                    val expr = named.scalar
                    val cellRefs = replaceRelativeCellReferences(coordinate, expr)
                    val typed = cellRefs.replaceExpr {
                        when(it) {
                            is NamedScalarVariable<*> -> {
                                val name = it.name.toUpperCase()
                                val coordinate = cellNameToCoordinate(name) // Check name is valid cell name
                                cells[name] = variable(it.initial, it.type as AlgebraicType<Number>)
                                CoerceScalar(AbsoluteCellReferenceExpr(coordinate), it.type)
                            }
                            is NamedScalar<*> -> {
                                remaining += it
                                when {
                                    it.scalar is ConstantScalar<*> -> {
                                        val name = it.name.toUpperCase()
                                        val coordinate = cellNameToCoordinate(name) // Check name is valid cell name
                                        cells[name] = variable(it.scalar.value, it.type as AlgebraicType<Number>)
                                        CoerceScalar(AbsoluteCellReferenceExpr(coordinate), it.type)
                                    }
                                    else -> it
                                }
                            }
                            else ->
                                it
                        }
                    }
                    cells[name] = typed
                }
                is NamedScalarVariable<*> -> {
                    cells[name] = variable(named.initial, named.type as AlgebraicType<Number>)
                }
                is NamedValueExpr<*> -> {
                    cells[name] = ValueExpr(named.value, named.type as KaneType<Any>)
                }
                is NamedMatrix<*> -> {
                    named.matrix.coordinates.forEach { target ->
                        val coordinate = coordinate + target
                        cells[coordinateToCellName(coordinate)] = replaceRelativeCellReferences(coordinate, named.matrix[target])
                    }
                }
                is NamedUntypedAbsoluteCellReference -> {
                    cells[name] = AbsoluteCellReferenceExpr(named.coordinate)
                }
                else -> error("${named.javaClass}")
            }
        }
    }
    override fun toString() = render()
}

fun Sheet.eval() : Sheet {
    val cells = cells.toMutableMap()

    // Replace variables with their initial value
    cells.forEach { (cell, expr) ->
        if (expr is ScalarVariable<*>) {
            cells[cell] = constant(expr.initial, expr.type)
        }
    }

    // Expand cell references
    var done = false
    var remainingDepth = 5
    while(!done && remainingDepth > 0) {
        done = true
        --remainingDepth
        if (remainingDepth == 0) break
        for ((cell, expr) in cells) {
            val replaced = expr.replaceExpr(BOTTOM_UP) {
                when {
                    it is CellReferenceExpr -> {
                        val tag = "$it"
                        val lookup = cells[tag]
                        if (lookup != null) done = false
                        lookup ?: it
                    }
                    else -> it
                }
            }
            cells[cell] = replaced
        }
    }

    if (remainingDepth == 0) return this

    // Reduce arithmetic
    cells.forEach { (cell, expr) ->
        cells[cell] = when(expr) {
            is AlgebraicExpr<*> -> expr.memoizeAndReduceArithmetic()
            else -> expr
        }
    }
    return SheetBuilder(name = name, cells = cells)
}

fun Sheet.minimize(
    target : String,
    vararg variables : String) : Sheet {
    val resolvedVariables = mutableMapOf<String, NamedScalarVariable<Double>>()

    // Turn each 'variable' into a NamedScalarVariable
    variables.forEach { cell ->
        val reduced = when(val value = cells.getValue(cell)) {
            is AlgebraicExpr<*> ->
                value.memoizeAndReduceArithmetic()
            else ->
                error("${value.javaClass}")
        }
        val variable = when(reduced) {
            is ConstantScalar -> NamedScalarVariable(cell, reduced.value as Double, reduced.type as AlgebraicType<Double>)
            is ScalarVariable -> NamedScalarVariable(cell, reduced.initial as Double, reduced.type as AlgebraicType<Double>)
            else -> error("$cell (${cells.getValue(cell)}) is not a constant")
        }
        resolvedVariables[cell] = variable
    }

    // Follow all expressions from 'target'
    var targetExpr = cells.getValue(target)
    var done = false
    while(!done) {
        done = true
        val next = targetExpr.replaceExpr {
            when(it) {
                is CoerceScalar<*> -> {
                    when(it.value) {
                        is ScalarVariable<*> -> {
                            // This variable is not being optimized so treat it as constant
                            constant(it.value.initial)
                        }
                        is ScalarExpr<*> -> it.value
                        else -> error("${it.value.javaClass}")
                    }
                }
                is AbsoluteCellReferenceExpr -> {
                    val ref = "$it"
                    val result = resolvedVariables[ref]?:cells.getValue(ref)
                    done = false
                    result
                }
                else -> it
            }
        } as ScalarExpr<Double>
        targetExpr = next
    }
    if (targetExpr !is ScalarExpr<*>) {
        error("'$target' was not a numeric expression")
    }
    val reduced = (targetExpr as ScalarExpr<Double>).reduceArithmetic()
    val namedTarget : NamedScalarExpr<Double> = NamedScalar(target, reduced)

    // Differentiate target with respect to each variable
    val diffs = resolvedVariables.map { (name, variable) ->
        val name = "d$target/d${variable.name}"
        val derivative = differentiate(d(namedTarget)/d(variable)).reduceArithmetic()
        NamedScalar(name, derivative)
    }

    // Assign back variables updated by a delta of their respective derivative
    val assignBacks = (resolvedVariables.values zip diffs).map { (variable, diff) ->
        NamedScalarAssign("update(${variable.name})", variable, variable - multiply(0.01, diff)).reduceArithmetic()
    }

    // Create the model, allocate space for it, and iterate. Break when the target didn't move much
    val model = Tableau(resolvedVariables.values + diffs + namedTarget + assignBacks).linearize()
    val space = model.allocateSpace()
    var priorTarget = model.shape(namedTarget).ref(space).value
    for(i in 0 until 100000) {
        val sb = StringBuilder()
        sb.append(resolvedVariables.values.joinToString(" ") {
            val value = model.shape(it).ref(space)
            "${it.name}: $value"
        })
        model.eval(space)
        val newTarget = model.shape(namedTarget).ref(space).value
        if (abs(newTarget-priorTarget) < 0.0000000000001)
            break
        priorTarget = newTarget
    }

    // Extract the computed values and plug them back into a new sheet
    val new = cells.toMutableMap()
    resolvedVariables.values.forEach {
        val value = model.shape(it).ref(space)
        assert(new.containsKey(it.name))
        new[it.name] = variable(value.value, (cells[it.name] as AlgebraicExpr<Double>).type)
    }
    return SheetBuilder(name, new)
}

fun Sheet.render() : String {
    if (cells.isEmpty()) return ""
    val sb = StringBuilder()
    val widths = Array(columns + 1) { 0 }


    // Calculate widths
    for(row in 0 until rows) {
        widths[0] = max(widths[0], "$row".length)
        for(column in 0 until columns) {
            val cell = coordinateToCellName(column, row)
            val value = cells[cell]?.toString() ?: ""
            widths[column + 1] = max(widths[column + 1], value.length)
            widths[column + 1] = max(widths[column + 1], indexToColumnName(column).length)
        }
    }

    // Column headers
    widths.indices.forEach { index ->
        val width = widths[index]
        if (index == 0) sb.append(" ".repeat(width) + " ")
        else sb.append(padCenter(indexToColumnName(index - 1), width) + " ")
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
            val cell = coordinateToCellName(column, row)
            val value = cells[cell]?.toString() ?: ""
            sb.append(padLeft(value, widths[column + 1]) + " ")
        }
        if (row != rows - 1) sb.append("\n")
    }
    return "$sb"
}

// Construction
fun sheetOf(name : String = "", init : SheetBuilder.() -> Unit) : Sheet {
    val sheet = SheetBuilder(name = name)
    init(sheet)
    return sheet
}


// Cell naming
fun coordinateToCellName(column : Int, row : Int) : String {
    if (column < 0) return "#REF!"
    if (row < 0) return "#REF!"
    return "${indexToColumnName(column)}${row+1}"
}

fun coordinateToCellName(coord : Coordinate) = coordinateToCellName(coord.column, coord.row)

fun columnNameToIndex(column : String) : Int {
    var result = 0
    for(c in column) {
        assert(c in 'A'..'Z')
        result *= 26
        result += (c - 'A' + 1)
    }
    return result - 1
}


fun indexToColumnName(column : Int) : String {
    assert(column >= 0)
    return when (column) {
        in 0..25 -> ('A' + column).toString()
        else -> indexToColumnName(column / 26 - 1) + indexToColumnName(column % 26)
    }
}

fun cellNameToCoordinate(tag : String) : Coordinate {
    val column = StringBuilder()
    val row = StringBuilder()
    for(i in tag.indices) {
        val c = tag[i]
        if (c !in 'A'..'Z') break
        column.append(c)
    }
    for(i in column.length until tag.length) {
        val c = tag[i]
        assert(c in '0'..'9') {
            error("Could not convert $tag to a cell name")
        }
        row.append(c)
    }
    assert(row.isNotEmpty()) {
        "Cell name '$tag' did not have a number row part"
    }
    return Coordinate(
        column = columnNameToIndex(column.toString()),
        row = row.toString().toInt() - 1
    )
}


// Coercion
private fun replaceRelativeCellReferences(coordinate : Coordinate, value : Any) : Expr {
    return when (value) {
        is Expr -> value.replaceExpr(TOP_DOWN) {
            when(it) {
                is NamedScalar<*> -> {
                    val coordinate = cellNameToCoordinate(it.name.toUpperCase())
                    CoerceScalar(AbsoluteCellReferenceExpr(coordinate), it.type)
                }
                is UntypedRelativeCellReference -> it.toAbsolute(coordinate)
                else -> it
            }
        }
        is Number -> constant(value)
        is String -> ValueExpr(value, StringType.kaneType)
        else -> error("${value.javaClass}")
    }
}