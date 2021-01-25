package com.github.jomof.kane.functions

import com.github.jomof.kane.*

private val MEDIAN by UnaryOp()

class MedianFunction : AlgebraicUnaryMatrixScalarFunction, AlgebraicUnaryScalarStatisticFunction {
    override val meta = MEDIAN

    override fun reduceArithmetic(value: ScalarStatistic) = constant(value.statistic.median, value.type)
    override fun reduceArithmetic(elements: List<ScalarExpr>) = TODO()
    override fun reduceArithmetic(value: MatrixExpr) = TODO()
}

val median = MedianFunction()