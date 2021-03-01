//[Kane](../index.md)/[com.github.jomof.kane](index.md)/[readCsv](read-csv.md)



# readCsv  
[jvm]  
Content  
fun [readCsv](read-csv.md)(csv: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), delimiter: [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html) = ',', names: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)> = listOf()): [ReadCsvFileRowSequence](../com.github.jomof.kane.impl/-read-csv-file-row-sequence/index.md)  
fun [readCsv](read-csv.md)(csv: [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html), parseContext: [CsvParseContext](../com.github.jomof.kane.impl.csv/-csv-parse-context/index.md) = CsvParseContext()): [ReadCsvFileRowSequence](../com.github.jomof.kane.impl/-read-csv-file-row-sequence/index.md)  
fun [readCsv](read-csv.md)(csv: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), parseContext: [CsvParseContext](../com.github.jomof.kane.impl.csv/-csv-parse-context/index.md) = CsvParseContext()): [ReadCsvFileRowSequence](../com.github.jomof.kane.impl/-read-csv-file-row-sequence/index.md)  
More info  


Read CSV from a file.

  



