package com.github.jomof.kane

import com.github.jomof.kane.impl.*
import com.github.jomof.kane.impl.sheet.*

/**
 * Return a subsection of a sheet.
 */
//operator fun Sheet.get(vararg ranges: String): Sheet {
//    val first = ranges[0]
//    if (ranges.size == 1 && looksLikeCellName(first)) {
//        val sheet = this
//        return sheetOf {
//            listOf(ExogenousSheetScalar(cellNameToCoordinate(first), sheet).toNamed(coordinate(0, 0)))
//        }
//    }
//    val asColumnIndices = tryConvertToColumnIndex(ranges.toList())
//    if (asColumnIndices != null) return ordinalColumns(asColumnIndices)
//    error("Couldn't get sheet subset for ${ranges.joinToString(",")}")
//}

/**
 * Return a single cell from a Sheet
 */
operator fun Sheet.get(column: Int, row: Int): Expr {
    return cells.getValue(coordinate(column, row))
}

operator fun MatrixVariable.get(column: Int, row: Int): MatrixVariableElement = getMatrixElement(column, row)
operator fun MatrixExpr.get(column: Int, row: Int): ScalarExpr = getMatrixElement(column, row)

operator fun Sequence<Row>.get(vararg ranges: String): Sequence<Row> {
    val first = ranges[0]
    if (ranges.size == 1 && looksLikeCellName(first)) {
        val sheet = this
        return sheetOf {
            listOf(ExogenousSheetScalar(cellNameToCoordinate(first), sheet).toNamed(coordinate(0, 0)))
        }
    }
    val rangesAsIndex = ranges.map { range ->
        if (looksLikeColumnName(range)) cellNameToColumnIndex(range)
        else null
    }
    return ColumnFilteringSequence(this) { columnIndex, columnDescriptor ->
        if (columnDescriptor == null) false
        else ranges.contains(columnDescriptor.name) || rangesAsIndex.contains(columnIndex)
    }
}