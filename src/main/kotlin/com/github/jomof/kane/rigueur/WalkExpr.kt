@file:Suppress("UNCHECKED_CAST")
package com.github.jomof.kane.rigueur

import com.github.jomof.kane.rigueur.Direction.BOTTOM_UP
import com.github.jomof.kane.rigueur.Direction.TOP_DOWN

private fun Any.unsafeReplace(direction : Direction, type : AlgebraicType<out Number>, f : ExprFunction<out Number>) : Any {
    val self = f.wrap { it.unsafeReplace(direction, type, f) }
    return when(direction) {
        TOP_DOWN -> f(this).dispatchExpr(self)
        BOTTOM_UP -> f(dispatchExpr(self))
    }
}

private fun Any.dispatchExpr(self: ExprFunction<out Any>): Any {
    return when (this) {
        is Slot<*>,
        is NamedMatrixVariable<*>,
        is MatrixVariableElement<*>,
        is ConstantScalar<*>,
        is AbsoluteCellReferenceExpr<*>,
        is NamedScalarVariable<*> -> this
        is CoerceScalar<*> -> when (value) {
            is TypedExpr -> mapChildren(self)
            else -> error("${value.javaClass}")
        }
        is ParentExpr<*> -> mapChildren(self)
        else -> error("$javaClass")
    }
}

enum class Direction { TOP_DOWN, BOTTOM_UP }

fun TypedExpr.replaceTypedExprTopDown(f: (TypedExpr) -> TypedExpr) =
    unsafeReplace(TOP_DOWN, type, ExprFunction(type) { f(it as TypedExpr) }) as TypedExpr
fun <E:Number> Expr<E>.replaceExprTopDown(f: (Expr<E>) -> Expr<E>) =
    unsafeReplace(TOP_DOWN, type, ExprFunction<E>(type) { f(it as Expr<E>) }) as Expr<E>
fun <E:Number> ScalarExpr<E>.replaceTopDown(f: (ScalarExpr<E>) -> ScalarExpr<E>) =
    unsafeReplace(TOP_DOWN, type, ExprFunction<E>(type) { f(it as ScalarExpr<E>) }) as ScalarExpr<E>

fun TypedExpr.replaceTypedExprBottomUp(f: (TypedExpr) -> TypedExpr) =
    unsafeReplace(BOTTOM_UP, type, ExprFunction(type) { f(it as TypedExpr) }) as TypedExpr
fun <E:Number> Expr<E>.replaceExprBottomUp(f: (Expr<E>) -> Expr<E>) =
    unsafeReplace(BOTTOM_UP, type, ExprFunction<E>(type) { f(it as Expr<E>) }) as Expr<E>
fun <E:Number> ScalarExpr<E>.replaceBottomUp(f: (ScalarExpr<E>) -> ScalarExpr<E>) =
    unsafeReplace(BOTTOM_UP, type, ExprFunction<E>(type) { f(it as ScalarExpr<E>) }) as ScalarExpr<E>

fun TypedExpr.replaceTyped(direction : Direction = BOTTOM_UP, f: (TypedExpr) -> TypedExpr) =
    unsafeReplace(direction, type, ExprFunction(type) { f(it as TypedExpr) }) as TypedExpr
fun <E:Number> Expr<E>.replaceExpr(direction : Direction = BOTTOM_UP, f: (Expr<E>) -> Expr<E>) =
    unsafeReplace(direction, type, ExprFunction<E>(type) { f(it as Expr<E>) }) as Expr<E>
fun <E:Number> ScalarExpr<E>.replace(direction : Direction = BOTTOM_UP, f: (ScalarExpr<E>) -> ScalarExpr<E>) =
    unsafeReplace(direction, type, ExprFunction<E>(type) { f(it as ScalarExpr<E>) }) as ScalarExpr<E>

fun <S:Any, E:Number> Expr<E>.foldTopDown(state: S, f: (state : S, expr : Expr<E>) -> S) : S {
    var prior = state
    unsafeReplace(TOP_DOWN, type, ExprFunction<E>(type) {
        prior = f(prior, it as Expr<E>)
        it // This defeats replacement so this whole operation becomes a visit instead
    })
    return prior
}
