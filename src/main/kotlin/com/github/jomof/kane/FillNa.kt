package com.github.jomof.kane

import com.github.jomof.kane.impl.sheet.Sheet

/**
 * Replaces all double values with [value].
 */
fun Sequence<Row>.fillna(value: Double) = mapDoubles {
    if (it.isNaN()) value
    else it
}
