package com.github.jomof.kane.impl.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.impl.*
import com.github.jomof.kane.impl.visitor.RewritingVisitor

internal class ExpandNamedCells(private val lookup: Cells) : RewritingVisitor() {
    override fun rewrite(expr: CoerceScalar): Expr = with(expr) {
        return when (value) {
            is ScalarVariable -> constant(value.initial)
            is ScalarExpr -> rewrite(value)
            is SheetRangeExpr -> {
                if (value.rangeRef !is CellRangeRef) error("")
                val ref = value.rangeRef.toCoordinate()
                val result = (lookup[ref] ?: constant(0.0)) as ScalarExpr
                rewrite(result)
            }
            else -> error("${value.javaClass}")
        }
    }
}

fun Expr.expandNamedCells(lookup: Cells): Expr {
    return ExpandNamedCells(lookup).rewrite(this)
}