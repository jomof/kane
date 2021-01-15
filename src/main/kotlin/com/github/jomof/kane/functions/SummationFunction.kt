package com.github.jomof.kane.functions

import com.github.jomof.kane.*

private val SUMMATION by UnaryOp(op = "∑")

private class SummationFunction : AlgebraicUnaryMatrixScalarFunction {
    override val meta = SUMMATION
    override fun <E : Number> reduceArithmetic(value: MatrixExpr<E>): ScalarExpr<E> {
        return value.elements.drop(1).fold(value.elements[0]) { prior, current ->
                    prior + current
                }
    }
}

val summation : AlgebraicUnaryMatrixScalarFunction = SummationFunction()