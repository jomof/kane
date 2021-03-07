package com.github.jomof.kane

import com.github.jomof.kane.api.Row

/**
 * Replaces all double values with [value].
 */
fun Sequence<Row>.fillna(value: Double) = mapDoubles {
    if (it.isNaN()) value
    else it
}
