package com.github.jomof.kane.rigueur

import com.github.jomof.kane.rigueur.types.kaneType

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
inline fun <reified E:Number> columnOf(vararg elements : E) : MatrixExpr<E> = matrixOf(1, elements.size, *elements)
inline fun <reified E:Number> columnOf(vararg elements : ScalarExpr<E>) : MatrixExpr<E> = matrixOf(1, elements.size, *elements)
inline fun <reified E:Number> columnOf(elements : List<ScalarExpr<E>>) : MatrixExpr<E> = matrixOf(1, elements.size, *(elements.toTypedArray()))
fun columnOf(range : IntRange) : MatrixExpr<Int> = columnOf(*range.map { it }.toTypedArray())
