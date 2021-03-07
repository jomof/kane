package com.github.jomof.kane

import com.github.jomof.kane.api.Row


/**
 * Returns a row sequence containing only distinct elements from the given sequence.
 *
 * Among equal elements of the given sequence, only the first one will be present in the resulting sequence.
 * The elements in the resulting sequence are in the same order as they were in the source sequence.
 *
 * The operation is _intermediate_ and _stateful_.
 */
fun Sequence<Row>.distinct(): Sequence<Row> {
    return this.distinctBy { it }
}