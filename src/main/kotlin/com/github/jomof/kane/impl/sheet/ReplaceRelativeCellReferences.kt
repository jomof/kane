package com.github.jomof.kane.impl.sheet

import com.github.jomof.kane.Expr
import com.github.jomof.kane.MatrixExpr
import com.github.jomof.kane.impl.Coordinate
import com.github.jomof.kane.impl.NamedMatrix
import com.github.jomof.kane.impl.rebase
import com.github.jomof.kane.impl.visitor.RewritingVisitor

fun Expr.replaceRelativeCellReferences(coordinate: Coordinate): Expr {
    fun MatrixExpr.self(coordinate: Coordinate) = replaceRelativeCellReferences(coordinate) as MatrixExpr
    return object : RewritingVisitor() {
        override fun rewrite(expr: SheetRangeExpr) = run {
            val rebased = expr.rangeRef.rebase(coordinate)
            if (rebased == expr.rangeRef) expr
            else expr.copy(rangeRef = rebased)
        }

        override fun rewrite(expr: NamedMatrix) = run {
            when (expr.name) {
                is Coordinate -> {
                    val rebased = expr.matrix.self(expr.name)
                    if (rebased == expr.matrix) expr
                    else expr.copy(matrix = rebased)
                }
                else -> super.rewrite(expr)
            }
        }
    }.rewrite(this)
}
