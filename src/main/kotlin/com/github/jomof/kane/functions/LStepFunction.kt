package com.github.jomof.kane.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.UnaryOp
import com.github.jomof.kane.constant

val LSTEP by UnaryOp()

private class LStepFunction : AlgebraicUnaryScalarFunction {
    override val meta = LSTEP
    override fun doubleOp(value: Double) = if (value < 0.0) 0.1 else 1.0
    override fun floatOp(value: Float) = if (value < 0.0f) 0.1f else 1.0f

    override fun reduceArithmetic(value: ScalarExpr): ScalarExpr? {
        return null
    }

    override fun differentiate(
        expr : ScalarExpr,
        exprd : ScalarExpr,
        variable : ScalarExpr
    ): ScalarExpr {
        return constant(0.0, expr.type)
    }
}

val lstep : AlgebraicUnaryScalarFunction = LStepFunction()