package com.github.jomof.kane.rigueur.functions

import com.github.jomof.kane.rigueur.*
import com.github.jomof.kane.rigueur.types.kaneType
import kotlin.math.pow

// f(scalar,scalar)->scalar
interface AlgebraicBinaryScalarFunction {
    val meta : BinaryOp
    operator fun <E:Number> invoke(p1 : E, p2 : E) : E = when(p1) {
        is Double -> doubleOp(p1, p2 as Double)
        is Float -> floatOp(p1, p2 as Float)
        else -> error("${p1.javaClass}")
    } as E
    operator fun invoke(p1 : Double, p2 : Double) = doubleOp(p1, p2)
    operator fun invoke(p1 : Float, p2 : Float) = floatOp(p1, p2)
    fun doubleOp(p1 : Double, p2 : Double) : Double
    fun floatOp(p1 : Float, p2 : Float) : Float
    operator fun <E:Number> invoke(p1 : ScalarExpr<E>, p2 : ScalarExpr<E>) : ScalarExpr<E> =
        AlgebraicBinaryScalar(pow, p1, p2)
    operator fun <E:Number> invoke(p1 : UntypedScalar, p2 : ScalarExpr<E>) : ScalarExpr<E> =
        AlgebraicBinaryScalar(pow, CoerceScalar(p1, p2.type), p2)
    operator fun <E:Number> invoke(p1 : UntypedScalar, p2 : E) : ScalarExpr<E> =
        AlgebraicBinaryScalar(pow, CoerceScalar(p1, p2.javaClass.kaneType), constant(p2))
    operator fun <E:Number> invoke(p1 : MatrixExpr<E>, p2 : ScalarExpr<E>) : MatrixExpr<E> =
        AlgebraicBinaryMatrixScalar(this, p1.columns, p1.rows, p1, p2)
    operator fun <E:Number> invoke(p1 : MatrixExpr<E>, p2 : E) : MatrixExpr<E> =
        AlgebraicBinaryMatrixScalar(this, p1.columns, p1.rows, p1, constant(p2))
    operator fun <E:Number> invoke(p1 : E, p2 : ScalarExpr<E>) : ScalarExpr<E> = invoke(constant(p1), p2)
    operator fun <E:Number> invoke(p1 : ScalarExpr<E>, p2 : E) : ScalarExpr<E> = invoke(p1, constant(p2))
    fun render(p1 : Expr, p2 : Expr) : String
    fun <E:Number> reduceArithmetic(p1 : ScalarExpr<E>, p2 : ScalarExpr<E>) : ScalarExpr<E>?
    fun <E:Number> differentiate(
        p1 : ScalarExpr<E>,
        p1d : ScalarExpr<E>,
        p2 : ScalarExpr<E>,
        p2d : ScalarExpr<E>,
        variable : ScalarExpr<E>) : ScalarExpr<E>
}


data class AlgebraicBinaryScalar<E:Number>(
    val op : AlgebraicBinaryScalarFunction,
    val left : ScalarExpr<E>,
    val right : ScalarExpr<E>) : ScalarExpr<E>, ParentExpr<E> {
    init { track() }
    override val type get() = left.type
    override val children get() = listOf(left, right)
    override fun toString() = op.render(left, right)
    override fun mapChildren(f: ExprFunction) = copy(left = f(left), right = f(right))
}

data class AlgebraicBinaryMatrixScalar<E:Number>(
    val op : AlgebraicBinaryScalarFunction,
    override val columns: Int,
    override val rows: Int,
    val left : MatrixExpr<E>,
    val right : ScalarExpr<E>) : MatrixExpr<E> {
    init { track() }
    override val type get() = left.type
    override fun get(column: Int, row: Int) = AlgebraicBinaryScalar(op, left[column, row], right)
    override fun toString() = op.render(left, right)
    override fun mapChildren(f: ExprFunction) = copy(left = f(left), right = f(right))
}

private class PowFunction : AlgebraicBinaryScalarFunction {
    override val meta = POW
    override fun doubleOp(p1: Double, p2: Double) = p1.pow(p2)
    override fun floatOp(p1: Float, p2: Float) = p1.pow(p2)
    override fun render(p1: Expr, p2: Expr): String {
        val p1binop = p1.tryGetBinaryOp()
        val p2binop = p2.tryGetBinaryOp()
        val p1Parens = requiresParens(meta, p1binop, childIsRight = false)
        val p2Parens = requiresParens(meta, p2binop, childIsRight = true)
        val p1p = if (p1Parens) "($p1)" else "$p1"
        val p2p = if (p2Parens) "($p2)" else "$p2"
        val supe = tryConvertToSuperscript(p2p) ?: return "pow($p1, $p2)"
        return "$p1p$supe"
    }

    override fun <E : Number> reduceArithmetic(p1: ScalarExpr<E>, p2: ScalarExpr<E>): ScalarExpr<E>? {
        val leftConst = p1.tryFindConstant()
        val rightConst = p2.tryFindConstant()
        return when {
            leftConst != null && rightConst != null -> ConstantScalar(pow(leftConst, rightConst), p1.type)
            leftConst == p1.type.one -> p1
            leftConst == p1.type.zero -> p1
            rightConst == p2.type.one -> p1
            rightConst == p2.type.zero -> ConstantScalar(p1.type.one, p1.type)
            p1 is AlgebraicBinaryScalar && p1.op == pow -> pow(p1.left, p2 * p1.right)
            else -> null
        }
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