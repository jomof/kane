package com.github.jomof.kane.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.cos
import com.github.jomof.kane.impl.UnaryOp
import com.github.jomof.kane.impl.functions.AlgebraicUnaryScalarFunction
import kotlin.math.sin

private val SIN by UnaryOp()

internal class SinFunction : AlgebraicUnaryScalarFunction {
    override val meta = SIN
    override fun doubleOp(value: Double) = sin(value)

    override fun differentiate(
        expr: ScalarExpr,
        exprd: ScalarExpr,
        variable: ScalarExpr
    ): ScalarExpr {
        return cos(expr)
    }
}
