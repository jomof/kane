package com.github.jomof.kane

import com.github.jomof.kane.impl.Expr
import com.github.jomof.kane.impl.cellNameToCoordinate
import com.github.jomof.kane.impl.coordinate
import com.github.jomof.kane.impl.looksLikeCellName
import com.github.jomof.kane.impl.sheet.Sheet
import com.github.jomof.kane.impl.sheet.ordinalColumns
import com.github.jomof.kane.impl.sheet.tryConvertToColumnIndex

/**
 * Return a subsection of a sheet.
 */
operator fun Sheet.get(vararg ranges: String): Expr {
    if (ranges.size == 1 && looksLikeCellName(ranges[0]))
        return cells.getValue(cellNameToCoordinate(ranges[0]))
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