package com.github.jomof.kane

import kotlin.math.min
import kotlin.random.Random

/**
 * Retrieve [count] random elements from a row sequence.
 */
fun Sequence<Row>.sample(count: Int = 5, random: Random = Random(7)): Sequence<Row> {
    val countMin = min(count, rows)
    val samples = mutableSetOf<Int>()
    while (samples.size < countMin) {
        samples.add(random.nextInt(rows))
    }
    return filter { samples.contains(it.rowOrdinal + 1) }
}