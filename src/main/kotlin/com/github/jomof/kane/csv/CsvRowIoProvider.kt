package com.github.jomof.kane.csv

import com.github.jomof.kane.api.RowIoProvider

class CsvRowIoProvider : RowIoProvider {
    override val name = "Comma Separated Values"
    override val fileExtension = "csv"
}