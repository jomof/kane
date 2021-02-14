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

interface StatisticExpr : ScalarExpr {
}

interface MatrixExpr : AlgebraicExpr {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
}
