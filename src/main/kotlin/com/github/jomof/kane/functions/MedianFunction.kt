package com.github.jomof.kane.functions

import com.github.jomof.kane.StreamingSamples
import com.github.jomof.kane.UnaryOp

private val MEDIAN by UnaryOp()

class MedianFunction : AlgebraicUnaryScalarStatisticFunction {
    override val meta = MEDIAN
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.median
}

val median = MedianFunction()