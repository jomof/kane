package com.github.jomof.kane

import com.github.jomof.kane.api.Row
import kotlin.random.Random

/**
 * Return a random sample from a row sequence
 *  [n] is number of samples to return. Cannot be used with [fraction].
 *  [fraction] is fraction of samples to return. Cannot be used with [n].
 * Default is n=5.
 * Using [fraction] may be more efficient in some cases because it can
 * operate sequentially without first counting the number of rows.
 */
fun Sequence<Row>.sample(
    n: Int? = null,
    fraction: Double? = null,
    random: Random = Random(7)
): Sequence<Row> {
    if (n != null && fraction != null) {
        error("Only one of 'n' and 'fraction' is allowed.")
    }
    when {
        fraction != null -> {
            return filter {
                fraction > 1.0 || random.nextDouble(0.0, 1.0) <= fraction
            }
        }
        else -> {
            val count = n ?: 5
            val countMin = min(count, rows)
            val samples = mutableSetOf<Int>()
            while (samples.size < countMin) {
                samples.add(random.nextInt(rows))
            }
            return filter {
                samples.contains(it.rowOrdinal)
            }
        }
    }
}