package com.github.jomof.kane

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.jomof.kane.functions.*
import com.github.jomof.kane.sheet.*
import com.github.jomof.kane.types.dollars
import com.github.jomof.kane.types.percent
import org.junit.Test
import java.io.File
import java.util.*
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.round

class SheetTest {

    @Test
    fun `test column tags`() {
        indexToColumnName(0).assertString("A")
        indexToColumnName(25).assertString("Z")
        indexToColumnName(26).assertString("AA")
        indexToColumnName(27).assertString("AB")
        indexToColumnName(51).assertString("AZ")
        indexToColumnName(52).assertString("BA")
        indexToColumnName(701).assertString("ZZ")
        indexToColumnName(702).assertString("AAA")

        cellNameToColumnIndex("A").assertString("0")
        cellNameToColumnIndex("Z").assertString("25")
        cellNameToColumnIndex("AA").assertString("26")
        cellNameToColumnIndex("AZ").assertString("51")
        cellNameToColumnIndex("BA").assertString("52")
        cellNameToColumnIndex("ZZ").assertString("701")
        cellNameToColumnIndex("AAA").assertString("702")

        cellNameToCoordinate("A1").assertString("[0,0]")
        cellNameToCoordinate("Z1").assertString("[25,0]")
        cellNameToCoordinate("A25").assertString("[0,24]")

        coordinateToCellName(0, 0).assertString("A1")
    }

    @Test
    fun `don't expand constants initially`() {
        val sheet = sheetOf {
            val a1 by constant(1)
            val a2 by a1 + 1
            listOf(a1, a2)
        }
        println(sheet)
        sheet["A1"].assertString("1")
        sheet["A2"].assertString("A1+1")
        val evaluated = sheet.eval()
        println(evaluated)
        evaluated["A1"].assertString("1")
        evaluated["A2"].assertString("2")
    }

    @Test
    fun `basic sheet`() {
        val sheet = sheetOf {
            val a1 by constant(1)
            val b1 by left + 8
            val a2 by a1 + 1
            val a3 by up + dollars(1.1)
            val a4 by down + 3.0
            val a5 by right + 5.0
            val b5 by constant(7.0)
            listOf(a1, a2, a3, b1, a4, a5, b5)
        }
        println(sheet)
        sheet["A1"].assertString("1")
        sheet["A2"].assertString("A1+1")
        sheet["A3"].assertString("A2+$1.10")
        sheet["A4"].assertString("A5+3")
        sheet["A5"].assertString("B5+5")
        sheet["B1"].assertString("A1+8")
        sheet["B5"].assertString("7")
        val evaluated = sheet.eval()
        println(evaluated)
        evaluated["A1"].assertString("1")
        evaluated["A2"].assertString("2")
        evaluated["A3"].assertString("\$3.10")
        evaluated["A4"].assertString("15")
        evaluated["A5"].assertString("12")
        evaluated["B1"].assertString("9")
        evaluated["B5"].assertString("7")
    }

    @Test
    fun `cell naming`() {
        coordinateToCellName(0,0).assertString("A1")
    }

    @Test
    fun `weird case where % was not converted to dollar`() {
        val stock = percent(0.7)
        val start = dollars(100)
        val plan = sheetOf {
            val a1 by sp500(constant(1988))
            val b1 by (start * a1 * stock + start * a1 * (1.0 - stock))
            listOf(b1)
        }.eval()
        println(plan)
        plan["B1"].assertString("$16")
    }

    @Test
    fun `add formula to csv`() {
        val sheet = sheetOfCsv {
            """
            A,B
            1.0,-0.4
            2.0,-1.0
            """
        }.copy {
            val c1 by range("A") + range("B")
            listOf(c1)
        }
        println(sheet)
    }

    @Test
    fun `reference range up past end of sheet`() {
        val sheet = sheetOf {
            val columnC by range("C")
            val columnD by range("D")
            val a1 by 0.0
            val b2 by columnC.up * columnD
            listOf(a1, b2)
        }
        sheet.assertString(
            """
              A   B   columnC 
              - ----- ------- 
            1 0               
            2   C1*D2            
        """.trimIndent()
        )
    }

    @Test
    fun `range expanssion doesn't reduce arithmetic`() {
        val sheet = sheetOfCsv {
            """
            A
            1.0
            2.0
            """
        }.copy {
            val x by range("A")
            val m by 0.0
            val b by 0.0
            val c1 by m * x + b
            listOf(c1)
        }
        sheet.assertString(
            """
              x B    C   
              - - ------ 
            1 1   m*A1+b 
            2 2   m*A2+b 
            b=0
            m=0
        """.trimIndent()
        )
    }

    @Test
    fun `range expanssion doesn't reduce arithmetic with anonymous range`() {
        val sheet = sheetOfCsv {
            """
            A
            1.0
            2.0
            """
        }.copy {
            val m by 0.0
            val b by 0.0
            val c1 by m * range("A") + b
            listOf(c1)
        }
        sheet.assertString(
            """
              A B    C   
              - - ------ 
            1 1   m*A1+b 
            2 2   m*A2+b 
            b=0
            m=0
        """.trimIndent()
        )
    }

    @Test
    fun `names of ranged expressions get expanded to columns`() {
        val sheet = sheetOfCsv {
            """
            A
            1.0
            2.0
            """
        }.copy {
            val x by range("A")
            val m by 0.0
            val b by 0.0
            val prediction by m * x + b
            listOf(prediction)
        }
        sheet.assertString(
            """
              x prediction 
              - ---------- 
            1 1     m*A1+b 
            2 2     m*A2+b 
            b=0
            m=0
        """.trimIndent()
        )
    }

