package com.github.jomof.kane.functions

import com.github.jomof.kane.AlgebraicExpr
import com.github.jomof.kane.MatrixExpr
import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.get
import com.github.jomof.kane.impl.BinaryOp
import com.github.jomof.kane.impl.constant
import com.github.jomof.kane.impl.functions.AlgebraicDeferredDataMatrix
import com.github.jomof.kane.impl.matrixOf
import com.github.jomof.kane.impl.repeated

val STACK = BinaryOp(op = " stack ", precedence = 5, associative = true, infix = true)

// Stack Matrix function
private fun stackMatrix(
    leftDisplay: AlgebraicExpr,
    rightDisplay: AlgebraicExpr,
    left: MatrixExpr,
    right: MatrixExpr
): MatrixExpr {
    assert(left.columns == right.columns)
    return AlgebraicDeferredDataMatrix(
        STACK,
        leftDisplay,
        rightDisplay,
        matrixOf(left.columns, left.rows + right.rows) { (column, row) ->
            if (row < left.rows) left[column, row]
            else right[column, row - left.rows]
        }
    )
}

infix fun MatrixExpr.stack(right: Double): MatrixExpr =
    stackMatrix(this, constant(right), this, repeated(columns, 1, right))

infix fun Double.stack(right: MatrixExpr): MatrixExpr =
    stackMatrix(constant(this), right, repeated(right.columns, 1, this), right)

infix fun MatrixExpr.stack(right: ScalarExpr) = stackMatrix(this, right, this, repeated(columns, 1, right))
infix fun ScalarExpr.stack(right: MatrixExpr) = stackMatrix(this, right, repeated(right.columns, 1, this), right)
infix fun MatrixExpr.stack(right: MatrixExpr) = stackMatrix(this, right, this, right)
