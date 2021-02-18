package com.github.jomof.kane

import org.junit.Test

class SheetOfCsvTest {
    @Test
    fun basic() {
        val sheet = sheetOfCsv(
            """
            a,b
            1,2
            3,4
        """.trimIndent()
        )
        sheet.assertString(
            """
              a b 
              - - 
            1 1 2 
            2 3 4 
        """.trimIndent()
        )
    }
}