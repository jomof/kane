package com.github.jomof.kane.impl.functions

import com.github.jomof.kane.impl.StreamingSamples
import com.github.jomof.kane.impl.SummaryOp

private val MAX by SummaryOp()

class MaxFunction : AlgebraicSummaryFunction {
    override val meta = MAX
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.max
}
