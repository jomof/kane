package com.github.jomof.kane.impl.functions

import com.github.jomof.kane.impl.StreamingSamples
import com.github.jomof.kane.impl.SummaryOp

private val MEDIAN by SummaryOp()

class MedianFunction : AlgebraicSummaryFunction {
    override val meta = MEDIAN
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.median
}



