package com.github.jomof.kane.api

interface RowIoProvider {
    /**
     * The name of the supported format.
     */
    val name : String

    /**
     * The file extension.
     */
    val fileExtension : String
}