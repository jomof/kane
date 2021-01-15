package com.github.jomof.kane.rigueur.functions

import com.github.jomof.kane.rigueur.ScalarExpr
import com.github.jomof.kane.rigueur.UnaryOp
import com.github.jomof.kane.rigueur.constant
import com.github.jomof.kane.rigueur.types.zero

val STEP by UnaryOp()

private class StepFunction : AlgebraicUnaryScalarFunction {
    override val meta = STEP
    override fun doubleOp(value: Double) = if (value < 0.0) 0.0 else 1.0
    override fun floatOp(value: Float) = if (value < 0.0f) 0.0f else 1.0f

    override fun <E : Number> reduceArithmetic(value: ScalarExpr<E>): ScalarExpr<E>? {
        return null
    }

    override fun <E : Number> differentiate(
        expr : ScalarExpr<E>,
        exprd : ScalarExpr<E>,
        variable : ScalarExpr<E>
    ): ScalarExpr<E> {
        return constant(expr.type.zero, expr.type)
    }
}

val step : AlgebraicUnaryScalarFunction = StepFunction()