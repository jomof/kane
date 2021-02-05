package com.github.jomof.kane.functions

import com.github.jomof.kane.impl.StreamingSamples
import com.github.jomof.kane.impl.UnaryOp
import com.github.jomof.kane.impl.functions.AlgebraicUnaryScalarStatisticFunction

private val MEDIAN by UnaryOp()

class MedianFunction : AlgebraicUnaryScalarStatisticFunction {
    override val meta = MEDIAN
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.median
}
