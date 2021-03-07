package com.github.jomof.kane

import com.github.jomof.kane.api.Row
import com.github.jomof.kane.impl.RowShufflingSequence
import kotlin.random.Random

/**
 * Shuffle this [Row] sequence
 */
fun Sequence<Row>.shuffled(): Sequence<Row> =
    shuffled(Random(7))

/**
 * Shuffle this [Row] sequence
 */
fun Sequence<Row>.shuffled(random: Random): Sequence<Row> =
    RowShufflingSequence(this, random)