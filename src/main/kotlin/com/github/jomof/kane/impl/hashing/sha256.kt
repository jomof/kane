package com.github.jomof.kane.impl.hashing

import java.security.MessageDigest

fun sha256(bytes: ByteArray): ByteArray {
    val md = MessageDigest.getInstance("SHA-256")
    return md.digest(bytes)
}