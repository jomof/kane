package com.github.jomof.kane.impl.sheet

import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import com.github.doyaaaaaken.kotlincsv.dsl.context.CsvReaderContext
import com.github.jomof.kane.Row
import com.github.jomof.kane.impl.coordinate
import java.io.InputStream
import java.nio.charset.Charset

/**
 * Parse CSV from string in [data] parameter.
 */
internal fun parseCsv(
    data: String,
    names: List<String> = listOf(),
    keep: List<String> = listOf(), // List of columns to keep
    charset: String = "UTF-8",
    quoteChar: Char = '"',
    delimiter: Char = ',',
    escapeChar: Char = '"',
    skipEmptyLine: Boolean = false,
    skipMissMatchedRow: Boolean = false
): Sequence<Row> = readCsv(
    data.byteInputStream(Charset.forName(charset)),
    CsvParameters(
        names = names,
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
//
//internal class ReadCsvWithHeader(
//    private val csv: File,
//    private val params: CsvParameters,
//    private val context: CsvReaderContext
//) : Sequence<Row> {
//    private val bytes by lazy { csv.readBytes() }
//    override fun iterator(): Iterator<Row> {
//        val ips = bytes.inputStream()
//        val reader = CsvReader(context).open(bytes.inputStream())
////        val csvFileReader = reader.open(csv.inputStream())
//        return object : Iterator<Row> {
//            override fun hasNext(): Boolean {
//                TODO("Not yet implemented")
//            }
//
//            override fun next(): Row {
//                TODO("Not yet implemented")
//            }
//
//        }
//    }
//
//}

private fun readCsvWithHeader(
    csv: InputStream,
    params: CsvParameters,
    context: CsvReaderContext
): Sequence<Row> {
    val sb = SheetBuilderImpl()
    val rows: MutableList<Map<String, String>> = mutableListOf()
    var totalRows = 0
    CsvReader(context).open(csv) {
        for (row in readAllWithHeaderAsSequence()) {
            ++totalRows
            rows += row
                .filter { params.keep.isEmpty() || params.keep.contains(it.key) }
                .map { it.key.trim().intern() to it.value.trim().intern() }
                .toMap()
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
    println("Read ${rows.size} rows with ${info.columnInfos.size} columns")
    return sb.build().limitOutputLines(10)
}

private fun readCsvWithoutHeader(
    csv: InputStream,
    params: CsvParameters,
    context: CsvReaderContext
): Sheet {
    val sb = SheetBuilderImpl()
    val rows: MutableList<List<String>> = mutableListOf()
    if (params.keep.isNotEmpty()) {
        error("keep='${params.keep.joinToString(",")}' not allowed for CSV without headers")
    }
    var totalRows = 0
    CsvReader(context).open(csv) {
        ++totalRows
        for (row in readAllAsSequence()) {
            rows += row.map { it.trim().intern() }
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

    println("Read ${rows.size} rows with ${info.columnInfos.size} columns")

    return sb.build()
}

internal fun readCsv(
    csv: InputStream,
    params: CsvParameters,
    context: CsvReaderContext
): Sequence<Row> {
    return if (params.names.isEmpty()) readCsvWithHeader(csv, params, context)
    else readCsvWithoutHeader(csv, params, context).limitOutputLines(10)
}

internal data class CsvParameters(
    val names: List<String>,
    val keep: Set<String>
)

internal fun csvReaderContext(
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

