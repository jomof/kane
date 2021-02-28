package com.github.jomof.kane.impl.csv

import com.github.jomof.kane.impl.ReadCsvRowSequence
import java.io.File

fun readCsvRowSequence(file: File, parseContext: CsvParseContext = CsvParseContext()) =
    ReadCsvRowSequence(file, parseContext)