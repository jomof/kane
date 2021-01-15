package com.github.jomof.kane.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.UnaryOp
import kotlin.math.exp

val EXP by UnaryOp()

private class ExpFunction : AlgebraicUnaryScalarFunction {
    override val meta = EXP
    override fun doubleOp(value: Double) = exp(value)
    override fun floatOp(value: Float) = exp(value)

    override fun <E : Number> reduceArithmetic(value: ScalarExpr<E>): ScalarExpr<E>? {
        return null
    }

    override fun <E : Number> differentiate(
        expr : ScalarExpr<E>,
        exprd : ScalarExpr<E>,
        variable : ScalarExpr<E>
    ) = exp(expr) * exprd
}

val exp : AlgebraicUnaryScalarFunction = ExpFunction()