package com.github.jomof.kane

import com.github.jomof.kane.sheet.groupOf
import com.github.jomof.kane.sheet.sheetOfCsv
import org.junit.Test

class GroupByTest {
    @Test
    fun basic() {
        val check = sheetOfCsv {
            """
                A,B
                1,2
                3,4
            """
        }.groupOf {
            val a by range("A")
            listOf(a)
        }
        println(check)

    }
}