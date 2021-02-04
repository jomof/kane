package com.github.jomof.kane

import com.github.jomof.kane.sheet.NamedSheetRangeExpr
import com.github.jomof.kane.sheet.SheetRangeExpr


fun ScalarVariable.toNamed(name: Id) = NamedScalarVariable(name, initial, type)
fun MatrixVariable.toNamed(name: Id) = NamedMatrixVariable(name, columns, type, initial)
fun ScalarAssign.toNamed(name: Id) = NamedScalarAssign(name, left, right)
fun MatrixAssign.toNamed(name: Id) = NamedMatrixAssign(name, left, right)
fun ScalarExpr.toNamed(name: Id) = NamedScalar(name, this)
fun MatrixExpr.toNamed(name: Id) = NamedMatrix(name, this)
fun UntypedScalar.toNamed(name: Id) = NamedUntypedScalar(name, this)
fun SheetRangeExpr.toNamed(name: Id) = NamedSheetRangeExpr(name, this)
fun AlgebraicExpr.toNamed(name: Id) = (this as Expr).toNamed(name) as NamedAlgebraicExpr
fun <E : Any> ValueExpr<E>.toNamed(name: Id) = toNamedValueExpr(name)
fun <E : Any> Tiling<E>.toNamed(name: Id) = toNamedTilingExpr(name)

fun Expr.toNamed(name: Id): NamedExpr {
    return when (this) {
        is ScalarExpr -> toNamed(name)
        is MatrixExpr -> toNamed(name)
        is UntypedScalar -> toNamed(name)
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
        is NamedScalar -> scalar
        is NamedMatrix -> matrix
        is NamedUntypedScalar -> expr
        is NamedSheetRangeExpr -> range
        is NamedTiling<*> -> tiling
        is NamedScalarAssign -> ScalarAssign(left, right)
        is NamedMatrixAssign -> MatrixAssign(left, right)
        is NamedValueExpr<*> -> toUnnamedValueExpr()
        is NamedScalarVariable -> ScalarVariable(initial, type)
        is NamedMatrixVariable -> MatrixVariable(columns, rows, type, initial)
        else -> TODO()
    }
}