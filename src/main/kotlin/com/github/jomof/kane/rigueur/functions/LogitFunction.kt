package com.github.jomof.kane.rigueur.functions

import com.github.jomof.kane.rigueur.ScalarExpr
import com.github.jomof.kane.rigueur.UnaryOp
import com.github.jomof.kane.rigueur.constant
import com.github.jomof.kane.rigueur.types.one
import com.github.jomof.kane.rigueur.types.two
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