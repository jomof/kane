@file:Suppress("UNCHECKED_CAST")
package com.github.jomof.kane.impl.sheet

import com.github.jomof.kane.eval
import com.github.jomof.kane.impl.*
import com.github.jomof.kane.impl.functions.AlgebraicBinaryRangeStatistic
import com.github.jomof.kane.impl.types.KaneType
import com.github.jomof.kane.impl.visitor.RewritingVisitor
import com.github.jomof.kane.impl.visitor.visit
import java.lang.Integer.max
import kotlin.math.min
import kotlin.reflect.KProperty

interface SheetRange : Expr {
    fun up(move: Int): SheetRange
    fun down(move: Int): SheetRange
    fun left(move: Int): SheetRange
    fun right(move: Int): SheetRange
    val up get() = up(1)
    val down get() = down(1)
    val left get() = left(1)
    val right get() = right(1)
}

data class SheetRangeExpr(val rangeRef: SheetRangeRef) :
    SheetRange {
    private val name by lazy { rangeRef.toString() }
    override fun up(move: Int) = copy(rangeRef = rangeRef.up(move))
    override fun down(move: Int) = copy(rangeRef = rangeRef.down(move))
    override fun left(move: Int) = copy(rangeRef = rangeRef.left(move))
    override fun right(move: Int) = copy(rangeRef = rangeRef.right(move))
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toString() = name
}

data class NamedSheetRangeExpr(
    override val name: Id,
    val range: SheetRange
) : SheetRange, NamedExpr {

    override fun toString() = render()

    override fun up(move: Int) = range.up(move)
    override fun down(move: Int) = range.down(move)
    override fun left(move: Int) = range.left(move)
    override fun right(move: Int) = range.right(move)
}

/**
 * Turn an expression into a ScalarExpr based on context.
 */
data class CoerceScalar(
    val value: Expr
) : ScalarExpr {
    init {
        assert(value !is ScalarExpr) {
            "coerce of scalar?"
        }
        assert(value !is CoerceScalar) {
            "coerce of coerce?"
        }
        track()
    }

    override fun toString() = render()
}

data class ColumnDescriptor(val name: Id, val type: AdmissibleDataType<*>?)
data class RowDescriptor(val name: List<Expr>)
data class SheetDescriptor(
    val limitOutputLines: Int = Int.MAX_VALUE,
    val showColumnTags: Boolean = true
)

data class Cells(private val map: Map<Id, Expr>) {
    init {
        map.forEach { (id, _) -> Identifier.validate(id) }
    }

    fun <R : Any> mapNotNull(transform: (Map.Entry<Id, Expr>) -> R?): List<R> {
        return map.mapNotNullTo(ArrayList<R>(), transform)
    }

    fun <R : Any> map(transform: (Map.Entry<Id, Expr>) -> R): List<R> {
        return map.map(transform)
    }

    fun filter(predicate: (Map.Entry<Id, Expr>) -> Boolean) = map.filter(predicate).toCells()
    fun mapExprs(transform: (Id, Expr) -> Expr): Cells {
        val mutable = toMutableMap()
        map.forEach { (id, expr) ->
            mutable[id] = transform(id, expr)
        }
        return mutable.toCells()
    }

    fun forEach(action: (Map.Entry<Id, Expr>) -> Unit) {
        return map.forEach(action)
    }

    fun toMutableMap() = map.toMutableMap()
    fun toMap() = map
    operator fun iterator() = map.iterator()
    fun isEmpty() = map.isEmpty()
    fun isNotEmpty() = map.isNotEmpty()
    fun getValue(key: Id) = get(key) ?: error("No cell named '$key' in cell map")
    operator fun get(key: Id): Expr? {
        Identifier.validate(key)
        return map[key]
    }

    infix operator fun <E : Expr> plus(right: Map<Id, E>): Cells = (toMap() + right).toCells()
}

fun List<Pair<Id, Expr>>.toCells() = Cells(toMap())
fun Map<Id, Expr>.toCells() = Cells(this)

