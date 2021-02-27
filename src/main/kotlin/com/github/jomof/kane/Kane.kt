package com.github.jomof.kane

import com.github.jomof.kane.impl.functions.AlgebraicSummaryFunction
import com.github.jomof.kane.impl.sheet.Sheet
import com.github.jomof.kane.impl.sheet.possibleDataFormats
import com.github.jomof.kane.impl.sheet.showExcelColumnTags
import com.github.jomof.kane.impl.sheet.showRowAndColumnForSingleCell
import java.io.File

/**
 * Global metadata about the Kane system.
 */
class Kane {
    companion object {
        /**
         * Folder used for caching intermediates. May be safely deleted.
         */
        val userCacheFolder: File
            get() {
                val result = File(System.getProperty("user.home")).resolve(".kane")
                result.mkdirs()
                return result
            }

        /**
         * List of data formats supported by Kane.
         */
        val dataFormats: Sheet = sheetOf {
            nameColumn(0, "format")
            nameColumn(1, "type")
            val a1 by possibleDataFormats.map { it.toString() }
            val b1 by possibleDataFormats.map { it.type.simpleName }
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
        ).map { it as AlgebraicSummaryFunction }

        /**
         * List of functions that take one parameter.
         */
        val unaryFunctions = listOf(
            sin,
            cos,
            lrelu,
            lstep,
            logit,
            exp,
            relu,
            tanh,
            step,
            negate
        ).map { it }

        /**
         * Internal metrics.
         */
        val metrics = KaneMetrics()
    }
}

class KaneMetrics(
    /**
     * Number of times Sequence<Row> was fully instantiated into a sheet.
     */
    var sheetInstatiations: Int = 0
)