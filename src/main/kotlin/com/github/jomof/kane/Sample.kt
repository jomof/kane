package com.github.jomof.kane

import com.github.jomof.kane.impl.sheet.Sheet
import com.github.jomof.kane.impl.sheet.ordinalRows
import kotlin.math.min
import kotlin.random.Random

/**
 * Retrieve the last [count] elements of a sheet.
 */
fun Sheet.sample(count : Int = 5, random : Random = Random(7)) : Sheet {
    val countMin = min(count, rows)
    val samples = mutableSetOf<Int>()
    while (samples.size < countMin) {
        samples.add(random.nextInt(rows))
    }
    return ordinalRows(samples.toList().sorted())
}