package com.github.jomof.kane

import com.github.jomof.kane.api.Row
import com.github.jomof.kane.impl.ConstantScalar
import com.github.jomof.kane.impl.RetypeScalar
import com.github.jomof.kane.impl.ValueExpr
import com.github.jomof.kane.impl.sheet.Sheet
import com.github.jomof.kane.impl.sheet.toCells
import com.github.jomof.kane.impl.toSheet


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
                new[name] = expr.dup(value = result)
            }
            is ValueExpr<*> -> {
            }
            is RetypeScalar -> when (expr.scalar) {
                is ConstantScalar -> {
                    val result = translate(expr.scalar.value)
                    new[name] = expr.dup(scalar = expr.scalar.dup(value = result))
                }
                else -> error("${expr.scalar.javaClass}")
            }
            else ->
                error("${expr.javaClass}")
        }
    }
    return dup(cells = new.toCells())
}

/**
 * Map elements that are coercible to double.
 */
fun Expr.mapDoubles(translate: (Double) -> Double): Sequence<Row> {
    return when (this) {
        is Sheet -> mapDoubles(translate)
        else -> error("Unsupported $javaClass")
    }
}

/**
 * Map elements that are coercible to double.
 */
fun Sequence<Row>.mapDoubles(translate: (Double) -> Double): Sequence<Row> =
    (toSheet() as Expr).mapDoubles(translate)
