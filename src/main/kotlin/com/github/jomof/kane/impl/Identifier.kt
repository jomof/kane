package com.github.jomof.kane.impl

typealias Id = Any

class Identifier {
    companion object {

        fun normalizeUserInput(value: Coordinate): Unit = error("Already static coordinate")
        fun normalizeUserInput(value: Id): Id =
            when (value) {
                is Coordinate -> value
                is String -> if (looksLikeCellName(value)) cellNameToCoordinate(value) else value
                else -> error("")
            }

        fun row(value: String): Unit = error("Already static string, use cellNameToRowIndex")
        fun row(value: Coordinate): Unit = error("Already static coordinate")
        fun row(value: Id): Int = when (value) {
            is String -> cellNameToRowIndex(value)
            is Coordinate -> value.row
            else -> error("${value.javaClass}")
        }

        fun column(value: String): Unit = error("Already static string, use cellNameToColumnIndex")
        fun column(value: Coordinate): Unit = error("Already static coordinate")
        fun column(value: Id): Int = when (value) {
            is String -> cellNameToColumnIndex(value)
            is Coordinate -> value.column
            else -> error("${value.javaClass}")
        }

        fun coordinate(value: Coordinate): Unit = error("Already static coordinate")
        fun coordinate(value: String): Unit = error("Already static string, use cellNameToCoordinate")
        fun coordinate(value: Any): Coordinate = when (value) {
            is String -> cellNameToCoordinate(value)
            is Coordinate -> value
            else -> error("${value.javaClass}")
        }

        fun string(value: Coordinate): Unit = error("Already static coordinate, use coordinateToCellName")
        fun string(value: String): Unit = error("Already static string")
        fun string(value: Id): String = when (value) {
            is String -> value
            is Coordinate -> coordinateToCellName(value)
            else -> error("${value.javaClass}")
        }

        fun width(value: String): Int = error("Already static string")
        fun width(value: Id): Int = string(value).length

        fun validate(value: Coordinate): Unit = error("Already static coordinate")
        fun validate(value: Id) {
            if (value is Coordinate) return
            if (value !is String) error("Expected string but was ${value.javaClass}")
            if (looksLikeCellName(value))
                error("Should be coordinate not string")
            if (value.startsWith("[") && value.endsWith("]"))
                error("Should be coordinate not string")
        }
    }
}

fun Id.identifierString() = Identifier.string(this)