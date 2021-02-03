package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.visitor.RewritingVisitor

fun Expr.replaceRelativeCellReferences(coordinate: Coordinate): Expr {
    fun MatrixExpr.self(coordinate: Coordinate) = replaceRelativeCellReferences(coordinate) as MatrixExpr
    return object : RewritingVisitor() {
        override fun rewrite(expr: SheetRangeExpr) = run {
            val rebased = expr.range.rebase(coordinate)
            if (rebased == expr.range) expr
            else expr.copy(range = rebased)
        }

        override fun rewrite(expr: NamedMatrix) = run {
            val rebased = expr.matrix.self(Identifier.coordinate(expr.name))
            if (rebased == expr.matrix) expr
            else expr.copy(matrix = rebased)
        }
    }.rewrite(this)
}
