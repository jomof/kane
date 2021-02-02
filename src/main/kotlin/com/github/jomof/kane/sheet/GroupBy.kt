package com.github.jomof.kane.sheet

import com.github.jomof.kane.*

/**
 * Conceptually, a GroupBy is a dictionary from key to sheet.
 * It initially consists of [sheet] which is the data source and [selector] which is used
 * to build a key from a row.
 */
class GroupBy(
    val sheet: Sheet,
    private val keySelector: List<NamedExpr>
) : Expr {
    val groups by lazy { createGroups() }

    private fun createGroups(): Map<List<Expr>, Sheet> {
        val map = mutableMapOf<List<Expr>, MutableList<Int>>()
        (1..sheet.rows).map { row ->
            val view = RowView(sheet, row - 1)
            val key = keySelector.map { it.toUnnamed().eval(rangeExprProvider = view) }
            val ordinals = map.computeIfAbsent(key) { mutableListOf() }
            ordinals.add(row)
        }
        return map.map { it.key to sheet.ordinalRows(it.value) }.toMap()
    }

    operator fun iterator() = groups.iterator()

    private fun toKeySelectorSheet(): Sheet {
        return sheetOf {
            nameColumn(0, "key selector")
            keySelector.forEachIndexed { index, expr ->
                nameRow(index + 1, expr.name)
            }
            val a1 by columnOf(keySelector.map { "${it.toUnnamed()}" })
            listOf(a1)
        }
    }

    val html: String get() = toKeySelectorSheet().html
    override fun toString() = toKeySelectorSheet().toString()

    operator fun get(vararg keys: Any): Sheet {
        val keyExprs = keys.toList().map { convertAnyToExpr(it) }
        return groups.getValue(keyExprs)
    }
}


private fun Sheet.buildGrouping(builder: SheetBuilderImpl): GroupBy {
    val immediate = builder.getImmediateNamedExprs()
    val debuilderized = removeBuilderPrivateExpressions(immediate)
    val names = debuilderized.map { (name, expr) -> expr.toNamed(name) }
    return GroupBy(this, names)
}

fun Sheet.groupOf(selector: SheetBuilder.() -> List<NamedExpr>): GroupBy {
    val builder = SheetBuilderImpl()
    selector(builder as SheetBuilder).forEach { builder.add(it) }
    return buildGrouping(builder)
}

fun Sheet.groupBy(vararg columns: String) = groupOf { columns.map { column(it).toNamed(it) } }