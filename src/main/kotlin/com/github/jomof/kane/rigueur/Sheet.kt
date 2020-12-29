package com.github.jomof.kane.rigueur

import com.github.jomof.kane.rigueur.Sheet.Companion.coordinateToCellTag

interface CellReferenceExpr : UntypedExpr

data class AbsoluteCellReferenceExpr(private val coordinate : Coordinate) : CellReferenceExpr {
    override fun toString() = coordinateToCellTag(coordinate)
}
data class RelativeCellReferenceExpr(val coordinate : Coordinate) : CellReferenceExpr {
    fun above(offset : Int = 1) = copy(coordinate = coordinate.copy(row = coordinate.row - offset))
    fun below(offset : Int = 1) = copy(coordinate = coordinate.copy(row = coordinate.row + offset))
    fun left(offset : Int = 1) = copy(coordinate = coordinate.copy(column = coordinate.column - offset))
    fun right(offset : Int = 1) = copy(coordinate = coordinate.copy(column = coordinate.column + offset))
    override fun toString() = "ref$coordinate"
}

data class CoerceCellAlgebraic<E:Any>(
    val cell : CellReferenceExpr,
    override val type : AlgebraicType<E>) : ScalarExpr<E> {
    override fun toString() = "$cell"
}

data class Sheet(
    val name : String,
    val cells : Map<String, TypedExpr> = mapOf()) {

    operator fun get(cell : String) = cells[cell]

    fun with(vararg assign : Pair<String, Any>) : Sheet {
        val result = cells.toMutableMap()
        for((cell,value) in assign) {
            val coord = cellTagToCoordinate(cell)
            result[cell] = when (value) {
                is TypedExpr -> value.replaceTypedExpr {
                    when(it) {
                        is CoerceCellAlgebraic<*> -> when(it.cell) {
                            is RelativeCellReferenceExpr -> {
                                val newCoord = coord + it.cell.coordinate
                                it.copy(cell = AbsoluteCellReferenceExpr(newCoord))
                            }
                            else -> it
                        }
                        is TypedExpr -> it
                        else -> error("${it.javaClass}")
                    }
                }
                is Int -> ConstantScalar(value, Int::class.java.algebraicType)
                else -> error("Type of '$value' is unknown")
            }
        }
        return this.copy(cells = result)
    }

    companion object {
        val CURRENT = RelativeCellReferenceExpr(Coordinate(0, 0))

        fun indexToColumnTag(column : Int) : String {
            assert(column >= 0)
            return when (column) {
                in 0..25 -> ('A' + column).toString()
                else -> indexToColumnTag(column / 26 - 1) + indexToColumnTag(column % 26)
            }
        }

        fun columnTagToIndex(column : String) : Int {
            var result = 0
            for(c in column) {
                assert(c in 'A'..'Z')
                result *= 26
                result += (c - 'A' + 1)
            }
            return result - 1
        }

        fun cellTagToCoordinate(tag : String) : Coordinate {
            val column = StringBuilder()
            val row = StringBuilder()
            for(i in tag.indices) {
                val c = tag[i]
                if (c !in 'A'..'Z') break
                column.append(c)
            }
            for(i in column.length until tag.length) {
                val c = tag[i]
                assert(c in '0'..'9')
                row.append(c)
            }
            return Coordinate(
                column = columnTagToIndex(column.toString()),
                row = row.toString().toInt() - 1)
        }

        fun coordinateToCellTag(coord : Coordinate) : String {
            if (coord.column < 0) return "#REF!"
            if (coord.row < 0) return "#REF!"
            return "${indexToColumnTag(coord.column)}${coord.row}"
        }
    }
}

// Times
inline operator fun <reified E:Any> CellReferenceExpr.plus(right : E) =
    CoerceCellAlgebraic(this, E::class.java.algebraicType) + right

