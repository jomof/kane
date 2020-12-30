@file:Suppress("UNCHECKED_CAST")
package com.github.jomof.kane.rigueur

private fun Any.unsafeReplace(type : AlgebraicType<*>?, f : ExprFunction<*>) : Any {
    with(f(this)) {
        val self = f.wrap { it.unsafeReplace(type, f) }
        return when (this) {
            is Slot<*>,
            is NamedMatrixVariable<*>,
            is MatrixVariableElement<*>,
            is ConstantScalar<*>,
            is AbsoluteCellReferenceExpr<*>,
            is NamedScalarVariable<*> -> this
            is CoerceScalar<*> -> when(value) {
                is TypedExpr -> mapChildren(self)
                else -> error("${value.javaClass}")
            }
            is ParentExpr<*> ->
                mapChildren(self)
            else -> error("$javaClass")
        }
    }
}

fun TypedExpr.replaceTypedExpr(f: (TypedExpr) -> TypedExpr) =
    unsafeReplace(type, ExprFunction<Any>(type) { f(it as TypedExpr) }) as TypedExpr
fun <E:Any> Expr<E>.replaceExpr(f: (Expr<E>) -> Expr<E>) =
    unsafeReplace(type, ExprFunction<E>(type) { f(it as Expr<E>) }) as Expr<E>
fun <E:Any> ScalarExpr<E>.replace(f: (ScalarExpr<E>) -> ScalarExpr<E>) =
    unsafeReplace(type, ExprFunction<E>(type) { f(it as ScalarExpr<E>) }) as ScalarExpr<E>

fun <S:Any, E:Any> Expr<E>.fold(state: S, f: (state : S, expr : Expr<E>) -> S) : S {
    var prior = state
    unsafeReplace(type, ExprFunction<E>(type) {
        prior = f(prior, it as Expr<E>)
        it // This defeats replacement so this whole operation becomes a visit instead
    })
    return prior
}