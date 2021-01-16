package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.functions.AlgebraicBinaryScalar
import com.github.jomof.kane.functions.AlgebraicUnaryMatrix
import com.github.jomof.kane.functions.AlgebraicUnaryMatrixScalar
import com.github.jomof.kane.functions.AlgebraicUnaryScalar

private fun AlgebraicExpr.reduceArithmeticImpl(
    cells : Map<String, Expr>,
    variables : Set<String>,
    depth : Int) : AlgebraicExpr? {
    fun ScalarExpr.self() = reduceArithmeticImpl(cells, variables, depth + 1) as ScalarExpr?
    fun MatrixExpr.self() = reduceArithmeticImpl(cells, variables, depth + 1) as MatrixExpr?
    if (depth > 1000)
        return null
    val result = when(this) {
        is NamedScalar-> copy(scalar = scalar.self() ?: return null)
        is NamedMatrix -> copy(matrix = matrix.self() ?: return null)
        is AlgebraicUnaryScalar -> copy(value = value.self() ?: return null)
        is AlgebraicUnaryMatrix -> copy(value = value.self() ?: return null)
        is AlgebraicUnaryMatrixScalar -> copy(value = value.self() ?: return null)
        is AlgebraicBinaryScalar -> copyReduce(left = left.self() ?: return null, right = right.self() ?: return null)
        is CoerceScalar -> {
            val result: ScalarExpr? = when (value) {
                is ScalarVariable -> {
                    // This variable is not being optimized so treat it as constant
                    ConstantScalar(value.initial, type)
                }
                is AbsoluteCellReferenceExpr -> {
                    val ref = "$value"
                    if (variables.contains(ref)) this
                    else {
                        val result = cells[ref] ?: constant(0.0)
                        when {
                            result is ConstantScalar && result.type.java == type.java -> result as ScalarExpr
                            result is ConstantScalar -> constant(type.coerceFrom(result.value), type)
                            else -> this
                        }
                    }
                }
                is AlgebraicExpr -> {
                    val result = value.reduceArithmeticImpl(cells, variables, depth + 1)
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
        else ->
            error("$javaClass")
    }
    return result
}
private fun Expr.reduceArithmeticImpl(cells : Map<String, Expr>, variables : Set<String>, depth : Int) : Expr? {
    return when(this) {
        is AlgebraicExpr -> reduceArithmeticImpl(cells, variables, depth + 1)?.memoizeAndReduceArithmetic()
        is ValueExpr<*> -> this
        is AbsoluteCellReferenceExpr -> this
        else -> error("$javaClass")
    }
}
fun Expr.reduceArithmetic(cells : Map<String, Expr>, variables : Set<String>) : Expr {
    return reduceArithmeticImpl(cells, variables, 1) ?: this
}
fun NamedExpr.reduceArithmetic(cells : Map<String, Expr>, variables : Set<String>) : NamedExpr =
    (this as Expr).reduceArithmetic(cells, variables) as NamedExpr