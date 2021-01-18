package com.github.jomof.kane

import com.github.jomof.kane.ComputableIndex.FixedIndex
import com.github.jomof.kane.ComputableIndex.RelativeIndex


data class Coordinate(val column : Int, val row : Int) {
    override fun toString() = "[$column,$row]"
}

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

    fun reduceToFixed() : Coordinate {
        if (column !is FixedIndex) error("")
        if (row !is FixedIndex) error("")
        return Coordinate(column.index, row.index)
    }
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

fun coordinatesOf(columns: Int, rows: Int) = sequence {
    for(row in 0 until rows) {
        for(column in 0 until columns) {
            yield(Coordinate(column,row))
        }
    }
}.toList()

operator fun Coordinate.plus(right : Coordinate) = copy(column = column + right.column, row = row + right.row)
//operator fun Coordinate.plus(right : ComputableCoordinate) : ComputableCoordinate {
//    val column : Int = when(right.column) {
//        is FixedIndex -> right.column.index
//        is RelativeIndex -> column + right.column.index
//    }
//    val row : Int = when(right.row) {
//        is FixedIndex -> right.row.index
//        is RelativeIndex -> row + right.row.index
//    }
//    return ComputableCoordinate(FixedIndex(column), FixedIndex(row))
//}
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
//fun computeAbsoluteCoordinate(base : ComputableCoordinate, adjustment : Coordinate) : ComputableCoordinate {
//    val fixedBase = base.reduceToFixed()
//    return ComputableCoordinate(FixedIndex(fixedBase), FixedIndex(row))
//}
//operator fun ComputableCoordinate.plus(right : Coordinate) : ComputableCoordinate {
//    val column = column + right.column
//    val row = row + right.row
//    return ComputableCoordinate(column, row)
//}