package com.github.jomof.kane.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.UnaryOp
import kotlin.math.max

val LRELU by UnaryOp()
private class LReluFunction : AlgebraicUnaryScalarFunction {
    override val meta = LRELU
    override fun doubleOp(value: Double) = max(0.1 * value, value)
    override fun floatOp(value: Float) =  max(0.1f * value, value)

    override fun reduceArithmetic(value: ScalarExpr): ScalarExpr? {
        return null
    }

    override fun differentiate(
        expr : ScalarExpr,
        exprd : ScalarExpr,
        variable : ScalarExpr): ScalarExpr {
        return lstep(expr) * exprd
    }
}

val lrelu : AlgebraicUnaryScalarFunction = LReluFunction()