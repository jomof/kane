package com.github.jomof.kane

import com.github.jomof.kane.impl.NamedExpr
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
