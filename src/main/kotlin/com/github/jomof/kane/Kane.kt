package com.github.jomof.kane

import com.github.jomof.kane.functions.*
import com.github.jomof.kane.sheet.Sheet
import com.github.jomof.kane.sheet.possibleDataFormats
import com.github.jomof.kane.sheet.sheetOf

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
        }

        /**
         * List of statistics functions that take one parameter.
         */
        val unaryStatisticsFunctions = listOf(
            count,
            nans,
            mean,
            min,
            median,
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