    @Test
    fun `names of ranged expressions get expanded to columns in order`() {
        val sheet = sheetOfCsv {
            """
            A
            1.0
            2.0
            """
        }.copy {
            val x by range("A")
            val m by 0.0
            val b by 0.0
            val prediction by m * x + b
            val error by prediction - 5.0
            listOf(error)
        }
        sheet.assertString(
            """
              x prediction   error  
              - ---------- -------- 
            1 1     m*A1+b m*A1+b-5 
            2 2     m*A2+b m*A2+b-5 
            b=0
            m=0
        """.trimIndent()
        )
    }

    @Test
    fun `statistic summary function over range`() {
        val sheet = sheetOfCsv {
            """
            A
            1.0
            2.0
            """
        }.copy {
            val x by range("A")
            val total by sum(x)
            listOf(total)
        }
        sheet.assertString(
            """
              x 
              - 
            1 1 
            2 2 
            total=sum(A)
        """.trimIndent()
        )
    }

    @Test
    fun `basic define column`() {
        val sheet = sheetOf {
            val increasing by range("B")
            val a2 by columnOf(5) { 1.1 + increasing.up }
            val b1 by columnOf(5) { constant(1.0 + it) }
            listOf(a2, b1)
        }.eval()
        println(sheet)
        sheet.assertString("""
               A  increasing 
              --- ---------- 
            1              1 
            2 2.1          2 
            3 3.1          3 
            4 4.1          4 
            5 5.1          5 
            6 6.1            
        """.trimIndent())
    }

    @Test
    fun `recursive sheet`() {
        val sheet = sheetOf {
            val a1 by down
            val a2 by up + 1
            listOf(a1, a2)
        }
        println(sheet)
        val evaluated = sheet.eval()
        println(evaluated)
        evaluated["A1"].assertString("A2")
        evaluated["A2"].assertString("A1+1")
    }

    @Test
    fun `columnOf sheet`() {
        val sheet = sheetOf {
            val a1 by columnOf(2020, 2021, 2022, 2023)
            val b2 by columnOf(2020, 2021, 2022, 2023)
            val c3 by columnOf(up + 1, up + 2, up + 3, up + 4)
            listOf(a1, b2, c3)
        }
        println(sheet)
    }

    @Test
    fun `relative reference`() {
        val sheet = sheetOf {
            val a1 by dollars(0.15)
            val a2 by up + dollars(0.35)
            listOf(a1, a2)
        }
        val evaluated = sheet.eval()
        println(evaluated)
        evaluated["A1"].assertString("$0.15")
        evaluated["A2"].assertString("$0.50")
    }

    @Test
    fun `percent of dollars`() {
        val sheet = sheetOf {
            val a1 by percent(0.15)
            val a2 by dollars(1_234.00)
            val a3 by a1 * a2
            val a4 by up(3) * a2
            val a5 by a1 * up(3)
            listOf(a3, a4, a5)
        }
        val evaluated = sheet.eval()
        println(evaluated)
        evaluated["A1"].assertString("15%")
        evaluated["A2"].assertString("\$1,234.00")
        evaluated["A3"].assertString("\$185.10")
        evaluated["A4"].assertString("\$185.10")
        evaluated["A5"].assertString("\$185.10")
    }

    @Test
    fun `shiller PE to following year return`() {
        val startYear = 1928
        val endYear = 1938 // 2019
        // expected(#years, %stock) [then 5% and 95% confidence]
        val sheet = sheetOf {
            val a1 by constant("m")
            val b1 by constant(1.0)
            val a2 by constant("b")
            val b2 by constant(0.0)
            val a3 by constant("YEAR")
            val a4 by columnOf(startYear.toDouble() to endYear.toDouble())
            val b3 by constant("Shiller PE")
            val b4 by shillerPE(a4)
            val c3 by constant("S&P 500")
            val c4 by sp500(a4)
            val d4 by b4 * b1 + b2
            val c1 by constant("error")
            val d1 by sum(pow(d4 - c4, 2.0))

            listOf(
                a1, a2, b1, b2, a3, a4, b4, b3, c3, c4, d1, c1, d4
            )
        }
        println(sheet.eval())
        val min = sheet.minimize("D1", listOf("B1", "B2"), learningRate = 0.00001)
        println(min.eval())
    }

    @Test
    fun `types in sheet`() {
        val sheet = sheetOf {
            val a1 by constant("My String")
            val a2 by dollars(1.23)
            listOf(a1, a2)
        }
        println("$sheet\n")
        sheet["A1"].assertString("My String")
        sheet["A2"].assertString("$1.23")
    }

    @Test
    fun `find minimum`() {
        val sheet = sheetOf {
            val a1 by constant(2.0)
            val b1 by constant(11.0)
            val a2 by constant(-2.0)
            val a3 by b1 - a2 - 1.0
            val a4 by pow(a1, 2.0) + pow(up, 2.0) + 1.0
            listOf(a1, a2, b1, a3, a4)
        }
        println("$sheet\n")
        val minimized = sheet.minimize(
            target = "A4",
            variables = listOf("A1", "B1", "A2")
        )
        println("$minimized\n")
        minimized["A1"].assertString("0")
        minimized["A2"].assertString("4")
        minimized["B1"].assertString("5")
        println(minimized.eval())
        minimized.eval()["A4"].assertString("1") // The minimized target
    }

