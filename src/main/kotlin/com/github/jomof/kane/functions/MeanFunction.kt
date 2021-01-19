package com.github.jomof.kane.functions

import com.github.jomof.kane.*

private val MEAN by UnaryOp()

class MeanFunction : AlgebraicUnaryMatrixScalarFunction, AlgebraicUnaryScalarStatisticFunction {
    override val meta = MEAN

    override fun reduceArithmetic(value: ScalarStatistic) = constant(value.statistic.mean, value.type)

    override fun reduceArithmetic(value: MatrixExpr): ScalarExpr {
        return value.elements.drop(1).fold(value.elements[0]) { prior, current ->
            prior + current
        } / constant(value.elements.size.toDouble())
    }
}

val mean = MeanFunction()