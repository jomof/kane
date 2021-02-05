//[Kane](../index.md)/[com.github.jomof.kane.impl.sheet](index.md)/[parseCsv](parse-csv.md)



# parseCsv  
[jvm]  
Content  
fun [parseCsv](parse-csv.md)(data: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), names: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)> = listOf(), sample: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) = 1.0, keep: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)> = listOf(), charset: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = "UTF-8", quoteChar: [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html) = '"', delimiter: [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html) = ',', escapeChar: [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html) = '"', skipEmptyLine: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = false, skipMissMatchedRow: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = false): [Sheet](-sheet/index.md)  
More info  


Parse CSV from string in data parameter.

  



