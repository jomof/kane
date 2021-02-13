package com.github.jomof.kane.impl.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.impl.UnaryOp
import com.github.jomof.kane.step
import com.github.jomof.kane.times
import kotlin.math.max

val RELU by UnaryOp()

internal class ReluFunction : AlgebraicUnaryScalarFunction {
    override val meta = RELU
    override fun doubleOp(value: Double) = max(0.0, value)

    override fun differentiate(
        expr: ScalarExpr,
        exprd: ScalarExpr,
        variable: ScalarExpr
    ): ScalarExpr {
        return step(expr) * exprd
    }
}
