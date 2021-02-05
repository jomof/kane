//[Kane](../../index.md)/[com.github.jomof.kane.sheet](../index.md)/[SheetBuilderImpl](index.md)



# SheetBuilderImpl  
 [jvm] class [SheetBuilderImpl](index.md)(**columnDescriptors**: [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), [ColumnDescriptor](../-column-descriptor/index.md)>, **rowDescriptors**: [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), [RowDescriptor](../-row-descriptor/index.md)>, **sheetDescriptor**: [SheetDescriptor](../-sheet-descriptor/index.md), **added**: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[NamedExpr](../../com.github.jomof.kane/-named-expr/index.md)>) : [SheetBuilder](../-sheet-builder/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [build](build.md)| [jvm]  <br>Content  <br>fun [build](build.md)(): [Sheet](../-sheet/index.md)  <br><br><br>
| [cell](../-sheet-builder/cell.md)| [jvm]  <br>Content  <br>open override fun [cell](../-sheet-builder/cell.md)(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [SheetBuilderRange](../-sheet-builder-range/index.md)  <br><br><br>
| [column](column.md)| [jvm]  <br>Content  <br>open override fun [column](column.md)(range: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [SheetBuilderRange](../-sheet-builder-range/index.md)  <br>fun [column](column.md)(index: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), type: [AdmissibleDataType](../-admissible-data-type/index.md)<*>?)  <br><br><br>
| [down](../-sheet-builder/down.md)| [jvm]  <br>Content  <br>open override fun [down](../-sheet-builder/down.md)(offset: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [SheetBuilderRange](../-sheet-builder-range/index.md)  <br><br><br>
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [getImmediateNamedExprs](get-immediate-named-exprs.md)| [jvm]  <br>Content  <br>fun [getImmediateNamedExprs](get-immediate-named-exprs.md)(): [Cells](../-cells/index.md)  <br><br><br>
| [getValue](../-sheet-builder/get-value.md)| [jvm]  <br>Content  <br>open operator override fun [Number](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-number/index.html).[getValue](../-sheet-builder/get-value.md)(nothing: [Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html)?, property: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)<*>): [NamedScalar](../../com.github.jomof.kane/-named-scalar/index.md)  <br>open operator override fun [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html).[getValue](../-sheet-builder/get-value.md)(nothing: [Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html)?, property: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)<*>): [NamedExpr](../../com.github.jomof.kane/-named-expr/index.md)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [left](../-sheet-builder/left.md)| [jvm]  <br>Content  <br>open override fun [left](../-sheet-builder/left.md)(offset: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [SheetBuilderRange](../-sheet-builder-range/index.md)  <br><br><br>
| [nameColumn](name-column.md)| [jvm]  <br>Content  <br>open override fun [nameColumn](name-column.md)(column: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), name: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html))  <br><br><br>
| [nameRow](name-row.md)| [jvm]  <br>Content  <br>open override fun [nameRow](name-row.md)(row: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), name: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html))  <br>open override fun [nameRow](name-row.md)(row: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), name: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Expr](../../com.github.jomof.kane/-expr/index.md)>)  <br><br><br>
| [range](../-sheet-builder/range.md)| [jvm]  <br>Content  <br>open override fun [range](../-sheet-builder/range.md)(range: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [SheetBuilderRange](../-sheet-builder-range/index.md)  <br><br><br>
| [right](../-sheet-builder/right.md)| [jvm]  <br>Content  <br>open override fun [right](../-sheet-builder/right.md)(offset: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [SheetBuilderRange](../-sheet-builder-range/index.md)  <br><br><br>
| [set](set.md)| [jvm]  <br>Content  <br>fun [set](set.md)(cell: [Coordinate](../../com.github.jomof.kane/-coordinate/index.md), value: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html), type: [KaneType](../../com.github.jomof.kane.types/-kane-type/index.md)<*>)  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [up](../-sheet-builder/up.md)| [jvm]  <br>Content  <br>open override fun [up](../-sheet-builder/up.md)(offset: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [SheetBuilderRange](../-sheet-builder-range/index.md)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [down](index.md#com.github.jomof.kane.sheet/SheetBuilderImpl/down/#/PointingToDeclaration/)|  [jvm] open override val [down](index.md#com.github.jomof.kane.sheet/SheetBuilderImpl/down/#/PointingToDeclaration/): [SheetBuilderRange](../-sheet-builder-range/index.md)   <br>
| [left](index.md#com.github.jomof.kane.sheet/SheetBuilderImpl/left/#/PointingToDeclaration/)|  [jvm] open override val [left](index.md#com.github.jomof.kane.sheet/SheetBuilderImpl/left/#/PointingToDeclaration/): [SheetBuilderRange](../-sheet-builder-range/index.md)   <br>
| [right](index.md#com.github.jomof.kane.sheet/SheetBuilderImpl/right/#/PointingToDeclaration/)|  [jvm] open override val [right](index.md#com.github.jomof.kane.sheet/SheetBuilderImpl/right/#/PointingToDeclaration/): [SheetBuilderRange](../-sheet-builder-range/index.md)   <br>
| [sheetDescriptor](index.md#com.github.jomof.kane.sheet/SheetBuilderImpl/sheetDescriptor/#/PointingToDeclaration/)|  [jvm] val [sheetDescriptor](index.md#com.github.jomof.kane.sheet/SheetBuilderImpl/sheetDescriptor/#/PointingToDeclaration/): [SheetDescriptor](../-sheet-descriptor/index.md)   <br>
| [up](index.md#com.github.jomof.kane.sheet/SheetBuilderImpl/up/#/PointingToDeclaration/)|  [jvm] open override val [up](index.md#com.github.jomof.kane.sheet/SheetBuilderImpl/up/#/PointingToDeclaration/): [SheetBuilderRange](../-sheet-builder-range/index.md)   <br>


## Extensions  
  
|  Name|  Summary| 
|---|---|
| [convertNamedColumnsToColumnRanges](../convert-named-columns-to-column-ranges.md)| [jvm]  <br>Content  <br>fun [SheetBuilderImpl](index.md).[convertNamedColumnsToColumnRanges](../convert-named-columns-to-column-ranges.md)(cells: [Cells](../-cells/index.md)): [Cells](../-cells/index.md)  <br><br><br>
| [scalarizeRanges](../scalarize-ranges.md)| [jvm]  <br>Content  <br>fun [SheetBuilderImpl](index.md).[scalarizeRanges](../scalarize-ranges.md)(cells: [Cells](../-cells/index.md)): [Cells](../-cells/index.md)  <br><br><br>

