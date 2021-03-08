@file:Suppress("UNCHECKED_CAST")
package com.github.jomof.kane.impl.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.api.Row
import com.github.jomof.kane.api.RowDescriptor
import com.github.jomof.kane.api.SheetDescriptor
import com.github.jomof.kane.impl.*
import com.github.jomof.kane.impl.types.KaneType
import com.github.jomof.kane.impl.visitor.SheetRewritingVisitor
import java.lang.Integer.max
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set
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

data class SheetRangeExpr(
    val rangeRef: SheetRangeRef,
    override val name: Id = anonymous
) : SheetRange, INameable {
    init {
        assert(rangeRef !is CellRangeRef)
    }

    override fun up(move: Int) = copy(rangeRef = rangeRef.up(move))
    override fun down(move: Int) = copy(rangeRef = rangeRef.down(move))
    override fun left(move: Int) = copy(rangeRef = rangeRef.left(move))
    override fun right(move: Int) = copy(rangeRef = rangeRef.right(move))
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toNamed(name: Id) = dup(name = name)
    override fun toUnnamed() = dup(name = anonymous)
    override fun toString() = render()

    fun dup(rangeRef: SheetRangeRef = this.rangeRef, name: Id = this.name): SheetRangeExpr {
        if (rangeRef === this.rangeRef && name == this.name) return this
        return copy(rangeRef = rangeRef, name = name)
    }
}

data class CellSheetRangeExpr(val rangeRef: CellRangeRef, override val name: Id = anonymous) :
    SheetRange, ScalarExpr, INameableScalar {
    override fun up(move: Int) = copy(rangeRef = rangeRef.up(move))
    override fun down(move: Int) = copy(rangeRef = rangeRef.down(move))
    override fun left(move: Int) = copy(rangeRef = rangeRef.left(move))
    override fun right(move: Int) = copy(rangeRef = rangeRef.right(move))
    override fun toString() = render()

    override fun toNamed(name: Id): ScalarExpr {
        if (this.name === anonymous) return dup(name = name)
        return NamedScalar(name, this)
    }

    override fun toUnnamed() = dup(name = anonymous)

    fun dup(rangeRef: CellRangeRef = this.rangeRef, name: Id = this.name): CellSheetRangeExpr {
        if (rangeRef === this.rangeRef && name == this.name) return this
        return copy(rangeRef = rangeRef, name = name)
    }
}

data class ExogenousSheetScalar(
    val lookup: Id,
    val rowSequence: Sequence<Row>,
    override val name: Id = anonymous
) : ScalarExpr, INameableScalar {
    override fun toNamed(name: Id): ScalarExpr {
        if (this.name === anonymous) return dup(name = name)
        return NamedScalar(name, this)
    }

    override fun toUnnamed() = dup(name = anonymous)

    override fun toString() = rowSequence.toSheet().cells[lookup].toString()
    fun dup(name: Id = this.name): ExogenousSheetScalar {
        if (name == this.name) return this
        return ExogenousSheetScalar(lookup = lookup, rowSequence = rowSequence, name = name)
    }
}

/**
 * Turn an expression into a ScalarExpr based on context.
 */
data class CoerceMatrix(
    val value: Expr
) : MatrixExpr {
    init {
        assert(value !is MatrixExpr)
        assert(value !is SheetRangeExpr || value.rangeRef !is CellRangeRef)
    }

    override fun toString() = render()

    fun dup(value: Expr = this.value): CoerceMatrix {
        if (value === this.value) return this
        return CoerceMatrix(value)
    }
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
    fun dup(value: Expr = this.value): CoerceScalar {
        if (value === this.value) return this
        return CoerceScalar(value)
    }
}

data class ColumnDescriptor(val name: String, val type: AdmissibleDataType<*>?)

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

    fun forEach(action: (Map.Entry<Id, Expr>) -> Unit) {
        return map.forEach(action)
    }

    fun toMutableMap() = map.toMutableMap()
    fun toMap() = map
    val keys get() = map.keys
    val values get() = map.values
    operator fun iterator() = map.iterator()
    fun isEmpty() = map.isEmpty()
    fun isNotEmpty() = map.isNotEmpty()
    fun count() = map.count()
    fun firstValue() = map.entries.first().value
    fun getValue(key: Id) = get(key) ?: error("No cell named '$key' in cell map")
    operator fun get(key: Id): Expr? {
        Identifier.validate(key)
        return map[key]
    }

    infix operator fun <E : Expr> plus(right: Map<Id, E>): Cells = (toMap() + right).toCells()
}

