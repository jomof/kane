package com.github.jomof.kane.impl.visitor

import com.github.jomof.kane.Expr
import com.github.jomof.kane.impl.*
import com.github.jomof.kane.impl.sheet.*
import kotlin.math.max

internal open class SheetRewritingVisitor(
    allowNameChange: Boolean = false,
    checkIdentity: Boolean = false
) : RewritingVisitor(allowNameChange, checkIdentity) {
    private val columnDescriptorStack = mutableListOf<MutableMap<Int, ColumnDescriptor>>()
    private val cellsStack = mutableListOf<Lazy<MutableMap<Id, Expr>>>()
    private val columnDescriptors: MutableMap<Int, ColumnDescriptor> get() = columnDescriptorStack[0]
    protected val cells: MutableMap<Id, Expr> get() = cellsStack[0].value
    protected fun inSheet() = cellsStack.isNotEmpty()
    protected fun columnNameExists(name: Id) = columnDescriptors.any { it.value.name == name }
    protected fun columnIndexOfName(name: Id): Int {
        if (name is Coordinate) return name.column
        val byColumnDescriptor = columnDescriptors.toList().firstOrNull { it.second.name == name }?.first
        if (byColumnDescriptor != null) return byColumnDescriptor
        when (val cell = cells[name] ?: error("Couldn't resolve name $name")) {
            is CoerceMatrix -> when (val value = cell.value) {
                is SheetRangeExpr -> when (val range = value.rangeRef) {
                    is RectangleRangeRef -> return range.first.column.index
                    else -> error("${range.javaClass}")
                }
                else -> error("${value.javaClass}")
            }
            else -> error("${cell.javaClass}")
        }
    }

    protected fun rowIndexOfName(name: Id): Int {
        return if (name is Coordinate) name.row
        else 0
    }

    protected fun getNamedColumnIndex(name: Id) =
        columnDescriptors.toList().firstOrNull { (_, desc) -> desc.name == name }?.first

    protected fun getColumnDescriptor(index: Int) = columnDescriptors[index] ?: ColumnDescriptor(name = "", type = null)
    protected fun putColumnDescriptor(index: Int, desc: ColumnDescriptor) {
        columnDescriptors[index] = desc
    }

    protected fun cellExists(name: Id) = cells.containsKey(name)
    protected fun putCell(name: Id, expr: Expr) {
        assert(expr !is ScalarReference || name !== expr.name) {
            "Self reference inside cell"
        }
        cells[name] = expr
    }

    protected fun removeCell(name: Id) {
        cells.remove(name)
    }

    protected fun columnCount(): Int {
        val fromCells: Int = cells.keys.filterIsInstance<Coordinate>().maxOfOrNull { it.column } ?: 0
        val fromColumnDescriptorOrNull: Int? = columnDescriptors.keys.maxOrNull()
        val fromColumnDescriptor = if (fromColumnDescriptorOrNull == null) 0 else fromColumnDescriptorOrNull + 1
        return max(fromCells, fromColumnDescriptor)
    }

    protected fun rowCount(): Int? {
        if (cellsStack.isEmpty()) return null
        return (cells.keys.filterIsInstance<Coordinate>().maxOfOrNull { it.row } ?: 0) + 1
    }

    protected fun allocateColumnRange(name: String, width: Int): Int {
        val firstColumn = columnCount()
        for (element in 0 until width) {
            val column = firstColumn + element
            val name = if (element == 0) name else "$name:${element + 1}"
            putColumnDescriptor(column, ColumnDescriptor(name = name, type = null))
        }
        return firstColumn
    }

    override fun rewrite(expr: Sheet): Expr {
        try {
            columnDescriptorStack.add(0, expr.columnDescriptors.toMutableMap())
            val sheetCells = lazy {
                expr.cells.toMutableMap()
            }
            cellsStack.add(0, sheetCells)
            var changed = false
            for (cell in expr.cells) {
                val (name, cellExpr) = cell
                val (rewrittenName, rewrittenCell) = cell(name, cellExpr)
                val cellWasChangedInternally =
                    name == rewrittenName &&
                            sheetCells.isInitialized() &&
                            cellExpr != cells[name]
                changed = changed || (rewrittenCell !== cellExpr)
                changed = changed || (rewrittenName !== name)
                changed = changed || cellWasChangedInternally
                if (!cellWasChangedInternally) {
                    putCell(rewrittenName, rewrittenCell)
                }
            }
            return if (changed
                || columnDescriptors != expr.columnDescriptors
                || cells != expr.cells.toMap()
            )
                expr.dup(
                    columnDescriptors = columnDescriptors,
                    cells = cells.toCells()
                )
            else expr
        } finally {
            columnDescriptorStack.removeAt(0)
            cellsStack.removeAt(0)
        }
    }
}