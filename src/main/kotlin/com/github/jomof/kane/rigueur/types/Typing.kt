@file:Suppress("UNCHECKED_CAST")

package com.github.jomof.kane.rigueur.types

import com.github.jomof.kane.rigueur.*
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.pow
import kotlin.math.exp
import kotlin.reflect.KClass

open class KaneType<E:Any>(val java : Class<E>)
abstract class AlgebraicType<E:Number>(java : Class<E>) : KaneType<E>(java) {
    abstract val simpleName : String
    open val zero : E get() = error("not supported")
    open val one : E get() = error("not supported")
    abstract fun render(value : Number) : String
    abstract fun binary(op : BinaryOp, left : E, right : E) : E
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
    override val zero = 0.0
    override val one = 1.0
    override fun unary(op : UnaryOp, value : Double) = when(op) {
        NEGATE -> -value
        LOGIT -> logit(value)
        RELU -> relu(value)
        LRELU -> lrelu(value)
        STEP -> step(value)
        LSTEP -> lstep(value)
        EXP -> exp(value)
        TANH -> tanh(value)
        D -> 0.0
        else -> error("$op")
    }
    override fun binary(op: BinaryOp, left: Double, right: Double) = when(op) {
        PLUS -> left + right
        MINUS -> left - right
        TIMES -> left * right
        DIV -> left / right
        POW -> left.pow(right)
        else -> error("$op")
    }

    override fun compare(left: Double, right: Double) = left.compareTo(right)
    override fun allocArray(size: Int, init: (Int) -> Double) = Array(size, init)
    override fun allocNullableArray(size: Int, init: (Int) -> Double?) = Array(size, init)
    override fun render(value: Number): String {
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
    override val zero = 0.0f
    override val one = 1.0f
    override fun unary(op : UnaryOp, value : Float) = when(op) {
        NEGATE -> -value
        LOGIT -> logit(value)
        RELU -> relu(value)
        LRELU -> lrelu(value)
        STEP -> step(value)
        LSTEP -> lstep(value)
        EXP -> exp(value)
        TANH -> tanh(value)
        D -> 0.0f
        else -> error("$op")
    }
    override fun binary(op: BinaryOp, left: Float, right: Float) = when(op) {
        PLUS -> left + right
        MINUS -> left - right
        TIMES -> left * right
        DIV -> left / right
        POW -> left.pow(right)
        else -> error("$op")
    }

    override fun compare(left: Float, right: Float) = left.compareTo(right)
    override fun allocArray(size: Int, init: (Int) -> Float) = Array(size, init)
    override fun allocNullableArray(size: Int, init: (Int) -> Float?) = Array(size, init)
    override fun render(value: Number): String {
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
    override val zero = 0
    override fun unary(op : UnaryOp, value : Int) = when(op) {
        else -> error("$op")
    }
    override fun binary(op: BinaryOp, left: Int, right: Int) = when(op) {
        PLUS -> left + right
        MINUS -> left - right
        TIMES -> left * right
        DIV -> left / right
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

val <E:Number> Class<E>.algebraicType : AlgebraicType<E>
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

val <E:Number> KClass<E>.algebraicType get() = java.algebraicType

fun <E:Number> AlgebraicType<E>.negate(value : E) = unary(NEGATE, value)
fun <E:Number> AlgebraicType<E>.add(left : E, right : E) = binary(PLUS, left, right)
fun <E:Number> AlgebraicType<E>.subtract(left : E, right : E) = binary(MINUS, left, right)
fun <E:Number> AlgebraicType<E>.multiply(left : E, right : E) = binary(TIMES, left, right)
fun <E:Number> AlgebraicType<E>.divide(left : E, right : E) = binary(DIV, left, right)
fun <E:Number> AlgebraicType<E>.pow(left : E, right : E) = binary(POW, left, right)
fun <E:Number> AlgebraicType<E>.lt(left : E, right : E) = compare(left, right) == -1
fun <E:Number> AlgebraicType<E>.lte(left : E, right : E) = compare(left, right) != 1
fun <E:Number> AlgebraicType<E>.gt(left : E, right : E) = compare(left, right) == 1
fun <E:Number> AlgebraicType<E>.gte(left : E, right : E) = compare(left, right) != -1
fun <E:Number> AlgebraicType<E>.eq(left : E, right : E) = compare(left, right) == 0
val <E:Number> AlgebraicType<E>.two : E get() = add(one, one)
val <E:Number> AlgebraicType<E>.half : E get() = coerceFrom(0.5)
val <E:Number> AlgebraicType<E>.negativeOne : E get() = negate(one)