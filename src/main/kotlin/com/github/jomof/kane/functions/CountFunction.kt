package com.github.jomof.kane.functions

import com.github.jomof.kane.*

private val COUNT by UnaryOp()

class CountFunction : AlgebraicUnaryMatrixScalarFunction, AlgebraicUnaryScalarStatisticFunction {
    override val meta = COUNT

    override fun reduceArithmetic(value: ScalarStatistic) = constant(value.statistic.count.toDouble(), value.type)

    override fun reduceArithmetic(value: MatrixExpr): ScalarExpr {
        return constant(value.rows.toDouble() * value.columns.toDouble())
    }
}

val count = CountFunction()