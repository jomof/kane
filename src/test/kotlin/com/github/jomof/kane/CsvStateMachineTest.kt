package com.github.jomof.kane

import com.github.jomof.kane.impl.csv.CsvParseField
import com.github.jomof.kane.impl.csv.parseCsv
import org.junit.Test
import java.io.*

class CsvStateMachineTest {

    @Test
    fun basics() {
        parseCsv("").assertString("[]")
        parseCsv("a").assertString("[[a]]")
        parseCsv("a,b").assertString("[[a, b]]")
        parseCsv("a\nb").assertString("[[a], [b]]")
        parseCsv("a,b\nc,d").assertString("[[a, b], [c, d]]")
        parseCsv("\"a\",b\nc,d").assertString("[[a, b], [c, d]]")
        parseCsv("\"a,b\"\nc,d").assertString("[[a,b], [c, d]]")
    }

    @Test
    fun `simple quote`() {
        parseCsv("\"a\",b").assertString("[[a, b]]")
    }

    @Test
    fun `delimiter in quote`() {
        parseCsv("\"a,b\"").assertString("[[a,b]]")
    }

    @Test
    fun `quote then another empty line`() {
        parseCsv("\"a\"\n").assertString("[[a]]")
    }

    @Test
    fun `quote then another line`() {
        parseCsv("\"a\"\nb").assertString("[[a], [b]]")
    }

    @Test
    fun `just a comma`() {
        parseCsv(",").assertString("[[, ]]")
    }

    @Test
    fun `a comma, a line, and another comma`() {
        parseCsv(",\n,").assertString("[[, ], [, ]]")
    }

    @Test
    fun `cr-lf`() {
        parseCsv("a\r\nb").assertString("[[a], [b]]")
    }

    @Test
    fun `undefined, just don't crash`() {
        parseCsv("a\r\rb")
        parseCsv("a\r")
        parseCsv("a\"")
        parseCsv("a\\\r")
        parseCsv("a\\")
        parseCsv("\"a\\")
    }

    @Test
    fun `double quote`() {
        parseCsv("\"\"\"").assertString("[[\"]]")
    }

    @Test
    fun `json embedded`() {
        parseCsv("1,\"{\"\"type\"\": \"\"Point\"\", \"\"coordinates\"\": [102.0, 0.5]}\"").assertString(
            """
            [[1, {"type": "Point", "coordinates": [102.0, 0.5]}]]
        """.trimIndent()
        )
    }

    @Test
    fun `UTF-8`() {
        parseCsv(
            """
            a,b,c
            1,2,3
            4,5,ʤ
            """.trimIndent()
        ).assertString("[[a, b, c], [1, 2, 3], [4, 5, ʤ]]")
    }

    @Test
    fun `quoted cr-lf`() {
        parseCsv(
            """
            "a
             b"
            """.trimIndent()
        ).assertString(
            """
            [[a
             b]]
            """.trimIndent()
        )
    }


    @Test
    fun `space before quote`() {
        parseCsv(" \"a\"").assertString("[[a]]")
    }

    @Test
    fun `space before unquoted`() {
        parseCsv(" a").assertString("[[a]]")
    }

    @Test
    fun `space after unquoted`() {
        parseCsv("a ").assertString("[[a]]")
    }

    @Test
    fun `space in unquoted field`() {
        parseCsv("a b").assertString("[[a b]]")
    }

    @Test
    fun `comma in quotes`() {
        parseCsv("a b").assertString("[[a b]]")
    }

    @Test
    fun `space in quoted field`() {
        parseCsv("\"a b\"").assertString("[[a b]]")
    }

    @Test
    fun `escaped quote in unquoted field`() {
        parseCsv("a\\\"b").assertString("[[a\"b]]")
    }

    @Test
    fun `escaped escape char in unquoted field`() {
        parseCsv("a\\\\b").assertString("[[a\\b]]")
    }

    @Test
    fun `escaped quote in quoted field`() {
        parseCsv("\"a\\\"b\"").assertString("[[a\"b]]")
    }

    @Test
    fun `escaped escape char in quoted field`() {
        parseCsv("\"a\\\\b\"").assertString("[[a\\b]]")
    }

    @Test
    fun `read large file bytes`() {
        repeat(1) {
            val offsets = mutableListOf<Long>()
            var lastWasEol = true
            parseCsv(BufferedReader(FileReader("data/covid.csv"))) { field ->
                when (field) {
                    is CsvParseField.TextField -> if (lastWasEol) {
                        lastWasEol = false
                        offsets.add(field.startOffset)
                    }
                    is CsvParseField.EolineField -> lastWasEol = true
                }
            }
            println(offsets.size)
        }
    }
}