package com.github.jomof.kane.functions

import com.github.jomof.kane.*

private val MAX by UnaryOp()

class MaxFunction : AlgebraicUnaryMatrixScalarFunction, AlgebraicUnaryScalarStatisticFunction {
    override val meta = MAX
    override fun reduceArithmetic(value: ScalarStatistic) = constant(value.statistic.max, value.type)

    override fun reduceArithmetic(value: MatrixExpr): ScalarExpr? {
        if (!value.elements.all { it is ConstantScalar }) return null
        return constant(value.elements.map { (it as ConstantScalar).value }.maxOrNull()!!, value.type)
    }
}

val max = MaxFunction()