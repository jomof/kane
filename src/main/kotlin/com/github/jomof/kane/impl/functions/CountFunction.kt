package com.github.jomof.kane.impl.functions

import com.github.jomof.kane.MatrixExpr
import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.impl.*

private val COUNT by SummaryOp()

class CountFunction : AlgebraicSummaryFunction {
    override val meta = COUNT
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.count.toDouble()
    override fun reduceArithmetic(elements: List<ScalarExpr>) = constant(elements.size.toDouble())
    override fun reduceArithmetic(value: MatrixExpr): ScalarExpr {
        return constant(value.rows.toDouble() * value.columns.toDouble())
    }
}