    @Test
    fun `find minimum but keep a variable constant`() {
        val sheet = sheetOf {
            val a1 by constant(2.0)
            val b1 by constant(11.0)
            val a2 by constant(-2.0)
            val a3 by b1 - a2 - 1.0
            val a4 by pow(a1, 2.0) + pow(up, 2.0) + 1.0
            listOf(a1, a2, b1, a3, a4)
        }
        println("$sheet\n")
        val minimized = sheet.minimize(
            target = "A4",
            variables = listOf("A1", "A2")
        ) // Does not include B1
        println("$minimized\n")
        minimized["A1"].assertString("0")
        minimized["A2"].assertString("10")
        minimized["B1"].assertString("11") // Should be held constant
        minimized.eval()["A4"].assertString("1") // The minimized target
    }

    @Test
    fun `empty sheet can still print`() {
        val sheet = sheetOf {
            listOf()
        }
        println(sheet)
    }

    @Test
    fun `access column by name`() {
        val retire = sheetOfCsv {
            """
                cats,dogs
                1,2
                3,4
            """.trimIndent()
        }.copy {
            val cats by column("cats")
            val dogs by column("dogs")
            val sum by columnOf(2) { cats + dogs }
            listOf(sum)
        }
        retire.assertString(
            """
              cats dogs  sum  
              ---- ---- ----- 
            1    1    2 A1+B1 
            2    3    4 A2+B2
            """.trimIndent()
        )
    }

    @Test
    fun `column of two ranges added`() {
        val retire = sheetOfCsv {
            """
                A,B
                1,2
                3,4
            """.trimIndent()
        }.copy {
            val a by range("A")
            val b by range("B")
            val c1 by columnOf(2) { a + b }
            listOf(c1)
        }
        retire.assertString(
            """
              a b   C   
              - - ----- 
            1 1 2 A1+B1 
            2 3 4 A2+B2
        """.trimIndent()
        )
    }

    @Test
    fun `column of two ranges added anonymous`() {
        val retire = sheetOfCsv {
            """
                A,B
                1,2
                3,4
            """.trimIndent()
        }.copy {
            val a = range("A")
            val b = range("B")
            val c1 by columnOf(2) { a + b }
            listOf(c1)
        }
        retire.assertString(
            """
              A B   C   
              - - ----- 
            1 1 2 A1+B1 
            2 3 4 A2+B2
        """.trimIndent()
        )
    }

    @Test
    fun `statistic of block range`() {
        val check = sheetOfCsv {
            """
                A,B
                1,2
                3,4
            """.trimIndent()
        }.copy {
            val r by range("A1:B2")
            val mean by mean(r)
            val stddev by stddev(r)
            val percentile by percentile(r, 0.25)
            listOf(mean, stddev, percentile)
        }
        check.assertString(
            """
                  A B 
                  - - 
                1 1 2 
                2 3 4 
                mean=mean(A1:B2)
                percentile=percentile(A1:B2,0.25)
                stddev=stddev(A1:B2)
            """.trimIndent()
        )
        check.eval().assertString(
            """
                  A B 
                  - - 
                1 1 2 
                2 3 4 
                mean=2.5
                percentile=2
                stddev=1.29099
            """.trimIndent()
        )
    }

    @Test
    fun `division table using fixed column and rows`() {
        val sheet = sheetOf {
            val b1 by rowOf(10) { it }
            val a2 by columnOf(10) { it }
            val b2 by matrixOf(10, 10) {
                cell("C$1") / cell("\$A3")
            }
            listOf(b1, a2, b2)
        }
        sheet.assertString(
            """
               A    B      C      D      E      F      G      H      I      J      K   
               - ------ ------ ------ ------ ------ ------ ------ ------ ------ ------ 
             1        0      1      2      3      4      5      6      7      8      9 
             2 0  C1/A3  D1/A3  E1/A3  F1/A3  G1/A3  H1/A3  I1/A3  J1/A3  K1/A3  L1/A3 
             3 1  C1/A4  D1/A4  E1/A4  F1/A4  G1/A4  H1/A4  I1/A4  J1/A4  K1/A4  L1/A4 
             4 2  C1/A5  D1/A5  E1/A5  F1/A5  G1/A5  H1/A5  I1/A5  J1/A5  K1/A5  L1/A5 
             5 3  C1/A6  D1/A6  E1/A6  F1/A6  G1/A6  H1/A6  I1/A6  J1/A6  K1/A6  L1/A6 
             6 4  C1/A7  D1/A7  E1/A7  F1/A7  G1/A7  H1/A7  I1/A7  J1/A7  K1/A7  L1/A7 
             7 5  C1/A8  D1/A8  E1/A8  F1/A8  G1/A8  H1/A8  I1/A8  J1/A8  K1/A8  L1/A8 
             8 6  C1/A9  D1/A9  E1/A9  F1/A9  G1/A9  H1/A9  I1/A9  J1/A9  K1/A9  L1/A9 
             9 7 C1/A10 D1/A10 E1/A10 F1/A10 G1/A10 H1/A10 I1/A10 J1/A10 K1/A10 L1/A10 
            10 8 C1/A11 D1/A11 E1/A11 F1/A11 G1/A11 H1/A11 I1/A11 J1/A11 K1/A11 L1/A11 
            11 9 C1/A12 D1/A12 E1/A12 F1/A12 G1/A12 H1/A12 I1/A12 J1/A12 K1/A12 L1/A12  
        """.trimIndent()
        )
        sheet.eval().assertString(
            """
               A    B       C       D       E       F       G       H       I       J       K   
               - ------- ------- ------- ------- ------- ------- ------- ------- ------- ------ 
             1         0       1       2       3       4       5       6       7       8      9 
             2 0       1       2       3       4       5       6       7       8       9     L1 
             3 1     0.5       1     1.5       2     2.5       3     3.5       4     4.5   L1/2 
             4 2 0.33333 0.66667       1 1.33333 1.66667       2 2.33333 2.66667       3   L1/3 
             5 3    0.25     0.5    0.75       1    1.25     1.5    1.75       2    2.25   L1/4 
             6 4     0.2     0.4     0.6     0.8       1     1.2     1.4     1.6     1.8   L1/5 
             7 5 0.16667 0.33333     0.5 0.66667 0.83333       1 1.16667 1.33333     1.5   L1/6 
             8 6 0.14286 0.28571 0.42857 0.57143 0.71429 0.85714       1 1.14286 1.28571   L1/7 
             9 7   0.125    0.25   0.375     0.5   0.625    0.75   0.875       1   1.125   L1/8 
            10 8 0.11111 0.22222 0.33333 0.44444 0.55556 0.66667 0.77778 0.88889       1   L1/9 
            11 9   1/A12   2/A12   3/A12   4/A12   5/A12   6/A12   7/A12   8/A12   9/A12 L1/A12 
        """.trimIndent()
        )
    }

