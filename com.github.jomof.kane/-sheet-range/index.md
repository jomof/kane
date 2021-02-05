//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[SheetRange](index.md)



# SheetRange  
 [jvm] interface [SheetRange](index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [contains](contains.md)| [jvm]  <br>Content  <br>abstract fun [contains](contains.md)(name: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [down](down.md)| [jvm]  <br>Content  <br>abstract fun [down](down.md)(move: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [SheetRange](index.md)  <br><br><br>
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [left](left.md)| [jvm]  <br>Content  <br>abstract fun [left](left.md)(move: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [SheetRange](index.md)  <br><br><br>
| [right](right.md)| [jvm]  <br>Content  <br>abstract fun [right](right.md)(move: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [SheetRange](index.md)  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [up](up.md)| [jvm]  <br>Content  <br>abstract fun [up](up.md)(move: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [SheetRange](index.md)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [down](index.md#com.github.jomof.kane/SheetRange/down/#/PointingToDeclaration/)|  [jvm] open val [down](index.md#com.github.jomof.kane/SheetRange/down/#/PointingToDeclaration/): [SheetRange](index.md)   <br>
| [left](index.md#com.github.jomof.kane/SheetRange/left/#/PointingToDeclaration/)|  [jvm] open val [left](index.md#com.github.jomof.kane/SheetRange/left/#/PointingToDeclaration/): [SheetRange](index.md)   <br>
| [right](index.md#com.github.jomof.kane/SheetRange/right/#/PointingToDeclaration/)|  [jvm] open val [right](index.md#com.github.jomof.kane/SheetRange/right/#/PointingToDeclaration/): [SheetRange](index.md)   <br>
| [up](index.md#com.github.jomof.kane/SheetRange/up/#/PointingToDeclaration/)|  [jvm] open val [up](index.md#com.github.jomof.kane/SheetRange/up/#/PointingToDeclaration/): [SheetRange](index.md)   <br>


## Inheritors  
  
|  Name| 
|---|
| [CellRange](../-cell-range/index.md)
| [RectangleRange](../-rectangle-range/index.md)
| [ColumnRange](../-column-range/index.md)
| [NamedColumnRange](../-named-column-range/index.md)


## Extensions  
  
|  Name|  Summary| 
|---|---|
| [rebase](../rebase.md)| [jvm]  <br>Content  <br>fun [SheetRange](index.md).[rebase](../rebase.md)(base: [Coordinate](../-coordinate/index.md)): [SheetRange](index.md)  <br><br><br>

