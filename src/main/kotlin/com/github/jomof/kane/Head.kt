package com.github.jomof.kane

import com.github.jomof.kane.api.Row

/**
 * Retrieve the first [count] elements of a sheet.
 */
fun Sequence<Row>.head(count: Int = 5) = take(count)