//[Kane](../index.md)/[com.github.jomof.kane](index.md)/[sheetOfCsv](sheet-of-csv.md)



# sheetOfCsv  
[jvm]  
Content  
fun [sheetOfCsv](sheet-of-csv.md)(text: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), names: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)> = listOf(), quoteChar: [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html) = '"', delimiter: [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html) = ',', escapeChar: [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html) = '"', skipEmptyLine: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = false, skipMissMatchedRow: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = false): [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)<[Row](-row/index.md)>  
More info  


Construct a sheet by parsing provided text.

  



