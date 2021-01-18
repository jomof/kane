package com.github.jomof.kane.sheet

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.jomof.kane.Coordinate
import java.io.File

fun readCsvAsSheet(csv : File) : Sheet {
    val sb = SheetBuilder()
    val rows: List<Map<String, String>> = csvReader().readAllWithHeader(csv)
    val info = analyzeDataTypes(rows)
    for(column in info.columnInfos.indices) {
        val columnInfo = info.columnInfos[column]
        sb.column(column, columnInfo.name)
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