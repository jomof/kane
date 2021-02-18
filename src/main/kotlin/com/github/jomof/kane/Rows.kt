package com.github.jomof.kane

/**
 * Return the count of rows in this [Sequence<Row>]. Note that it may instantiate the underlying sequence.
 */
val Sequence<Row>.rows
    get() = when (this) {
        is CountableRows -> rows
        else -> count()
    }