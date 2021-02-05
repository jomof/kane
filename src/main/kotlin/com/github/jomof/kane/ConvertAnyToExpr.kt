package com.github.jomof.kane

import com.github.jomof.kane.sheet.CoerceScalar
import com.github.jomof.kane.sheet.analyzeDataType
import com.github.jomof.kane.sheet.parseToExpr

fun convertAnyToExpr(any: Any): Expr {
    return when (any) {
        is Double -> ConstantScalar(any)
        is Number -> ConstantScalar(any.toDouble())
        is String -> analyzeDataType(any).parseToExpr(any)
        is Expr -> any
        else -> error("Unsupported type: ${any.javaClass}")
    }
}

fun convertAnyToScalarExpr(any: Any): ScalarExpr =
    when (val expr = convertAnyToExpr(any)) {
        is ScalarExpr -> expr
        is UntypedScalar -> CoerceScalar(expr)
        else ->
            error("${expr.javaClass}")
    }

fun convertAnyToNamedExpr(name: Id, any: Any): NamedExpr {
    return convertAnyToExpr(any).toNamed(name)
}