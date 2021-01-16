package com.github.jomof.kane

import com.github.jomof.kane.functions.*
import com.github.jomof.kane.sheet.*
import com.github.jomof.kane.types.AlgebraicType
import java.lang.Integer.max
import kotlin.math.abs
import kotlin.reflect.KProperty

interface CellReferenceExpr : Expr

data class AbsoluteCellReferenceExpr(
    private val coordinate : Coordinate
) : CellReferenceExpr {
    init { track() }
    override fun toString() = coordinateToCellName(coordinate)
}

data class UntypedRelativeCellReference(val offset : Coordinate) : UntypedScalar {
    init { track() }
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
    init { track() }
    override fun toString() = "$name=${coordinateToCellName(coordinate)}"
}

data class CoerceScalar(
    val value : Expr,
    override val type : AlgebraicType
) : ScalarExpr {
    init {
        assert(value !is CoerceScalar) {
            "coerce of coerce?"
        }
        assert(value !is TypedExpr<*> || type != value.type) {
            "coerce to same type? ${type.simpleName}"
        }
        track()
    }
    override fun toString() = "$value"
    fun copy(value : Expr) : CoerceScalar {
        return if (value === this.value) this
            else CoerceScalar(value, type)
    }
}

interface Sheet {
    val cells : Map<String, Expr>
    val columns : Int
    val rows : Int
    operator fun get(cell : String) = cells[cell]
}

data class EmptySheet(
    override val cells: Map<String, Expr> = mapOf(),
    override val columns: Int = 0,
    override val rows: Int = 0) : Sheet

data class SheetImpl(
    override val cells: Map<String, Expr>,
) : Sheet {
    override val columns : Int = cells
        .filter { looksLikeCellName(it.key)}
        .map { cellNameToCoordinate(it.key).column }.maxOrNull()!! + 1
    override val rows : Int = cells
        .filter { looksLikeCellName(it.key)}
        .map { cellNameToCoordinate(it.key).row }.maxOrNull()!! + 1
    override fun toString() = render()
}

class SheetBuilder {
    private val added : MutableList<NamedExpr> = mutableListOf()
    fun up(offset : Int = 1) = UntypedRelativeCellReference(Coordinate(column = 0, row = -offset))
    fun down(offset : Int = 1) = UntypedRelativeCellReference(Coordinate(column = 0, row = offset))
    fun left(offset : Int = 1) = UntypedRelativeCellReference(Coordinate(column = -offset, row = 0))
    fun right(offset : Int = 1) = UntypedRelativeCellReference(Coordinate(column = offset, row = 0))
    val up get() = up()
    val down get() = down()
    val left get() = left()
    val right get() = right()

    private fun getImmediateNamedExprs() : Map<String, NamedExpr> {
        val cells = mutableMapOf<String, NamedExpr>()

        // Add immediate named expressions
        added.forEach { namedExpr ->
            when(namedExpr) {
                is NamedExpr -> cells[namedExpr.name] = namedExpr
                else ->
                    error("${namedExpr.javaClass}")
            }
        }

        return cells
    }

    private fun getEmbeddedNamedExprs(cells : Map<String, NamedExpr>) : Map<String, NamedExpr> {
        val result = cells.toMutableMap()
        var done = false
        while(!done) {
            done = true
            result.values.toList().forEach {
                it.visit { child ->
                    when(child) {
                        is NamedExpr -> {
                            if (!result.containsKey(child.name)) {
                                done = false
                                result[child.name] = child
                            }
                        }
                    }
                }
            }
        }
        return result
    }

    private fun replaceRelativeReferences(cells : Map<String, NamedExpr>) : Map<String, NamedExpr> {
        return cells
            .map { (name, expr) -> expr.replaceRelativeCellReferences() }
            .map { it.name to it }.toMap()
    }

    private fun convertCellNamesToUpperCase(cells : Map<String, NamedExpr>) : Map<String, NamedExpr> {
        return cells
            .map { (name, expr) -> expr.convertCellNamesToUpperCase() }
            .map { it.name to it }.toMap()
    }

    private fun scalarizeMatrixes(cells : Map<String, NamedExpr>) : Map<String, NamedExpr> {
        val result = cells.toMutableMap()
        var done = false
        while(!done) {
            done = true
            result.toList().forEach { (name, expr) ->
                if (looksLikeCellName(name)) {
                    val upperLeft = cellNameToCoordinate(name)
                    when {
                        expr is NamedMatrix -> {
                            var replacedOurName = false
                            expr.matrix.coordinates.map { offset ->
                                val finalCoordinate = upperLeft + offset
                                val finalCellName = coordinateToCellName(finalCoordinate)
                                val extracted = extractScalarizedMatrixElement(expr.matrix, offset)
                                if (finalCellName == name) {
                                    replacedOurName = true
                                }
                                result[finalCellName] = NamedScalar(finalCellName, extracted)
                            }
                            assert(replacedOurName) {
                                "Should have replaced our own name"
                            }
                            done = false
                        }
                        expr is NamedTiling<*> -> {
                            coordinatesOf(expr.tiling.columns, expr.tiling.rows).map { offset ->
                                val finalCoordinate = upperLeft + offset
                                val finalCellName = coordinateToCellName(finalCoordinate)
                                result[finalCellName] = expr.tiling.getNamedElement(finalCellName, offset)
                            }
                        }
                    }
                }
            }
        }

        return result
    }

    private fun expandUnaryOperationMatrixes(cells : Map<String, NamedExpr>) : Map<String, NamedExpr> {
        return cells
            .map { (name, expr) ->
                val result = expr.expandUnaryOperationMatrixes()
                result
            }
            .map { it.name to it }.toMap()
    }

