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
                    CoerceScalar(
                        SheetRangeExpr(
                            cellNameToCoordinate(name).toComputableCoordinate()
                        ), type
                    )
                }
                scalar is ConstantScalar -> {
                    NamedScalarVariable(name, scalar.value, scalar.type)
                }
                else ->
                    scalar.self()
            }
        is NamedMatrix -> copy(matrix = matrix.self())
        is AlgebraicUnaryScalar -> copy(value = value.self())
        is AlgebraicUnaryScalarStatistic -> copy(value = value.self())
        is AlgebraicUnaryMatrix -> copy(value = value.self())
        is AlgebraicUnaryMatrixScalar -> copy(value = value.self())
        is AlgebraicBinaryMatrix -> copy(
            left = left.self(),
            right = right.self()
        )
        is AlgebraicBinaryScalarMatrix -> copy(
            left = left.self(),
            right = right.self()
        )
        is AlgebraicBinaryScalar -> copy(
            left = left.self(),
            right = right.self()
        )
        is AlgebraicBinaryMatrixScalar -> copy(
            left = left.self(),
            right = right.self()
        )
        is AlgebraicBinaryScalarStatistic -> copy(
            left = left.self(),
            right = right.self()
        )
        is CoerceScalar -> this
        is ConstantScalar -> this
        is DiscreteUniformRandomVariable -> this
        is DataMatrix -> map { it.self() }
        else ->
            error("$javaClass")
    }
}

fun Expr.replaceNamesWithCellReferences(excluding: String): Expr {
    return when (this) {
        is AlgebraicExpr -> replaceNamesWithCellReferencesAlgebraic(excluding)
//        is NamedScalar -> replaceNamesWithCellReferencesAlgebraic(excluding)
//        is NamedValueExpr<*> -> toUnnamed()
//        is NamedComputableCellReference -> ComputableCellReference(range)
//        is NamedMatrix -> this
        is ValueExpr<*> -> this
        is SheetRangeExpr -> this
        else -> error("$javaClass")
    }
}
