package com.github.jomof.kane

import com.github.jomof.kane.ComputableIndex.*

interface SheetRangeRef {
    fun up(move: Int): SheetRangeRef
    fun down(move: Int): SheetRangeRef
    fun left(move: Int): SheetRangeRef
    fun right(move: Int): SheetRangeRef
    val up get() = up(1)
    val down get() = down(1)
    val left get() = left(1)
    val right get() = right(1)
    fun contains(name: Id): Boolean
}


class Coordinate private constructor(val column: Int, val row: Int) {
    init {
        if (locked) {
            assert(column >= coordinateLookupSide || row >= coordinateLookupSide) {
                "Unexpected"
            }
        }
    }

    operator fun component1(): Int = column
    operator fun component2(): Int = row
    override fun toString() = "[$column,$row]"
    fun toComputableCoordinate() = CellRangeRef(MoveableIndex(column), MoveableIndex(row))
    fun copy(column: Int = this.column, row: Int = this.row): Coordinate = coordinate(column, row)
    override fun hashCode() = row * 7 + column * 119
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Coordinate) return false
        if (column != other.column) return false
        if (row != other.row) return false
        return true
    }

    companion object {
        /**
         * Cache/intern some lower-range coordinates
         */
        private var locked = false
        private const val coordinateLookupSide = 50
        private val coordinateLookup = Array(coordinateLookupSide * coordinateLookupSide) { index ->
            val column = index % coordinateLookupSide
            val row = index / coordinateLookupSide
            Coordinate(column, row)
        }

        fun coordinate(column: Int, row: Int): Coordinate {
            if (column < coordinateLookupSide && row < coordinateLookupSide) {
                val result = coordinateLookup[row * coordinateLookupSide + column]
                assert(result.row == row && result.column == column) {
                    "invalid coordinate lookup"
                }
                return result
            }
            locked = true
            return Coordinate(column, row)
        }
    }
}

fun coordinate(column: Int, row: Int) = Coordinate.coordinate(column, row)

fun coordinatesOf(columns: Int, rows: Int) = sequence {
    for (row in 0 until rows) {
        for (column in 0 until columns) {
            yield(coordinate(column, row))
        }
    }
}.toList()

operator fun Coordinate.plus(right: Coordinate) = copy(column = column + right.column, row = row + right.row)

sealed class ComputableIndex {
    abstract val index: Int
    abstract fun toColumnName(): String
    abstract fun toRowName(): String
    operator fun minus(move: Int) = when (this) {
        is FixedIndex -> FixedIndex(index - move)
        is MoveableIndex -> MoveableIndex(index - move)
        is RelativeIndex -> RelativeIndex(index - move)
    }

    operator fun plus(move: Int) = when (this) {
        is FixedIndex -> FixedIndex(index + move)
        is MoveableIndex -> MoveableIndex(index + move)
        is RelativeIndex -> RelativeIndex(index + move)
    }

    data class FixedIndex(override val index: Int) : ComputableIndex() {
        init {
            assert(index >= 0)
        }

        override fun toColumnName() = "\$${indexToColumnName(index)}"
        override fun toRowName() = "\$${index + 1}"
        override fun toString() = index.toString()
    }

    data class MoveableIndex(override val index: Int) : ComputableIndex() {
        init {
            assert(index >= 0) {
                "negative index"
            }
        }

        override fun toColumnName() = indexToColumnName(index)
        override fun toRowName() = "${index + 1}"
        override fun toString() = "[moveable $index]"
    }

    data class RelativeIndex(override val index: Int) : ComputableIndex() {
        override fun toColumnName() =
            when {
                index > 0 -> "col+$index"
                index < 0 -> "col$index"
                else -> "col"
            }

        override fun toRowName() =
            when {
                index > 0 -> "row+$index"
                index < 0 -> "row$index"
                else -> "row"
            }

        override fun toString() = "[relative $index]"
    }
}

