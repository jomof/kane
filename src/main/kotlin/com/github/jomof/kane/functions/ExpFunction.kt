package com.github.jomof.kane.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.exp
import com.github.jomof.kane.impl.UnaryOp
import com.github.jomof.kane.impl.functions.AlgebraicUnaryScalarFunction
import com.github.jomof.kane.times

private val EXP by UnaryOp()

internal class ExpFunction : AlgebraicUnaryScalarFunction {
    override val meta = EXP
    override fun doubleOp(value: Double) = Math.exp(value)

    override fun differentiate(
        expr: ScalarExpr,
        exprd: ScalarExpr,
        variable: ScalarExpr
    ) = exp(expr) * exprd
}
