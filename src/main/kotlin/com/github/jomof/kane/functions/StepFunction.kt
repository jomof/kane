package com.github.jomof.kane.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.UnaryOp
import com.github.jomof.kane.constant

val STEP by UnaryOp()

private class StepFunction : AlgebraicUnaryScalarFunction {
    override val meta = STEP
    override fun doubleOp(value: Double) = if (value < 0.0) 0.0 else 1.0
    override operator fun invoke(value : ScalarExpr) : ScalarExpr = AlgebraicUnaryScalar(this, value, value.type)

    override fun differentiate(
        expr : ScalarExpr,
        exprd : ScalarExpr,
        variable : ScalarExpr
    ): ScalarExpr {
        return constant(0.0, expr.type)
    }
}

val step : AlgebraicUnaryScalarFunction = StepFunction()