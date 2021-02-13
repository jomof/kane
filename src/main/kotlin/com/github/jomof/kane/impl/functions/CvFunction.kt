package com.github.jomof.kane.impl.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.div
import com.github.jomof.kane.impl.StreamingSamples
import com.github.jomof.kane.impl.SummaryOp
import com.github.jomof.kane.mean
import com.github.jomof.kane.stdev

private val CV by SummaryOp()

class CvFunction : AlgebraicSummaryFunction {
    override val meta = CV
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.coefficientOfVariation
    override fun reduceArithmetic(elements: List<ScalarExpr>): ScalarExpr? {
        val statistic = super.reduceArithmetic(elements)
        if (statistic != null) return statistic
        val stdev = (stdev as AlgebraicSummaryFunction).reduceArithmetic(elements) ?: return null
        val mean = (mean as AlgebraicSummaryFunction).reduceArithmetic(elements) ?: return null
        return stdev / mean
    }
}
