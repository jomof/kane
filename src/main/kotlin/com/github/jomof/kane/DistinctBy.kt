package com.github.jomof.kane

import com.github.jomof.kane.api.Row
import com.github.jomof.kane.impl.RowDistinctSequence

/**
 * Returns a row sequence containing only elements from the given sequence
 * having distinct keys returned by the given [selector] function.
 *
 * Among elements of the given sequence with equal keys, only the first one will be present in the resulting sequence.
 * The elements in the resulting sequence are in the same order as they were in the source sequence.
 */
fun <K> Sequence<Row>.distinctBy(selector: (Row) -> K): Sequence<Row> {
    return RowDistinctSequence(this, selector)
}