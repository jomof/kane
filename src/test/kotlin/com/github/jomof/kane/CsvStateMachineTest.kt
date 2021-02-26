package com.github.jomof.kane

import com.github.jomof.kane.CsvStateMachineTest.ParseField.EolineField
import com.github.jomof.kane.CsvStateMachineTest.ParseField.TextField
import com.github.jomof.kane.impl.csv.CsvStateMachine
import com.github.jomof.kane.impl.csv.CsvStateMachine.ReadResult.*
import org.junit.Test
import java.io.*

class CsvStateMachineTest {

    sealed class ParseField {
        data class TextField(var startOffset: Long, var content: StringBuilder) : ParseField()
        object EolineField : ParseField()
    }

    private fun parse(
        bufferedReader: Reader,
        receive: (field: ParseField) -> Unit
    ) {
        val machine = CsvStateMachine()
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

    private fun parse(text: String): List<List<String>> {
        val allLines = mutableListOf<List<String>>()
        val currentLine = mutableListOf<String>()
        parse(StringReader(text)) { field ->
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

    @Test
    fun basics() {
        parse("").assertString("[]")
        parse("a").assertString("[[a]]")
        parse("a,b").assertString("[[a, b]]")
        parse("a\nb").assertString("[[a], [b]]")
        parse("a,b\nc,d").assertString("[[a, b], [c, d]]")
        parse("\"a\",b\nc,d").assertString("[[a, b], [c, d]]")
        parse("\"a,b\"\nc,d").assertString("[[a,b], [c, d]]")
    }

    @Test
    fun `simple quote`() {
        parse("\"a\",b").assertString("[[a, b]]")
    }

    @Test
    fun `delimiter in quote`() {
        parse("\"a,b\"").assertString("[[a,b]]")
    }

    @Test
    fun `quote then another empty line`() {
        parse("\"a\"\n").assertString("[[a]]")
    }

    @Test
    fun `quote then another line`() {
        parse("\"a\"\nb").assertString("[[a], [b]]")
    }

    @Test
    fun `just a comma`() {
        parse(",").assertString("[[, ]]")
    }

    @Test
    fun `a comma, a line, and another comma`() {
        parse(",\n,").assertString("[[, ], [, ]]")
    }

    @Test
    fun `cr-lf`() {
        parse("a\r\nb").assertString("[[a], [b]]")
    }

    @Test
    fun `cr-cr`() {
        parse("a\r\rb") // Just check that it shouldn't crash
    }

    @Test
    fun `space before quote`() {
        parse(" \"a\"").assertString("[[a]]")
    }

    @Test
    fun `space before unquoted`() {
        parse(" a").assertString("[[a]]")
    }

    @Test
    fun `space after unquoted`() {
        parse("a ").assertString("[[a]]")
    }

    @Test
    fun `space in unquoted field`() {
        parse("a b").assertString("[[a b]]")
    }

    @Test
    fun `space in quoted field`() {
        parse("\"a b\"").assertString("[[a b]]")
    }

    @Test
    fun `read large file bytes`() {
        repeat(1) {
            val offsets = mutableListOf<Long>()
            var lastWasEol = true
            parse(BufferedReader(FileReader("data/covid.csv"))) { field ->
                when (field) {
                    is TextField -> if (lastWasEol) {
                        lastWasEol = false
                        offsets.add(field.startOffset)
                    }
                    is EolineField -> lastWasEol = true
                }
            }
            println(offsets.size)
        }
    }
}