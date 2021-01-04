package com.github.jomof.kane.rigueur.functions

import com.github.jomof.kane.rigueur.*
import com.github.jomof.kane.rigueur.track

val NEGATE by UnaryOp(op = "-")

private class NegateFunction : AlgebraicUnaryScalarFunction {
    override val meta = NEGATE
    override fun doubleOp(value: Double) = -value
    override fun floatOp(value: Float) = -value

    override fun <E : Number> reduceArithmetic(value: ScalarExpr<E>): ScalarExpr<E>? {
        if (value is AlgebraicUnaryScalar && value.op == negate) return value.value
        return null
    }

    override fun <E : Number> differentiate(
        expr: ScalarExpr<E>,
        exprd: ScalarExpr<E>,
        variable: ScalarExpr<E>
    ) = -exprd
}

val negate : AlgebraicUnaryScalarFunction = NegateFunction()

// Unary minus
operator fun <E:Number> ScalarExpr<E>.unaryMinus() = negate(this)
operator fun <E:Number> MatrixExpr<E>.unaryMinus() = negate(this)
