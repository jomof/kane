package com.github.jomof.kane

import com.github.jomof.kane.NamedExpr
import com.github.jomof.kane.impl.convertAnyToNamedExpr
import com.github.jomof.kane.impl.coordinate
import com.github.jomof.kane.impl.sheet.Sheet
import com.github.jomof.kane.impl.sheet.SheetBuilder
import com.github.jomof.kane.impl.sheet.SheetBuilderImpl

/**
 * Builder used to construct a Sheet object.
 */
fun sheetOf(init: SheetBuilder.() -> List<NamedExpr>): Sheet {
    val builder = SheetBuilderImpl()
    init(builder as SheetBuilder).forEach { builder.add(it) }
    return builder.build()
}

fun sheetOf(map: Map<*, *>): Sheet {
    val sb = SheetBuilderImpl()

    var index = 0
    for ((key, _) in map) {
        sb.column(index++, "$key")
    }

    var column = 0
    for ((_, values) in map) {
        when (values) {
            is List<*> -> {
                for ((row, value) in values.withIndex()) {
                    if (value != null) {
                        sb.add(convertAnyToNamedExpr(coordinate(column, row), value))
                    }
                }
            }
            else -> error("")
        }
        column++
    }
    return sb.build()
}