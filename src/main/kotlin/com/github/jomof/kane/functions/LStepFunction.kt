package com.github.jomof.kane.functions

import com.github.jomof.kane.impl.ScalarExpr
import com.github.jomof.kane.impl.UnaryOp
import com.github.jomof.kane.impl.constant
import com.github.jomof.kane.impl.functions.AlgebraicUnaryScalarFunction

val LSTEP by UnaryOp()

private class LStepFunction : AlgebraicUnaryScalarFunction {
    override val meta = LSTEP
    override fun doubleOp(value: Double) = if (value < 0.0) 0.1 else 1.0

    override fun differentiate(
        expr : ScalarExpr,
        exprd : ScalarExpr,
        variable : ScalarExpr
    ): ScalarExpr {
        return constant(0.0)
    }
}

val lstep : AlgebraicUnaryScalarFunction = LStepFunction()