package com.github.jomof.kane.rigueur

inline fun <reified E:Number> bitsToArray(
    value : Int,
    count : Int,
    type: AlgebraicType<E> = E::class.algebraicType) = bitsToArray(value, count, type.zero, type.one)

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