package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.functions.*
import kotlin.random.Random

/**
 * Functions to reduce sheet expressions where random variable samples are fixed once chosen.
 */
private class SingleSampleReducer(
    // Set of variables to *not* expand.
    val excludeVariables : Set<String>,
    // Random  variables' values, null if random variables should not be expanded
    val randomVariableValues : Map<RandomVariableExpr, ConstantScalar>?
) {
    private fun AlgebraicExpr.reduceArithmeticImpl(
        cells : Map<String, Expr>,
        depth: Int
    ): AlgebraicExpr? {
        fun ScalarExpr.self() = reduceArithmeticImpl(
            cells,
            depth + 1
        ) as ScalarExpr?

        fun MatrixExpr.self() = reduceArithmeticImpl(
            cells,
            depth + 1
        ) as MatrixExpr?
        if (depth > 1000)
            return null
        val result = when (this) {
            is NamedScalar -> copy(scalar = scalar.self() ?: return null)
            is NamedMatrix -> copy(matrix = matrix.self() ?: return null)
            is AlgebraicUnaryScalar -> copy(value = value.self() ?: return null)
            is AlgebraicUnaryRandomVariableScalar -> copy(value = value.self() ?: return null)
            is AlgebraicUnaryMatrix -> copy(value = value.self() ?: return null)
            is AlgebraicUnaryMatrixScalar -> copy(value = value.self() ?: return null)
            is AlgebraicBinaryScalar -> copyReduce(
                left = left.self() ?: return null,
                right = right.self() ?: return null
            )
            is AlgebraicBinaryMatrixScalar -> copy(
                left = left.self() ?: return null,
                right = right.self() ?: return null
            )
            is AlgebraicBinaryScalarStatistic -> copy(
                left = left.self() ?: return null,
                right = right.self() ?: return null
            )
            is CoerceScalar -> {
                val result: ScalarExpr? = when (value) {
                    is ScalarVariable -> {
                        // This variable is not being optimized so treat it as constant
                        ConstantScalar(value.initial, type)
                    }
                    is ComputableCellReference -> {
                        val ref = "$value"
                        if (excludeVariables.contains(ref)) this
                        else {
                            val result = cells[ref] ?: constant(0.0)
                            when {
                                result is ConstantScalar && result.type.java == type.java -> result
                                result is ConstantScalar -> constant(type.coerceFrom(result.value), type)
                                result is NamedScalarVariable -> result
                                else -> this
                            }
                        }
                    }
                    is NamedComputableCellReference -> {
                        val ref = "$value"
                        if (excludeVariables.contains(ref)) this
                        else {
                            val result = cells[ref] ?: constant(0.0)
                            when {
                                result is ConstantScalar && result.type.java == type.java -> result
                                result is ConstantScalar -> constant(type.coerceFrom(result.value), type)
                                result is NamedScalarVariable -> result
                                else -> this
                            }
                        }
                    }
                    is AlgebraicExpr -> {
                        val result = value.reduceArithmeticImpl(
                            cells,
                            depth + 1
                        )
                        if (value.type == type) value as ScalarExpr
                        else copy(value = result as Expr)
                    }
                    else -> error("${value.javaClass}")
                }
                result
            }
            is ConstantScalar -> this
            is DataMatrix -> {
                val scalars = mutableListOf<ScalarExpr>()
                for (element in elements) {
                    val result = element.self() ?: return null
                    scalars += result
                }
                copy(elements = scalars)
            }
            is ScalarVariable -> ConstantScalar(initial, type)
            is NamedScalarVariable -> {
                if (excludeVariables.contains(name)) this
                else cells[name] as AlgebraicExpr
            }
            is RandomVariableExpr ->
                randomVariableValues?.getValue(this) ?: this
            else ->
                error("$javaClass")
        }
        return result
    }

    private fun Expr.reduceArithmeticImpl(
        cells: Map<String, Expr>,
        depth: Int
    ): Expr? {
        return when (this) {
            is AlgebraicExpr -> reduceArithmeticImpl(
                cells,
                depth + 1
            )?.memoizeAndReduceArithmetic()
            is ValueExpr<*> -> this
            is ComputableCellReference -> this
            else -> error("$javaClass")
        }
    }

    fun reduceArithmetic(
        expr : Expr,
        cells: Map<String, Expr>): Expr {
        return expr.reduceArithmeticImpl(cells, 1) ?: expr
    }
}

private fun Sheet.sampleOrReduceArithmetic(
    excludeVariables : Set<String>,
    randomVariableValues : Map<RandomVariableExpr, ConstantScalar>?
) : Sheet {
    var new = cells
    var done = false

    val reducer = SingleSampleReducer(excludeVariables, randomVariableValues)
    while (!done) {
        done = true
        new = new.map { (name, expr) ->
            val reduced = reducer.reduceArithmetic(expr, new)
            if (!done || reduced != expr) {
                done = false
            }
            name to reduced
        }.toMap()
    }

    return Sheet.of(columnDescriptors, new)
}

fun Sheet.reduceArithmeticNoSample(excludeVariables : Set<String>) =
    sampleOrReduceArithmetic(excludeVariables, null)
fun Sheet.sampleReduceArithmetic(excludeVariables : Set<String>, randomVariableValues : Map<RandomVariableExpr, ConstantScalar>) =
    sampleOrReduceArithmetic(excludeVariables, randomVariableValues)