package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.ComputableIndex.FixedIndex
import com.github.jomof.kane.ComputableIndex.RelativeIndex
import com.github.jomof.kane.functions.d
import com.github.jomof.kane.functions.div
import com.github.jomof.kane.functions.minus
import com.github.jomof.kane.functions.multiply
import com.github.jomof.kane.types.AlgebraicType
import com.github.jomof.kane.types.KaneType
import java.lang.Integer.max
import kotlin.math.abs
import kotlin.math.min
import kotlin.reflect.KProperty

data class ComputableCellReference(val coordinate : ComputableCoordinate) : UntypedScalar {
    private val name by lazy { coordinateToCellName(coordinate) }
    private fun up(move: Int) = copy(coordinate = coordinate.up(move))
    fun down(move: Int) = copy(coordinate = coordinate.down(move))
    fun left(move : Int) = copy(coordinate = coordinate.left(move))
    fun right(move : Int) = copy(coordinate = coordinate.right(move))
    val up get() = up(1)
    val down get() = down(1)
    val left get() = left(1)
    val right get() = right(1)
    operator fun getValue(nothing: Nothing?, property: KProperty<*>) = run {
        NamedComputableCellReference(property.name, coordinate)
    }
    override fun toString() = name
}

data class NamedComputableCellReference(
    override val name : String,
    val coordinate : ComputableCoordinate
) : UntypedScalar, NamedExpr {
    init {
        track()
    }

    override fun toString() = "$name=${coordinateToCellName(coordinate)}"

    private fun up(move: Int) = copy(coordinate = coordinate.up(move))
    private fun down(move: Int) = copy(coordinate = coordinate.down(move))
    fun left(move: Int) = copy(coordinate = coordinate.left(move))
    fun right(move: Int) = copy(coordinate = coordinate.right(move))
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

data class ColumnDescriptor(val name : String, val type: AdmissibleDataType<*>?)
data class RowDescriptor(val name : String)
data class SheetDescriptor(val limitOutputLines : Int = Int.MAX_VALUE)
interface Sheet : Expr {
    val columnDescriptors : Map<Int, ColumnDescriptor>
    val rowDescriptors : Map<Int, RowDescriptor>
    val cells : Map<String, Expr>
    val sheetDescriptor : SheetDescriptor
    val columns : Int
    val rows : Int

    companion object {
        private data class SheetImpl(
            override val columnDescriptors : Map<Int, ColumnDescriptor>,
            override val rowDescriptors : Map<Int, RowDescriptor>,
            override val cells: Map<String, Expr>,
            override val sheetDescriptor : SheetDescriptor,
            override val columns : Int,
            override val rows : Int
        ) : Sheet {
            fun toHTML() = "<p>Some <em>HTML</em></p>"
            override fun toString() = render()
        }
        private val emptySheet = SheetImpl(mapOf(), mapOf(), mapOf(), SheetDescriptor(), 0, 0)
        fun of(
            columnDescriptors : Map<Int, ColumnDescriptor>,
            rowDescriptors : Map<Int, RowDescriptor>,
            sheetDescriptor: SheetDescriptor,
            cells : Map<String, Expr>) : Sheet {
            if (cells.isEmpty() &&
                columnDescriptors.isEmpty() &&
                rowDescriptors.isEmpty() &&
                sheetDescriptor == SheetDescriptor()) return emptySheet
            if (cells.isEmpty()) return SheetImpl(columnDescriptors, rowDescriptors, cells, sheetDescriptor, 0, 0)

            val columnsFromCells : Int = cells
                .filter { looksLikeCellName(it.key) }
                .map { cellNameToColumnIndex(it.key) }.maxOrNull()!! + 1
            val columnsColumnDescriptors : Int = columnDescriptors
                .map { it.key }.maxOrNull()?:0 + 1
            val rows : Int = cells
                .filter { looksLikeCellName(it.key) }
                .map { cellNameToRowIndex(it.key) }.maxOrNull()!! + 1
            return SheetImpl(columnDescriptors, rowDescriptors, cells, sheetDescriptor, max(columnsFromCells, columnsColumnDescriptors), rows)
        }
    }

    fun copy(
        columnDescriptors : Map<Int, ColumnDescriptor> = this.columnDescriptors,
        rowDescriptors : Map<Int, RowDescriptor> = this.rowDescriptors,
        cells : Map<String, Expr> = this.cells,
        sheetDescriptor : SheetDescriptor = this.sheetDescriptor
    ) = of(columnDescriptors, rowDescriptors, sheetDescriptor, cells)

    /**
     * Limit the number of output lines when rendering the sheet
     */
    fun limitOutputLines(limit : Int) = copy(sheetDescriptor = sheetDescriptor.copy(limitOutputLines = limit))

    /**
     * Create a new sheet with cell values changed.
     */
    fun copy(vararg assignments : Pair<String, Any>) : Sheet {
        val new = cells.toMutableMap()
        assignments.forEach { (name,expr) ->
            new[name] = when (expr) {
                is Double -> constant(expr)
                is String -> analyzeDataType(expr).parseToExpr(expr)
                is ScalarExpr -> expr
                else -> error("Unsupported cell type ")
            }
        }
        return copy(cells = new)
    }
}

class SheetBuilder {
    private val columnDescriptors : MutableMap<Int, ColumnDescriptor> = mutableMapOf()
    private val added : MutableList<NamedExpr> = mutableListOf()
    fun up(offset: Int) = ComputableCellReference(ComputableCoordinate.relative(column = 0, row = -offset))
    private fun down(offset: Int) = ComputableCellReference(ComputableCoordinate.relative(column = 0, row = offset))
    fun left(offset: Int) = ComputableCellReference(ComputableCoordinate.relative(column = -offset, row = 0))
    fun right(offset : Int) = ComputableCellReference(ComputableCoordinate.relative(column = offset, row = 0))
    val up get() = up(1)
    val down get() = down(1)
    val left get() = left(1)
    val right get() = right(1)

    private fun parseAndNameValue(name : String, value : String) : NamedExpr {
        val admissibleType = analyzeDataType(value)
        return when(val parsed = analyzeDataType(value).parseToExpr(value)) {
            is ScalarExpr -> NamedScalar(name, parsed)
            is ValueExpr<*> -> NamedValueExpr(name, value, admissibleType.type as KaneType<Any>)
            else -> error("${parsed.javaClass}")
        }
    }

    operator fun String.getValue(nothing: Nothing?, property: KProperty<*>) = parseAndNameValue(property.name, this)
    operator fun Number.getValue(nothing: Nothing?, property: KProperty<*>) = NamedScalar(property.name, constant(this.toDouble()))

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
            .map { (_, expr) -> expr.replaceRelativeCellReferences() }
            .map { it.name to it }.toMap()
    }

    private fun convertCellNamesToUpperCase(cells : Map<String, NamedExpr>) : Map<String, NamedExpr> {
        return cells
            .map { (_, expr) -> expr.convertCellNamesToUpperCase() }
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
                    when (expr) {
                        is NamedMatrix -> {
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
                        is NamedTiling<*> -> {
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
            .map { (_, expr) ->
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
                    columnDescriptors[expr.coordinate.column.index] = ColumnDescriptor(name, null)
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
        return Sheet.of(columnDescriptors, mapOf(), SheetDescriptor(), splitNamed)
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

    fun column(index : Int, name : String, type : AdmissibleDataType<*>? = null) {
        columnDescriptors[index] = ColumnDescriptor(name, type)
    }

    fun range(name : String) : ComputableCellReference {
        val index = cellNameToColumnIndex(name)
        return ComputableCellReference(
            ComputableCoordinate(
                column = FixedIndex(index),
                row = RelativeIndex(0)
            )
        )
    }
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
    val targetExpr = reducedArithmetic.cells.getValue(target).expandNamedCells(lookup)
    println("count = ${targetExpr.count()}")
    if (targetExpr !is ScalarExpr) {
        error("'$target' was not a numeric expression")
    }
    val reduced = targetExpr.reduceArithmetic()
    val namedTarget : NamedScalarExpr = NamedScalar(target, reduced)

    // Differentiate target with respect to each variable
    println("Differentiating target expression with respect to change variables")
    val diffs = resolvedVariables.map { (_, variable) ->
        val name = "d$target/d${variable.name}"
        val derivative = differentiate(d(namedTarget) / d(variable)).reduceArithmetic()
        NamedScalar(name, derivative)
    }

    // Assign back variables updated by a delta of their respective derivative
    val assignBacks = (resolvedVariables.values zip diffs).map { (variable, diff) ->
        NamedScalarAssign("update(${variable.name})", variable, variable - multiply(learningRate, diff)).reduceArithmetic()
    }

    // Create the model, allocate space for it, and iterate. Break when the target didn't move much
    println("Linearizing equations")
    val model = Tableau(resolvedVariables.values + diffs + namedTarget + assignBacks, diffs[0].type).linearize()
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
    return copy(cells = new)
}

fun Sheet.render() : String {
    if (cells.isEmpty()) return ""
    val sb = StringBuilder()
    val widths = Array(columns + 1) {
        if (it > 0) columnDescriptors[it-1]?.name?.length?:0 else 0
    }

    fun colName(column : Int) = columnDescriptors[column]?.name ?: indexToColumnName(column)
    fun rowName(row : Int) = rowDescriptors[row]?.name ?: "$row"

    // Effective row count for formatting
    val rows = min(rows, sheetDescriptor.limitOutputLines)

    // Calculate width of row number column
    for(row in 0 until rows + 1) {
        widths[0] = max(widths[0], rowName(row).length)
    }

    // Calculate widths of column headers
    for(column in 0 until columns) {
        widths[column + 1] = max(widths[column + 1], colName(column).length)
    }

    // Calculate widths
    for(row in 0 until rows) {
        for(column in 0 until columns) {
            val cell = coordinateToCellName(ComputableCoordinate.fixed(column, row))
            val value = cells[cell]?.toString() ?: ""
            widths[column + 1] = max(widths[column + 1], value.length)
        }
    }

    // Column headers
    widths.indices.forEach { index ->
        val width = widths[index]
        if (index == 0) sb.append(" ".repeat(width) + " ")
        else {
            val columnName = colName(index - 1)
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
        sb.append(padLeft(rowName(row+1), widths[0]) + " ")
        for(column in 0 until columns) {
            val cell = coordinateToCellName(ComputableCoordinate.fixed(column, row))
            val value = cells[cell]?.toString() ?: ""
            sb.append(padLeft(value, widths[column + 1]) + " ")
        }
        if (row != rows - 1) sb.append("\n")
    }
    if (this.rows > rows) {
        sb.append("\n...and ${this.rows-rows} more rows")
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

