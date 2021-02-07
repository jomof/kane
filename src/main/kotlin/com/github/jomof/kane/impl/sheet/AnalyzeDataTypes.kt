@file:Suppress("UNCHECKED_CAST")
package com.github.jomof.kane.impl.sheet

import com.github.jomof.kane.Expr
import com.github.jomof.kane.impl.ConstantScalar
import com.github.jomof.kane.impl.RetypeScalar
import com.github.jomof.kane.impl.ValueExpr
import com.github.jomof.kane.impl.types.*
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min

data class DataTypeAnalysis(
    val columns : Int,
    val rows : Int,
    val columnInfos : List<ColumnInfo>
) {
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("columns=$columns\n")
        sb.append("rows=$rows")
        columnInfos.forEachIndexed { index, info ->
            sb.append("\ncolumn #$index : ${info.name} [${info.typeInfo.type.simpleName}]")
        }
        return sb.toString()
    }
}

data class ColumnInfo(
    val name : String,
    val typeInfo : AdmissibleDataType<*>,
    val minWidth : Int,
    val maxWidth : Int
)

interface AdmissibleDataType<T:Any> {
    val type : KaneType<T>
    fun tryParse(string : String) : T?
}

private class StringAdmissibleDataType : AdmissibleDataType<String> {
    override val type = String::class.java.kaneType
    override fun tryParse(string: String) = string
    override fun toString() = "string"
}

internal class DoubleAdmissibleDataType : AdmissibleDataType<Double> {
    override val type = kaneDouble
    override fun tryParse(string: String) =
        if (string.isBlank()) Double.NaN else string.toDoubleOrNull()

    override fun toString() = "double"
}

private class DollarsAndCentsAdmissibleDataType : AdmissibleDataType<Double> {
    private val format = NumberFormat.getCurrencyInstance(Locale.US)
    override val type = DollarsAndCentsAlgebraicType.kaneType
    override fun tryParse(string: String) : Double? {
        if (!string.contains("$")) return null
        if (string.isBlank()) return Double.NaN
        return format.parse(string)?.toDouble()
    }

    override fun toString() = "currency (${type.render(1000.12)})"
}

private class DollarsAdmissibleDataType : AdmissibleDataType<Double> {
    private val format = NumberFormat.getCurrencyInstance(Locale.US)
    override val type = DollarAlgebraicType.kaneType
    override fun tryParse(string: String) : Double? {
        if (!string.contains("$")) return null
        if (string.contains(".")) return null
        if (string.isBlank()) return Double.NaN
        return format.parse(string)?.toDouble()
    }

    override fun toString() = "currency (${type.render(1000.12)})"
}

private class DateTimeAdmissibleDataType(val formatting : String) : AdmissibleDataType<Date> {
    private val format = SimpleDateFormat(formatting)
    override val type = object : KaneType<Date>(Date::class.java) {
        override val simpleName get() = "date"
        override fun render(value: Date) = format.format(value)
    }

    override fun toString() = formatting
    override fun tryParse(string: String): Date? {
        return try {
            format.parse(string)
        } catch (e: ParseException) {
            null
        }
    }
}

internal val doubleAdmissibleDataType = DoubleAdmissibleDataType()
internal val possibleDataFormats = listOf<AdmissibleDataType<*>>(
    doubleAdmissibleDataType,
    DollarsAdmissibleDataType(),
    DollarsAndCentsAdmissibleDataType(),
    DateTimeAdmissibleDataType("yyyy-MM-dd HH:mm:ss"),
    DateTimeAdmissibleDataType("yyyy-MM-dd"),
    StringAdmissibleDataType()
)

fun analyzeDataType(value: String): AdmissibleDataType<*> {
    return possibleDataFormats.first { it.tryParse(value) != null }
}

fun analyzeDataListTypes(data: List<String>): AdmissibleDataType<*> {
    val rows = data.size

    var acceptedDataTypes = possibleDataFormats.toMutableList()
    var maxWidth = 0
    var minWidth = Int.MAX_VALUE
    data.forEach { value ->
        maxWidth = max(maxWidth, value.length)
        minWidth = min(minWidth, value.length)
        acceptedDataTypes = acceptedDataTypes
            .filter { tryType -> tryType.tryParse(value) != null }
            .toMutableList()
    }
    return acceptedDataTypes.first()
}

fun analyzeDataTypes(data: List<Map<String, String>>): DataTypeAnalysis {
    val rows = data.size
    val columns = data.flatMap { it.keys }.distinct()
    val columnInfos = columns.map { name ->
        var acceptedDataTypes = possibleDataFormats.toMutableList()
        var maxWidth = 0
        var minWidth = Int.MAX_VALUE
        data.forEach { map ->
            val value = map[name] ?: ""
            maxWidth = max(maxWidth, value.length)
            minWidth = min(minWidth, value.length)
            acceptedDataTypes = acceptedDataTypes
                .filter { tryType -> tryType.tryParse(value) != null }
                .toMutableList()
        }
        ColumnInfo(
            name = name,
            typeInfo = acceptedDataTypes.first(),
            maxWidth = maxWidth,
            minWidth = minWidth)
    }

    return DataTypeAnalysis(
        columns = columns.size,
        rows = rows,
        columnInfos = columnInfos)
}

fun analyzeDataTypes(
    columnNames : List<String>,
    data : List<List<String>>) : DataTypeAnalysis {
    val rows = data.size
    val columnInfos = columnNames.mapIndexed { index, name ->
        var acceptedDataTypes = possibleDataFormats.toMutableList()
        var maxWidth = 0
        var minWidth = Int.MAX_VALUE
        data.forEach { list ->
            val value = list[index]
            maxWidth = max(maxWidth, value.length)
            minWidth = min(minWidth, value.length)
            acceptedDataTypes = acceptedDataTypes
                .filter { tryType -> tryType.tryParse(value) != null }
                .toMutableList()
        }
        ColumnInfo(
            name = name,
            typeInfo = acceptedDataTypes.first(),
            maxWidth = maxWidth,
            minWidth = minWidth)
    }

    return DataTypeAnalysis(
        columns = columnNames.size,
        rows = rows,
        columnInfos = columnInfos)
}

fun <T : Any> AdmissibleDataType<T>.parseToExpr(value: String): Expr {
    return when (val parsed = tryParse(value)!!) {
        is Double -> RetypeScalar(ConstantScalar(parsed), type as AlgebraicType)
        else -> ValueExpr(parsed, type as KaneType<Any>)
    }
}