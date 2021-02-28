package com.github.jomof.kane

import com.github.jomof.kane.impl.sheet.parseCsv

/**
 * Construct a sheet by parsing provided text.
 */
fun sheetOfCsv(
    text: String,
    names: List<String> = listOf(),
    quoteChar: Char = '"',
    delimiter: Char = ',',
    escapeChar: Char = '"',
    skipEmptyLine: Boolean = false,
    skipMissMatchedRow: Boolean = false,
): Sequence<Row> {
    return parseCsv(
        data = text.trimIndent().trim('\r', '\n'),
        names = names,
        quoteChar = quoteChar,
        delimiter = delimiter,
        escapeChar = escapeChar,
        skipEmptyLine = skipEmptyLine,
        skipMissMatchedRow = skipMissMatchedRow
    )
}