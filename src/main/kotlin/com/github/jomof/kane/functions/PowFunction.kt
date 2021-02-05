package com.github.jomof.kane.functions

import com.github.jomof.kane.impl.*
import kotlin.math.pow

val POW by BinaryOp(precedence = 0)

private class PowFunction : AlgebraicBinaryScalarFunction {
    override val meta = POW
    override fun doubleOp(p1: Double, p2: Double) = p1.pow(p2)

    override fun reduceArithmetic(p1: ScalarExpr, p2: ScalarExpr): ScalarExpr? {
        val leftIsConst = p1.canGetConstant()
        val rightIsConst = p2.canGetConstant()
        return when {
            leftIsConst && p1.getConstant() == 1.0 -> p1
            leftIsConst && p1.getConstant() == 0.0 -> p1
            leftIsConst && p1.getConstant() == -0.0 -> p1
            rightIsConst && p2.getConstant() == 1.0 -> p1
            rightIsConst && p2.getConstant() == 0.0 -> ConstantScalar(1.0)
            rightIsConst && p2.getConstant() == -0.0 -> ConstantScalar(1.0)
            leftIsConst && rightIsConst -> constant(invoke(p1.getConstant(), p2.getConstant()))
            p1 is AlgebraicBinaryScalar && p1.op == pow ->
                pow(p1.left, p2 * p1.right)
            else -> null
        }
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