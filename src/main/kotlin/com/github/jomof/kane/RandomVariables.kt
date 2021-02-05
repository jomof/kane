package com.github.jomof.kane

import com.github.jomof.kane.types.algebraicType
import com.github.jomof.kane.visitor.visit
import kotlin.random.Random
import kotlin.reflect.KProperty

interface RandomVariableExpr : ScalarExpr {
    fun sample(random : Random) : ConstantScalar
}

class DiscreteUniformRandomVariable(
    val values: List<Double>
) : RandomVariableExpr, ScalarExpr {
    init {
        track()
    }

    override fun sample(random: Random): ConstantScalar {
        val index = random.nextInt(values.size)
        val sample = values[index]
        return ConstantScalar(sample)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)

    override fun toString(): String {
        val min = values.minByOrNull { it } ?: 0.0
        val max = values.maxByOrNull { it } ?: 0.0
        return "random($min to $max)"
    }

    fun copy(values: List<Double> = this.values): Nothing =
        error("Shouldn't copy DiscreteUniformRandomVariable")
}

class ScalarStatistic(
    val statistic: StreamingSamples
) : ScalarExpr {
    override fun toString(): String {
        return this.algebraicType.render(statistic.median)
//        if (statistic.count == 1) return type.render(statistic.median)
//        return type.render(statistic.mean) + "±" + type.render(statistic.stddev)
    }

    fun copy(statistic: StreamingSamples = this.statistic) =
        ScalarStatistic(statistic)
}

fun randomOf(range : Pair<Double, Double>, step : Double = 1.0) : DiscreteUniformRandomVariable {
    val (start, stop) = range
    var current = start
    val elements = mutableListOf<Double>()
    while(current < stop + 1) {
        elements += current
        current += step
    }
    return DiscreteUniformRandomVariable(elements)
}


fun Expr.findRandomVariables() : Set<RandomVariableExpr> {
    val result = mutableSetOf<RandomVariableExpr>()
    visit {
        if (it is RandomVariableExpr) result.add(it)
    }
    return result
}