package com.github.jomof.kane.rigueur

import com.github.jomof.kane.rigueur.functions.*
import com.github.jomof.kane.rigueur.types.dollars
import org.junit.Test
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

        columnNameToIndex("A").assertString("0")
        columnNameToIndex("Z").assertString("25")
        columnNameToIndex("AA").assertString("26")
        columnNameToIndex("AZ").assertString("51")
        columnNameToIndex("BA").assertString("52")
        columnNameToIndex("ZZ").assertString("701")
        columnNameToIndex("AAA").assertString("702")

        cellNameToCoordinate("A1").assertString("[0,0]")
        cellNameToCoordinate("Z1").assertString("[25,0]")
        cellNameToCoordinate("A25").assertString("[0,24]")

        coordinateToCellName(0,0).assertString("A1")
    }

    @Test
    fun `don't expand constants initially`() {
        val sheet = sheetOf {
            val a1 by constant(1)
            val a2 by a1 + 1
            add(a1, a2)
        }
        println(sheet)
        sheet["A1"]!!.assertString("1")
        sheet["A2"]!!.assertString("A1+1")
        val evaluated = sheet.eval()
        println(evaluated)
        evaluated["A1"]!!.assertString("1")
        evaluated["A2"]!!.assertString("2")
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
            add(a1, a2, a3, b1, a4, a5, b5)
        }
        println(sheet)
        sheet["A1"]!!.assertString("1")
        sheet["A2"]!!.assertString("A1+1")
        sheet["A3"]!!.assertString("A2+$1.10")
        sheet["A4"]!!.assertString("A5+3")
        sheet["A5"]!!.assertString("B5+5")
        sheet["B1"]!!.assertString("A1+8")
        sheet["B5"]!!.assertString("7")
        val evaluated = sheet.eval()
        println(evaluated)
        evaluated["A1"]!!.assertString("1")
        evaluated["A2"]!!.assertString("2")
        evaluated["A3"]!!.assertString("\$3.10")
        evaluated["A4"]!!.assertString("15")
        evaluated["A5"]!!.assertString("12")
        evaluated["B1"]!!.assertString("9")
        evaluated["B5"]!!.assertString("7")
    }

    @Test
    fun `recursive sheet`() {
        val sheet = sheetOf {
            val a1 by down
            val a2 by up + 1
            add(a1, a2)
        }
        println(sheet)
        val evaluated = sheet.eval()
        println(evaluated)
        evaluated["A1"]!!.assertString("A2")
        evaluated["A2"]!!.assertString("A1+1")
    }

    @Test
    fun `columnOf sheet`() {
        val sheet = sheetOf {
            val a1 by columnOf(2020, 2021, 2022, 2023)
            val b2 by columnOf(2020, 2021, 2022, 2023)
            val c3 by columnOf(up + 1, up + 2, up + 3, up + 4)
            add(a1, b2, c3)
        }
        println(sheet)
    }

    @Test
    fun `types in sheet`() {
        val sheet = sheetOf {
            val a1 by constant("My String")
            val a2 by dollars(1.23)
            add(a1, a2)
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
            add(a1,a2,b1,a3,a4)
        }
        println("$sheet\n")
        val minimized = sheet.minimize(
            target = "A4",
            variables = listOf("A1", "B1", "A2"))
        println("$minimized\n")
        minimized["A1"].assertString("0")
        minimized["A2"].assertString("4")
        minimized["B1"].assertString("5")
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
            add(a1,a2,b1,a3,a4)
        }
        println("$sheet\n")
        val minimized = sheet.minimize(
            target = "A4",
            variables = listOf("A1", "A2")) // Does not include B1
        println("$minimized\n")
        minimized["A1"].assertString("0")
        minimized["A2"].assertString("10")
        minimized["B1"].assertString("11") // Should be held constant
        minimized.eval()["A4"].assertString("1") // The minimized target
    }

    @Test
    fun `empty sheet can still print`() {
        val sheet = sheetOf {
        }
        println(sheet)
    }

    @Test
    fun `sum of relative references`() {
        val sheet = sheetOf {
            val a1 by columnOf(1.0, 1.0)
            val b1 by columnOf(left + 1.0, left + 2.0)
            val b3 by summation(b1)
            add(a1, b1, b3)
        }
        println(sheet)

        sheet["A1"].assertString("1")
        sheet["A2"].assertString("1")
        sheet["B1"].assertString("A1+1")
        sheet["B2"].assertString("A2+2")
        sheet["B3"].assertString("B1+B2")
        println(sheet.eval())
        sheet.eval()["B3"].assertString("5")
    }

    @Test
    fun `softmax`() {
        val sheet = sheetOf {
            val a1 by columnOf(1.0, 1.0)
            val b1 by columnOf(left + 1.0, left + 2.0)
            val c1 by softmax(b1)
            add(a1, b1, c1)
        }
        println(sheet)
        sheet["A1"].assertString("1")
        sheet["A2"].assertString("1")
        sheet["B1"].assertString("A1+1")
        sheet["B2"].assertString("A2+2")
        sheet["C1"].assertString("exp(B1)/(exp(B1)+exp(B2))")
        sheet["C2"].assertString("exp(B2)/(exp(B1)+exp(B2))")
        println(sheet.eval())

    }

    @Test
    fun `transitive reference`() {
        val sheet = sheetOf {
            val a1 by columnOf(0.2, 1.3)
            val b1 by columnOf((left(1) + 0.0)*left(1), (left(1) + 0.0)*left(1))
            val d1 by softmax(b1, constant(0.1))
            val e1 by softmin(b1, constant(0.1))
            val b3 by summation(b1)
            add(d1, b3, a1, e1, b1)
        }
        println(sheet)
        println(sheet.eval())
    }

    @Test
    fun `summation of inline expression`() {
        val sheet = sheetOf {
            val a1 by columnOf(0.2, 1.3)
            val b1 by columnOf((left(1) + 0.0)*left(1), (left(1) + 0.0)*left(1))
            val d1 by softmax(10_000.00 - b1)
            val b3 by summation(b1)
            add(d1, b3, b1, a1)
        }
        println(sheet)
        println(sheet.eval())
    }

    @Test
    fun `statistical functions`() {
        val sheet = sheetOf {
            val a1 by columnOf(7 .. 12)
            val d1 by constant("mean")
            val e1 by mean(a1)
            val d2 by constant("stdev")
            val e2 by stddev(a1)
            val d3 by constant("count")
            val e3 by count(a1)

            add(a1, d1, e1, d2, e2, d3, e3)
        }
        println(sheet)
        println(sheet.eval())
        sheet.eval()["E1"].assertString("9.5")
        sheet.eval()["E2"].assertString("4.1833")
        sheet.eval()["E3"].assertString("6")
    }

   // @Test
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
                    val stockIncrease = stockReturn * total * stock
                    val bondIncrease = bondReturn * total * (1.0 - stock)
                    total += stockIncrease + bondIncrease
                }
                // Convert to annual percentage (1-annual)^years = total
                // annual total^(1/years) - 1
                val annual = pow(total, 1.0 / years) - 1.0
                population += annual
            }

            val sorted = population.sorted()
            val percentile = sorted[min(sorted.size, round((sorted.size - 1.0) * percentile).toInt())]

            return percentile
        }

        for (years in 1 .. 30) {
            for (stock in 0 .. 100 step 5) {
                for (percentile in 0 .. 100 step 5) {
                    val stockpct = stock.toDouble() / 100.0
                    val percentilepct = percentile.toDouble() / 100.0
                    val result = expected(years, stockpct, percentilepct)
                    println("$years, $stockpct, $percentilepct, $result")
                }
            }
        }

        println(expected(30, 0.7, 0.05))
    }

    @Test
    fun `optimize stock-bond split based on trailing Shiller PE`() {
        val startYear = 1928
        val endYear = 2019 // 2019
        val totalYears = endYear - startYear + 1
        val rollingWindow = 2
        // expected(#years, %stock) [then 5% and 95% confidence]
        val sheet = sheetOf {
            val a1 by constant("m")
            val b1 by constant(0.0)
            val a2 by constant("b")
            val b2 by constant(0.0)
            val a3 by constant("YEAR")
            val a4 by columnOf(startYear .. endYear)
            val b3 by constant("Shiller PE")
            val b4 by shillerPE(a4)
            val c3 by constant("S&P 500")
            val c4 by sp500(a4)
            val c2 by stddev(c4)
            val d3 by constant("BAA Corp Bonds")
            val d4 by baaCorporateBond(a4)
            val d2 by stddev(d4)

            val g3 by constant("stock(%)")
            val g4 by columnOf((0 until totalYears - rollingWindow).map { logit(b1/left(5) + b2) })
            val h3 by constant("bond(%)")
            val h4 by columnOf((0 until totalYears - rollingWindow).map { 1.0 - left })

            val compositeCumulative = (0 until rollingWindow).fold(dollars(1.00)) { prior, current ->
                prior * (1.0 + left(6).down(current))*left(2).down(current) +
                prior * (1.0 + left(5).down(current))*left(1).down(current)
            }
            val i3 by constant("Mix($)")
            val i4 by columnOf((0 until totalYears - rollingWindow * 2).map { compositeCumulative } )
            val i2 by covar(i4)

            val i1 by constant("error")
            val j2 by summation(i4)

            val j1 by pow(1.45 - i2,2.0)
            add(g4, a1, a2, b1, b2, a3, a4, b4, b3, c2, c3, c4, d3,
                d4, g3, h3, h4, i3, i4, i1, j1, j2, d2, i2)
        }
        println(sheet.eval())
            val min = sheet.minimize(
                target = "J1",
                variables = listOf("B1", "B2")
            ).eval()
        println(min.eval())
//        min["G4"].assertString("0")
//        min["H4"].assertString("1")

    }
}