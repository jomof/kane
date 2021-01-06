package com.github.jomof.kane.rigueur.functions

import com.github.jomof.kane.rigueur.*
import com.github.jomof.kane.rigueur.types.kaneType

// f(scalar,scalar)->scalar (extended to matrix 1<->1 mapping)
interface AlgebraicBinaryScalarFunction {
    val meta : BinaryOp
    operator fun <E:Number> invoke(p1 : E, p2 : E) : E = when(p1) {
        is Double -> doubleOp(p1, (p2 as Number).toDouble())
        is Float -> floatOp(p1, (p2 as Number).toFloat())
        is Int -> intOp(p1, (p2 as Number).toInt())
        else -> error("${p1.javaClass}")
    } as E
    operator fun invoke(p1 : Double, p2 : Double) = doubleOp(p1, p2)
    operator fun invoke(p1 : Float, p2 : Float) = floatOp(p1, p2)
    fun doubleOp(p1 : Double, p2 : Double) : Double
    fun floatOp(p1 : Float, p2 : Float) : Float
    fun intOp(p1 : Int, p2 : Int) : Int
    operator fun <E:Number> invoke(p1 : ScalarExpr<E>, p2 : ScalarExpr<E>) : ScalarExpr<E> =
        AlgebraicBinaryScalar(this, p1, p2)
    operator fun <E:Number> invoke(p1 : UntypedScalar, p2 : ScalarExpr<E>) : ScalarExpr<E> =
        AlgebraicBinaryScalar(this, CoerceScalar(p1, p2.type), p2)
    operator fun <E:Number> invoke(p1 : ScalarExpr<E>, p2 : UntypedScalar) : ScalarExpr<E> =
        AlgebraicBinaryScalar(this, p1, CoerceScalar(p2, p1.type))
    operator fun <E:Number> invoke(p1 : UntypedScalar, p2 : E) : ScalarExpr<E> =
        AlgebraicBinaryScalar(this, CoerceScalar(p1, p2.javaClass.kaneType), constant(p2))
    operator fun <E:Number> invoke(p1 : E, p2 : UntypedScalar) : ScalarExpr<E> =
        AlgebraicBinaryScalar(this, constant(p1), CoerceScalar(p2, p1.javaClass.kaneType))
    operator fun <E:Number> invoke(p1 : MatrixExpr<E>, p2 : ScalarExpr<E>) : MatrixExpr<E> =
        AlgebraicBinaryMatrixScalar(this, p1.columns, p1.rows, p1, p2)
    operator fun <E:Number> invoke(p1 : E, p2 : MatrixExpr<E>) : MatrixExpr<E> =
        AlgebraicBinaryScalarMatrix(this, p2.columns, p2.rows, constant(p1, p2.type), p2)
    operator fun <E:Number> invoke(p1 : ScalarExpr<E>, p2 : MatrixExpr<E>) : MatrixExpr<E> =
        AlgebraicBinaryScalarMatrix(this, p2.columns, p2.rows, p1, p2)
    operator fun <E:Number> invoke(p1 : MatrixExpr<E>, p2 : MatrixExpr<E>) : MatrixExpr<E> =
        AlgebraicBinaryMatrix(this, p2.columns, p2.rows, p1, p2)
    operator fun <E:Number> invoke(p1 : MatrixExpr<E>, p2 : E) : MatrixExpr<E> =
        AlgebraicBinaryMatrixScalar(this, p1.columns, p1.rows, p1, constant(p2))
    operator fun <E:Number> invoke(p1 : E, p2 : ScalarExpr<E>) : ScalarExpr<E> = invoke(constant(p1), p2)
    operator fun <E:Number> invoke(p1 : ScalarExpr<E>, p2 : E) : ScalarExpr<E> = invoke(p1, constant(p2))
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
    override fun toString() = render()
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
    override fun toString() = render()
    override fun mapChildren(f: ExprFunction) = copy(left = f(left), right = f(right))
}

data class AlgebraicBinaryScalarMatrix<E:Number>(
    val op : AlgebraicBinaryScalarFunction,
    override val columns: Int,
    override val rows: Int,
    val left : ScalarExpr<E>,
    val right : MatrixExpr<E>) : MatrixExpr<E> {
    init { track() }
    override val type get() = left.type
    override fun get(column: Int, row: Int) = AlgebraicBinaryScalar(op, left, right[column, row])
    override fun toString() = render()
    override fun mapChildren(f: ExprFunction) = copy(left = f(left), right = f(right))
}

