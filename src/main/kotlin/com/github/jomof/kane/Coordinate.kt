package com.github.jomof.kane

data class Coordinate(val column : Int, val row : Int) {
    override fun toString() = "[$column,$row]"
}

fun coordinatesOf(columns: Int, rows: Int) = sequence {
    for(row in 0 until rows) {
        for(column in 0 until columns) {
            yield(Coordinate(column,row))
        }
    }
}.toList()

operator fun Coordinate.plus(right : Coordinate) = copy(column = column + right.column, row = row + right.row)