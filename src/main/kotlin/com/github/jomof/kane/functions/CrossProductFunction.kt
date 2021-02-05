package com.github.jomof.kane.functions

import com.github.jomof.kane.impl.*
import com.github.jomof.kane.impl.functions.AlgebraicDeferredDataMatrix
import com.github.jomof.kane.plus
import com.github.jomof.kane.times

val CROSS by BinaryOp(op = " cross ", precedence = 5, associative = false, infix = true)

// Cross Product
infix fun MatrixExpr.cross(right : MatrixExpr) : MatrixExpr {
    fun lm() = if (this is NamedExpr) "matrix '$name'" else "left matrix"
    fun rm() = if (right is NamedExpr) "matrix '${right.name}'" else "right matrix"
    assert(columns == right.rows) {
        "${lm()} columns $columns did not equal ${rm()} rows ${right.rows}"
    }
    val outer = this
    return AlgebraicDeferredDataMatrix(
        CROSS,
        this,
        right,
        matrixOf(right.columns, rows) { (column, row) ->
            (1 until columns)
                .fold(outer[0, row] * right[column, 0]) { prior: ScalarExpr, i ->
                    prior + outer[i, row] * right[column, i]
                }
        }
    )
}
