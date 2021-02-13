package com.github.jomof.kane.impl.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.impl.UnaryOp
import com.github.jomof.kane.lstep
import com.github.jomof.kane.times
import kotlin.math.max

private val LRELU by UnaryOp()

internal class LreluFunction : AlgebraicUnaryScalarFunction {
    override val meta = LRELU
    override fun doubleOp(value: Double) = max(0.1 * value, value)

    override fun differentiate(
        expr: ScalarExpr,
        exprd: ScalarExpr,
        variable: ScalarExpr
    ): ScalarExpr {
        return lstep(expr) * exprd
    }
}
