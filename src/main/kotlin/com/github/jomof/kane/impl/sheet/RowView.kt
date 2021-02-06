package com.github.jomof.kane.impl.sheet

import com.github.jomof.kane.Expr
import com.github.jomof.kane.get
import com.github.jomof.kane.impl.NamedColumnRangeRef
import com.github.jomof.kane.impl.constant
import com.github.jomof.kane.impl.coordinateToCellName

interface RangeExprProvider {
    fun range(range: SheetRangeExpr): Expr = range
}

class RowView(private val sheet: Sheet, private val row: Int) : RangeExprProvider {
    val name: List<Expr> get() = sheet.rowDescriptors[row + 1]?.name ?: listOf(constant(row))
    operator fun get(column: String): String {
        val columnIndex = sheet.tryConvertToColumnIndex(column) ?: error("'$column' was not a recognized column")
        val cell = coordinateToCellName(columnIndex, row)
        return sheet[cell].toString()
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

    override fun toString() = name.joinToString(" ")
}