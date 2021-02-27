package com.github.jomof.kane.impl.hashing

import java.math.BigInteger


private const val ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
private val big0: BigInteger = BigInteger.ZERO
private const val byte0: Byte = 0
private val big58: BigInteger = BigInteger.valueOf(58L)

fun encodeBase58(hash: String, base: Int = 16): String {
    var x = if (base == 16 && hash.take(2) == "0x") BigInteger(hash.drop(2), 16)
    else BigInteger(hash, base)
    val sb = StringBuilder()
    while (x > big0) {
        val r = (x % big58).toInt()
        sb.append(ALPHABET[r])
        x /= big58
    }
    return sb.toString().reversed()
}

fun encodeBase58(bytes: ByteArray) =
    encodeBase58("0x${bytesToHexString(bytes)}")

fun decodeBase58(input: String): ByteArray {
    var result = big0
    for (c in input) {
        result *= big58
        result += BigInteger.valueOf(ALPHABET.indexOf(c).toLong())
    }
    return result.toByteArray().toList().dropWhile { it == byte0 }.toByteArray()
}
