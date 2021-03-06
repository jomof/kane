package com.github.jomof.kane.impl.functions

import com.github.jomof.kane.*
import com.github.jomof.kane.impl.UnaryOp
import com.github.jomof.kane.impl.constant

private val STEP by UnaryOp()

internal class StepFunction : AlgebraicUnaryFunction {
    override val meta = STEP
    override fun doubleOp(value: Double) = if (value < 0.0) 0.0 else 1.0
    override operator fun invoke(value: ScalarExpr): ScalarExpr = AlgebraicUnaryScalarScalar(this, value)

    override fun differentiate(
        expr: ScalarExpr,
        exprd: ScalarExpr,
        variable: ScalarExpr
    ): ScalarExpr {
        return constant(0.0)
    }
}
