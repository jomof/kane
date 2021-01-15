package com.github.jomof.kane.functions

import com.github.jomof.kane.*

// Stack Matrix function
// Stack
private fun <E:Number> stackMatrix(
    leftDisplay : TypedExpr<E>,
    rightDisplay : TypedExpr<E>,
    left : MatrixExpr<E>,
    right : MatrixExpr<E>) : MatrixExpr<E> {
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
infix fun <E:Number> MatrixExpr<E>.stack(right : E) : MatrixExpr<E> = stackMatrix(this, constant(right, type), this, repeated(columns, 1, type, right))
infix fun <E:Number> E.stack(right : MatrixExpr<E>) : MatrixExpr<E> = stackMatrix(constant(this, right.type), right, repeated(right.columns, 1, right.type, this), right)
infix fun <E:Number> MatrixExpr<E>.stack(right : ScalarExpr<E>) = stackMatrix(this, right, this, repeated(columns, 1, right))
infix fun <E:Number> ScalarExpr<E>.stack(right : MatrixExpr<E>) = stackMatrix(this, right, repeated(right.columns, 1, this), right)
infix fun <E:Number> MatrixExpr<E>.stack(right : MatrixExpr<E>) = stackMatrix(this, right, this, right)
