package com.github.jomof.kane

import com.github.jomof.kane.impl.Identifier
import com.github.jomof.kane.impl.sheet.Sheet
import com.github.jomof.kane.impl.sheet.fullColumnDescriptor
import com.github.jomof.kane.impl.sheet.showExcelColumnTags
import com.github.jomof.kane.impl.sheet.showRowAndColumnForSingleCell
import com.github.jomof.kane.impl.toSheet


/**
 * Return the column types of this sheet.
 */
val Sequence<Row>.types: Sheet
    get() {
        val sheet = toSheet()
        return sheetOf {
            nameColumn(0, "name")
            nameColumn(1, "type")
            nameColumn(2, "format")
            val descriptors = (0 until sheet.columns).map { sheet.fullColumnDescriptor(it) }
            val a1 by descriptors.map { Identifier.string(it.name) }
            val b1 by descriptors.map { it.type!!.type.simpleName }
            val c1 by descriptors.map { it.type.toString() }
        listOf(a1, b1, c1)
    }
        .showExcelColumnTags(false)
        .showRowAndColumnForSingleCell(true)
}