package com.github.jomof.kane.impl

import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.impl.types.AlgebraicType

interface MatrixShape {
    val columns : Int
    val rows : Int
    val type : AlgebraicType
    fun ref(array : DoubleArray) : MutableMatrix
    fun owns(index : Int) : Boolean
}

data class LinearMatrixShape(
    override val columns : Int,
    override val rows : Int,
    val offset : Int,
    override val type : AlgebraicType
) : MatrixShape {
    override fun ref(array : DoubleArray) = LinearMatrixRef(columns, rows, type, offset, array)
    override fun owns(index: Int) = index >= offset && index < offset + columns * rows
}

class LinearMatrixRef(
    override val columns : Int,
    override val rows : Int,
    override val type : AlgebraicType,
    private val offset : Int,
    private val array : DoubleArray) : MutableMatrix {
    private fun coordinateToIndex(column : Int, row : Int) = row * columns + column
    override fun toString() = render()
    override fun get(column: Int, row: Int) = array[coordinateToIndex(column, row) + offset]
    override fun set(column: Int, row: Int, value: Double) {
        assert(column >= 0)
        assert(row >= 0)
        assert(column <= columns)
        assert(row <= rows)
        array[coordinateToIndex(column, row) + offset] = value
    }
}

data class LookupMatrixShape(
    override val columns : Int,
    override val rows : Int,
    val offsets : List<Int>,
    override val type : AlgebraicType
) : MatrixShape {
    override fun ref(array : DoubleArray) = LookupMatrixRef(columns, rows, type, offsets, array)
    override fun owns(index: Int) = offsets.contains(index)
}

class LookupMatrixRef(
    override val columns : Int,
    override val rows : Int,
    override val type : AlgebraicType,
    private val offsets : List<Int>,
    private val array : DoubleArray) : MutableMatrix {
    private fun coordinateToIndex(column : Int, row : Int) = row * columns + column
    override fun get(column: Int, row: Int) = array[offsets[coordinateToIndex(column, row)]]
    override fun set(column: Int, row: Int, value: Double) { array[offsets[coordinateToIndex(column, row)]] = value }
    override fun toString() = render()
}

data class LookupOrConstantsMatrixShape(
    override val columns: Int,
    override val rows: Int,
    val elements: List<ScalarExpr>,
    override val type: AlgebraicType
) : MatrixShape {
    override fun ref(array : DoubleArray) = LookupOrConstantMatrixRef(columns, rows, type, elements, array)
    override fun owns(index: Int) = elements.filterIsInstance<Slot>().map { it.slot }.contains(index)
}

class LookupOrConstantMatrixRef(
    override val columns : Int,
    override val rows : Int,
    override val type : AlgebraicType,
    val elements : List<ScalarExpr>,
    private val array : DoubleArray) : MutableMatrix {
    private fun coordinateToIndex(column : Int, row : Int) = row * columns + column
    override fun get(column: Int, row: Int) : Double = run {
            when(val offset = elements[coordinateToIndex(column, row)]) {
                is Slot -> array[offset.slot]
                is ConstantScalar -> offset.value
                else -> error("${offset.javaClass}")
            }
    }
    override fun set(column: Int, row: Int, value: Double) {
        when(val offset = elements[coordinateToIndex(column, row)]) {
            is Slot -> array[offset.slot] = value
            else -> error("Can't set non-slot")
        }
    }
    override fun toString() = render()
}

data class EmbeddedScalarShape(val offset : Int) {
    fun ref(array : DoubleArray) = ScalarRef(offset, array)
}

class ScalarRef(
    private val offset : Int,
    private val array : DoubleArray) {
    val value : Double get() = array[offset]
    fun set(value : Double) { array[offset] = value }
    override fun toString() = "$value"
}

