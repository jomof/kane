package com.github.jomof.kane.functions

import com.github.jomof.kane.*
import kotlin.math.pow

val POW by BinaryOp(precedence = 0)

private class PowFunction : AlgebraicBinaryScalarFunction {
    override val meta = POW
    override fun doubleOp(p1: Double, p2: Double) = p1.pow(p2)

    override fun reduceArithmetic(p1: ScalarExpr, p2: ScalarExpr): ScalarExpr? {
        val leftConst = p1.tryFindConstant()
        val rightConst = p2.tryFindConstant()
        val result = when {
            leftConst == 1.0 -> p1
            leftConst == 0.0 -> p1
            leftConst == -0.0 -> p1
            rightConst == 1.0-> p1
            rightConst == 0.0 -> ConstantScalar(1.0, p1.type)
            rightConst == -0.0 -> ConstantScalar(1.0, p1.type)
            leftConst != null && rightConst != null -> constant(invoke(leftConst, rightConst), p1.type)
            p1 is AlgebraicBinaryScalar && p1.op == pow ->
                pow(p1.left, p2 * p1.right)
            else -> null
        }
        return result
    }

    override fun differentiate(
        p1: ScalarExpr,
        p1d: ScalarExpr,
        p2: ScalarExpr,
        p2d: ScalarExpr,
        variable: ScalarExpr
    ): ScalarExpr {
        return p2 * pow(p1, p2 - 1.0) * p1d
    }
}

val pow : AlgebraicBinaryScalarFunction = PowFunction()