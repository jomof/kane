package com.github.jomof.kane.impl.functions

import com.github.jomof.kane.MatrixExpr
import com.github.jomof.kane.impl.StreamingSamples
import com.github.jomof.kane.impl.SummaryOp
import com.github.jomof.kane.impl.elements

private val MIN by SummaryOp()

class MinFunction : AlgebraicSummaryFunction {
    override val meta = MIN
    override fun reduceArithmetic(value: MatrixExpr) = reduceArithmetic(value.elements)
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.min
}