interface Sheet : Expr {
    val columnDescriptors: Map<Int, ColumnDescriptor>
    val rowDescriptors: Map<Int, RowDescriptor>
    val cells: Cells
    val sheetDescriptor: SheetDescriptor
    val columns: Int
    val rows: Int

    companion object {
        private data class SheetImpl(
            override val columnDescriptors: Map<Int, ColumnDescriptor>,
            override val rowDescriptors: Map<Int, RowDescriptor>,
            override val cells: Cells,
            override val sheetDescriptor: SheetDescriptor,
            override val columns: Int,
            override val rows: Int
        ) : Sheet {
            override fun toString() = render()
        }

        private val emptySheet = SheetImpl(mapOf(), mapOf(), Cells(mapOf()), SheetDescriptor(), 0, 0)
        fun of(
            columnDescriptors: Map<Int, ColumnDescriptor>,
            rowDescriptors: Map<Int, RowDescriptor>,
            sheetDescriptor: SheetDescriptor,
            cells: Cells
        ): Sheet {
            if (cells.isEmpty() &&
                columnDescriptors.isEmpty() &&
                rowDescriptors.isEmpty() &&
                sheetDescriptor == SheetDescriptor()
            ) return emptySheet
            if (cells.isEmpty()) return SheetImpl(columnDescriptors, rowDescriptors, cells, sheetDescriptor, 0, 0)
            val columnsFromCells: Int = cells
                .toMap()
                .filter { it.key is Coordinate }
                .map { Identifier.column(it.key) }.maxOrNull()!! + 1
            val columnsColumnDescriptors: Int = columnDescriptors
                .map { it.key }.maxOrNull()?:0 + 1
            val rows : Int = cells
                .toMap()
                .filter { it.key is Coordinate }
                .map { Identifier.row(it.key) }.maxOrNull()!! + 1
            return SheetImpl(columnDescriptors, rowDescriptors, cells, sheetDescriptor, max(columnsFromCells, columnsColumnDescriptors), rows)
        }
    }

    fun copy(
        columnDescriptors: Map<Int, ColumnDescriptor> = this.columnDescriptors,
        rowDescriptors: Map<Int, RowDescriptor> = this.rowDescriptors,
        cells: Cells = this.cells,
        sheetDescriptor: SheetDescriptor = this.sheetDescriptor
    ) = of(columnDescriptors, rowDescriptors, sheetDescriptor, cells)

    /**
     * Limit the number of output lines when rendering the sheet
     */
    fun limitOutputLines(limit: Int) = copy(sheetDescriptor = sheetDescriptor.copy(limitOutputLines = limit))

    /**
     * Whether to show Excel-like column tags in the column headers
     */
    fun showExcelColumnTags(show: Boolean = true) = copy(sheetDescriptor = sheetDescriptor.copy(showColumnTags = show))

