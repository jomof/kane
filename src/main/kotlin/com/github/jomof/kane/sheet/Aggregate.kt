package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.functions.AlgebraicUnaryScalarStatisticFunction

private class SheetRangeExprProvider(val sheet: Sheet) : RangeExprProvider {
    override fun range(range: SheetRangeExpr): Expr = sheet["$range"]
}

private fun GroupBy.buildAggregation(builder: SheetBuilderImpl): Sheet {
    val immediate = builder.getImmediateNamedExprs()
    val debuilderized = removeBuilderPrivateExpressions(immediate)
    var column = 0
    val groupBy = this
    return sheetOf {
        var row = 1
        for ((key, _) in groupBy) {
            nameRow(row++, key)
        }
        val result = mutableListOf<NamedExpr>()
        for ((name, expr) in debuilderized) {
            nameColumn(column, name)
            row = 0
            for ((_, sheet) in groupBy) {
                val cell = coordinateToCellName(Coordinate(column, row))
                result += expr.eval(rangeExprProvider = SheetRangeExprProvider(sheet)).toNamed(cell)
                row++
            }
            column++
        }
        result
    }
}

fun GroupBy.aggregate(selector: SheetBuilder.() -> List<NamedExpr>): Sheet {
    val builder = SheetBuilderImpl()
    selector(builder as SheetBuilder).forEach { builder.add(it) }
    return buildAggregation(builder)
}

fun GroupBy.aggregate(vararg functions: AlgebraicUnaryScalarStatisticFunction): Sheet {
    return aggregate {
        val result = mutableListOf<NamedExpr>()
        for (column in 0 until sheet.columns) {
            val columnInfo = sheet.fullColumnDescriptor(column)
            if (columnInfo.type!!.type.java != Double::class.java) continue
            for (function in functions) {
                result += function(range(columnInfo.name)).toNamed(function.meta.op + " " + columnInfo.name)
            }
        }
        result
    }
}