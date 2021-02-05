package com.github.jomof.kane.impl.sheet

import com.github.jomof.kane.impl.*
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
            val rebased = expr.matrix.self(com.github.jomof.kane.impl.Identifier.coordinate(expr.name))
            if (rebased == expr.matrix) expr
            else expr.copy(matrix = rebased)
        }
    }.rewrite(this)
}