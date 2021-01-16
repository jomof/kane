package com.github.jomof.kane.functions

import com.github.jomof.kane.*

val MINUS by BinaryOp(op = "-", precedence = 4, infix = true)

private class SubtractFunction : AlgebraicBinaryScalarFunction {
    override val meta = MINUS
    override fun doubleOp(p1: Double, p2: Double) = p1 - p2

    override fun reduceArithmetic(p1: ScalarExpr, p2: ScalarExpr): ScalarExpr? {
        if (p1 is NamedScalarVariable && p2 is NamedScalarVariable) return null
        val leftConst = p1.tryFindConstant()
        val rightConst = p2.tryFindConstant()
        val result = when {
            leftConst == 0.0 -> -p2
            leftConst == -0.0 -> -p2
            rightConst == 0.0 -> p1
            rightConst == -0.0 -> p1
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
    override fun differentiate(
        p1: ScalarExpr,
        p1d: ScalarExpr,
        p2: ScalarExpr,
        p2d: ScalarExpr,
        variable: ScalarExpr
    ) = p1d - p2d
}

val subtract : AlgebraicBinaryScalarFunction = SubtractFunction()

// Minus
operator fun <E:Number> ScalarExpr.minus(right : E) = subtract(this, right.toDouble())
operator fun <E:Number> E.minus(right : ScalarExpr) = subtract(this.toDouble(), right)
operator fun ScalarExpr.minus(right : ScalarExpr) = subtract(this, right)
operator fun <E:Number> MatrixExpr.minus(right : E) = subtract(this, right.toDouble())
operator fun MatrixExpr.minus(right : ScalarExpr) = subtract(this, right)
operator fun <E:Number> E.minus(right : MatrixExpr) = subtract(this.toDouble(), right)
operator fun ScalarExpr.minus(right : MatrixExpr) = subtract(this, right)
operator fun MatrixExpr.minus(right : MatrixExpr) = subtract(this, right)
operator fun ScalarExpr.minus(right : UntypedScalar) = subtract(this, right)
operator fun UntypedScalar.minus(right : ScalarExpr) = subtract(this, right)
operator fun <E:Number> UntypedScalar.minus(right : E) = subtract(this, right.toDouble())
operator fun <E:Number> E.minus(right : UntypedScalar) = subtract(this.toDouble(), right)