package com.github.jomof.kane.sheet

import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import com.github.doyaaaaaken.kotlincsv.dsl.context.CsvReaderContext
import com.github.jomof.kane.Identifier
import com.github.jomof.kane.coordinate
import com.github.jomof.kane.indexToColumnName
import java.io.File
import java.io.InputStream
import java.nio.charset.Charset
import kotlin.random.Random

/**
 * Read CSV from a file.
 */
fun readCsv(
    csv: String,
    names: List<String> = listOf(),
    sample: Double = 1.0,
    keep: List<String> = listOf(), // List of columns to keep
    charset: String = "UTF-8",
    quoteChar: Char = '"',
    delimiter: Char = ',',
    escapeChar: Char = '"',
    skipEmptyLine: Boolean = false,
    skipMissMatchedRow: Boolean = false
): Sheet = readCsv(
    File(csv).inputStream(),
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

/**
 * Read CSV from a file.
 */
fun readCsv(
    csv: File,
    names: List<String> = listOf(),
    sample: Double = 1.0,
    keep: List<String> = listOf(), // List of columns to keep
    charset: String = "UTF-8",
    quoteChar: Char = '"',
    delimiter: Char = ',',
    escapeChar: Char = '"',
    skipEmptyLine: Boolean = false,
    skipMissMatchedRow: Boolean = false
) = readCsv(
    csv.inputStream(),
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

/**
 * Parse CSV from string in [data] parameter.
 */
fun parseCsv(
    data: String,
    names: List<String> = listOf(),
    sample: Double = 1.0,
    keep: List<String> = listOf(), // List of columns to keep
    charset: String = "UTF-8",
    quoteChar: Char = '"',
    delimiter: Char = ',',
    escapeChar: Char = '"',
    skipEmptyLine: Boolean = false,
    skipMissMatchedRow: Boolean = false
): Sheet = readCsv(
    data.byteInputStream(Charset.forName(charset)),
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
    csv: InputStream,
    params: CsvParameters,
    context: CsvReaderContext
): Sheet {
    val sb = SheetBuilderImpl()
    val random = Random(7)
    val rows: MutableList<Map<String, String>> = mutableListOf()
    var totalRows = 0
    CsvReader(context).open(csv) {
        for (row in readAllWithHeaderAsSequence()) {
            ++totalRows
            if (params.sample >= 1.0 || random.nextDouble(0.0, 1.0) < params.sample) {
                rows += row
                    .filter { params.keep.isEmpty() || params.keep.contains(it.key) }
                    .map { it.key.trim().intern() to it.value.trim().intern() }
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
            val coordinate = coordinate(column, row)
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
    csv: InputStream,
    params: CsvParameters,
    context: CsvReaderContext
): Sheet {
    val sb = SheetBuilderImpl()
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
                rows += row.map { it.trim().intern() }
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
            val coordinate = coordinate(column, row)
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
    csv: InputStream,
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

/**
 * Write Sheet to a file named [csv]
 */
fun Sheet.writeCsv(csv: String) {
    writeCsv(File(csv))
}

/**
 * Write Sheet to a file named [csv]
 */
fun Sheet.writeCsv(csv: File) {
    csv.writeText("")

    fun colName(column: Int) = columnDescriptors[column]?.name ?: indexToColumnName(column)

    // Column headers
    (0..columns).forEach { column ->
        val columnName = colName(column)
        csv.appendText(Identifier.string(columnName))
        if (column != columns - 1) csv.appendText(",")

    }
    csv.appendText("\n")

    // Data
    val sb = StringBuilder()
    for (row in 0 until rows) {
        for (column in 0 until columns) {
            val cell = coordinate(column, row)
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

/**
 * Construct a sheet by parsing provided text.
 */
fun sheetOfCsv(
    names: List<String> = listOf(),
    sample: Double = 1.0,
    keep: List<String> = listOf(), // List of columns to keep
    quoteChar: Char = '"',
    delimiter: Char = ',',
    escapeChar: Char = '"',
    skipEmptyLine: Boolean = false,
    skipMissMatchedRow: Boolean = false,
    init: () -> String
): Sheet {
    val text = init()
    return parseCsv(
        data = text.trimIndent().trim('\r', '\n'),
        names = names,
        sample = sample,
        keep = keep,
        quoteChar = quoteChar,
        delimiter = delimiter,
        escapeChar = escapeChar,
        skipEmptyLine = skipEmptyLine,
        skipMissMatchedRow = skipMissMatchedRow
    )
}