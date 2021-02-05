package com.github.jomof.kane

import com.github.jomof.kane.functions.mean
import com.github.jomof.kane.functions.median
import com.github.jomof.kane.sheet.*
import org.junit.Test

class FunctionalTest {
    @Test
    fun ordinalRows() {
        val zoo = readCsv("data/zoo.csv").showExcelColumnTags(false)
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
        val zoo = readCsv("data/zoo.csv").showExcelColumnTags(false)
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
        val zoo = readCsv("data/zoo.csv").showExcelColumnTags(false)
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
        val zoo = readCsv("data/zoo.csv")
        println(zoo)
        val sample = zoo.sample()
        println(sample)
        sample.assertString(
            """
           animal [A] uniq_id [B] water_need [C] 
           ---------- ----------- -------------- 
         3   elephant        1003            550 
         5      tiger        1005            320 
         7      tiger        1007            290 
        12      zebra        1012            230 
        16       lion        1016            420 
        """.trimIndent()
        )
    }

    @Test
    fun getColumns() {
        val zoo = readCsv("data/zoo.csv").showExcelColumnTags(false)
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
        val zoo = readCsv("data/zoo.csv").showExcelColumnTags(false)
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
        val zoo = readCsv("data/zoo.csv")
        val filtered = zoo.filterRows { row ->
            row["uniq_id"] == "1005"
        }
        filtered.assertString(
            """
          animal [A] uniq_id [B] water_need [C] 
          ---------- ----------- -------------- 
        5      tiger        1005            320 
        """.trimIndent()
        )
    }

    @Test
    fun statistics() {
        val zoo = readCsv("data/zoo.csv")
        zoo.describe().assertString(
            """
                      uniq_id  water_need 
                     -------- ----------- 
               count       22          22 
                 NaN        0           0 
                mean   1011.5   347.72727 
                 min     1001          80 
                 25%     1006         230 
              median     1012         330 
                 75%     1017         430 
                 max     1022         600 
            variance 42.16667 21770.77922 
              stddev  6.49359   147.54924 
            skewness        0     0.05906 
            kurtosis -1.20497    -0.76712 
                  cv  0.00642     0.42432 
                 sum    22253        7650
        """.trimIndent()
        )
    }

    @Test
    fun `statistics of column`() {
        val zoo = readCsv("data/zoo.csv")
        zoo["water_need"].describe().assertString(
            """
                     water_need 
                     ----------- 
               count          22 
                 NaN           0 
                mean   347.72727 
                 min          80 
                 25%         230 
              median         330 
                 75%         430 
                 max         600 
            variance 21770.77922 
              stddev   147.54924 
            skewness     0.05906 
            kurtosis    -0.76712 
                  cv     0.42432 
                 sum        7650
        """.trimIndent()
        )
    }

    @Test
    fun `individual statistics of column`() {
        val zoo = readCsv("data/zoo.csv")
        mean(zoo["water_need"]).assertString("347.72727")
        median(zoo["water_need"]).assertString("330")
    }

    @Test
    fun `individual statistics of sheet`() {
        val zoo = readCsv("data/zoo.csv")
        mean(zoo).assertString(
            """
                 uniq_id water_need 
                 ------- ---------- 
            mean  1011.5  347.72727
        """.trimIndent()
        )
    }

    @Test
    fun `column types`() {
        val sheet = readCsv("./src/test/kotlin/com/github/jomof/kane/SP500toGDP.csv")
        sheet.types.assertString(
            """
                 name    type         format       
              --------- ------ ------------------- 
            1  DateTime   date yyyy-MM-dd HH:mm:ss 
            2 SP500/GDP double              double 
        """.trimIndent()
        )
    }
}