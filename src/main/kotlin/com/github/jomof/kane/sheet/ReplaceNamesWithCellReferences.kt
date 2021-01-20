package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.functions.*

private fun AlgebraicExpr.replaceNamesWithCellReferencesAlgebraic(excluding : String) : AlgebraicExpr {
    fun MatrixExpr.self() = replaceNamesWithCellReferencesAlgebraic(excluding) as MatrixExpr
    fun ScalarExpr.self() = replaceNamesWithCellReferencesAlgebraic(excluding) as ScalarExpr
    return when(this) {
        is NamedScalar ->
            when {
                name == excluding -> copy(scalar = scalar.self())
                looksLikeCellName(name) -> {
                    CoerceScalar(ComputableCellReference(cellNameToCoordinate(name)), type)
                }
                scalar is ConstantScalar -> {
                    NamedScalarVariable(name, scalar.value, scalar.type)
                }
                else ->
                    scalar.self()
            }
        is NamedMatrix -> copy(matrix = matrix.self())
        is AlgebraicUnaryScalar -> copy(value = value.self())
        is AlgebraicUnaryRandomVariableScalar -> copy(value = value.self())
        is AlgebraicUnaryMatrix -> copy(value = value.self())
        is AlgebraicUnaryMatrixScalar -> copy(value = value.self())
        is AlgebraicBinaryScalarMatrix -> copy(
            left = left.self(),
            right = right.self())
        is AlgebraicBinaryScalar -> copy(
            left = left.self(),
            right = right.self())
        is AlgebraicBinaryMatrixScalar -> copy(
            left = left.self(),
            right = right.self())
        is AlgebraicBinaryScalarStatistic -> copy(
            left = left.self(),
            right = right.self())
        is CoerceScalar -> {
            val result : AlgebraicExpr = when (value) {
                is ComputableCellReference -> this
                is NamedComputableCellReference -> this
                else -> error("${value.javaClass}")
            }
            result
        }
        is ConstantScalar -> this
        is DiscreteUniformRandomVariable -> this
        is DataMatrix -> map { it.self() }
        else ->
            error("$javaClass")
    }
}

private fun Expr.replaceNamesWithCellReferences(excluding : String) : Expr {
    return when(this) {
        is NamedScalar -> replaceNamesWithCellReferencesAlgebraic(excluding)
        is NamedValueExpr<*> -> toValueExpr()
        is NamedComputableCellReference -> ComputableCellReference(coordinate)
        is NamedMatrix -> this
        else -> error("$javaClass")
    }
}

fun NamedExpr.replaceNamesWithCellReferences(excluding : String) : Expr =
    (this as Expr).replaceNamesWithCellReferences(excluding)