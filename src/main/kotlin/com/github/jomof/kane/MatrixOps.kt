package com.github.jomof.kane


fun matrixOf(columns: Int, rows: Int, elements : List<Double>) = DataMatrix(
    columns,
    rows,
    elements.map { constant(it) }.toList()
)
fun matrixOf(columns: Int, rows: Int, vararg elements : Double) = DataMatrix(
    columns,
    rows,
    elements.map { constant(it) }.toList()
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



