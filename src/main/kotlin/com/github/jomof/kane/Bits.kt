package com.github.jomof.kane

fun bitsToArray(
    value : Int,
    count : Int) = bitsToArray(value, count, 0.0, 1.0)

fun bitsToArray(
    value : Int,
    count : Int,
    zero : Double,
    one : Double) = DoubleArray(count) { index ->
    assert(count in 1..32)
    val mask = 1 shl index
    val masked = value and mask
    if (masked == mask) one else zero
}