package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.ComputableIndex.FixedIndex
import com.github.jomof.kane.ComputableIndex.RelativeIndex
import com.github.jomof.kane.functions.*
import com.github.jomof.kane.track
import com.github.jomof.kane.types.AlgebraicType
import com.github.jomof.kane.types.KaneType
import java.lang.Integer.max
import kotlin.math.abs
import kotlin.reflect.KProperty


data class ComputableCellReference(val coordinate : ComputableCoordinate) : UntypedScalar {
    fun up(move : Int) = copy(coordinate = coordinate.up(move))
    fun down(move : Int) = copy(coordinate = coordinate.down(move))
    fun left(move : Int) = copy(coordinate = coordinate.left(move))
    fun right(move : Int) = copy(coordinate = coordinate.right(move))
    val up get() = up(1)
    val down get() = down(1)
    val left get() = left(1)
    val right get() = right(1)
    operator fun getValue(nothing: Nothing?, property: KProperty<*>) = run {
        NamedComputableCellReference(property.name, coordinate)
    }

    override fun toString() = coordinateToCellName(coordinate)
}

data class NamedComputableCellReference(
    override val name : String,
    val coordinate : ComputableCoordinate
) : UntypedScalar, NamedExpr {
    init { track() }
    override fun toString() = "$name=${coordinateToCellName(coordinate)}"

    fun up(move : Int) = copy(coordinate = coordinate.up(move))
    fun down(move : Int) = copy(coordinate = coordinate.down(move))
    fun left(move : Int) = copy(coordinate = coordinate.left(move))
    fun right(move : Int) = copy(coordinate = coordinate.right(move))
    val up get() = up(1)
    val down get() = down(1)
    val left get() = left(1)
    val right get() = right(1)
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

data class ColumnDescriptor(val name : String)

interface Sheet {
    val columnDescriptors : Map<Int, ColumnDescriptor>
    val cells : Map<String, Expr>
    val columns : Int
    val rows : Int
    operator fun get(cell : String) = cells[cell]
    companion object {
        private data class SheetImpl(
            override val columnDescriptors : Map<Int, ColumnDescriptor>,
            override val cells: Map<String, Expr>,
            override val columns : Int,
            override val rows : Int
        ) : Sheet {
            override fun toString() = render()

        }
        private val emptySheet = SheetImpl(mapOf(), mapOf(), 0, 0)
        fun of(columnDescriptors : Map<Int, ColumnDescriptor>, cells : Map<String, Expr>) : Sheet {
            if (cells.isEmpty() && columnDescriptors.isEmpty()) return emptySheet
            if (cells.isEmpty()) return SheetImpl(columnDescriptors, cells, 0, 0)

            val columnsFromCells : Int = cells
                .filter { looksLikeCellName(it.key) }
                .map { (cellNameToCoordinate(it.key).column as FixedIndex).index }.maxOrNull()!! + 1
            val columnsColumnDescriptors : Int = columnDescriptors
                .map { it.key }.maxOrNull()?:0 + 1
            val rows : Int = cells
                .filter { looksLikeCellName(it.key) }
                .map { (cellNameToCoordinate(it.key).row as FixedIndex).index }.maxOrNull()!! + 1
            return SheetImpl(columnDescriptors, cells, max(columnsFromCells, columnsColumnDescriptors), rows)
        }
    }
}

class SheetBuilder {
    private val columnDescriptors : MutableMap<Int, ColumnDescriptor> = mutableMapOf()
    private val added : MutableList<NamedExpr> = mutableListOf()
    fun up(offset : Int) = ComputableCellReference(ComputableCoordinate.relative(column = 0, row = -offset))
    fun down(offset : Int) = ComputableCellReference(ComputableCoordinate.relative(column = 0, row = offset))
    fun left(offset : Int) = ComputableCellReference(ComputableCoordinate.relative(column = -offset, row = 0))
    fun right(offset : Int) = ComputableCellReference(ComputableCoordinate.relative(column = offset, row = 0))
    val up get() = up(1)
    val down get() = down(1)
    val left get() = left(1)
    val right get() = right(1)

    private fun getImmediateNamedExprs() : Map<String, NamedExpr> {
        val cells = mutableMapOf<String, NamedExpr>()

        // Add immediate named expressions
        added.forEach { namedExpr ->
            cells[namedExpr.name] = namedExpr
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
                    val upperLeft = cellNameToCoordinate(name).reduceToFixed()
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
                        is NamedScalarVariable -> expr.scalar
                        is NamedExpr -> error("${expr.scalar.javaClass}")
                        else -> expr.scalar
                    }
                    is NamedMatrix -> expr.matrix
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
                if (expr is NamedComputableCellReference
                    && expr.coordinate.column is FixedIndex) {
                    columnDescriptors[expr.coordinate.column.index] = ColumnDescriptor(name)
                }
                name to expr.replaceNamesWithCellReferences(name)
            }
            .toMap()
    }

    fun build() : Sheet {
        val immediate = getImmediateNamedExprs()
        val withEmbedded = getEmbeddedNamedExprs(immediate)
        val upperCased = convertCellNamesToUpperCase(withEmbedded)
        val scalarized = scalarizeMatrixes(upperCased)
        val relativeReferencesReplaced = replaceRelativeReferences(scalarized)
        val unaryMatrixesExpanded = expandUnaryOperationMatrixes(relativeReferencesReplaced)
        val namesReplaced = replaceNamesWithCellReferences(unaryMatrixesExpanded)
        val splitNamed = splitNames(namesReplaced)
        return Sheet.of(columnDescriptors, splitNamed)
    }

    fun add(vararg add : NamedExpr) {
        added += add
    }

    fun set(cell : Coordinate, value : Any, type : KaneType<*>) {
        val name = coordinateToCellName(ComputableCoordinate.fixed(cell))
        add(if (value is Double) NamedScalar(name, constant(value, type as AlgebraicType))
            else NamedValueExpr(name, value, type as KaneType<Any>)
        )
    }

    fun column(index : Int, name : String) {
        columnDescriptors[index] = ColumnDescriptor(name)
    }

    fun range(name : String) : ComputableCellReference {
        val index = columnNameToIndex(name)
        return ComputableCellReference(
            ComputableCoordinate(
                column = FixedIndex(index),
                row = RelativeIndex(0)
            )
        )
    }
}

