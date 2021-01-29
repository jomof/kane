package com.github.jomof.kane.functions

import com.github.jomof.kane.*

private val PERCENTILE by BinaryOp(precedence = 7)

class PercentileFunction : AlgebraicBinaryScalarStatisticFunction {
    override val meta = PERCENTILE
    override fun reduceArithmetic(left: List<ScalarExpr>, right: ScalarExpr): ScalarExpr? {
        val right = right.tryFindConstant() ?: return null
        val statistic = StreamingSamples()
        left.forEach {
            val value = it.tryFindConstant() ?: return null
            statistic.insert(value)
        }
        return ConstantScalar(statistic.percentile(right), left.first().type)
    }

    override fun reduceArithmetic(left: ScalarStatistic, right: ScalarExpr): ScalarExpr {
        return when (right) {
            is ConstantScalar -> constant(left.statistic.percentile(right.value), left.type)
            is ScalarStatistic -> {
                error("${left.javaClass} ${right.javaClass}")
            }
            else -> error("${left.javaClass} ${right.javaClass}")
        }
    }
}

val percentile = PercentileFunction()