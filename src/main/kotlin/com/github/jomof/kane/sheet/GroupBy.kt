package com.github.jomof.kane.sheet

import com.github.jomof.kane.Expr

class GroupBy(
    private val sheet: Sheet,
    private val selector: List<Expr>
) : Expr

class GroupByBuilder

fun Sheet.groupOf(selector: SheetBuilder.() -> List<Expr>): GroupBy {
    return GroupBy(this, selector(toBuilder()))
}