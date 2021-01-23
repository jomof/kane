package com.github.jomof.kane.functions

import com.github.jomof.kane.*

private val VARIANCE by UnaryOp()

class VarianceFunction : AlgebraicUnaryMatrixScalarFunction, AlgebraicUnaryScalarStatisticFunction {
    override val meta = VARIANCE

    override fun reduceArithmetic(value: ScalarStatistic) = constant(value.statistic.variance, value.type)

    override fun reduceArithmetic(value: MatrixExpr): ScalarExpr {
        val mean = mean(value)
        return summation(pow(value - mean, 2.0))
    }
}

val variance = VarianceFunction()