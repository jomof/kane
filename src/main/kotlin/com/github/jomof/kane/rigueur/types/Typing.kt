package com.github.jomof.kane.rigueur

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.pow
import kotlin.math.exp
import kotlin.reflect.KClass

open class KaneType<E:Any>(val java : Class<E>)
abstract class AlgebraicType<E:Any>(java : Class<E>) : KaneType<E>(java) {
    abstract val simpleName : String
    open val zero : E get() = error("not supported")
    open val one : E get() = error("not supported")
    abstract fun render(value : Any) : String
    abstract fun binary(op : BinaryOp, left : E, right : E) : E
    abstract fun unary(op : UnaryOp, value : E) : E
    abstract fun compare(left : E, right : E) : Int
    abstract fun allocArray(size : Int, init: (Int) -> E) : Array<E>
    abstract fun allocNullableArray(size : Int, init: (Int) -> E?) : Array<E?>
    abstract fun fromDouble(double : Double) : E
}
val DoubleAlgebraicType = object : AlgebraicType<Double>(Double::class.java) {
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
    override fun render(value: Any): String {
        val result = BigDecimal(value as Double).setScale(5, RoundingMode.HALF_EVEN).toString()
        return if (result.contains(".")) result.trimEnd('0').trimEnd('.')
            else result
    }
    override fun fromDouble(double : Double) = double
}
val FloatAlgebraicType = object : AlgebraicType<Float>(Float::class.java) {
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
    override fun render(value: Any): String {
        val result = BigDecimal((value as Float).toDouble()).setScale(5, RoundingMode.HALF_EVEN).toString()
        return if (result.contains(".")) result.trimEnd('0').trimEnd('.')
        else result
    }
    override fun fromDouble(double : Double) = double.toFloat()
}
val StringAlgebraicType = object : AlgebraicType<String>(String::class.java) {
    override val simpleName = "string"
    override val zero = ""
    override fun unary(op : UnaryOp, value : String) = when(op) {
        else -> error("$op")
    }
    override fun binary(op: BinaryOp, left: String, right: String) = when(op) {
        else -> error("$op")
    }
    override fun compare(left: String, right: String) = left.compareTo(right)
    override fun allocArray(size: Int, init: (Int) -> String) = Array(size, init)
    override fun allocNullableArray(size: Int, init: (Int) -> String?) = Array(size, init)
    override fun render(value: Any) = value.toString()
    override fun fromDouble(double : Double) = "$double"
}

val IntAlgebraicType = object : AlgebraicType<Int>(Int::class.java) {
    override val simpleName = "int"
    override val zero = 0
    override fun unary(op : UnaryOp, value : Int) = when(op) {
        else -> error("$op")
    }
    override fun binary(op: BinaryOp, left: Int, right: Int) = when(op) {
        else -> error("$op")
    }
    override fun compare(left: Int, right: Int) = left.compareTo(right)
    override fun allocArray(size: Int, init: (Int) -> Int) = Array(size, init)
    override fun allocNullableArray(size: Int, init: (Int) -> Int?) = Array(size, init)
    override fun render(value: Any) = value.toString()
    override fun fromDouble(double : Double) = double.toInt()
}

val <E:Any> Class<E>.algebraicType : AlgebraicType<E> get() = when(this) {
    Double::class.java,
    java.lang.Double::class.java -> DoubleAlgebraicType
    Float::class.java,
    java.lang.Float::class.java -> FloatAlgebraicType
    String::class.java,
    java.lang.String::class.java -> StringAlgebraicType
    Int::class.java,
    java.lang.Integer::class.java -> IntAlgebraicType
    else ->
        error("$this")
} as AlgebraicType<E>

val <E:Any> KClass<E>.algebraicType get() = java.algebraicType

fun <E:Any> AlgebraicType<E>.negate(value : E) = unary(NEGATE, value)
fun <E:Any> AlgebraicType<E>.add(left : E, right : E) = binary(PLUS, left, right)
fun <E:Any> AlgebraicType<E>.subtract(left : E, right : E) = binary(MINUS, left, right)
fun <E:Any> AlgebraicType<E>.multiply(left : E, right : E) = binary(TIMES, left, right)
fun <E:Any> AlgebraicType<E>.divide(left : E, right : E) = binary(DIV, left, right)
fun <E:Any> AlgebraicType<E>.pow(left : E, right : E) = binary(POW, left, right)
fun <E:Any> AlgebraicType<E>.lt(left : E, right : E) = compare(left, right) == -1
fun <E:Any> AlgebraicType<E>.lte(left : E, right : E) = compare(left, right) != 1
fun <E:Any> AlgebraicType<E>.gt(left : E, right : E) = compare(left, right) == 1
fun <E:Any> AlgebraicType<E>.gte(left : E, right : E) = compare(left, right) != -1
fun <E:Any> AlgebraicType<E>.eq(left : E, right : E) = compare(left, right) == 0
val <E:Any> AlgebraicType<E>.two : E get() = add(one, one)
val <E:Any> AlgebraicType<E>.half : E get() = fromDouble(0.5)
val <E:Any> AlgebraicType<E>.negativeOne : E get() = negate(one)