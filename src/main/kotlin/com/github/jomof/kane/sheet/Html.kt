package com.github.jomof.kane.sheet

import com.github.jomof.kane.ComputableCoordinate
import com.github.jomof.kane.coordinateToCellName
import com.github.jomof.kane.indexToColumnName

/**
 * Render the sheet as HTML
 */
val Sheet.html: String
    get() {
        val sb = StringBuilder()
        sb.append(
            """
        <link rel=\"stylesheet\" type=\"text/css\" href=\"https://cdn.datatables.net/1.10.23/css/jquery.dataTables.css\">
          
        <script type=\"text/javascript\" charset=\"utf8\" src=\"https://cdn.datatables.net/1.10.23/js/jquery.dataTables.js\">
        ${'$'}(document).ready( function () {
            ${'$'}('#table_id').DataTable();
        } );
        </script>
    """.trimIndent()
        )

        fun colName(column: Int) = columnDescriptors[column]?.name ?: indexToColumnName(column)
        sb.append("<table id=\"table_id\" class=\"display\">")

        // Column headers
        sb.append("<thead><tr>")
        (0 until columns).forEach { column ->
            val columnName = colName(column)
            sb.append("<th>$columnName</th>")
        }
        sb.append("</thead></tr>")


        // Data
        sb.append("<tbody>")
        for (row in 0 until rows) {
            sb.append("<tr>")
            for (column in 0 until columns) {
                val cell = coordinateToCellName(ComputableCoordinate.fixed(column, row))
                val value = cells[cell]?.toString() ?: ""
                sb.append("<td>$value</td>")
            }
            sb.append("</tr>")
        }
        sb.append("</table>")
        return sb.toString()
    }