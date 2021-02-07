package com.github.jomof.kane.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.impl.StreamingSamples
import com.github.jomof.kane.impl.UnaryOp
import com.github.jomof.kane.impl.functions.AlgebraicUnaryScalarStatisticFunction
import com.github.jomof.kane.plus

private val SUM by UnaryOp()

class SumFunction : AlgebraicUnaryScalarStatisticFunction {
    override val meta = SUM
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.sum
    override fun reduceArithmetic(elements: List<ScalarExpr>): ScalarExpr {
        val statistic = super.reduceArithmetic(elements)
        if (statistic != null) return statistic
        return elements.drop(1).fold(elements[0]) { prior, current -> prior + current }
    }
}


