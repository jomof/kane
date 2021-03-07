package com.github.jomof.kane

import com.github.jomof.kane.api.Row
import com.github.jomof.kane.impl.RowDropTakeSequence
import com.github.jomof.kane.impl.RowTakeSequence


/**
 * Returns a sequence containing first [n] elements.
 */
fun Sequence<Row>.take(n: Int): Sequence<Row> {
    require(n >= 0) { "Requested element count $n is less than zero." }
    return when {
        n == 0 -> emptySequence()
        this is RowDropTakeSequence -> this.take(n)
        else -> RowTakeSequence(this, n)
    }
}