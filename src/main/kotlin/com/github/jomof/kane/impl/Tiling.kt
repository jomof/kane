package com.github.jomof.kane.impl

import com.github.jomof.kane.Expr
import com.github.jomof.kane.TypedExpr
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






