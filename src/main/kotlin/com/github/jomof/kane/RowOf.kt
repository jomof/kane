package com.github.jomof.kane

/**
 * Build a [Sheet] row from [elements].
 */
fun rowOf(vararg elements: Number) = elements.toList().toRow()

/**
 * Build a [Sheet] row from [elements].
 */
fun rowOf(vararg elements: ScalarExpr) = elements.toList().toRow()

/**
 * Build a [Sheet] row from [elements].
 */
inline fun <reified E : Any> rowOf(vararg elements: E) = elements.toList().toRow()