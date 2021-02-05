package com.github.jomof.kane.functions

import com.github.jomof.kane.*

private val PERCENTILE by BinaryOp(precedence = 7)
private val PERCENTILE25 by UnaryOp("25%")
private val PERCENTILE75 by UnaryOp("75%")

class PercentileFunction : AlgebraicBinaryScalarStatisticFunction {
    override val meta = PERCENTILE
    override fun reduceArithmetic(left: List<ScalarExpr>, right: ScalarExpr): ScalarExpr? {
        if (!right.canGetConstant()) return null
        val right = right.getConstant()
        val statistic = StreamingSamples()
        left.forEach {
            if (!it.canGetConstant()) return null
            statistic.insert(it.getConstant())
        }
        return ConstantScalar(statistic.percentile(right))
    }

    override fun reduceArithmetic(left: ScalarStatistic, right: ScalarExpr): ScalarExpr {
        return when (right) {
            is ConstantScalar -> constant(left.statistic.percentile(right.value))
            is ScalarStatistic -> constant(left.statistic.percentile(right.statistic.mean))
            else -> error("${left.javaClass} ${right.javaClass}")
        }
    }
}

val percentile = PercentileFunction()
val percentile25 = object : AlgebraicUnaryScalarStatisticFunction {
    override val meta = PERCENTILE25
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.percentile(0.25)
}
val percentile75 = object : AlgebraicUnaryScalarStatisticFunction {
    override val meta = PERCENTILE75
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.percentile(0.75)
}
