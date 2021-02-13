package com.github.jomof.kane.impl.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.impl.*

private val PERCENTILE by BinaryOp(precedence = 7)
private val PERCENTILE25 by SummaryOp("25%")
private val PERCENTILE75 by SummaryOp("75%")

open class PercentileFunction : AlgebraicBinaryScalarStatisticFunction {
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

class Percentile25Function : AlgebraicSummaryFunction {
    override val meta = PERCENTILE25
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.percentile(0.25)
}

class Percentile75Function : AlgebraicSummaryFunction {
    override val meta = PERCENTILE75
    override fun lookupStatistic(statistic: StreamingSamples) = statistic.percentile(0.75)
}




