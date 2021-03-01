package com.github.jomof.kane

import org.junit.Test

class AssertTest {
    @Test
    fun basic() {
        differenceBetweenLines("ab", "ac").assertString(
            """
            actual:   'ab' [#98]
                        ↕
            expected: 'ac' [#99]
        """.trimIndent()
        )
    }

    @Test
    fun empty1() {
        differenceBetweenLines("", "a").assertString(
            """
            actual:   ''
                       ↕
            expected: 'a' [#97]
        """.trimIndent()
        )
    }

    @Test
    fun empty2() {
        differenceBetweenLines("a", "").assertString(
            """
            actual:   'a' [#97]
                       ↕
            expected: ''
        """.trimIndent()
        )
    }
}