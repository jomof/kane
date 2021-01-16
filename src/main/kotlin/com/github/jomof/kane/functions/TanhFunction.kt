package com.github.jomof.kane.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.UnaryOp
import com.github.jomof.kane.constant
import kotlin.math.exp

val TANH by UnaryOp()

private class TanhFunction : AlgebraicUnaryScalarFunction {
    override val meta = TANH
    override fun doubleOp(value: Double) = (exp(2.0 * value) - 1.0) / (exp(2.0 * value) + 1.0)
    override fun floatOp(value: Float) = (exp(2.0f * value) - 1.0f) / (exp(2.0f * value) + 1.0f)

    override fun reduceArithmetic(value: ScalarExpr): ScalarExpr? {
        return null
    }

    override fun differentiate(
        expr : ScalarExpr,
        exprd : ScalarExpr,
        variable : ScalarExpr
    ): ScalarExpr {
        return constant(1.0, expr.type) - pow(tanh(expr), 2.0) * exprd
    }
}

val tanh : AlgebraicUnaryScalarFunction = TanhFunction()