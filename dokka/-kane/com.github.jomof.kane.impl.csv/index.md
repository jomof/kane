//[Kane](../index.md)/[com.github.jomof.kane.impl.csv](index.md)



# Package com.github.jomof.kane.impl.csv  


## Types  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl.csv/CsvParseContext///PointingToDeclaration/"></a>[CsvParseContext](-csv-parse-context/index.md)| <a name="com.github.jomof.kane.impl.csv/CsvParseContext///PointingToDeclaration/"></a>[jvm]  <br>Content  <br>data class [CsvParseContext](-csv-parse-context/index.md)(**headerHasColumnNames**: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), **columnNames**: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>, **linesForTypeAnalysis**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), **treatAsNaN**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?, **quoteChar**: [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html), **delimiter**: [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html), **escapeChar**: [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html))  <br><br><br>
| <a name="com.github.jomof.kane.impl.csv/ReadCsvRowSequence///PointingToDeclaration/"></a>[ReadCsvRowSequence](-read-csv-row-sequence/index.md)| <a name="com.github.jomof.kane.impl.csv/ReadCsvRowSequence///PointingToDeclaration/"></a>[jvm]  <br>Content  <br>data class [ReadCsvRowSequence](-read-csv-row-sequence/index.md)(**file**: [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html), **parseContext**: [CsvParseContext](-csv-parse-context/index.md)) : [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)<[Row](../com.github.jomof.kane/-row/index.md)> , [CountableRows](../com.github.jomof.kane/-countable-rows/index.md), [CountableColumns](../com.github.jomof.kane/-countable-columns/index.md)  <br><br><br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl.csv//readCsvRowSequence/#java.io.File#com.github.jomof.kane.impl.csv.CsvParseContext/PointingToDeclaration/"></a>[readCsvRowSequence](read-csv-row-sequence.md)| <a name="com.github.jomof.kane.impl.csv//readCsvRowSequence/#java.io.File#com.github.jomof.kane.impl.csv.CsvParseContext/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [readCsvRowSequence](read-csv-row-sequence.md)(file: [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html), parseContext: [CsvParseContext](-csv-parse-context/index.md) = CsvParseContext()): [ReadCsvRowSequence](-read-csv-row-sequence/index.md)  <br><br><br>

