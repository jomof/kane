package com.github.jomof.kane.functions

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.ScalarStatistic
import com.github.jomof.kane.UnaryOp
import com.github.jomof.kane.constant

private val KURTOSIS by UnaryOp()

class KurtosisFunction : AlgebraicUnaryScalarStatisticFunction {
    override val meta = KURTOSIS
    override fun reduceArithmetic(value: ScalarStatistic) = constant(value.statistic.kurtosis, value.type)
    override fun reduceArithmetic(elements: List<ScalarExpr>) = TODO()
}

val kurtosis = KurtosisFunction()