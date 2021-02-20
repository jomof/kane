package com.github.jomof.kane

import com.github.jomof.kane.impl.sheet.Sheet

/**
 * Convert a single celled [Sheet] value to an [Int]
 */
fun Sheet.toInt(): Int = toDouble().toInt()

/**
 * Convert a single celled [MatrixExpr] value to an [Int]
 */
fun MatrixExpr.toInt(): Int = toDouble().toInt()

/**
 * Convert a [ScalarExpr] to [Int]
 */
fun ScalarExpr.toInt(): Int = toDouble().toInt()

/**
 * Convert a row sequence to [Int]
 */
fun Sequence<Row>.toInt(): Int = toDouble().toInt()