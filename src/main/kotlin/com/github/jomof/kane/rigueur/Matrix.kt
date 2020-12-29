package com.github.jomof.kane.rigueur

interface Matrix<E:Any> {
    val columns : Int
    val rows : Int
    val type : AlgebraicType<E>
    operator fun get(coordinate : Coordinate) = get(coordinate.column, coordinate.row)
    operator fun get(column : Int, row : Int) : E
}

interface MutableMatrix<E:Any> : Matrix<E> {
    operator fun set(coordinate : Coordinate, value : E) { set(coordinate.column, coordinate.row, value) }
    operator fun set(column : Int, row : Int, value : E)
}

fun <E:Any> Matrix<E>.forEach(call : (E) -> Unit) {
    for(column in 0 until columns) {
        for(row in 0 until rows) {
            call(this[column,row])
        }
    }
}

fun <E:Any> MutableMatrix<E>.mapAssign(init : (E) -> E) {
    for(column in 0 until columns) {
        for(row in 0 until rows) {
            this[column,row] = init(this[column,row])
        }
    }
}

fun <E:Any> MutableMatrix<E>.zero() { set(type.zero) }
fun <E:Any> MutableMatrix<E>.set(value : E) { mapAssign { value } }
fun <E:Any> MutableMatrix<E>.set(value : Matrix<E>) {
    for(column in 0 until columns) {
        for(row in 0 until rows) {
            this[column,row] = value[column,row]
        }
    }
}

operator fun <E:Any> MutableMatrix<E>.divAssign(value: E) { mapAssign { type.divide(it, value) } }
operator fun <E:Any> MutableMatrix<E>.timesAssign(value: E) { mapAssign { type.multiply(it, value) } }
operator fun <E:Any> MutableMatrix<E>.minusAssign(value: Matrix<E>) {
    for(column in 0 until columns) {
        for(row in 0 until rows) {
            this[column,row] = type.subtract(this[column,row], value[column,row])
        }
    }
}

fun <E:Any> Matrix<E>.render(): String {
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


inline fun <reified E:Any> valueMatrixOf(columns : Int, rows : Int, vararg values : E) =
    ValueMatrix(columns, rows, E::class.java.algebraicType, values.toList())
inline fun <reified E:Any> Array<E>.toColumnMatrix() =
    ValueMatrix(1, size, E::class.java.algebraicType, toList())
inline fun <reified E:Any> Array<E>.toRowMatrix() =
    ValueMatrix(size, 1, E::class.java.algebraicType, toList())

data class ValueMatrix<E:Any>(
    override val columns: Int,
    override val rows: Int,
    override val type: AlgebraicType<E>,
    val elements : List<E>
) : Matrix<E> {
    private fun coordinateToIndex(column : Int, row : Int) = row * columns + column
    override fun get(column: Int, row: Int) = elements[coordinateToIndex(column, row)]
    override fun toString() = render()
}
