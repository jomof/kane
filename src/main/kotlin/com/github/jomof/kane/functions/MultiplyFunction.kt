package com.github.jomof.kane.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.impl.*
import com.github.jomof.kane.impl.functions.AlgebraicBinaryScalar
import com.github.jomof.kane.impl.functions.AlgebraicBinaryScalarFunction
import com.github.jomof.kane.impl.functions.AlgebraicUnaryScalar
import com.github.jomof.kane.plus
import com.github.jomof.kane.pow
import com.github.jomof.kane.times

val TIMES by BinaryOp(op = "*", precedence = 1, associative = true, infix = true)

private class MultiplyFunction : AlgebraicBinaryScalarFunction {
    override val meta = TIMES
    override fun doubleOp(p1: Double, p2: Double) = p1 * p2

    override fun reduceArithmetic(p1: ScalarExpr, p2: ScalarExpr): ScalarExpr? {
        if (p1 is NamedScalarVariable && p2 is NamedScalarVariable) return null
        val leftIsConst = p1.canGetConstant()
        val rightIsConst = p2.canGetConstant()

        return when {
            p1 == p2 && p1 is AlgebraicUnaryScalar && p1.op == negate -> pow(p1.value, 2.0)
            p1 == p2 -> pow(p1, 2.0)
            rightIsConst && p2.getConstant() == 0.0 -> p2
            rightIsConst && p2.getConstant() == -0.0 -> p2
            rightIsConst && p2.getConstant() == 1.0 -> p1
            leftIsConst && p1.getConstant() == 0.0 -> p1
            leftIsConst && p1.getConstant() == -0.0 -> p1
            leftIsConst && p1.getConstant() == 1.0 -> p2
            leftIsConst && p1.getConstant() == -1.0 -> -p2
            rightIsConst && p2.getConstant() == -1.0 -> -p1
            leftIsConst && rightIsConst -> constant(p1.getConstant() * p2.getConstant())
            p1 is AlgebraicUnaryScalar && p1.op == negate -> {
                // -(x*y)*z -> -(x*y*z)
                -(p1.value * p2)
            }
            p2 is AlgebraicUnaryScalar && p2.op == negate -> {
                // x*(-(y*x)) -> -(x*y*z)
                -(p1 * p2.value)
            }
            p1 is AlgebraicBinaryScalar && p1.op == pow &&
                    p2 is AlgebraicBinaryScalar && p2.op == pow &&
                    p1.left == p2.left ->
                pow(p1.left, p1.right + p2.right) // x^5 * x^4 => x^9
            p1 is AlgebraicBinaryScalar && p1.op == multiply && p1.right == p2 -> p1.left * pow(p2, 2.0)
            p1 is AlgebraicBinaryScalar && p1.op == multiply && p1.left == p2 -> p1.right * pow(p2, 2.0)
            leftIsConst && p2 is AlgebraicBinaryScalar && p2.op == multiply && p2.left is ConstantScalar -> {
                // 4*3*y -> 12*y
                (p1 * p2.left) * p2.right
            }
            p2 is AlgebraicBinaryScalar && p2.op == multiply && p2.left is ConstantScalar && p2.right !is ConstantScalar -> {
                // x*(3*y) -> 3*(x*y)
                p2.left * p1 * p2.right
            }

            p1 is AlgebraicBinaryScalar && p1.op == multiply && p1.left is ConstantScalar && p2 !is ConstantScalar -> {
                // (24*x)*y -> 24*(x*y)
                p1.left * (p1.right * p2)
            }
            !leftIsConst && rightIsConst -> p2 * p1
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
        return p1 * p2d + p1d * p2
    }
}

val multiply : AlgebraicBinaryScalarFunction = MultiplyFunction()