    private fun splitNames(cells : Map<String, Expr>) : Map<String, Expr> {
        return cells
            .map { (name, expr) ->
                name to when(expr) {
                    is NamedScalar -> when(expr.scalar) {
                        is NamedScalar -> expr.scalar
                        is NamedExpr -> error("${expr.scalar.javaClass}")
                        else -> expr.scalar
                    }
                    is NamedExpr -> error("${expr.javaClass}")
                    else ->
                        expr
                }
            }
            .toMap()
    }

    private fun replaceNamesWithCellReferences(cells : Map<String, NamedExpr>) : Map<String, Expr> {
        return cells
            .map { (name, expr) ->
                name to expr.replaceNamesWithCellReferences(name)
            }
            .toMap()
    }

    fun build() : Sheet {
        if (added.isEmpty()) return EmptySheet()
        val immediate = getImmediateNamedExprs()
        val withEmbedded = getEmbeddedNamedExprs(immediate)
        val upperCased = convertCellNamesToUpperCase(withEmbedded)
        val scalarized = scalarizeMatrixes(upperCased)
        val relativeReferencesReplaced = replaceRelativeReferences(scalarized)
        val unaryMatrixesExpanded = expandUnaryOperationMatrixes(relativeReferencesReplaced)
        val namesReplaced = replaceNamesWithCellReferences(unaryMatrixesExpanded)
        val splitNamed = splitNames(namesReplaced)
        return SheetImpl(splitNamed)
    }

    fun add(vararg add : NamedExpr) {
        added += add
    }
}

fun Sheet.eval() : Sheet {
    val reduced = reduceArithmetic(setOf())
    return SheetImpl(reduced.cells)
}

private fun Sheet.variable(cell : String) : NamedScalarVariable {
    val reduced = when(val value = cells.getValue(cell)) {
        is AlgebraicExpr ->
            value.memoizeAndReduceArithmetic()
        else ->
            error("${value.javaClass}")
    }
    return when(reduced) {
        is ConstantScalar -> NamedScalarVariable(cell, reduced.value, reduced.type)
        is ScalarVariable -> NamedScalarVariable(cell, reduced.initial, reduced.type)
        else -> error("$cell (${cells.getValue(cell)}) is not a constant")
    }
}

fun Sheet.reduceArithmetic(variables : Set<String>) : Sheet {
    var new = cells
    var done = false

    while (!done) {
        done = true
        new = new.map { (name, expr) ->
            val reduced = expr.reduceArithmetic(new, variables)
            if (reduced != expr) {
                //println("Reduced $name: $expr -> $reduced")
                done = false
            }
            name to reduced
        }.toMap()
    }

    return SheetImpl(new)
}

fun Sheet.minimize(
    target : String,
    variables : List<String>,
    learningRate : Double = 0.001) : Sheet {
    val resolvedVariables = mutableMapOf<String, NamedScalarVariable>()

    // Turn each 'variable' into a NamedScalarVariable
    variables.forEach { cell ->
        resolvedVariables[cell] = variable(cell)
    }

    // Follow all expressions from 'target'
    val reducedArithmetic = reduceArithmetic(variables.toSet())
    println("Expanding target expression")
    val lookup = cells + resolvedVariables
    var targetExpr = reducedArithmetic.cells.getValue(target).expandNamedCells(lookup)
    println("count = ${targetExpr.count()}")
    if (targetExpr !is ScalarExpr) {
        error("'$target' was not a numeric expression")
    }
    val reduced = targetExpr.reduceArithmetic()
    val namedTarget : NamedScalarExpr = NamedScalar(target, reduced)

    // Differentiate target with respect to each variable
    println("Differentiating target expression with respect to change variables")
    val diffs = resolvedVariables.map { (name, variable) ->
        val name = "d$target/d${variable.name}"
        val derivative = differentiate(d(namedTarget)/d(variable)).reduceArithmetic()
        NamedScalar(name, derivative)
    }

    // Assign back variables updated by a delta of their respective derivative
    val assignBacks = (resolvedVariables.values zip diffs).map { (variable, diff) ->
        NamedScalarAssign("update(${variable.name})", variable, variable - multiply(learningRate, diff)).reduceArithmetic()
    }

    // Create the model, allocate space for it, and iterate. Break when the target didn't move much
    println("Linearizing equations")
    val model = Tableau(resolvedVariables.values + diffs + namedTarget + assignBacks).linearize()
    val space = model.allocateSpace()
    var priorTarget = model.shape(namedTarget).ref(space).value
    println("Minimizing")
    for(i in 0 until 100000) {
//        val sb = StringBuilder()
//        sb.append(resolvedVariables.values.joinToString(" ") {
//            val value = model.shape(it).ref(space)
//            "${it.name}: $value"
//        })
//        println(sb)
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
        new[it.name] = variable(value.value, (cells[it.name] as AlgebraicExpr).type)
    }
    return SheetImpl(new)
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

    // Non-cell data
    val nonCells = cells.filter { !looksLikeCellName(it.key) }
    if (nonCells.isNotEmpty()) {
        nonCells.toList().sortedBy { it.first }.forEach {
            sb.append("\n${it.first}=${it.second}")
        }
    }
    return "$sb"
}

// Construction
fun sheetOf(init : SheetBuilder.() -> Unit) : Sheet {
    val sheet = SheetBuilder()
    init(sheet)
    return sheet.build()
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

fun looksLikeCellName(tag : String) : Boolean {
    if (tag.length < 2) return false
    if (tag[0] !in 'A'..'Z') return false
    if (tag.last() !in '0'..'9') return false
    return true
}

fun cellNameToCoordinate(tag : String) : Coordinate {
    assert(looksLikeCellName(tag))
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

