package com.github.jomof.kane

import com.github.jomof.kane.impl.Id
import com.github.jomof.kane.impl.toNamed
import com.github.jomof.kane.impl.types.KaneType
import kotlin.reflect.KProperty

interface Expr
interface NamedExpr : Expr {
    val name: Id
}

interface TypedExpr<E : Any> : Expr {
    val type: KaneType<E>
}

interface AlgebraicExpr : Expr
interface NamedAlgebraicExpr : NamedExpr, AlgebraicExpr
interface NamedScalarExpr : NamedAlgebraicExpr, ScalarExpr
interface NamedMatrixExpr : NamedAlgebraicExpr, MatrixExpr

interface ScalarExpr : AlgebraicExpr {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): ScalarExpr = toNamed(property.name)
}

interface StatisticExpr : ScalarExpr

interface MatrixExpr : AlgebraicExpr {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): MatrixExpr = toNamed(property.name)
}

const val anonymous = "<anon>"



