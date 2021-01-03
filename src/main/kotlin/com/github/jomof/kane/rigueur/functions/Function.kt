package com.github.jomof.kane.rigueur.functions

import com.github.jomof.kane.rigueur.ScalarExpr

// f(scalar,scalar)->scalar
interface AlgebraicScalarFunction {
    fun <E:Number> invoke(p1 : E, p2 : E) : E
    fun <E:Number> invoke(p1 : ScalarExpr<E>, p2 : ScalarExpr<E>) : ScalarExpr<E>
}