    @Test
    fun `sum of relative references`() {
        val sheet = sheetOf {
            val a1 by columnOf(1.0, 1.0)
            val b1 by columnOf(left + 1.0, left + 2.0)
            val b3 by sum(b1)
            listOf(a1, b1, b3)
        }
        println(sheet)

        sheet["A1"].assertString("1")
        sheet["A2"].assertString("1")
        sheet["B1"].assertString("A1+1")
        sheet["B2"].assertString("A2+2")
        sheet["B3"].assertString("sum(B1)")
        println(sheet.eval())
        sheet.eval()["B3"].assertString("5")
    }

    @Test
    fun `read and clean csv`() {
        val csv = File("./src/test/kotlin/com/github/jomof/kane/SP500toGDP.csv").absoluteFile
        val rows: List<Map<String, String>> = csvReader().readAllWithHeader(csv)
        assert(csv.isFile)
        println(analyzeDataTypes(rows))
        val sheet = readCsv(csv)
        println(sheet)
    }

    @Test
    fun `copy should not change moveable references`() {
        val sheet = sheetOf {
            val a1 by 1.0
            val a2 by a1 + a1
            listOf(a2)
        }.copy("a1" to 2.0)
        sheet.assertString(
            """
                A   
              ----- 
            1     2 
            2 A1+A1 
        """.trimIndent()
        )
    }

    @Test
    fun `sheet with named columns`() {
        val sheet = sheetOf {
            nameColumn(0, "Column 0")
            nameColumn(1, "Column 1")
            listOf()
        }
        println(sheet)
    }

    @Test
    fun `sheet with named tiling scalarizes`() {
        val sheet = sheetOf {
            val tiling by columnOf("a", "b", "c")
            listOf(tiling)
        }
        sheet.assertString(
            """
              tiling 
              ------ 
            1      a 
            2      b 
            3      c 
        """.trimIndent()
        )
    }

    @Test
    fun `second element of columnOf`() {
        val sheet = sheetOf {
            val a1 by columnOf(1.0, 2.0)
            listOf(a1)
        }
        println(sheet)
        sheet["A1"].assertString("1")
        sheet["A2"].assertString("2")
    }

    @Test
    fun softmax() {
        val sheet = sheetOf {
            val a1 by columnOf(1.0, 1.0)
            val b1 by columnOf(left + 1.0, left + 2.0)
            val c1 by softmax(b1)
            listOf(a1, b1, c1)
        }
        println(sheet)
        println(sheet.eval())
        sheet["A1"].assertString("1")
        sheet["A2"].assertString("1")
        sheet["B1"].assertString("A1+1")
        sheet["B2"].assertString("A2+2")
        sheet["C1"].assertString("exp(B1)/sum(exp(B1))")
        sheet["C2"].assertString("exp(B2)/sum(exp(B1))")

    }

    @Test
    fun `transitive reference`() {
        val sheet = sheetOf {
            val a1 by columnOf(0.2, 1.3)
            val b1 by columnOf((left(1) + 0.0) * left(1), (left(1) + 0.0) * left(1))
            val d1 by softmax(b1, constant(0.1))
            val e1 by softmin(b1, constant(0.1))
            val b3 by sum(b1)
            listOf(d1, b3, a1, e1, b1)
        }
        println(sheet)
        println(sheet.eval())
    }

    @Test
    fun `summation of inline expression`() {
        val sheet = sheetOf {
            val a1 by columnOf(0.2, 1.3)
            val b1 by columnOf((left(1) + 0.0) * left(1), (left(1) + 0.0) * left(1))
            val d1 by softmax(10_000.00 - b1)
            val b3 by sum(b1)
            listOf(d1, b3, b1, a1)
        }
        println(sheet)
        println(sheet.eval())
    }

    @Test
    fun `simplest tiling operations`() {
        val sheet = sheetOf {
            val a1 by columnOf("x", 92.2)
            listOf(a1)
        }
        sheet.assertString(
            """
                A  
              ---- 
            1    x 
            2 92.2 
        """.trimIndent()
        )
    }

