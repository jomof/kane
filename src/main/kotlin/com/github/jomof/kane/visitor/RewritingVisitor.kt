package com.github.jomof.kane.visitor

import com.github.jomof.kane.*
import com.github.jomof.kane.functions.*
import com.github.jomof.kane.sheet.CoerceScalar
import com.github.jomof.kane.sheet.NamedSheetRangeExpr
import com.github.jomof.kane.sheet.SheetRangeExpr

open class RewritingVisitor {
    open fun CoerceScalar.rewrite(): Expr = copy(value = rewrite(value))
    open fun ConstantScalar.rewrite(): Expr = this
    open fun DiscreteUniformRandomVariable.rewrite(): Expr = this
    open fun SheetRangeExpr.rewrite(): Expr = this
    open fun Tiling<*>.rewrite(): Expr = this
    open fun ValueExpr<*>.rewrite(): Expr = this
    open fun NamedScalar.rewrite(): Expr = copy(scalar = scalar(scalar))
    open fun NamedSheetRangeExpr.rewrite(): Expr = copy(range = range(range))
    open fun NamedMatrix.rewrite(): Expr = copy(matrix = matrix(matrix))
    open fun DataMatrix.rewrite(): Expr = map { scalar(it) }
    open fun AlgebraicUnaryRangeStatistic.rewrite(): Expr = this
    open fun AlgebraicUnaryScalar.rewrite(): Expr = copy(value = scalar(value))
    open fun AlgebraicUnaryScalarStatistic.rewrite(): Expr = copy(value = scalar(value))
    open fun AlgebraicUnaryMatrix.rewrite(): Expr = copy(value = matrix(value))
    open fun AlgebraicUnaryMatrixScalar.rewrite(): Expr = copy(value = matrix(value))
    open fun AlgebraicBinaryRangeStatistic.rewrite(): Expr = copy(right = scalar(right))
    open fun AlgebraicBinaryMatrix.rewrite(): Expr = copy(left = matrix(left), right = matrix(right))
    open fun AlgebraicBinaryMatrixScalar.rewrite(): Expr = copy(left = matrix(left), right = scalar(right))
    open fun AlgebraicBinaryScalar.rewrite(): Expr = copy(left = scalar(left), right = scalar(right))
    open fun AlgebraicBinaryScalarMatrix.rewrite(): Expr = copy(left = scalar(left), right = matrix(right))
    open fun AlgebraicBinaryScalarStatistic.rewrite(): Expr = copy(left = scalar(left), right = scalar(right))

    open fun scalar(expr: ScalarExpr) = rewrite(expr) as ScalarExpr
    open fun matrix(expr: MatrixExpr) = rewrite(expr) as MatrixExpr
    open fun range(expr: SheetRangeExpr) = rewrite(expr) as SheetRangeExpr
    open fun unnamed(expr: UnnamedExpr) = rewrite(expr) as UnnamedExpr

    open fun rewrite(expr: Expr): Expr {
        return when (expr) {
            is AlgebraicBinaryMatrix -> expr.rewrite()
            is AlgebraicBinaryMatrixScalar -> expr.rewrite()
            is AlgebraicBinaryRangeStatistic -> expr.rewrite()
            is AlgebraicBinaryScalar -> expr.rewrite()
            is AlgebraicBinaryScalarMatrix -> expr.rewrite()
            is AlgebraicBinaryScalarStatistic -> expr.rewrite()
            is AlgebraicUnaryMatrix -> expr.rewrite()
            is AlgebraicUnaryMatrixScalar -> expr.rewrite()
            is AlgebraicUnaryRangeStatistic -> expr.rewrite()
            is AlgebraicUnaryScalar -> expr.rewrite()
            is AlgebraicUnaryScalarStatistic -> expr.rewrite()
            is CoerceScalar -> expr.rewrite()
            is ConstantScalar -> expr.rewrite()
            is DiscreteUniformRandomVariable -> expr.rewrite()
            is DataMatrix -> expr.rewrite()
            is NamedScalar -> expr.rewrite()
            is NamedSheetRangeExpr -> expr.rewrite()
            is NamedMatrix -> expr.rewrite()
            is SheetRangeExpr -> expr.rewrite()
            is Tiling<*> -> expr.rewrite()
            is ValueExpr<*> -> expr.rewrite()
            else -> error("${expr.javaClass}")
        }
    }
}