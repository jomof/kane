package com.github.jomof.kane

import com.github.jomof.kane.impl.sheet.GroupBy
import com.github.jomof.kane.impl.sheet.Sheet
import com.github.jomof.kane.impl.toSheet


/**
 * Get a subset of the columns from this [Sheet].
 */
fun Sequence<Row>.columns(vararg columns: String) = get(*columns)

/**
 * Get a subset of the columns from this [GroupBy].
 */
fun GroupBy.columns(vararg columns: String) = copy(source = source.columns(*columns).toSheet())