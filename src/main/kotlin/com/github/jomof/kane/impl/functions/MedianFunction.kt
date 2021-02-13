package com.github.jomof.kane.impl.functions

import com.github.jomof.kane.impl.StreamingSamples
import com.github.jomof.kane.impl.UnaryOp

private val MEDIAN by UnaryOp()

class MedianFunction : AlgebraicUnaryScalarStatisticFunction {
    override val meta = MEDIAN
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.median
}



