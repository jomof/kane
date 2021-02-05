package com.github.jomof.kane

import com.github.jomof.kane.impl.sheet.RowView
import com.github.jomof.kane.impl.sheet.Sheet
import com.github.jomof.kane.impl.sheet.ordinalRows

/**
 * Filter rows of a Sheet with a predicate function.
 */
fun Sheet.filterRows(predicate: (RowView) -> Boolean): Sheet {
    val rows = (1..rows).filter {
        predicate(RowView(this, it - 1))
    }
    return ordinalRows(rows)
}