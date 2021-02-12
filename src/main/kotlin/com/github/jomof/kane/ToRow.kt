package com.github.jomof.kane

import com.github.jomof.kane.impl.DataMatrix
import com.github.jomof.kane.impl.Tiling
import com.github.jomof.kane.impl.constant
import com.github.jomof.kane.impl.types.kaneType

/**
 * Convert a List<E> to a [Sheet] row.
 */
@JvmName("toRowNumber")
fun List<Number>.toRow(): MatrixExpr = DataMatrix(size, 1, map { constant(it.toDouble()) })

@JvmName("toRowScalar")
fun List<ScalarExpr>.toRow(): MatrixExpr = DataMatrix(size, 1, this)

@JvmName("toRowIntRange")
fun IntRange.toRow() = toList().toRow()

@JvmName("toRowScalarAny")
inline fun <reified E : Any> List<E>.toRow() = Tiling(size, 1, toList(), E::class.java.kaneType)