    @Test
    fun `tiling operations`() {
        val sheet = sheetOf {
            val a2 by columnOf("three", "blind", "mice")
            val b1 by rowOf("see", "how", "they", "run")
            val b2 by rowOf("x", 92.2, "y", 102.2)
            val b3 by columnOf("m", 11.2, "b", 7.1)
            val c3 by rowOf(dollars(1.11), dollars(1.23))
            val c4 by rowOf(11.0 to 13.0)
            val b7 by constant(1492.0)
            val c5 by rowOf((1..4).map { logit(b7) })
            listOf(a2, b1, b2, b3, c3, c4, c5)
        }
        println(sheet)
        sheet.assertString("""
                A     B      C         D         E         F     
              ----- ---- --------- --------- --------- --------- 
            1        see       how      they       run           
            2 three    x      92.2         y     102.2           
            3 blind    m     ${'$'}1.11     ${'$'}1.23                     
            4  mice 11.2        11        12        13           
            5          b logit(B7) logit(B7) logit(B7) logit(B7) 
            6        7.1                                         
            7       1492                                         
        """.trimIndent())
    }

    @Test
    fun `statistical functions`() {
        val sheet = sheetOf {
            val a1 by columnOf(7.0 to 12.0)
            val d1 by "mean"
            val e1 by mean(a1)
            val d2 by "stdev"
            val e2 by stddev(a1)
            val d3 by "count"
            val e3 by count(a1)

            listOf(a1, d1, e1, d2, e2, d3, e3)
        }
        println(sheet)
        println(sheet.eval())
        sheet.eval()["E1"].assertString("9.5")
        sheet.eval()["E2"].assertString("1.87083")
        sheet.eval()["E3"].assertString("6")
    }

    @Test
    fun `statistics of dog weights`() {
        val sheet = sheetOfCsv {
            """
                weight
                600
                470
                170
                430
                300
            """.trimIndent()
        }.copy {
            val weight by column("weight")
            val mean by mean(weight)
            val difference by mean - weight
            val squared by pow(difference, 2.0)
            val sumSquared by sum(squared)
            val variance by sumSquared / (count(squared) - 1.0)
            val varianceExpr by variance(weight)
            val stddev by pow(variance, 0.5)
            val stddevExpr by stddev(weight)
            difference.assertString("difference=mean-weight")
            listOf(mean, difference, squared, sumSquared, variance, varianceExpr, stddev, stddevExpr)
        }
        println(sheet)
        sheet.eval().assertString(
            """
              weight squared difference 
              ------ ------- ---------- 
            1    600   42436       -206 
            2    470    5776        -76 
            3    170   50176        224 
            4    430    1296        -36 
            5    300    8836         94 
            mean=394
            stddev=164.71187
            stddevExpr=164.71187
            sumSquared=108520
            variance=27130
            varianceExpr=27130  
        """.trimIndent()
        )
    }

