package com.github.jomof.kane.impl

import com.github.jomof.kane.*
import com.github.jomof.kane.impl.functions.AlgebraicDeferredDataMatrix
import com.github.jomof.kane.impl.sheet.*


fun ScalarExpr.toNamed(name: Id): ScalarExpr = when (this) {
    is INameableScalar -> toNamed(name)
    is NamedScalar,
    is MatrixVariableElement,
    is CoerceScalar,
    is Slot -> NamedScalar(name, this)
    else -> error("$javaClass")
}

fun MatrixExpr.toNamed(name: Id): MatrixExpr = when (this) {
    is INameableMatrix -> toNamed(name)
    is AlgebraicDeferredDataMatrix,
    is CoerceMatrix,
    is RetypeMatrix,
    is MatrixVariable,
    is DataMatrix -> NamedMatrix(name, this)
    else -> error("$javaClass")
}

fun AlgebraicExpr.toNamed(name: Id) = (this as Expr).toNamed(name) as NamedAlgebraicExpr
fun <E : Any> Tiling<E>.toNamed(name: Id) = toNamedTilingExpr(name)

fun Expr.toNamed(name: Id): Expr {
    return when (this) {
        is INameable -> toNamed(name)
        is ScalarExpr -> toNamed(name)
        is MatrixExpr -> toNamed(name)
        is SheetRange -> toNamed(name)
        is ScalarAssign -> toNamed(name)
        is MatrixAssign -> toNamed(name)
        is ScalarVariable -> toNamed(name)
        is MatrixVariable -> toNamed(name)
        is Tiling<*> -> toNamed(name)
        else -> error("$javaClass")
    }
}

fun Expr.toUnnamed(): Expr {
    return when (this) {
        is INameable -> toUnnamed()
        is MatrixVariableElement,
        is AlgebraicDeferredDataMatrix,
        is ScalarVariable,
        is MatrixAssign,
        is RetypeMatrix,
        is CoerceMatrix,
        is CoerceScalar,
        is GroupBy,
        is Tableau,
        is DataMatrix,
        is Sheet -> this
        is NamedScalar -> scalar
        is NamedMatrix -> matrix
        is NamedTiling<*> -> tiling
        is NamedSheet -> sheet
        else ->
            error("$javaClass")
    }
}


val Expr.name: Id
    get() : Id {
        val result = when (this) {
            is INameable -> name
            is NamedScalar -> name
            is NamedTiling<*> -> name
            is NamedMatrix -> name
            is ScalarReference -> name
            else ->
                error("$javaClass")
        }
        assert(result != anonymous) {
            "Should call hasName first"
        }
        return result
    }

fun Expr.hasName(): Boolean = when (this) {
    is INameable -> hasName()
    is NamedScalar -> true
    is NamedTiling<*> -> true
    is NamedMatrix -> true
    is MatrixAssign -> false
    is RetypeMatrix -> false
    is Tableau -> false
    is CoerceScalar -> false
    is MatrixVariableElement -> false
    is DataMatrix -> false
    is AlgebraicDeferredDataMatrix -> false
    is Sheet -> false
    is CoerceMatrix -> false
    is Tiling<*> -> false
    is ScalarVariable -> false
    is GroupBy -> false
    is Slot -> false
    is CellIndexedScalar -> false
    is ScalarReference -> true
    is MatrixReference -> true
    else -> error("$javaClass")
}

fun Expr.withNameOf(other: Expr, newName: () -> Id = { other.name }): Expr {
    if (!hasName() && !other.hasName()) return this
    if (!other.hasName()) return toUnnamed()
    val otherName = newName()
    if (!hasName()) return toNamed(otherName)
    val thisName = name
    if (thisName == otherName) return this
    val named = toNamed(otherName)
    return named
}

fun AlgebraicExpr.withNameOf(other: Expr, name: () -> Id = { other.name }) =
    (this as Expr).withNameOf(other, name) as AlgebraicExpr

fun MatrixExpr.withNameOf(other: Expr, name: () -> Id = { other.name }) =
    (this as Expr).withNameOf(other, name) as MatrixExpr