    /**
     * Create a new sheet with cell values changed.
     */
    fun copy(vararg assignments: Pair<String, Any>): Sheet {
        val sb = toBuilder()
        assignments.forEach { (name, any) ->
            sb.add(convertAnyToNamedExpr(Identifier.normalizeUserInput(name), any))
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
    private val rowDescriptors: MutableMap<Int, RowDescriptor> = rowDescriptors.toMutableMap()
    private val added: MutableList<NamedExpr> = added.toMutableList()

    override fun column(range: String): SheetBuilderRange {
        val parsed = parseRange(range)
        assert(parsed is ColumnRangeRef || parsed is NamedColumnRangeRef)
        return SheetBuilderRange(this, parsed)
    }

    fun getImmediateNamedExprs(): Cells {
        val cells = mutableMapOf<Id, Expr>()

        // Add immediate named expressions
        added.forEach { namedExpr ->
            cells[Identifier.normalizeUserInput(namedExpr.name)] = namedExpr.toUnnamed()
        }

        return cells.toCells()
    }

    private fun getEmbeddedNamedExprs(cells: Cells): Cells {
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
        return result.toCells()
    }

    private fun replaceRelativeReferences(cells: Cells): Cells {
        return cells
            .map { (name, expr) ->
                val base = if (name is Coordinate) name else coordinate(0, 0)
                name to expr.replaceRelativeCellReferences(base)
            }
            .toCells()
    }

    private fun convertCellNamesToUpperCase(cells: Cells): Cells {
        return cells
            .map { (name, expr) ->
                val upper = Identifier.string(name).toUpperCase()
                val new = if (looksLikeCellName(upper)) cellNameToCoordinate(upper) else name
                new to expr.convertCellNamesToUpperCase()
            }
            .map { it.first to it.second }
            .toCells()
    }

    private fun scalarizeMatrixes(cells: Cells): Cells {
        val result = cells.toMutableMap()
        val namedColumns = mutableMapOf<String, Int>()

        // First, assign all named matrixes to a column or set of columns
        result.toList().forEach { (name, expr) ->
            if (name !is Coordinate) {
                when (expr) {
                    is MatrixExpr,
                    is Tiling<*> -> {
                        val cellsCoordinates = result.map { it.key }.filterIsInstance<Coordinate>()
                        val columns = cellsCoordinates.map { it.column }.toSet()
                        val existingColumns = (columnDescriptors.keys + columns).sorted()
                        for (next in 0 until Int.MAX_VALUE) {
                            if (!existingColumns.contains(next)) {
                                columnDescriptors[next] = ColumnDescriptor(name, null)
                                namedColumns[Identifier.string(name)] = next
                                val allocatedColumnBase = coordinate(next, 0)
                                result[allocatedColumnBase] = expr
                                result.remove(name)
                                break
                            }
                        }
                    }
                }
            }
        }

        // Then, expand matrix elements
        result.toList().forEach { (name, expr) ->
            if (name is Coordinate) {
                when (expr) {
                    is ScalarExpr,
                    is ValueExpr<*>,
                    is SheetRangeExpr,
                    is AlgebraicBinaryRangeStatistic -> {
                    }
                    is MatrixExpr -> {
                        var replacedOurName = false
                        expr.coordinates.map { offset ->
                            val finalCoordinate = name + offset
                            val extracted = extractScalarizedMatrixElement(expr, offset, namedColumns)
                            val slided = extracted.slideScalarizedCellsRewritingVisitor(name, offset)
                            if (finalCoordinate == name) {
                                replacedOurName = true
                            }
                            result[finalCoordinate] = slided
                        }
                        assert(replacedOurName) {
                            "Should have replaced our own name in $name"
                        }
                    }
                    is Tiling<*> -> {
                        coordinatesOf(expr.columns, expr.rows).map { offset ->
                            val finalCoordinate = name + offset
                            result[finalCoordinate] = expr.getUnnamedElement(offset)
                        }
                    }
                    else -> error("${expr.javaClass}")
                }
            }
        }

        return result.toCells()
    }

    private fun expandUnaryOperations(cells: Cells) = cells
        .mapExprs { _, expr ->
            expr.expandUnaryOperations()
        }

    private fun replaceNamesWithCellReferences(cells: Cells) = cells
        .mapExprs { name, expr ->
            expr.replaceNamesWithCellReferences(name)
        }

    fun build(): Sheet {
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
            assert(name is Coordinate || expr !is SheetRangeExpr) {
                "Should have eliminated $name"
            }
        }
        return Sheet.of(columnDescriptors, rowDescriptors, sheetDescriptor, namesReplaced)
    }

    internal fun add(vararg add: NamedExpr) {
        added += add
    }

    fun set(cell : Coordinate, value : Any, type : KaneType<*>) {
        add(
            if (value is Double) NamedScalar(cell, constant(value))
            else NamedValueExpr(cell, value, type as KaneType<Any>)
        )
    }

    fun column(index: Int, name: String, type: AdmissibleDataType<*>? = null) {
        columnDescriptors[index] = ColumnDescriptor(name, type)
    }

    override fun nameColumn(column: Int, name: Id) {
        columnDescriptors[column] = ColumnDescriptor(name, null)
    }

    override fun nameRow(row: Int, name: Id) {
        rowDescriptors[row] = RowDescriptor(listOf(constant(Identifier.string(name))))
    }

    override fun nameRow(row: Int, name: List<Expr>) {
        rowDescriptors[row] = RowDescriptor(name)
    }
}


internal fun Sheet.namedVariableOf(cell: Id): NamedScalarVariable {
    val reduced = when (val value = cells.getValue(cell)) {
        is AlgebraicExpr -> value.eval()
        else ->
            error("${value.javaClass}")
    }
    return when (reduced) {
        is ConstantScalar -> NamedScalarVariable(cell, reduced.value)
        is ScalarVariable -> NamedScalarVariable(cell, reduced.initial)
        else -> error("$cell (${cells.getValue(cell)}) is not a constant")
    }
}

internal fun Sheet.replaceNamesWithVariables(variables: Map<Id, NamedScalarVariable>): Sheet {
    return object : RewritingVisitor() {
        override fun rewrite(expr: NamedScalar): Expr {
            return variables[expr.name] ?: super.rewrite(expr)
        }
    }.rewrite(this) as Sheet
}

fun Sheet.columnName(column: Int): String {
    val namedColumnOrNull = columnDescriptors[column]?.name
    val excelColumnName = indexToColumnName(column)
    return when {
        namedColumnOrNull == null -> excelColumnName
        namedColumnOrNull is String && namedColumnOrNull.toUpperCase() == excelColumnName -> namedColumnOrNull
        namedColumnOrNull is String && sheetDescriptor.showColumnTags -> "$namedColumnOrNull [$excelColumnName]"
        else -> Identifier.string(namedColumnOrNull)
    }
}

fun Sheet.rowName(row: Int) = rowDescriptors[row]?.name?.joinToString(" ") ?: "$row"

fun Sheet.render(): String {
    if (cells.isEmpty()) return ""
    val sb = StringBuilder()
    val widths = Array(columns + 1) {
        if (it > 0) {
            val name = columnDescriptors[it - 1]?.name
            if (name == null) 0
            else Identifier.width(name)
        } else 0
    }

    // Effective row count for formatting
    val rows = min(rows, sheetDescriptor.limitOutputLines)

    // Calculate width of row number column
    for(row in 0 until rows + 1) {
        widths[0] = max(widths[0], rowName(row).length)
    }

    // Calculate widths of column headers
    for(column in 0 until columns) {
        widths[column + 1] = max(widths[column + 1], columnName(column).length)
    }

    // Calculate widths
    for(row in 0 until rows) {
        for(column in 0 until columns) {
            val cell = coordinate(column, row)
            val value = cells[cell]?.toString() ?: ""
            widths[column + 1] = max(widths[column + 1], value.length)
        }
    }

    // Column headers
    widths.indices.forEach { index ->
        val width = widths[index]
        if (index == 0) sb.append(" ".repeat(width) + " ")
        else {
            val columnName = columnName(index - 1)
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
            val cell = coordinate(column, row)
            val value = cells[cell]?.toString() ?: ""
            sb.append(padLeft(value, widths[column + 1]) + " ")
        }
        if (row != rows - 1) sb.append("\n")
    }
    if (this.rows > rows) {
        sb.append("\n...and ${this.rows-rows} more rows")
    }

    // Non-cell data
    val nonCells = cells.toMap().filter { !(it.key is Coordinate) }
    if (nonCells.isNotEmpty()) {
        nonCells.toList().sortedBy { Identifier.string(it.first) }.forEach {
            if (it.second !is SheetRangeExpr) {
                sb.append("\n${it.first}=${it.second}")
            }
        }
    }
    return "$sb"
}



