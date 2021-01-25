package com.github.jomof.kane.functions

import com.github.jomof.kane.*

private val MIN by UnaryOp()

class MinFunction : AlgebraicUnaryMatrixScalarFunction, AlgebraicUnaryScalarStatisticFunction {
    override val meta = MIN
    override fun reduceArithmetic(value: ScalarStatistic) = constant(value.statistic.min, value.type)
    override fun reduceArithmetic(elements: List<ScalarExpr>): ScalarExpr? {
        if (!elements.all { it is ConstantScalar }) return null
        return constant(elements.map { (it as ConstantScalar).value }.minOrNull()!!, elements[0].type)
    }

    override fun reduceArithmetic(value: MatrixExpr) = reduceArithmetic(value.elements)
}

val min = MinFunction()