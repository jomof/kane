package com.github.jomof.kane

import com.github.jomof.kane.functions.*
import com.github.jomof.kane.sheet.*

/**
 * Evaluate an expression, substitute sample value for random variables.
 */
private fun Expr.sampleEval(
    randomVariableValues : Map<RandomVariableExpr, ConstantScalar>) : Expr {
    fun ScalarExpr.self() = sampleEval(randomVariableValues) as ScalarExpr
    return when (this) {
        is NamedScalar -> copy(scalar = scalar.self())
        is RandomVariableExpr -> randomVariableValues.getValue(this)
        is AlgebraicBinaryScalar -> copyReduce(left = left.self(), right = right.self())
        is AlgebraicBinaryScalarStatistic -> copy(left = left.self(), right = right.self())
        is AlgebraicUnaryScalarStatistic -> copy(value = value.self())
        is ConstantScalar -> this
        else ->
            error("$javaClass")
    }
}

private fun Expr.convertToStatistics() : Expr {
    fun ScalarExpr.self() = convertToStatistics() as ScalarExpr
    fun MatrixExpr.self() = convertToStatistics() as MatrixExpr
    return when(this) {
        is ConstantScalar -> {
            val stream = StreamingSamples()
            stream.insert(value)
            ScalarStatistic(stream, type)
        }
        is NamedScalar -> copy(scalar = scalar.self())
        is NamedMatrix -> copy(matrix = matrix.self())
        is AlgebraicUnaryScalarStatistic -> copy(value = value.self())
        is AlgebraicUnaryScalar -> copy(value = value.self())
        is AlgebraicUnaryMatrix -> copy(value = value.self())
        is AlgebraicBinaryScalar -> copyReduce(left = left.self(), right = right.self())
        is AlgebraicBinaryMatrixScalar -> copy(left = left.self(), right = right.self())
        is AlgebraicBinaryScalarStatistic -> copy(left = left.self(), right = right.self())
        is Sheet -> {
            val cells = cells.map { (name, expr) -> name to expr.convertToStatistics() }.toMap()
            copy(cells = cells)
        }
        is SheetRangeExpr -> this
        is ValueExpr<*> -> this
        is CoerceScalar -> copy(value = value.convertToStatistics())
        is DataMatrix -> map { it.self() }
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
        this is NamedMatrix && incoming is NamedMatrix -> {
            assert(name == incoming.name)
            matrix.accumulateStatistics(incoming.matrix)
        }
        this is ScalarStatistic && incoming is ConstantScalar -> {
            statistic.insert(incoming.value)
        }
        this is AlgebraicUnaryScalarStatistic && incoming is AlgebraicUnaryScalarStatistic -> {
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
        this is Sheet && incoming is Sheet -> {
            cells.forEach { (name, expr) ->
                expr.accumulateStatistics(incoming.cells.getValue(name))
            }
        }
        this is SheetRangeExpr && incoming is SheetRangeExpr -> {
        }
        this is CoerceScalar && incoming is CoerceScalar -> value.accumulateStatistics(incoming.value)
        this is DataMatrix && incoming is DataMatrix -> {
            (elements zip incoming.elements).forEach { (left, incoming) -> left.accumulateStatistics(incoming) }
        }
        this is ValueExpr<*> && incoming is ValueExpr<*> -> {}
        else ->
            error("$javaClass : ${incoming.javaClass}")
    }
}

private fun Expr.reduceStatistics() : Expr {
    fun ScalarExpr.self() = reduceStatistics() as ScalarExpr
    fun MatrixExpr.self() = reduceStatistics() as MatrixExpr
    return when(this) {
        is NamedScalar -> copy(scalar = scalar.self())
        is NamedMatrix -> copy(matrix = matrix.self())
        is AlgebraicUnaryScalarStatistic -> {
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
        is ScalarStatistic ->
            if (statistic.stddev == 0.0) constant(statistic.median, type)
            else this
        is AlgebraicBinaryScalar -> copyReduce(left = left.self(), right = right.self())
        is DataMatrix -> map { it.self() }
        is Sheet -> copy(cells = cells.map { (name, cell) -> name to cell.reduceStatistics() }.toMap())
        is SheetRangeExpr -> this
        is ValueExpr<*> -> this
        else -> error("$javaClass")
    }
}

fun Expr.eval() : Expr {
    val randomVariables = findRandomVariables()
    if (randomVariables.isEmpty()) return sampleEval(mutableMapOf())

    var stats : Expr? = null
    val randomVariableElements = randomVariables.map { variable ->
        (variable as DiscreteUniformRandomVariable).values.map { value ->
            constant(value, variable.type)
        }
    }
    cartesianOf(randomVariableElements) { randomVariableValues ->
        val variableValues = (randomVariables zip randomVariableValues)
            .map { (variable, value) -> variable to (value as ConstantScalar) }
            .toMap()
        val sample = sampleEval(variableValues)
        if (stats == null) {
            stats = sample.convertToStatistics()
        } else {
            stats!!.accumulateStatistics(sample)
        }
    }
    return stats!!.reduceStatistics()
}

fun Sheet.reduceArithmetic(excludeVariables : Set<String>) : Sheet {
    val randomVariables = findRandomVariables()
    if (randomVariables.isEmpty()) return reduceArithmeticNoSample(excludeVariables)
    val reduced = reduceArithmeticNoSample(excludeVariables)
    var stats : Expr? = null
    val randomVariableElements = randomVariables.map { variable ->
        (variable as DiscreteUniformRandomVariable).values.map { value ->
            constant(value, variable.type)
        }
    }
    cartesianOf(randomVariableElements) { randomVariableValues ->
        val variableValues = (randomVariables zip randomVariableValues)
            .map { (variable, value) -> variable to (value as ConstantScalar) }
            .toMap()
        val sample = reduced.sampleReduceArithmetic(excludeVariables, variableValues)
        if (stats == null) {
            stats = sample.convertToStatistics() as Sheet
        } else {
            stats!!.accumulateStatistics(sample)
        }
    }
    return stats!!.reduceStatistics() as Sheet
}

fun Sheet.eval() = reduceArithmetic(setOf())