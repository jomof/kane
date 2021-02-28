package com.github.jomof.kane.impl.csv

import kotlinx.serialization.Serializable

@Serializable
data class CsvParseContext(
    val columnNames: List<String> = listOf(), // Otherwise, column names come from first line
    val linesForTypeAnalysis: Int = 100,
    val treatAsNaN: String? = null,
    val quoteChar: Char = '\"',
    val delimiter: Char = ',',
    val escapeChar: Char = '\\'
)