package com.github.jomof.kane.types

import java.math.BigDecimal
import java.math.RoundingMode

abstract class KaneType<E:Any>(val java : Class<E>) {
    open val simpleName : String get() = java.simpleName
    abstract fun render(value: E): String
    override fun toString() = simpleName
}
abstract class AlgebraicType(java : Class<Double>) : KaneType<Double>(java) {
    abstract fun coerceFrom(value : Number) : Double
}

class StringKaneType : KaneType<String>(String::class.java) {
    override fun render(value: String) = value
    companion object {
        val kaneType = StringKaneType()
    }
}

class IntegerKaneType : KaneType<Integer>(Integer::class.java) {
    override fun render(value: Integer) = "$value"
    companion object {
        val kaneType = IntegerKaneType()
    }
}

class ObjectKaneType : KaneType<Object>(Object::class.java) {
    override fun render(value: Object) = "$value"
    companion object {
        val kaneType = StringKaneType()
    }
}

val <T:Any> Class<T>.kaneType: KaneType<T>
    get() {
        return when(this) {
            Double::class.java -> DoubleAlgebraicType.kaneType
            String::class.java -> StringKaneType.kaneType
            Integer::class.java -> IntegerKaneType.kaneType
            Object::class.java -> ObjectKaneType.kaneType
            else ->
                error("$this")
        } as KaneType<T>
    }

class DoubleAlgebraicType(
    val prefix : String = "",
    val trimLeastSignificantZeros : Boolean = true,
    val precision : Int = 5
) : AlgebraicType(Double::class.java) {
    override val simpleName = "double"
    override fun render(value: Double): String {
        if (value.isInfinite()) return "Infinite"
        if (value.isNaN()) return "NaN"
        val result = BigDecimal(value).setScale(precision, RoundingMode.HALF_EVEN).toString()
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