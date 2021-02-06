package com.github.jomof.kane

import com.github.jomof.kane.impl.cellNameToCoordinate
import com.github.jomof.kane.impl.coordinate
import com.github.jomof.kane.impl.looksLikeCellName
import com.github.jomof.kane.impl.sheet.Sheet
import com.github.jomof.kane.impl.sheet.ordinalColumns
import com.github.jomof.kane.impl.sheet.tryConvertToColumnIndex
import com.github.jomof.kane.impl.toNamed

/**
 * Return a subsection of a sheet.
 */
operator fun Sheet.get(vararg ranges: String): Sheet {
    val first = ranges[0]
    if (ranges.size == 1 && looksLikeCellName(first)) {
        val coordinate = cellNameToCoordinate(first)
        val value = cells.getValue(coordinate)
        return sheetOf { listOf(value.toNamed(coordinate(0, 0))) }
    }
    val asColumnIndices = tryConvertToColumnIndex(ranges.toList())
    if (asColumnIndices != null) return ordinalColumns(asColumnIndices)
    error("Couldn't get sheet subset for ${ranges.joinToString(",")}")
}

/**
 * Return a single cell from a Sheet
 */
operator fun Sheet.get(column: Int, row: Int): Expr {
    return cells.getValue(coordinate(column, row))
}