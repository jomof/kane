package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.functions.AlgebraicBinaryScalar
import com.github.jomof.kane.functions.AlgebraicUnaryScalar

private fun AlgebraicExpr.expandNamedCells(lookup : Map<String, Expr>): AlgebraicExpr {
    fun ScalarExpr.self() = expandNamedCells(lookup) as ScalarExpr
    fun MatrixExpr.self() = expandNamedCells(lookup) as MatrixExpr
    val result = when(this) {
        is NamedScalarVariable -> this
        is AlgebraicUnaryScalar -> copy(value = value.self())
        is AlgebraicBinaryScalar -> {
            val changed = copy(left = left.self(), right = right.self())
            if (changed !== this) changed.memoizeAndReduceArithmetic()
            else this
        }
        is CoerceScalar -> {
            val result : ScalarExpr = when(value) {
                is ScalarVariable -> constant(value.initial)
                is ScalarExpr -> value.self() as ScalarExpr
                is AbsoluteCellReferenceExpr -> {
                    val ref = "$value"
                    val result = (lookup[ref]?:constant(0.0)) as ScalarExpr
                    result.self()
                }
                else -> error("${value.javaClass}")
            }
            result
        }
//        is AbsoluteCellReferenceExpr -> {
//            val ref = "$this"
//            val result = lookup.getValue(ref).expandNamedCells(lookup)
//            result as ScalarExpr
//        }
        is ConstantScalar -> this
        else -> error("$javaClass")
    }
    return result
}
fun Expr.expandNamedCells(lookup : Map<String, Expr>) : Expr {
    return when(this) {
        is AlgebraicExpr -> expandNamedCells(lookup)
        else -> error("$javaClass")
    }
}