package com.github.jomof.kane.functions

import com.github.jomof.kane.*

private val CV by UnaryOp()

class CoefficientOfVariationFunction : AlgebraicUnaryMatrixScalarFunction, AlgebraicUnaryScalarStatisticFunction {
    override val meta = CV
    override fun reduceArithmetic(value: ScalarStatistic) = constant(value.statistic.coefficientOfVariation, value.type)
    override fun reduceArithmetic(value: MatrixExpr) = reduceArithmetic(value.elements)
    override fun reduceArithmetic(elements: List<ScalarExpr>): ScalarExpr? {
        val stddev = stddev.reduceArithmetic(elements)
        val mean = mean.reduceArithmetic(elements)
        return stddev / mean
    }
}

val cv = CoefficientOfVariationFunction()