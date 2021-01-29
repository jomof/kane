package com.github.jomof.kane.sheet

import com.github.jomof.kane.Expr
import com.github.jomof.kane.UnnamedExpr
import com.github.jomof.kane.visitor.RewritingVisitor

private fun UnnamedExpr.removeBuilderPrivateExpressions(): UnnamedExpr {
    return object : RewritingVisitor() {
        override fun rewrite(expr: Expr): Expr {
            if (expr is SheetBuilderRange) return SheetRangeExpr(expr.range)
            return super.rewrite(expr)
        }
    }.rewrite(this) as UnnamedExpr
}

fun removeBuilderPrivateExpressions(cells: Map<String, UnnamedExpr>): Map<String, UnnamedExpr> {
    return cells
        .map { (name, expr) -> name to expr.removeBuilderPrivateExpressions() }
        .toMap()
}
