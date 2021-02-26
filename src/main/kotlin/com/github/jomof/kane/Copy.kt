package com.github.jomof.kane

import com.github.jomof.kane.impl.sheet.Sheet
import com.github.jomof.kane.impl.sheet.SheetBuilderImpl
import com.github.jomof.kane.impl.sheet.toBuilder
import com.github.jomof.kane.impl.toSheet

/**
 * Create a new sheet with cell values changed.
 */
fun Sequence<Row>.copy(init: SheetBuilderImpl.() -> List<Expr>): Sheet {
    val sheet = toSheet().toBuilder()
    init(sheet).forEach { sheet.add(it) }
    return sheet.build()
}