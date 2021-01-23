package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.functions.*
import com.github.jomof.kane.types.AlgebraicType


/**
 * Return a new sheet with columns summarized into statistics
 */
val Expr.statistics
    get() = when (this) {
        is Sheet -> statistics
        else -> error("$javaClass")
    }

/**
 * Return a new sheet with columns summarized into statistics
 */
val Sheet.statistics
    get() : Sheet {
        val statistics = columnStatistics()
        val cells = mutableMapOf<String, Expr>()
        val rowDescriptors = Kane.unaryStatisticsFunctions
            .mapIndexed { index, func -> index + 1 to RowDescriptor(func.meta.op) }.toMap()
        for (column in 0 until columns) {
            val columnInfo = fullColumnDescriptor(column)
            if (columnInfo.type!!.type.java != Double::class.java) continue
            val statistic = statistics[column]
            val type = columnInfo.type.type as AlgebraicType
            for (row in Kane.unaryStatisticsFunctions.indices) {
                val result = when (val func = Kane.unaryStatisticsFunctions[row]) {
                    count -> statistic.count.toDouble()
                    nans -> statistic.nans.toDouble()
                    mean -> statistic.mean
                    min -> statistic.min
                    median -> statistic.median
                    max -> statistic.max
                    variance -> statistic.variance
                    stddev -> statistic.stddev
                    skewness -> statistic.skewness
                    kurtosis -> statistic.kurtosis
                    summation -> statistic.sum
                    cv -> statistic.coefficientOfVariation
                    else -> error(func.meta.op)
                }
                cells[coordinateToCellName(column, row)] = constant(result, type)
            }
        }
        return copy(cells = cells, rowDescriptors = rowDescriptors)
            .limitOutputLines(Kane.unaryStatisticsFunctions.size)
    }

private fun Sheet.columnStatistics(): List<StreamingSamples> {
    val evaled = eval()
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