package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.functions.*


private fun AlgebraicExpr.convertCellNamesToUpperCase() : AlgebraicExpr {
    fun MatrixExpr.self() = convertCellNamesToUpperCase() as MatrixExpr
    fun ScalarExpr.self() = convertCellNamesToUpperCase() as ScalarExpr
    fun Id.upper(): Id {
        if (this is Coordinate) return this
        if (this !is String) error("not a string")
        val upper = toUpperCase()
        return if (looksLikeCellName(upper)) cellNameToCoordinate(upper) else this
    }
    return when (this) {
        is NamedScalarVariable -> copy(name = name.upper())
        is NamedMatrix -> copy(name = name.upper(), matrix = matrix.self())
        is NamedScalar -> copy(name = name.upper(), scalar = scalar.self())
        is AlgebraicUnaryScalar -> copy(value = value.self())
        is AlgebraicUnaryScalarStatistic -> copy(value = value.convertCellNamesToUpperCase())
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

fun Expr.convertCellNamesToUpperCase(): Expr {
    return when (this) {
        is AlgebraicExpr -> convertCellNamesToUpperCase()
        is Tiling<*> -> this
        is ValueExpr<*> -> this
        is SheetRangeExpr -> this
        is AlgebraicBinaryRangeStatistic -> this
        is NamedSheetRangeExpr -> this
        else -> error("$javaClass")
    }
}
