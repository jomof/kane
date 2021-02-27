package com.github.jomof.kane.impl.hashing

fun bytesToHexString(bytes: ByteArray): String {
    val sb = StringBuilder()
    for (byte in bytes) sb.append("%02x".format(byte))
    return sb.toString()
}