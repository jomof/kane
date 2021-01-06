package com.github.jomof.kane.rigueur.functions

import com.github.jomof.kane.rigueur.*
import com.github.jomof.kane.rigueur.types.*

val PLUS by BinaryOp(op = "+", precedence = 3, associative = true, infix = true)
private class AddFunction : AlgebraicBinaryScalarFunction {
    override val meta = PLUS
    override fun doubleOp(p1: Double, p2: Double) = p1 + p2
    override fun floatOp(p1: Float, p2: Float) = p1 + p2
    override fun intOp(p1: Int, p2: Int) = p1 + p2
    override fun <E : Number> reduceArithmetic(p1: ScalarExpr<E>, p2: ScalarExpr<E>): ScalarExpr<E>? {
        if (p1 is NamedScalarVariable && p2 is NamedScalarVariable) return null
        val leftConst = p1.tryFindConstant()
        val rightConst = p2.tryFindConstant()
        val result = when {
            leftConst == p1.type.zero -> p2
            leftConst == p1.type.negativeZero -> p2
            rightConst == p2.type.zero -> p1
            rightConst == p2.type.negativeZero -> p1
            leftConst != null && rightConst == null -> p2 + p1
            leftConst != null && p2 is AlgebraicBinaryScalar && p2.op == add && p2.left is ConstantScalar && p2.right !is ConstantScalar -> {
                p2.right + (leftConst + p2.left)
            }
            leftConst != null && p2 is AlgebraicBinaryScalar && p2.op == add && p2.right is ConstantScalar && p2.left !is ConstantScalar -> {
                p2.left + (leftConst + p2.right)
            }
            else ->
                null
        }
        assert(result != p1*p2) {
            "Should be null"
        }
        return result
    }
    override fun <E : Number> differentiate(
        p1: ScalarExpr<E>,
        p1d: ScalarExpr<E>,
        p2: ScalarExpr<E>,
        p2d: ScalarExpr<E>,
        variable: ScalarExpr<E>
    ) = p1d + p2d
}

val add : AlgebraicBinaryScalarFunction = AddFunction()

// Plus
operator fun <E:Number> ScalarExpr<E>.plus(right : E) = add(this, right)
operator fun <E:Number> E.plus(right : ScalarExpr<E>) = add(this, right)
operator fun <E:Number> ScalarExpr<E>.plus(right : ScalarExpr<E>) = add(this, right)
operator fun <E:Number> MatrixExpr<E>.plus(right : E) = add(this, right)
operator fun <E:Number> MatrixExpr<E>.plus(right : ScalarExpr<E>) = add(this, right)
operator fun <E:Number> E.plus(right : MatrixExpr<E>) = add(this, right)
operator fun <E:Number> ScalarExpr<E>.plus(right : MatrixExpr<E>) = add(this, right)
operator fun <E:Number> MatrixExpr<E>.plus(right : MatrixExpr<E>) = add(this, right)
operator fun <E:Number> ScalarExpr<E>.plus(right : UntypedScalar) = add(this, right)
operator fun <E:Number> UntypedScalar.plus(right : ScalarExpr<E>) = add(this, right)
operator fun <E:Number> UntypedScalar.plus(right : E) = add(this, right)
operator fun <E:Number> E.plus(right : UntypedScalar) = add(this, right)