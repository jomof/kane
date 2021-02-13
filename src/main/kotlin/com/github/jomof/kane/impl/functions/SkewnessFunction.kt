package com.github.jomof.kane.impl.functions

import com.github.jomof.kane.impl.StreamingSamples
import com.github.jomof.kane.impl.SummaryOp

private val SKEWNESS by SummaryOp()

class SkewnessFunction : AlgebraicSummaryFunction {
    override val meta = SKEWNESS
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.skewness
}
