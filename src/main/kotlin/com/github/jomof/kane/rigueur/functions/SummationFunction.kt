package com.github.jomof.kane.rigueur.functions

import com.github.jomof.kane.rigueur.*
import com.github.jomof.kane.rigueur.types.one
import com.github.jomof.kane.rigueur.types.two
import com.github.jomof.kane.rigueur.types.zero
import kotlin.math.exp

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