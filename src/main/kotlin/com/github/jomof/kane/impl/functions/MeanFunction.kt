package com.github.jomof.kane.impl.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.count
import com.github.jomof.kane.div
import com.github.jomof.kane.impl.StreamingSamples
import com.github.jomof.kane.impl.SummaryOp
import com.github.jomof.kane.sum

private val MEAN by SummaryOp()

class MeanFunction : AlgebraicSummaryFunction {
    override val meta = MEAN
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.mean
    override fun reduceArithmetic(elements: List<ScalarExpr>): ScalarExpr? {
        val statistic = super.reduceArithmetic(elements)
        if (statistic != null) return statistic
        val sum = (sum as AlgebraicSummaryFunction).reduceArithmetic(elements) ?: return null
        val count = (count as AlgebraicSummaryFunction).reduceArithmetic(elements) ?: return null
        return sum / count
    }
}

