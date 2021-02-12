package com.github.jomof.kane

import com.github.jomof.kane.impl.*
import com.github.jomof.kane.impl.sheet.Sheet
import com.github.jomof.kane.impl.sheet.SheetBuilderImpl
import com.github.jomof.kane.impl.sheet.columnName
import kotlin.collections.set

/**
 * Convert this [Sheet] to a map where the key is column name and the value is
 * a list of cell values for that column.
 */
fun Sheet.toMap(): Map<String, List<Any?>> {
    with(eval()) {
        val result = mutableMapOf<String, List<Any?>>()
        for (column in 0 until columns) {
            val name = columnName(column)
            if (result.contains(name)) continue
            val list = mutableListOf<Any?>()
            for (row in 0 until rows) {
                val value = cells[coordinate(column, row)]
                if (value == null) list += null
                else {
                    when {
                        value is ValueExpr<*> -> list += value.value
                        value.canGetConstant() -> list += value.getConstant()
                        else ->
                            error("${value.javaClass}")
                    }
                }
            }
            result[name] = list
        }
        return result
    }
}

/**
 * Convert this [NamedExpr] to a map where the key is column name and the value is
 * a list of cell values for that column. Nested column vectors will be expanded.
 */
fun NamedExpr.toMap(): Map<String, List<Any?>> {
    val sb = SheetBuilderImpl()
    sb.add(this)
    return sb.build().toMap()
}