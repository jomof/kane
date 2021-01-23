package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.functions.*

private fun AlgebraicExpr.expandUnaryOperationMatrixes(): AlgebraicExpr {
    fun MatrixExpr.self() = expandUnaryOperationMatrixes() as MatrixExpr
    fun ScalarExpr.self() = expandUnaryOperationMatrixes() as ScalarExpr
    return when (this) {
        is NamedMatrix -> copy(matrix = matrix.self())
        is NamedScalar -> copy(scalar = scalar.self())
        is AlgebraicUnaryMatrixScalar -> {
            val data = DataMatrix(value.columns, value.rows, value.coordinates.map { offset ->
                extractScalarizedMatrixElement(value, offset)
            })
            val result = op.reduceArithmetic(data)
            result?.self() ?: this
        }
        is DataMatrix -> map { it.self() }
        is AlgebraicUnaryScalar -> copy(value = value.self())
        is AlgebraicUnaryScalarStatistic -> copy(value = value.self())
        is AlgebraicUnaryMatrix -> copy(value = value.self())
        is AlgebraicBinaryScalar -> copy(left = left.self(), right = right.self())
        is AlgebraicBinaryMatrixScalar -> copy(left = left.self(), right = right.self())
        is AlgebraicBinaryScalarStatistic -> copy(left = left.self(), right = right.self())
        is ConstantScalar -> this
        is DiscreteUniformRandomVariable -> this
        is CoerceScalar -> copy(value = value.expandUnaryOperationMatrixes())
        else -> error("$javaClass")
    }
}
private fun Expr.expandUnaryOperationMatrixes() : Expr {
    return when(this) {
        is AlgebraicExpr -> expandUnaryOperationMatrixes()
        is ComputableCellReference -> this
        is NamedValueExpr<*> -> this
        is NamedComputableCellReference -> this
        is NamedTiling<*> -> this
        else -> error("$javaClass")
    }
}

fun NamedExpr.expandUnaryOperationMatrixes() : NamedExpr =
    (this as Expr).expandUnaryOperationMatrixes() as NamedExpr