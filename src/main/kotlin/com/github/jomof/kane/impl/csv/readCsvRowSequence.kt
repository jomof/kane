package com.github.jomof.kane.impl.csv

import com.github.jomof.kane.CountableColumns
import com.github.jomof.kane.CountableRows
import com.github.jomof.kane.Row
import com.github.jomof.kane.impl.ConstantScalar
import com.github.jomof.kane.impl.RetypeScalar
import com.github.jomof.kane.impl.ValueExpr
import com.github.jomof.kane.impl.sheet.ColumnDescriptor
import com.github.jomof.kane.impl.sheet.RowDescriptor
import com.github.jomof.kane.impl.sheet.SheetDescriptor
import com.github.jomof.kane.impl.sheet.typeNameToAdmissibleType
import com.github.jomof.kane.impl.types.AlgebraicType
import com.github.jomof.kane.impl.types.KaneType
import com.github.jomof.kane.impl.types.StringKaneType
import java.io.File
import java.io.RandomAccessFile
import java.util.*


data class ReadCsvRowSequence(
    val file: File,
    val parseContext: CsvParseContext
) :
    Sequence<Row>, CountableRows, CountableColumns {
    private val meta by lazy { CsvMetadata.computeIfAbsent(file, parseContext) }
    private val columnDescriptors by lazy {
        meta.columnInfos.mapIndexed { index, info ->
            index to ColumnDescriptor(info.name, typeNameToAdmissibleType(info.type))
        }.toMap()
    }

    override fun iterator(): Iterator<Row> {
        var currentRow = 0
        var currentPosition = meta.lineStartDeltas[0].toLong()
        val sheetDescriptor = SheetDescriptor()
        val sequence = this
        val reader by lazy { RandomAccessFile(file, "r") }

        return object : Iterator<Row> {
            override fun hasNext() = currentRow < meta.rows
            override fun next(): Row {
                val result = object : Row() {
                    private val lineStart = currentPosition
                    private val fields by lazy {
                        reader.seek(lineStart)
                        parseCsv(reader.readLine(), parseContext).singleOrNull() ?: listOf()
                    }
                    override val columnCount get() = meta.columns
                    override val columnDescriptors get() = sequence.columnDescriptors
                    override val rowOrdinal = currentRow
                    override val rowDescriptor: RowDescriptor? get() = null
                    override val sheetDescriptor: SheetDescriptor get() = sheetDescriptor
                    override fun get(column: Int): Any? {
                        val descriptor = columnDescriptors[column]!!
                        val field = if (column < fields.size) fields[column] else ""
                        return when (val parsed = descriptor.type!!.tryParse(field)) {
                            null -> ValueExpr(
                                value = field,
                                type = StringKaneType.kaneType
                            )
                            is Double ->
                                RetypeScalar(ConstantScalar(parsed), descriptor.type.type as AlgebraicType)
                            is String -> ValueExpr(
                                value = parsed,
                                type = descriptor.type.type as KaneType<String>
                            )
                            is Date -> ValueExpr(
                                value = parsed,
                                type = descriptor.type.type as KaneType<Date>
                            )
                            else -> error("${parsed.javaClass}")
                        }
                    }

                    override fun get(column: String): Any? {
                        val (index, descriptor) = columnDescriptors
                            .filter { it.value.name == column }
                            .toList()
                            .singleOrNull() ?: error("Column '$column' is unknown")
                        return this[index]
                    }

                }
                ++currentRow
                if (hasNext()) {
                    currentPosition += meta.lineStartDeltas[currentRow]
                }
                return result
            }

        }
    }

    override val columns: Int get() = meta.columns
    override val rows: Int get() = meta.rows
}

fun readCsvRowSequence(file: File, parseContext: CsvParseContext = CsvParseContext()) =
    ReadCsvRowSequence(file, parseContext)