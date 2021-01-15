package com.github.jomof.kane.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.UnaryOp

val D by UnaryOp()

private class DFunction : AlgebraicUnaryScalarFunction {
    override val meta = D
    override fun doubleOp(value: Double) = error("")
    override fun floatOp(value: Float) = error("")

    override fun <E : Number> reduceArithmetic(value: ScalarExpr<E>): ScalarExpr<E>? {
        return null
    }

    override fun <E : Number> differentiate(
        expr : ScalarExpr<E>,
        exprd : ScalarExpr<E>,
        variable : ScalarExpr<E>
    ) = error("")
}

val d : AlgebraicUnaryScalarFunction = DFunction()