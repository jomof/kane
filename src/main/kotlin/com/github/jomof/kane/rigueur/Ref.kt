package com.github.jomof.kane.rigueur

interface MatrixRef<E:Any> {
    val columns : Int
    val rows : Int
    val type : AlgebraicType<E>
    operator fun get(coordinate : Coordinate) : E
    operator fun get(column : Int, row : Int) = get(Coordinate(column, row))
    operator fun set(coordinate : Coordinate, value : E)
    operator fun set(column : Int, row : Int, value : E) { set(Coordinate(column, row), value) }
    fun zero() = set(type.zero)
    fun set(value : E) {
        for(column in 0 until columns) {
            for(row in 0 until rows) {
                this[column,row] = value
            }
        }
    }
}
interface MatrixShape<E:Any> {
    val columns : Int
    val rows : Int
    val type : AlgebraicType<E>
    fun ref(array : Array<E>) : MatrixRef<E>
}

data class LinearMatrixShape<E:Any>(
    override val columns : Int,
    override val rows : Int,
    val offset : Int,
    override val type : AlgebraicType<E>
) : MatrixShape<E> {
    override fun ref(array : Array<E>) = LinearMatrixRef(columns, rows, type, offset, array)
}

class LinearMatrixRef<E:Any>(
    override val columns : Int,
    override val rows : Int,
    override val type : AlgebraicType<E>,
    private val offset : Int,
    private val array : Array<E>) : MatrixRef<E> {
    private fun coordinateToIndex(coordinate : Coordinate) = coordinate.row * columns + coordinate.column
    override operator fun get(coordinate : Coordinate) = array[coordinateToIndex(coordinate) + offset]
    override operator fun set(coordinate : Coordinate, value : E) { array[coordinateToIndex(coordinate) + offset] = value }
    override fun toString() = render()
}

data class LookupMatrixShape<E:Any>(
    override val columns : Int,
    override val rows : Int,
    val offsets : List<Int>,
    override val type : AlgebraicType<E>
) : MatrixShape<E> {
    override fun ref(array : Array<E>) = LookupMatrixRef(columns, rows, type, offsets, array)
}

class LookupMatrixRef<E:Any>(
    override val columns : Int,
    override val rows : Int,
    override val type : AlgebraicType<E>,
    private val offsets : List<Int>,
    private val array : Array<E>) : MatrixRef<E> {
    private fun coordinateToIndex(coordinate : Coordinate) = coordinate.row * columns + coordinate.column
    override operator fun get(coordinate : Coordinate) = array[offsets[coordinateToIndex(coordinate)]]
    override operator fun set(coordinate : Coordinate, value : E) { array[offsets[coordinateToIndex(coordinate)]] = value }
    override fun toString() = render()
}

data class EmbeddedScalarShape(val offset : Int) {
    fun <E:Any> ref(array : Array<E>) = ScalarRef(offset, array)
}

class ScalarRef<E:Any>(
    private val offset : Int,
    private val array : Array<E>) {
    val value : E get() = array[offset]
    fun set(value : E) { array[offset] = value }
    override fun toString() = "$value"
}

fun <E:Any> MatrixRef<E>.render(): String {
    if (columns == 1 && rows == 1) return type.render(this[0,0])
    if (columns == 1) {
        return "col " + (0 until rows).map { type.render(this[0,it]) }.joinToString("|")
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
