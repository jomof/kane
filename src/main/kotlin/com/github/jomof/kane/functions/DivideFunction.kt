package com.github.jomof.kane.functions

import com.github.jomof.kane.div
import com.github.jomof.kane.impl.*
import com.github.jomof.kane.impl.functions.AlgebraicBinaryScalar
import com.github.jomof.kane.impl.functions.AlgebraicBinaryScalarFunction
import com.github.jomof.kane.impl.functions.AlgebraicUnaryScalar
import com.github.jomof.kane.minus
import com.github.jomof.kane.pow
import com.github.jomof.kane.times

val DIV by BinaryOp(op = "/", precedence = 2, infix = true)

private class DivideFunction : AlgebraicBinaryScalarFunction {
    override val meta = DIV
    override fun doubleOp(p1: Double, p2: Double) = p1 / p2

    override fun reduceArithmetic(p1: ScalarExpr, p2: ScalarExpr): ScalarExpr? {
        if (p1 is NamedScalarVariable && p2 is NamedScalarVariable) return null
        val leftIsConst = p1.canGetConstant()
        val rightIsConst = p2.canGetConstant()
        return when {
            leftIsConst && p1.getConstant() == 0.0 -> p1
            rightIsConst && p2.getConstant() == 1.0 -> p1
            leftIsConst && rightIsConst -> constant(invoke(p1.getConstant(), p2.getConstant()))
            p1 is AlgebraicUnaryScalar && p1.op == negate && p2 is AlgebraicUnaryScalar && p2.op == negate -> p1.value / p2.value
            p2 is AlgebraicBinaryScalar && p2.op == pow -> p1 * pow(p2.left, -p2.right)
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
        val topLeft = p1d * p2
        val topRight = p1 * p2d
        val top = topLeft - topRight
        val bottom = pow(p2, 2.0)
        return top / bottom
    }
}

val divide : AlgebraicBinaryScalarFunction = DivideFunction()