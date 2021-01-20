package com.github.jomof.kane.functions

import com.github.jomof.kane.*

private val MIN by UnaryOp(op = "∑")

class MinFunction : AlgebraicUnaryMatrixScalarFunction, AlgebraicUnaryScalarStatisticFunction {
    override val meta = MIN
    override fun reduceArithmetic(value: ScalarStatistic) = constant(value.statistic.min, value.type)

    override fun reduceArithmetic(value: MatrixExpr): ScalarExpr? {
        if (!value.elements.all { it is ConstantScalar }) return null
        return constant(value.elements.map { (it as ConstantScalar).value }.min()!!, value.type)
    }
}

val min = MinFunction()