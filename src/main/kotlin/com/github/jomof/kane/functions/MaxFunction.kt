package com.github.jomof.kane.functions

import com.github.jomof.kane.*

private val MAX by UnaryOp()

class MaxFunction : AlgebraicUnaryMatrixScalarFunction, AlgebraicUnaryScalarStatisticFunction {
    override val meta = MAX
    override fun reduceArithmetic(value: ScalarStatistic) = constant(value.statistic.max, value.type)
    override fun reduceArithmetic(elements: List<ScalarExpr>): ScalarExpr? {
        if (elements.isEmpty()) return null
        if (!elements.all { it is ConstantScalar }) return null
        return constant(elements.map { (it as ConstantScalar).value }.maxOrNull()!!, elements.first().type)
    }

    override fun reduceArithmetic(value: MatrixExpr) = reduceArithmetic(value.elements)
}

val max = MaxFunction()