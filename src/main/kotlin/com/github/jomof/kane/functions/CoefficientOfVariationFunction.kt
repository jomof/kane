package com.github.jomof.kane.functions

import com.github.jomof.kane.*

private val COEFFECIENTOFVARIATION by UnaryOp()

class CoefficientOfVariationFunction : AlgebraicUnaryMatrixScalarFunction, AlgebraicUnaryScalarStatisticFunction {
    override val meta = COEFFECIENTOFVARIATION
    override fun reduceArithmetic(value: ScalarStatistic) = constant(value.statistic.coefficientOfVariation, value.type)
    override fun reduceArithmetic(value: MatrixExpr) = stddev(value) / mean(value)
}

val coefficientOfVariation = CoefficientOfVariationFunction()