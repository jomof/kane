package com.github.jomof.kane.impl.functions

import com.github.jomof.kane.impl.StreamingSamples
import com.github.jomof.kane.impl.UnaryOp

private val NANS by UnaryOp("NaN")

class NansFunction : AlgebraicUnaryScalarStatisticFunction {
    override val meta = NANS
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.nans.toDouble()
}

