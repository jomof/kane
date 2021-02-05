package com.github.jomof.kane

import com.github.jomof.kane.functions.divide
import com.github.jomof.kane.functions.multiply
import com.github.jomof.kane.functions.subtract
import com.github.jomof.kane.types.AlgebraicType
import com.github.jomof.kane.types.kaneDouble

interface Matrix {
    val columns : Int
    val rows : Int
    val type : AlgebraicType
    operator fun get(coordinate : Coordinate) = get(coordinate.column, coordinate.row)
    operator fun get(column : Int, row : Int) : Double
}

interface MutableMatrix : Matrix {
    operator fun set(coordinate : Coordinate, value : Double) { set(coordinate.column, coordinate.row, value) }
    operator fun set(column : Int, row : Int, value : Double)
}

fun Matrix.forEach(call : (Double) -> Unit) {
    for(column in 0 until columns) {
        for(row in 0 until rows) {
            call(this[column,row])
        }
    }
}

fun MutableMatrix.mapAssign(init : (Double) -> Double) {
    for(column in 0 until columns) {
        for(row in 0 until rows) {
            this[column,row] = init(this[column,row])
        }
    }
}

fun MutableMatrix.zero() { set(0.0) }
fun MutableMatrix.set(value : Double) { mapAssign { value } }
fun MutableMatrix.set(value : Matrix) {
    for(column in 0 until columns) {
        for(row in 0 until rows) {
            this[column,row] = value[column,row]
        }
    }
}

operator fun MutableMatrix.divAssign(value: Double) { mapAssign { divide(it, value) } }
operator fun MutableMatrix.timesAssign(value: Double) { mapAssign { multiply(it, value) } }
operator fun MutableMatrix.minusAssign(value: Matrix) {
    for(column in 0 until columns) {
        for(row in 0 until rows) {
            this[column,row] = subtract(this[column,row], value[column,row])
        }
    }
}

fun Matrix.render(): String {
    if (columns == 1 && rows == 1) return type.render(this[0,0])
    if (columns == 1) {
        return "[" + (0 until rows).joinToString("|") { type.render(this[0, it]) } + "]ᵀ"
    }
    val sb = StringBuilder()
    for (row in 0 until rows) {
        for(column in 0 until columns) {
            sb.append(type.render(this[column,row]))
            if (column != columns - 1) sb.append("|")
        }
        if (row != rows - 1) sb.append("\n")
    }
    return sb.toString()
}


fun valueMatrixOf(columns : Int, rows : Int, vararg values : Double) =
    ValueMatrix(columns, rows, kaneDouble, values.toList())
fun DoubleArray.toColumnMatrix() =
    ValueMatrix(1, size, kaneDouble, toList())
fun DoubleArray.toRowMatrix() =
    ValueMatrix(size, 1, kaneDouble, toList())

data class ValueMatrix(
    override val columns: Int,
    override val rows: Int,
    override val type: AlgebraicType,
    val elements : List<Double>
) : Matrix {
    private fun coordinateToIndex(column : Int, row : Int) = row * columns + column
    override fun get(column: Int, row: Int) = elements[coordinateToIndex(column, row)]
    override fun toString() = render()
}