data class CellRangeRef(
    val column: ComputableIndex,
    val row: ComputableIndex
) : SheetRangeRef {
    init {
//        assert(column !is MoveableIndex || row !is MoveableIndex) {
//            "Should be NamedScalarVariable"
//        }
    }

    override fun up(move: Int) = copy(row = row - move)
    override fun down(move: Int) = copy(row = row + move)
    override fun left(move: Int) = copy(column = column - move)
    override fun right(move: Int) = copy(column = column + move)
    override fun toString(): String {
        val hasRelative = column is RelativeIndex || row is RelativeIndex
        val sb = StringBuilder()
        if (hasRelative) sb.append("(")
        sb.append(column.toColumnName())
        if (hasRelative) sb.append(" ")
        sb.append(row.toRowName())
        if (hasRelative) sb.append(")")
        return sb.toString()
    }

    override fun contains(name: Id) = when (name) {
        is String -> name == toString()
        is Coordinate -> column is MoveableIndex && row is MoveableIndex &&
                column.index == name.column && row.index == name.row
        else -> error("")
    }

    fun toCoordinate(): Coordinate {
        if (column !is MoveableIndex) error("")
        if (row !is MoveableIndex) error("")
        return coordinate(column.index, row.index)
    }

    companion object {
        fun relative(column: Int, row: Int) = CellRangeRef(RelativeIndex(column), RelativeIndex(row))
        fun moveable(column: Int, row: Int) = CellRangeRef(MoveableIndex(column), MoveableIndex(row))
        fun moveable(coordinate: Coordinate) = moveable(coordinate.column, coordinate.row)
    }
}

private fun CellRangeRef.rebase(base: Coordinate): CellRangeRef {
    return when {
        column is MoveableIndex && row is MoveableIndex -> this
        column is RelativeIndex && row is RelativeIndex ->
            CellRangeRef(
                MoveableIndex(column.index + base.column),
                MoveableIndex(row.index + base.row)
            )
        column is MoveableIndex && row is RelativeIndex ->
            CellRangeRef(
                column,
                MoveableIndex(row.index + base.row)
            )
        column is MoveableIndex && row is FixedIndex ->
            CellRangeRef(
                MoveableIndex(base.column),
                MoveableIndex(row.index)
            )
        column is FixedIndex && row is MoveableIndex ->
            CellRangeRef(
                MoveableIndex(column.index),
                MoveableIndex(base.row)
            )
        else ->
            error("$this and $base")
    }
}

private fun ColumnRangeRef.rebase(base: Coordinate): SheetRangeRef {
    return when {
        first is MoveableIndex && second is MoveableIndex -> this
        else ->
            error("$this and $base")
    }
}

private fun RectangleRangeRef.rebase(base: Coordinate): SheetRangeRef {
    return RectangleRangeRef(first = first.rebase(base), second = second.rebase(base))
}

fun SheetRangeRef.rebase(base: Coordinate): SheetRangeRef {
    return when (this) {
        is CellRangeRef -> rebase(base)
        is ColumnRangeRef -> rebase(base)
        is RectangleRangeRef -> rebase(base)
        else -> error("$javaClass")
    }
}

// Cell naming
fun coordinateToCellName(column: ComputableIndex, row: ComputableIndex) =
    CellRangeRef(column, row).toString()

fun coordinateToCellName(column: Int, row: Int): String {
    return coordinateToCellName(MoveableIndex(column), MoveableIndex(row))
}

fun coordinateToCellName(coord: Coordinate) =
    coordinateToCellName(MoveableIndex(coord.column), MoveableIndex(coord.row))

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

fun looksLikeCellName(tag : String) : Boolean {
    if (tag.contains("$")) {
        return looksLikeCellName(tag.replace("$", ""))
    }
    for (c in tag) {
        if (c !in 'A'..'Z' && c !in '0'..'9') return false
    }
    if (cellNameToColumnIndex(tag) > 50) return false
    if (tag.length < 2) return false
    if (tag[0] !in 'A'..'Z') return false
    if (tag.last() !in '0'..'9') return false
    return true
}

