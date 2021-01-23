package com.github.jomof.kane.functions

import com.github.jomof.kane.*

private val SUMMATION by UnaryOp(op = "∑")

class SummationFunction : AlgebraicUnaryMatrixScalarFunction, AlgebraicUnaryScalarStatisticFunction {
    override val meta = SUMMATION
    override fun reduceArithmetic(value: ScalarStatistic) = constant(value.statistic.sum, value.type)

    override fun reduceArithmetic(value: MatrixExpr): ScalarExpr {
        return value.elements.drop(1).fold(value.elements[0]) { prior, current ->
                    prior + current
                }
    }
}

val summation = SummationFunction()