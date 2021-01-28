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
    private fun Expr.reduceArithmeticImpl(
        cells: Map<String, Expr>,
        depth: Int
    ): AlgebraicExpr? {

        if (depth > 1000)
            return null
        return when (this) {
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
            is AlgebraicUnaryScalarStatistic -> {
                if (value is ScalarListExpr)
                    op.reduceArithmetic(value.values)
                else {
                    copy(
                        value = value.reduceArithmeticImpl(
                            cells,
                            depth + 1
                        ) as ScalarExpr? ?: return null
                    )
                }
            }
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
            is AlgebraicBinaryMatrix -> copy(
                left = left.reduceArithmeticImpl(cells, depth + 1) as MatrixExpr,
                right = right.reduceArithmeticImpl(cells, depth + 1) as MatrixExpr
            )
            is AlgebraicBinaryScalar -> {
                val left = left.reduceArithmeticImpl(
                    cells,
                    depth + 1
                ) ?: return null
                val right = right.reduceArithmeticImpl(
                    cells,
                    depth + 1
                ) ?: return null
                assert(left is ScalarExpr) {
                    this.left.reduceArithmeticImpl(
                        cells,
                        depth + 1
                    )
                    "expected ScalarExpr but got ${left.javaClass}"
                }
                copyReduce(
                    left = left as ScalarExpr,
                    right = right as ScalarExpr
                )
            }
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
                    is SheetRangeExpr -> {
                        val ref = "$value"
                        if (excludeVariables.contains(ref)) this
                        else {
                            when (value.range) {
                                is CellRange -> {
                                    val result = cells[ref] ?: constant(0.0)
                                    when {
                                        result is ConstantScalar && result.type.java == type.java -> result
                                        result is ConstantScalar -> constant(type.coerceFrom(result.value), type)
                                        result is NamedScalarVariable -> result
                                        else -> this
                                    }
                                }
                                is ColumnRange -> {
                                    val contained = cells
                                        .filter { (name, _) -> value.range.contains(name) }
                                        .map { (_, expr) ->
                                            when (expr) {
                                                is ScalarExpr -> expr
                                                else -> copy(value = expr)
                                            }
                                        }
                                    if (contained.size == 1) copy(value = contained.single())
                                    else ScalarListExpr(contained, type)
                                }
                                else ->
                                    error("${value.range.javaClass}")
                            }
                        }
                    }
                    is AlgebraicUnaryRangeStatistic -> {
                        val exprs = cells
                            .filter { value.range.contains(it.key) }
                            .map {
                                val reduced = it.value.reduceArithmeticImpl(cells, depth + 1) ?: return null
                                reduced as ScalarExpr
                            }
                        value.op.reduceArithmetic(exprs)!!
                    }
                    is NamedSheetRangeExpr -> (copy(value = value.range).reduceArithmeticImpl(cells, depth + 1)
                        ?: return null) as ScalarExpr
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
                else (cells[name] as AlgebraicExpr).reduceArithmeticImpl(cells, depth + 1)
            }
            is RandomVariableExpr ->
                randomVariableValues?.getValue(this) ?: this
            else ->
                error("$javaClass")
        }
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
            is SheetRangeExpr -> expr
            else -> error("$javaClass")
        }
    }
}

private fun Sheet.sampleOrReduceArithmetic(
    excludeVariables : Set<String>,
    randomVariableValues : Map<RandomVariableExpr, ConstantScalar>?
) : Sheet {
    val new = cells.toMutableMap()
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

    return copy(cells = new)
}

fun Sheet.reduceArithmeticNoSample(excludeVariables : Set<String>) =
    sampleOrReduceArithmetic(excludeVariables, null)
fun Sheet.sampleReduceArithmetic(excludeVariables : Set<String>, randomVariableValues : Map<RandomVariableExpr, ConstantScalar>) =
    sampleOrReduceArithmetic(excludeVariables, randomVariableValues)