package com.github.jomof.kane

import com.github.jomof.kane.ComputableIndex.FixedIndex
import com.github.jomof.kane.ComputableIndex.RelativeIndex

data class Coordinate(val column : Int, val row : Int) {
    override fun toString() = "[$column,$row]"
    fun toComputableCoordinate() = ComputableCoordinate(FixedIndex(column), FixedIndex(row))
}

fun coordinatesOf(columns: Int, rows: Int) = sequence {
    for(row in 0 until rows) {
        for(column in 0 until columns) {
            yield(Coordinate(column,row))
        }
    }
}.toList()

operator fun Coordinate.plus(right : Coordinate) = copy(column = column + right.column, row = row + right.row)

sealed class ComputableIndex {
    operator fun minus(move: Int) = when(this) {
        is FixedIndex -> FixedIndex(index - move)
        is RelativeIndex -> RelativeIndex(index - move)
    }
    operator fun plus(move: Int) = when(this) {
        is FixedIndex -> FixedIndex(index + move)
        is RelativeIndex -> RelativeIndex(index + move)
    }
    data class FixedIndex(val index : Int) : ComputableIndex() {
        override fun toString() = index.toString()
    }
    data class RelativeIndex(val index : Int) : ComputableIndex() {
        override fun toString() = "[$index]"
    }
}

data class ComputableCoordinate(
    val column : ComputableIndex,
    val row : ComputableIndex
) {
    fun up(move : Int) = copy(row = row - move)
    fun down(move : Int) = copy(row = row + move)
    fun left(move : Int) = copy(column = column - move)
    fun right(move : Int) = copy(column = column + move)
    val up get() = up(1)
    val down get() = down(1)
    val left get() = left(1)
    val right get() = right(1)
    override fun toString() = "[$column,$row]"
    companion object {
        fun relative(column : Int, row : Int) =
            ComputableCoordinate(RelativeIndex(column), RelativeIndex(row))

        fun fixed(column : Int, row : Int) =
            ComputableCoordinate(FixedIndex(column), FixedIndex(row))

        fun fixed(coordinate : Coordinate) =
            ComputableCoordinate(FixedIndex(coordinate.column), FixedIndex(coordinate.row))
    }
}

fun computeAbsoluteCoordinate(base : Coordinate, adjustment : ComputableCoordinate) : ComputableCoordinate {
    val column = when(val column = adjustment.column ) {
        is FixedIndex -> column.index
        is RelativeIndex -> column.index + base.column
    }
    val row = when(val row = adjustment.row ) {
        is FixedIndex -> row.index
        is RelativeIndex -> row.index + base.row
    }
    return ComputableCoordinate(FixedIndex(column), FixedIndex(row))
}


// Cell naming
fun coordinateToCellName(column : ComputableIndex, row : ComputableIndex) : String {
    return when {
        column is FixedIndex && row is FixedIndex -> {
            when {
                column.index < 0 -> "#REF!"
                row.index < 0 -> "#REF!"
                else -> indexToColumnName(column) + "${row.index + 1}"
            }
        }
        column is FixedIndex && row is RelativeIndex -> "[fixed ${column.index},${row.index}]"
        column is RelativeIndex && row is FixedIndex -> "[${column.index},fixed ${row.index}]"
        column is RelativeIndex && row is RelativeIndex -> "[${column.index},${row.index}]"
        else -> error("")
    }
}

fun coordinateToCellName(column : Int, row : Int) : String {
    return coordinateToCellName(FixedIndex(column), FixedIndex(row))
}

fun coordinateToCellName(coord : ComputableCoordinate) = coordinateToCellName(coord.column, coord.row)
fun coordinateToCellName(coord : Coordinate) = coordinateToCellName(FixedIndex(coord.column), FixedIndex(coord.row))

fun cellNameToColumnIndex(tag : String) : Int {
    var result = 0
    for(c in tag) {
        if(c !in 'A'..'Z') break
        result *= 26
        result += (c - 'A' + 1)
    }
    return result - 1
}

fun cellNameToRowIndex(tag : String) : Int {
    var index = 0
    for(c in tag) {
        if(c !in 'A'..'Z') break
        ++index
    }
    return tag.substring(index).toInt() - 1
}


fun indexToColumnName(column : Int) : String {
    assert(column >= 0)
    return when (column) {
        in 0..25 -> ('A' + column).toString()
        else -> indexToColumnName(column / 26 - 1) + indexToColumnName(column % 26)
    }
}

fun indexToColumnName(column : ComputableIndex) : String {
    return when(column) {
        is FixedIndex -> {
            val columnName = indexToColumnName(column.index)
            columnName
        }
        is RelativeIndex -> "[${column.index}]"
    }
}

fun looksLikeCellName(tag : String) : Boolean {
    if (tag.length < 2) return false
    if (tag[0] !in 'A'..'Z') return false
    if (tag.last() !in '0'..'9') return false
    return true
}

fun cellNameToCoordinate(tag : String) : Coordinate {
    assert(looksLikeCellName(tag))
    val column = cellNameToColumnIndex(tag)
    val row = cellNameToRowIndex(tag)
    return Coordinate(
        column = column,
        row = row
    )
}


