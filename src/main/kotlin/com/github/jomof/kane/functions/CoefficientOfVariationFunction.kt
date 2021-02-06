package com.github.jomof.kane.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.div
import com.github.jomof.kane.impl.StreamingSamples
import com.github.jomof.kane.impl.UnaryOp
import com.github.jomof.kane.impl.functions.AlgebraicUnaryScalarStatisticFunction
import com.github.jomof.kane.mean
import com.github.jomof.kane.stdev

private val CV by UnaryOp()

class CoefficientOfVariationFunction : AlgebraicUnaryScalarStatisticFunction {
    override val meta = CV
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.coefficientOfVariation
    override fun reduceArithmetic(elements: List<ScalarExpr>): ScalarExpr? {
        val statistic = super.reduceArithmetic(elements)
        if (statistic != null) return statistic
        val stdev = stdev.reduceArithmetic(elements) ?: return null
        return stdev / mean.reduceArithmetic(elements)
    }
}

val cv = CoefficientOfVariationFunction()