package com.github.jomof.kane

import com.github.jomof.kane.impl.toSheet
import org.junit.Test

class SequenceTest {
    @Test
    fun `filter true`() {
        val sheet = sheetOfCsv(
            """
            a,b
            1,2
            3,4
        """.trimMargin()
        )
        val filtered = sheet
            .filter { row -> true }
            .toSheet()
        filtered.assertString(
            """
              a b 
              - - 
            1 1 2 
            2 3 4 
        """.trimIndent()
        )
    }

    @Test
    fun `filter false`() {
        val sheet = sheetOfCsv(
            """
            a,b
            1,2
            3,4
        """.trimMargin()
        )
        val filtered = sheet
            .filter { row -> false }
            .toSheet()
        filtered.assertString("")
    }

    @Test
    fun `filter but keep row names`() {
        val csv = sheetOfCsv(
            """
            a,b
            1,2
            3,4
            4,5
        """.trimMargin()
        )
        csv.assertString(
            """
              a b 
              - - 
            1 1 2 
            2 3 4 
            3 4 5 
        """.trimIndent()
        )
        csv.toSheet().assertString(
            """
              a b 
              - - 
            1 1 2 
            2 3 4 
            3 4 5 
        """.trimIndent()
        )
        csv.toList().toSheet().assertString(
            """
              a b 
              - - 
            1 1 2 
            2 3 4 
            3 4 5 
        """.trimIndent()
        )
        csv.toList().filter { row -> row["a"] != "3" }.toSheet().assertString(
            """
              a b 
              - - 
            1 1 2 
            3 4 5 
        """.trimIndent()
        )
        val filtered = csv
            .filter { row -> row["a"] != "3" }
            .toSheet()
        filtered.assertString(
            """
              a b 
              - - 
            1 1 2 
            3 4 5 
        """.trimIndent()
        )
    }
}