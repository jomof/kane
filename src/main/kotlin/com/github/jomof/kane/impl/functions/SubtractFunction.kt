package com.github.jomof.kane.impl.functions

import com.github.jomof.kane.*
import com.github.jomof.kane.impl.*
import com.github.jomof.kane.minus
import com.github.jomof.kane.negate
import com.github.jomof.kane.plus

val MINUS by BinaryOp(op = "-", precedence = 4, infix = true)

private class SubtractFunction : AlgebraicBinaryFunction {
    override val meta = MINUS
    override fun doubleOp(p1: Double, p2: Double) = p1 - p2

    override fun reduceArithmetic(p1: ScalarExpr, p2: ScalarExpr): ScalarExpr? {
        if (p1 is NamedScalarVariable && p2 is NamedScalarVariable) return null
        val leftIsConst = p1.canGetConstant()
        val rightIsConst = p2.canGetConstant()
        return when {
            leftIsConst && p1.getConstant() == 0.0 -> -p2
            leftIsConst && p1.getConstant() == -0.0 -> -p2
            rightIsConst && p2.getConstant() == 0.0 -> p1
            rightIsConst && p2.getConstant() == -0.0 -> p1
            leftIsConst && rightIsConst -> constant(invoke(p1.getConstant(), p2.getConstant()))
            rightIsConst && p1 is AlgebraicBinaryScalarScalarScalar && p1.op == plus && p1.right is ConstantScalar -> {
                p1.left + (p1.right - p2.getConstant())
            }
            p2 is AlgebraicUnaryScalarScalar && p2.op == negate -> p1 + p2.value
            else ->
                null
        }
    }
    override fun differentiate(
        p1: ScalarExpr,
        p1d: ScalarExpr,
        p2: ScalarExpr,
        p2d: ScalarExpr,
        variable: ScalarExpr
    ) = p1d - p2d
}

val minus: AlgebraicBinaryFunction = SubtractFunction()
