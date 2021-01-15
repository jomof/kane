package com.github.jomof.kane

fun Any?.assertString(expected : String) {
    if (this == null) return "null".assertString(expected)
    val actual = toString().trim('\n')
    val expectedTrimmed = expected.trim('\n')
    assert(actual == expectedTrimmed) {
        "actual:   [$actual]\nexpected: [$expectedTrimmed]"
    }
}