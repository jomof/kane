package com.github.jomof.kane

import com.github.jomof.kane.sheet.*
import com.github.jomof.kane.sheet.tail
import org.junit.Test

class FunctionalTest {
    @Test
    fun ordinalRows() {
        val zoo = readCsv("zoo.csv")
        println(zoo)
        val picked = zoo.ordinalRows(2, 3, 4)
        println(picked)
        picked.assertString("""
               animal  uniq_id water_need 
              -------- ------- ---------- 
            2 elephant    1002        600 
            3 elephant    1003        550 
            4    tiger    1004        300 
        """.trimIndent())

        zoo.ordinalRows().assertString("")
        zoo.ordinalRows(17,16).assertString("""
               animal uniq_id water_need 
               ------ ------- ---------- 
            17   lion    1017        600 
            16   lion    1016        420 
        """.trimIndent())
    }

    @Test
    fun head() {
        val zoo = readCsv("zoo.csv")
        println(zoo)
        val head = zoo.head()
        println(head)
        head.assertString("""
               animal  uniq_id water_need 
              -------- ------- ---------- 
            1 elephant    1001        500 
            2 elephant    1002        600 
            3 elephant    1003        550 
            4    tiger    1004        300 
            5    tiger    1005        320 
        """.trimIndent())
    }

    @Test
    fun tail() {
        val zoo = readCsv("zoo.csv")
        println(zoo)
        val tail = zoo.tail()
        println(tail)
        tail.assertString("""
                animal  uniq_id water_need 
               -------- ------- ---------- 
            18     lion    1018        500 
            19     lion    1019        390 
            20 kangaroo    1020        410 
            21 kangaroo    1021        430 
            22 kangaroo    1022        410 
        """.trimIndent())
    }

    @Test
    fun sample() {
        val zoo = readCsv("zoo.csv")
        println(zoo)
        val sample = zoo.sample()
        println(sample)
        sample.assertString("""
                animal  uniq_id water_need 
               -------- ------- ---------- 
             3 elephant    1003        550 
             5    tiger    1005        320 
             7    tiger    1007        290 
            12    zebra    1012        230 
            16     lion    1016        420 
        """.trimIndent())
    }

    @Test
    fun getColumns() {
        val zoo = readCsv("zoo.csv")
        println(zoo)
        val cols = zoo["animal", "water_need"]
        println(cols)
        cols.assertString("""
                animal  water_need 
               -------- ---------- 
             1 elephant        500 
             2 elephant        600 
             3 elephant        550 
             4    tiger        300 
             5    tiger        320 
             6    tiger        330 
             7    tiger        290 
             8    tiger        310 
             9    zebra        200 
            10    zebra        220 
            ...and 12 more rows
        """.trimIndent())
    }

    @Test
    fun `get columns by Excel-like name`() {
        val zoo = readCsv("zoo.csv")
        val cols = zoo["B", "C"]
        println(cols)
        cols.assertString("""
               uniq_id water_need 
               ------- ---------- 
             1    1001        500 
             2    1002        600 
             3    1003        550 
             4    1004        300 
             5    1005        320 
             6    1006        330 
             7    1007        290 
             8    1008        310 
             9    1009        200 
            10    1010        220 
            ...and 12 more rows
        """.trimIndent())
    }

    @Test
    fun `filtering rows`() {
        val zoo = readCsv("zoo.csv")
        val filtered = zoo.filterRows { row ->
            row["uniq_id"] == "1005"
        }
        filtered.assertString("""
              animal uniq_id water_need 
              ------ ------- ---------- 
            5  tiger    1005        320 
        """.trimIndent())
    }

    @Test
    fun `column types`() {
        val sheet = readCsv("./src/test/kotlin/com/github/jomof/kane/SP500toGDP.csv")
        sheet.types.assertString("""
                 name    type         format       
              --------- ------ ------------------- 
            1  DateTime   date yyyy-MM-dd HH:mm:ss 
            2 SP500/GDP double              double 
        """.trimIndent())
    }
}