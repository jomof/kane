package com.github.jomof.kane.impl.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.impl.StreamingSamples
import com.github.jomof.kane.impl.SummaryOp
import com.github.jomof.kane.pow
import com.github.jomof.kane.variance

private val STDEV by SummaryOp()

internal class StdevFunction : AlgebraicSummaryFunction {
    override val meta = STDEV
    override fun lookupStatistic(statistic: StreamingSamples) =
        statistic.stdev

    override fun reduceArithmetic(elements: List<ScalarExpr>): ScalarExpr? {
        val statistic = super.reduceArithmetic(elements)
        if (statistic != null) return statistic
        val variance = (variance as AlgebraicSummaryFunction).reduceArithmetic(elements) ?: return null
        return pow(variance, 0.5)
    }
}

