package com.github.jomof.kane.impl

import com.github.jomof.kane.Expr
import com.github.jomof.kane.NamedExpr
import com.github.jomof.kane.ProvidesToSheet
import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.impl.sheet.CoerceScalar
import com.github.jomof.kane.impl.sheet.SheetRange
import com.github.jomof.kane.impl.sheet.analyzeDataType
import com.github.jomof.kane.impl.sheet.parseToExpr
import java.util.*

fun convertAnyToExpr(any: Any): Expr {
    return when (any) {
        is Double -> ConstantScalar(any)
        is Number -> ConstantScalar(any.toDouble())
        is String -> analyzeDataType(any).parseToExpr(any)
        is Expr -> any
        is ProvidesToSheet -> any.toSheet()
        else -> error("Unsupported type: ${any.javaClass}")
    }
}

fun convertAnyToScalarExpr(any: Any): ScalarExpr =
    when (val expr = convertAnyToExpr(any)) {
        is ScalarExpr -> expr
        is SheetRange -> CoerceScalar(expr)
        else ->
            error("${expr.javaClass}")
    }

fun convertAnyToNamedExpr(name: Id, any: Any): Expr {
    return convertAnyToExpr(any).toNamed(name)
}