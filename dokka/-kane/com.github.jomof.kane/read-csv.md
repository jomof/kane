//[Kane](../index.md)/[com.github.jomof.kane](index.md)/[readCsv](read-csv.md)



# readCsv  
[jvm]  
Content  
fun [readCsv](read-csv.md)(csv: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), names: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)> = listOf(), keep: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)> = listOf(), charset: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = "UTF-8", quoteChar: [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html) = '"', delimiter: [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html) = ',', escapeChar: [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html) = '"', skipEmptyLine: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = false, skipMissMatchedRow: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = false): [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)<[Row](-row/index.md)>  
fun [readCsv](read-csv.md)(csv: [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html), names: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)> = listOf(), keep: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)> = listOf(), charset: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = "UTF-8", quoteChar: [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html) = '"', delimiter: [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html) = ',', escapeChar: [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html) = '"', skipEmptyLine: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = false, skipMissMatchedRow: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = false): [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)<[Row](-row/index.md)>  
More info  


Read CSV from a file.

  



