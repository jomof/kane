package com.github.jomof.kane.impl.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.impl.UnaryOp
import com.github.jomof.kane.sin
import kotlin.math.cos

private val COS by UnaryOp()

internal class CosFunction : AlgebraicUnaryFunction {
    override val meta = COS
    override fun doubleOp(value: Double) = cos(value)

    override fun differentiate(
        expr: ScalarExpr,
        exprd: ScalarExpr,
        variable: ScalarExpr
    ): ScalarExpr {
        return -sin(expr)
    }
}
