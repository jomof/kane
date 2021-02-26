package com.github.jomof.kane.impl.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.impl.*

/**
 * Conceptually, a GroupBy is a dictionary from key to sheet.
 * It initially consists of [sheet] which is the data source and [selector] which is used
 * to build a key from a row.
 */
data class GroupBy(
    val source: Sequence<Row>,
    val keySelector: List<Expr>
) : Expr {
    private val sheet by lazy { source.toSheet() }
    private val groups by lazy { createGroups() }

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

    private fun descriptiveSheet(): Sheet {
        return sheetOf {
            nameColumn(0, "key selector")
            keySelector.forEachIndexed { index, expr ->
                nameRow(index + 1, expr.name)
            }
            val a1 by keySelector.map { "${it.toUnnamed()}" }
            listOf(a1)
        }
            .showExcelColumnTags(false)
            .showRowAndColumnForSingleCell(true)
    }

    val html: String get() = descriptiveSheet().html
    override fun toString() = descriptiveSheet().toString()

    operator fun get(vararg keys: Any): Sheet {
        val keyExprs = keys.toList().map { convertAnyToExpr(it) }
        return groups.getValue(keyExprs)
    }
}

private fun Sequence<Row>.buildGrouping(builder: SheetBuilderImpl): GroupBy {
    val immediate = builder.getImmediateNamedExprs()
    val names = immediate.cells.map { (name, expr) -> expr.toNamed(Identifier.string(name)) }
    return GroupBy(this, names)
}

internal fun Sequence<Row>.groupOf(selector: SheetBuilder.() -> List<Expr>): GroupBy {
    val builder = SheetBuilderImpl()
    selector(builder as SheetBuilder).forEach {
        builder.add(it)
    }
    return buildGrouping(builder)
}

