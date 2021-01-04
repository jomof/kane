package com.github.jomof.kane.rigueur.functions

import com.github.jomof.kane.rigueur.ScalarExpr
import com.github.jomof.kane.rigueur.UnaryOp
import com.github.jomof.kane.rigueur.constant
import com.github.jomof.kane.rigueur.types.one
import com.github.jomof.kane.rigueur.types.two
import kotlin.math.exp

val EXP by UnaryOp()

private class ExpFunction : AlgebraicUnaryScalarFunction {
    override val meta = EXP
    override fun doubleOp(value: Double) = exp(value)
    override fun floatOp(value: Float) = exp(value)

    override fun <E : Number> reduceArithmetic(value: ScalarExpr<E>): ScalarExpr<E>? {
        return null
    }

    override fun <E : Number> differentiate(
        expr : ScalarExpr<E>,
        exprd : ScalarExpr<E>,
        variable : ScalarExpr<E>
    ) = exp(expr) * exprd
}

val exp : AlgebraicUnaryScalarFunction = ExpFunction()