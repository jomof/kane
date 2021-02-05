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
        override fun rewrite(expr: NamedMatrix) = expr.copy(name = expr.name.upper(), matrix = matrix(expr.matrix))
        override fun rewrite(expr: NamedScalar) = expr.copy(name = expr.name.upper(), scalar = scalar(expr.scalar))
        override fun rewrite(expr: NamedScalarVariable) = expr.copy(name = expr.name.upper())
    }.rewrite(this)
}
