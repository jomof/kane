package com.github.jomof.kane

fun Any?.assertString(expected : String) {
    if (this == null) return "null".assertString(expected)
    val actual = toString().trim('\n', ' ')
    val expectedTrimmed = expected.trim('\n', ' ')
    if (actual != expectedTrimmed) {
        println(this)
        val actualSplit = actual.split('\n')
        val expectedSplit = expectedTrimmed.split('\n')
        (actualSplit zip expectedSplit).withIndex().forEach {
            val (a, e) = it.value
            if (a != e) {
                println("actual:   '$a'")
                println("expected: '$e'")
                error("unexpected on line ${it.index}")
            }
        }


        assert(actual == expectedTrimmed) {
            "actual:   [$actual]\nexpected: [$expectedTrimmed]"
        }
    }
}