package com.github.jomof.kane.api

import com.github.jomof.kane.impl.sheet.ColumnDescriptor

interface Row {
    val columnCount: Int
    val columnDescriptors: Map<Int, ColumnDescriptor>
    val rowOrdinal: Int // Zero relative
    val rowDescriptor: RowDescriptor?
    val sheetDescriptor: SheetDescriptor
    operator fun get(column: Int): Any?
    operator fun get(column: String): Any?
}