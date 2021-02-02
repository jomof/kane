package com.github.jomof.kane.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.StreamingSamples
import com.github.jomof.kane.UnaryOp

private val VARIANCE by UnaryOp()

class VarianceFunction : AlgebraicUnaryScalarStatisticFunction {
    override val meta = VARIANCE
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.variance
    override fun reduceArithmetic(elements: List<ScalarExpr>): ScalarExpr {
        val statistic = super.reduceArithmetic(elements)
        if (statistic != null) return statistic
        // Variance is the average of the square distances from the mean
        val mean = mean.reduceArithmetic(elements)
        val powers = elements.map { pow(it - mean, 2.0) }
        return sum.reduceArithmetic(powers) / (count.reduceArithmetic(elements) - 1.0)
    }
}

val variance = VarianceFunction()