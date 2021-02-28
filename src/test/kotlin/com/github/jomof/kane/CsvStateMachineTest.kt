package com.github.jomof.kane

import com.github.jomof.kane.impl.csv.*
import com.github.jomof.kane.impl.toSheet
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
        parseCsv(text).assertString("[[animal, uniq_id, water_need], [elephant, 1001, 500]]")
        val metadata = computeCsvMetadata(csv)
        metadata.rows.assertString("1")
        metadata.columns.assertString("3")
    }
}