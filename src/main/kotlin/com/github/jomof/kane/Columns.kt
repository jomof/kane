package com.github.jomof.kane

import com.github.jomof.kane.impl.sheet.GroupBy
import com.github.jomof.kane.impl.sheet.Sheet


/**
 * Get a subset of the columns from this [Sheet].
 */
fun Sheet.columns(vararg columns: String) = get(*columns)

/**
 * Get a subset of the columns from this [GroupBy].
 */
fun GroupBy.columns(vararg columns: String) = copy(sheet = sheet.columns(*columns))