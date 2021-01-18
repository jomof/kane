package com.github.jomof.kane.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.UnaryOp
import com.github.jomof.kane.constant
import kotlin.math.exp

val LOGIT by UnaryOp()

private class LogitFunction : AlgebraicUnaryScalarFunction {
    override val meta = LOGIT
    override fun doubleOp(value: Double) = 1.0 / (1.0 + exp(-value))

    override fun reduceArithmetic(value: ScalarExpr): ScalarExpr? {
        return null
    }

    override fun differentiate(
        expr : ScalarExpr,
        exprd : ScalarExpr,
        variable : ScalarExpr
    ): ScalarExpr {
        return logit(expr) * (constant(1.0, expr.type) - logit(expr)) * exprd
    }
}

val logit : AlgebraicUnaryScalarFunction = LogitFunction()