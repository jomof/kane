package com.github.jomof.kane.functions

import com.github.jomof.kane.*
import com.github.jomof.kane.types.one
import com.github.jomof.kane.types.two
import com.github.jomof.kane.types.zero

val DIV by BinaryOp(op = "/", precedence = 2, infix = true)

private class DivideFunction : AlgebraicBinaryScalarFunction {
    override val meta = DIV
    override fun doubleOp(p1: Double, p2: Double) = p1 / p2
    override fun floatOp(p1: Float, p2: Float) = p1 / p2
    override fun intOp(p1: Int, p2: Int) = p1 / p2

    override fun <E : Number> reduceArithmetic(p1: ScalarExpr<E>, p2: ScalarExpr<E>): ScalarExpr<E>? {
        if (p1 is NamedScalarVariable && p2 is NamedScalarVariable) return null
        val leftConst = p1.tryFindConstant()
        val rightConst = p2.tryFindConstant()
        val result = when {
            leftConst == p1.type.zero -> p1
            rightConst == p2.type.one -> p1
            leftConst != null && rightConst != null -> constant(invoke(leftConst, rightConst), p1.type)
            p1 is AlgebraicUnaryScalar && p1.op == negate && p2 is AlgebraicUnaryScalar && p2.op == negate -> p1.value / p2.value
            p2 is AlgebraicBinaryScalar && p2.op == pow -> p1 * pow(p2.left, -p2.right)
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
        val topLeft = p1d * p2
        val topRight = p1 * p2d
        val top = topLeft - topRight
        val bottom = pow(p2, p2.type.two)
        return top / bottom
    }
}

val divide : AlgebraicBinaryScalarFunction = DivideFunction()


// Div
operator fun <E:Number> ScalarExpr<E>.div(right : E) = divide(this, right)
operator fun <E:Number> E.div(right : ScalarExpr<E>) = divide(this, right)
operator fun <E:Number> ScalarExpr<E>.div(right : ScalarExpr<E>) = divide(this, right)
operator fun <E:Number> MatrixExpr<E>.div(right : E) = divide(this, right)
operator fun <E:Number> MatrixExpr<E>.div(right : ScalarExpr<E>) = divide(this, right)
operator fun <E:Number> E.div(right : MatrixExpr<E>) = divide(this, right)
operator fun <E:Number> ScalarExpr<E>.div(right : MatrixExpr<E>) = divide(this, right)
operator fun <E:Number> MatrixExpr<E>.div(right : MatrixExpr<E>) = divide(this, right)
operator fun <E:Number> ScalarExpr<E>.div(right : UntypedScalar) = divide(this, right)
operator fun <E:Number> UntypedScalar.div(right : ScalarExpr<E>) = divide(this, right)
operator fun <E:Number> UntypedScalar.div(right : E) = divide(this, right)
operator fun <E:Number> E.div(right : UntypedScalar) = divide(this, right)