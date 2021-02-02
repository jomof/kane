package com.github.jomof.kane.functions

import com.github.jomof.kane.StreamingSamples
import com.github.jomof.kane.UnaryOp

private val KURTOSIS by UnaryOp()

class KurtosisFunction : AlgebraicUnaryScalarStatisticFunction {
    override val meta = KURTOSIS
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.kurtosis
}

val kurtosis = KurtosisFunction()