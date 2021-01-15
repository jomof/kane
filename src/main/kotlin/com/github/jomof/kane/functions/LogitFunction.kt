package com.github.jomof.kane.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.UnaryOp
import com.github.jomof.kane.constant
import com.github.jomof.kane.types.one
import kotlin.math.exp

val LOGIT by UnaryOp()

private class LogitFunction : AlgebraicUnaryScalarFunction {
    override val meta = LOGIT
    override fun doubleOp(value: Double) = 1.0 / (1.0 + exp(-value))
    override fun floatOp(value: Float) = 1.0f / (1.0f + exp(-value))

    override fun <E : Number> reduceArithmetic(value: ScalarExpr<E>): ScalarExpr<E>? {
        return null
    }

    override fun <E : Number> differentiate(
        expr : ScalarExpr<E>,
        exprd : ScalarExpr<E>,
        variable : ScalarExpr<E>
    ): ScalarExpr<E> {
        return logit(expr) * (constant(expr.type.one, expr.type) - logit(expr)) * exprd
    }
}

val logit : AlgebraicUnaryScalarFunction = LogitFunction()