package com.github.jomof.kane

import com.github.jomof.kane.sheet.NamedSheetRangeExpr
import com.github.jomof.kane.sheet.SheetRangeExpr


fun ScalarVariable.toNamed(name: String) = NamedScalarVariable(name, initial, type)
fun MatrixVariable.toNamed(name: String) = NamedMatrixVariable(name, columns, type, initial)
fun ScalarAssign.toNamed(name: String) = NamedScalarAssign(name, left, right)
fun MatrixAssign.toNamed(name: String) = NamedMatrixAssign(name, left, right)
fun ScalarExpr.toNamed(name: String) = NamedScalar(name, this)
fun MatrixExpr.toNamed(name: String) = NamedMatrix(name, this)
fun UntypedScalar.toNamed(name: String) = NamedUntypedScalar(name, this)
fun SheetRangeExpr.toNamed(name: String) = NamedSheetRangeExpr(name, this)
fun <E : Any> ValueExpr<E>.toNamed(name: String) = toNamedValueExpr(name)
fun <E : Any> Tiling<E>.toNamed(name: String) = toNamedTilingExpr(name)

fun Expr.toNamed(name: String): NamedExpr {
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