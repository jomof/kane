package com.github.jomof.kane.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.StreamingSamples
import com.github.jomof.kane.UnaryOp

private val MEAN by UnaryOp()

class MeanFunction : AlgebraicUnaryScalarStatisticFunction {
    override val meta = MEAN
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.mean
    override fun reduceArithmetic(elements: List<ScalarExpr>): ScalarExpr {
        val statistic = super.reduceArithmetic(elements)
        if (statistic != null) return statistic
        val sum = sum.reduceArithmetic(elements)
        val count = count.reduceArithmetic(elements)
        return sum / count
    }
}

val mean = MeanFunction()