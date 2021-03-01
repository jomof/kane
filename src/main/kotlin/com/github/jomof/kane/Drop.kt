package com.github.jomof.kane

import com.github.jomof.kane.impl.RowDropSequence
import com.github.jomof.kane.impl.RowDropTakeSequence


/**
 * Returns a sequence containing all elements except first [n] elements.
 */
fun Sequence<Row>.drop(n: Int): Sequence<Row> {
    require(n >= 0) {
        "Requested element count $n is less than zero."
    }
    return when {
        n == 0 -> this
        this is RowDropTakeSequence -> this.drop(n)
        else -> RowDropSequence(this, n)
    }
}