    //@Test
    fun `predict 5th year`() {
        // Given last four years of stock, bond, and inflation predict fifth year

        val random = Random(3)
        val learningRate = 0.001
        val batchSize = 100.0
        val inputs = 15
        val count0 = 9
        val count1 = 7
        val count2 = 5
        val outputs = 3
        val input by matrixVariable(1, inputs) { 0.0001 }
        val w0 by matrixVariable(input.rows, count0) { random.nextGaussian() }
        val b1 by variable(0.0)
        val h1 by logit(w0 cross input) + b1
        val w1 by matrixVariable(h1.rows, count1) { random.nextGaussian() }

        val b2 by variable(0.0)
        val b3 by variable(0.0)
        val h2 by lrelu(w1 cross h1)
        val w2 by matrixVariable(h2.rows, count2) { random.nextGaussian() }

        val h3 by logit(w2 cross h2) + b2
        val w3 by matrixVariable(h3.rows, outputs) { random.nextGaussian() }

        val output by logit(w3 cross h3) + b3

        val target by matrixVariable(output.columns, output.rows) { 0.0 }
        val error by sum(0.5 * pow(target - output, 2.0))
        val sumdw0 by matrixVariable(w0.columns, w0.rows) { 0.0 }
        val sumdw1 by matrixVariable(w1.columns, w1.rows) { 0.0 }
        val sumdw2 by matrixVariable(w2.columns, w2.rows) { 0.0 }
        val sumdw3 by matrixVariable(w3.columns, w3.rows) { 0.0 }
        val sumb1 by variable(0.0)
        val sumb2 by variable(0.0)
        val sumb3 by variable(0.0)
        val dw0 by sumdw0 + learningRate/batchSize * differentiate(d(error) / d(w0))
        val dw1 by sumdw1 + learningRate/batchSize * differentiate(d(error) / d(w1))
        val dw2 by sumdw2 + learningRate/batchSize * differentiate(d(error) / d(w2))
        val dw3 by sumdw3 + learningRate/batchSize * differentiate(d(error) / d(w3))
        val db1 by sumb1 + learningRate/batchSize * differentiate(d(error) / d(b1))
        val db2 by sumb2 + learningRate/batchSize * differentiate(d(error) / d(b2))
        val db3 by sumb3 + learningRate/batchSize * differentiate(d(error) / d(b3))
        val adw0 by assign(dw0 to sumdw0)
        val adw1 by assign(dw1 to sumdw1)
        val adw2 by assign(dw2 to sumdw2)
        val adw3 by assign(dw3 to sumdw3)
        val adb1 by assign(db1 to sumb1)
        val adb2 by assign(db2 to sumb2)
        val adb3 by assign(db3 to sumb3)
        val tab = tableauOf(output.type,output, error, dw2, dw3, adw2, adw3, adw0, adw1, adb1, adb2, adb3)
        val layout = tab.linearize()
        //println(layout)
        val space = layout.allocateSpace()
        val targetRef = layout.shape(target).ref(space)
        val errorRef = layout.shape(error).ref(space)
        val outputRef = layout.shape(output).ref(space)
        val inputRef = layout.shape(input).ref(space)
        val w0ref = layout.shape(w0).ref(space)
        val w1ref = layout.shape(w1).ref(space)
        val w2ref = layout.shape(w2).ref(space)
        val w3ref = layout.shape(w3).ref(space)
        val b1ref = layout.shape(b1).ref(space)
        val b2ref = layout.shape(b2).ref(space)
        val b3ref = layout.shape(b3).ref(space)
        val sumdw0ref = layout.shape(sumdw0).ref(space)
        val sumdw1ref = layout.shape(sumdw1).ref(space)
        val sumdw2ref = layout.shape(sumdw2).ref(space)
        val sumdw3ref = layout.shape(sumdw3).ref(space)
        val sumb1ref = layout.shape(sumb1).ref(space)
        val sumb2ref = layout.shape(sumb2).ref(space)
        val sumb3ref = layout.shape(sumb3).ref(space)

        fun train() : Any {
            val startYear = abs(random.nextInt(2015 - 1928) + 1928)
            var index = 0
            for (year in startYear until startYear + 5) {
                inputRef[0, index++] = sp500(year)
                inputRef[0, index++] = baaCorporateBond(year)
                inputRef[0, index++] = usInflation(year)
            }
            targetRef[0, 0] = sp500(startYear + 5)
            targetRef[0, 1] = baaCorporateBond(startYear + 4)
            targetRef[0, 2] = usInflation(startYear + 4)

            layout.eval(space)
            return targetRef
        }

        var lastError = 1000.0
        repeat(100000) {
            sumdw0ref.zero()
            sumdw1ref.zero()
            sumdw2ref.zero()
            sumdw3ref.zero()
            sumb1ref.set(0.0)
            sumb2ref.set(0.0)
            sumb3ref.set(0.0)
            var totalError = 0.0
            repeat(batchSize.toInt()) {
                train()
                totalError += errorRef.value
            }
            totalError /= batchSize
            if(lastError > totalError) {
                lastError = totalError
                repeat(4) {
                    val train = train()
                    println("b1=$b1ref b2=$b2ref b3=$b3ref : $train -> $outputRef")
                }
                println("error=$totalError")
            }

            w0ref -= sumdw0ref
            w1ref -= sumdw1ref
            w2ref -= sumdw2ref
            w3ref -= sumdw3ref
            b1ref.set(b1ref.value - sumb1ref.value)
            b2ref.set(b2ref.value - sumb2ref.value)
            b3ref.set(b3ref.value - sumb3ref.value)
            if (it % 1000 == 999) {
                if (abs(totalError) < 0.0001)
                    return
            }

        }
    }

