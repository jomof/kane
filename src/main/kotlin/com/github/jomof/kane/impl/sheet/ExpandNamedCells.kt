package com.github.jomof.kane.impl.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.impl.*
import com.github.jomof.kane.impl.visitor.RewritingVisitor

internal class ExpandNamedCells(private val lookup: Cells) : RewritingVisitor() {
    override fun rewrite(expr: CellSheetRangeExpr): Expr {
        val ref = expr.rangeRef.toCoordinate()
        val result = (lookup[ref] ?: constant(0.0)) as ScalarExpr
        return rewrite(result).withNameOf(expr)
    }
}

fun Expr.expandNamedCells(lookup: Cells): Expr {
    return ExpandNamedCells(lookup).rewrite(this)
}