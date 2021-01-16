package com.github.jomof.kane.functions

import com.github.jomof.kane.*

val NEGATE by UnaryOp(op = "-")

private class NegateFunction : AlgebraicUnaryScalarFunction {
    override val meta = NEGATE
    override fun doubleOp(value: Double) = -value
    override fun floatOp(value: Float) = -value

    override fun reduceArithmetic(value: ScalarExpr): ScalarExpr? {
        if (value is AlgebraicUnaryScalar && value.op == negate) return value.value
        return null
    }

    override fun differentiate(
        expr: ScalarExpr,
        exprd: ScalarExpr,
        variable: ScalarExpr
    ) = -exprd
}

val negate : AlgebraicUnaryScalarFunction = NegateFunction()

// Unary minus
operator fun ScalarExpr.unaryMinus() = negate(this)
operator fun MatrixExpr.unaryMinus() = negate(this)
