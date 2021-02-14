package com.github.jomof.kane.impl.sheet

import com.github.jomof.kane.Expr
import com.github.jomof.kane.eval
import com.github.jomof.kane.get
import com.github.jomof.kane.NamedExpr
import com.github.jomof.kane.impl.coordinate
import com.github.jomof.kane.impl.toNamed
import com.github.jomof.kane.sheetOf

internal class SheetRangeExprProvider(val sheet: Sheet) : RangeExprProvider {
    override fun range(range: SheetRangeExpr): Expr = sheet["$range"]
}

private fun GroupBy.buildAggregation(builder: SheetBuilderImpl): Sheet {
    val immediate = builder.getImmediateNamedExprs()
    var column = 0
    val groupBy = this
    return sheetOf {
        var row = 1
        for ((key, _) in groupBy) {
            nameRow(row++, key)
        }
        val result = mutableListOf<NamedExpr>()
        for ((name, expr) in immediate.cells) {
            nameColumn(column, name)
            row = 0
            for ((_, sheet) in groupBy) {
                result += expr.eval(rangeExprProvider = SheetRangeExprProvider(sheet)).toNamed(coordinate(column, row))
                row++
            }
            column++
        }
        result
    }.showExcelColumnTags(false)
}

internal fun GroupBy.aggregate(selector: SheetBuilder.() -> List<NamedExpr>): Sheet {
    val builder = SheetBuilderImpl()
    selector(builder as SheetBuilder).forEach { builder.add(it) }
    return buildAggregation(builder)
}
