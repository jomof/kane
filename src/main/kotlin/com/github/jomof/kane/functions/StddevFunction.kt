package com.github.jomof.kane.functions

import com.github.jomof.kane.*

private val STDDEV by UnaryOp()

class StddevFunction : AlgebraicUnaryMatrixScalarFunction, AlgebraicUnaryScalarStatisticFunction {
    override val meta = STDDEV

    override fun reduceArithmetic(value: ScalarStatistic) = constant(value.statistic.stddev, value.type)

    override fun reduceArithmetic(value: MatrixExpr): ScalarExpr {
        val mean = mean(value)
        return pow(summation(pow(value-mean, 2.0)), 0.5)
    }
}

val stddev = StddevFunction()