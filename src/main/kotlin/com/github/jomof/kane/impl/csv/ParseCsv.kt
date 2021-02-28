package com.github.jomof.kane.impl.csv

import com.github.jomof.kane.impl.csv.CsvParseField.*
import com.github.jomof.kane.impl.csv.CsvStateMachine.ReadResult.*
import java.io.Reader
import java.io.StringReader

internal sealed class CsvParseField {
    data class TextField(var startOffset: Long, var content: StringBuilder) : CsvParseField()
    object EolineField : CsvParseField()
}

/**
 * This is the low level of the CSV parser.
 * It just receives "field" or "EOL".
 */
internal fun parseCsv(
    bufferedReader: Reader,
    context: CsvParseContext = CsvParseContext(),
    receive: (field: CsvParseField) -> Unit
) {
    val machine = CsvStateMachine(
        quoteChar = context.quoteChar,
        delimiter = context.delimiter,
        escapeChar = context.escapeChar
    )
    val sb = StringBuilder()
    var offset: Long = 0
    val field = TextField(0, sb)
    while (true) {
        val ci = bufferedReader.read()
        if (ci == -1) break
        ++offset
        val ch = ci.toChar()
        when (val result = machine.read(ch, sb)) {
            Continue -> {
            }
            EolineContinue -> {
                field.startOffset = offset
                sb.clear()
                receive(EolineField)
            }
            Eofield -> {
                receive(field)
                field.startOffset = offset
                sb.clear()
            }
            Eol -> {
                receive(field)
                field.startOffset = offset
                sb.clear()
                receive(EolineField)
            }
            else -> error("$result")
        }
    }
    when (val result = machine.flush()) {
        Continue -> {
        }
        Eofield -> receive(field)
        else -> error("$result")
    }

}

/**
 * Parse a [String] in CSV format.
 */
internal fun parseCsv(
    text: String,
    context: CsvParseContext = CsvParseContext()
): List<List<String>> {
    val allLines = mutableListOf<List<String>>()
    val currentLine = mutableListOf<String>()
    parseCsv(StringReader(text), context) { field ->
        when (field) {
            is TextField -> currentLine.add(field.content.trim().toString())
            is EolineField -> {
                allLines.add(currentLine.toList())
                currentLine.clear()
            }
        }
    }
    if (currentLine.isNotEmpty()) {
        allLines.add(currentLine.toList())
    }
    return allLines
}