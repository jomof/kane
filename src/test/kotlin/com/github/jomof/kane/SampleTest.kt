package com.github.jomof.kane

import com.github.jomof.kane.impl.csv.CsvParseContext
import com.github.jomof.kane.impl.csv.readCsvRowSequence
import org.junit.Test
import java.io.File

class SampleTest {
    @Test
    fun default() {
        val zoo = readCsvRowSequence(File("data/zoo.csv"))
        println(zoo)
        zoo.sample().assertString(
            """
                   animal [A] uniq_id [B] water_need [C] 
               ---------- ----------- -------------- 
             1   elephant        1001            500 
             6      tiger        1006            330 
             9      zebra        1009            200 
            13      zebra        1013            220 
            20   kangaroo        1020            410 
        """.trimIndent()
        )
    }

    @Test
    fun `default no-header`() {
        val zoo = readCsvRowSequence(
            File("data/zoo-no-header.csv"),
            CsvParseContext(columnNames = listOf("animal", "uniq_id", "water_need"))
        )
        zoo.sample().assertString(
            """
                   animal [A] uniq_id [B] water_need [C] 
               ---------- ----------- -------------- 
             1   elephant        1001            500 
             6      tiger        1006            330 
             9      zebra        1009            200 
            13      zebra        1013            220 
            20   kangaroo        1020            410 
        """.trimIndent()
        )
    }

    @Test
    fun zero() {
        val zoo = readCsvRowSequence(File("data/zoo.csv"))
        zoo.sample(n = 0).assertString("")
    }

    @Test
    fun one() {
        val zoo = readCsvRowSequence(File("data/zoo.csv"))
        zoo.sample(n = 1).assertString(
            """
              animal [A] uniq_id [B] water_need [C] 
              ---------- ----------- -------------- 
            6      tiger        1006            330 
        """.trimIndent()
        )
    }


    @Test
    fun `more than row count`() {
        val zoo = readCsvRowSequence(File("data/zoo.csv"))
        zoo.sample(n = 1000).rows.assertString(zoo.rows.toString())
    }

    @Test
    fun `sample 20%`() {
        val zoo = readCsvRowSequence(File("data/zoo.csv"))
        zoo.sample(fraction = 0.2).assertString(
            """
               animal [A] uniq_id [B] water_need [C] 
               ---------- ----------- -------------- 
             5      tiger        1005            320 
            11      zebra        1011            240 
            19       lion        1019            390 
        """.trimIndent()
        )
    }

    @Test
    fun `sample 0%`() {
        val zoo = readCsvRowSequence(File("data/zoo.csv"))
        zoo.sample(fraction = 0.0).assertString("")
    }
}