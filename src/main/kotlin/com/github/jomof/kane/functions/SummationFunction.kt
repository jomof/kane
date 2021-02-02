package com.github.jomof.kane.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.StreamingSamples
import com.github.jomof.kane.UnaryOp

private val SUM by UnaryOp()

class SummationFunction : AlgebraicUnaryScalarStatisticFunction {
    override val meta = SUM
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.sum
    override fun reduceArithmetic(elements: List<ScalarExpr>): ScalarExpr {
        val statistic = super.reduceArithmetic(elements)
        if (statistic != null) return statistic
        return elements.drop(1).fold(elements[0]) { prior, current -> prior + current }
    }
}

val sum = SummationFunction()