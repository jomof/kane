package com.github.jomof.kane.impl.csv

import com.github.jomof.kane.Kane
import com.github.jomof.kane.impl.StreamingSamples
import com.github.jomof.kane.impl.csv.CsvParseField.TextField
import com.github.jomof.kane.impl.hashing.encodeBase58
import com.github.jomof.kane.impl.hashing.sha256
import com.github.jomof.kane.impl.sheet.AdmissibleDataType
import com.github.jomof.kane.impl.sheet.doubleAdmissibleDataType
import com.github.jomof.kane.impl.sheet.possibleDataFormats
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.Reader
import kotlin.math.max
import kotlin.math.min

private const val SERIALIZATION_VERSION = "2"

@Serializable
internal data class CsvMetadata(
    val columns: Int,
    val rows: Int,
    val parseParameters: CsvParseContext,
    val columnInfos: List<CsvColumnInfo>,
    val lineStartDeltas: List<Int>
) {
    override fun toString(): String {

        return """
            columns = $columns
            rows = $rows
        """.trimIndent()
    }

    companion object {
        private fun read(cacheFile: File): CsvMetadata {
            return ProtoBuf.decodeFromByteArray(cacheFile.readBytes())
            //return Json.decodeFromString(cacheFile.readText())
        }

        fun computeIfAbsent(csv: File, parseContext: CsvParseContext = CsvParseContext()): CsvMetadata {
            val metadataFile = getCsvMetadataCacheFile(csv)
            if (metadataFile.exists() && metadataFile.lastModified() >= csv.lastModified()) {
                return read(metadataFile)
            }
            val metadata = computeCsvMetadata(csv, parseContext)
            metadata.write(metadataFile)
            return computeIfAbsent(csv, parseContext)
        }
    }
}

@Serializable
internal data class CsvColumnInfo(
    val name: String = "",
    val type: String = "",
    val minWidth: Int = Int.MAX_VALUE,
    val maxWidth: Int = Int.MIN_VALUE,
    val count: String? = null,
    val nans: String? = null,
    val sum: String? = null,
    val min: String? = null,
    val max: String? = null,
    val mean: String? = null,
    val median: String? = null,
    val variance: String? = null,
    val stdev: String? = null,
    val skewness: String? = null,
    val kurtosis: String? = null,
    val coefficientOfVariation: String? = null,
)

internal fun computeCsvMetadata(file: File, context: CsvParseContext = CsvParseContext()): CsvMetadata {
    return computeCsvMetadata(BufferedReader(FileReader(file)), context)
}

internal fun computeCsvMetadata(reader: Reader, context: CsvParseContext = CsvParseContext()): CsvMetadata {
    val lineStartDeltas = mutableListOf<Int>()
    var lastWasEol = true
    var columnNumber = 0
    var dataRowNumber = 0
    var lastLineStart: Long = 0
    var onColumnNamesLine = context.headerHasColumnNames
    val columnInfos = context.columnNames.map { name -> CsvColumnInfo(name) }.toMutableList()
    val columnTypes = mutableListOf<List<AdmissibleDataType<*>>>()
    val columnStatistics = mutableListOf<StreamingSamples>()

    fun onNewColumn(column: Int) {
        columnInfos.add(CsvColumnInfo())
        columnTypes.add(possibleDataFormats)
        columnStatistics.add(StreamingSamples())
    }

    fun onFieldOfColumnNamesLine(column: Int, field: TextField) {
        columnInfos[column] = columnInfos[column].copy(name = field.content.toString())
    }

    fun onDataField(column: Int, row: Int, field: TextField) {
        val content = field.content.toString()
        val col = columnInfos[column]
        if (col.minWidth > content.length || col.maxWidth < content.length) {
            columnInfos[column] = col.copy(
                minWidth = min(col.minWidth, content.length),
                maxWidth = max(col.maxWidth, content.length)
            )
        }
        val parsee = if (content == context.treatAsNaN) "NaN" else content
        if (row < context.linesForTypeAnalysis && columnTypes[column].size > 1) {
            columnTypes[column] = columnTypes[column].filter { type -> type.tryParse(parsee) != null }
        }
        if (columnTypes[column].first() == doubleAdmissibleDataType) {
            val value = doubleAdmissibleDataType.tryParse(parsee) ?: Double.NaN
            columnStatistics[column].insert(value)
        }
    }

    fun onFirstFieldOfNewLine(field: TextField) {
        lineStartDeltas.add((field.startOffset - lastLineStart).toInt())
        lastLineStart = field.startOffset
    }

    parseCsv(reader, context) { field ->
        if (columnInfos.size < columnNumber + 1) {
            onNewColumn(columnNumber)
        }
        when (field) {
            is TextField -> {
                if (onColumnNamesLine) {
                    onFieldOfColumnNamesLine(columnNumber, field)
                }
                if (!onColumnNamesLine) {
                    onDataField(columnNumber, dataRowNumber, field)
                    if (lastWasEol) {
                        onFirstFieldOfNewLine(field)
                        ++dataRowNumber
                    }
                }
                lastWasEol = false
                ++columnNumber
            }
            is CsvParseField.EolineField -> {
                columnNumber = 0
                lastWasEol = true
                onColumnNamesLine = false
            }
        }
    }
    return CsvMetadata(
        columns = columnInfos.size - 1,
        rows = lineStartDeltas.size - 1,
        parseParameters = context,
        columnInfos = columnInfos.mapIndexed { index, col ->
            val type = columnTypes[index].first()


            if (type == doubleAdmissibleDataType) {
                val statistics = columnStatistics[index]
                col.copy(
                    type = type.toString(),
                    count = statistics.count.toString(),
                    sum = statistics.sum.toString(),
                    min = statistics.min.toString(),
                    max = statistics.max.toString(),
                    mean = statistics.mean.toString(),
                    median = statistics.median.toString(),
                    variance = statistics.variance.toString(),
                    stdev = statistics.stdev.toString(),
                    skewness = statistics.skewness.toString(),
                    kurtosis = statistics.kurtosis.toString(),
                    coefficientOfVariation = statistics.coefficientOfVariation.toString()
                )
            } else {
                col.copy(type = type.toString())
            }

        },
        lineStartDeltas = lineStartDeltas
    )
}

internal fun getCsvMetadataCacheFile(csv: File, context: CsvParseContext = CsvParseContext()): File {
    val hashInputs = mutableListOf<Byte>()
    hashInputs.addAll(csv.absolutePath.toByteArray().toList())
    hashInputs.addAll(Json.encodeToString(context).toByteArray().toList())
    hashInputs.addAll(SERIALIZATION_VERSION.toByteArray().toList())
    val hash = encodeBase58(sha256(hashInputs.toByteArray()).sliceArray(0 until 8))
    val name = csv.name + ".$hash"
    return Kane.userCacheFolder.resolve(name)
}

internal fun CsvMetadata.write(cacheFile: File) {
    cacheFile.writeBytes(ProtoBuf.encodeToByteArray(this))
//    cacheFile.writeText(Json.encodeToString(this))
}
