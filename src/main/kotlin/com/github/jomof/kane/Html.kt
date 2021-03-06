package com.github.jomof.kane

import com.github.jomof.kane.api.Row
import com.github.jomof.kane.impl.Coordinate
import com.github.jomof.kane.impl.Identifier
import com.github.jomof.kane.impl.coordinate
import com.github.jomof.kane.impl.sheet.Sheet
import com.github.jomof.kane.impl.sheet.SheetRangeExpr
import com.github.jomof.kane.impl.sheet.columnName
import com.github.jomof.kane.impl.sheet.rowName
import com.github.jomof.kane.impl.toSheet


/**
 * Render the sheet as HTML
 */
val Sheet.html: String
    get() {
        if (cells.isEmpty()) return ""
        if (cells.count() == 1 && !sheetDescriptor.showRowAndColumnForSingleCell)
            return cells.firstValue().toString()

        val sb = StringBuilder()
//        sb.append(
//            """
//        <link rel=\"stylesheet\" type=\"text/css\" href=\"https://cdn.datatables.net/1.10.23/css/jquery.dataTables.css\">
//
//        <script type=\"text/javascript\" charset=\"utf8\" src=\"https://cdn.datatables.net/1.10.23/js/jquery.dataTables.js\">
//        ${'$'}(document).ready( function () {
//            ${'$'}('#table_id').DataTable();
//        } );
//        </script>
//    """.trimIndent()
//        )

        sb.append("\n<table id=\"table_id\" class=\"display\">\n")

        // Column headers
        sb.append("<thead><tr>\n")
        sb.append("  <th/>")
        (0 until columns).forEach { column ->
            val columnName = columnName(column)
            sb.append("<th>$columnName</th>")
        }
        sb.append("</thead></tr>\n")

        // Data
        sb.append("  <tbody>\n")
        for (row in 0 until rows) {
            sb.append("    <tr>")
            sb.append("<td>${rowName(row + 1)}</td>")
            for (column in 0 until columns) {
                val cell = coordinate(column, row)
                val value = cells[cell]?.toString() ?: ""
                sb.append("<td>$value</td>")
            }
            sb.append("</tr>\n")
        }
        sb.append("  </tbody>\n")
        sb.append("</table>\n")

        // Non-cell data
        val nonCells = cells.filter { it.key !is Coordinate }
        if (nonCells.isNotEmpty()) {
            nonCells.toMap().toList().sortedBy { Identifier.string(it.first) }.forEach {
                if (it.second !is SheetRangeExpr) {
                    sb.append("\n${it.first}=${it.second}<br/>")
                }
            }
        }
        return sb.toString()
    }

/**
 * Render the sheet as HTML
 */
val Sequence<Row>.html: String get() = toSheet().html