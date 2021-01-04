package com.github.jomof.kane.rigueur.functions

import com.github.jomof.kane.rigueur.*

val CROSS by BinaryOp(op = " cross ", precedence = 5, associative = false, infix = true)

// Cross Product
infix fun <E:Number> MatrixExpr<E>.cross(right : MatrixExpr<E>) : MatrixExpr<E> {
    fun lm() = if (this is NamedExpr) "matrix '$name'" else "left matrix"
    fun rm() = if (right is NamedExpr) "matrix '${right.name}'" else "right matrix"
    assert(columns == right.rows) {
        "${lm()} columns $columns did not equal ${rm()} rows ${right.rows}"
    }
    return AlgebraicDeferredDataMatrix(
        CROSS,
        this,
        right,
        matrixOf(right.columns, rows) { (column, row) ->
        (1 until columns)
            .fold(this[0, row] * right[column, 0]) { prior : ScalarExpr<E>, i ->
                prior + this[i, row] * right[column, i]
            }
        }
    )
}
