package com.github.jomof.kane.impl

import com.github.jomof.kane.*
import com.github.jomof.kane.impl.sheet.*


internal class RowFilteringSequence(
    val sequence: Sequence<Row>,
    val sendWhen: Boolean = true,
    val predicate: (Row) -> Boolean
) : Sequence<Row>, ProvidesToSheet {
    private val sheetDelegate = lazy { iterator().toSheet() }
    private val sheet by sheetDelegate

    override fun iterator(): Iterator<Row> = run {
        // If sheet happens to be available then use it
        if (sheetDelegate.isInitialized()) sheet.iterator()
        // Otherwise, iterate without fully instantiating
        else object : Iterator<Row> {
            val iterator = sequence.iterator()
            var nextState: Int = -1 // -1 for unknown, 0 for done, 1 for continue
            var nextItem: Row? = null

            private fun calcNext() {
                while (iterator.hasNext()) {
                    val item = iterator.next()
                    if (predicate(item) == sendWhen) {
                        nextItem = item
                        nextState = 1
                        return
                    }
                }
                nextState = 0
            }

            override fun next(): Row {
                if (nextState == -1)
                    calcNext()
                if (nextState == 0)
                    throw NoSuchElementException()
                val result = nextItem
                nextItem = null
                nextState = -1
                return result as Row
            }

            override fun hasNext(): Boolean {
                if (nextState == -1)
                    calcNext()
                return nextState == 1
            }
        }
    }

    override fun toSheet() = sheet
    override fun toString() = sheet.toString()
}


val Sequence<Row>.columns
    get() = when (this) {
        is CountableColumns -> columns
        else -> error("$javaClass")
    }

fun Collection<Row>.toSheet(): Sheet = iterator().toSheet()

fun Sequence<Row>.toSheet(): Sheet = when (this) {
    is Sheet -> this
    else -> iterator().toSheet()
}

fun Sequence<Row>.eval(): Sheet = ((toSheet() as Expr).eval() as Sheet).showExcelColumnTags(false)

private fun Iterator<Row>.toSheet(): Sheet {
    val cells = mutableMapOf<Id, Expr>()
    var rowNumber = 0
    val columnDescriptors = mutableMapOf<Int, ColumnDescriptor>()
    val rowDescriptors = mutableMapOf<Int, RowDescriptor>()
    var sheetDescriptor = SheetDescriptor()
    forEach { row ->
        sheetDescriptor = row.sheetDescriptor
        val rowDescriptor = row.rowDescriptor
        rowDescriptors[rowNumber + 1] = rowDescriptor
        (0 until row.columnCount).forEach { column ->
            if (!columnDescriptors.containsKey(column) && row.columnDescriptors.containsKey(column)) {
                columnDescriptors[column] = row.columnDescriptors.getValue(column)
            }
            val any = row[column]
            if (any != null) {
                val oldCoordinate = coordinate(column, row.rowOrdinal)
                val newCoordinate = coordinate(column, rowNumber)
                val expr = convertAnyToExpr(any).unfreeze(oldCoordinate).freeze(newCoordinate)
                cells[newCoordinate] = expr
            }
        }
        ++rowNumber
    }
    return Sheet.create(
        cells = cells.toCells(),
        rowDescriptors = rowDescriptors,
        columnDescriptors = columnDescriptors,
        sheetDescriptor = sheetDescriptor
    )
}