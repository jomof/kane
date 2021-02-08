package com.github.jomof.kane

import com.github.jomof.kane.impl.sheet.GroupBy
import com.github.jomof.kane.impl.sheet.Sheet
import com.github.jomof.kane.impl.sheet.groupOf
import com.github.jomof.kane.impl.toNamed

/**
 * Group a [Sheet] by [columns]. The result is a [GroupBy] object that is effectively a map from key (the columns)
 * to a [Sheet] that has those column values.
 */
fun Sheet.groupBy(vararg columns: String): GroupBy = groupOf { columns.map { column(it).toNamed(it) } }