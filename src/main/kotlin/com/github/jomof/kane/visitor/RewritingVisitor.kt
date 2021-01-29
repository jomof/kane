package com.github.jomof.kane.visitor

import com.github.jomof.kane.*
import com.github.jomof.kane.functions.*
import com.github.jomof.kane.sheet.CoerceScalar
import com.github.jomof.kane.sheet.NamedSheetRangeExpr
import com.github.jomof.kane.sheet.SheetRangeExpr

open class RewritingVisitor {
    open fun ConstantScalar.rewrite(): Expr = this
    open fun DiscreteUniformRandomVariable.rewrite(): Expr = this
    open fun CoerceScalar.rewrite(): Expr = copy(value = rewrite(value))
    open fun SheetRangeExpr.rewrite(): Expr = this
    open fun NamedScalar.rewrite(): Expr = copy(scalar = scalar.scalar())
    open fun NamedSheetRangeExpr.rewrite(): Expr = copy(range = range.range())
    open fun NamedMatrix.rewrite(): Expr = copy(matrix = matrix.matrix())
    open fun DataMatrix.rewrite(): Expr = map { it.scalar() }
    open fun AlgebraicUnaryScalar.rewrite(): Expr = copy(value = value.scalar())
    open fun AlgebraicUnaryMatrix.rewrite(): Expr = copy(value = value.matrix())
    open fun AlgebraicUnaryMatrixScalar.rewrite(): Expr = copy(value = value.matrix())
    open fun AlgebraicBinaryMatrixScalar.rewrite(): Expr = copy(left = left.matrix(), right = right.scalar())
    open fun AlgebraicBinaryScalar.rewrite(): Expr = copy(left = left.scalar(), right = right.scalar())
    open fun AlgebraicBinaryScalarMatrix.rewrite(): Expr = copy(left = left.scalar(), right = right.matrix())

    open fun ScalarExpr.scalar() = rewrite(this) as ScalarExpr
    open fun MatrixExpr.matrix() = rewrite(this) as MatrixExpr
    open fun SheetRangeExpr.range() = rewrite(this) as SheetRangeExpr

    open fun rewrite(expr: Expr): Expr {
        return when (expr) {
            is AlgebraicBinaryMatrixScalar -> expr.rewrite()
            is AlgebraicBinaryScalar -> expr.rewrite()
            is AlgebraicBinaryScalarMatrix -> expr.rewrite()
            is AlgebraicUnaryMatrix -> expr.rewrite()
            is AlgebraicUnaryMatrixScalar -> expr.rewrite()
            is AlgebraicUnaryScalar -> expr.rewrite()
            is CoerceScalar -> expr.rewrite()
            is ConstantScalar -> expr.rewrite()
            is DiscreteUniformRandomVariable -> expr.rewrite()
            is DataMatrix -> expr.rewrite()
            is NamedScalar -> expr.rewrite()
            is NamedSheetRangeExpr -> expr.rewrite()
            is NamedMatrix -> expr.rewrite()
            is SheetRangeExpr -> expr.rewrite()
            else -> error("${expr.javaClass}")
        }
    }
}