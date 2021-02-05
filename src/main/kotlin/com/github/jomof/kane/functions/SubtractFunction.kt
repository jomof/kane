package com.github.jomof.kane.functions

import com.github.jomof.kane.*
import com.github.jomof.kane.sheet.SheetRange

val MINUS by BinaryOp(op = "-", precedence = 4, infix = true)

private class SubtractFunction : AlgebraicBinaryScalarFunction {
    override val meta = MINUS
    override fun doubleOp(p1: Double, p2: Double) = p1 - p2

    override fun reduceArithmetic(p1: ScalarExpr, p2: ScalarExpr): ScalarExpr? {
        if (p1 is NamedScalarVariable && p2 is NamedScalarVariable) return null
        val leftIsConst = p1.canGetConstant()
        val rightIsConst = p2.canGetConstant()
        return when {
            leftIsConst && p1.getConstant() == 0.0 -> -p2
            leftIsConst && p1.getConstant() == -0.0 -> -p2
            rightIsConst && p2.getConstant() == 0.0 -> p1
            rightIsConst && p2.getConstant() == -0.0 -> p1
            leftIsConst && rightIsConst -> constant(invoke(p1.getConstant(), p2.getConstant()))
            rightIsConst && p1 is AlgebraicBinaryScalar && p1.op == add && p1.right is ConstantScalar -> {
                p1.left + (p1.right - p2.getConstant())
            }
            p2 is AlgebraicUnaryScalar && p2.op == negate -> p1 + p2.value
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
    ) = p1d - p2d
}

val subtract : AlgebraicBinaryScalarFunction = SubtractFunction()

// Minus
operator fun <E : Number> ScalarExpr.minus(right: E) = subtract(this, right.toDouble())
operator fun <E : Number> E.minus(right: ScalarExpr) = subtract(this.toDouble(), right)
operator fun ScalarExpr.minus(right: ScalarExpr) = subtract(this, right)
operator fun <E : Number> MatrixExpr.minus(right: E) = subtract(this, right.toDouble())
operator fun MatrixExpr.minus(right: ScalarExpr) = subtract(this, right)
operator fun <E : Number> E.minus(right: MatrixExpr) = subtract(this.toDouble(), right)
operator fun ScalarExpr.minus(right: MatrixExpr) = subtract(this, right)
operator fun MatrixExpr.minus(right: MatrixExpr) = subtract(this, right)
operator fun ScalarExpr.minus(right: SheetRange) = subtract(this, right)
operator fun SheetRange.minus(right: ScalarExpr) = subtract(this, right)
operator fun <E : Number> SheetRange.minus(right: E) = subtract(this, right.toDouble())
operator fun <E : Number> E.minus(right: SheetRange) = subtract(this.toDouble(), right)
operator fun SheetRange.minus(right: SheetRange) = subtract(this, right)
operator fun MatrixExpr.minus(right: SheetRange) = subtract(this, right)