package com.github.jomof.kane.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.UnaryOp
import kotlin.math.max

val RELU by UnaryOp()
private class ReluFunction : AlgebraicUnaryScalarFunction {
    override val meta = RELU
    override fun doubleOp(value: Double) = max(0.0, value)
    override fun floatOp(value: Float) = max(0.0f, value)

    override fun reduceArithmetic(value: ScalarExpr): ScalarExpr? {
        return null
    }

    override fun differentiate(
        expr : ScalarExpr,
        exprd : ScalarExpr,
        variable : ScalarExpr): ScalarExpr {
        return step(expr) * exprd
    }
}

val relu : AlgebraicUnaryScalarFunction = ReluFunction()