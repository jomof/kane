package com.github.jomof.kane.impl.sheet

import com.github.jomof.kane.impl.*
import kotlin.math.min
import kotlin.random.Random

/**
 * Methods for dealing with Sheet in a functional manner
 */

/**
 * Retrieve the first [count] elements of a sheet.
 */
fun Sheet.head(count : Int = 5) = ordinalRows(1 .. count)

/**
 * Retrieve the last [count] elements of a sheet.
 */
fun Sheet.tail(count : Int = 5) = ordinalRows(rows - count + 1 .. rows)

/**
 * Retrieve the last [count] elements of a sheet.
 */
fun Sheet.sample(count : Int = 5, random : Random = Random(7)) : Sheet {
    val countMin = min(count, rows)
    val samples = mutableSetOf<Int>()
    while (samples.size < countMin) {
        samples.add(random.nextInt(rows))
    }
    return ordinalRows(samples.toList().sorted())
}

/**
 * Return the requested rows. Elements are ordinal row number not row name.
 */
fun Sheet.ordinalRows(elements : List<Int>) : Sheet {
    val oldOrdinalToNewOrdinal = elements.indices.map { elements[it] to it+1 }.toMap()
    val rowDescriptors : Map<Int, RowDescriptor> = oldOrdinalToNewOrdinal.map { (old, new) ->
        val descriptor: RowDescriptor = rowDescriptors[old] ?: RowDescriptor(name = listOf(constant(old)))
        new to descriptor
    }.toMap()
    val cells = cells.mapNotNull { (name,expr) ->
        if (name !is Coordinate) name to expr
        else {
            val newRow = oldOrdinalToNewOrdinal[name.row + 1]
            if (newRow != null) {
                val newCoordinate = name.copy(row = newRow - 1)
                val unfrozen = expr.unfreeze(name)
                val refrozen = unfrozen.freeze(newCoordinate)
                newCoordinate to refrozen
            } else null
        }
    }.toCells()
    return copy(cells = cells, rowDescriptors = rowDescriptors)
}

/**
 * Return the requested rows. Elements are ordinal row number not row name.
 */
fun Sheet.ordinalRows(vararg elements : Int) = ordinalRows(elements.toList())

/**
 * Return the requested rows. Elements are ordinal row number not row name.
 */
fun Sheet.ordinalRows(range: IntRange) : Sheet = ordinalRows(range.map { it }.toList())

/**
 * Return a new sheet with just the ordinal columns from the list of columns
 */
fun Sheet.ordinalColumns(elements : List<Int>) : Sheet {
    val oldOrdinalToNewOrdinal = elements.indices.map { elements[it] to it }.toMap()
    val columnDescriptors : Map<Int, ColumnDescriptor> = oldOrdinalToNewOrdinal.map { (old, new) ->
        val descriptor : ColumnDescriptor = columnDescriptors[old] ?: ColumnDescriptor(name = old.toString(), null)
        new to descriptor
    }.toMap()
    val cells = cells.mapNotNull { (name,expr) ->
        if (name !is Coordinate) name to expr
        else {
            val newColumn = oldOrdinalToNewOrdinal[name.column]
            if (newColumn != null) {
                val newCoordinate = name.copy(column = newColumn)
                val unfrozen = expr.unfreeze(name)
                val refrozen = unfrozen.freeze(newCoordinate)
                newCoordinate to refrozen
            } else null
        }
    }.toCells()
    return copy(cells = cells, columnDescriptors = columnDescriptors)
}

/**
 * Map cells of a sheet that are coercible to double.
 */
fun Sheet.mapDoubles(translate: (Double) -> Double): Sheet {
    val evaled = eval()
    val new = cells.toMutableMap()
    evaled.cells.forEach { (name, expr) ->
        when (expr) {
            is ConstantScalar -> {
                val result = translate(expr.value)
                new[name] = expr.copy(value = result)
            }
            is ValueExpr<*> -> {
            }
            else -> error("${expr.javaClass}")
        }
    }
    return copy(cells = new.toCells())
}

fun Expr.mapDoubles(translate: (Double) -> Double): Expr {
    return when (this) {
        is Sheet -> mapDoubles(translate)
        else -> error("$javaClass")
    }
}


/**
 * Map cells of a sheet that are coercible to double.
 */
fun Sheet.fillna(value: Double) = mapDoubles {
    if (it.isNaN()) value
    else it
}

fun Expr.fillna(value: Double): Expr {
    return when (this) {
        is Sheet -> fillna(value)
        else -> error("$javaClass")
    }
}

/**
 * Filter rows of a Sheet with a predicate function.
 */
fun Sheet.filterRows(predicate: (RowView) -> Boolean): Sheet {
    val rows = (1..rows).filter {
        predicate(RowView(this, it - 1))
    }
    return ordinalRows(rows)
}

fun Sheet.columnType(column: Int): AdmissibleDataType<*> {
    var acceptedDataTypes = possibleDataFormats
    for ((name, expr) in cells) {
        if (name is Coordinate && column == name.column) {
            acceptedDataTypes = acceptedDataTypes
                .filter { tryType -> tryType.tryParse(expr.toString()) != null }
            if (acceptedDataTypes.size == 1) return acceptedDataTypes.first()
        }
    }
    return acceptedDataTypes.first()
}

internal fun Sheet.fullColumnDescriptor(column: Int): ColumnDescriptor {
    val name: String = columnDescriptors[column]?.name?.identifierString() ?: indexToColumnName(column)
    val type: AdmissibleDataType<*> = columnDescriptors[column]?.type ?: columnType(column)
    return ColumnDescriptor(name, type)
}

/**
 * Return the column types of this sheet.
 */
val Sheet.types : Sheet get() {
    return sheetOf {
        nameColumn(0, "name")
        nameColumn(1, "type")
        nameColumn(2, "format")
        val descriptors = (0 until columns).map { fullColumnDescriptor(it) }
        val a1 by columnOf(descriptors.map { Identifier.string(it.name) })
        val b1 by columnOf(descriptors.map { it.type!!.type.simpleName })
        val c1 by columnOf(descriptors.map { it.type.toString() })
        listOf(a1, b1, c1)
    }.showExcelColumnTags(false)
}

/**
 * Return a subsection of a sheet
 */
operator fun Sheet.get(vararg ranges: String): Expr {
    if (ranges.size == 1 && looksLikeCellName(ranges[0]))
        return cells.getValue(cellNameToCoordinate(ranges[0]))
    val asColumnIndices = tryConvertToColumnIndex(ranges.toList())
    if (asColumnIndices != null) return ordinalColumns(asColumnIndices)
    error("Couldn't get sheet subset for ${ranges.joinToString(",")}")
}

operator fun Sheet.get(column: Int, row: Int): Expr {
    return cells.getValue(coordinate(column, row))
}

internal fun Sheet.tryConvertToColumnIndex(range: String): Int? {
    val columnByName = columnDescriptors
        .filter { (_, descriptor) -> descriptor.name == range }
        .toList()
        .firstOrNull()
    if (columnByName != null) {
        return columnByName.first
    }
    // Check to see if it looks like an Excel column name
    if (looksLikeColumnName(range)) return cellNameToColumnIndex(range)
    return null
}

private fun Sheet.tryConvertToColumnIndex(ranges : List<String>) : List<Int>? =
    ranges.map { tryConvertToColumnIndex(it) ?: return null }
