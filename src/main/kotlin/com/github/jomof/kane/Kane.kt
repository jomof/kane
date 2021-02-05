package com.github.jomof.kane

import com.github.jomof.kane.functions.*
import com.github.jomof.kane.impl.columnOf
import com.github.jomof.kane.impl.sheet.Sheet
import com.github.jomof.kane.impl.sheet.possibleDataFormats

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
        }.showExcelColumnTags(false)

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
            stddev,
            skewness,
            kurtosis,
            cv,
            sum
        )
    }
}