fun Sheet.eval() : Sheet {
    val reduced = reduceArithmetic(setOf())
    return Sheet.of(columnDescriptors, reduced.cells)
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
            if (!done || reduced != expr) {
                done = false
            }
            name to reduced
        }.toMap()
    }

    return Sheet.of(columnDescriptors, new)
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
    val lookup = reducedArithmetic.cells + resolvedVariables
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
    return Sheet.of(columnDescriptors, new)
}

fun Sheet.render() : String {
    if (cells.isEmpty()) return ""
    val sb = StringBuilder()
    val widths = Array(columns + 1) {
        if (it > 0) columnDescriptors[it-1]?.name?.length?:0 else 0
    }

    // Calculate widths
    for(row in 0 until rows) {
        widths[0] = max(widths[0], "$row".length)
        for(column in 0 until columns) {
            val cell = coordinateToCellName(ComputableCoordinate.fixed(column, row))
            val value = cells[cell]?.toString() ?: ""
            widths[column + 1] = max(widths[column + 1], value.length)
            widths[column + 1] = max(widths[column + 1], indexToColumnName(column).length)
        }
    }

    // Column headers
    widths.indices.forEach { index ->
        val width = widths[index]
        if (index == 0) sb.append(" ".repeat(width) + " ")
        else {
            val columnName = columnDescriptors[index-1]?.name ?: indexToColumnName(index - 1)
            sb.append(padCenter(columnName, width) + " ")
        }
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
            val cell = coordinateToCellName(ComputableCoordinate.fixed(column, row))
            val value = cells[cell]?.toString() ?: ""
            sb.append(padLeft(value, widths[column + 1]) + " ")
        }
        if (row != rows - 1) sb.append("\n")
    }

    // Non-cell data
    val nonCells = cells.filter { !looksLikeCellName(it.key) }
    if (nonCells.isNotEmpty()) {
        nonCells.toList().sortedBy { it.first }.forEach {
            if (it.second !is ComputableCellReference) {
                sb.append("\n${it.first}=${it.second}")
            }
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
fun coordinateToCellName(column : ComputableIndex, row : ComputableIndex) : String {
    return when {
        column is FixedIndex && row is FixedIndex -> {
            when {
                column.index < 0 -> "#REF!"
                row.index < 0 -> "#REF!"
                else -> indexToColumnName(column) + "${row.index + 1}"
            }
        }
        column is FixedIndex && row is RelativeIndex -> "[fixed ${column.index},${row.index}]"
        column is RelativeIndex && row is FixedIndex -> "[${column.index},fixed ${row.index}]"
        column is RelativeIndex && row is RelativeIndex -> "[${column.index},${row.index}]"
        else -> error("")
    }
}

fun coordinateToCellName(column : Int, row : Int) : String {
    return coordinateToCellName(FixedIndex(column), FixedIndex(row))
}

fun coordinateToCellName(coord : ComputableCoordinate) = coordinateToCellName(coord.column, coord.row)
fun coordinateToCellName(coord : Coordinate) = coordinateToCellName(FixedIndex(coord.column), FixedIndex(coord.row))

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

fun indexToColumnName(column : ComputableIndex) : String {
    return when(column) {
        is FixedIndex -> {
            val columnName = indexToColumnName(column.index)
            columnName
        }
        is RelativeIndex -> "[${column.index}]"
    }
}

fun looksLikeCellName(tag : String) : Boolean {
    if (tag.length < 2) return false
    if (tag[0] !in 'A'..'Z') return false
    if (tag.last() !in '0'..'9') return false
    return true
}

fun cellNameToCoordinate(tag : String) : ComputableCoordinate {
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
    return ComputableCoordinate(
        column = FixedIndex(columnNameToIndex(column.toString())),
        row = FixedIndex(row.toString().toInt() - 1)
    )
}

