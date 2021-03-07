package com.github.jomof.kane

import com.github.jomof.kane.api.Row

/**
 * Retrieve the last [count] elements of a sheet.
 */
fun Sequence<Row>.tail(count: Int = 5) = drop(rows - count)