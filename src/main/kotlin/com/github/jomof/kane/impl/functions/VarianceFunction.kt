package com.github.jomof.kane.impl.functions

import com.github.jomof.kane.*
import com.github.jomof.kane.impl.StreamingSamples
import com.github.jomof.kane.impl.UnaryOp
import com.github.jomof.kane.impl.functions.AlgebraicUnaryScalarStatisticFunction

private val VARIANCE by UnaryOp()

class VarianceFunction : AlgebraicUnaryScalarStatisticFunction {
    override val meta = VARIANCE
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.variance
    override fun reduceArithmetic(elements: List<ScalarExpr>): ScalarExpr? {
        val statistic = super.reduceArithmetic(elements)
        if (statistic != null) return statistic
        // Variance is the average of the square distances from the mean
        val mean = (mean as AlgebraicUnaryScalarStatisticFunction).reduceArithmetic(elements) ?: return null
        val count = (count as AlgebraicUnaryScalarStatisticFunction).reduceArithmetic(elements) ?: return null
        val powers = elements.map { pow(it - mean, 2.0) }
        val sum = (sum as AlgebraicUnaryScalarStatisticFunction).reduceArithmetic(powers) ?: return null
        return sum / (count - 1.0)
    }
}
