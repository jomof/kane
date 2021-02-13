package com.github.jomof.kane.impl.functions

import com.github.jomof.kane.*
import com.github.jomof.kane.impl.StreamingSamples
import com.github.jomof.kane.impl.SummaryOp

private val VARIANCE by SummaryOp()

class VarianceFunction : AlgebraicSummaryFunction {
    override val meta = VARIANCE
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.variance
    override fun reduceArithmetic(elements: List<ScalarExpr>): ScalarExpr? {
        val statistic = super.reduceArithmetic(elements)
        if (statistic != null) return statistic
        // Variance is the average of the square distances from the mean
        val mean = (mean as AlgebraicSummaryFunction).reduceArithmetic(elements) ?: return null
        val count = (count as AlgebraicSummaryFunction).reduceArithmetic(elements) ?: return null
        val powers = elements.map { pow(it - mean, 2.0) }
        val sum = (sum as AlgebraicSummaryFunction).reduceArithmetic(powers) ?: return null
        return sum / (count - 1.0)
    }
}
