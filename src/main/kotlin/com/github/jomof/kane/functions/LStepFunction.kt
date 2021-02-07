package com.github.jomof.kane.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.impl.UnaryOp
import com.github.jomof.kane.impl.constant
import com.github.jomof.kane.impl.functions.AlgebraicUnaryScalarFunction

private val LSTEP by UnaryOp()

internal class LstepFunction : AlgebraicUnaryScalarFunction {
    override val meta = LSTEP
    override fun doubleOp(value: Double) = if (value < 0.0) 0.1 else 1.0

    override fun differentiate(
        expr: ScalarExpr,
        exprd: ScalarExpr,
        variable: ScalarExpr
    ): ScalarExpr {
        return constant(0.0)
    }
}