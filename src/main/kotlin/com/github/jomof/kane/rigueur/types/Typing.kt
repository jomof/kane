@file:Suppress("UNCHECKED_CAST")

package com.github.jomof.kane.rigueur.types

import com.github.jomof.kane.rigueur.*
import com.github.jomof.kane.rigueur.functions.*
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.pow
import kotlin.math.exp
import kotlin.reflect.KClass

abstract class KaneType<E:Any>(val java : Class<E>) {
    open val simpleName : String get() = java.simpleName
}
abstract class AlgebraicType<E:Number>(java : Class<E>) : KaneType<E>(java) {
    abstract fun render(value : Number) : String
    abstract fun unary(op : UnaryOp, value : E) : E
    abstract fun compare(left : E, right : E) : Int
    abstract fun allocArray(size : Int, init: (Int) -> E) : Array<E>
    abstract fun allocNullableArray(size : Int, init: (Int) -> E?) : Array<E?>
    abstract fun coerceFrom(value : Number) : E
}

class DoubleAlgebraicType(
    val prefix : String = "",
    val trimLeastSignificantZeros : Boolean = true,
    val precision : Int = 5
) : AlgebraicType<Double>(Double::class.java) {
    override val simpleName = "double"
    override fun unary(op : UnaryOp, value : Double) = when(op) {
        NEGATE -> -value
        LOGIT -> logit(value)
        RELU -> relu(value)
        LRELU -> lrelu(value)
        STEP -> step(value)
        LSTEP -> lstep(value)
        EXP -> exp(value)
        TANH -> tanh(value)
        SUMMATION -> value
        D -> Double.NaN
        else -> error("$op")
    }
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

class FloatAlgebraicType : AlgebraicType<Float>(Float::class.java) {
    override val simpleName = "float"
    override fun unary(op : UnaryOp, value : Float) = when(op) {
        NEGATE -> -value
        LOGIT -> logit(value)
        RELU -> relu(value)
        LRELU -> lrelu(value)
        STEP -> step(value)
        LSTEP -> lstep(value)
        EXP -> exp(value)
        TANH -> tanh(value)
        SUMMATION -> value
        D -> Float.NaN
        else -> error("$op")
    }
    override fun compare(left: Float, right: Float) = left.compareTo(right)
    override fun allocArray(size: Int, init: (Int) -> Float) = Array<Float>(size, init)
    override fun allocNullableArray(size: Int, init: (Int) -> Float?) = Array(size, init)
    override fun render(value: Number): String {
        if (value.toFloat().isInfinite()) return "Infinite"
        if (value.toFloat().isNaN()) return "NaN"
        val result = BigDecimal(value.toDouble()).setScale(5, RoundingMode.HALF_EVEN).toString()
        return if (result.contains(".")) result.trimEnd('0').trimEnd('.')
        else result
    }
    override fun coerceFrom(value : Number) = value.toFloat()

    companion object {
        val kaneType = FloatAlgebraicType()
    }
}

class IntAlgebraicType : AlgebraicType<Int>(Int::class.java) {
    override val simpleName = "int"
    override fun unary(op : UnaryOp, value : Int) = when(op) {
        else -> error("$op")
    }
    override fun compare(left: Int, right: Int) = left.compareTo(right)
    override fun allocArray(size: Int, init: (Int) -> Int) = Array(size, init)
    override fun allocNullableArray(size: Int, init: (Int) -> Int?) = Array(size, init)
    override fun render(value: Number) = value.toString()
    override fun coerceFrom(value : Number) = value.toInt()

    companion object {
        val kaneType = IntAlgebraicType()
    }
}

val <E:Number> Class<E>.kaneType : AlgebraicType<E>
    get() = when(this) {
    Double::class.java,
    java.lang.Double::class.java -> DoubleAlgebraicType.kaneType
    Float::class.java,
    java.lang.Float::class.java -> FloatAlgebraicType.kaneType
    Int::class.java,
    java.lang.Integer::class.java -> IntAlgebraicType.kaneType
    else ->
        error("$this")
} as AlgebraicType<E>

val <E:Number> KClass<E>.kaneType get() = java.kaneType

val <E:Number> AlgebraicType<E>.zero : E get() = coerceFrom(0.0)
val <E:Number> AlgebraicType<E>.one : E get() = coerceFrom(1.0)
val <E:Number> AlgebraicType<E>.two : E get() = coerceFrom(2.0)
val <E:Number> AlgebraicType<E>.half : E get() = coerceFrom(0.5)
val <E:Number> AlgebraicType<E>.negativeOne : E get() = coerceFrom(-1.0)
val <E:Number> AlgebraicType<E>.negativeZero : E get() = coerceFrom(-0.0)