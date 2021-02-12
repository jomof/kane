package com.github.jomof.kane.impl

import com.github.jomof.kane.Expr
import com.github.jomof.kane.MatrixExpr
import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.TypedExpr
import com.github.jomof.kane.impl.sheet.SheetBuilderImpl
import com.github.jomof.kane.impl.types.KaneType
import com.github.jomof.kane.impl.types.kaneType
import kotlin.reflect.KProperty

data class Tiling<E : Any>(
    val columns: Int,
    val rows: Int,
    val data: List<E>,
    override val type: KaneType<E>,
) : TypedExpr<E> {
    init {
        assert(rows * columns == data.size)
        assert(rows > 0)
        assert(columns > 0)
    }

    fun toNamedTilingExpr(name: Id) = NamedTiling(name, this)
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)

    fun getUnnamedElement(coordinate: Coordinate): Expr {
        return when (val element = data[coordinate.row * columns + coordinate.column]) {
            is Double -> ConstantScalar(element)
            else -> ValueExpr(element, element.javaClass.kaneType)
        }
    }
}

data class NamedTiling<E:Any>(
    override val name: Id,
    val tiling: Tiling<E>
) : NamedExpr, TypedExpr<E> {
    override val type get() = tiling.type
}

fun rowOf(vararg elements : Double) : MatrixExpr = matrixOf(elements.size, 1, *elements)
fun rowOf(vararg elements : ScalarExpr) : MatrixExpr = matrixOf(elements.size, 1, *elements)
fun rowOf(elements: List<ScalarExpr>): MatrixExpr = matrixOf(elements.size, 1, *(elements.toTypedArray()))
fun rowOf(count: Int, init: SheetBuilderImpl.(Int) -> Any): MatrixExpr {
    val sb = SheetBuilderImpl()
    val elements = (0 until count)
        .map {
            convertAnyToScalarExpr(init(sb, it))
        }
    return matrixOf(count, 1, *elements.toTypedArray())
}

fun rowOf(range: Pair<Double, Double>, step: Double = 1.0): MatrixExpr {
    val (start, stop) = range
    var current = start
    val elements = mutableListOf<Double>()
    while (current < stop + 1) {
        elements += current
        current += step
    }
    return matrixOf(elements.size, 1, elements)
}

inline fun <reified E : Any> rowOf(vararg elements: E): Tiling<E> {
    val valueType = E::class.java.kaneType
    return Tiling(elements.size, 1, elements.toList(), valueType)
}


