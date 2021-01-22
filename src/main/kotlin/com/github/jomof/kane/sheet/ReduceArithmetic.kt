package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.functions.*

/**
 * Functions to reduce sheet expressions where random variable samples are fixed once chosen.
 */
private class SingleSampleReducer(
    // Set of variables to *not* expand.
    val excludeVariables : Set<String>,
    // Random  variables' values, null if random variables should not be expanded
    val randomVariableValues : Map<RandomVariableExpr, ConstantScalar>?
) {
    val memo : MutableMap<TypedExpr<*>,TypedExpr<*>> = mutableMapOf()
    val selfMemo : MutableMap<TypedExpr<*>,TypedExpr<*>> = mutableMapOf()
    private fun AlgebraicExpr.reduceArithmeticImpl(
        cells : Map<String, Expr>,
        depth: Int
    ): AlgebraicExpr? {

        if (depth > 1000)
            return null
        val result = when (this) {
            is NamedScalar -> copy(
                scalar = scalar.reduceArithmeticImpl(
                    cells,
                    depth + 1
                ) as ScalarExpr? ?: return null
            )
            is NamedMatrix -> copy(
                matrix = matrix.reduceArithmeticImpl(
                    cells,
                    depth + 1
                ) as MatrixExpr? ?: return null
            )
            is AlgebraicUnaryScalar -> copy(
                value = value.reduceArithmeticImpl(
                    cells,
                    depth + 1
                ) as ScalarExpr? ?: return null
            )
            is AlgebraicUnaryScalarStatistic -> copy(
                value = value.reduceArithmeticImpl(
                    cells,
                    depth + 1
                ) as ScalarExpr? ?: return null
            )
            is AlgebraicUnaryMatrix -> copy(
                value = value.reduceArithmeticImpl(
                    cells,
                    depth + 1
                ) as MatrixExpr? ?: return null
            )
            is AlgebraicUnaryMatrixScalar -> copy(
                value = value.reduceArithmeticImpl(
                    cells,
                    depth + 1
                ) as MatrixExpr? ?: return null
            )
            is AlgebraicBinaryScalar -> copyReduce(
                left = left.reduceArithmeticImpl(
                    cells,
                    depth + 1
                ) as ScalarExpr? ?: return null,
                right = right.reduceArithmeticImpl(
                    cells,
                    depth + 1
                ) as ScalarExpr? ?: return null
            )
            is AlgebraicBinaryMatrixScalar -> copy(
                left = left.reduceArithmeticImpl(
                    cells,
                    depth + 1
                ) as MatrixExpr? ?: return null,
                right = right.reduceArithmeticImpl(
                    cells,
                    depth + 1
                ) as ScalarExpr? ?: return null
            )
            is AlgebraicBinaryScalarStatistic -> copy(
                left = left.reduceArithmeticImpl(
                    cells,
                    depth + 1
                ) as ScalarExpr? ?: return null,
                right = right.reduceArithmeticImpl(
                    cells,
                    depth + 1
                ) as ScalarExpr? ?: return null
            )
            is CoerceScalar -> {
                val result: ScalarExpr = when (value) {
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
                    val result = element.reduceArithmeticImpl(
                        cells,
                        depth + 1
                    ) as ScalarExpr? ?: return null
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

    fun reduceArithmetic(
        expr : Expr,
        cells: Map<String, Expr>): Expr {
        return when (expr) {
            is AlgebraicExpr -> expr.reduceArithmeticImpl(
                cells,
                1
            )?.memoizeAndReduceArithmetic(memo = memo, selfMemo = selfMemo) ?: expr
            is ValueExpr<*> -> expr
            is ComputableCellReference -> expr
            else -> error("$javaClass")
        }
    }
}

private fun Sheet.sampleOrReduceArithmetic(
    excludeVariables : Set<String>,
    randomVariableValues : Map<RandomVariableExpr, ConstantScalar>?
) : Sheet {
    var new = cells.toMutableMap()
    var done = false

    val reducer = SingleSampleReducer(excludeVariables, randomVariableValues)

    while (!done) {

        done = true
        new.forEach { (name, expr) ->
            val reduced = reducer.reduceArithmetic(expr, new)
            if (!done || reduced != expr) {
                done = false
            }
            //name to reduced
            new[name] = reduced
        }
    }

    return Sheet.of(columnDescriptors, new)
}

fun Sheet.reduceArithmeticNoSample(excludeVariables : Set<String>) =
    sampleOrReduceArithmetic(excludeVariables, null)
fun Sheet.sampleReduceArithmetic(excludeVariables : Set<String>, randomVariableValues : Map<RandomVariableExpr, ConstantScalar>) =
    sampleOrReduceArithmetic(excludeVariables, randomVariableValues)