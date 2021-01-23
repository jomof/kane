package com.github.jomof.kane.functions

import com.github.jomof.kane.ScalarStatistic
import com.github.jomof.kane.UnaryOp
import com.github.jomof.kane.constant

private val SKEWNESS by UnaryOp()

class SkewnessFunction : AlgebraicUnaryScalarStatisticFunction {
    override val meta = SKEWNESS
    override fun reduceArithmetic(value: ScalarStatistic) = constant(value.statistic.skewness, value.type)
}

val skewness = SkewnessFunction()