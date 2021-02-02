package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.functions.*
import com.github.jomof.kane.types.AlgebraicType
import com.github.jomof.kane.types.DoubleAlgebraicType
import com.github.jomof.kane.types.KaneType
import com.github.jomof.kane.visitor.RewritingVisitor
import com.github.jomof.kane.visitor.visit
import java.lang.Integer.max
import kotlin.math.abs
import kotlin.math.min
import kotlin.reflect.KProperty

data class SheetRangeExpr(val range: SheetRange) : UntypedScalar {
    private val name by lazy { range.toString() }
    fun up(move: Int) = copy(range = range.up(move))
    fun down(move: Int) = copy(range = range.down(move))
    fun left(move: Int) = copy(range = range.left(move))
    fun right(move: Int) = copy(range = range.right(move))
    val up get() = up(1)
    val down get() = down(1)
    val left get() = left(1)
    val right get() = right(1)
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toString() = name
}

data class NamedSheetRangeExpr(
    override val name: String,
    val range: SheetRangeExpr
) : UntypedScalar, NamedExpr {
    init {
        track()
        assert(name != "increasing" || range.toString() != "Brow-1") {
            "Should have stripped name?"
        }
    }

    override fun toString() = render()

    fun up(move: Int) = range.up(move)
    fun down(move: Int) = range.down(move)
    fun left(move: Int) = range.left(move)
    fun right(move: Int) = range.right(move)
    val up get() = up(1)
    val down get() = down(1)
    val left get() = left(1)
    val right get() = right(1)
}

