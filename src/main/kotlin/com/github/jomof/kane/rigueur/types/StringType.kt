package com.github.jomof.kane.rigueur.types

class StringType private constructor (): KaneType<String>(String::class.java) {
    companion object {
        val kaneType = StringType()
    }
}

