package com.github.jomof.kane

import com.github.jomof.kane.types.kaneType

fun matrixOf(columns: Int, rows: Int, elements : List<Double>) = DataMatrix(
    columns,
    rows,
    elements.map { ConstantScalar(it, elements[0].javaClass.kaneType) }.toList()
)
fun matrixOf(columns: Int, rows: Int, vararg elements : Double) = DataMatrix(
    columns,
    rows,
    elements.map { ConstantScalar(it, elements[0].javaClass.kaneType) }.toList()
)
fun matrixOf(columns: Int, rows: Int, vararg elements : ScalarExpr) = DataMatrix(
    columns,
    rows,
    elements.toList()
)
fun matrixOf(columns: Int, rows: Int, action:(Coordinate)->ScalarExpr) : DataMatrix =
    DataMatrix(
        columns,
        rows,
        coordinatesOf(columns, rows).map(action).toList()
    )



