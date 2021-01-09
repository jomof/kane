@file:Suppress("UNCHECKED_CAST")
package com.github.jomof.kane.rigueur

import com.github.jomof.kane.rigueur.Direction.BOTTOM_UP
import com.github.jomof.kane.rigueur.Direction.TOP_DOWN

private fun Expr.unsafeReplace(
    direction : Direction,
    f : ExprFunction) : Expr {
    val self = f.wrap { it.unsafeReplace(direction, f) }
    return when(direction) {
        TOP_DOWN -> f(this).dispatchExpr(self)
        BOTTOM_UP -> f(dispatchExpr(self))
    }
}

private fun Expr.dispatchExpr(self: ExprFunction): Expr {
    return when (this) {
        is NamedUntypedAbsoluteCellReference,
        is UntypedRelativeCellReference,
        is Slot<*>,
        is NamedMatrixVariable<*>,
        is MatrixVariableElement<*>,
        is ScalarVariable<*>,
        is ConstantScalar<*>,
        is ValueExpr<*>,
        is NamedValueExpr<*>,
        is AbsoluteCellReferenceExpr,
        is NamedScalarVariable<*> -> this
        is CoerceScalar<*> -> mapChildren(self)
        is ParentExpr<*> -> mapChildren(self)
        else -> error("$javaClass")
    }
}

enum class Direction { TOP_DOWN, BOTTOM_UP }

fun TypedExpr<*>.replaceTypedExprTopDown(f: (TypedExpr<*>) -> TypedExpr<*>) =
    unsafeReplace(TOP_DOWN, ExprFunction { f(it as TypedExpr<*>) }) as TypedExpr<*>
fun <E:Number> AlgebraicExpr<E>.replaceExprTopDown(f: (AlgebraicExpr<E>) -> AlgebraicExpr<E>) =
    unsafeReplace(TOP_DOWN, ExprFunction { f(it as AlgebraicExpr<E>) }) as AlgebraicExpr<E>
fun <E:Number> ScalarExpr<E>.replaceTopDown(f: (ScalarExpr<E>) -> ScalarExpr<E>) =
    unsafeReplace(TOP_DOWN, ExprFunction { f(it as ScalarExpr<E>) }) as ScalarExpr<E>

fun TypedExpr<*>.replaceTypedExprBottomUp(f: (TypedExpr<*>) -> TypedExpr<*>) =
    unsafeReplace(BOTTOM_UP, ExprFunction { f(it as TypedExpr<*>) }) as TypedExpr<*>
fun <E:Number> ScalarExpr<E>.replaceBottomUp(f: (ScalarExpr<E>) -> ScalarExpr<E>) =
    unsafeReplace(BOTTOM_UP, ExprFunction { f(it as ScalarExpr<E>) }) as ScalarExpr<E>

fun Expr.replaceExpr(direction : Direction = BOTTOM_UP, f: (Expr) -> Expr) =
    unsafeReplace(direction, ExprFunction { f(it) })
fun TypedExpr<*>.replaceTyped(direction : Direction = BOTTOM_UP, f: (TypedExpr<*>) -> TypedExpr<*>) =
    unsafeReplace(direction, ExprFunction { f(it as TypedExpr<*>) }) as TypedExpr<*>
fun <E:Number> ScalarExpr<E>.replace(direction : Direction = BOTTOM_UP, f: (ScalarExpr<E>) -> ScalarExpr<E>) =
    unsafeReplace(direction, ExprFunction { f(it as ScalarExpr<E>) }) as ScalarExpr<E>


fun <S:Any, E:Number> AlgebraicExpr<E>.foldTopDown(state: S, f: (state : S, expr : AlgebraicExpr<E>) -> S) : S {
    var prior = state
    unsafeReplace(TOP_DOWN, ExprFunction {
        prior = f(prior, it as AlgebraicExpr<E>)
        it // This defeats replacement so this whole operation becomes a visit instead
    })
    return prior
}

fun Expr.visit(f: (expr : Expr) -> Unit) {
    unsafeReplace(TOP_DOWN, ExprFunction {
        f(it)
        it // This defeats replacement so this whole operation becomes a visit instead
    })
}
