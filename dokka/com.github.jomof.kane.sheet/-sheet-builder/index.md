//[Kane](../../index.md)/[com.github.jomof.kane.sheet](../index.md)/[SheetBuilder](index.md)



# SheetBuilder  
 [jvm] interface [SheetBuilder](index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [cell](cell.md)| [jvm]  <br>Content  <br>open fun [cell](cell.md)(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [SheetBuilderRange](../-sheet-builder-range/index.md)  <br><br><br>
| [column](column.md)| [jvm]  <br>Content  <br>abstract fun [column](column.md)(range: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [SheetBuilderRange](../-sheet-builder-range/index.md)  <br><br><br>
| [down](down.md)| [jvm]  <br>Content  <br>open fun [down](down.md)(offset: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [SheetBuilderRange](../-sheet-builder-range/index.md)  <br><br><br>
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [getValue](get-value.md)| [jvm]  <br>Content  <br>open operator fun [Number](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-number/index.html).[getValue](get-value.md)(nothing: [Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html)?, property: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)<*>): [NamedScalar](../../com.github.jomof.kane/-named-scalar/index.md)  <br>open operator fun [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html).[getValue](get-value.md)(nothing: [Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html)?, property: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)<*>): [NamedExpr](../../com.github.jomof.kane/-named-expr/index.md)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [left](left.md)| [jvm]  <br>Content  <br>open fun [left](left.md)(offset: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [SheetBuilderRange](../-sheet-builder-range/index.md)  <br><br><br>
| [nameColumn](name-column.md)| [jvm]  <br>Content  <br>abstract fun [nameColumn](name-column.md)(column: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), name: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html))  <br><br><br>
| [nameRow](name-row.md)| [jvm]  <br>Content  <br>abstract fun [nameRow](name-row.md)(row: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), name: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html))  <br>abstract fun [nameRow](name-row.md)(row: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), name: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Expr](../../com.github.jomof.kane/-expr/index.md)>)  <br><br><br>
| [range](range.md)| [jvm]  <br>Content  <br>open fun [range](range.md)(range: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [SheetBuilderRange](../-sheet-builder-range/index.md)  <br><br><br>
| [right](right.md)| [jvm]  <br>Content  <br>open fun [right](right.md)(offset: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [SheetBuilderRange](../-sheet-builder-range/index.md)  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [up](up.md)| [jvm]  <br>Content  <br>open fun [up](up.md)(offset: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [SheetBuilderRange](../-sheet-builder-range/index.md)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [down](index.md#com.github.jomof.kane.sheet/SheetBuilder/down/#/PointingToDeclaration/)|  [jvm] open val [down](index.md#com.github.jomof.kane.sheet/SheetBuilder/down/#/PointingToDeclaration/): [SheetBuilderRange](../-sheet-builder-range/index.md)   <br>
| [left](index.md#com.github.jomof.kane.sheet/SheetBuilder/left/#/PointingToDeclaration/)|  [jvm] open val [left](index.md#com.github.jomof.kane.sheet/SheetBuilder/left/#/PointingToDeclaration/): [SheetBuilderRange](../-sheet-builder-range/index.md)   <br>
| [right](index.md#com.github.jomof.kane.sheet/SheetBuilder/right/#/PointingToDeclaration/)|  [jvm] open val [right](index.md#com.github.jomof.kane.sheet/SheetBuilder/right/#/PointingToDeclaration/): [SheetBuilderRange](../-sheet-builder-range/index.md)   <br>
| [up](index.md#com.github.jomof.kane.sheet/SheetBuilder/up/#/PointingToDeclaration/)|  [jvm] open val [up](index.md#com.github.jomof.kane.sheet/SheetBuilder/up/#/PointingToDeclaration/): [SheetBuilderRange](../-sheet-builder-range/index.md)   <br>


## Inheritors  
  
|  Name| 
|---|
| [SheetBuilderImpl](../-sheet-builder-impl/index.md)

