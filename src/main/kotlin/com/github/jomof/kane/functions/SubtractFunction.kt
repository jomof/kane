package com.github.jomof.kane.functions

import com.github.jomof.kane.*
import com.github.jomof.kane.types.negativeZero
import com.github.jomof.kane.types.zero

val MINUS by BinaryOp(op = "-", precedence = 4, infix = true)

private class SubtractFunction : AlgebraicBinaryScalarFunction {
    override val meta = MINUS
    override fun doubleOp(p1: Double, p2: Double) = p1 - p2
    override fun floatOp(p1: Float, p2: Float) = p1 - p2
    override fun intOp(p1: Int, p2: Int) = p1 - p2

    override fun <E : Number> reduceArithmetic(p1: ScalarExpr<E>, p2: ScalarExpr<E>): ScalarExpr<E>? {
        if (p1 is NamedScalarVariable && p2 is NamedScalarVariable) return null
        val leftConst = p1.tryFindConstant()
        val rightConst = p2.tryFindConstant()
        val result = when {
            leftConst == p1.type.zero -> -p2
            leftConst == p1.type.negativeZero -> -p2
            rightConst == p2.type.zero -> p1
            rightConst == p2.type.negativeZero -> p1
            leftConst != null && rightConst != null -> constant(invoke(leftConst, rightConst), p1.type)
            rightConst != null && p1 is AlgebraicBinaryScalar && p1.op == add && p1.right is ConstantScalar -> {
                p1.left + (p1.right - rightConst)
            }
            p2 is AlgebraicUnaryScalar && p2.op == negate -> p1 + p2.value
            else ->
                null
        }
        return result
    }
    override fun <E : Number> differentiate(
        p1: ScalarExpr<E>,
        p1d: ScalarExpr<E>,
        p2: ScalarExpr<E>,
        p2d: ScalarExpr<E>,
        variable: ScalarExpr<E>
    ) = p1d - p2d
}

val subtract : AlgebraicBinaryScalarFunction = SubtractFunction()

// Minus
operator fun <E:Number> ScalarExpr<E>.minus(right : E) = subtract(this, right)
operator fun <E:Number> E.minus(right : ScalarExpr<E>) = subtract(this, right)
operator fun <E:Number> ScalarExpr<E>.minus(right : ScalarExpr<E>) = subtract(this, right)
operator fun <E:Number> MatrixExpr<E>.minus(right : E) = subtract(this, right)
operator fun <E:Number> MatrixExpr<E>.minus(right : ScalarExpr<E>) = subtract(this, right)
operator fun <E:Number> E.minus(right : MatrixExpr<E>) = subtract(this, right)
operator fun <E:Number> ScalarExpr<E>.minus(right : MatrixExpr<E>) = subtract(this, right)
operator fun <E:Number> MatrixExpr<E>.minus(right : MatrixExpr<E>) = subtract(this, right)
operator fun <E:Number> ScalarExpr<E>.minus(right : UntypedScalar) = subtract(this, right)
operator fun <E:Number> UntypedScalar.minus(right : ScalarExpr<E>) = subtract(this, right)
operator fun <E:Number> UntypedScalar.minus(right : E) = subtract(this, right)
operator fun <E:Number> E.minus(right : UntypedScalar) = subtract(this, right)