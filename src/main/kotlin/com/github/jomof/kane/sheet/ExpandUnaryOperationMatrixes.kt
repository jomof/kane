package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.functions.*

private fun AlgebraicExpr.expandUnaryOperations(): AlgebraicExpr {
    fun MatrixExpr.self() = expandUnaryOperations() as MatrixExpr
    fun ScalarExpr.self() = expandUnaryOperations() as ScalarExpr
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
        is AlgebraicBinaryMatrix -> copy(left = left.self(), right = right.self())
        is AlgebraicBinaryMatrixScalar -> copy(left = left.self(), right = right.self())
        is AlgebraicBinaryScalarStatistic -> copy(left = left.self(), right = right.self())
        is ConstantScalar -> this
        is DiscreteUniformRandomVariable -> this
        is NamedScalarVariable -> this
        is CoerceScalar -> copy(value = value.expandUnaryOperations())
        else -> error("$javaClass")
    }
}

fun Expr.expandUnaryOperations(): Expr {
    return when (this) {
        is AlgebraicExpr -> expandUnaryOperations()
        else -> this
    }
}
