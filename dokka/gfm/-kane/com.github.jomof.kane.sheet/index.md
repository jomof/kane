//[Kane](../index.md)/[com.github.jomof.kane.sheet](index.md)



# Package com.github.jomof.kane.sheet  


## Types  
  
|  Name|  Summary| 
|---|---|
| [AdmissibleDataType](-admissible-data-type/index.md)| [jvm]  <br>Content  <br>interface [AdmissibleDataType](-admissible-data-type/index.md)<[T](-admissible-data-type/index.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)>  <br><br><br>
| [Cells](-cells/index.md)| [jvm]  <br>Content  <br>data class [Cells](-cells/index.md)(**map**: [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html), [Expr](../com.github.jomof.kane/-expr/index.md)>)  <br><br><br>
| [CoerceScalar](-coerce-scalar/index.md)| [jvm]  <br>Brief description  <br><br><br>Turn an expression into a ScalarExpr based on context.<br><br>  <br>Content  <br>data class [CoerceScalar](-coerce-scalar/index.md)(**value**: [Expr](../com.github.jomof.kane/-expr/index.md)) : [ScalarExpr](../com.github.jomof.kane/-scalar-expr/index.md)  <br><br><br>
| [ColumnDescriptor](-column-descriptor/index.md)| [jvm]  <br>Content  <br>data class [ColumnDescriptor](-column-descriptor/index.md)(**name**: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html), **type**: [AdmissibleDataType](-admissible-data-type/index.md)<*>?)  <br><br><br>
| [ColumnInfo](-column-info/index.md)| [jvm]  <br>Content  <br>data class [ColumnInfo](-column-info/index.md)(**name**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **typeInfo**: [AdmissibleDataType](-admissible-data-type/index.md)<*>, **minWidth**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), **maxWidth**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html))  <br><br><br>
| [DataTypeAnalysis](-data-type-analysis/index.md)| [jvm]  <br>Content  <br>data class [DataTypeAnalysis](-data-type-analysis/index.md)(**columns**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), **rows**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), **columnInfos**: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[ColumnInfo](-column-info/index.md)>)  <br><br><br>
| [GroupBy](-group-by/index.md)| [jvm]  <br>Brief description  <br><br><br>Conceptually, a GroupBy is a dictionary from key to sheet. It initially consists of [sheet](-group-by/index.md#com.github.jomof.kane.sheet/GroupBy/sheet/#/PointingToDeclaration/) which is the data source and selector which is used to build a key from a row.<br><br>  <br>Content  <br>class [GroupBy](-group-by/index.md)(**sheet**: [Sheet](-sheet/index.md), **keySelector**: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[NamedExpr](../com.github.jomof.kane/-named-expr/index.md)>) : [Expr](../com.github.jomof.kane/-expr/index.md)  <br><br><br>
| [NamedSheetRangeExpr](-named-sheet-range-expr/index.md)| [jvm]  <br>Content  <br>data class [NamedSheetRangeExpr](-named-sheet-range-expr/index.md)(**name**: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html), **range**: [SheetRangeExpr](-sheet-range-expr/index.md)) : [UntypedScalar](../com.github.jomof.kane/-untyped-scalar/index.md), [NamedExpr](../com.github.jomof.kane/-named-expr/index.md)  <br><br><br>
| [RangeExprProvider](-range-expr-provider/index.md)| [jvm]  <br>Content  <br>interface [RangeExprProvider](-range-expr-provider/index.md)  <br><br><br>
| [RowDescriptor](-row-descriptor/index.md)| [jvm]  <br>Content  <br>data class [RowDescriptor](-row-descriptor/index.md)(**name**: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Expr](../com.github.jomof.kane/-expr/index.md)>)  <br><br><br>
| [RowView](-row-view/index.md)| [jvm]  <br>Content  <br>class [RowView](-row-view/index.md)(**sheet**: [Sheet](-sheet/index.md), **row**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)) : [RangeExprProvider](-range-expr-provider/index.md)  <br><br><br>
| [Sheet](-sheet/index.md)| [jvm]  <br>Content  <br>interface [Sheet](-sheet/index.md) : [Expr](../com.github.jomof.kane/-expr/index.md)  <br><br><br>
| [SheetBuilder](-sheet-builder/index.md)| [jvm]  <br>Content  <br>interface [SheetBuilder](-sheet-builder/index.md)  <br><br><br>
| [SheetBuilderImpl](-sheet-builder-impl/index.md)| [jvm]  <br>Content  <br>class [SheetBuilderImpl](-sheet-builder-impl/index.md)(**columnDescriptors**: [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), [ColumnDescriptor](-column-descriptor/index.md)>, **rowDescriptors**: [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), [RowDescriptor](-row-descriptor/index.md)>, **sheetDescriptor**: [SheetDescriptor](-sheet-descriptor/index.md), **added**: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[NamedExpr](../com.github.jomof.kane/-named-expr/index.md)>) : [SheetBuilder](-sheet-builder/index.md)  <br><br><br>
| [SheetBuilderRange](-sheet-builder-range/index.md)| [jvm]  <br>Content  <br>data class [SheetBuilderRange](-sheet-builder-range/index.md)(**builder**: [SheetBuilder](-sheet-builder/index.md), **range**: [SheetRange](../com.github.jomof.kane/-sheet-range/index.md)) : [UntypedScalar](../com.github.jomof.kane/-untyped-scalar/index.md)  <br><br><br>
| [SheetDescriptor](-sheet-descriptor/index.md)| [jvm]  <br>Content  <br>data class [SheetDescriptor](-sheet-descriptor/index.md)(**limitOutputLines**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), **showColumnTags**: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html))  <br><br><br>
| [SheetRangeExpr](-sheet-range-expr/index.md)| [jvm]  <br>Content  <br>data class [SheetRangeExpr](-sheet-range-expr/index.md)(**range**: [SheetRange](../com.github.jomof.kane/-sheet-range/index.md)) : [UntypedScalar](../com.github.jomof.kane/-untyped-scalar/index.md)  <br><br><br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| [aggregate](aggregate.md)| [jvm]  <br>Content  <br>fun [GroupBy](-group-by/index.md).[aggregate](aggregate.md)(vararg functions: [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)<Out [AlgebraicUnaryScalarStatisticFunction](../com.github.jomof.kane.functions/-algebraic-unary-scalar-statistic-function/index.md)>): [Sheet](-sheet/index.md)  <br>fun [GroupBy](-group-by/index.md).[aggregate](aggregate.md)(selector: [SheetBuilder](-sheet-builder/index.md).() -> [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[NamedExpr](../com.github.jomof.kane/-named-expr/index.md)>): [Sheet](-sheet/index.md)  <br><br><br>
| [analyzeDataType](analyze-data-type.md)| [jvm]  <br>Content  <br>fun [analyzeDataType](analyze-data-type.md)(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [AdmissibleDataType](-admissible-data-type/index.md)<*>  <br><br><br>
| [analyzeDataTypes](analyze-data-types.md)| [jvm]  <br>Content  <br>fun [analyzeDataTypes](analyze-data-types.md)(data: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>>): [DataTypeAnalysis](-data-type-analysis/index.md)  <br>fun [analyzeDataTypes](analyze-data-types.md)(columnNames: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>, data: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>>): [DataTypeAnalysis](-data-type-analysis/index.md)  <br><br><br>
| [columnName](column-name.md)| [jvm]  <br>Content  <br>fun [Sheet](-sheet/index.md).[columnName](column-name.md)(column: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [columnType](column-type.md)| [jvm]  <br>Content  <br>fun [Sheet](-sheet/index.md).[columnType](column-type.md)(column: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [AdmissibleDataType](-admissible-data-type/index.md)<*>  <br><br><br>
| [convertCellNamesToUpperCase](convert-cell-names-to-upper-case.md)| [jvm]  <br>Content  <br>fun [Expr](../com.github.jomof.kane/-expr/index.md).[convertCellNamesToUpperCase](convert-cell-names-to-upper-case.md)(): [Expr](../com.github.jomof.kane/-expr/index.md)  <br><br><br>
| [convertNamedColumnsToColumnRanges](convert-named-columns-to-column-ranges.md)| [jvm]  <br>Content  <br>fun [SheetBuilderImpl](-sheet-builder-impl/index.md).[convertNamedColumnsToColumnRanges](convert-named-columns-to-column-ranges.md)(cells: [Cells](-cells/index.md)): [Cells](-cells/index.md)  <br><br><br>
| [describe](describe.md)| [jvm]  <br>Brief description  <br><br><br>Return a new sheet with data summarized into statistics<br><br>  <br>Content  <br>fun [Expr](../com.github.jomof.kane/-expr/index.md).[describe](describe.md)(): [Sheet](-sheet/index.md)  <br>fun [NamedMatrix](../com.github.jomof.kane/-named-matrix/index.md).[describe](describe.md)(): [Sheet](-sheet/index.md)  <br>fun [MatrixExpr](../com.github.jomof.kane/-matrix-expr/index.md).[describe](describe.md)(name: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)): [Sheet](-sheet/index.md)  <br><br><br>[jvm]  <br>Brief description  <br><br><br>Return a new sheet with columns summarized into statistics<br><br>  <br>Content  <br>fun [GroupBy](-group-by/index.md).[describe](describe.md)(): [Sheet](-sheet/index.md)  <br>fun [Sheet](-sheet/index.md).[describe](describe.md)(): [Sheet](-sheet/index.md)  <br><br><br>
| [expandNamedCells](expand-named-cells.md)| [jvm]  <br>Content  <br>fun [Expr](../com.github.jomof.kane/-expr/index.md).[expandNamedCells](expand-named-cells.md)(lookup: [Cells](-cells/index.md)): [Expr](../com.github.jomof.kane/-expr/index.md)  <br><br><br>
| [expandUnaryOperations](expand-unary-operations.md)| [jvm]  <br>Content  <br>fun [Expr](../com.github.jomof.kane/-expr/index.md).[expandUnaryOperations](expand-unary-operations.md)(): [Expr](../com.github.jomof.kane/-expr/index.md)  <br><br><br>
| [extractScalarizedMatrixElement](extract-scalarized-matrix-element.md)| [jvm]  <br>Content  <br>fun [extractScalarizedMatrixElement](extract-scalarized-matrix-element.md)(matrix: [MatrixExpr](../com.github.jomof.kane/-matrix-expr/index.md), coordinate: [Coordinate](../com.github.jomof.kane/-coordinate/index.md), namedColumns: [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)>): [ScalarExpr](../com.github.jomof.kane/-scalar-expr/index.md)  <br><br><br>
| [fillna](fillna.md)| [jvm]  <br>Content  <br>fun [Expr](../com.github.jomof.kane/-expr/index.md).[fillna](fillna.md)(value: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)): [Expr](../com.github.jomof.kane/-expr/index.md)  <br><br><br>[jvm]  <br>Brief description  <br><br><br>Map cells of a sheet that are coercible to double.<br><br>  <br>Content  <br>fun [Sheet](-sheet/index.md).[fillna](fillna.md)(value: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)): [Sheet](-sheet/index.md)  <br><br><br>
| [filterRows](filter-rows.md)| [jvm]  <br>Brief description  <br><br><br>Filter rows of a Sheet with a predicate function.<br><br>  <br>Content  <br>fun [Sheet](-sheet/index.md).[filterRows](filter-rows.md)(predicate: ([RowView](-row-view/index.md)) -> [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Sheet](-sheet/index.md)  <br><br><br>
| [freeze](freeze.md)| [jvm]  <br>Brief description  <br><br><br>Convert relative cell references to moveable. current is the cell that this formula occupies.<br><br>  <br>Content  <br>fun [Expr](../com.github.jomof.kane/-expr/index.md).[freeze](freeze.md)(current: [Coordinate](../com.github.jomof.kane/-coordinate/index.md)): [Expr](../com.github.jomof.kane/-expr/index.md)  <br><br><br>
| [get](get.md)| [jvm]  <br>Brief description  <br><br><br>Return a subsection of a sheet<br><br>  <br>Content  <br>operator fun [Sheet](-sheet/index.md).[get](get.md)(vararg ranges: [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)<Out [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>): [Expr](../com.github.jomof.kane/-expr/index.md)  <br><br><br>[jvm]  <br>Content  <br>operator fun [Sheet](-sheet/index.md).[get](get.md)(column: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), row: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Expr](../com.github.jomof.kane/-expr/index.md)  <br><br><br>
| [groupBy](group-by.md)| [jvm]  <br>Content  <br>fun [Sheet](-sheet/index.md).[groupBy](group-by.md)(vararg columns: [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)<Out [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>): [GroupBy](-group-by/index.md)  <br><br><br>
| [groupOf](group-of.md)| [jvm]  <br>Content  <br>fun [Sheet](-sheet/index.md).[groupOf](group-of.md)(selector: [SheetBuilder](-sheet-builder/index.md).() -> [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[NamedExpr](../com.github.jomof.kane/-named-expr/index.md)>): [GroupBy](-group-by/index.md)  <br><br><br>
| [head](head.md)| [jvm]  <br>Brief description  <br><br><br>Retrieve the first count elements of a sheet.<br><br>  <br>Content  <br>fun [Sheet](-sheet/index.md).[head](head.md)(count: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Sheet](-sheet/index.md)  <br><br><br>
| [mapDoubles](map-doubles.md)| [jvm]  <br>Content  <br>fun [Expr](../com.github.jomof.kane/-expr/index.md).[mapDoubles](map-doubles.md)(translate: ([Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)) -> [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)): [Expr](../com.github.jomof.kane/-expr/index.md)  <br><br><br>[jvm]  <br>Brief description  <br><br><br>Map cells of a sheet that are coercible to double.<br><br>  <br>Content  <br>fun [Sheet](-sheet/index.md).[mapDoubles](map-doubles.md)(translate: ([Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)) -> [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)): [Sheet](-sheet/index.md)  <br><br><br>
| [minimize](minimize.md)| [jvm]  <br>Content  <br>fun [Sheet](-sheet/index.md).[minimize](minimize.md)(target: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), variables: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>, learningRate: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)): [Sheet](-sheet/index.md)  <br><br><br>
| [ordinalColumns](ordinal-columns.md)| [jvm]  <br>Brief description  <br><br><br>Return a new sheet with just the ordinal columns from the list of columns<br><br>  <br>Content  <br>fun [Sheet](-sheet/index.md).[ordinalColumns](ordinal-columns.md)(elements: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)>): [Sheet](-sheet/index.md)  <br><br><br>
| [ordinalRows](ordinal-rows.md)| [jvm]  <br>Brief description  <br><br><br>Return the requested rows. Elements are ordinal row number not row name.<br><br>  <br>Content  <br>fun [Sheet](-sheet/index.md).[ordinalRows](ordinal-rows.md)(vararg elements: [IntArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int-array/index.html)): [Sheet](-sheet/index.md)  <br>fun [Sheet](-sheet/index.md).[ordinalRows](ordinal-rows.md)(elements: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)>): [Sheet](-sheet/index.md)  <br>fun [Sheet](-sheet/index.md).[ordinalRows](ordinal-rows.md)(range: [IntRange](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.ranges/-int-range/index.html)): [Sheet](-sheet/index.md)  <br><br><br>
| [parseCsv](parse-csv.md)| [jvm]  <br>Brief description  <br><br><br>Parse CSV from string in data parameter.<br><br>  <br>Content  <br>fun [parseCsv](parse-csv.md)(data: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), names: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>, sample: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), keep: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>, charset: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), quoteChar: [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html), delimiter: [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html), escapeChar: [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html), skipEmptyLine: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), skipMissMatchedRow: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Sheet](-sheet/index.md)  <br><br><br>
| [parseToExpr](parse-to-expr.md)| [jvm]  <br>Content  <br>fun <[T](parse-to-expr.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)> [AdmissibleDataType](-admissible-data-type/index.md)<[T](parse-to-expr.md)>.[parseToExpr](parse-to-expr.md)(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Expr](../com.github.jomof.kane/-expr/index.md)  <br><br><br>
| [readCsv](read-csv.md)| [jvm]  <br>Brief description  <br><br><br>Read CSV from a file.<br><br>  <br>Content  <br>fun [readCsv](read-csv.md)(csv: [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html), names: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>, sample: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), keep: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>, charset: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), quoteChar: [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html), delimiter: [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html), escapeChar: [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html), skipEmptyLine: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), skipMissMatchedRow: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Sheet](-sheet/index.md)  <br>fun [readCsv](read-csv.md)(csv: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), names: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>, sample: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), keep: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>, charset: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), quoteChar: [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html), delimiter: [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html), escapeChar: [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html), skipEmptyLine: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), skipMissMatchedRow: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Sheet](-sheet/index.md)  <br><br><br>
| [removeBuilderPrivateExpressions](remove-builder-private-expressions.md)| [jvm]  <br>Content  <br>fun [Sheet](-sheet/index.md).[removeBuilderPrivateExpressions](remove-builder-private-expressions.md)(): [Sheet](-sheet/index.md)  <br>fun [removeBuilderPrivateExpressions](remove-builder-private-expressions.md)(cells: [Cells](-cells/index.md)): [Cells](-cells/index.md)  <br><br><br>
| [render](render.md)| [jvm]  <br>Content  <br>fun [Sheet](-sheet/index.md).[render](render.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [replaceNamesWithCellReferences](replace-names-with-cell-references.md)| [jvm]  <br>Content  <br>fun [Expr](../com.github.jomof.kane/-expr/index.md).[replaceNamesWithCellReferences](replace-names-with-cell-references.md)(excluding: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)): [Expr](../com.github.jomof.kane/-expr/index.md)  <br><br><br>
| [replaceRelativeCellReferences](replace-relative-cell-references.md)| [jvm]  <br>Content  <br>fun [Expr](../com.github.jomof.kane/-expr/index.md).[replaceRelativeCellReferences](replace-relative-cell-references.md)(coordinate: [Coordinate](../com.github.jomof.kane/-coordinate/index.md)): [Expr](../com.github.jomof.kane/-expr/index.md)  <br><br><br>
| [rowName](row-name.md)| [jvm]  <br>Content  <br>fun [Sheet](-sheet/index.md).[rowName](row-name.md)(row: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [sample](sample.md)| [jvm]  <br>Brief description  <br><br><br>Retrieve the last count elements of a sheet.<br><br>  <br>Content  <br>fun [Sheet](-sheet/index.md).[sample](sample.md)(count: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), random: [Random](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.random/-random/index.html)): [Sheet](-sheet/index.md)  <br><br><br>
| [scalarizeRanges](scalarize-ranges.md)| [jvm]  <br>Content  <br>fun [SheetBuilderImpl](-sheet-builder-impl/index.md).[scalarizeRanges](scalarize-ranges.md)(cells: [Cells](-cells/index.md)): [Cells](-cells/index.md)  <br><br><br>
| [sheetOf](sheet-of.md)| [jvm]  <br>Content  <br>fun [sheetOf](sheet-of.md)(init: [SheetBuilder](-sheet-builder/index.md).() -> [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[NamedExpr](../com.github.jomof.kane/-named-expr/index.md)>): [Sheet](-sheet/index.md)  <br><br><br>
| [sheetOfCsv](sheet-of-csv.md)| [jvm]  <br>Brief description  <br><br><br>Construct a sheet by parsing provided text.<br><br>  <br>Content  <br>fun [sheetOfCsv](sheet-of-csv.md)(names: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>, sample: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), keep: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>, quoteChar: [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html), delimiter: [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html), escapeChar: [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html), skipEmptyLine: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), skipMissMatchedRow: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), init: () -> [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Sheet](-sheet/index.md)  <br><br><br>
| [slideScalarizedCellsRewritingVisitor](slide-scalarized-cells-rewriting-visitor.md)| [jvm]  <br>Content  <br>fun [Expr](../com.github.jomof.kane/-expr/index.md).[slideScalarizedCellsRewritingVisitor](slide-scalarized-cells-rewriting-visitor.md)(upperLeft: [Coordinate](../com.github.jomof.kane/-coordinate/index.md), offset: [Coordinate](../com.github.jomof.kane/-coordinate/index.md)): [Expr](../com.github.jomof.kane/-expr/index.md)  <br><br><br>
| [tail](tail.md)| [jvm]  <br>Brief description  <br><br><br>Retrieve the last count elements of a sheet.<br><br>  <br>Content  <br>fun [Sheet](-sheet/index.md).[tail](tail.md)(count: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Sheet](-sheet/index.md)  <br><br><br>
| [toCells](to-cells.md)| [jvm]  <br>Content  <br>fun [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)<[Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html), [Expr](../com.github.jomof.kane/-expr/index.md)>>.[toCells](to-cells.md)(): [Cells](-cells/index.md)  <br>fun [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html), [Expr](../com.github.jomof.kane/-expr/index.md)>.[toCells](to-cells.md)(): [Cells](-cells/index.md)  <br><br><br>
| [unfreeze](unfreeze.md)| [jvm]  <br>Brief description  <br><br><br>Convert moveable cell references to relative. This is so that rows and columns can be moved, added, and deleted before refreezing. current is the cell that this formula occupies.<br><br>  <br>Content  <br>fun [Expr](../com.github.jomof.kane/-expr/index.md).[unfreeze](unfreeze.md)(current: [Coordinate](../com.github.jomof.kane/-coordinate/index.md)): [Expr](../com.github.jomof.kane/-expr/index.md)  <br><br><br>
| [writeCsv](write-csv.md)| [jvm]  <br>Brief description  <br><br><br>Write Sheet to a file named csv<br><br>  <br>Content  <br>fun [Sheet](-sheet/index.md).[writeCsv](write-csv.md)(csv: [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html))  <br>fun [Sheet](-sheet/index.md).[writeCsv](write-csv.md)(csv: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html))  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [html](index.md#com.github.jomof.kane.sheet//html/com.github.jomof.kane.sheet.Sheet#/PointingToDeclaration/)|  [jvm] <br><br>Render the sheet as HTML<br><br>val [Sheet](-sheet/index.md).[html](index.md#com.github.jomof.kane.sheet//html/com.github.jomof.kane.sheet.Sheet#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [possibleDataFormats](index.md#com.github.jomof.kane.sheet//possibleDataFormats/#/PointingToDeclaration/)|  [jvm] val [possibleDataFormats](index.md#com.github.jomof.kane.sheet//possibleDataFormats/#/PointingToDeclaration/): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[AdmissibleDataType](-admissible-data-type/index.md)<*>>   <br>
| [types](index.md#com.github.jomof.kane.sheet//types/com.github.jomof.kane.sheet.Sheet#/PointingToDeclaration/)|  [jvm] <br><br>Return the column types of this sheet.<br><br>val [Sheet](-sheet/index.md).[types](index.md#com.github.jomof.kane.sheet//types/com.github.jomof.kane.sheet.Sheet#/PointingToDeclaration/): [Sheet](-sheet/index.md)   <br>
