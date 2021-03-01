package com.github.jomof.kane

import com.github.jomof.kane.impl.ParseCsvRowSequence
import com.github.jomof.kane.impl.csv.CsvParseContext
import com.github.jomof.kane.impl.csv.computeCsvMetadata
import com.github.jomof.kane.impl.csv.parseCsvText
import java.io.StringReader

/**
 * Parse csv from text
 */
fun parseCsv(
    text: String,
    parseContext: CsvParseContext = CsvParseContext()
): Sequence<Row> = ParseCsvRowSequence(
    data = parseCsvText(text, parseContext),
    meta = computeCsvMetadata(StringReader(text), parseContext)
)