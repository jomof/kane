package com.github.jomof.kane.impl.functions

import com.github.jomof.kane.impl.StreamingSamples
import com.github.jomof.kane.impl.UnaryOp

private val KURTOSIS by UnaryOp()

class KurtosisFunction : AlgebraicUnaryScalarStatisticFunction {
    override val meta = KURTOSIS
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.kurtosis
}