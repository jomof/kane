package com.github.jomof.kane.functions

import com.github.jomof.kane.StreamingSamples
import com.github.jomof.kane.UnaryOp

private val SKEWNESS by UnaryOp()

class SkewnessFunction : AlgebraicUnaryScalarStatisticFunction {
    override val meta = SKEWNESS
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.skewness
}

val skewness = SkewnessFunction()