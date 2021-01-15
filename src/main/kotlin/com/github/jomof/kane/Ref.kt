package com.github.jomof.kane

import com.github.jomof.kane.types.AlgebraicType

interface MatrixShape<E:Number> {
    val columns : Int
    val rows : Int
    val type : AlgebraicType<E>
    fun ref(array : Array<E>) : MutableMatrix<E>
    fun owns(index : Int) : Boolean
}

data class LinearMatrixShape<E:Number>(
    override val columns : Int,
    override val rows : Int,
    val offset : Int,
    override val type : AlgebraicType<E>
) : MatrixShape<E> {
    override fun ref(array : Array<E>) = LinearMatrixRef(columns, rows, type, offset, array)
    override fun owns(index: Int) = index >= offset && index < offset + columns * rows
}

class LinearMatrixRef<E:Number>(
    override val columns : Int,
    override val rows : Int,
    override val type : AlgebraicType<E>,
    private val offset : Int,
    private val array : Array<E>) : MutableMatrix<E> {
    private fun coordinateToIndex(column : Int, row : Int) = row * columns + column
    override fun toString() = render()
    override fun get(column: Int, row: Int) = array[coordinateToIndex(column, row) + offset]
    override fun set(column: Int, row: Int, value: E) {
        assert(column >= 0)
        assert(row >= 0)
        assert(column <= columns)
        assert(row <= rows)
        array[coordinateToIndex(column, row) + offset] = value
    }
}

data class LookupMatrixShape<E:Number>(
    override val columns : Int,
    override val rows : Int,
    val offsets : List<Int>,
    override val type : AlgebraicType<E>
) : MatrixShape<E> {
    override fun ref(array : Array<E>) = LookupMatrixRef(columns, rows, type, offsets, array)
    override fun owns(index: Int) = offsets.contains(index)
}

class LookupMatrixRef<E:Number>(
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


data class LookupOrConstantsMatrixShape<E:Number>(
    override val columns: Int,
    override val rows: Int,
    val elements: List<ScalarExpr<E>>,
    override val type: AlgebraicType<E>
) : MatrixShape<E> {
    override fun ref(array : Array<E>) = LookupOrConstantMatrixRef(columns, rows, type, elements, array)
    override fun owns(index: Int) = elements.filterIsInstance<Slot<E>>().map { it.slot }.contains(index)
}

class LookupOrConstantMatrixRef<E:Number>(
    override val columns : Int,
    override val rows : Int,
    override val type : AlgebraicType<E>,
    val elements : List<ScalarExpr<E>>,
    private val array : Array<E>) : MutableMatrix<E> {
    private fun coordinateToIndex(column : Int, row : Int) = row * columns + column
    override fun get(column: Int, row: Int) : E = run {
            when(val offset = elements[coordinateToIndex(column, row)]) {
                is Slot -> array[offset.slot]
                is ConstantScalar -> offset.value
                else -> error("${offset.javaClass}")
            }
    }
    override fun set(column: Int, row: Int, value: E) {
        when(val offset = elements[coordinateToIndex(column, row)]) {
            is Slot -> array[offset.slot] = value
            else -> error("Can't set non-slot")
        }
    }
    override fun toString() = render()
}

data class EmbeddedScalarShape(val offset : Int) {
    fun <E:Number> ref(array : Array<E>) = ScalarRef(offset, array)
}

class ScalarRef<E:Number>(
    private val offset : Int,
    private val array : Array<E>) {
    val value : E get() = array[offset]
    fun set(value : E) { array[offset] = value }
    override fun toString() = "$value"
}

