@file:Suppress("UNCHECKED_CAST")

package com.github.jomof.kane.types

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.reflect.KClass

abstract class KaneType<E:Any>(val java : Class<E>) {
    open val simpleName : String get() = java.simpleName
}
abstract class AlgebraicType(java : Class<Double>) : KaneType<Double>(java) {
    abstract fun render(value : Number) : String
    abstract fun compare(left : Double, right : Double) : Int
    abstract fun allocArray(size : Int, init: (Int) -> Double) : Array<Double>
    abstract fun allocNullableArray(size : Int, init: (Int) -> Double?) : Array<Double?>
    abstract fun coerceFrom(value : Number) : Double
}

class DoubleAlgebraicType(
    val prefix : String = "",
    val trimLeastSignificantZeros : Boolean = true,
    val precision : Int = 5
) : AlgebraicType(Double::class.java) {
    override val simpleName = "double"
    override fun compare(left: Double, right: Double) = left.compareTo(right)
    override fun allocArray(size: Int, init: (Int) -> Double) = Array(size, init)
    override fun allocNullableArray(size: Int, init: (Int) -> Double?) = Array(size, init)
    override fun render(value: Number): String {
        if (value.toDouble().isInfinite()) return "Infinite"
        if (value.toDouble().isNaN()) return "NaN"
        val result = BigDecimal(value.toDouble()).setScale(precision, RoundingMode.HALF_EVEN).toString()
        val trimmed = if (result.contains(".") && trimLeastSignificantZeros)
                result.trimEnd('0').trimEnd('.')
            else result
        return prefix + trimmed
    }
    override fun coerceFrom(value : Number) = value.toDouble()

    companion object {
        val kaneType = DoubleAlgebraicType()
    }
}
val Class<Double>.kaneType : AlgebraicType
    get() = when(this) {
    Double::class.java,
    java.lang.Double::class.java -> DoubleAlgebraicType.kaneType
    else ->
        error("$this")
}

val KClass<Double>.kaneType get() = java.kaneType

//val AlgebraicType.zero : Double get() = coerceFrom(0.0)
//val AlgebraicType.one : Double get() = coerceFrom(1.0)
//val AlgebraicType.two : Double get() = coerceFrom(2.0)
//val AlgebraicType.half : Double get() = coerceFrom(0.5)
//val AlgebraicType.negativeOne : Double get() = coerceFrom(-1.0)
//val AlgebraicType.negativeZero : Double get() = coerceFrom(-0.0)