package com.github.jomof.kane.functions

import com.github.jomof.kane.*

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


// Div
operator fun <E:Number> ScalarExpr.div(right : E) = divide(this, right.toDouble())
operator fun <E:Number> E.div(right : ScalarExpr) = divide(this.toDouble(), right)
operator fun ScalarExpr.div(right : ScalarExpr) = divide(this, right)
operator fun <E:Number> MatrixExpr.div(right : E) = divide(this, right.toDouble())
operator fun MatrixExpr.div(right : ScalarExpr) = divide(this, right)
operator fun <E:Number> E.div(right : MatrixExpr) = divide(this.toDouble(), right)
operator fun ScalarExpr.div(right : MatrixExpr) = divide(this, right)
operator fun MatrixExpr.div(right : MatrixExpr) = divide(this, right)
operator fun ScalarExpr.div(right : UntypedScalar) = divide(this, right)
operator fun UntypedScalar.div(right : ScalarExpr) = divide(this, right)
operator fun <E:Number> UntypedScalar.div(right : E) = divide(this, right.toDouble())
operator fun <E:Number> E.div(right : UntypedScalar) = divide(this.toDouble(), right)
operator fun UntypedScalar.div(right : UntypedScalar) = divide(this, right)
operator fun MatrixExpr.div(right : UntypedScalar) = divide(this, right)