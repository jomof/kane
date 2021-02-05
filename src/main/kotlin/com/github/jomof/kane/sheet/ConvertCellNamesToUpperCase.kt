package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.visitor.RewritingVisitor

fun Expr.convertCellNamesToUpperCase(): Expr {
    fun Id.upper(): Id {
        if (this is Coordinate) return this
        if (this !is String) error("not a string")
        val upper = toUpperCase()
        return if (looksLikeCellName(upper)) cellNameToCoordinate(upper) else this
    }
    return object : RewritingVisitor() {
        override fun rewrite(expr: NamedMatrix): Expr = with(expr) {
            val rewritten = matrix(matrix)
            val name = expr.name.upper()
            return if (rewritten === matrix && name == expr.name) this
            else copy(name = name, matrix = rewritten)
        }

        override fun rewrite(expr: NamedScalar): Expr = with(expr) {
            val rewritten = scalar(scalar)
            val name = expr.name.upper()
            return if (rewritten === scalar && name == expr.name) this
            else copy(name = name, scalar = rewritten)
        }

        override fun rewrite(expr: NamedScalarVariable) = expr.copy(name = expr.name.upper())
    }.rewrite(this)
}
