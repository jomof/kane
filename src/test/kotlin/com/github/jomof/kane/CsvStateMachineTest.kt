package com.github.jomof.kane

import com.github.jomof.kane.impl.csv.*
import com.github.jomof.kane.impl.toSheet
import org.junit.Test
import java.io.*

class CsvStateMachineTest {

    @Test
    fun basics() {
        parseCsvText("").assertString("[]")
        parseCsvText("a").assertString("[[a]]")
        parseCsvText("a,b").assertString("[[a, b]]")
        parseCsvText("a\nb").assertString("[[a], [b]]")
        parseCsvText("a,b\nc,d").assertString("[[a, b], [c, d]]")
        parseCsvText("\"a\",b\nc,d").assertString("[[a, b], [c, d]]")
        parseCsvText("\"a,b\"\nc,d").assertString("[[a,b], [c, d]]")
    }

    @Test
    fun `simple quote`() {
        parseCsvText("\"a\",b").assertString("[[a, b]]")
    }

    @Test
    fun `delimiter in quote`() {
        parseCsvText("\"a,b\"").assertString("[[a,b]]")
    }

    @Test
    fun `quote then another empty line`() {
        parseCsvText("\"a\"\n").assertString("[[a]]")
    }

    @Test
    fun `quote then another line`() {
        parseCsvText("\"a\"\nb").assertString("[[a], [b]]")
    }

    @Test
    fun `just a comma`() {
        parseCsvText(",").assertString("[[, ]]")
    }

    @Test
    fun `a comma, a line, and another comma`() {
        parseCsvText(",\n,").assertString("[[, ], [, ]]")
    }

    @Test
    fun `cr-lf`() {
        parseCsvText("a\r\nb").assertString("[[a], [b]]")
    }

    @Test
    fun `undefined, just don't crash`() {
        parseCsvText("a\r\rb")
        parseCsvText("a\r")
        parseCsvText("a\"")
        parseCsvText("a\\\r")
        parseCsvText("a\\")
        parseCsvText("\"a\\")
    }

    @Test
    fun `double quote`() {
        parseCsvText("\"\"\"").assertString("[[\"]]")
    }

    @Test
    fun `json embedded`() {
        parseCsvText("1,\"{\"\"type\"\": \"\"Point\"\", \"\"coordinates\"\": [102.0, 0.5]}\"").assertString(
            """
            [[1, {"type": "Point", "coordinates": [102.0, 0.5]}]]
        """.trimIndent()
        )
    }

    @Test
    fun `UTF-8`() {
        parseCsvText(
            """
            a,b,c
            1,2,3
            4,5,ʤ
            """.trimIndent()
        ).assertString("[[a, b, c], [1, 2, 3], [4, 5, ʤ]]")
    }

    @Test
    fun `quoted cr-lf`() {
        parseCsvText(
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
        parseCsvText(" \"a\"").assertString("[[a]]")
    }

    @Test
    fun `space before unquoted`() {
        parseCsvText(" a").assertString("[[a]]")
    }

    @Test
    fun `space after unquoted`() {
        parseCsvText("a ").assertString("[[a]]")
    }

    @Test
    fun `space in unquoted field`() {
        parseCsvText("a b").assertString("[[a b]]")
    }

    @Test
    fun `comma in quotes`() {
        parseCsvText("a b").assertString("[[a b]]")
    }

    @Test
    fun `space in quoted field`() {
        parseCsvText("\"a b\"").assertString("[[a b]]")
    }

    @Test
    fun `escaped quote in unquoted field`() {
        parseCsvText("a\\\"b").assertString("[[a\"b]]")
    }

    @Test
    fun `escaped escape char in unquoted field`() {
        parseCsvText("a\\\\b").assertString("[[a\\b]]")
    }

    @Test
    fun `escaped quote in quoted field`() {
        parseCsvText("\"a\\\"b\"").assertString("[[a\"b]]")
    }

    @Test
    fun `escaped escape char in quoted field`() {
        parseCsvText("\"a\\\\b\"").assertString("[[a\\b]]")
    }

    @Test
    fun `read csv cache x10`() {
        val csv = File("data/covid.csv")
        repeat(10) {
            val md = CsvMetadata.computeIfAbsent(csv)
            println(md)
        }
    }

    @Test
    fun `read csv as row sequence`() {
        val csv = File("data/covid.csv")
        println(readCsv(csv).sample(10).toSheet())
    }

    // All of these should be fast because they never instantiate
    // the full sheet.
    @Test
    fun `fast functional operations`() {
        val csv = File("data/covid.csv")
        readCsv(csv).sample(10).toList()
        readCsv(csv).head(10).toList()
        readCsv(csv).tail(10).toList()
        readCsv(csv).take(10).toList()
        readCsv(csv).drop(87369 - 10).toList()
    }

//    @Test
//    fun `distinctBy performance`() {
//        val csv = File("data/covid.csv")
//        val distinct = readCsv(csv)
//            .filter { row -> row["state"]?.toString()?.length ?: 0 > 4 }
//            .distinctBy { row -> row["state"] }.toSheet()
//        println(distinct)
//    }

    @Test
    fun `one-line repro`() {
        val csv = File("data/zoo-one-row.csv")
        val text = csv.readText()
        parseCsvText(text).assertString("[[animal, uniq_id, water_need], [elephant, 1001, 500]]")
        val metadata = computeCsvMetadata(csv)
        metadata.rows.assertString("1")
        metadata.columns.assertString("3")
    }
}