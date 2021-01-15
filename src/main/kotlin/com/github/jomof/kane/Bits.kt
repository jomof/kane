package com.github.jomof.kane

import com.github.jomof.kane.types.AlgebraicType
import com.github.jomof.kane.types.kaneType
import com.github.jomof.kane.types.one
import com.github.jomof.kane.types.zero

inline fun <reified E:Number> bitsToArray(
    value : Int,
    count : Int,
    type: AlgebraicType<E> = E::class.kaneType) = bitsToArray(value, count, type.zero, type.one)

inline fun <reified E:Number> bitsToArray(
    value : Int,
    count : Int,
    zero : E,
    one : E) = Array(count) { index ->
    assert(count in 1..32)
    val mask = 1 shl index
    val masked = value and mask
    if (masked == mask) one else zero
}