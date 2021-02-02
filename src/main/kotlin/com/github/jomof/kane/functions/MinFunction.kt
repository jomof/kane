package com.github.jomof.kane.functions

import com.github.jomof.kane.StreamingSamples
import com.github.jomof.kane.UnaryOp

private val MIN by UnaryOp()

class MinFunction : AlgebraicUnaryScalarStatisticFunction {
    override val meta = MIN
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.min
}

val min = MinFunction()