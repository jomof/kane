package com.github.jomof.kane.impl

import com.github.jomof.kane.*
import com.github.jomof.kane.anonymous
import com.github.jomof.kane.impl.types.algebraicType
import com.github.jomof.kane.impl.visitor.visit
import kotlin.random.Random
import kotlin.reflect.KProperty


interface RandomVariableExpr : ScalarExpr {
    fun sample(random : Random) : ConstantScalar
}

data class DiscreteUniformRandomVariable(
    val values: List<Double>,
    val identity: Any = Any(),
    override val name: Id = anonymous
) : RandomVariableExpr, StatisticExpr, INameableScalar {
    init {
        track()
    }

    override fun sample(random: Random): ConstantScalar {
        val index = random.nextInt(values.size)
        val sample = values[index]
        return ConstantScalar(sample)
    }

    override fun toNamed(name: Id): ScalarExpr {
        if (this.name === anonymous) return dup(name = name)
        return NamedScalar(name, this)
    }

    override fun toUnnamed() = dup(name = anonymous)

    override fun toString() = render()

    fun dup(values: List<Double> = this.values, name: Id = this.name): DiscreteUniformRandomVariable {
        if (values === this.values && name == this.name) return this
        return DiscreteUniformRandomVariable(values, identity, name)
    }
}


class StreamingSampleStatisticExpr(
    val statistic: StreamingSamples,
    override val name: Id = anonymous
) : StatisticExpr, INameableScalar {
    override fun toNamed(name: Id): ScalarExpr {
        if (this.name === anonymous) return dup(name = name)
        return NamedScalar(name, this)
    }

    override fun toUnnamed() = dup(name = anonymous)

    override fun toString(): String {
        return this.algebraicType.render(statistic.median)
    }

    fun dup(name: Id = this.name, statistic: StreamingSamples = this.statistic): StreamingSampleStatisticExpr {
        if (name === this.name && statistic == this.statistic) return this
        return StreamingSampleStatisticExpr(statistic, name)
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
    return DiscreteUniformRandomVariable(elements)
}


fun Expr.findRandomVariables(): Set<DiscreteUniformRandomVariable> {
    val result = mutableSetOf<DiscreteUniformRandomVariable>()
    visit {
        if (it is DiscreteUniformRandomVariable) result.add(it)
    }
    return result
}