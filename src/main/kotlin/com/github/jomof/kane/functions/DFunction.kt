package com.github.jomof.kane.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.UnaryOp

val D by UnaryOp()

private class DFunction : AlgebraicUnaryScalarFunction {
    override val meta = D
    override fun doubleOp(value: Double) = error("")
    override fun floatOp(value: Float) = error("")

    override fun reduceArithmetic(value: ScalarExpr): ScalarExpr? {
        return null
    }

    override fun differentiate(
        expr : ScalarExpr,
        exprd : ScalarExpr,
        variable : ScalarExpr
    ) = error("")
}

val d : AlgebraicUnaryScalarFunction = DFunction()