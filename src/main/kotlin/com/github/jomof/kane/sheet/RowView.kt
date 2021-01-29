package com.github.jomof.kane.sheet

import com.github.jomof.kane.coordinateToCellName

class RowView(private val sheet: Sheet, private val row: Int) {
    val name: String get() = sheet.rowDescriptors[row + 1]?.name ?: "$row"
    operator fun get(column: String): String {
        val columnIndex = sheet.tryConvertToColumnIndex(column) ?: error("'$column' was not a recognized column")
        val cell = coordinateToCellName(columnIndex, row)
        return sheet[cell].toString()
    }
}