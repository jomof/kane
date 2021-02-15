package com.github.jomof.kane.impl

import com.github.jomof.kane.*
import com.github.jomof.kane.impl.functions.AlgebraicDeferredDataMatrix
import com.github.jomof.kane.impl.sheet.*


fun ScalarVariable.toNamed(name: Id) = NamedScalarVariable(name, initial)
fun MatrixVariable.toNamed(name: Id) = NamedMatrixVariable(name, columns, initial)
fun ScalarAssign.toNamed(name: Id) = NamedScalarAssign(name, left, right)
fun MatrixAssign.toNamed(name: Id) = NamedMatrixAssign(name, left, right)
fun ScalarExpr.toNamed(name: Id): ScalarExpr = when (this) {
    is AlgebraicBinaryScalarScalarScalar -> toNamed(name)
    is AlgebraicSummaryScalarScalar -> toNamed(name)
    is AlgebraicUnaryScalarScalar -> toNamed(name)
    is AlgebraicSummaryMatrixScalar -> toNamed(name)
    is ConstantScalar,
    is StreamingSampleStatisticExpr,
    is RetypeScalar,
    is MatrixVariableElement,
    is CoerceScalar,
    is Slot,
    is DiscreteUniformRandomVariable -> NamedScalar(name, this)
    else -> error("$javaClass")
}

fun MatrixExpr.toNamed(name: Id): MatrixExpr = when (this) {
    is AlgebraicBinaryScalarMatrixMatrix -> toNamed(name)
    is AlgebraicBinaryMatrixScalarMatrix -> toNamed(name)
    is AlgebraicBinaryMatrixMatrixMatrix -> toNamed(name)
    is AlgebraicUnaryMatrixMatrix -> toNamed(name)
    is AlgebraicDeferredDataMatrix,
    is CoerceMatrix,
    is RetypeMatrix,
    is NamedMatrixVariable,
    is DataMatrix -> NamedMatrix(name, this)
    else -> error("$javaClass")
}

fun SheetRangeExpr.toNamed(name: Id) = dup(name = name)
fun CellSheetRangeExpr.toNamed(name: Id) = dup(name = name)
fun AlgebraicUnaryScalarScalar.toNamed(name: Id) = dup(name = name)
fun AlgebraicSummaryScalarScalar.toNamed(name: Id) = dup(name = name)
fun AlgebraicBinaryScalarScalarScalar.toNamed(name: Id) = dup(name = name)
fun AlgebraicSummaryMatrixScalar.toNamed(name: Id) = dup(name = name)
fun AlgebraicUnaryMatrixMatrix.toNamed(name: Id) = dup(name = name)
fun AlgebraicBinaryScalarMatrixMatrix.toNamed(name: Id) = dup(name = name)
fun AlgebraicBinaryMatrixScalarMatrix.toNamed(name: Id) = dup(name = name)
fun AlgebraicBinaryMatrixMatrixMatrix.toNamed(name: Id) = dup(name = name)
fun AlgebraicBinarySummaryScalarScalarScalar.toNamed(name: Id) = dup(name = name)
fun AlgebraicBinarySummaryMatrixScalarScalar.toNamed(name: Id) = dup(name = name)
fun AlgebraicExpr.toNamed(name: Id) = (this as Expr).toNamed(name) as NamedAlgebraicExpr
fun <E : Any> ValueExpr<E>.toNamed(name: Id) = dup(name = name)
fun <E : Any> Tiling<E>.toNamed(name: Id) = toNamedTilingExpr(name)

fun Expr.toNamed(name: Id): Expr {
    return when (this) {
        is ScalarExpr -> toNamed(name)
        is MatrixExpr -> toNamed(name)
        is SheetRange -> toNamed(name)
        is ScalarAssign -> toNamed(name)
        is MatrixAssign -> toNamed(name)
        is ScalarVariable -> toNamed(name)
        is MatrixVariable -> toNamed(name)
        is ValueExpr<*> -> toNamed(name)
        is Tiling<*> -> toNamed(name)
        else -> TODO()
    }
}

