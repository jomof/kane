package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.functions.*

private fun Expr.self(): Expr {
    val result = when (this) {
        is AlgebraicUnaryMatrixScalar -> copy(value = value.self())
        is AlgebraicUnaryScalarStatistic -> copy(value = value.self())
        is AlgebraicUnaryScalar -> copy(value = value.self())
        is AlgebraicUnaryMatrix -> copy(value = value.self())
        is NamedMatrix -> copy(matrix = matrix.self())
        is AlgebraicBinaryMatrix -> copy(left = left.self(), right = right.self())
        is AlgebraicBinaryScalarStatistic -> copy(left = left.self(), right = right.self())
        is AlgebraicBinaryScalarMatrix -> copy(left = left.self(), right = right.self())
        is AlgebraicBinaryMatrixScalar -> copy(left = left.self(), right = right.self())
        is AlgebraicBinaryScalar -> copy(
            left = left.self(),
            right = right.self(),
        )
        is CoerceScalar -> copy(value = value.self())
        is SheetBuilder.SheetBuilderRange -> SheetRangeExpr(range = range)
        is SheetRangeExpr -> this
        is ConstantScalar -> this
        is NamedScalar -> this
        is DiscreteUniformRandomVariable -> this
        is NamedSheetRangeExpr -> this
        is ValueExpr<*> -> this
        is Tiling<*> -> this
        is AlgebraicUnaryRangeStatistic -> this
        is DataMatrix -> map { it.self() }
        else -> error("$javaClass")
    }
    assert(!result.toString().contains("BUILDER")) {
        "Still had a builder reference"
    }
    return result
}

private inline fun <reified E : Any> Any.cast(): E {
    assert(this is E) {
        "$javaClass cannot be cast to ${E::class.java}"
    }
    return this as E

}

private fun ScalarExpr.self() = (this as Expr).self() as ScalarExpr
private fun MatrixExpr.self() = (this as Expr).self() as MatrixExpr
private fun UnnamedExpr.self(): UnnamedExpr = when (val result = (this as Expr).self()) {
    is NamedExpr -> result.toUnnamed()
    else -> result.cast()
}

fun removeBuilderPrivateExpressions(cells: Map<String, UnnamedExpr>): Map<String, UnnamedExpr> {
    return cells
        .map { (name, expr) -> name to expr.self() }
        .toMap()
}
