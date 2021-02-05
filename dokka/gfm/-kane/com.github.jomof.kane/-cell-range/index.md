//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[CellRange](index.md)



# CellRange  
 [jvm] data class [CellRange](index.md)(**column**: [ComputableIndex](../-computable-index/index.md), **row**: [ComputableIndex](../-computable-index/index.md)) : [SheetRange](../-sheet-range/index.md)   


## Types  
  
|  Name|  Summary| 
|---|---|
| [Companion](-companion/index.md)| [jvm]  <br>Content  <br>object [Companion](-companion/index.md)  <br><br><br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| [component1](component1.md)| [jvm]  <br>Content  <br>operator fun [component1](component1.md)(): [ComputableIndex](../-computable-index/index.md)  <br><br><br>
| [component2](component2.md)| [jvm]  <br>Content  <br>operator fun [component2](component2.md)(): [ComputableIndex](../-computable-index/index.md)  <br><br><br>
| [contains](contains.md)| [jvm]  <br>Content  <br>open override fun [contains](contains.md)(name: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [copy](copy.md)| [jvm]  <br>Content  <br>fun [copy](copy.md)(column: [ComputableIndex](../-computable-index/index.md), row: [ComputableIndex](../-computable-index/index.md)): [CellRange](index.md)  <br><br><br>
| [down](down.md)| [jvm]  <br>Content  <br>open override fun [down](down.md)(move: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [CellRange](index.md)  <br><br><br>
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [left](left.md)| [jvm]  <br>Content  <br>open override fun [left](left.md)(move: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [CellRange](index.md)  <br><br><br>
| [right](right.md)| [jvm]  <br>Content  <br>open override fun [right](right.md)(move: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [CellRange](index.md)  <br><br><br>
| [toCoordinate](to-coordinate.md)| [jvm]  <br>Content  <br>fun [toCoordinate](to-coordinate.md)(): [Coordinate](../-coordinate/index.md)  <br><br><br>
| [toString](to-string.md)| [jvm]  <br>Content  <br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [up](up.md)| [jvm]  <br>Content  <br>open override fun [up](up.md)(move: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [CellRange](index.md)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [column](index.md#com.github.jomof.kane/CellRange/column/#/PointingToDeclaration/)|  [jvm] val [column](index.md#com.github.jomof.kane/CellRange/column/#/PointingToDeclaration/): [ComputableIndex](../-computable-index/index.md)   <br>
| [down](index.md#com.github.jomof.kane/CellRange/down/#/PointingToDeclaration/)|  [jvm] open override val [down](index.md#com.github.jomof.kane/CellRange/down/#/PointingToDeclaration/): [SheetRange](../-sheet-range/index.md)   <br>
| [left](index.md#com.github.jomof.kane/CellRange/left/#/PointingToDeclaration/)|  [jvm] open override val [left](index.md#com.github.jomof.kane/CellRange/left/#/PointingToDeclaration/): [SheetRange](../-sheet-range/index.md)   <br>
| [right](index.md#com.github.jomof.kane/CellRange/right/#/PointingToDeclaration/)|  [jvm] open override val [right](index.md#com.github.jomof.kane/CellRange/right/#/PointingToDeclaration/): [SheetRange](../-sheet-range/index.md)   <br>
| [row](index.md#com.github.jomof.kane/CellRange/row/#/PointingToDeclaration/)|  [jvm] val [row](index.md#com.github.jomof.kane/CellRange/row/#/PointingToDeclaration/): [ComputableIndex](../-computable-index/index.md)   <br>
| [up](index.md#com.github.jomof.kane/CellRange/up/#/PointingToDeclaration/)|  [jvm] open override val [up](index.md#com.github.jomof.kane/CellRange/up/#/PointingToDeclaration/): [SheetRange](../-sheet-range/index.md)   <br>

