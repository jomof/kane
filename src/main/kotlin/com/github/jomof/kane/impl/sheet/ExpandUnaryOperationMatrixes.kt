package com.github.jomof.kane.impl.sheet

import com.github.jomof.kane.Expr
import com.github.jomof.kane.impl.DataMatrix
import com.github.jomof.kane.impl.columns
import com.github.jomof.kane.impl.coordinates
import com.github.jomof.kane.impl.functions.AlgebraicUnaryMatrixScalar
import com.github.jomof.kane.impl.rows
import com.github.jomof.kane.impl.visitor.RewritingVisitor

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

fun expandUnaryOperations(sheet: Sheet): Sheet = sheet.expandUnaryOperations() as Sheet