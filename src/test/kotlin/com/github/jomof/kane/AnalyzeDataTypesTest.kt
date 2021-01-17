package com.github.jomof.kane

import com.github.jomof.kane.sheet.analyzeDataTypes
import org.junit.Test

class AnalyzeDataTypesTest {

    fun analyzeType(vararg text : String) = analyzeDataTypes(text.toList().map { mapOf("column" to it) }).columnInfos[0].typeInfo

    @Test
    fun basic() {
        analyzeType("$0.01", "$0.02").assertString("currency (\$1,000.12)")
        analyzeType("$1", "$2").assertString("currency (\$1,000)")
        analyzeType("$1,000", "$2").assertString("currency (\$1,000)")
        analyzeType("$1,000", "$2.00").assertString("currency (\$1,000.12)")
        analyzeType("0.1", "0.2").assertString("double")
        analyzeType("1979-03-31 00:00:00", "1979-02-31 00:00:00").assertString("yyyy-MM-dd HH:mm:ss")
        analyzeType("1979-03-31", "1979-02-31").assertString("yyyy-MM-dd")
        analyzeType("1979-02-31 00:00:00", "1979-03-31").assertString("yyyy-MM-dd")
    }
}