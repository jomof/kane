package com.github.jomof.kane.impl.sheet

import com.github.jomof.kane.Expr
import com.github.jomof.kane.RowBase
import com.github.jomof.kane.api.RowDescriptor
import com.github.jomof.kane.api.SheetDescriptor
import com.github.jomof.kane.get
import com.github.jomof.kane.impl.NamedColumnRangeRef
import com.github.jomof.kane.impl.coordinate

interface RangeExprProvider {
    fun range(range: SheetRangeExpr): Expr = range
}

class RowView(private val sheet: Sheet, private val row: Int) : RowBase(), RangeExprProvider {
    override val rowDescriptor: RowDescriptor? get() = sheet.rowDescriptors[row + 1]
    override val sheetDescriptor: SheetDescriptor get() = sheet.sheetDescriptor
    override val columnCount: Int get() = sheet.columns
    override val columnDescriptors: Map<Int, ColumnDescriptor> = sheet.columnDescriptors
    override val rowOrdinal: Int get() = row

    override fun get(column: Int) = sheet.cells[coordinate(column, row)]

    override fun get(column: String): String {
        val columnIndex = sheet.tryConvertToColumnIndex(column) ?: error("'$column' was not a recognized column")
        val cell = coordinate(columnIndex, row)
        return sheet.cells[cell].toString()
    }

    override fun range(range: SheetRangeExpr): Expr {
        return when (range.rangeRef) {
            is NamedColumnRangeRef -> {
                val column = sheet.columnDescriptors
                    .filter { it.value.name == range.rangeRef.name }
                    .map { it.key }.single()
                sheet[column, row]
            }
            else -> error("${range.rangeRef.javaClass}")
        }
    }


}