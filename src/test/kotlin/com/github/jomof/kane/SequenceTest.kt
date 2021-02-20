package com.github.jomof.kane

import com.github.jomof.kane.impl.forceSheetInstantiation
import com.github.jomof.kane.impl.toSheet
import org.junit.Test
import kotlin.random.Random

class SequenceTest {
    @Test
    fun `dont check in forceSheetInstantiation`() {
        forceSheetInstantiation.assertString("false")
    }

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
    fun `filter twice coalesce`() {
        val sheet = sheetOfCsv(
            """
            a,b
            1,2
            3,4
            5,6
        """.trimMargin()
        )
        val filtered = sheet
            .filter { row -> row["a"] != "1" }
            .filter { row -> row["a"] != "5" }
            .toSheet()
        filtered.assertString(
            """
              a b 
              - - 
            2 3 4 
        """.trimIndent()
        )
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

    @Test
    fun shuffled() {
        val shuffled = sheetOfCsv(
            """
            a,b
            1,2
            3,4
            4,5
        """.trimMargin()
        ).shuffled(Random(7))
        shuffled.assertString(
            """
              a b 
              - - 
            3 4 5 
            1 1 2 
            2 3 4 
        """.trimIndent()
        )

    }

    @Test
    fun drop() {
        val sheet = sheetOfCsv(
            """
            a,b
            1,2
            3,4
        """.trimMargin()
        ).drop(1)
        sheet.assertString(
            """
              a b 
              - - 
            2 3 4 
        """.trimIndent()
        )
    }

    @Test
    fun take() {
        val sheet = sheetOfCsv(
            """
            a,b
            1,2
            3,4
        """.trimMargin()
        ).take(1)
        sheet.assertString(
            """
              a b 
              - - 
            1 1 2 
        """.trimIndent()
        )
    }

    @Test
    fun `drop take`() {
        val sheet = sheetOfCsv(
            """
            a,b
            1,2
            3,4
            5,6
        """.trimMargin()
        ).drop(1).take(1)
        sheet.assertString(
            """
              a b 
              - - 
            2 3 4 
        """.trimIndent()
        )
    }

    @Test
    fun `index by column name`() {
        val sheet = sheetOfCsv(
            """
            c1,c2,c3
            1,2,7
            3,4,8
            5,6,9
        """.trimMargin()
        )
        sheet["c1", "c3"].assertString(
            """
              c1 [A] c3 [B] 
              ------ ------ 
            1      1      7 
            2      3      8 
            3      5      9 
        """.trimIndent()
        )
    }

    @Test
    fun `index by cell`() {
        val sheet = sheetOfCsv(
            """
            c1,c2,c3
            1,2,7
            3,4,8
            5,6,9
        """.trimMargin()
        )
        sheet["A1"].assertString("1")
    }

    @Test
    fun `index into sheet`() {
        val sheet = sheetOfCsv(
            """
            c1,c2,c3
            1,2,7
            3,4,8
            5,6,9
        """.trimMargin()
        )
        sheet["A1"].assertString("1")
        sheet["B2"].assertString("4")
        sheet["N12"].assertString("null")
    }

    @Test
    fun `summary of column`() {
        val sheet = sheetOfCsv(
            """
            c1,c2,c3
            1,2,7
            3,4,8
            5,6,9
        """.trimMargin()
        )
        sheet["c1"].assertString(
            """
              c1 [A] 
              ------ 
            1      1 
            2      3 
            3      5 
        """.trimIndent()
        )
        sheet["c1"].toSheet().assertString(
            """
              c1 [A] 
              ------ 
            1      1 
            2      3 
            3      5 
        """.trimIndent()
        )
        sheet["c1"].describe().assertString(
            """
                    c1   
                 ------- 
           count       3 
             NaN       0 
            mean       3 
             min       1 
             25%       1 
          median       3 
             75%       5 
             max       5 
        variance       4 
           stdev       2 
        skewness       0 
        kurtosis    -1.5 
              cv 0.66667 
             sum       9 
        """.trimIndent()
        )
        val c1 = sheet["c1"].toSheet()
        c1.assertString(
            """
              c1 [A] 
              ------ 
            1      1 
            2      3 
            3      5 
        """.trimIndent()
        )
        c1.describe().toSheet().filter { "$it" == "mean" }.assertString("3")
        c1.describe().filter { "$it" == "mean" }.assertString("3")
        val described = c1.describe().filter { "$it" == "mean" }
        described["A1"].assertString("3")
        mean(c1).assertString("3")
    }

    @Test
    fun `repro case of wrong column count`() {
        val prior = Kane.metrics.sheetInstatiations
        val sheet = sheetOf {
            val a1 by listOf(1.0, 1.0)
            listOf(a1)
        }
        println(sheet)
        sheet.toSheet().assertString(
            """
              A 
              - 
            1 1 
            2 1 
        """.trimIndent()
        )
        sheet["A1"].assertString("1")
        sheet["A2"].assertString("1")
        (Kane.metrics.sheetInstatiations - prior).assertString("0")
    }

    @Test
    fun `repro case Excel-like column names`() {
        val prior = Kane.metrics.sheetInstatiations
        val sheet = sheetOfCsv(
            """
            c1,c2,c3
            1,2,7
            3,4,8
            5,6,9
        """.trimMargin()
        )
        sheet["A", "C"].assertString(
            """
              c1 [A] c3 [B] 
              ------ ------ 
            1      1      7 
            2      3      8 
            3      5      9  
        """.trimIndent()
        )
        (Kane.metrics.sheetInstatiations - prior).assertString("1")
    }

    @Test
    fun `repro case`() {
        val prior = Kane.metrics.sheetInstatiations
        val sheet = sheetOfCsv(
            """
            height
            45.306120
            """
        ).copy {
            val extra by column("height") * 1.0
            listOf(extra)
        }
        val filtered = sheet.filter { row -> true }
        filtered.assertString(
            """
              height [A] extra [B] 
              ---------- --------- 
            1   45.30612      A1*1 
            """.trimIndent()
        )
        (Kane.metrics.sheetInstatiations - prior).assertString("1")
    }

    @Test
    fun `repro formula adjustment in filtering`() {
        val sheet = {
            sheetOfCsv(
                """
                weight,gender
                157.500553,male
                174.094104,female
                177.540920,male
                """
            ).copy {
                val bmi by column("weight") + 1.0
                listOf(bmi)
            }
        }
        val expected = """
              weight [A] gender [B] bmi [C] 
              ---------- ---------- ------- 
            1  157.50055       male    A1+1 
            3  177.54092       male    A2+1 
        """.trimIndent()
        sheet().filter { row -> row["gender"] == "male" }.assertString(expected)
        sheet().toSheet().filter { row -> row["gender"] == "male" }.assertString(expected)
        sheet().filter { row -> row["gender"] == "male" }.toSheet().assertString(expected)
        val expected2 = """
              weight [A] gender [B] bmi [C] 
              ---------- ---------- ------- 
            3  177.54092       male    A1+1 
        """.trimIndent()
        sheet().filter { row -> row["gender"] == "male" }.filterIndexed { index, row -> index == 1 }
            .assertString(expected2)
    }


    @Test
    fun `check parition`() {
        val sheet = {
            sheetOfCsv(
                """
                weight,gender
                157.500553,male
                174.094104,female
                177.540920,male
                """
            ).copy {
                val bmi by column("weight") + 1.0
                listOf(bmi)
            }
        }
        val (male, female) = sheet().partition { row -> row["gender"] == "male" }
        male.assertString(
            """
              weight [A] gender [B] bmi [C] 
              ---------- ---------- ------- 
            1  157.50055       male    A1+1 
            3  177.54092       male    A2+1 
        """.trimIndent()
        )
        female.assertString(
            """
              weight [A] gender [B] bmi [C] 
              ---------- ---------- ------- 
            2   174.0941     female    A1+1 
        """.trimIndent()
        )
    }

    @Test
    fun `check distinct`() {
        val sheet = {
            sheetOfCsv(
                """
                weight,gender
                157.500553,male
                174.094104,female
                177.540920,male
                """
            ).copy {
                val bmi by column("weight") + 1.0
                listOf(bmi)
            }
        }
        val result = sheet()["gender"].distinct()
        result.assertString(
            """
              gender [A] 
              ---------- 
            1       male 
            2     female 
        """.trimIndent()
        )
    }

    @Test
    fun `repro lost row name`() {
        val sheet = {
            sheetOfCsv(
                """
                weight,gender
                157.500553,male
                174.094104,female
                177.540920,male
                """
            ).copy {
                val bmi by column("weight") + 1.0
                listOf(bmi)
            }
        }
        val expected2 = """
              weight [A] gender [B] bmi [C] 
              ---------- ---------- ------- 
            3  177.54092       male    A1+1 
        """.trimIndent()
        sheet().filter { row -> row["gender"] == "male" }.filterIndexed { index, row -> index == 1 }
            .assertString(expected2)
        sheet().toSheet().filter { row -> row["gender"] == "male" }.filterIndexed { index, row -> index == 1 }
            .assertString(expected2)
        sheet().filter { row -> row["gender"] == "male" }.toSheet().filterIndexed { index, row -> index == 1 }
            .assertString(expected2)
        sheet().filter { row -> row["gender"] == "male" }.filterIndexed { index, row -> index == 1 }.toSheet()
            .assertString(expected2)
    }

    @Test
    fun `support, filter row and ordinal toString`() {
        val sheet = {
            sheetOfCsv(
                """
                weight,gender
                157.500553,male
                174.094104,female
                177.540920,male
                """
            )
        }
        val expected = """
           weight [A] gender [B] 
          ---------- ---------- 
        1  157.50055       male 
        3  177.54092       male            
        """.trimIndent()
        sheet().filter { row -> row["gender"] == "male" }.assertString(expected)
        sheet().toSheet().filter { row -> row["gender"] == "male" }.assertString(expected)
        sheet().filter { row -> row["gender"] == "male" }.toSheet().assertString(expected)

    }

    @Test
    fun `repro formula adjustment in indexed filtering`() {
        val prior = Kane.metrics.sheetInstatiations
        val sheet = sheetOfCsv(
            """
                weight,gender
                157.500553,male
                174.094104,female
                177.540920,male
                """
        ).copy {
            val bmi by column("weight") + 1.0
            listOf(bmi)
        }
        val filtered = sheet.filterIndexed { index, _ ->
            index != 1
        }
        filtered.assertString(
            """
              weight [A] gender [B] bmi [C] 
              ---------- ---------- ------- 
            1  157.50055       male    A1+1 
            3  177.54092       male    A2+1 
            """.trimIndent()
        )
        (Kane.metrics.sheetInstatiations - prior).assertString("1")
    }

    @Test
    fun `repro formula adjustment in chained indexed filtering`() {
        val prior = Kane.metrics.sheetInstatiations
        val sheet = sheetOfCsv(
            """
                weight,gender
                157.500553,male
                174.094104,female
                177.540920,male
                """
        ).copy {
            val bmi by column("weight") + 1.0
            listOf(bmi)
        }
        val filtered = sheet
            .filterIndexed { index, _ -> index != 0 }
            .filterIndexed { index, _ -> index != 0 }
        filtered.assertString(
            """
              weight [A] gender [B] bmi [C] 
              ---------- ---------- ------- 
            3  177.54092       male    A1+1 
            """.trimIndent()
        )
        if (!forceSheetInstantiation) (Kane.metrics.sheetInstatiations - prior).assertString("1")
    }
}