data class AlgebraicBinaryMatrix<E:Number>(
    val op : AlgebraicBinaryScalarFunction,
    override val columns: Int,
    override val rows: Int,
    val left : MatrixExpr<E>,
    val right : MatrixExpr<E>) : MatrixExpr<E> {
    init { track() }
    override val type get() = left.type
    override fun get(column: Int, row: Int) = AlgebraicBinaryScalar(op, left[column, row], right[column, row])
    override fun toString() = render()
    override fun mapChildren(f: ExprFunction) = copy(left = f(left), right = f(right))
}

// f(scalar)->scalar
interface AlgebraicUnaryScalarFunction {
    val meta : UnaryOp
    operator fun <E:Number> invoke(value : E) : E = when(value) {
        is Double -> doubleOp(value)
        is Float -> floatOp(value)
        is Int -> doubleOp(value.toDouble())
        else -> error("${value.javaClass}")
    } as E
    operator fun invoke(value : Double) = doubleOp(value)
    operator fun invoke(value : Float) = floatOp(value)
    fun doubleOp(value : Double) : Double
    fun floatOp(value : Float) : Float
    operator fun <E:Number> invoke(value : ScalarExpr<E>) : ScalarExpr<E> = AlgebraicUnaryScalar(this, value)
    operator fun <E:Number> invoke(value : MatrixExpr<E>) : MatrixExpr<E> = AlgebraicUnaryMatrix(this, value)
    fun <E:Number> reduceArithmetic(value : ScalarExpr<E>) : ScalarExpr<E>?
    fun <E:Number> differentiate(expr : ScalarExpr<E>, exprd : ScalarExpr<E>, variable : ScalarExpr<E>) : ScalarExpr<E>
}

data class AlgebraicUnaryScalar<E:Number>(
    val op : AlgebraicUnaryScalarFunction,
    val value : ScalarExpr<E>
) : ScalarExpr<E>, ParentExpr<E> {
    init { track() }
    override val type get() = value.type
    override val children get() = listOf(value)
    override fun toString() = render()
    override fun mapChildren(f: ExprFunction) = copy(value = f(value))
}

data class AlgebraicUnaryMatrix<E:Number>(
    val op : AlgebraicUnaryScalarFunction,
    val value : MatrixExpr<E>) : MatrixExpr<E> {
    init { track() }
    override val columns get() = value.columns
    override val rows get() = value.rows
    override val type get() = value.type
    override fun get(column: Int, row: Int) = AlgebraicUnaryScalar(op, value[column,row])
    override fun toString() = render()
    override fun mapChildren(f: ExprFunction) = copy(value = f(value))
}

// f(matrix)->scalar
interface AlgebraicUnaryMatrixScalarFunction {
    val meta : UnaryOp
    operator fun <E:Number> invoke(value : MatrixExpr<E>) : ScalarExpr<E> = AlgebraicUnaryMatrixScalar(this, value)
    fun <E:Number> reduceArithmetic(value : MatrixExpr<E>) : ScalarExpr<E>
}

data class AlgebraicUnaryMatrixScalar<E:Number>(
    val op : AlgebraicUnaryMatrixScalarFunction,
    val value : MatrixExpr<E>) : ScalarExpr<E>, ParentExpr<E> {
    init { track() }
    override val type get() = value.type
    override val children : Iterable<ScalarExpr<E>> get() = value.elements.asIterable()
    override fun toString() = render()
    override fun mapChildren(f: ExprFunction) = copy(value = f(value))
}

// f(expr, expr) -> matrix
data class AlgebraicDeferredDataMatrix<E:Number>(
    val op : BinaryOp,
    val left : TypedExpr<E>,
    val right : TypedExpr<E>,
    val data : DataMatrix<E>
) : MatrixExpr<E> {
    override val type = data.type
    override fun mapChildren(f: ExprFunction) = copy(
        left = f(left),
        right = f(right),
        data = data.mapChildren(f))
    override val columns = data.columns
    override val rows = data.rows
    override fun get(column: Int, row: Int) = data[column, row]
    override fun toString() = render()
}