    //@Test
    fun `build table of expected`() {
        fun expected(years : Int, stock : Double, percentile : Double) : Double {
            val minYear = 1928
            val maxYear = 2019
            val population = mutableListOf<Double>()

            for (startYear in minYear..maxYear) {
                var total = 1.00 // One dollar
                for (year in 0 until years) {
                    val inspectionYear =
                        if (year + startYear <= maxYear) year + startYear
                        else year + startYear - (maxYear - minYear) + 1

                    val stockReturn = sp500(inspectionYear.toDouble())
                    val bondReturn = baaCorporateBond(inspectionYear.toDouble())
                    val inflationReturn = usInflation(inspectionYear.toDouble())
                    val stockIncrease = stockReturn * total * stock
                    val bondIncrease = bondReturn * total * (1.0 - stock)
                    val inflationIncrease = inflationReturn * total
                    total += stockIncrease + bondIncrease - inflationIncrease
                }
                // Convert to annual percentage (1-annual)^years = total
                // annual total^(1/years) - 1
                val annual = pow(total, 1.0 / years) - 1.0
                population += annual
            }

            val sorted = population.sorted()
            return sorted[min(sorted.size, round((sorted.size - 1.0) * percentile).toInt())]
        }

        val data = mutableMapOf<Triple<Double, Double, Double>, Double>()
        for (years in 5 .. 30) {
            for (stock in 64 .. 74 step 2) {
                for (percentile in 48 .. 52 step 2) {
                    val stockpct = stock.toDouble() / 100.0
                    val percentilepct = percentile.toDouble() / 100.0
                    val result = expected(years, stockpct, percentilepct)
                    data[Triple(years / 100.0, stockpct, percentilepct)] = result
                    println("years=$years stock=$stock percentile=$percentile -> $result")
                }
            }
        }

        println("Training ${data.size} points")

        val random = Random(3)
        val learningRate = 1.0
        val batchSize = 100000.0
        val inputs = 3
        //val count0 = 2
        val count1 = 3
        val count2 = 3
        val outputs = 1
        val input by matrixVariable(1, inputs) { 0.0001 }
        //val w0 by matrixVariable(input.rows, count0) { random.nextGaussian() }

        //val h1 by lrelu(w0 cross input)
        val w1 by matrixVariable(input.rows, count1) { random.nextGaussian() }
        val b1 by variable(0.0)
        val b2 by variable(0.0)
        val b3 by variable(0.0)
        val h2 by logit(w1 cross input) + b1
        val w2 by matrixVariable(h2.rows, count2) { random.nextGaussian() }

        val h3 by (w2 cross h2) + b2
        val w3 by matrixVariable(h3.rows, outputs) { random.nextGaussian() }

        val output by logit(w3 cross h3) + b3

        val target by matrixVariable(output.columns, output.rows) { 0.0 }
        val error by sum(0.5 * pow(target - output, 2.0))
        //val sumdw0 by matrixVariable(w0.columns,w0.rows) { 0.0 }
        val sumdw1 by matrixVariable(w1.columns, w1.rows) { 0.0 }
        val sumdw2 by matrixVariable(w2.columns, w2.rows) { 0.0 }
        val sumdw3 by matrixVariable(w3.columns, w3.rows) { 0.0 }
        val sumb1 by variable(0.0)
        val sumb2 by variable(0.0)
        val sumb3 by variable(0.0)
        //val dw0 by sumdw0 + learningRate/batchSize * differentiate(d(error) / d(w0))
        val dw1 by sumdw1 + learningRate/batchSize * differentiate(d(error) / d(w1))
        val dw2 by sumdw2 + learningRate/batchSize * differentiate(d(error) / d(w2))
        val dw3 by sumdw3 + learningRate/batchSize * differentiate(d(error) / d(w3))
        val db1 by sumb1 + learningRate/batchSize * differentiate(d(error) / d(b1))
        val db2 by sumb2 + learningRate/batchSize * differentiate(d(error) / d(b2))
        val db3 by sumb3 + learningRate/batchSize * differentiate(d(error) / d(b3))
        //val adw0 by assign(dw0 to sumdw0)
        val adw1 by assign(dw1 to sumdw1)
        val adw2 by assign(dw2 to sumdw2)
        val adw3 by assign(dw3 to sumdw3)
        val adb1 by assign(db1 to sumb1)
        val adb2 by assign(db2 to sumb2)
        val adb3 by assign(db3 to sumb3)
        val tab = tableauOf(output.type,output, error, dw2, dw3, adw2, adw3, adw1, adb1, adb2, adb3)
        val layout = tab.linearize()
        //println(layout)
        val space = layout.allocateSpace()
        val targetRef = layout.shape(target).ref(space)
        val errorRef = layout.shape(error).ref(space)
        val outputRef = layout.shape(output).ref(space)
        val inputRef = layout.shape(input).ref(space)
        //val w0ref = layout.shape(w0).ref(space)
        val w1ref = layout.shape(w1).ref(space)
        val w2ref = layout.shape(w2).ref(space)
        val w3ref = layout.shape(w3).ref(space)
        val b1ref = layout.shape(b1).ref(space)
        val b2ref = layout.shape(b2).ref(space)
        val b3ref = layout.shape(b3).ref(space)
        //val sumdw0ref = layout.shape(sumdw0).ref(space)
        val sumdw1ref = layout.shape(sumdw1).ref(space)
        val sumdw2ref = layout.shape(sumdw2).ref(space)
        val sumdw3ref = layout.shape(sumdw3).ref(space)
        val sumb1ref = layout.shape(sumb1).ref(space)
        val sumb2ref = layout.shape(sumb2).ref(space)
        val sumb3ref = layout.shape(sumb3).ref(space)

        val keys = data.keys.toTypedArray()
        fun train() : Any {
            val roll = abs(random.nextInt(data.size))
            val key = keys[roll]
            val (years, stock, percentile) = key
            val result = data[key]!!
            if (abs(result) > 3.0)
               assert(false)
            inputRef[0, 0] = years
            inputRef[0, 1] = stock
            inputRef[0, 2] = percentile
            targetRef.set(result)
            layout.eval(space)
            return result
        }

        var lastError = 1000.0
        repeat(100000) {
            //sumdw0ref.zero()
            sumdw1ref.zero()
            sumdw2ref.zero()
            sumdw3ref.zero()
            sumb1ref.set(0.0)
            sumb2ref.set(0.0)
            sumb3ref.set(0.0)
            var totalError = 0.0
            repeat(batchSize.toInt()) {
                train()
                totalError += errorRef.value
            }
            totalError /= batchSize
            if(lastError > totalError) {
                lastError = totalError
                repeat(4) {
                    val train = train()
                    println("b1=$b1ref b2=$b2ref b3=$b3ref : $train -> $outputRef")
                }
                println("error=$totalError")
            }

            //w0ref -= sumdw0ref
            w1ref -= sumdw1ref
            w2ref -= sumdw2ref
            w3ref -= sumdw3ref
            b1ref.set(b1ref.value - sumb1ref.value)
            b2ref.set(b2ref.value - sumb2ref.value)
            b3ref.set(b3ref.value - sumb3ref.value)
            if (it % 1000 == 999) {
                if (abs(totalError) < 0.0001)
                    return
            }

        }
    }

    @Test
    fun `repro named matrix issue`() {
        val sheet = sheetOf {
            val a1 by 0.0
            val b1 by columnOf(left(1) * left(1))
            val a2 by cv(b1)
            listOf(a1, b1, a2)
        }
        println(sheet.eval())

        sheet.minimize(
            target = "A2",
            variables = listOf("A1")
        ).eval()
    }

