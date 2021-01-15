package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.functions.*


private fun <E:Number> AlgebraicExpr<E>.convertCellNamesToUpperCase() : AlgebraicExpr<E> {
    fun <E:Number> MatrixExpr<E>.self() = convertCellNamesToUpperCase() as MatrixExpr<E>
    fun <E:Number> ScalarExpr<E>.self() = convertCellNamesToUpperCase() as ScalarExpr<E>
    fun String.upper() = if (looksLikeCellName(toUpperCase())) toUpperCase() else this
    return when(this) {
        is NamedMatrix -> copy(name = name.upper(), matrix = matrix.self())
        is NamedScalar -> copy(name = name.upper(), scalar = scalar.self())
        is AlgebraicUnaryScalar -> copy(value = value.self())
        is AlgebraicUnaryMatrix -> copy(value = value.self())
        is AlgebraicUnaryMatrixScalar -> copy(value = value.self())
        is AlgebraicBinaryScalar -> copy(left = left.self(), right = right.self())
        is AlgebraicBinaryMatrixScalar -> copy(left = left.self(), right = right.self())
        is AlgebraicBinaryScalarMatrix -> copy(left = left.self(), right = right.self())
        is ConstantScalar -> this
        is DataMatrix -> map { it.self() }
        is CoerceScalar -> this
        else -> error("$javaClass")
    }
}

private fun Expr.convertCellNamesToUpperCase() : Expr {
    fun String.upper() = if (looksLikeCellName(toUpperCase())) toUpperCase() else this
    return when(this) {
        is AlgebraicExpr<*> -> convertCellNamesToUpperCase()
        is NamedValueExpr<*> -> copy(name = name.upper())
        is NamedUntypedAbsoluteCellReference -> copy(name = name.upper())
        else -> error("$javaClass")
    }
}

fun NamedExpr.convertCellNamesToUpperCase() : NamedExpr =
    (this as Expr).convertCellNamesToUpperCase() as NamedExpr