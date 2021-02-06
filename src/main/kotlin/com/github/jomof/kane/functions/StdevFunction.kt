package com.github.jomof.kane.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.impl.StreamingSamples
import com.github.jomof.kane.impl.UnaryOp
import com.github.jomof.kane.impl.functions.AlgebraicUnaryScalarStatisticFunction
import com.github.jomof.kane.pow

private val STDEV by UnaryOp()

internal class StdevFunction : AlgebraicUnaryScalarStatisticFunction {
    override val meta = STDEV
    override fun lookupStatistic(statistic: StreamingSamples) =
        statistic.stdev

    override fun reduceArithmetic(elements: List<ScalarExpr>): ScalarExpr? {
        val statistic = super.reduceArithmetic(elements)
        if (statistic != null) return statistic
        val variance = variance.reduceArithmetic(elements)
        return pow(variance, 0.5)
    }
}

