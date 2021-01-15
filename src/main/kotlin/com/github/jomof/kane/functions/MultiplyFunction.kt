package com.github.jomof.kane.functions

import com.github.jomof.kane.*
import com.github.jomof.kane.types.*

val TIMES by BinaryOp(op = "*", precedence = 1, associative = true, infix = true)



private class MultiplyFunction : AlgebraicBinaryScalarFunction {
    override val meta = TIMES
    override fun doubleOp(p1: Double, p2: Double) = p1 * p2
    override fun floatOp(p1: Float, p2: Float) = p1 * p2
    override fun intOp(p1: Int, p2: Int) = p1 * p2

//    private fun <E:Number> coagulateConstantInIsland(left : ScalarExpr<E>, right : ScalarExpr<E>) : Pair<E?, ScalarExpr<E>?> {
//        val leftConst = left.tryFindConstant()
//        val rightConst = right.tryFindConstant()
//        return when {
//            leftConst != null && rightConst != null -> invoke(leftConst, rightConst) to null
//            leftConst != null && right is AlgebraicBinaryScalar && right.op == this -> {
//                val (constant, expr) = coagulateConstantInIsland(right.left, right.right)
//                when {
//                    constant != null -> invoke(leftConst, constant) to expr
//                    else -> leftConst to expr
//                }
//
//            }
//            rightConst != null && left is AlgebraicBinaryScalar && left.op == this -> {
//                val (constant, expr) = coagulateConstantInIsland(left.left, left.right)
//                when {
//                    constant != null -> invoke(rightConst, constant) to expr
//                    else -> rightConst to expr
//                }
//            }
//            else -> null to null
//        }
//    }


    override fun <E : Number> reduceArithmetic(p1: ScalarExpr<E>, p2: ScalarExpr<E>): ScalarExpr<E>? {
        if (p1 is NamedScalarVariable && p2 is NamedScalarVariable) return null
        val leftConst = p1.tryFindConstant()
        val rightConst = p2.tryFindConstant()
        val result = when {
            p1 == p2 && p1 is AlgebraicUnaryScalar && p1.op == negate-> pow(p1.value, p1.type.two)
            p1 == p2 -> pow(p1, p1.type.two)
            rightConst == p2.type.zero -> p2
            rightConst == p2.type.negativeZero -> p2
            rightConst == p2.type.one -> p1
            leftConst == p1.type.zero -> p1
            leftConst == p1.type.negativeZero -> p1
            leftConst == p1.type.one -> p2
            leftConst == p1.type.negativeOne -> -p2
            rightConst == p2.type.negativeOne -> -p1
            leftConst != null && rightConst != null -> when {
                leftConst is Double && rightConst is Double -> constant(leftConst * rightConst, p1.type)
                else -> constant(invoke(leftConst, rightConst), p1.type)
            } as ScalarExpr<E>
            p1 is AlgebraicUnaryScalar && p1.op == negate -> {
                // -(x*y)*z -> -(x*y*z)
                -(p1.value * p2)
            }
            p2 is AlgebraicUnaryScalar && p2.op == negate -> {
                // x*(-(y*x)) -> -(x*y*z)
                -(p1 * p2.value)
            }
            p1 is AlgebraicBinaryScalar && p1.op == pow &&
                p2 is AlgebraicBinaryScalar && p2.op == pow &&
                p1.left == p2.left ->
                pow(p1.left, p1.right + p2.right) // x^5 * x^4 => x^9
            p1 is AlgebraicBinaryScalar && p1.op == multiply && p1.right == p2 -> p1.left * pow(p2, p2.type.two)
            p1 is AlgebraicBinaryScalar && p1.op == multiply && p1.left == p2 -> p1.right * pow(p2, p2.type.two)
            leftConst != null && p2 is AlgebraicBinaryScalar && p2.op == multiply && p2.left is ConstantScalar -> {
                // 4*3*y -> 12*y
                p1 * p2.left.value * p2.right
            }
            p2 is AlgebraicBinaryScalar && p2.op == multiply && p2.left is ConstantScalar && p2.right !is ConstantScalar -> {
                // x*(3*y) -> 3*(x*y)
                p2.left * p1 * p2.right
            }

            p1 is AlgebraicBinaryScalar && p1.op == multiply && p1.left is ConstantScalar && p2 !is ConstantScalar -> {
                // (24*x)*y -> 24*(x*y)
                p1.left * (p1.right * p2)
            }
            leftConst == null && rightConst != null -> p2 * p1
            else -> {
                null
//                val (constant, expr) = coagulateConstantInIsland(p1, p2)
//                when {
//                    constant == null && expr == null ->
//                        null
//                    constant != null && expr != null ->
//                        constant * expr
//                    constant != null ->
//                        constant(constant, p1.type)
//                    expr != null ->
//                        expr
//                    else -> error("")
//                }
            }
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
        return p1 * p2d + p1d * p2
    }
}

val multiply : AlgebraicBinaryScalarFunction = MultiplyFunction()

// Times
operator fun <E:Number> ScalarExpr<E>.times(right : E) = multiply(this, right)
operator fun <E:Number> E.times(right : ScalarExpr<E>) = multiply(this, right)
operator fun <E:Number> ScalarExpr<E>.times(right : ScalarExpr<E>) = multiply(this, right)
operator fun <E:Number> MatrixExpr<E>.times(right : E) = multiply(this, right)
operator fun <E:Number> MatrixExpr<E>.times(right : ScalarExpr<E>) = multiply(this, right)
operator fun <E:Number> E.times(right : MatrixExpr<E>) = multiply(this, right)
operator fun <E:Number> ScalarExpr<E>.times(right : MatrixExpr<E>) = multiply(this, right)
operator fun <E:Number> MatrixExpr<E>.times(right : MatrixExpr<E>) = multiply(this, right)
operator fun <E:Number> ScalarExpr<E>.times(right : UntypedScalar) = multiply(this, right)
operator fun <E:Number> UntypedScalar.times(right : ScalarExpr<E>) = multiply(this, right)
operator fun <E:Number> UntypedScalar.times(right : E) = multiply(this, right)
operator fun <E:Number> E.times(right : UntypedScalar) = multiply(this, right)