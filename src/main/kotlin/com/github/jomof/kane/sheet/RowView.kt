package com.github.jomof.kane.sheet

import com.github.jomof.kane.Expr
import com.github.jomof.kane.NamedColumnRange
import com.github.jomof.kane.coordinateToCellName


interface RangeExprProvider {
    fun range(range: SheetRangeExpr): Expr = range
}

class RowView(private val sheet: Sheet, private val row: Int) : RangeExprProvider {
    val name: String get() = sheet.rowDescriptors[row + 1]?.name ?: "$row"
    operator fun get(column: String): String {
        val columnIndex = sheet.tryConvertToColumnIndex(column) ?: error("'$column' was not a recognized column")
        val cell = coordinateToCellName(columnIndex, row)
        return sheet[cell].toString()
    }

    override fun range(range: SheetRangeExpr): Expr {
        return when (range.range) {
            is NamedColumnRange -> {
                val column = sheet.columnDescriptors
                    .filter { it.value.name == range.range.name }
                    .map { it.key }.single()
                sheet[column, row]
            }
            else -> error("${range.range.javaClass}")
        }
    }
}