package com.github.jomof.kane.impl.sheet

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.impl.*

/**
 * Methods for dealing with Sheet in a functional manner
 */


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
    return dup(cells = cells, rowDescriptors = rowDescriptors)
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
    return dup(cells = cells, columnDescriptors = columnDescriptors)
}

internal fun Sheet.columnType(column: Int): AdmissibleDataType<*> {
    var acceptedDataTypes = possibleDataFormats
    for ((name, expr) in cells) {
        if (name is Coordinate && column == name.column) {
            acceptedDataTypes = acceptedDataTypes
                .filter { tryType ->
                    tryType.tryParse(expr.toString()) != null ||
                            (tryType == doubleAdmissibleDataType && expr is ScalarExpr)
                }
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

internal fun Sheet.tryConvertToColumnIndex(ranges : List<String>) : List<Int>? =
    ranges.map { tryConvertToColumnIndex(it) ?: return null }

