package com.github.jomof.kane.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.ScalarStatistic
import com.github.jomof.kane.UnaryOp
import com.github.jomof.kane.constant

private val NANS by UnaryOp("NaN")

class NaNsFunction : AlgebraicUnaryScalarStatisticFunction {
    override val meta = NANS
    override fun reduceArithmetic(value: ScalarStatistic) = constant(value.statistic.nans.toDouble(), value.type)
    override fun reduceArithmetic(elements: List<ScalarExpr>) = TODO()
}

val nans = NaNsFunction()