package com.github.jomof.kane

import com.github.jomof.kane.api.Row
import com.github.jomof.kane.impl.sheet.Sheet


/**
 * Splits the original row sequence into pair of row sequences,
 * where *first* contains elements for which [predicate] yielded `true`,
 * while *second* contains elements for which [predicate] yielded `false`.
 *
 * The operation is terminal.
 */
inline fun Sequence<Row>.partition(crossinline predicate: (Row) -> Boolean): Pair<Sheet, Sheet> {
    val first = filter { predicate(it) }.toSheet()
    val second = filter { !predicate(it) }.toSheet()
    return Pair(first, second)
}