fun looksLikeColumnName(tag : String) : Boolean {
    for(c in tag) {
        if(c !in 'A'..'Z') return false
    }
    return true
}

fun cellNameToCoordinate(tag: String): Coordinate {
    assert(looksLikeCellName(tag)) {
        "$tag doesn't look like a cell name"
    }
    val column = cellNameToColumnIndex(tag)
    val row = cellNameToRowIndex(tag)
    return coordinate(
        column = column,
        row = row
    )
}

fun cellNameToComputableCoordinate(name: String): CellRangeRef {
    val columnIsFixed = name[0] == '$'
    val lastIndexOfDollar = name.lastIndexOf('$')
    val rowIsFixed = lastIndexOfDollar > 0
    val coordinate = cellNameToCoordinate(name.replace("$", ""))
    val column = if (columnIsFixed) FixedIndex(coordinate.column) else MoveableIndex(coordinate.column)
    val row = if (rowIsFixed) FixedIndex(coordinate.row) else MoveableIndex(coordinate.row)
    return CellRangeRef(
        column = column,
        row = row
    )
}

data class RectangleRangeRef(
    val first: CellRangeRef,
    val second: CellRangeRef
) : SheetRangeRef {
    override fun up(move: Int) = copy(first = first.up(move), second = second.up(move))
    override fun down(move: Int) = copy(first = first.down(move), second = second.down(move))
    override fun left(move: Int) = copy(first = first.down(move), second = second.down(move))
    override fun right(move: Int) = copy(first = first.down(move), second = second.down(move))
    override fun toString() = "$first:$second"
    override fun contains(name: Id): Boolean {
        if (name !is Coordinate) return false
        return name.column >= first.column.index &&
                name.column <= second.column.index &&
                name.row >= first.row.index &&
                name.row <= second.row.index
    }
}

data class ColumnRangeRef(
    val first: ComputableIndex,
    val second: ComputableIndex
) : SheetRangeRef {
    override fun up(move: Int) = CellRangeRef(first, RelativeIndex(-1))
    override fun down(move: Int) = CellRangeRef(first, RelativeIndex(1))
    override fun left(move: Int) = copy(first = first - move, second = second - move)
    override fun right(move: Int) = copy(first = first + move, second = second + move)
    override fun toString(): String {
        if (first == second) return first.toColumnName()
        return "${first.toColumnName()}:${second.toColumnName()}"
    }

    override fun contains(name: Id): Boolean {
        if (name !is Coordinate) return false
        return name.column >= first.index && name.column <= second.index
    }
}

data class NamedColumnRangeRef(
    val name: String,
    val columnOffset: Int = 0,
    val rowOffset: Int = 0
) : SheetRangeRef {
    override fun up(move: Int) = copy(rowOffset = rowOffset - move)
    override fun down(move: Int) = copy(rowOffset = rowOffset + move)
    override fun left(move: Int) = copy(columnOffset = columnOffset - move)
    override fun right(move: Int) = copy(columnOffset = columnOffset - move)
    override fun toString(): String {
        return if (columnOffset == 0 && rowOffset == 0) name
        else "$name[$columnOffset, $rowOffset]"
    }

    override fun contains(name: Id) = error("not supported")
}


fun parseRange(range: String): SheetRangeRef {
    if (looksLikeCellName(range)) {
        return cellNameToComputableCoordinate(range)
    }
    val split = range.split(':')
    if (split.size == 1) {
        val value = split[0]
        if (looksLikeColumnName(value)) {
            val column = cellNameToComputableCoordinate(value + "1").column
            return ColumnRangeRef(column, column)
        } else {
            return NamedColumnRangeRef(range)
        }
    }

    val (left, right) = range.split(':')
    if (looksLikeColumnName(left) && looksLikeColumnName(right)) {
        return ColumnRangeRef(
            cellNameToComputableCoordinate(left + "1").column,
            cellNameToComputableCoordinate(right + "1").column,
        )
    }
    return RectangleRangeRef(
        cellNameToComputableCoordinate(left),
        cellNameToComputableCoordinate(right)
    )
}


