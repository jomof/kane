package com.github.jomof.kane.rigueur.types

import com.github.jomof.kane.rigueur.*
import com.github.jomof.kane.rigueur.functions.*
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.exp
import kotlin.math.pow

private val significandBits = 3
private val exponentBits = 4
val exponentMask = ((1 shl exponentBits) - 1) shl significandBits
val significandMask = (1 shl significandBits) - 1
fun ieeeSign(index : Int) = (index and 128) shr (significandBits + exponentBits)
fun ieeeExponent(index : Int) = (index and exponentMask) shr significandBits
fun ieeeSignificand(index : Int) = (index and significandMask)
fun ieeeBasis(index : Int) = when(val exponent = ieeeExponent(index)) {
    0 -> 0.125
    else -> (2.0).pow(exponent - 4)
}
fun ieeeStart(index : Int) = when(val exponent = ieeeExponent(index)) {
    0 -> 0.0
    else -> (2.0).pow(exponent - 1)
}
fun ieeeAdd(index : Int) = ieeeBasis(index) * ieeeSignificand(index)
fun ieeeAbs(index : Int) : Double {
    if (ieeeExponent(index) == 15 && ieeeSignificand(index) == 0) return Double.POSITIVE_INFINITY
    if (ieeeExponent(index) == 15) return Double.NaN
    val start = ieeeStart(index)
    val add = ieeeAdd(index)
    return start + add
}
fun ieee(index : Int) : Double {
    val sign = ieeeSign(index)
    val absolute = ieeeAbs(index)
    return if (sign == 0) absolute
    else -absolute
}

val doubleLookup = Array(256) { index -> ieee(index) / 32.0}

fun byteToDouble(byte : Byte) = doubleLookup[byte + 128]
fun doubleToByteIndex(value : Double) = (doubleLookup.indices.minByOrNull { (doubleLookup[it] - value).pow(2.0) }!! - 128).toByte()
fun buildBinaryOperation(op : (Double, Double) -> Double) = Array(256 * 256) {
    val leftByte = it shr 8
    val rightByte = it and 255
    val leftDouble = doubleLookup[leftByte]
    val rightDouble = doubleLookup[rightByte]
    val result = op(leftDouble, rightDouble)
    val resultIndex = doubleToByteIndex(result)
    resultIndex
}
private val additionTable by lazy { buildBinaryOperation { left, right -> left + right } }
private val subtractionTable by lazy { buildBinaryOperation { left, right -> left - right } }
private val multiplicationTable by lazy { buildBinaryOperation { left, right -> left * right } }
private val powerTable by lazy { buildBinaryOperation { left, right -> left.pow(right) } }
private val divisionTable by lazy { buildBinaryOperation { left, right ->
    if (right == 0.0) 0.0
    else left / right
}}
private fun lookup(table : Array<Byte>, left : Byte, right : Byte) : Byte = run {
    val index = (left+128 shl 8) + right+128
    val result = table[index]
    result
}
private fun buildUnaryOperation(op : (Double) -> Double) = Array(256) {
    val byte = it and 255
    val double = doubleLookup[byte]
    val result = op(double)
    val resultIndex = doubleToByteIndex(result)
    resultIndex
}
private val logitTable by lazy { buildUnaryOperation { value -> logit(value) } }
private val reluTable by lazy { buildUnaryOperation { value -> relu(value) } }
private val lreluTable by lazy { buildUnaryOperation { value -> lrelu(value) } }
private val stepTable by lazy { buildUnaryOperation { value -> step(value) } }
private val lstepTable by lazy { buildUnaryOperation { value -> lstep(value) } }
private val expTable by lazy { buildUnaryOperation { value -> exp(value) } }
private val tanhTable by lazy { buildUnaryOperation { value -> tanh(value) } }
private fun lookup(table : Array<Byte>, byte : Byte) = table[byte + 128]

val QuarterPrecisionAlgebraicType = object : AlgebraicType<Byte>(Byte::class.java) {
    override val simpleName = "8-bit float"
    override fun compare(left: Byte, right: Byte) =
        doubleLookup[left+128].compareTo(doubleLookup[right+128])
    override fun allocArray(size: Int, init: (Int) -> Byte) = Array(size, init)
    override fun allocNullableArray(size: Int, init: (Int) -> Byte?) = Array(size, init)
    override fun render(value: Number): String {
        val value = byteToDouble(value.toByte())
        val result = BigDecimal(value).setScale(5, RoundingMode.HALF_EVEN).toString()
        return if (result.contains(".")) result.trimEnd('0').trimEnd('.')
        else result
    }
    override fun coerceFrom(value: Number) = doubleToByteIndex(value.toDouble())
}
