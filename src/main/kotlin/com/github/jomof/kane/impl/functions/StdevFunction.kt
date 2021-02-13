package com.github.jomof.kane.impl.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.impl.StreamingSamples
import com.github.jomof.kane.impl.UnaryOp
import com.github.jomof.kane.pow
import com.github.jomof.kane.variance

private val STDEV by UnaryOp()

internal class StdevFunction : AlgebraicUnaryScalarStatisticFunction {
    override val meta = STDEV
    override fun lookupStatistic(statistic: StreamingSamples) =
        statistic.stdev

    override fun reduceArithmetic(elements: List<ScalarExpr>): ScalarExpr? {
        val statistic = super.reduceArithmetic(elements)
        if (statistic != null) return statistic
        val variance = (variance as AlgebraicUnaryScalarStatisticFunction).reduceArithmetic(elements) ?: return null
        return pow(variance, 0.5)
    }
}

