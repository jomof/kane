package com.github.jomof.kane

import com.github.jomof.kane.impl.sheet.Sheet
import com.github.jomof.kane.impl.sheet.ordinalRows

/**
 * Retrieve the first [count] elements of a sheet.
 */
fun Sheet.head(count: Int = 5) = take(count)