package com.github.jomof.kane.impl.csv

import kotlinx.serialization.Serializable

@Serializable
data class CsvParseContext(
    val headerHasColumnNames: Boolean = true,
    val columnNames: List<String> = listOf(),
    val linesForTypeAnalysis: Int = 100,
    val treatAsNaN: String? = null,
    val quoteChar: Char = '\"',
    val delimiter: Char = ',',
    val escapeChar: Char = '\\'
)