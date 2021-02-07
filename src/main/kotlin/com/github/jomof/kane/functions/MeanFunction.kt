package com.github.jomof.kane.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.count
import com.github.jomof.kane.div
import com.github.jomof.kane.impl.StreamingSamples
import com.github.jomof.kane.impl.UnaryOp
import com.github.jomof.kane.impl.functions.AlgebraicUnaryScalarStatisticFunction
import com.github.jomof.kane.sum

private val MEAN by UnaryOp()

class MeanFunction : AlgebraicUnaryScalarStatisticFunction {
    override val meta = MEAN
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.mean
    override fun reduceArithmetic(elements: List<ScalarExpr>): ScalarExpr? {
        val statistic = super.reduceArithmetic(elements)
        if (statistic != null) return statistic
        val sum = (sum as AlgebraicUnaryScalarStatisticFunction).reduceArithmetic(elements) ?: return null
        val count = (count as AlgebraicUnaryScalarStatisticFunction).reduceArithmetic(elements) ?: return null
        return sum / count
    }
}

