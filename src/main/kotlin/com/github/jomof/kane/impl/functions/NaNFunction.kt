package com.github.jomof.kane.impl.functions

import com.github.jomof.kane.impl.StreamingSamples
import com.github.jomof.kane.impl.SummaryOp

private val NANS by SummaryOp("NaN")

class NansFunction : AlgebraicSummaryFunction {
    override val meta = NANS
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.nans.toDouble()
}

