package com.github.jomof.kane.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.StreamingSamples
import com.github.jomof.kane.UnaryOp

private val STDDEV by UnaryOp()

class StddevFunction : AlgebraicUnaryScalarStatisticFunction {
    override val meta = STDDEV
    override fun lookupStatistic(statistic: StreamingSamples) =
        statistic.stddev

    override fun reduceArithmetic(elements: List<ScalarExpr>): ScalarExpr? {
        val statistic = super.reduceArithmetic(elements)
        if (statistic != null) return statistic
        val variance = variance.reduceArithmetic(elements)
        return pow(variance, 0.5)
    }
}

val stddev = StddevFunction()