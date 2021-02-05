package com.github.jomof.kane.functions

import com.github.jomof.kane.impl.*

val NEGATE by UnaryOp(op = "-")

private class NegateFunction : AlgebraicUnaryScalarFunction {
    override val meta = NEGATE
    override fun doubleOp(value: Double) = -value

    override fun reduceArithmetic(value: ScalarExpr): ScalarExpr? {
        if (value is AlgebraicUnaryScalar && value.op == negate) return value.value
        if (value is ConstantScalar) return value.copy(value = -value.value)
        if (value is ScalarListExpr) return ScalarListExpr(value.values.map { negate(it) })
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
