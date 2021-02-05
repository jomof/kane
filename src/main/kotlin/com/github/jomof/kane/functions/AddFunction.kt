package com.github.jomof.kane.functions

import com.github.jomof.kane.*
import com.github.jomof.kane.sheet.SheetRange

val PLUS by BinaryOp(op = "+", precedence = 3, associative = true, infix = true)
private class AddFunction : AlgebraicBinaryScalarFunction {
    override val meta = PLUS
    override fun doubleOp(p1: Double, p2: Double) = p1 + p2
    override fun reduceArithmetic(p1: ScalarExpr, p2: ScalarExpr): ScalarExpr? {
        if (p1 is NamedScalarVariable && p2 is NamedScalarVariable) return null
        val leftIsConst = p1.canGetConstant()
        val rightIsConst = p2.canGetConstant()
        return when {
            leftIsConst && p1.getConstant() == 0.0 -> p2
            leftIsConst && p1.getConstant() == -0.0 -> p2
            rightIsConst && p2.getConstant() == 0.0 -> p1
            rightIsConst && p2.getConstant() == -0.0 -> p1
            leftIsConst && !rightIsConst -> p2 + p1
            leftIsConst && rightIsConst -> constant(invoke(p1.getConstant(), p2.getConstant()))
            leftIsConst && p2 is AlgebraicBinaryScalar && p2.op == add && p2.left is ConstantScalar && p2.right !is ConstantScalar -> {
                p2.right + (p1.getConstant() + p2.left)
            }
            leftIsConst && p2 is AlgebraicBinaryScalar && p2.op == add && p2.right is ConstantScalar && p2.left !is ConstantScalar -> {
                p2.left + (p1.getConstant() + p2.right)
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
operator fun <E : Number> ScalarExpr.plus(right: E) = add(this, right.toDouble())
operator fun <E : Number> E.plus(right: ScalarExpr) = add(this.toDouble(), right)
operator fun ScalarExpr.plus(right: ScalarExpr) = add(this, right)
operator fun <E : Number> MatrixExpr.plus(right: E) = add(this, right.toDouble())
operator fun MatrixExpr.plus(right: ScalarExpr) = add(this, right)
operator fun <E : Number> E.plus(right: MatrixExpr) = add(this.toDouble(), right)
operator fun ScalarExpr.plus(right: MatrixExpr) = add(this, right)
operator fun MatrixExpr.plus(right: MatrixExpr) = add(this, right)
operator fun ScalarExpr.plus(right: SheetRange) = add(this, right)
operator fun SheetRange.plus(right: ScalarExpr) = add(this, right)
operator fun <E : Number> SheetRange.plus(right: E) = add(this, right.toDouble())
operator fun <E : Number> E.plus(right: SheetRange) = add(this.toDouble(), right)
operator fun SheetRange.plus(right: SheetRange) = add(this, right)
operator fun MatrixExpr.plus(right: SheetRange) = add(this, right)
