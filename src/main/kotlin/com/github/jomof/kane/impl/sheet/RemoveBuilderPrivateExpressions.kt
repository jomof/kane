package com.github.jomof.kane.impl.sheet

import com.github.jomof.kane.Expr
import com.github.jomof.kane.impl.visitor.RewritingVisitor

private fun Expr.removeBuilderPrivateExpressions(): Expr {
    return object : RewritingVisitor() {
        override fun rewrite(expr: Expr): Expr {
            if (expr is SheetBuilderRange) return SheetRangeExpr(expr.rangeRef)
            return super.rewrite(expr)
        }
    }.rewrite(this)
}

fun removeBuilderPrivateExpressions(sheet: Sheet) = sheet.removeBuilderPrivateExpressions() as Sheet

