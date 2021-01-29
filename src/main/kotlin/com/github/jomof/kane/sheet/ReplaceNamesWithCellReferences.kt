package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.functions.*


private class ReplaceNamesWithCellReferences(val excluding: String) {
    private fun MatrixExpr.replace() = (this as Expr).replace() as MatrixExpr
    private fun ScalarExpr.replace() = (this as Expr).replace() as ScalarExpr
    private fun Expr.replace(): Expr {
        return when (this) {
            is NamedScalar ->
                when {
                    name == excluding -> copy(scalar = scalar.replace())
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
                        scalar.replace()
                }
            is NamedMatrix -> copy(matrix = matrix.replace())
            is AlgebraicUnaryScalar -> copy(value = value.replace())
            is AlgebraicUnaryScalarStatistic -> copy(value = value.replace())
            is AlgebraicUnaryMatrix -> copy(value = value.replace())
            is AlgebraicUnaryMatrixScalar -> copy(value = value.replace())
            is AlgebraicBinaryMatrix -> copy(
                left = left.replace(),
                right = right.replace()
            )
            is AlgebraicBinaryScalarMatrix -> copy(
                left = left.replace(),
                right = right.replace()
            )
            is AlgebraicBinaryScalar -> copy(
                left = left.replace(),
                right = right.replace()
            )
            is AlgebraicBinaryMatrixScalar -> copy(
                left = left.replace(),
                right = right.replace()
            )
            is AlgebraicBinaryScalarStatistic -> copy(
                left = left.replace(),
                right = right.replace()
            )
            is NamedSheetRangeExpr -> toUnnamed()
            is CoerceScalar -> copy(value = value.replace())
            is AlgebraicUnaryRangeStatistic -> this
            is AlgebraicBinaryRangeStatistic -> copy(right = right.replace())
            is ConstantScalar -> this
            is DiscreteUniformRandomVariable -> this
            is NamedScalarVariable -> this
            is SheetRangeExpr -> this
            is ValueExpr<*> -> this
            is Tiling<*> -> this
            is DataMatrix -> map { it.replace() }
            else ->
                error("$javaClass")
        }
    }

    fun run(expr: Expr) = expr.replace()
}

fun Expr.replaceNamesWithCellReferences(excluding: String): Expr {
    return ReplaceNamesWithCellReferences(excluding).run(this)
}
