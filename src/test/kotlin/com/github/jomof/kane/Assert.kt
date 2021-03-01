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
                val firstDifference = Math.max(
                    Math.max(actualLine.length, expectedLine.length),
                    (actualLine zip expectedLine).indexOfFirst { (ac, ec) -> ac != ec })
                println(differenceBetweenLines(actual, expected))
                error("unexpected on line ${it.index}")
            }
        }
    }
}

internal fun differenceBetweenLines(l1: String, l2: String): String {
    val sb = StringBuilder()
    val updownArrow = String(Character.toChars(0x2195))
    val indexOfFirstDifference = (l1 zip l2).indexOfFirst { (ac, ec) -> ac != ec }
    val firstDifference = if (indexOfFirstDifference == -1)
        l1.length.coerceAtMost(l2.length)
    else indexOfFirstDifference
    val c1 = if (firstDifference < l1.length) "[#${l1[firstDifference].toInt()}]" else ""
    val c2 = if (firstDifference < l2.length) "[#${l2[firstDifference].toInt()}]" else ""
    sb.append("actual:   '$l1' $c1\n")
    sb.append(updownArrow.padStart(firstDifference + 12) + "\n")
    sb.append("expected: '$l2' $c2\n")
    return sb.toString()
}