fun List<Pair<Id, Expr>>.toCells() = Cells(toMap())
fun Map<Id, Expr>.toCells() = Cells(this)

data class Sheet(
    override val columnDescriptors: Map<Int, ColumnDescriptor>,
    val rowDescriptors: Map<Int, RowDescriptor>,
    val cells: Cells,
    val sheetDescriptor: SheetDescriptor,
    override val columns: Int,
    override val rows: Int,
    override val name: Id = anonymous
) : Expr,
    INameable,
    Sequence<Row>,
    CountableColumns,
    CountableRows,
    ProvidesToSheet,
    ProvidesColumnDescriptors {
    override fun toNamed(name: Id) = dup(name = name)
    override fun toUnnamed() = dup(name = anonymous)
    override fun toSheet() = this
    override fun toString() = render()

    companion object {

        val empty = Sheet(mapOf(), mapOf(), Cells(mapOf()), SheetDescriptor(), 0, 0)
        internal fun create(
            columnDescriptors: Map<Int, ColumnDescriptor> = mapOf(),
            rowDescriptors: Map<Int, RowDescriptor> = mapOf(),
            sheetDescriptor: SheetDescriptor = SheetDescriptor(),
            cells: Cells,
            name: Id = anonymous
        ): Sheet {
            if (cells.isEmpty() &&
                columnDescriptors.isEmpty() &&
                rowDescriptors.isEmpty() &&
                sheetDescriptor == SheetDescriptor()
            ) return empty
            if (cells.isEmpty()) return Sheet(columnDescriptors, rowDescriptors, cells, sheetDescriptor, 0, 0)
            val coordinateCells = cells
                .keys
                .filterIsInstance<Coordinate>()

            val columnsFromCells: Int = coordinateCells.map { it.column }.maxOrNull() ?: 0
            val columnsColumnDescriptors: Int = columnDescriptors.map { it.key }.maxOrNull() ?: 0
            val rowsIndexes = coordinateCells.map { it.row }
            val rows = rowsIndexes.maxOrNull() ?: 0
            return Sheet(
                columnDescriptors,
                rowDescriptors,
                cells,
                sheetDescriptor,
                max(columnsFromCells, columnsColumnDescriptors) + 1,
                rows + 1,
                name
            )
        }
    }

    operator fun getValue(nothing: Nothing?, property: KProperty<*>) = toNamed(property.name)

    fun copy(
        columnDescriptors: Map<Int, ColumnDescriptor> = this.columnDescriptors,
        rowDescriptors: Map<Int, RowDescriptor> = this.rowDescriptors,
        cells: Cells = this.cells,
        sheetDescriptor: SheetDescriptor = this.sheetDescriptor,
        name: Id = this.name
    ) {
        error("Use dup")
    }

    fun dup(
        columnDescriptors: Map<Int, ColumnDescriptor> = this.columnDescriptors,
        rowDescriptors: Map<Int, RowDescriptor> = this.rowDescriptors,
        cells: Cells = this.cells,
        sheetDescriptor: SheetDescriptor = this.sheetDescriptor,
        name: Id = this.name
    ): Sheet {
        if (columnDescriptors === this.columnDescriptors &&
            rowDescriptors === this.rowDescriptors &&
            cells === this.cells &&
            sheetDescriptor === this.sheetDescriptor &&
            name === this.name
        ) return this
        return create(columnDescriptors, rowDescriptors, sheetDescriptor, cells, name)
    }

    /**
     * Create a new sheet with cell values changed.
     */
    fun copy(vararg assignments: Pair<Id, Any>): Sheet {
        val sb = toBuilder()
        assignments.forEach { (name, any) ->
            sb.add(convertAnyToNamedExpr(Identifier.normalizeUserInput(name), any))
        }
        return sb.build()
    }

    override fun iterator() = object : Iterator<Row> {
        val sheet: Sheet = this@Sheet
        var row = 0
        override fun hasNext() = row < rows
        override fun next() = RowView(sheet, row++)
    }
}

/**
 * Limit the number of output lines when rendering the sheet
 */
internal fun Sheet.limitOutputLines(limit: Int) = dup(sheetDescriptor = sheetDescriptor.copy(limitOutputLines = limit))

/**
 * Whether to show Excel-like column tags in the column headers
 */
internal fun Sequence<Row>.showExcelColumnTags(show: Boolean = true) =
    toSheet().dup(sheetDescriptor = toSheet().sheetDescriptor.copy(showExcelColumnTags = show))

