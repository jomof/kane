package com.github.jomof.kane.rigueur.sheet

import com.github.jomof.kane.rigueur.*
import com.github.jomof.kane.rigueur.functions.AlgebraicBinaryScalar
import com.github.jomof.kane.rigueur.functions.AlgebraicUnaryMatrix
import com.github.jomof.kane.rigueur.functions.AlgebraicUnaryMatrixScalar
import com.github.jomof.kane.rigueur.functions.AlgebraicUnaryScalar

private fun <E:Number> AlgebraicExpr<E>.reduceArithmeticImpl(
    cells : Map<String, Expr>,
    variables : Set<String>,
    depth : Int) : AlgebraicExpr<E>? {
    fun <E:Number> ScalarExpr<E>.self() = reduceArithmeticImpl(cells, variables, depth + 1) as ScalarExpr<E>?
    fun <E:Number> MatrixExpr<E>.self() = reduceArithmeticImpl(cells, variables, depth + 1) as MatrixExpr<E>?
    if (depth > 1000)
        return null
    val result = when(this) {
        is NamedMatrix -> copy(matrix = matrix.self() ?: return null)
        is AlgebraicUnaryScalar -> copy(value = value.self() ?: return null)
        is AlgebraicUnaryMatrix -> copy(value = value.self() ?: return null)
        is AlgebraicUnaryMatrixScalar -> copy(value = value.self() ?: return null)
        is AlgebraicBinaryScalar -> copyReduce(left = left.self() ?: return null, right = right.self() ?: return null)
        is CoerceScalar -> {
            val result: ScalarExpr<E>? = when (value) {
                is ScalarVariable<*> -> {
                    // This variable is not being optimized so treat it as constant
                    ConstantScalar(value.initial as E, type)
                }
                is AbsoluteCellReferenceExpr -> {
                    val ref = "$value"
                    if (variables.contains(ref)) this
                    else {
                        val result = cells.getValue(ref).reduceArithmeticImpl(cells, variables, depth + 1)
                        when {
                            result == null -> null
                            result is TypedExpr<*> && result.type == type -> result as ScalarExpr<E>
                            else -> copy(value = result).self()
                        }
                    }
                }
                is AlgebraicExpr<*> -> {
                    val result = value.reduceArithmeticImpl(cells, variables, depth + 1)
                    if (value.type == type) value as ScalarExpr<E>
                    else copy(value = result as Expr)
                }
                else -> error("${value.javaClass}")
            }
            result
        }
        is ConstantScalar -> this
        is DataMatrix -> {
            val scalars = mutableListOf<ScalarExpr<E>>()
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
        is AlgebraicExpr<*> -> reduceArithmeticImpl(cells, variables, depth + 1)?.memoizeAndReduceArithmetic()
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