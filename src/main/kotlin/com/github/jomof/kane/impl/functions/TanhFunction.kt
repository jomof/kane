package com.github.jomof.kane.impl.functions

import com.github.jomof.kane.*
import com.github.jomof.kane.impl.UnaryOp
import com.github.jomof.kane.impl.constant
import kotlin.math.exp

private val TANH by UnaryOp()

internal class TanhFunction : AlgebraicUnaryFunction {
    override val meta = TANH
    override fun doubleOp(value: Double) = (exp(2.0 * value) - 1.0) / (exp(2.0 * value) + 1.0)

    override fun differentiate(
        expr: ScalarExpr,
        exprd: ScalarExpr,
        variable: ScalarExpr
    ): ScalarExpr {
        return constant(1.0) - pow(tanh(expr), 2.0) * exprd
    }
}