package com.github.jomof.kane.impl.csv

import com.github.jomof.kane.impl.csv.CsvStateMachine.ReadResult.*
import com.github.jomof.kane.impl.csv.CsvStateMachine.State.*

internal class CsvStateMachine(
    private val quoteChar: Char = '\"',
    private val delimiter: Char = ',',
    private val escapeChar: Char = '\\'
) {
    private val BOM = '\uFEFF'

    enum class ReadResult {
        // Caller should supply the next char.
        Continue,

        // Caller should supply the next char and register that a new line was seen.
        EolineContinue,

        // End of field means 'sb' contains a field value. Caller is responsible for clearing sb
        Eofield,

        // End of line means this line is ending. Also, 'sb' contains a field value.
        Eol
    }

    private enum class State {
        StartOfFile,
        StartOfField,
        InsideQuotedField,
        LineFeed,
        InsideField,
        AfterEndQuote,
        AfterDelimiter,
        EscapingInsideField,
        EscapingInsideQuotedField
    }

    private var state: State = StartOfFile

    /**
     * Reads the next character
     */
    fun read(ch: Char, sb: StringBuilder): ReadResult {
        when (state) {
            StartOfFile -> {
                if (Character.isWhitespace(ch)) return Continue
                when (ch) {
                    '\r', '\n', '\u2028', '\u2029', '\u0085' -> return Continue
                    else -> state = StartOfField
                }
            }
        }
        return when (state) {
            StartOfFile,
            LineFeed,
            AfterDelimiter,
            StartOfField -> when (ch) {
                BOM -> Continue
                ' ' -> Continue
                quoteChar -> {
                    state = InsideQuotedField
                    Continue
                }
                delimiter -> {
                    state = AfterDelimiter
                    Eofield
                }
                '\n', '\u2028', '\u2029', '\u0085' -> {
                    if (state == StartOfFile) Continue
                    else {
                        state = StartOfField
                        Eol
                    }
                }
                '\r' -> {
                    if (state == StartOfFile) Continue
                    else {
                        state = LineFeed
                        Continue
                    }
                }
                else -> {
                    sb.append(ch)
                    state = InsideField
                    Continue
                }
            }
            EscapingInsideField -> {
                sb.append(ch)
                state = InsideField
                Continue
            }
            EscapingInsideQuotedField -> {
                sb.append(ch)
                state = InsideQuotedField
                Continue
            }
            InsideField -> when (ch) {
                quoteChar -> {
                    // Odd case embedded quote character
                    Continue
                }
                escapeChar -> {
                    state = EscapingInsideField
                    Continue
                }
                delimiter -> {
                    state = StartOfField
                    Eofield
                }
                '\n', '\u2028', '\u2029', '\u0085' -> {
                    state = StartOfField
                    Eol
                }
                '\r' -> {
                    state = LineFeed
                    Continue
                }
                else -> {
                    sb.append(ch)
                    state = InsideField
                    Continue
                }
            }
            InsideQuotedField -> when (ch) {
                quoteChar -> {
                    state = AfterEndQuote
                    Continue
                }
                escapeChar -> {
                    state = EscapingInsideQuotedField
                    Continue
                }
                else -> {
                    sb.append(ch)
                    state = InsideQuotedField
                    Continue
                }
            }
            AfterEndQuote -> when (ch) {
                delimiter -> {
                    state = StartOfField
                    Eofield
                }
                '\n', '\u2028', '\u2029', '\u0085' -> {
                    state = StartOfField
                    Eol
                }
                '\r' -> {
                    state = LineFeed
                    Eol
                }
                quoteChar -> {
                    // Double quote seen
                    state = InsideQuotedField
                    sb.append("\"")
                    Continue
                }
                else -> Continue
            }
        }
    }

    fun flush(): ReadResult {
        return when (state) {
            StartOfFile -> Continue
            StartOfField -> Continue
            AfterEndQuote -> Eofield
            AfterDelimiter -> Eofield
            InsideField -> Eofield
            InsideQuotedField -> Eofield
            LineFeed -> Continue
            EscapingInsideField -> Continue
            EscapingInsideQuotedField -> Continue
        }
    }
}