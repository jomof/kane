package com.github.jomof.kane.functions

import com.github.jomof.kane.*

val PLUS by BinaryOp(op = "+", precedence = 3, associative = true, infix = true)
private class AddFunction : AlgebraicBinaryScalarFunction {
    override val meta = PLUS
    override fun doubleOp(p1: Double, p2: Double) = p1 + p2
    override fun reduceArithmetic(p1: ScalarExpr, p2: ScalarExpr): ScalarExpr? {
        if (p1 is NamedScalarVariable && p2 is NamedScalarVariable) return null
        val leftConst = p1.tryFindConstant()
        val rightConst = p2.tryFindConstant()
        return when {
            leftConst == 0.0 -> p2
            leftConst == -0.0 -> p2
            rightConst == 0.0 -> p1
            rightConst == -0.0 -> p1
            leftConst != null && rightConst == null -> p2 + p1
            leftConst != null && rightConst != null -> constant(invoke(leftConst, rightConst), type(p1.type, p2.type))
            leftConst != null && p2 is AlgebraicBinaryScalar && p2.op == add && p2.left is ConstantScalar && p2.right !is ConstantScalar -> {
                p2.right + (leftConst + p2.left)
            }
            leftConst != null && p2 is AlgebraicBinaryScalar && p2.op == add && p2.right is ConstantScalar && p2.left !is ConstantScalar -> {
                p2.left + (leftConst + p2.right)
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

val add : AlgebraicBinaryScalarFunction = AddFunction()

// Plus
operator fun <E:Number> ScalarExpr.plus(right : E) = add(this, right.toDouble())
operator fun <E:Number> E.plus(right : ScalarExpr) = add(this.toDouble(), right)
operator fun ScalarExpr.plus(right : ScalarExpr) = add(this, right)
operator fun <E:Number> MatrixExpr.plus(right : E) = add(this, right.toDouble())
operator fun MatrixExpr.plus(right : ScalarExpr) = add(this, right)
operator fun <E:Number> E.plus(right : MatrixExpr) = add(this.toDouble(), right)
operator fun ScalarExpr.plus(right : MatrixExpr) = add(this, right)
operator fun MatrixExpr.plus(right : MatrixExpr) = add(this, right)
operator fun ScalarExpr.plus(right : UntypedScalar) = add(this, right)
operator fun UntypedScalar.plus(right : ScalarExpr) = add(this, right)
operator fun <E:Number> UntypedScalar.plus(right : E) = add(this, right.toDouble())
operator fun <E:Number> E.plus(right : UntypedScalar) = add(this.toDouble(), right)
operator fun UntypedScalar.plus(right : UntypedScalar) = add(this, right)
operator fun MatrixExpr.plus(right : UntypedScalar) = add(this, right)
