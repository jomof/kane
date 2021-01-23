package com.github.jomof.kane.functions

import com.github.jomof.kane.MatrixExpr
import com.github.jomof.kane.ScalarStatistic
import com.github.jomof.kane.UnaryOp
import com.github.jomof.kane.constant

private val CV by UnaryOp()

class CoefficientOfVariationFunction : AlgebraicUnaryMatrixScalarFunction, AlgebraicUnaryScalarStatisticFunction {
    override val meta = CV
    override fun reduceArithmetic(value: ScalarStatistic) = constant(value.statistic.coefficientOfVariation, value.type)
    override fun reduceArithmetic(value: MatrixExpr) = stddev(value) / mean(value)
}

val cv = CoefficientOfVariationFunction()