package com.github.jomof.kane.impl.functions

import com.github.jomof.kane.impl.StreamingSamples
import com.github.jomof.kane.impl.SummaryOp

private val KURTOSIS by SummaryOp()

class KurtosisFunction : AlgebraicSummaryFunction {
    override val meta = KURTOSIS
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.kurtosis
}