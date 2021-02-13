package com.github.jomof.kane

import com.github.jomof.kane.impl.columns
import com.github.jomof.kane.impl.rows
import com.github.jomof.kane.impl.sheet.Sheet

/**
 * Get the single cell from this [Sheet]. Error if there is more than one cell.
 */
fun Sheet.single() = cells.values.single()

/**
 * Get the single element from this [MatrixExpr]. Error if there is more than one element.
 */
fun MatrixExpr.single(): ScalarExpr {
    if (rows != 1 || columns != 1) error("Matrix has more than one element.")
    return this[0, 0]
}