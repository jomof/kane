package com.github.jomof.kane.rigueur.functions

import com.github.jomof.kane.rigueur.*
import com.github.jomof.kane.rigueur.track


private class NegateFunction : AlgebraicUnaryScalarFunction {
    override val meta = NEGATE
    override fun doubleOp(value: Double) = -value
    override fun floatOp(value: Float) = -value

    override fun render(value: Expr): String {
        return when {
            (value is NamedScalarVariable<*> ||
                    value is AlgebraicBinaryScalar<*> && value.op == multiply) -> "-$value"
            else -> "-($value)"
        }
    }

    override fun <E : Number> reduceArithmetic(value: ScalarExpr<E>): ScalarExpr<E>? {
        if (value is AlgebraicUnaryScalar && value.op == negate) return value.value
        return null
    }
}

val negate : AlgebraicUnaryScalarFunction = NegateFunction()