    @Test
    fun `optimize stock-bond split based on trailing Shiller PE`() {
        val startYear = 1928
        val endYear = 1938 // 2019
        val totalYears = endYear - startYear + 1
        val rollingWindow = 2
        // expected(#years, %stock) [then 5% and 95% confidence]
        val sheet = sheetOf {
            val a1 by constant("m")
            val b1 by constant(0.0)
            val a2 by constant("b")
            val b2 by constant(0.0)
            val a3 by constant("YEAR")
            val a4 by columnOf(startYear.toDouble() to endYear.toDouble())
            val b3 by constant("Shiller PE")
            val b4 by shillerPE(a4)
            val c3 by constant("S&P 500")
            val c4 by sp500(a4)
            val c2 by stddev(c4)
            val d3 by constant("BAA Corp Bonds")
            val d4 by baaCorporateBond(a4)
            val d2 by stddev(d4)

            val g3 by constant("stock(%)")
            val g4 by columnOf((0 until totalYears - rollingWindow).map {
                logit(b1 / left(5) + b2)
            })
            val h3 by constant("bond(%)")
            val h4 by columnOf((0 until totalYears - rollingWindow).map { 1.0 - left })

            val compositeCumulative = (0 until rollingWindow).fold(dollars(1.00)) { prior, current ->
                prior * (1.0 + left(6).down(current)) * left(2).down(current) +
                        prior * (1.0 + left(5).down(current)) * left(1).down(current)
            }
            val i3 by constant("Mix($)")
            val i4 by columnOf((0 until totalYears - rollingWindow * 2).map { compositeCumulative })
            val i2 by cv(i4)

            val i1 by constant("error")
            val j2 by sum(i4)

            val j1 by pow(1.45 - i2, 2.0)
            listOf(
                g4, a1, a2, b1, b2, a3, a4, b4, b3, c2, c3, c4, d3,
                d4, g3, h3, h4, i3, i4, i1, j1, j2, d2, i2
            )
        }
        println(sheet.eval())
        repeat(1) {
            val min = sheet.minimize(
                target = "J1",
                variables = listOf("B1", "B2"),
                learningRate = 0.01
            ).eval()
            println(min.eval())
        }

//        min["G4"].assertString("0")
//        min["H4"].assertString("1")

    }

    @Test
    fun `copy analysis ages`() {
        val start = 2021
        val end = start + 30
        val allYears = end - start + 1
        val startAllocs = AlgebraicBinaryScalar.allocs
        val modelStartYear by randomOf(1928.0 to 2019.0)
        val retire = sheetOf {
            val years by columnOf(allYears) { constant(start + it) }
            val j by years - 1969
            val k by years - 1972
            listOf(years, j, k)
        }
        println(retire.eval())
        (AlgebraicBinaryScalar.allocs - startAllocs).assertString("62")
    }

    @Test
    fun `copy analysis with modelStartYear`() {
        val start = 2021
        val end = start + 30
        val allYears = end - start + 1
        val startAllocs = AlgebraicBinaryScalar.allocs
        val modelStartYear by randomOf(1928.0 to 2019.0)
        val retire = sheetOf {
            val years by columnOf(allYears) { constant(start + it) }
            val j by years - 1969
            val k by years - 1972
            val modelYear by years - start + modelStartYear
            listOf(years, j, k, modelYear)
        }
        println(retire.eval())
        (AlgebraicBinaryScalar.allocs - startAllocs).assertString("247")
    }

    @Test
    fun `copy analysis with with stock, bond, and inflation`() {
        val start = 2021
        val end = start + 30
        val allYears = end - start + 1
        val startAllocs = AlgebraicBinaryScalar.allocs
        val modelStartYear by randomOf(1928.0 to 2019.0)
        val retire = sheetOf {
            val years by columnOf(allYears) { constant(start + it) }
            val j by years - 1969
            val k by years - 1972
            val modelYear by years - start + modelStartYear
            val stock by sp500(modelYear)
            val bond by baaCorporateBond(modelYear)
            val inflation by usInflation(modelYear)
            listOf(years, j, k, modelYear, stock, bond, inflation)
        }
        println(retire.eval())
        (AlgebraicBinaryScalar.allocs - startAllocs).assertString("619")

    }

    @Test
    fun `copy analysis with with pre and post tax`() {
        val start = 2021
        val end = start + 30
        val allYears = end - start + 1
        val startAllocs = AlgebraicBinaryScalar.allocs
        val modelStartYear by randomOf(1928.0 to 2019.0)
        val pre1 by dollars(25)
        val pre2 by dollars(25)
        val pre3 by dollars(25)
        val pre4 by dollars(25)
        val initialPreTax by pre1 + pre2 + pre3 + pre4
        val post1 by dollars(250)
        val post2 by dollars(25)
        val post3 by dollars(25)
        val post4 by dollars(25)
        val initialPostTax by post1 + post2 + post3 + post4

        val retire = sheetOf {
            val stockRatio by percent(0.7)
            val years by columnOf(allYears) { constant(start + it) }
            val j by years - 1969
            val k by years - 1972
            val modelYear by years - start + modelStartYear
            val stock by sp500(modelYear)
            val bond by baaCorporateBond(modelYear)
            val inflation by usInflation(modelYear)
            val multiplier by 1.0 + stock * stockRatio + bond * (1.0 - stockRatio)
            val preTax by initialPreTax stack (multiplier * up)
            val postTax by initialPostTax stack (multiplier * up)
            val total by preTax + postTax
            listOf(years, j, k, modelYear, stock, bond, inflation, multiplier, preTax, postTax)
        }
        println(retire.eval())
        // (AlgebraicBinaryScalar.allocs - startAllocs).assertString("619")
    }
}