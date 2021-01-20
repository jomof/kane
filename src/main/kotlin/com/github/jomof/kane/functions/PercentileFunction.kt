package com.github.jomof.kane.functions

import com.github.jomof.kane.*

private val PERCENTILE by UnaryOp()

class PercentileFunction : AlgebraicBinaryScalarStatisticFunction {
    override val meta = PERCENTILE
    override fun reduceArithmetic(left: ScalarStatistic, right: ScalarExpr): ScalarExpr {
        return when {
            right is ConstantScalar -> constant(left.statistic.percentile(right.value), left.type)
            else -> error("${left.javaClass} ${right.javaClass}")
        }
    }
}

val percentile = PercentileFunction()