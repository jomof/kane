package com.github.jomof.kane.rigueur.types

class StringType private constructor (): KaneType<String>(String::class.java) {
    override val simpleName = "string"
    companion object {
        val kaneType = StringType()
    }
}

