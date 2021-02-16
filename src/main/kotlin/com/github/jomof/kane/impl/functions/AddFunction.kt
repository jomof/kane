package com.github.jomof.kane.impl.functions

import com.github.jomof.kane.*
import com.github.jomof.kane.impl.*
import com.github.jomof.kane.plus

val PLUS by BinaryOp(op = "+", precedence = 3, associative = true, infix = true)

private class AddFunction : AlgebraicBinaryFunction {
    override val meta = PLUS
    override fun doubleOp(p1: Double, p2: Double) = p1 + p2
    override fun reduceArithmetic(p1: ScalarExpr, p2: ScalarExpr): ScalarExpr? {
        if (p1 is ScalarVariable && p2 is ScalarVariable) return null
        val leftIsConst = p1.canGetConstant()
        val rightIsConst = p2.canGetConstant()
        return when {
            leftIsConst && p1.getConstant() == 0.0 -> p2
            leftIsConst && p1.getConstant() == -0.0 -> p2
            rightIsConst && p2.getConstant() == 0.0 -> p1
            rightIsConst && p2.getConstant() == -0.0 -> p1
            leftIsConst && !rightIsConst -> p2 + p1
            leftIsConst && rightIsConst -> constant(p1.getConstant() + p2.getConstant())
            leftIsConst && p2 is AlgebraicBinaryScalarScalarScalar && p2.op == plus && p2.left is ConstantScalar && p2.right !is ConstantScalar -> {
                p2.right + (p1.getConstant() + p2.left)
            }
            leftIsConst && p2 is AlgebraicBinaryScalarScalarScalar && p2.op == plus && p2.right is ConstantScalar && p2.left !is ConstantScalar -> {
                p2.left + (p1.getConstant() + p2.right)
            }
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
    ) = p1d + p2d
}

val plus: AlgebraicBinaryFunction = AddFunction()


