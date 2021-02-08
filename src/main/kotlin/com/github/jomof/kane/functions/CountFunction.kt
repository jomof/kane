package com.github.jomof.kane.functions

import com.github.jomof.kane.MatrixExpr
import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.impl.*
import com.github.jomof.kane.impl.functions.AlgebraicUnaryScalarStatisticFunction

private val COUNT by UnaryOp()

class CountFunction : AlgebraicUnaryScalarStatisticFunction {
    override val meta = COUNT
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.count.toDouble()
    override fun reduceArithmetic(elements: List<ScalarExpr>) = constant(elements.size.toDouble())
    override fun reduceArithmetic(value: MatrixExpr): ScalarExpr {
        return constant(value.rows.toDouble() * value.columns.toDouble())
    }
}

