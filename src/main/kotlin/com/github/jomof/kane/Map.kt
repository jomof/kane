package com.github.jomof.kane

import com.github.jomof.kane.impl.DataMatrix
import com.github.jomof.kane.impl.elements

/**
 * Returns a list containing the results of applying the given [transform]
 * function to each element in the original collection.
 */
fun MatrixExpr.map(transform: (ScalarExpr) -> ScalarExpr): MatrixExpr =
    DataMatrix(columns, rows, elements.map(transform).toList())