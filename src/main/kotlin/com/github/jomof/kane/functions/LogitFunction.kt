package com.github.jomof.kane.functions

import com.github.jomof.kane.impl.ScalarExpr
import com.github.jomof.kane.impl.UnaryOp
import com.github.jomof.kane.impl.constant
import com.github.jomof.kane.impl.functions.AlgebraicUnaryScalarFunction
import com.github.jomof.kane.minus
import com.github.jomof.kane.times
import kotlin.math.exp

val LOGIT by UnaryOp()

private class LogitFunction : AlgebraicUnaryScalarFunction {
    override val meta = LOGIT
    override fun doubleOp(value: Double) = 1.0 / (1.0 + exp(-value))

    override fun differentiate(
        expr : ScalarExpr,
        exprd : ScalarExpr,
        variable : ScalarExpr
    ): ScalarExpr {
        return logit(expr) * (constant(1.0) - logit(expr)) * exprd
    }
}

val logit : AlgebraicUnaryScalarFunction = LogitFunction()