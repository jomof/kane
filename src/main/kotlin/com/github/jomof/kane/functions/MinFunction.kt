package com.github.jomof.kane.functions

import com.github.jomof.kane.impl.StreamingSamples
import com.github.jomof.kane.impl.UnaryOp
import com.github.jomof.kane.impl.functions.AlgebraicUnaryScalarStatisticFunction

private val MIN by UnaryOp()

class MinFunction : AlgebraicUnaryScalarStatisticFunction {
    override val meta = MIN
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.min
}

val min = MinFunction()