package com.github.jomof.kane

import com.github.jomof.kane.impl.RowFilteringSequence

fun Sequence<Row>.filterIndexed(predicate: (Int, Row) -> Boolean) =
    RowFilteringSequence(this, true, predicate)