data class CoerceScalar(
    val value: Expr,
    override val type: AlgebraicType
) : ScalarExpr {
    init {
        assert(value !is ScalarExpr) {
            "coerce of scalar?"
        }
        assert(value !is CoerceScalar) {
            "coerce of coerce?"
        }
        assert(value !is TypedExpr<*> || type != value.type) {
            "coerce to same type? ${type.simpleName}"
        }
        track()
    }

    override fun toString() = render()
    fun copy(value: Expr): ScalarExpr {
        return when {
            value === this.value -> this
            value is ScalarExpr -> value
            else -> CoerceScalar(value, type)
        }
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
    fun limitOutputLines(limit: Int) = copy(sheetDescriptor = sheetDescriptor.copy(limitOutputLines = limit))

    /**
     * Create a new sheet with cell values changed.
     */
    fun copy(vararg assignments: Pair<String, Any>): Sheet {
        val sb = toBuilder()
        assignments.forEach { (name, any) ->
            sb.add(convertAnyToNamedExpr(name, any))
        }
        return sb.build()
    }

    /**
     * Create a new sheet with cell values changed.
     */
    fun copy(init: SheetBuilderImpl.() -> List<NamedExpr>): Sheet {
        val sheet = toBuilder()
        init(sheet).forEach { sheet.add(it) }
        return sheet.build()
    }


    fun toBuilder(): SheetBuilderImpl {
        val named = cells.map { (name, expr) -> expr.toNamed(name) }
        return SheetBuilderImpl(
            columnDescriptors = columnDescriptors,
            rowDescriptors = rowDescriptors,
            sheetDescriptor = sheetDescriptor,
            added = named
        )
    }
}

class SheetBuilderImpl(
    columnDescriptors: Map<Int, ColumnDescriptor> = mapOf(),
    rowDescriptors: Map<Int, RowDescriptor> = mapOf(),
    val sheetDescriptor: SheetDescriptor = SheetDescriptor(),
    added: List<NamedExpr> = listOf()
) : SheetBuilder {
    internal val columnDescriptors: MutableMap<Int, ColumnDescriptor> = columnDescriptors.toMutableMap()
    internal val rowDescriptors: MutableMap<Int, RowDescriptor> = rowDescriptors.toMutableMap()
    private val added: MutableList<NamedExpr> = added.toMutableList()

    override fun column(range: String): SheetBuilderRange {
        val parsed = parseRange(range)
        assert(parsed is ColumnRange || parsed is NamedColumnRange)
        return SheetBuilderRange(this, parsed)
    }

    fun getImmediateNamedExprs(): Map<String, Expr> {
        val cells = mutableMapOf<String, Expr>()

        // Add immediate named expressions
        added.forEach { namedExpr ->
            cells[namedExpr.name] = namedExpr.toUnnamed()
        }

        return cells
    }

    private fun getEmbeddedNamedExprs(cells: Map<String, Expr>): Map<String, Expr> {
        val result = cells.toMutableMap()
        var done = false
        while (!done) {
            done = true
            result.values.toList().forEach {
                it.visit { child ->
                    when (child) {
                        is NamedExpr -> {
                            if (!result.containsKey(child.name)) {
                                done = false
                                result[child.name] = child.toUnnamed()
                            }
                        }
                    }
                }
            }
        }
        return result
    }

    private fun replaceRelativeReferences(cells: Map<String, Expr>): Map<String, Expr> {
        return cells
            .map { (name, expr) ->
                val base =
                    if (looksLikeCellName(name)) cellNameToCoordinate(name) else Coordinate(0, 0)

                name to expr.replaceRelativeCellReferences(base)
            }
            .toMap()
    }

    private fun convertCellNamesToUpperCase(cells: Map<String, Expr>): Map<String, Expr> {
        return cells
            .map { (name, expr) ->
                val upper = name.toUpperCase()
                val new = if (looksLikeCellName(upper)) upper else name
                new to expr.convertCellNamesToUpperCase()
            }
            .map { it.first to it.second }.toMap()
    }

    private fun scalarizeMatrixes(cells: Map<String, Expr>): Map<String, Expr> {
        val result = cells.map { it.key to it.value }.toMap().toMutableMap()
        var done = false
        while (!done) {
            done = true
            result.toList().forEach { (name, expr) ->
                if (!looksLikeCellName(name)) {
                    when (expr) {
                        is MatrixExpr,
                        is Tiling<*> -> {
                            done = false
                            val cellsCoordinates =
                                result.map { it.key }.filter { looksLikeCellName(it) }.map { cellNameToCoordinate(it) }
                            val columns = cellsCoordinates.map { it.column }.toSet()
                            val existingColumns = (columnDescriptors.keys + columns).sorted()
                            done = false
                            for (next in 0 until Int.MAX_VALUE) {
                                if (!existingColumns.contains(next)) {
                                    columnDescriptors[next] = ColumnDescriptor(name, null)
                                    val allocatedColumnBase = coordinateToCellName(next, 0)
                                    result[allocatedColumnBase] = expr
                                    result.remove(name)
                                    break
                                }
                            }
                        }
                    }
                } else {
                    val upperLeft = cellNameToCoordinate(name)
                    when (expr) {
                        is ScalarExpr,
                        is ValueExpr<*>,
                        is SheetRangeExpr,
                        is AlgebraicBinaryRangeStatistic -> {
                        }
                        is MatrixExpr -> {
                            var replacedOurName = false
                            expr.coordinates.map { offset ->
                                val finalCoordinate = upperLeft + offset
                                val finalCellName = coordinateToCellName(finalCoordinate)
                                val extracted = extractScalarizedMatrixElement(expr, offset)
                                    .slideScalarizedCellsRewritingVisitor(upperLeft, offset)
                                if (finalCellName == name) {
                                    replacedOurName = true
                                }
                                result[finalCellName] = extracted
                            }
                            assert(replacedOurName) {
                                "Should have replaced our own name in $name"
                            }
                            done = false
                        }
                        is Tiling<*> -> {
                            coordinatesOf(expr.columns, expr.rows).map { offset ->
                                val finalCoordinate = upperLeft + offset
                                val finalCellName = coordinateToCellName(finalCoordinate)
                                result[finalCellName] = expr.getUnnamedElement(offset)
                            }
                        }
                        else -> error("${expr.javaClass}")
                    }
                }
            }
        }

        return result.map { it.key to it.value }.toMap()
    }

    private fun expandUnaryOperations(cells: Map<String, Expr>): Map<String, Expr> {
        return cells
            .map { (name, expr) ->
                val result = expr.expandUnaryOperations()
                name to result
            }.toMap()
    }

    private fun replaceNamesWithCellReferences(cells: Map<String, Expr>): Map<String, Expr> {
        return cells
            .map { (name, expr) ->
//                if (expr is ComputableCellReference
//                    && (expr.range as ComputableCoordinate).column is MoveableIndex) {
//                    columnDescriptors[expr.range.column.index] = ColumnDescriptor(name, null)
//                }
                name to expr.replaceNamesWithCellReferences(name)
            }
            .toMap()
    }

    fun build() : Sheet {
        val immediate = getImmediateNamedExprs()
        val debuilderized = removeBuilderPrivateExpressions(immediate)
        val noNamedColumns = convertNamedColumnsToColumnRanges(debuilderized)
        val withEmbedded = getEmbeddedNamedExprs(noNamedColumns)
        val upperCased = convertCellNamesToUpperCase(withEmbedded)
        val scalarized = scalarizeMatrixes(upperCased)
        val scalarizeRanges = scalarizeRanges(scalarized)
        val relativeReferencesReplaced = replaceRelativeReferences(scalarizeRanges)
        val unaryMatrixesExpanded = expandUnaryOperations(relativeReferencesReplaced)
        val namesReplaced = replaceNamesWithCellReferences(unaryMatrixesExpanded)
        namesReplaced.forEach { (name, expr) ->
            assert(looksLikeCellName(name) || expr !is SheetRangeExpr) {
                "Should have eliminated $name"
            }
        }
        return Sheet.of(columnDescriptors, rowDescriptors, sheetDescriptor, namesReplaced)
    }

    internal fun add(vararg add: NamedExpr) {
        added += add
    }

    fun set(cell : Coordinate, value : Any, type : KaneType<*>) {
        val name = CellRange.moveable(cell).toString()
        add(
            if (value is Double) NamedScalar(name, constant(value, type as AlgebraicType))
            else NamedValueExpr(name, value, type as KaneType<Any>)
        )
    }

    fun column(index: Int, name: String, type: AdmissibleDataType<*>? = null) {
        columnDescriptors[index] = ColumnDescriptor(name, type)
    }

    override fun nameColumn(column: Int, name: String) {
        columnDescriptors[column] = ColumnDescriptor(name, null)
    }

    override fun nameRow(row: Int, name: String) {
        rowDescriptors[row] = RowDescriptor(name)
    }
}



private fun Sheet.variable(cell : String) : NamedScalarVariable {
    val reduced = when(val value = cells.getValue(cell)) {
        is AlgebraicExpr -> value.eval()
        else ->
            error("${value.javaClass}")
    }
    return when (reduced) {
        is ConstantScalar -> NamedScalarVariable(cell, reduced.value, reduced.type)
        is ScalarVariable -> NamedScalarVariable(cell, reduced.initial, reduced.type)
        else -> error("$cell (${cells.getValue(cell)}) is not a constant")
    }
}

private fun Sheet.replaceNamesWithVariables(variables: Map<String, NamedScalarVariable>): Sheet {
    return object : RewritingVisitor() {
        override fun rewrite(expr: NamedScalar): Expr {
            return variables[expr.name] ?: super.rewrite(expr)
        }
    }.rewrite(this) as Sheet
}

fun Sheet.minimize(
    target: String,
    variables: List<String>,
    learningRate: Double = 0.001
): Sheet {
    val resolvedVariables = mutableMapOf<String, NamedScalarVariable>()

    // Turn each 'variable' into a NamedScalarVariable
    variables.forEach { cell ->
        resolvedVariables[cell] = variable(cell)
    }

    // Replace names with variables
    val namesReplaced = replaceNamesWithVariables(resolvedVariables)

    // Follow all expressions from 'target'
    val reducedArithmetic = namesReplaced.eval(
        reduceVariables = true,
        excludeVariables = variables.toSet()
    )
    //val reducedArithmetic = reduceArithmetic(variables.toSet())
    println("Expanding target expression '$target'")
    val lookup = reducedArithmetic.cells + resolvedVariables
    val targetExpr = reducedArithmetic.cells.getValue(target).expandNamedCells(lookup)
    if (targetExpr !is ScalarExpr) {
        error("'$target' was not a numeric expression")
    }
    val reduced = targetExpr.eval()
    val namedTarget: NamedScalarExpr = NamedScalar(target, reduced)

    // Differentiate target with respect to each variable
    println("Differentiating target expression $target with respect to change variables: ${variables.joinToString(" ")}")
    val diffs = resolvedVariables.map { (_, variable) ->
        val name = "d$target/d${variable.name}"
        val derivative = differentiate(d(namedTarget) / d(variable)).eval()
        NamedScalar(name, derivative)
    }

    // Assign back variables updated by a delta of their respective derivative
    val assignBacks = (resolvedVariables.values zip diffs).map { (variable, diff) ->
        NamedScalarAssign(
            "update(${variable.name})",
            variable,
            variable - multiply(learningRate, diff)
        ).eval()
    }

    // Create the model, allocate space for it, and iterate. Break when the target didn't move much
    val model = Tableau(resolvedVariables.values + diffs + namedTarget + assignBacks, diffs[0].type).linearize()
    val space = model.allocateSpace()
    println("Plan has ${model.slotCount()} common sub-expressions and uses ${space.size * 8} bytes")
    model.eval(space)
    var priorTarget: Double = Double.MAX_VALUE
    val originalTarget: Double = model.shape(namedTarget).ref(space).value
    println("Minimizing")
    for (i in 0 until 100000) {
//        val sb = StringBuilder()
//        sb.append(resolvedVariables.values.joinToString(" ") {
//            val value = model.shape(it).ref(space)
//            "${it.name}: $value"
//        })
//        println(sb)
        model.eval(space)
        val newTarget = model.shape(namedTarget).ref(space).value
        if (abs(newTarget - priorTarget) < 0.00000000000001)
            break
        priorTarget = newTarget
    }

    println(
        "Target '$target' reduced from ${DoubleAlgebraicType.kaneType.render(originalTarget)} to ${
            DoubleAlgebraicType.kaneType.render(
                priorTarget
            )
        }"
    )

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
            val cell = CellRange.moveable(column, row).toString()
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
            val cell = CellRange.moveable(column, row).toString()
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
            if (it.second !is SheetRangeExpr) {
                sb.append("\n${it.first}=${it.second}")
            }
        }
    }
    return "$sb"
}

// Construction
fun sheetOf(init: SheetBuilder.() -> List<NamedExpr>): Sheet {
    val builder = SheetBuilderImpl()
    init(builder as SheetBuilder).forEach { builder.add(it) }
    return builder.build()
}

