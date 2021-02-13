package com.github.jomof.kane.impl.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.impl.UnaryOp
import com.github.jomof.kane.impl.constant
import com.github.jomof.kane.logit
import com.github.jomof.kane.minus
import com.github.jomof.kane.times
import kotlin.math.exp

private val LOGIT by UnaryOp()

internal class LogitFunction : AlgebraicUnaryFunction {
    override val meta = LOGIT
    override fun doubleOp(value: Double) = 1.0 / (1.0 + exp(-value))

    override fun differentiate(
        expr: ScalarExpr,
        exprd: ScalarExpr,
        variable: ScalarExpr
    ): ScalarExpr {
        return logit(expr) * (constant(1.0) - logit(expr)) * exprd
    }
}
