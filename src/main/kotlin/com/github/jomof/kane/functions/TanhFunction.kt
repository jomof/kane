package com.github.jomof.kane.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.UnaryOp
import com.github.jomof.kane.constant
import com.github.jomof.kane.types.one
import com.github.jomof.kane.types.two
import kotlin.math.exp

val TANH by UnaryOp()

private class TanhFunction : AlgebraicUnaryScalarFunction {
    override val meta = TANH
    override fun doubleOp(value: Double) = (exp(2.0 * value) - 1.0) / (exp(2.0 * value) + 1.0)
    override fun floatOp(value: Float) = (exp(2.0f * value) - 1.0f) / (exp(2.0f * value) + 1.0f)

    override fun <E : Number> reduceArithmetic(value: ScalarExpr<E>): ScalarExpr<E>? {
        return null
    }

    override fun <E : Number> differentiate(
        expr : ScalarExpr<E>,
        exprd : ScalarExpr<E>,
        variable : ScalarExpr<E>
    ): ScalarExpr<E> {
        return constant(expr.type.one, expr.type) - pow(tanh(expr), expr.type.two) * exprd
    }
}

val tanh : AlgebraicUnaryScalarFunction = TanhFunction()