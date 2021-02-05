package com.github.jomof.kane

import com.github.jomof.kane.impl.padCenter
import com.github.jomof.kane.impl.padLeft
import com.github.jomof.kane.impl.padRight
import org.junit.Test

class StringTest {
    @Test
    fun padding() {
        ("["+ padRight("x", 3) + "]").assertString("[x  ]")
        ("["+ padLeft("x", 3) + "]").assertString("[  x]")
        ("["+ padCenter("x", 3) + "]").assertString("[ x ]")
        ("["+ padCenter("xx", 3) + "]").assertString("[ xx]")
        ("["+ padRight("xx", 3) + "]").assertString("[xx ]")
        ("["+ padLeft("xx", 3) + "]").assertString("[ xx]")
        ("["+ padCenter("xxx", 3) + "]").assertString("[xxx]")
        ("["+ padRight("xxx", 3) + "]").assertString("[xxx]")
        ("["+ padLeft("xxx", 3) + "]").assertString("[xxx]")
        ("["+ padCenter("xxxx", 3) + "]").assertString("[xxxx]")
        ("["+ padRight("xxxx", 3) + "]").assertString("[xxxx]")
        ("["+ padLeft("xxxx", 3) + "]").assertString("[xxxx]")
    }
}