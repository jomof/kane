package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.functions.AlgebraicBinaryScalar
import com.github.jomof.kane.functions.AlgebraicUnaryScalar

private fun <E:Number> AlgebraicExpr<E>.expandNamedCells(lookup : Map<String, Expr>): AlgebraicExpr<E> {
    fun <E : Number> ScalarExpr<E>.self() = expandNamedCells(lookup) as ScalarExpr<E>
    fun <E : Number> MatrixExpr<E>.self() = expandNamedCells(lookup) as MatrixExpr<E>
    val result = when(this) {
        is NamedScalarVariable -> this
        is AlgebraicUnaryScalar -> copy(value = value.self())
        is AlgebraicBinaryScalar -> copy(left = left.self(), right = right.self())
        is CoerceScalar<*> -> {
            val result : ScalarExpr<E> = when(value) {
                is ScalarVariable<*> -> constant(value.initial as E)
                is ScalarExpr<*> -> value.self() as ScalarExpr<E>
                is AbsoluteCellReferenceExpr -> {
                    val ref = "$value"
                    val result = lookup.getValue(ref).expandNamedCells(lookup)
                    result as ScalarExpr<E>
                }
                else -> error("${value.javaClass}")
            }
            result
        }
//        is AbsoluteCellReferenceExpr -> {
//            val ref = "$this"
//            val result = lookup.getValue(ref).expandNamedCells(lookup)
//            result as ScalarExpr<E>
//        }
        is ConstantScalar -> this
        else -> error("$javaClass")
    }
    return result
}
fun Expr.expandNamedCells(lookup : Map<String, Expr>) : Expr {
    return when(this) {
        is AlgebraicExpr<*> -> expandNamedCells(lookup)
        else -> error("$javaClass")
    }
}