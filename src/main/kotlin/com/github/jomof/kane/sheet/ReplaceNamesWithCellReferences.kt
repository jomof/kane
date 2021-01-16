package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.functions.*

private fun AlgebraicExpr.replaceNamesWithCellReferencesAlgebraic(excluding : String) : AlgebraicExpr {
    fun MatrixExpr.self() = replaceNamesWithCellReferencesAlgebraic(excluding) as MatrixExpr
    fun ScalarExpr.self() = replaceNamesWithCellReferencesAlgebraic(excluding) as ScalarExpr
    return when(this) {
        is NamedScalar ->
            if (name != excluding && looksLikeCellName(name)) {
                CoerceScalar(AbsoluteCellReferenceExpr(cellNameToCoordinate(name)), type)
            } else scalar.self()
        is NamedMatrix ->
            if (name != excluding) {
                CoerceScalar(AbsoluteCellReferenceExpr(cellNameToCoordinate(name)), type)
            } else copy(matrix = matrix.self())
        is AlgebraicUnaryScalar -> copy(value = value.self())
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
        is CoerceScalar -> {
            val result : AlgebraicExpr = when (value) {
                is AbsoluteCellReferenceExpr -> this
                else -> error("${value.javaClass}")
            }
            result
        }
        is ConstantScalar -> this
        else ->
            error("$javaClass")
    }
}

private fun Expr.replaceNamesWithCellReferences(excluding : String) : Expr {
    return when(this) {
        is NamedScalar -> replaceNamesWithCellReferencesAlgebraic(excluding)
        is NamedValueExpr<*> -> toValueExpr()
        is NamedUntypedAbsoluteCellReference -> AbsoluteCellReferenceExpr(coordinate)
        else -> error("$javaClass")
    }
}

fun NamedExpr.replaceNamesWithCellReferences(excluding : String) : Expr =
    (this as Expr).replaceNamesWithCellReferences(excluding)