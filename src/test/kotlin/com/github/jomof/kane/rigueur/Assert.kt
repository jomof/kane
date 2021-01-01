package com.github.jomof.kane.rigueur

fun Any.assertString(expected : String) {
    val actual = toString().trim('\n')
    val expectedTrimmed = expected.trim('\n')
    assert(actual == expectedTrimmed) {
        "actual:   [$actual]\nexpected: [$expectedTrimmed]"
    }
}