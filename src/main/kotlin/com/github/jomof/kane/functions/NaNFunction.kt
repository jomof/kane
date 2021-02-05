package com.github.jomof.kane.functions

import com.github.jomof.kane.impl.StreamingSamples
import com.github.jomof.kane.impl.UnaryOp
import com.github.jomof.kane.impl.functions.AlgebraicUnaryScalarStatisticFunction

private val NANS by UnaryOp("NaN")

class NaNsFunction : AlgebraicUnaryScalarStatisticFunction {
    override val meta = NANS
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.nans.toDouble()
}

