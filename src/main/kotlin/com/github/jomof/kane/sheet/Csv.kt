package com.github.jomof.kane.sheet

import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import com.github.doyaaaaaken.kotlincsv.dsl.context.CsvReaderContext
import com.github.jomof.kane.Coordinate
import java.io.File

fun readCsv(csv : String,
            names : List<String> = listOf(),
            charset : String = "UTF-8",
            quoteChar: Char = '"',
            delimiter: Char = ',',
            escapeChar: Char = '"',
            skipEmptyLine: Boolean = false,
            skipMissMatchedRow: Boolean = false) : Sheet = readCsv(File(csv), names, csvReader(
                charset,
                quoteChar,
                delimiter,
                escapeChar,
                skipEmptyLine,
                skipMissMatchedRow))

fun readCsv(csv : File,
            names : List<String> = listOf(),
            charset : String = "UTF-8",
            quoteChar: Char = '"',
            delimiter: Char = ',',
            escapeChar: Char = '"',
            skipEmptyLine: Boolean = false,
            skipMissMatchedRow: Boolean = false) = readCsv(csv, names, csvReader(
                charset,
                quoteChar,
                delimiter,
                escapeChar,
                skipEmptyLine,
                skipMissMatchedRow
            ))

private fun readCsvWithHeader(
    csv : File,
    csvReader: CsvReader) : Sheet {
    val sb = SheetBuilder()
    val rows: List<Map<String, String>> = csvReader.readAllWithHeader(csv)
    val info = analyzeDataTypes(rows)
    for(column in info.columnInfos.indices) {
        val columnInfo = info.columnInfos[column]
        sb.column(column, columnInfo.name, columnInfo.typeInfo)
    }
    for(row in 0 until info.rows) {
        val map = rows[row]
        for(column in info.columnInfos.indices) {
            val columnInfo = info.columnInfos[column]
            val coordinate = Coordinate(column, row)
            val value = columnInfo.typeInfo.tryParse(map[columnInfo.name] ?: "")
            if (value != null) {
                sb.set(coordinate, value, columnInfo.typeInfo.type)
            }
        }
    }
    return sb.build()
}

private fun readCsvWithoutHeader(
    csv : File,
    names : List<String> = listOf(),
    csvReader: CsvReader) : Sheet {
    val sb = SheetBuilder()
    val rows: List<List<String>> = csvReader.readAll(csv)
    val info = analyzeDataTypes(names, rows)
    for(column in info.columnInfos.indices) {
        val columnInfo = info.columnInfos[column]
        sb.column(column, columnInfo.name, columnInfo.typeInfo)
    }
    for(row in 0 until info.rows) {
        val list = rows[row]
        for(column in info.columnInfos.indices) {
            val columnInfo = info.columnInfos[column]
            val coordinate = Coordinate(column, row)
            val value = columnInfo.typeInfo.tryParse(list[column])
            if (value != null) {
                sb.set(coordinate, value, columnInfo.typeInfo.type)
            }
        }
    }
    return sb.build()
}

private fun readCsv(
    csv : File,
    names : List<String> = listOf(),
    csvReader: CsvReader) : Sheet {
    val sheet =
        if (names.isEmpty()) readCsvWithHeader(csv, csvReader)
        else readCsvWithoutHeader(csv, names, csvReader)
    return sheet.limitOutputLines(10)
}

private fun csvReader(
    charset : String,
    quoteChar: Char,
    delimiter: Char,
    escapeChar: Char,
    skipEmptyLine: Boolean,
    skipMissMatchedRow: Boolean
) : CsvReader {
    val context = CsvReaderContext()
    context.charset = charset
    context.quoteChar = quoteChar
    context.delimiter = delimiter
    context.escapeChar = escapeChar
    context.skipEmptyLine = skipEmptyLine
    context.skipMissMatchedRow = skipMissMatchedRow
    return CsvReader(context)
}