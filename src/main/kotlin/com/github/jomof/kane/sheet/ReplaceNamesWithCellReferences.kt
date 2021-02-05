package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.functions.*


private class ReplaceNamesWithCellReferences(val excluding: Id) {
    private fun MatrixExpr.replace() = (this as Expr).replace() as MatrixExpr
    private fun ScalarExpr.replace() = (this as Expr).replace() as ScalarExpr
    private fun SheetRange.replace() = (this as Expr).replace() as SheetRange
    private fun Expr.replace(): Expr {
        return when (this) {
            is NamedSheetRangeExpr ->
                when {
                    name == excluding -> copy(range = range.replace())
                    name is Coordinate -> {
                        CoerceScalar(
                            SheetRangeExpr(
                                name.toComputableCoordinate()
                            )
                        )
                    }

                    else ->
                        range.replace()
                }
            is NamedScalar ->
                when {
                    name == excluding -> copy(scalar = scalar.replace())
                    name is Coordinate -> {
                        CoerceScalar(
                            SheetRangeExpr(
                                name.toComputableCoordinate()
                            )
                        )
                    }
//                    scalar is ConstantScalar -> {
//                        NamedScalarVariable(name, scalar.value, scalar.type)
//                    }
                    scalar is ConstantScalar -> this
                    scalar is DiscreteUniformRandomVariable -> this
                    else -> scalar.replace()
                }
            is NamedMatrix -> copy(matrix = matrix.replace())
            is AlgebraicUnaryScalar -> copy(value = value.replace())
            is AlgebraicUnaryScalarStatistic -> copy(value = value.replace() as AlgebraicExpr)
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
            is CoerceScalar -> copy(value = value.replace())
            //is AlgebraicUnaryRangeStatistic -> this
            is AlgebraicBinaryRangeStatistic -> copy(right = right.replace())
            is ConstantScalar -> this
            is DiscreteUniformRandomVariable ->
                this
            is NamedScalarVariable -> this
            is SheetRangeExpr -> this
            is ValueExpr<*> -> this
            is Tiling<*> -> this
            is DataMatrix -> map { it.replace() }
            is RetypeScalar -> copy(scalar = scalar.replace())
            is RetypeMatrix -> copy(matrix = matrix.replace())
            else ->
                error("$javaClass")
        }
    }

    fun run(expr: Expr) = expr.replace()
}

fun Expr.replaceNamesWithCellReferences(excluding: Id): Expr {
    return ReplaceNamesWithCellReferences(excluding).run(this)
}
