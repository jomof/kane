package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.functions.*


private fun AlgebraicExpr.convertCellNamesToUpperCase() : AlgebraicExpr {
    fun MatrixExpr.self() = convertCellNamesToUpperCase() as MatrixExpr
    fun ScalarExpr.self() = convertCellNamesToUpperCase() as ScalarExpr
    fun String.upper() = if (looksLikeCellName(toUpperCase())) toUpperCase() else this
    return when(this) {
        is NamedMatrix -> copy(name = name.upper(), matrix = matrix.self())
        is NamedScalar -> copy(name = name.upper(), scalar = scalar.self())
        is AlgebraicUnaryScalar -> copy(value = value.self())
        is AlgebraicUnaryScalarStatistic -> copy(value = value.self())
        is AlgebraicUnaryMatrix -> copy(value = value.self())
        is AlgebraicUnaryMatrixScalar -> copy(value = value.self())
        is AlgebraicBinaryScalar -> copy(left = left.self(), right = right.self())
        is AlgebraicBinaryScalarStatistic -> copy(left = left.self(), right = right.self())
        is AlgebraicBinaryMatrixScalar -> copy(left = left.self(), right = right.self())
        is AlgebraicBinaryScalarMatrix -> copy(left = left.self(), right = right.self())
        is AlgebraicBinaryMatrix -> copy(left = left.self(), right = right.self())
        is AlgebraicDeferredDataMatrix -> copy(left = left, right = right, data = data.self() as DataMatrix)
        is ConstantScalar -> this
        is DataMatrix -> map { it.self() }
        is CoerceScalar -> this
        is DiscreteUniformRandomVariable -> this
        else -> error("$javaClass")
    }
}

private fun Expr.convertCellNamesToUpperCase() : Expr {
    return when (this) {
        is AlgebraicExpr -> convertCellNamesToUpperCase()
//        is NamedValueExpr<*> -> copy(name = name.upper())
//        is NamedComputableCellReference -> copy(name = name.upper())
//        is NamedTiling<*> -> copy(name = name.upper())
        is Tiling<*> -> this
        is ValueExpr<*> -> this
        is SheetRangeExpr -> this
        else -> error("$javaClass")
    }
}

fun UnnamedExpr.convertCellNamesToUpperCase(): UnnamedExpr =
    (this as Expr).convertCellNamesToUpperCase() as UnnamedExpr