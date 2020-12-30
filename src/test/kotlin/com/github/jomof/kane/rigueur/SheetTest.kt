package com.github.jomof.kane.rigueur

import com.github.jomof.kane.rigueur.Sheet.Companion.CURRENT
import com.github.jomof.kane.rigueur.types.dollars
import org.junit.Test

class SheetTest {
    @Test
    fun `test column tags`() {
        Sheet.indexToColumnTag(0).assertString("A")
        Sheet.indexToColumnTag(25).assertString("Z")
        Sheet.indexToColumnTag(26).assertString("AA")
        Sheet.indexToColumnTag(27).assertString("AB")
        Sheet.indexToColumnTag(51).assertString("AZ")
        Sheet.indexToColumnTag(52).assertString("BA")
        Sheet.indexToColumnTag(701).assertString("ZZ")
        Sheet.indexToColumnTag(702).assertString("AAA")

        Sheet.columnTagToIndex("A").assertString("0")
        Sheet.columnTagToIndex("Z").assertString("25")
        Sheet.columnTagToIndex("AA").assertString("26")
        Sheet.columnTagToIndex("AZ").assertString("51")
        Sheet.columnTagToIndex("BA").assertString("52")
        Sheet.columnTagToIndex("ZZ").assertString("701")
        Sheet.columnTagToIndex("AAA").assertString("702")

        Sheet.cellTagToCoordinate("A1").assertString("[0,0]")
        Sheet.cellTagToCoordinate("Z1").assertString("[25,0]")
        Sheet.cellTagToCoordinate("A25").assertString("[0,24]")

        Sheet.coordinateToCellTag(0,0).assertString("A1")
    }

    @Test
    fun `basic sheet`() {
        val sheet = Sheet("basics").with(
            "A1" to 1,
            "A2" to CURRENT.above() + 1,
            "A3" to CURRENT.above() + dollars(1.1),
            "B1" to CURRENT.above() + 2
        )
        sheet["A1"]!!.assertString("1")
        sheet["A2"]!!.assertString("A1+1")
        sheet["A3"]!!.assertString("A2+$1.10")
        sheet["B1"]!!.assertString("#REF!+2") // Out of range
        val evaluated = sheet.eval()
        evaluated["A3"]!!.assertString("$3.10")
        println(evaluated)
    }

    @Test
    fun `dollars sheet`() {
        val sheet = Sheet("dollars").with(
            "A1" to 2020, "B1" to dollars(1_000.25),
            "A2" to 2021, "B2" to dollars(10_000),
            "A3" to 2023, "B3" to dollars(100_000),
            "A4" to 2024, "B4" to dollars(1_000_000),
        )
        println(sheet)
    }

    @Test
    fun `replace expr`() {
        val v by variable(0.0)
        val expr by v + v
        val result = expr
            .replace {
                when(it) {
                    is BinaryScalar -> it.left - it.right
                    else -> it
                }
            }
        result.assertString("expr=v-v")
    }
}