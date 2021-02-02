package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.visitor.RewritingVisitor

fun Expr.replaceRelativeCellReferences(coordinate: Coordinate): Expr {
    fun MatrixExpr.self(coordinate: Coordinate) = replaceRelativeCellReferences(coordinate) as MatrixExpr
    return object : RewritingVisitor() {
        override fun rewrite(expr: SheetRangeExpr) = expr.copy(range = expr.range.rebase(coordinate))
        override fun rewrite(expr: NamedMatrix) = expr.copy(matrix = expr.matrix.self(cellNameToCoordinate(expr.name)))
    }.rewrite(this)
}
