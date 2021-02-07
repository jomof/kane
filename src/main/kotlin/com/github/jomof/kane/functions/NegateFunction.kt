package com.github.jomof.kane.functions

import com.github.jomof.kane.MatrixExpr
import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.impl.ConstantScalar
import com.github.jomof.kane.impl.ScalarListExpr
import com.github.jomof.kane.impl.UnaryOp
import com.github.jomof.kane.impl.functions.AlgebraicUnaryScalar
import com.github.jomof.kane.impl.functions.AlgebraicUnaryScalarFunction
import com.github.jomof.kane.negate


private val NEGATE by UnaryOp(op = "-")

internal class NegateFunction : AlgebraicUnaryScalarFunction {
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


// Unary minus
operator fun ScalarExpr.unaryMinus() = negate(this)
operator fun MatrixExpr.unaryMinus() = negate(this)
