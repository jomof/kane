package com.github.jomof.kane.rigueur.functions

import com.github.jomof.kane.rigueur.*
import com.github.jomof.kane.rigueur.types.one
import com.github.jomof.kane.rigueur.types.two
import com.github.jomof.kane.rigueur.types.zero
import kotlin.math.exp

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

val SUMMATION by UnaryOp(op = "∑")

private class SummationFunction : AlgebraicUnaryMatrixScalarFunction {
    override val meta = SUMMATION
    override fun <E : Number> reduceArithmetic(value: MatrixExpr<E>): ScalarExpr<E> {
        return value.elements.fold(constant(value.type.zero, value.type)) { prior, current ->
            prior + current
        }
    }
}

val summation : AlgebraicUnaryMatrixScalarFunction = SummationFunction()