package com.github.jomof.kane

import com.github.jomof.kane.types.AlgebraicType
import com.github.jomof.kane.types.DoubleAlgebraicType

interface RandomVariableExpr : ScalarExpr

data class DiscreteUniformRandomVariable(
    val values : List<Double>,
    override val type : AlgebraicType
) : RandomVariableExpr, ScalarExpr {
    init { track() }
    override fun toString() : String {
        val min = values.minByOrNull { it } ?: 0.0
        val max = values.maxByOrNull { it } ?: 0.0
        return "random($min to $max)"
    }
}

fun randomOf(range : Pair<Double, Double>, step : Double = 1.0) : DiscreteUniformRandomVariable {
    val (start, stop) = range
    var current = start
    val elements = mutableListOf<Double>()
    while(current < stop + 1) {
        elements += current
        current += step
    }
    return DiscreteUniformRandomVariable(elements, DoubleAlgebraicType.kaneType)
}


fun Expr.findRandomVariables() : Map<String, RandomVariableExpr> {
    val result = mutableMapOf<String, RandomVariableExpr>()
    visit {
        if (it is NamedScalar && it.scalar is RandomVariableExpr)
            result[it.name] = it.scalar
    }
    return result
}