package com.github.jomof.kane

import com.github.jomof.kane.impl.RowFilteringSequence
import com.github.jomof.kane.impl.sheet.RowView
import com.github.jomof.kane.impl.sheet.Sheet
import com.github.jomof.kane.impl.sheet.ordinalRows


fun Sequence<Row>.filter(predicate: (Row) -> Boolean) =
    RowFilteringSequence(this, true) { _, row -> predicate(row) }
