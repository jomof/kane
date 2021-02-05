package com.github.jomof.kane.functions

import com.github.jomof.kane.impl.StreamingSamples
import com.github.jomof.kane.impl.UnaryOp
import com.github.jomof.kane.impl.functions.AlgebraicUnaryScalarStatisticFunction

private val MAX by UnaryOp()

class MaxFunction : AlgebraicUnaryScalarStatisticFunction {
    override val meta = MAX
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.max
}

val max = MaxFunction()