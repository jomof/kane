package com.github.jomof.kane.impl

import java.lang.Integer.max

private const val PAD_CHAR = " "

fun padRight(value : String, width : Int) =
    value + PAD_CHAR.repeat(max(0, width - value.length))

fun padLeft(value : String, width : Int) =
    PAD_CHAR.repeat(max(0, width - value.length)) + value

fun padCenter(value : String, width : Int) : String {
    val spaces = max(0, width - value.length)
    val right = spaces / 2
    val left = spaces - right
    assert(right >= 0)
    assert(left >= 0)
    return PAD_CHAR.repeat(left) + value + PAD_CHAR.repeat(right)
}
