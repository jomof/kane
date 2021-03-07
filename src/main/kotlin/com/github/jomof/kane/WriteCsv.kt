package com.github.jomof.kane

import com.github.jomof.kane.api.Row
import com.github.jomof.kane.impl.Identifier
import com.github.jomof.kane.impl.coordinate
import com.github.jomof.kane.impl.indexToColumnName
import com.github.jomof.kane.impl.sheet.Sheet
import com.github.jomof.kane.impl.toSheet
import java.io.File

/**
 * Write Sheet to a file named [csv]
 */
fun Sequence<Row>.writeCsv(csv: String) {
    writeCsv(File(csv))
}

/**
 * Write Sheet to a file named [csv]
 */
fun Sequence<Row>.writeCsv(csv: File) {
    val sheet = toSheet()
    csv.writeText("")

    fun colName(column: Int) = sheet.columnDescriptors[column]?.name ?: indexToColumnName(column)

    // Column headers
    (0..sheet.columns).forEach { column ->
        val columnName = colName(column)
        csv.appendText(Identifier.string(columnName))
        if (column != sheet.columns - 1) csv.appendText(",")

    }
    csv.appendText("\n")

    // Data
    val sb = StringBuilder()
    for (row in 0 until rows) {
        for (column in 0 until sheet.columns) {
            val cell = coordinate(column, row)
            val value = sheet.cells[cell]?.toString() ?: ""
            if (value.contains(" ")) {
                sb.append("\"$value\"")
            } else {
                sb.append(value)
            }
            if (column != sheet.columns - 1) sb.append(",")
        }
        sb.append("\n")
        if (row % 100 == 0) {
            // Write in batches
            csv.appendText(sb.toString())
            sb.clear()
        }
    }
    csv.appendText(sb.toString())
}