package com.github.jomof.kane.rigueur.functions

import com.github.jomof.kane.rigueur.ScalarExpr
import com.github.jomof.kane.rigueur.UnaryOp
import com.github.jomof.kane.rigueur.constant
import com.github.jomof.kane.rigueur.types.one
import com.github.jomof.kane.rigueur.types.two
import kotlin.math.exp

val D by UnaryOp()

private class DFunction : AlgebraicUnaryScalarFunction {
    override val meta = D
    override fun doubleOp(value: Double) = error("")
    override fun floatOp(value: Float) = error("")

    override fun <E : Number> reduceArithmetic(value: ScalarExpr<E>): ScalarExpr<E>? {
        return null
    }

    override fun <E : Number> differentiate(
        expr : ScalarExpr<E>,
        exprd : ScalarExpr<E>,
        variable : ScalarExpr<E>
    ) = error("")
}

val d : AlgebraicUnaryScalarFunction = DFunction()