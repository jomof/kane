package com.github.jomof.kane.functions

import com.github.jomof.kane.*

private val VARIANCE by UnaryOp()

class VarianceFunction : AlgebraicUnaryMatrixScalarFunction, AlgebraicUnaryScalarStatisticFunction {
    override val meta = VARIANCE

    override fun reduceArithmetic(value: ScalarStatistic) = constant(value.statistic.variance, value.type)
    override fun reduceArithmetic(value: MatrixExpr) = reduceArithmetic(value.elements)
    override fun reduceArithmetic(elements: List<ScalarExpr>): ScalarExpr {
        val mean = mean.reduceArithmetic(elements)
        val powers = elements.map { pow(it - mean, 2.0) }
        return summation.reduceArithmetic(powers)
    }
}

val variance = VarianceFunction()