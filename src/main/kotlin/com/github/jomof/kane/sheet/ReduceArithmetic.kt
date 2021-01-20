package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.functions.*
import kotlin.random.Random

private fun AlgebraicExpr.reduceArithmeticImpl(
    cells : Map<String, Expr>,
    variables : Set<String>,
    random : Random,
    randomVariableValues : MutableMap<RandomVariableExpr, ConstantScalar>,
    depth : Int) : AlgebraicExpr? {
    fun ScalarExpr.self() = reduceArithmeticImpl(cells, variables, random, randomVariableValues, depth + 1) as ScalarExpr?
    fun MatrixExpr.self() = reduceArithmeticImpl(cells, variables, random, randomVariableValues, depth + 1) as MatrixExpr?
    if (depth > 1000)
        return null
    val result = when(this) {
        is NamedScalar-> copy(scalar = scalar.self() ?: return null)
        is NamedMatrix -> copy(matrix = matrix.self() ?: return null)
        is AlgebraicUnaryScalar -> copy(value = value.self() ?: return null)
        is AlgebraicUnaryRandomVariableScalar -> copy(value = value.self() ?: return null)
        is AlgebraicUnaryMatrix -> copy(value = value.self() ?: return null)
        is AlgebraicUnaryMatrixScalar -> copy(value = value.self() ?: return null)
        is AlgebraicBinaryScalar -> copyReduce(left = left.self() ?: return null, right = right.self() ?: return null)
        is AlgebraicBinaryMatrixScalar -> copy(left = left.self() ?: return null, right = right.self() ?: return null)
        is AlgebraicBinaryScalarStatistic -> copy(left = left.self() ?: return null, right = right.self() ?: return null)
        is CoerceScalar -> {
            val result: ScalarExpr? = when (value) {
                is ScalarVariable -> {
                    // This variable is not being optimized so treat it as constant
                    ConstantScalar(value.initial, type)
                }
                is ComputableCellReference -> {
                    val ref = "$value"
                    if (variables.contains(ref)) this
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
                    if (variables.contains(ref)) this
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
                        variables,
                        random,
                        randomVariableValues,
                        depth + 1)
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
            for(element in elements) {
                val result = element.self() ?: return null
                scalars += result
            }
            copy(elements = scalars)
        }
        is ScalarVariable -> ConstantScalar(initial, type)
        is NamedScalarVariable -> {
            if(variables.contains(name)) this
            else cells[name] as AlgebraicExpr
        }
        is RandomVariableExpr ->
            randomVariableValues.computeIfAbsent(this) {
                sample(random)
            }
        else ->
            error("$javaClass")
    }
    return result
}
private fun Expr.reduceArithmeticImpl
            (cells : Map<String, Expr>,
             variables : Set<String>,
             random : Random,
             randomVariableValues : MutableMap<RandomVariableExpr, ConstantScalar>,
             depth : Int) : Expr? {
    return when(this) {
        is AlgebraicExpr -> reduceArithmeticImpl(
            cells,
            variables,
            random,
            randomVariableValues,
            depth + 1)?.memoizeAndReduceArithmetic()
        is ValueExpr<*> -> this
        is ComputableCellReference -> this
        else -> error("$javaClass")
    }
}

private fun Expr.reduceArithmetic(
    cells : Map<String, Expr>,
    variables : Set<String>,
    random : Random) : Expr {
    return reduceArithmeticImpl(
        cells,
        variables,
        random,
        mutableMapOf(),
        1) ?: this
}

fun Sheet.sampleReduceArithmetic(variables : Set<String>, random : Random) : Sheet {
    var new = cells
    var done = false

    while (!done) {
        done = true
        new = new.map { (name, expr) ->
            val reduced = expr.reduceArithmetic(new, variables, random)
            if (!done || reduced != expr) {
                done = false
            }
            name to reduced
        }.toMap()
    }

    return Sheet.of(columnDescriptors, new)
}

