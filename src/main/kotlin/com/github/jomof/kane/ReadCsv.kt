package com.github.jomof.kane

import com.github.jomof.kane.impl.ReadCsvRowSequence
import com.github.jomof.kane.impl.csv.CsvParseContext
import java.io.File


/**
 * Read CSV from a file.
 */
fun readCsv(
    csv: String,
    delimiter: Char = ',',
    names: List<String> = listOf()
) =
    ReadCsvRowSequence(
        File(csv),
        CsvParseContext(delimiter = delimiter, columnNames = names)
    )

/**
 * Read CSV from a file.
 */
fun readCsv(csv: File, parseContext: CsvParseContext = CsvParseContext()) =
    ReadCsvRowSequence(csv, parseContext)

/**
 * Read CSV from a file.
 */
fun readCsv(csv: String, parseContext: CsvParseContext = CsvParseContext()) =
    ReadCsvRowSequence(File(csv), parseContext)
