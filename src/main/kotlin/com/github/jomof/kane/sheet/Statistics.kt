package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.types.AlgebraicType


/**
 * Return a new sheet with columns summarized into statistics
 */
fun Expr.describe(): Sheet = when (this) {
    is Sheet -> describe()
    else -> error("$javaClass")
}

/**
 * Return a new sheet with columns summarized into statistics
 */
fun Sheet.describe(): Sheet {
    val statistics = columnStatistics()
    val cells = mutableMapOf<String, Expr>()
    val rowDescriptors = Kane.unaryStatisticsFunctions
        .mapIndexed { index, func -> index + 1 to RowDescriptor(func.meta.op) }.toMap()
    val relevantColumns = mutableListOf<Int>()
    for (column in 0 until columns) {
        val columnInfo = fullColumnDescriptor(column)
        if (columnInfo.type!!.type.java != Double::class.java) continue
        relevantColumns += column
        val statistic = statistics[column]
        val type = columnInfo.type.type as AlgebraicType
        for (row in Kane.unaryStatisticsFunctions.indices) {
            val result = Kane.unaryStatisticsFunctions[row].lookupStatistic(statistic)
            cells[coordinateToCellName(column, row)] = constant(result, type)
        }
    }
    return copy(cells = cells, rowDescriptors = rowDescriptors)
        .ordinalColumns(relevantColumns)
        .limitOutputLines(Kane.unaryStatisticsFunctions.size)
}

private fun Sheet.columnStatistics(): List<StreamingSamples> {
    val evaled = evalGradual()
    val samples = (0 until evaled.columns).map { StreamingSamples() }
    cells.forEach { (name, expr) ->
        val constant = expr.tryFindConstant()
        if (constant != null && looksLikeCellName(name)) {
            val coordinate = cellNameToCoordinate(name)
            samples[coordinate.column].insert(constant)
        }
    }
    return samples
}