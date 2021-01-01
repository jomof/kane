package com.github.jomof.kane.rigueur

import com.github.jomof.kane.rigueur.types.*

interface Matrix<E:Number> {
    val columns : Int
    val rows : Int
    val type : AlgebraicType<E>
    operator fun get(coordinate : Coordinate) = get(coordinate.column, coordinate.row)
    operator fun get(column : Int, row : Int) : E
}

interface MutableMatrix<E:Number> : Matrix<E> {
    operator fun set(coordinate : Coordinate, value : E) { set(coordinate.column, coordinate.row, value) }
    operator fun set(column : Int, row : Int, value : E)
}

fun <E:Number> Matrix<E>.forEach(call : (E) -> Unit) {
    for(column in 0 until columns) {
        for(row in 0 until rows) {
            call(this[column,row])
        }
    }
}

fun <E:Number> MutableMatrix<E>.mapAssign(init : (E) -> E) {
    for(column in 0 until columns) {
        for(row in 0 until rows) {
            this[column,row] = init(this[column,row])
        }
    }
}

fun <E:Number> MutableMatrix<E>.zero() { set(type.zero) }
fun <E:Number> MutableMatrix<E>.set(value : E) { mapAssign { value } }
fun <E:Number> MutableMatrix<E>.set(value : Matrix<E>) {
    for(column in 0 until columns) {
        for(row in 0 until rows) {
            this[column,row] = value[column,row]
        }
    }
}

operator fun <E:Number> MutableMatrix<E>.divAssign(value: E) { mapAssign { type.divide(it, value) } }
operator fun <E:Number> MutableMatrix<E>.timesAssign(value: E) { mapAssign { type.multiply(it, value) } }
operator fun <E:Number> MutableMatrix<E>.minusAssign(value: Matrix<E>) {
    for(column in 0 until columns) {
        for(row in 0 until rows) {
            this[column,row] = type.subtract(this[column,row], value[column,row])
        }
    }
}

fun <E:Number> Matrix<E>.render(): String {
    if (columns == 1 && rows == 1) return type.render(this[0,0])
    if (columns == 1) {
        return "[" + (0 until rows).map { type.render(this[0,it]) }.joinToString("|") + "]ᵀ"
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


inline fun <reified E:Number> valueMatrixOf(columns : Int, rows : Int, vararg values : E) =
    ValueMatrix(columns, rows, E::class.java.kaneType, values.toList())
inline fun <reified E:Number> Array<E>.toColumnMatrix() =
    ValueMatrix(1, size, E::class.java.kaneType, toList())
inline fun <reified E:Number> Array<E>.toRowMatrix() =
    ValueMatrix(size, 1, E::class.java.kaneType, toList())

data class ValueMatrix<E:Number>(
    override val columns: Int,
    override val rows: Int,
    override val type: AlgebraicType<E>,
    val elements : List<E>
) : Matrix<E> {
    private fun coordinateToIndex(column : Int, row : Int) = row * columns + column
    override fun get(column: Int, row: Int) = elements[coordinateToIndex(column, row)]
    override fun toString() = render()
}
