package com.github.jomof.kane

import com.github.jomof.kane.impl.sheet.Sheet

/**
 * Replaces all double values with [value].
 */
fun Sheet.fillna(value: Double) = mapDoubles {
    if (it.isNaN()) value
    else it
}

/**
 * Replaces all double values with [value].
 */
fun Expr.fillna(value: Double): Expr {
    return when (this) {
        is Sheet -> fillna(value)
        else -> error("$javaClass")
    }
}