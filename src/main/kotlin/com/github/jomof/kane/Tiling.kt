package com.github.jomof.kane

import com.github.jomof.kane.types.KaneType
import com.github.jomof.kane.types.kaneType
import kotlin.reflect.KProperty

data class Tiling<E:Any>(
    val columns : Int,
    val rows : Int,
    val data : List<E>,
    override val type: KaneType<E>,
) : TypedExpr<E> {
    init {
        assert(rows * columns == data.size)
        assert(rows > 0)
        assert(columns > 0)
    }
    operator fun getValue(e: Any?, property: KProperty<*>) = NamedTiling(property.name, this)
    fun getNamedElement(name : String, coordinate: Coordinate) : NamedExpr {
        val element = data[coordinate.row * columns + coordinate.column]
        return when {
            element is Number -> NamedScalar(name, ConstantScalar(element, element.javaClass.kaneType))
            else -> NamedValueExpr(name, element, object : KaneType<E>(element.javaClass) { })
        }
    }
}

data class NamedTiling<E:Any>(
    override val name : String,
    val tiling : Tiling<E>
) : NamedExpr, TypedExpr<E> {
    override val type get() = tiling.type
}

inline fun <reified E:Number> columnOf(vararg elements : E) : MatrixExpr<E> = matrixOf(1, elements.size, *elements)
inline fun <reified E:Number> columnOf(vararg elements : ScalarExpr<E>) : MatrixExpr<E> = matrixOf(1, elements.size, *elements)
inline fun <reified E:Number> columnOf(elements : List<ScalarExpr<E>>) : MatrixExpr<E> = matrixOf(1, elements.size, *(elements.toTypedArray()))
fun columnOf(range : IntRange) : MatrixExpr<Double> = columnOf(*range.map { it.toDouble() }.toTypedArray())
inline fun <reified E:Any> columnOf(vararg elements : E) : Tiling<E> {
    val valueType = object : KaneType<E>(E::class.java) { }
    return Tiling(1, elements.size, elements.toList(), valueType)
}

inline fun <reified E:Number> rowOf(vararg elements : E) : MatrixExpr<E> = matrixOf(elements.size, 1, *elements)
inline fun <reified E:Number> rowOf(vararg elements : ScalarExpr<E>) : MatrixExpr<E> = matrixOf(elements.size, 1, *elements)
inline fun <reified E:Number> rowOf(elements : List<ScalarExpr<E>>) : MatrixExpr<E> = matrixOf(elements.size, 1, *(elements.toTypedArray()))
fun rowOf(range : IntRange) : MatrixExpr<Double> = rowOf(*range.map { it.toDouble() }.toTypedArray())
inline fun <reified E:Any> rowOf(vararg elements : E) : Tiling<E> {
    val valueType = object : KaneType<E>(E::class.java) { }
    return Tiling(elements.size, 1, elements.toList(), valueType)
}



