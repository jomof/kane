package com.github.jomof.kane

fun Any?.assertString(expected : String) {
    if (this == null) return "null".assertString(expected)
    val actual = toString().trim('\r', '\n', ' ')
    val expectedTrimmed = expected.trim('\r', '\n', ' ')
    if (actual != expectedTrimmed) {
        println(this)
        val actualSplit = actual.split('\n').map { it.trim('\r', '\n', ' ') }
        val expectedSplit = expectedTrimmed.split('\n').map { it.trim('\r', '\n', ' ') }
        (actualSplit zip expectedSplit).withIndex().forEach {
            val (actualLine, expectedLine) = it.value
            if (actualLine != expectedLine) {
                val updownArrow = String(Character.toChars(0x2195))
                val firstDifference = Math.max(0, (actualLine zip expectedLine).indexOfFirst { (ac, ec) -> ac != ec })
                println("actual:   '$actualLine'")
                println(updownArrow.padStart(firstDifference + 12))
                println("expected: '$expectedLine'")
                error("unexpected on line ${it.index}")
            }
        }
    }
}