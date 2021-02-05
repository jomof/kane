package com.github.jomof.kane.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.UnaryOp
import kotlin.math.exp

val EXP by UnaryOp()

private class ExpFunction : AlgebraicUnaryScalarFunction {
    override val meta = EXP
    override fun doubleOp(value: Double) = exp(value)

    override fun differentiate(
        expr : ScalarExpr,
        exprd : ScalarExpr,
        variable : ScalarExpr
    ) = exp(expr) * exprd
}

val exp : AlgebraicUnaryScalarFunction = ExpFunction()