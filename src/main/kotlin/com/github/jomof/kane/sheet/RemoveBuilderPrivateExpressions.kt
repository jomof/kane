package com.github.jomof.kane.sheet

import com.github.jomof.kane.Expr
import com.github.jomof.kane.visitor.RewritingVisitor

private fun Expr.removeBuilderPrivateExpressions(): Expr {
    return object : RewritingVisitor() {
        override fun rewrite(expr: Expr): Expr {
            if (expr is SheetBuilderRange) return SheetRangeExpr(expr.range)
            return super.rewrite(expr)
        }
    }.rewrite(this)
}

fun removeBuilderPrivateExpressions(cells: Map<String, Expr>): Map<String, Expr> {
    return cells
        .map { (name, expr) -> name to expr.removeBuilderPrivateExpressions() }
        .toMap()
}
