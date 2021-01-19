package com.github.jomof.kane

import com.github.jomof.kane.functions.AlgebraicBinaryScalar
import com.github.jomof.kane.functions.AlgebraicBinaryScalarStatistic
import com.github.jomof.kane.functions.AlgebraicUnaryRandomVariableScalar
import kotlin.random.Random

/**
 * Evaluate an expression, substitute sample value for random variables.
 */
private fun Expr.sampleEval(
    random : Random,
    randomVariableValues : MutableMap<RandomVariableExpr, ConstantScalar>) : Expr {
    fun ScalarExpr.self() = sampleEval(random, randomVariableValues) as ScalarExpr
    return when(this) {
        is NamedScalar -> copy(scalar = scalar.self())
        is RandomVariableExpr ->
            randomVariableValues.computeIfAbsent(this) {
                sample(random)
            }
        is AlgebraicBinaryScalar -> copyReduce(left = left.self(), right = right.self(),)
        is AlgebraicBinaryScalarStatistic -> copy(left = left.self(), right = right.self(),)
        is AlgebraicUnaryRandomVariableScalar -> copy(value = value.self())
        is ConstantScalar -> this
        else ->
            error("$javaClass")
    }
}

private fun Expr.convertToStatistics() : Expr {
    fun ScalarExpr.self() = convertToStatistics() as ScalarExpr
    return when(this) {
        is ConstantScalar -> {
            val stream = StreamingSamples()
            stream.insert(value)
            ScalarStatistic(stream, type)
        }
        is NamedScalar -> copy(scalar = scalar.self())
        is AlgebraicUnaryRandomVariableScalar -> copy(value = value.self())
        is AlgebraicBinaryScalar -> copyReduce(left = left.self(), right = right.self())
        is AlgebraicBinaryScalarStatistic -> copy(left = left.self(), right = right.self())
        else ->
            error("$javaClass")
    }
}

private fun Expr.accumulateStatistics(incoming : Expr) {
    when {
        this is NamedScalar && incoming is NamedScalar -> {
            assert(name == incoming.name)
            scalar.accumulateStatistics(incoming.scalar)
        }
        this is ScalarStatistic && incoming is ConstantScalar -> {
            statistic.insert(incoming.value)
        }
        this is AlgebraicUnaryRandomVariableScalar && incoming is AlgebraicUnaryRandomVariableScalar -> {
            value.accumulateStatistics(incoming.value)
        }
        this is AlgebraicBinaryScalar && incoming is AlgebraicBinaryScalar -> {
            this.left.accumulateStatistics(incoming.left)
            this.right.accumulateStatistics(incoming.right)
        }
        this is AlgebraicBinaryScalarStatistic && incoming is AlgebraicBinaryScalarStatistic -> {
            this.left.accumulateStatistics(incoming.left)
            this.right.accumulateStatistics(incoming.right)
        }
        else ->
            error("$javaClass : ${incoming.javaClass}")
    }
}

private fun Expr.reduceStatistics() : Expr {
    fun ScalarExpr.self() = reduceStatistics() as ScalarExpr
    return when(this) {
        is NamedScalar -> copy(scalar = scalar.self())
        is AlgebraicUnaryRandomVariableScalar -> {
            when(value) {
                is ScalarStatistic -> op.reduceArithmetic(value)
                is NamedScalar -> {
                    when(value.scalar) {
                        is ScalarStatistic -> op.reduceArithmetic(value.scalar)
                        else -> error("${value.javaClass}")
                    }
                }
                else -> error("${value.javaClass}")
            }
        }
        is AlgebraicBinaryScalarStatistic ->
            when {
                left is ScalarStatistic -> op.reduceArithmetic(left, right.self())
                left is NamedScalar && left.scalar is ScalarStatistic ->
                    op.reduceArithmetic(left.scalar, right.self())
                else -> error("${left.javaClass}")
            }

        is ScalarStatistic -> constant(statistic.median)
        is AlgebraicBinaryScalar -> copyReduce(left = left.self(), right = right.self())
        else -> error("$javaClass")
    }
}

fun Expr.eval(random : Random = Random(7), samples : Int = 100) : Expr {
    val randomVariables = findRandomVariables()
    val firstSample = sampleEval(random, mutableMapOf())
    if (randomVariables.isEmpty()) return firstSample
    val stats = firstSample.convertToStatistics()
    repeat(samples - 1) {
        stats.accumulateStatistics(sampleEval(random, mutableMapOf()))
    }
    return stats.reduceStatistics()
}