package com.github.jomof.kane.functions

import com.github.jomof.kane.impl.ScalarExpr
import com.github.jomof.kane.impl.StreamingSamples
import com.github.jomof.kane.impl.UnaryOp

private val CV by UnaryOp()

class CoefficientOfVariationFunction : AlgebraicUnaryScalarStatisticFunction {
    override val meta = CV
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.coefficientOfVariation
    override fun reduceArithmetic(elements: List<ScalarExpr>): ScalarExpr? {
        val statistic = super.reduceArithmetic(elements)
        if (statistic != null) return statistic
        val stddev = stddev.reduceArithmetic(elements) ?: return null
        return stddev / mean.reduceArithmetic(elements)
    }
}

val cv = CoefficientOfVariationFunction()