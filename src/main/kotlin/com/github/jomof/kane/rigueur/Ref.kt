package com.github.jomof.kane.rigueur

interface MatrixShape<E:Any> {
    val columns : Int
    val rows : Int
    val type : AlgebraicType<E>
    fun ref(array : Array<E>) : MutableMatrix<E>
    fun owns(index : Int) : Boolean
}

data class LinearMatrixShape<E:Any>(
    override val columns : Int,
    override val rows : Int,
    val offset : Int,
    override val type : AlgebraicType<E>
) : MatrixShape<E> {
    override fun ref(array : Array<E>) = LinearMatrixRef(columns, rows, type, offset, array)
    override fun owns(index: Int) = index >= offset && index < offset + columns * rows
}

class LinearMatrixRef<E:Any>(
    override val columns : Int,
    override val rows : Int,
    override val type : AlgebraicType<E>,
    private val offset : Int,
    private val array : Array<E>) : MutableMatrix<E> {
    private fun coordinateToIndex(column : Int, row : Int) = row * columns + column
    override fun toString() = render()
    override fun get(column: Int, row: Int) = array[coordinateToIndex(column, row) + offset]
    override fun set(column: Int, row: Int, value: E) { array[coordinateToIndex(column, row) + offset] = value }
}

data class LookupMatrixShape<E:Any>(
    override val columns : Int,
    override val rows : Int,
    val offsets : List<Int>,
    override val type : AlgebraicType<E>
) : MatrixShape<E> {
    override fun ref(array : Array<E>) = LookupMatrixRef(columns, rows, type, offsets, array)
    override fun owns(index: Int) = offsets.contains(index)
}

class LookupMatrixRef<E:Any>(
    override val columns : Int,
    override val rows : Int,
    override val type : AlgebraicType<E>,
    private val offsets : List<Int>,
    private val array : Array<E>) : MutableMatrix<E> {
    private fun coordinateToIndex(column : Int, row : Int) = row * columns + column
    override fun get(column: Int, row: Int) = array[offsets[coordinateToIndex(column, row)]]
    override fun set(column: Int, row: Int, value: E) { array[offsets[coordinateToIndex(column, row)]] = value }
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

