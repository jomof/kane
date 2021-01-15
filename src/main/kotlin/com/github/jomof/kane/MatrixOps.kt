package com.github.jomof.kane

import com.github.jomof.kane.types.kaneType

inline fun <reified E:Number> matrixOf(columns: Int, rows: Int, vararg elements : E) = DataMatrix(
    columns,
    rows,
    elements.map { ConstantScalar(it, elements[0].javaClass.kaneType) }.toList()
)
inline fun <reified E:Number> matrixOf(columns: Int, rows: Int, vararg elements : ScalarExpr<E>) = DataMatrix(
    columns,
    rows,
    elements.toList()
)
fun <E:Number> matrixOf(columns: Int, rows: Int, action:(Coordinate)->ScalarExpr<E>) : DataMatrix<E> =
    DataMatrix(
        columns,
        rows,
        coordinatesOf(columns, rows).map(action).toList()
    )



