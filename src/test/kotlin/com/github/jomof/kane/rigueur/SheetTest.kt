package com.github.jomof.kane.rigueur

import com.github.jomof.kane.rigueur.functions.*
import com.github.jomof.kane.rigueur.types.dollars
import org.junit.Test

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
    fun `basic sheet`() {
        val sheet = sheetOf("basics") {
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
        val sheet = sheetOf("dollars") {
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
        val sheet = sheetOf("dollars") {
            val a1 by columnOf(2020, 2021, 2022, 2023)
            val b2 by columnOf(2020, 2021, 2022, 2023)
            val c3 by columnOf(up + 1, up + 2, up + 3, up + 4)
            add(a1, b2, c3)
        }
        println(sheet)
    }

    @Test
    fun `types in sheet`() {
        val sheet = sheetOf("dollars") {
            val a1 by constant("My String")
            val a2 by dollars(1.23)
            add(a1, a2)
        }
        println("$sheet\n")
        sheet["A1"]!!.assertString("My String")
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
        val minimized = sheet.minimize("A4", "A1", "B1", "A2")
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
        val minimized = sheet.minimize("A4", "A1", "A2") // Does not include B1
        println("$minimized\n")
        minimized["A1"].assertString("0")
        minimized["A2"].assertString("10")
        minimized["B1"].assertString("11") // Should be held constant
        minimized.eval()["A4"].assertString("1") // The minimized target
    }

    @Test
    fun `replace expr`() {
        val v by variable(0.0)
        val expr by v + v
        val result = expr
            .replaceBottomUp {
                when(it) {
                    is AlgebraicBinaryScalar -> it.left - it.right
                    else -> it
                }
            }
        result.assertString("expr=v-v")
    }

    @Test
    fun `empty sheet can still print`() {
        val sheet = sheetOf {
        }
        println(sheet)
    }

    @Test
    fun `optimize stock-bond split based on trailing Shiller PE`() {
        val startYear = 1928
        val endYear = 1949 // 2019
        val totalYears = endYear - startYear + 1
        val rollingWindow = 2
        val sheet = sheetOf {
            val a1 by constant("m")
            val b1 by constant(0.0)
            val a2 by constant("b")
            val b2 by constant(0.5)
            val a3 by constant("YEAR")
            val a4 by columnOf(startYear .. endYear)
            val b3 by constant("Shiller PE")
            val b4 by shillerPE(a4)
            val c3 by constant("S&P 500")
            val c4 by sp500(a4)
            val d3 by constant("BAA Corp Bonds")
            val d4 by baaCorporateBond(a4)

            val g3 by constant("stock(%)")
            val g4 by columnOf((0 until totalYears - rollingWindow).map { logit(b1/left(5) + b2) })
            val h3 by constant("bond(%)")
            val h4 by columnOf((0 until totalYears - rollingWindow).map { 1.0 - left })

            val compositeCumulative = (0 until rollingWindow).fold(dollars(100.00)) { prior, current ->
                prior * (1.0 + left(6).down(current))*left(2).down(current) +
                prior * (1.0 + left(5).down(current))*left(1).down(current)
            }
            val i3 by constant("Mix($)")
            val i4 by columnOf((0 until totalYears - rollingWindow * 2).map { compositeCumulative } )

            val j3 by dollars(0.0)
            val j4 by columnOf((0 until totalYears - rollingWindow * 2).map { dollars(0.0) + up + left } )

            val j1 by pow(200_000.00 - down(totalYears-rollingWindow*2+2),2.0)
            add(g4, a1, a2, b1, b2, a3, a4, b4, b3, c3, c4, d3,
                d4, g3, h3, h4, i3, i4, j3, j4, j1)
        }
        println(sheet.eval())
        val min = sheet.minimize("J1", "B1", "B2").eval()
        println(min.eval())
        min["G4"].assertString("1")
        min["H4"].assertString("0")

    }
}