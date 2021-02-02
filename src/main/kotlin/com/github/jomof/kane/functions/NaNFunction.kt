package com.github.jomof.kane.functions

import com.github.jomof.kane.StreamingSamples
import com.github.jomof.kane.UnaryOp

private val NANS by UnaryOp("NaN")

class NaNsFunction : AlgebraicUnaryScalarStatisticFunction {
    override val meta = NANS
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.nans.toDouble()
}

val nans = NaNsFunction()