package com.github.jomof.kane.sheet

import com.github.jomof.kane.DataMatrix
import com.github.jomof.kane.Expr
import com.github.jomof.kane.coordinates
import com.github.jomof.kane.functions.AlgebraicUnaryMatrixScalar
import com.github.jomof.kane.visitor.RewritingVisitor

fun Expr.expandUnaryOperations(): Expr {
    return object : RewritingVisitor() {
        override fun rewrite(expr: AlgebraicUnaryMatrixScalar): Expr {
            val data = DataMatrix(expr.value.columns, expr.value.rows, expr.value.coordinates.map { offset ->
                extractScalarizedMatrixElement(expr.value, offset, mapOf())
            })
            val result = expr.op.reduceArithmetic(data) ?: return super.rewrite(expr)
            return rewrite(result)
        }
    }.rewrite(this)
}
