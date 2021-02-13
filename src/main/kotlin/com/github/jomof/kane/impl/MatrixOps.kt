package com.github.jomof.kane.impl

import com.github.jomof.kane.ScalarExpr

fun matrixOf(columns: Int, rows: Int, vararg elements: Double) = DataMatrix(
    columns,
    rows,
    elements.map { constant(it) }.toList()
)

fun matrixOf(columns: Int, rows: Int, vararg elements: ScalarExpr) = DataMatrix(
    columns,
    rows,
    elements.toList()
)

fun matrixOf(columns: Int, rows: Int, init: (Coordinate) -> Any): DataMatrix {
    val elements = coordinatesOf(columns, rows).map {
        convertAnyToScalarExpr(init(it))
    }
    return matrixOf(columns, rows, *elements.toTypedArray())
}



