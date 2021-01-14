package com.github.jomof.kane.rigueur.functions

import com.github.jomof.kane.rigueur.*
import com.github.jomof.kane.rigueur.types.negativeZero
import com.github.jomof.kane.rigueur.types.one
import com.github.jomof.kane.rigueur.types.zero
import kotlin.math.pow

val POW by BinaryOp(precedence = 0)

private class PowFunction : AlgebraicBinaryScalarFunction {
    override val meta = POW
    override fun doubleOp(p1: Double, p2: Double) = p1.pow(p2)
    override fun floatOp(p1: Float, p2: Float) = p1.pow(p2)
    override fun intOp(p1: Int, p2: Int) = p1.toDouble().pow(p2.toDouble()).toInt()

    override fun <E : Number> reduceArithmetic(p1: ScalarExpr<E>, p2: ScalarExpr<E>): ScalarExpr<E>? {
        val leftConst = p1.tryFindConstant()
        val rightConst = p2.tryFindConstant()
        val result = when {
            leftConst == p1.type.one -> p1
            leftConst == p1.type.zero -> p1
            leftConst == p1.type.negativeZero -> p1
            rightConst == p2.type.one -> p1
            rightConst == p2.type.zero -> ConstantScalar(p1.type.one, p1.type)
            rightConst == p2.type.negativeZero -> ConstantScalar(p1.type.one, p1.type)
            leftConst != null && rightConst != null -> constant(invoke(leftConst, rightConst), p1.type)
            p1 is AlgebraicBinaryScalar && p1.op == pow ->
                pow(p1.left, p2 * p1.right)
            else -> null
        }
        return result
    }

    override fun <E : Number> differentiate(
        p1: ScalarExpr<E>,
        p1d: ScalarExpr<E>,
        p2: ScalarExpr<E>,
        p2d: ScalarExpr<E>,
        variable: ScalarExpr<E>
    ): ScalarExpr<E> {
        return p2 * pow(p1, p2 - p2.type.one) * p1d
    }
}

val pow : AlgebraicBinaryScalarFunction = PowFunction()