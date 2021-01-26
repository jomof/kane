package com.github.jomof.kane

import com.github.jomof.kane.ComputableIndex.*


interface SheetRange {
    fun up(move: Int): SheetRange
    fun down(move: Int): SheetRange
    fun left(move: Int): SheetRange
    fun right(move: Int): SheetRange
    val up get() = up(1)
    val down get() = down(1)
    val left get() = left(1)
    val right get() = right(1)
    fun contains(name: String): Boolean
}


data class Coordinate(val column: Int, val row: Int) {
    override fun toString() = "[$column,$row]"
    fun toComputableCoordinate() = CellRange(MoveableIndex(column), MoveableIndex(row))
}

fun coordinatesOf(columns: Int, rows: Int) = sequence {
    for (row in 0 until rows) {
        for (column in 0 until columns) {
            yield(Coordinate(column,row))
        }
    }
}.toList()

operator fun Coordinate.plus(right : Coordinate) = copy(column = column + right.column, row = row + right.row)

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

data class CellRange(
    val column: ComputableIndex,
    val row: ComputableIndex
) : SheetRange {
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

    override fun contains(name: String) = name == toString()

    companion object {
        fun relative(column: Int, row: Int) = CellRange(RelativeIndex(column), RelativeIndex(row))
        fun moveable(column: Int, row: Int) = CellRange(MoveableIndex(column), MoveableIndex(row))
        fun moveable(coordinate: Coordinate) = moveable(coordinate.column, coordinate.row)
    }
}
//
//fun computeMoveableCoordinate(base: Coordinate, adjustment: CellRange): CellRange {
//    adjustment.rebase(base)
//    val column = when (val column = adjustment.column) {
//        is FixedIndex -> column.index
//        is MoveableIndex -> column.index
//        is RelativeIndex -> column.index + base.column
//    }
//    val row = when (val row = adjustment.row) {
//        is FixedIndex -> row.index
//        is MoveableIndex -> row.index
//        is RelativeIndex -> row.index + base.row
//    }
//    return CellRange(MoveableIndex(column), MoveableIndex(row))
//}
//
//fun computeMoveableCoordinate(base: Coordinate, adjustment: ColumnRange): CellRange {
//    adjustment.rebase(base)
//    val column = when (val column = adjustment.first) {
//        is FixedIndex -> column.index
//        is MoveableIndex -> column.index
//        is RelativeIndex -> column.index + base.column
//    }
//    return CellRange(MoveableIndex(column), MoveableIndex(base.row))
//}
//
//fun computeMoveableCoordinate(base: Coordinate, adjustment: SheetRange): CellRange {
//    adjustment.rebase(base)
//    return when(adjustment) {
//        is CellRange -> computeMoveableCoordinate(base, adjustment)
//        is ColumnRange -> computeMoveableCoordinate(base, adjustment)
//        else -> error("${adjustment.javaClass}")
//    }
//}

private fun CellRange.rebase(base: Coordinate): SheetRange {
    return when {
        column is MoveableIndex && row is MoveableIndex -> this
        column is RelativeIndex && row is RelativeIndex ->
            CellRange(
                MoveableIndex(column.index + base.column),
                MoveableIndex(row.index + base.row)
            )
        column is MoveableIndex && row is RelativeIndex ->
            CellRange(
                column,
                MoveableIndex(row.index + base.row)
            )
        else ->
            error("$this and $base")
    }
}

private fun ColumnRange.rebase(base: Coordinate): SheetRange {
    return when {
        first is MoveableIndex && second is MoveableIndex -> this
        else ->
            error("$this and $base")
    }
}

fun SheetRange.rebase(base: Coordinate): SheetRange {
    return when {
        this is CellRange -> rebase(base)
        this is ColumnRange -> rebase(base)
        else ->
            error("$javaClass")
    }
}

// Cell naming
fun coordinateToCellName(column: ComputableIndex, row: ComputableIndex) =
    CellRange(column, row).toString()

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
    if (tag.contains("$"))
        return looksLikeCellName(tag.replace("$", ""))
    for (c in tag) {
        if (c !in 'A'..'Z' && c !in '0'..'9') return false
    }
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
    assert(looksLikeCellName(tag))
    val column = cellNameToColumnIndex(tag)
    val row = cellNameToRowIndex(tag)
    return Coordinate(
        column = column,
        row = row
    )
}

fun cellNameToComputableCoordinate(name: String): CellRange {
    val columnIsFixed = name[0] == '$'
    val lastIndexOfDollar = name.lastIndexOf('$')
    val rowIsFixed = lastIndexOfDollar > 0
    val coordinate = cellNameToCoordinate(name.replace("$", ""))
    val column = if (columnIsFixed) FixedIndex(coordinate.column) else MoveableIndex(coordinate.column)
    val row = if (rowIsFixed) FixedIndex(coordinate.row) else MoveableIndex(coordinate.row)
    return CellRange(
        column = column,
        row = row
    )
}

data class RectangleSheetRange(
    val first: CellRange,
    val second: CellRange
) : SheetRange {
    override fun up(move: Int) = copy(first = first.up(move), second = second.up(move))
    override fun down(move: Int) = copy(first = first.down(move), second = second.down(move))
    override fun left(move: Int) = copy(first = first.down(move), second = second.down(move))
    override fun right(move: Int) = copy(first = first.down(move), second = second.down(move))
    override fun toString() = "$first:$second"
    override fun contains(name: String): Boolean {
        val cell = cellNameToCoordinate(name)
        return cell.column >= first.column.index &&
                cell.column <= second.column.index &&
                cell.row >= first.row.index &&
                cell.row <= second.row.index
    }
}

data class ColumnRange(
    val first: ComputableIndex,
    val second: ComputableIndex
) : SheetRange {
    override fun up(move: Int) = CellRange(first, RelativeIndex(-1))
    override fun down(move: Int) = CellRange(first, RelativeIndex(1))
    override fun left(move: Int) = copy(first = first - move, second = second - move)
    override fun right(move: Int) = copy(first = first + move, second = second + move)
    override fun toString(): String {
        if (first == second) return first.toColumnName()
        return "${first.toColumnName()}:${second.toColumnName()}"
    }

    override fun contains(name: String): Boolean {
        val cell = cellNameToCoordinate(name)
        return cell.column >= first.index && cell.column <= second.index
    }
}


fun parseRange(range: String): SheetRange {
    if (looksLikeCellName(range)) {
        return cellNameToComputableCoordinate(range)
    }
    val split = range.split(':')
    if (split.size == 1) {
        val value = split[0]
        assert(looksLikeColumnName(value))
        val column = cellNameToComputableCoordinate(value + "1").column
        return ColumnRange(column, column)
    }

    val (left, right) = range.split(':')
    if (looksLikeColumnName(left) && looksLikeColumnName(right)) {
        return ColumnRange(
            cellNameToComputableCoordinate(left + "1").column,
            cellNameToComputableCoordinate(right + "1").column,
        )
    }
    return RectangleSheetRange(
        cellNameToComputableCoordinate(left),
        cellNameToComputableCoordinate(right)
    )
}


