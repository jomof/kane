package com.github.jomof.kane

import com.github.jomof.kane.impl.*
import com.github.jomof.kane.impl.sheet.*

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


/**
 * Return a  sheet with data summarized into statistics
 */
fun Expr.describe(): Sheet = when (this) {
    is Sheet -> describe()
    is MatrixExpr -> describe()
    else -> error("$javaClass")
}

/**
 * Return a  sheet with data summarized into statistics
 */
fun MatrixExpr.describe(name: Id = "matrix"): Sheet = when (this) {
    is NamedMatrix -> describe()
    is DataMatrix -> describe(name)
    else -> error("$javaClass")
}

/**
 * Return a new sheet with data summarized into statistics
 */
fun NamedMatrix.describe() = matrix.describe(name)

/**
 * Return a new sheet with data summarized into statistics
 */
fun DataMatrix.describe(name: Id = "matrix"): Sheet {
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
fun GroupBy.describe() = aggregate(*Kane.unaryStatisticsFunctions.toTypedArray())