package com.github.jomof.kane.sheet

import com.github.jomof.kane.CellRange
import com.github.jomof.kane.indexToColumnName

/**
 * Render the sheet as HTML
 */
val Sheet.html: String
    get() {
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

        fun colName(column: Int) = columnDescriptors[column]?.name ?: indexToColumnName(column)
        fun rowName(row: Int) = rowDescriptors[row]?.name ?: "$row"
        sb.append("\n<table id=\"table_id\" class=\"display\">\n")

        // Column headers
        sb.append("<thead><tr>\n")
        sb.append("  <th/>")
        (0 until columns).forEach { column ->
            val columnName = colName(column)
            sb.append("<th>$columnName</th>")
        }
        sb.append("</thead></tr>\n")

        // Data
        sb.append("  <tbody>\n")
        for (row in 0 until rows) {
            sb.append("    <tr>")
            sb.append("<td>${rowName(row + 1)}</td>")
            for (column in 0 until columns) {
                val cell = CellRange.moveable(column, row).toString()
                val value = cells[cell]?.toString() ?: ""
                sb.append("<td>$value</td>")
            }
            sb.append("</tr>\n")
        }
        sb.append("  </tbody>\n")
        sb.append("</table>\n")
        return sb.toString()
    }