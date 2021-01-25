package com.github.jomof.kane.sheet

import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import com.github.doyaaaaaken.kotlincsv.dsl.context.CsvReaderContext
import com.github.jomof.kane.ComputableCoordinate
import com.github.jomof.kane.Coordinate
import com.github.jomof.kane.indexToColumnName
import java.io.File
import kotlin.random.Random

fun readCsv(
    csv: String,
    names: List<String> = listOf(),
    sample: Double = 1.0,
    keep: List<String> = listOf(),
    charset: String = "UTF-8",
    quoteChar: Char = '"',
    delimiter: Char = ',',
    escapeChar: Char = '"',
    skipEmptyLine: Boolean = false,
    skipMissMatchedRow: Boolean = false
): Sheet = readCsv(
    File(csv),
    CsvParameters(
        names = names,
        sample = sample,
        keep = keep.toSet()
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

fun readCsv(
    csv: File,
    names: List<String> = listOf(),
    sample: Double = 1.0,
    keep: List<String> = listOf(),
    charset: String = "UTF-8",
    quoteChar: Char = '"',
    delimiter: Char = ',',
    escapeChar: Char = '"',
    skipEmptyLine: Boolean = false,
    skipMissMatchedRow: Boolean = false
) = readCsv(
    csv,
    CsvParameters(
        names = names,
        sample = sample,
        keep = keep.toSet()
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

private fun readCsvWithHeader(
    csv: File,
    params: CsvParameters,
    context: CsvReaderContext
): Sheet {
    val sb = SheetBuilder()
    val random = Random(7)
    val rows: MutableList<Map<String, String>> = mutableListOf()
    var totalRows = 0
    CsvReader(context).open(csv) {
        for (row in readAllWithHeaderAsSequence()) {
            ++totalRows
            if (params.sample >= 1.0 || random.nextDouble(0.0, 1.0) < params.sample) {
                rows += row
                    .filter { params.keep.isEmpty() || params.keep.contains(it.key) }
                    .map { it.key.intern() to it.value.intern() }
                    .toMap()
            }
        }
    }

    val info = analyzeDataTypes(rows)
    for (column in info.columnInfos.indices) {
        val columnInfo = info.columnInfos[column]
        sb.column(column, columnInfo.name, columnInfo.typeInfo)
    }
    for (row in 0 until info.rows) {
        val map = rows[row]
        for (column in info.columnInfos.indices) {
            val columnInfo = info.columnInfos[column]
            val coordinate = Coordinate(column, row)
            val value = columnInfo.typeInfo.tryParse(map[columnInfo.name] ?: "")
            if (value != null) {
                sb.set(coordinate, value, columnInfo.typeInfo.type)
            }
        }
    }

    if (params.sample < 1.0) {
        println("Sampled ${rows.size} of $totalRows rows with ${info.columnInfos.size} columns")
    } else {
        println("Read ${rows.size} rows with ${info.columnInfos.size} columns")
    }
    return sb.build()
}

private fun readCsvWithoutHeader(
    csv: File,
    params: CsvParameters,
    context: CsvReaderContext
): Sheet {
    val sb = SheetBuilder()
    val rows: MutableList<List<String>> = mutableListOf()
    val random = Random(7)
    if (params.keep.isNotEmpty()) {
        error("keep='${params.keep.joinToString(",")}' not allowed for CSV without headers")
    }
    var totalRows = 0
    CsvReader(context).open(csv) {
        ++totalRows
        for (row in readAllAsSequence()) {
            if (params.sample >= 1.0 || random.nextDouble(0.0, 1.0) < params.sample) {
                rows += row.map { it.intern() }
            }
        }
    }

    val info = analyzeDataTypes(params.names, rows)
    for (column in info.columnInfos.indices) {
        val columnInfo = info.columnInfos[column]
        sb.column(column, columnInfo.name, columnInfo.typeInfo)
    }
    for (row in 0 until info.rows) {
        val list = rows[row]
        for (column in info.columnInfos.indices) {
            val columnInfo = info.columnInfos[column]
            val coordinate = Coordinate(column, row)
            val value = columnInfo.typeInfo.tryParse(list[column])
            if (value != null) {
                sb.set(coordinate, value, columnInfo.typeInfo.type)
            }
        }
    }

    if (params.sample < 1.0) {
        println("Sampled ${rows.size} of $totalRows rows with ${info.columnInfos.size} columns")
    } else {
        println("Read ${rows.size} rows with ${info.columnInfos.size} columns")
    }

    return sb.build()
}

private fun readCsv(
    csv: File,
    params: CsvParameters,
    context: CsvReaderContext
): Sheet {
    val sheet =
        if (params.names.isEmpty()) readCsvWithHeader(csv, params, context)
        else readCsvWithoutHeader(csv, params, context)
    return sheet.limitOutputLines(10)
}

private data class CsvParameters(
    val names: List<String>,
    val keep: Set<String>,
    val sample: Double
)

private fun csvReaderContext(
    charset: String,
    quoteChar: Char,
    delimiter: Char,
    escapeChar: Char,
    skipEmptyLine: Boolean,
    skipMissMatchedRow: Boolean
): CsvReaderContext {
    val context = CsvReaderContext()
    context.charset = charset
    context.quoteChar = quoteChar
    context.delimiter = delimiter
    context.escapeChar = escapeChar
    context.skipEmptyLine = skipEmptyLine
    context.skipMissMatchedRow = skipMissMatchedRow
    return context
}

fun Sheet.writeCsv(csv: String) {
    writeCsv(File(csv))
}

fun Sheet.writeCsv(csv: File) {
    csv.writeText("")

    fun colName(column: Int) = columnDescriptors[column]?.name ?: indexToColumnName(column)

    // Column headers
    (0..columns).forEach { column ->
        val columnName = colName(column)
        csv.appendText(columnName)
        if (column != columns - 1) csv.appendText(",")

    }
    csv.appendText("\n")

    // Data
    val sb = StringBuilder()
    for (row in 0 until rows) {
        for (column in 0 until columns) {
            val cell = ComputableCoordinate.moveable(column, row).toString()
            val value = cells[cell]?.toString() ?: ""
            if (value.contains(" ")) {
                sb.append("\"$value\"")
            } else {
                sb.append(value)
            }
            if (column != columns - 1) sb.append(",")
        }
        sb.append("\n")
        if (row % 100 == 0) {
            // Write in batches
            csv.appendText(sb.toString())
            sb.clear()
        }
    }
    csv.appendText(sb.toString())
}