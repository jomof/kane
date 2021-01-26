package com.github.jomof.kane.functions

import com.github.jomof.kane.*

private val MEAN by UnaryOp()

class MeanFunction : AlgebraicUnaryMatrixScalarFunction, AlgebraicUnaryScalarStatisticFunction {
    override val meta = MEAN

    override fun reduceArithmetic(value: ScalarStatistic) = constant(value.statistic.mean, value.type)
    override fun reduceArithmetic(value: MatrixExpr) = reduceArithmetic(value.elements)
    override fun reduceArithmetic(elements: List<ScalarExpr>): ScalarExpr {
        val sum = sum.reduceArithmetic(elements)
        val count = count.reduceArithmetic(elements)
        return sum / count
    }
}

val mean = MeanFunction()