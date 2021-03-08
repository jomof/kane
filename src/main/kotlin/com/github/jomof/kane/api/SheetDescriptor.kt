package com.github.jomof.kane.api

data class SheetDescriptor(
    val limitOutputLines: Int = Int.MAX_VALUE,
    val showExcelColumnTags: Boolean = true,
    val showRowAndColumnForSingleCell: Boolean = false
)