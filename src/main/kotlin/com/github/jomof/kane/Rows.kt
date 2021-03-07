package com.github.jomof.kane

import com.github.jomof.kane.api.Row

/**
 * Return the count of rows in this [Sequence<Row>]. Note that it may instantiate the underlying sequence.
 */
val Sequence<Row>.rows
    get() = when (this) {
        is CountableRows -> rows
        else -> count()
    }