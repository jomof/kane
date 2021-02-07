package com.github.jomof.kane

import com.github.jomof.kane.impl.columnOf
import com.github.jomof.kane.impl.functions.AlgebraicUnaryScalarStatisticFunction
import com.github.jomof.kane.impl.sheet.Sheet
import com.github.jomof.kane.impl.sheet.possibleDataFormats
import com.github.jomof.kane.impl.sheet.showExcelColumnTags
import com.github.jomof.kane.impl.sheet.showRowAndColumnForSingleCell

/**
 * Global metadata about the Kane system.
 */
class Kane {
    companion object {
        /**
         * List of data formats supported by Kane.
         */
        val dataFormats: Sheet = sheetOf {
            nameColumn(0, "format")
            nameColumn(1, "type")
            val a1 by columnOf(possibleDataFormats.map { it.toString() })
            val b1 by columnOf(possibleDataFormats.map { it.type.simpleName })
            listOf(a1, b1)
        }.showExcelColumnTags(false).showRowAndColumnForSingleCell(true)

        /**
         * List of statistics functions that take one parameter.
         */
        val unaryStatisticsFunctions = listOf(
            count,
            nans,
            mean,
            min,
            percentile25,
            median,
            percentile75,
            max,
            variance,
            stdev,
            skewness,
            kurtosis,
            cv,
            sum
        ).map { it as AlgebraicUnaryScalarStatisticFunction }

        /**
         * List of functions that take one parameter.
         */
        val unaryFunctions = listOf(
            lrelu,
            lstep,
            logit,
            exp,
            relu,
            tanh,
            step,
            negate
        ).map { it }
    }
}