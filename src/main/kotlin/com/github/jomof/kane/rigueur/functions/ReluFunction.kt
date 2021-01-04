package com.github.jomof.kane.rigueur.functions

import com.github.jomof.kane.rigueur.ScalarExpr
import com.github.jomof.kane.rigueur.UnaryOp
import kotlin.math.max

val RELU by UnaryOp()
private class ReluFunction : AlgebraicUnaryScalarFunction {
    override val meta = RELU
    override fun doubleOp(value: Double) = max(0.0, value)
    override fun floatOp(value: Float) = max(0.0f, value)

    override fun <E : Number> reduceArithmetic(value: ScalarExpr<E>): ScalarExpr<E>? {
        return null
    }

    override fun <E : Number> differentiate(
        expr : ScalarExpr<E>,
        exprd : ScalarExpr<E>,
        variable : ScalarExpr<E>): ScalarExpr<E> {
        return step(expr) * exprd
    }
}

val relu : AlgebraicUnaryScalarFunction = ReluFunction()