fun Expr.toUnnamed(): Expr {
    return when (this) {
        is ConstantScalar,
        is RetypeScalar,
        is DiscreteUniformRandomVariable,
        is StreamingSampleStatisticExpr,
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
        is NamedScalarAssign -> ScalarAssign(left, right)
        is NamedMatrixAssign -> MatrixAssign(left, right)
        is NamedScalarVariable -> ScalarVariable(initial)
        is NamedMatrixVariable -> MatrixVariable(columns, rows, initial)
        is NamedSheet -> sheet
        is AlgebraicBinaryScalarScalarScalar -> dup(name = anonymous)
        is AlgebraicUnaryScalarScalar -> dup(name = anonymous)
        is AlgebraicSummaryScalarScalar -> dup(name = anonymous)
        is AlgebraicSummaryMatrixScalar -> dup(name = anonymous)
        is AlgebraicBinaryMatrixMatrixMatrix -> dup(name = anonymous)
        is AlgebraicUnaryMatrixMatrix -> dup(name = anonymous)
        is AlgebraicBinaryMatrixScalarMatrix -> dup(name = anonymous)
        is AlgebraicBinaryScalarMatrixMatrix -> dup(name = anonymous)
        is AlgebraicBinarySummaryScalarScalarScalar -> dup(name = anonymous)
        is AlgebraicBinarySummaryMatrixScalarScalar -> dup(name = anonymous)
        is CellSheetRangeExpr -> dup(name = anonymous)
        is SheetRangeExpr -> dup(name = anonymous)
        is ValueExpr<*> -> dup(name = anonymous)
        else -> error("$javaClass")
    }
}


val Expr.name: Id
    get() : Id {
        val result = when (this) {
            is NamedScalar -> name
            is NamedTiling<*> -> name
            is NamedMatrix -> name
            is NamedScalarVariable -> name
            is NamedMatrixAssign -> name
            is NamedMatrixVariable -> name
            is NamedScalarAssign -> name
            is AlgebraicBinaryScalarScalarScalar -> name
            is AlgebraicSummaryScalarScalar -> name
            is AlgebraicSummaryMatrixScalar -> name
            is AlgebraicUnaryScalarScalar -> name
            is AlgebraicUnaryMatrixMatrix -> name
            is AlgebraicBinaryScalarMatrixMatrix -> name
            is AlgebraicBinaryMatrixScalarMatrix -> name
            is AlgebraicBinaryMatrixMatrixMatrix -> name
            is AlgebraicBinarySummaryScalarScalarScalar -> name
            is AlgebraicBinarySummaryMatrixScalarScalar -> name
            is CellSheetRangeExpr -> name
            is SheetRangeExpr -> name
            is ValueExpr<*> -> name
            else ->
                error("$javaClass")
        }
        assert(result != anonymous) {
            "Should call hasName first"
        }
        return result
    }

fun Expr.hasName(): Boolean = when (this) {
    is NamedMatrixVariable -> true
    is NamedScalar -> true
    is NamedMatrixAssign -> true
    is NamedTiling<*> -> true
    is NamedMatrix -> true
    is NamedScalarAssign -> true
    is NamedScalarVariable -> true
    is ConstantScalar -> false
    is MatrixAssign -> false
    is RetypeMatrix -> false
    is Tableau -> false
    is DiscreteUniformRandomVariable -> false
    is CoerceScalar -> false
    is MatrixVariableElement -> false
    is DataMatrix -> false
    is RetypeScalar -> false
    is AlgebraicDeferredDataMatrix -> false
    is Sheet -> false
    is CoerceMatrix -> false
    is StreamingSampleStatisticExpr -> false
    is Tiling<*> -> false
    is ScalarVariable -> false
    is GroupBy -> false
    is Slot -> false
    is CellIndexedScalar -> false
    is AlgebraicBinaryScalarScalarScalar -> name != anonymous
    is AlgebraicUnaryScalarScalar -> name != anonymous
    is AlgebraicSummaryScalarScalar -> name != anonymous
    is AlgebraicSummaryMatrixScalar -> name != anonymous
    is AlgebraicBinarySummaryScalarScalarScalar -> name != anonymous
    is AlgebraicBinaryMatrixMatrixMatrix -> name != anonymous
    is AlgebraicUnaryMatrixMatrix -> name != anonymous
    is AlgebraicBinaryMatrixScalarMatrix -> name != anonymous
    is AlgebraicBinaryScalarMatrixMatrix -> name != anonymous
    is AlgebraicBinarySummaryMatrixScalarScalar -> name != anonymous
    is CellSheetRangeExpr -> name != anonymous
    is SheetRangeExpr -> name != anonymous
    is ValueExpr<*> -> name != anonymous
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
