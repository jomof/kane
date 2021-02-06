package com.github.jomof.kane

import com.github.jomof.kane.impl.toNamed
import com.github.jomof.kane.impl.types.KaneType
import kotlin.reflect.KProperty

interface Expr

interface TypedExpr<E : Any> : Expr {
    val type: KaneType<E>
}

interface AlgebraicExpr : Expr

interface ScalarExpr : AlgebraicExpr {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
}
interface MatrixExpr : AlgebraicExpr {
    val columns: Int
    val rows: Int
    operator fun get(column: Int, row: Int): ScalarExpr
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
}
