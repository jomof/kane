//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[SheetRangeRef](index.md)



# SheetRangeRef  
 [jvm] interface [SheetRangeRef](index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [contains](contains.md)| [jvm]  <br>Content  <br>abstract fun [contains](contains.md)(name: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [down](down.md)| [jvm]  <br>Content  <br>abstract fun [down](down.md)(move: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [SheetRangeRef](index.md)  <br><br><br>
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [left](left.md)| [jvm]  <br>Content  <br>abstract fun [left](left.md)(move: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [SheetRangeRef](index.md)  <br><br><br>
| [right](right.md)| [jvm]  <br>Content  <br>abstract fun [right](right.md)(move: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [SheetRangeRef](index.md)  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [up](up.md)| [jvm]  <br>Content  <br>abstract fun [up](up.md)(move: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [SheetRangeRef](index.md)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [down](index.md#com.github.jomof.kane/SheetRangeRef/down/#/PointingToDeclaration/)|  [jvm] open val [down](index.md#com.github.jomof.kane/SheetRangeRef/down/#/PointingToDeclaration/): [SheetRangeRef](index.md)   <br>
| [left](index.md#com.github.jomof.kane/SheetRangeRef/left/#/PointingToDeclaration/)|  [jvm] open val [left](index.md#com.github.jomof.kane/SheetRangeRef/left/#/PointingToDeclaration/): [SheetRangeRef](index.md)   <br>
| [right](index.md#com.github.jomof.kane/SheetRangeRef/right/#/PointingToDeclaration/)|  [jvm] open val [right](index.md#com.github.jomof.kane/SheetRangeRef/right/#/PointingToDeclaration/): [SheetRangeRef](index.md)   <br>
| [up](index.md#com.github.jomof.kane/SheetRangeRef/up/#/PointingToDeclaration/)|  [jvm] open val [up](index.md#com.github.jomof.kane/SheetRangeRef/up/#/PointingToDeclaration/): [SheetRangeRef](index.md)   <br>


## Inheritors  
  
|  Name| 
|---|
| [CellRangeRef](../-cell-range-ref/index.md)
| [RectangleRangeRef](../-rectangle-range-ref/index.md)
| [ColumnRangeRef](../-column-range-ref/index.md)
| [NamedColumnRangeRef](../-named-column-range-ref/index.md)


## Extensions  
  
|  Name|  Summary| 
|---|---|
| [rebase](../rebase.md)| [jvm]  <br>Content  <br>fun [SheetRangeRef](index.md).[rebase](../rebase.md)(base: [Coordinate](../-coordinate/index.md)): [SheetRangeRef](index.md)  <br><br><br>

