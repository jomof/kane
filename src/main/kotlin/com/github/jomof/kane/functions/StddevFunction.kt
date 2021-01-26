package com.github.jomof.kane.functions

import com.github.jomof.kane.*

private val STDDEV by UnaryOp()

class StddevFunction : AlgebraicUnaryMatrixScalarFunction, AlgebraicUnaryScalarStatisticFunction {
    override val meta = STDDEV

    override fun reduceArithmetic(value: ScalarStatistic) = constant(value.statistic.stddev, value.type)
    override fun reduceArithmetic(elements: List<ScalarExpr>): ScalarExpr {
        val mean = mean.reduceArithmetic(elements)
        val powers = elements.map { pow(it - mean, 2.0) }
        return pow(sum.reduceArithmetic(powers), 0.5)
    }

    override fun reduceArithmetic(value: MatrixExpr) = reduceArithmetic(value.elements)
}

val stddev = StddevFunction()