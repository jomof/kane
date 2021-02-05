package com.github.jomof.kane.impl.sheet

import com.github.jomof.kane.Kane
import com.github.jomof.kane.impl.*


/**
 * Return a new sheet with data summarized into statistics
 */
fun Expr.describe(): Sheet = when (this) {
    is Sheet -> describe()
    is MatrixExpr -> describe()
    is NamedMatrix -> matrix.describe(name)
    else -> error("$javaClass")
}

/**
 * Return a new sheet with data summarized into statistics
 */
fun NamedMatrix.describe() = matrix.describe(name)

/**
 * Return a new sheet with data summarized into statistics
 */
fun MatrixExpr.describe(name: Id = "matrix"): Sheet {
    val statistic = StreamingSamples()
    elements.forEach {
        if (!it.eval().canGetConstant()) error("Could not evaluate constant for all elements")
        statistic.insert(it.eval().getConstant())
    }

    val columnDescriptors = mapOf(
        0 to ColumnDescriptor(name = name, DoubleAdmissibleDataType())
    )
    val rowDescriptors = Kane.unaryStatisticsFunctions
        .mapIndexed { index, func -> index + 1 to RowDescriptor(listOf(constant(func.meta.op))) }.toMap()
    val cells = mutableMapOf<Id, Expr>()
    for (row in Kane.unaryStatisticsFunctions.indices) {
        val result = Kane.unaryStatisticsFunctions[row].lookupStatistic(statistic)
        cells[coordinate(0, row)] = constant(result)
    }
    return Sheet.of(
        cells = cells.toCells(),
        rowDescriptors = rowDescriptors,
        columnDescriptors = columnDescriptors,
        sheetDescriptor = SheetDescriptor()
    )
        .limitOutputLines(Kane.unaryStatisticsFunctions.size)
        .showExcelColumnTags(false)
}

/**
 * Return a new sheet with columns summarized into statistics
 */
fun Sheet.describe(): Sheet {
    val statistics = columnStatistics()
    val cells = mutableMapOf<Id, Expr>()
    val rowDescriptors = Kane.unaryStatisticsFunctions
        .mapIndexed { index, func -> index + 1 to RowDescriptor(listOf(constant(func.meta.op))) }.toMap()
    val relevantColumns = mutableListOf<Int>()
    for (column in 0 until columns) {
        val columnInfo = fullColumnDescriptor(column)
        if (columnInfo.type!!.type.java != Double::class.java) continue
        relevantColumns += column
        val statistic = statistics[column]
        for (row in Kane.unaryStatisticsFunctions.indices) {
            val result = Kane.unaryStatisticsFunctions[row].lookupStatistic(statistic)
            cells[coordinate(column, row)] = constant(result)
        }
    }
    return copy(cells = cells.toCells(), rowDescriptors = rowDescriptors)
        .ordinalColumns(relevantColumns)
        .limitOutputLines(Kane.unaryStatisticsFunctions.size)
        .showExcelColumnTags(false)
}

private fun Sheet.columnStatistics(): List<StreamingSamples> {
    val evaled = eval()
    val samples = (0 until evaled.columns).map { StreamingSamples() }
    cells.forEach { (name, expr) ->
        if (name is Coordinate) {
            if (expr.canGetConstant()) {
                samples[name.column].insert(expr.getConstant())
            }
        }
    }
    return samples
}

/**
 * Return a new sheet with columns summarized into statistics
 */
fun GroupBy.describe() = aggregate(*Kane.unaryStatisticsFunctions.toTypedArray())