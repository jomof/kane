package com.github.jomof.kane

import com.github.jomof.kane.sheet.Sheet
import com.github.jomof.kane.types.AlgebraicType
import com.github.jomof.kane.types.DoubleAlgebraicType
import kotlin.random.Random

interface RandomVariableExpr : ScalarExpr {
    fun sample(random : Random) : ConstantScalar
}

class DiscreteUniformRandomVariable(
    val values : List<Double>,
    override val type : AlgebraicType
) : RandomVariableExpr, ScalarExpr {
    init { track() }

    override fun sample(random : Random): ConstantScalar {
        val index = random.nextInt(values.size)
        val sample = values[index]
        return ConstantScalar(sample, type)
    }

    override fun toString() : String {
        val min = values.minByOrNull { it } ?: 0.0
        val max = values.maxByOrNull { it } ?: 0.0
        return "random($min to $max)"
    }
}

class ScalarStatistic(
    val statistic : StreamingSamples,
    override val type : AlgebraicType
) : ScalarExpr {
    override fun toString() : String {
        return type.render(statistic.median)
//        if (statistic.count == 1) return type.render(statistic.median)
//        return type.render(statistic.mean) + "±" + type.render(statistic.stddev)
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


fun Expr.findRandomVariables() : Set<RandomVariableExpr> {
    val result = mutableSetOf<RandomVariableExpr>()
    visit {
        if (it is RandomVariableExpr) result.add(it)
    }
    return result
}