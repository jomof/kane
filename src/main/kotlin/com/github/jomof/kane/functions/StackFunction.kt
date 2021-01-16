package com.github.jomof.kane.functions

import com.github.jomof.kane.*

// Stack Matrix function
private fun stackMatrix(
    leftDisplay : TypedExpr<Double>,
    rightDisplay : TypedExpr<Double>,
    left : MatrixExpr,
    right : MatrixExpr) : MatrixExpr {
    assert(left.columns == right.columns)
    return AlgebraicDeferredDataMatrix(
        BinaryOp(op = " stack ", precedence = 5, associative = true, infix = true),
        leftDisplay,
        rightDisplay,
        matrixOf(left.columns, left.rows + right.rows) { (column, row) ->
            if (row < left.rows) left[column, row]
            else right[column, row - left.rows]
        }
    )
}
infix fun MatrixExpr.stack(right : Double) : MatrixExpr = stackMatrix(this, constant(right, type), this, repeated(columns, 1, type, right))
infix fun Double.stack(right : MatrixExpr) : MatrixExpr = stackMatrix(constant(this, right.type), right, repeated(right.columns, 1, right.type, this), right)
infix fun MatrixExpr.stack(right : ScalarExpr) = stackMatrix(this, right, this, repeated(columns, 1, right))
infix fun ScalarExpr.stack(right : MatrixExpr) = stackMatrix(this, right, repeated(right.columns, 1, this), right)
infix fun MatrixExpr.stack(right : MatrixExpr) = stackMatrix(this, right, this, right)
