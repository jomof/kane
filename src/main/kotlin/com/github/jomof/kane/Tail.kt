package com.github.jomof.kane

import com.github.jomof.kane.impl.sheet.Sheet
import com.github.jomof.kane.impl.sheet.ordinalRows

/**
 * Retrieve the last [count] elements of a sheet.
 */
fun Sheet.tail(count : Int = 5) = ordinalRows(rows - count + 1 .. rows)