@file:Suppress("UNCHECKED_CAST")
package com.github.jomof.kane.impl.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.impl.*
import com.github.jomof.kane.impl.types.KaneType
import com.github.jomof.kane.impl.visitor.RewritingVisitor
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

data class SheetRangeExpr(val rangeRef: SheetRangeRef) :
    SheetRange {
    override fun up(move: Int) = copy(rangeRef = rangeRef.up(move))
    override fun down(move: Int) = copy(rangeRef = rangeRef.down(move))
    override fun left(move: Int) = copy(rangeRef = rangeRef.left(move))
    override fun right(move: Int) = copy(rangeRef = rangeRef.right(move))
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toString() = render()
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
data class CoerceMatrix(
    val value: Expr
) : MatrixExpr {
    init {
        assert(value !is MatrixExpr)
        assert(value !is ScalarListExpr) {
            "scalarlistexpr"
        }
        assert(value !is SheetRangeExpr || value.rangeRef !is CellRangeRef) {
            "scalarlistexpr"
        }
    }

    override fun toString() = render()
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
    val showExcelColumnTags: Boolean = true,
    val showRowAndColumnForSingleCell: Boolean = false
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
    val keys get() = map.keys
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

data class NamedSheet(
    override val name: String,
    val sheet: Sheet
) : NamedExpr

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
        internal fun of(
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
            val coordinateCells = cells
                .keys
                .filterIsInstance<Coordinate>()

            val columnsFromCells: Int = coordinateCells.map { it.column }.maxOrNull() ?: 0
            val columnsColumnDescriptors: Int = columnDescriptors.map { it.key }.maxOrNull() ?: 0
            val rowsIndexes = coordinateCells.map { it.row }
            val rows = rowsIndexes.maxOrNull() ?: 0
            return SheetImpl(
                columnDescriptors,
                rowDescriptors,
                cells,
                sheetDescriptor,
                max(columnsFromCells, columnsColumnDescriptors) + 1,
                rows + 1
            )
        }
    }

    operator fun getValue(nothing: Nothing?, property: KProperty<*>) = NamedSheet(property.name, this)

    fun copy(
        columnDescriptors: Map<Int, ColumnDescriptor> = this.columnDescriptors,
        rowDescriptors: Map<Int, RowDescriptor> = this.rowDescriptors,
        cells: Cells = this.cells,
        sheetDescriptor: SheetDescriptor = this.sheetDescriptor
    ) = of(columnDescriptors, rowDescriptors, sheetDescriptor, cells)

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

    /**
     * Create a new sheet with cell values changed.
     */
    fun copy(init: SheetBuilderImpl.() -> List<NamedExpr>): Sheet {
        val sheet = toBuilder()
        init(sheet).forEach { sheet.add(it) }
        return sheet.build()
    }
}

/**
 * Limit the number of output lines when rendering the sheet
 */
internal fun Sheet.limitOutputLines(limit: Int) = copy(sheetDescriptor = sheetDescriptor.copy(limitOutputLines = limit))

/**
 * Whether to show Excel-like column tags in the column headers
 */
internal fun Sheet.showExcelColumnTags(show: Boolean = true) =
    copy(sheetDescriptor = sheetDescriptor.copy(showExcelColumnTags = show))

internal fun GroupBy.showExcelColumnTags(show: Boolean = true) = copy(sheet = sheet.showExcelColumnTags(show))

/**
 * Whether to show Excel-like column tags in the column headers
 */
internal fun Sheet.showRowAndColumnForSingleCell(show: Boolean = true) =
    copy(sheetDescriptor = sheetDescriptor.copy(showRowAndColumnForSingleCell = show))


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
    added: List<NamedExpr> = listOf()
) : SheetBuilder {
    internal val columnDescriptors: MutableMap<Int, ColumnDescriptor> = columnDescriptors.toMutableMap()
    private val rowDescriptors: MutableMap<Int, RowDescriptor> = rowDescriptors.toMutableMap()
    private val added: MutableList<NamedExpr> = added.toMutableList()

    fun getImmediateNamedExprs(): Sheet {
        val cells = mutableMapOf<Id, Expr>()

        // Add immediate named expressions
        added.forEach { namedExpr ->
            cells[Identifier.normalizeUserInput(namedExpr.name)] = namedExpr.toUnnamed()
        }

        return Sheet.of(columnDescriptors, rowDescriptors, sheetDescriptor, cells.toCells())
    }

    fun build(): Sheet {
        val immediate = getImmediateNamedExprs()
        return immediate.build()
    }

    internal fun add(vararg add: NamedExpr) {
        added += add
    }

    fun set(cell: Id, value: Any, type: KaneType<*>) {
        add(
            if (value is Double) NamedScalar(cell, constant(value))
            else NamedValueExpr(cell, value, type as KaneType<Any>)
        )
    }

    fun column(index: Int, name: Id, type: AdmissibleDataType<*>? = null) {
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
        namedColumnOrNull is String && sheetDescriptor.showExcelColumnTags -> "$namedColumnOrNull [$excelColumnName]"
        else -> Identifier.string(namedColumnOrNull)
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
            sb.append("\n${it.first}=${it.second}")
        }
    }
    return "$sb"
}