internal fun GroupBy.showExcelColumnTags(show: Boolean = true) =
    copy(source = source.toSheet().showExcelColumnTags(show))

/**
 * Whether to show Excel-like column tags in the column headers
 */
internal fun Sheet.showRowAndColumnForSingleCell(show: Boolean = true) =
    dup(sheetDescriptor = sheetDescriptor.copy(showRowAndColumnForSingleCell = show))


internal fun Sheet.toBuilder(): SheetBuilderImpl {
    val named = cells.map { (name, expr) -> expr.toNamed(name) }
    return SheetBuilderImpl(
        columnDescriptors = columnDescriptors,
        rowDescriptors = rowDescriptors,
        sheetDescriptor = sheetDescriptor,
        added = named
    )
}

class SheetBuilderImpl(
    columnDescriptors: Map<Int, ColumnDescriptor> = mapOf(),
    rowDescriptors: Map<Int, RowDescriptor> = mapOf(),
    val sheetDescriptor: SheetDescriptor = SheetDescriptor(),
    added: List<Expr> = listOf()
) : SheetBuilder {
    internal val columnDescriptors: MutableMap<Int, ColumnDescriptor> = columnDescriptors.toMutableMap()
    private val rowDescriptors: MutableMap<Int, RowDescriptor> = rowDescriptors.toMutableMap()
    private val added: MutableList<Expr> = added.toMutableList()

    fun getImmediateNamedExprs(): Sheet {
        val cells = mutableMapOf<Id, Expr>()

        // Add immediate named expressions
        added.forEach { namedExpr ->
            cells[Identifier.normalizeUserInput(namedExpr.name)] = namedExpr.toUnnamed()
        }

        return Sheet.create(columnDescriptors, rowDescriptors, sheetDescriptor, cells.toCells(), anonymous)
    }

    fun build(): Sheet {
        val immediate = getImmediateNamedExprs()
        return immediate.build()
    }

    internal fun add(vararg add: Expr) {
        add.forEach { expr ->
            assert(expr.hasName()) {
                "Expected named"
            }
        }
        added += add
    }

    fun set(cell: Id, value: Any, type: KaneType<*>) {
        add(
            if (value is Double) constant(value).toNamed(cell)
            else ValueExpr(value, type as KaneType<Any>, cell)
        )
    }

    fun column(index: Int, name: String, type: AdmissibleDataType<*>? = null) {
        columnDescriptors[index] = ColumnDescriptor(name, type)
    }

    override fun nameColumn(column: Int, name: String) {
        columnDescriptors[column] = ColumnDescriptor(name, null)
    }

    override fun nameRow(row: Int, name: String) {
        rowDescriptors[row] = RowDescriptor(listOf(name))
    }

    override fun nameRow(row: Int, name: List<String>) {
        rowDescriptors[row] = RowDescriptor(name)
    }
}


internal fun Sheet.namedVariableOf(cell: Id): ScalarVariable {
    val reduced = when (val value = cells.getValue(cell)) {
        is AlgebraicExpr -> value.eval()
        else ->
            error("${value.javaClass}")
    }
    return when (reduced) {
        is ConstantScalar -> ScalarVariable(reduced.value, cell)
        is ScalarVariable -> ScalarVariable(reduced.initial, cell)
        else -> error("$cell (${cells.getValue(cell)}) is not a constant")
    }
}

internal fun Sheet.replaceNamesWithVariables(variables: Map<Id, ScalarVariable>): Sheet {
    return object : SheetRewritingVisitor() {
        override fun cell(name: Id, expr: Expr): Pair<Id, Expr> {
            val variable = variables[name]
            if (variable != null) return name to variable
            return super.cell(name, expr)
        }

        override fun rewrite(expr: Expr): Expr {
            if (!expr.hasName()) return super.rewrite(expr)
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
        namedColumnOrNull is String && sheetDescriptor.showExcelColumnTags -> "$namedColumnOrNull [$excelColumnName]"
        else -> namedColumnOrNull
    }
}

fun Sheet.rowName(row: Int) = rowDescriptors[row]?.name?.joinToString(" ") ?: "$row"

fun Sheet.render(): String {
    if (cells.isEmpty()) return ""
    if (cells.count() == 1 && !sheetDescriptor.showRowAndColumnForSingleCell)
        return cells.firstValue().toString()
    val sb = StringBuilder()
    val widths = Array(columns + 1) {
        if (it > 0) {
            val name = columnDescriptors[it - 1]?.name
            name?.length ?: 0
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
            sb.append("\n${it.first}=${it.second}")
        }
    }
    return "$sb"
}



