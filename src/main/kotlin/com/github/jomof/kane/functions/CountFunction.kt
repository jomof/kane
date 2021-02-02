package com.github.jomof.kane.functions

import com.github.jomof.kane.*

private val COUNT by UnaryOp()

class CountFunction : AlgebraicUnaryMatrixScalarFunction, AlgebraicUnaryScalarStatisticFunction {
    override val meta = COUNT
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.count.toDouble()
    override fun reduceArithmetic(elements: List<ScalarExpr>) = constant(elements.size.toDouble())
    override fun reduceArithmetic(value: MatrixExpr) = constant(value.rows.toDouble() * value.columns.toDouble())
}

val count = CountFunction()