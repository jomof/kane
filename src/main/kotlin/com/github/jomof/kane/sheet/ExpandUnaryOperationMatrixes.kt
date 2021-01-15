package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.functions.*

private fun <E:Number> AlgebraicExpr<E>.expandUnaryOperationMatrixes() : AlgebraicExpr<E> {
    fun <E:Number> MatrixExpr<E>.self() = expandUnaryOperationMatrixes() as MatrixExpr<E>
    fun <E:Number> ScalarExpr<E>.self() = expandUnaryOperationMatrixes() as ScalarExpr<E>
    val result  = when(this) {
        is NamedMatrix -> copy(matrix = matrix.self())
        is NamedScalar -> copy(scalar = scalar.self())
        is AlgebraicUnaryMatrixScalar -> {
            val data = DataMatrix(value.columns, value.rows, value.coordinates.map { offset ->
                extractScalarizedMatrixElement(value, offset)
            })
            val result = op.reduceArithmetic(data)
            result.self()
        }
        is AlgebraicUnaryScalar -> copy(value = value.self())
        is AlgebraicUnaryMatrix -> copy(value = value.self())
        is AlgebraicBinaryScalar -> copy(left = left.self(), right = right.self())
        is AlgebraicBinaryMatrixScalar -> copy(left = left.self(), right = right.self())
        is ConstantScalar -> this
        is CoerceScalar -> copy(value = value.expandUnaryOperationMatrixes())
        else -> error("$javaClass")
    }
    return result
}
private fun Expr.expandUnaryOperationMatrixes() : Expr {
    return when(this) {
        is AlgebraicExpr<*> -> expandUnaryOperationMatrixes()
        is AbsoluteCellReferenceExpr -> this
        is NamedValueExpr<*> -> this
        is NamedUntypedAbsoluteCellReference -> this
        else -> error("$javaClass")
    }
}

fun NamedExpr.expandUnaryOperationMatrixes() : NamedExpr =
    (this as Expr).expandUnaryOperationMatrixes() as NamedExpr