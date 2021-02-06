package com.github.jomof.kane

import com.github.jomof.kane.impl.ConstantScalar
import com.github.jomof.kane.impl.ValueExpr
import com.github.jomof.kane.impl.sheet.Sheet
import com.github.jomof.kane.impl.sheet.toCells


/**
 * Map cells of a sheet that are coercible to double.
 */
fun Sheet.mapDoubles(translate: (Double) -> Double): Sheet {
    val evaled = eval()
    val new = cells.toMutableMap()
    evaled.cells.forEach { (name, expr) ->
        when (expr) {
            is ConstantScalar -> {
                val result = translate(expr.value)
                new[name] = expr.copy(value = result)
            }
            is ValueExpr<*> -> {
            }
            else -> error("${expr.javaClass}")
        }
    }
    return copy(cells = new.toCells())
}

/**
 * Map elements that are coercible to double.
 */
fun Expr.mapDoubles(translate: (Double) -> Double): Expr {
    return when (this) {
        is Sheet -> mapDoubles(translate)
        else -> error("Unsupported ${javaClass}")
    }
}