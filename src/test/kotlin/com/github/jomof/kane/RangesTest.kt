package com.github.jomof.kane

import org.junit.Test

class RangesTest {
    @Test
    fun `check cell names`() {
        cellNameToComputableCoordinate("A1").assertString("A1")
        cellNameToComputableCoordinate("B1").assertString("B1")
        cellNameToComputableCoordinate("A2").assertString("A2")
        cellNameToComputableCoordinate("\$A1").assertString("\$A1")
        cellNameToComputableCoordinate("\$B1").assertString("\$B1")
        cellNameToComputableCoordinate("\$A2").assertString("\$A2")
        cellNameToComputableCoordinate("A\$1").assertString("A\$1")
        cellNameToComputableCoordinate("B\$1").assertString("B\$1")
        cellNameToComputableCoordinate("A\$2").assertString("A\$2")
        cellNameToComputableCoordinate("\$A\$1").assertString("\$A\$1")
        cellNameToComputableCoordinate("\$B\$1").assertString("\$B\$1")
        cellNameToComputableCoordinate("\$A\$2").assertString("\$A\$2")
    }

    @Test
    fun `check parse range and move`() {
        parseRange("B2").right.assertString("C2")
        parseRange("B2").left.assertString("A2")
        parseRange("B2").up.assertString("B1")
        parseRange("B2").down.assertString("B3")

        parseRange("\$B2").right.assertString("\$C2")
        parseRange("\$B2").left.assertString("\$A2")
        parseRange("\$B2").up.assertString("\$B1")
        parseRange("\$B2").down.assertString("\$B3")

        parseRange("B\$2").right.assertString("C\$2")
        parseRange("B\$2").left.assertString("A\$2")
        parseRange("B\$2").up.assertString("B\$1")
        parseRange("B\$2").down.assertString("B\$3")

        parseRange("\$B\$2").right.assertString("\$C\$2")
        parseRange("\$B\$2").left.assertString("\$A\$2")
        parseRange("\$B\$2").up.assertString("\$B\$1")
        parseRange("\$B\$2").down.assertString("\$B\$3")

        parseRange("B2:C3").up.assertString("B1:C2")
        parseRange("B2:C3").down.assertString("B3:C4")
        parseRange("B2:C3").left.assertString("B3:C4")
        parseRange("B2:C3").right.assertString("B3:C4")


        parseRange("B:B").up.assertString("(B row-1)")
        parseRange("B:B").down.assertString("(B row+1)")
        parseRange("B:B").left.assertString("A")
        parseRange("B:B").right.assertString("C")

        parseRange("B:C").up.assertString("(B row-1)")
        parseRange("B:C").down.assertString("(B row+1)")
        parseRange("B:C").left.assertString("A:B")
        parseRange("B:C").right.assertString("C:D")
    }

    @Test
    fun `relative coordinate`() {
        CellRange.relative(-1, -1).assertString("(col-1 row-1)")
        CellRange.relative(0, 0).assertString("(col row)")
        CellRange.relative(1, 1).assertString("(col+1 row+1)")

    }

    @Test
    fun `check parse range`() {
        parseRange("A1").assertString("A1")
        parseRange("B1").assertString("B1")
        parseRange("A2").assertString("A2")
        parseRange("\$A1").assertString("\$A1")
        parseRange("\$B1").assertString("\$B1")
        parseRange("\$A2").assertString("\$A2")
        parseRange("A\$1").assertString("A\$1")
        parseRange("B\$1").assertString("B\$1")
        parseRange("A\$2").assertString("A\$2")
        parseRange("\$A\$1").assertString("\$A\$1")
        parseRange("\$B\$1").assertString("\$B\$1")
        parseRange("\$A\$2").assertString("\$A\$2")

        parseRange("A1:B2").assertString("A1:B2")
        parseRange("\$A1:B\$2").assertString("\$A1:B\$2")

        parseRange("A:A").assertString("A")
        parseRange("A:B").assertString("A:B")
        parseRange("A:AA").assertString("A:AA")

        parseRange("D").assertString("D")
    }

    @Test
    fun `repro AA issue`() {
        cellNameToComputableCoordinate("AA1").column.assertString("[moveable 26]")
        cellNameToComputableCoordinate("AA1").column.toColumnName().assertString("AA")
        parseRange("A:AA").assertString("A:AA")
    }

    @Test
    fun `repro D issue`() {
        parseRange("D").assertString("D")
    }

    @Test
    fun `repro A1 B2 issue`() {
        looksLikeCellName("A1:B2").assertString("false")
        parseRange("A1:B2").assertString("A1:B2")
    }


    @Test
    fun `rebase range colummn up`() {
        val columnB = parseRange("B").up
        val cellA2 = cellNameToCoordinate("A2")
        columnB.rebase(cellA2).assertString("B1")
    }

    @Test
    fun `rebase range`() {
//        val columnB = parseRange("B") as ColumnRange
//        val cellA2 = parseRange("A2") as CellRange
//        CellRange(RelativeIndex(0), RelativeIndex(-1))
//            .rebase(Coordinate(0, 1)).assertString("A1")
//        CellRange(RelativeIndex(0), RelativeIndex(-1))
//            .rebase(Coordinate(0, 1)).assertString("A1")
//        parseRange("A1").rebase(base).assertString("A1")
//        parseRange("A1").down.assertString("A2")
//        parseRange("A1").down.rebase(base).assertString("A2")
//        parseRange("A").rebase(base).assertString("A")
//        parseRange("A").down.assertString("(A row+1)")
    }
}