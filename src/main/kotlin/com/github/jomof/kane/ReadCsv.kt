package com.github.jomof.kane

import com.github.jomof.kane.impl.sheet.CsvParameters
import com.github.jomof.kane.impl.sheet.csvReaderContext
import com.github.jomof.kane.impl.sheet.readCsv
import java.io.File

/**
 * Read CSV from a file.
 */
fun readCsv(
    csv: String,
    names: List<String> = listOf(),
    charset: String = "UTF-8",
    quoteChar: Char = '"',
    delimiter: Char = ',',
    escapeChar: Char = '"',
    skipEmptyLine: Boolean = false,
    skipMissMatchedRow: Boolean = false
): Sequence<Row> = readCsv(
    File(csv).inputStream(),
    CsvParameters(
        names = names
    ),
    csvReaderContext(
        charset,
        quoteChar,
        delimiter,
        escapeChar,
        skipEmptyLine,
        skipMissMatchedRow
    )
)


/**
 * Read CSV from a file.
 */
fun readCsv(
    csv: File,
    names: List<String> = listOf(),
    charset: String = "UTF-8",
    quoteChar: Char = '"',
    delimiter: Char = ',',
    escapeChar: Char = '"',
    skipEmptyLine: Boolean = false,
    skipMissMatchedRow: Boolean = false
) = readCsv(
    csv.inputStream(),
    CsvParameters(
        names = names
    ),
    csvReaderContext(
        charset,
        quoteChar,
        delimiter,
        escapeChar,
        skipEmptyLine,
        skipMissMatchedRow
    )
)
