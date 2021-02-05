//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[NamedColumnRange](index.md)



# NamedColumnRange  
 [jvm] data class [NamedColumnRange](index.md)(**name**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **columnOffset**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), **rowOffset**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)) : [SheetRange](../-sheet-range/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [component1](component1.md)| [jvm]  <br>Content  <br>operator fun [component1](component1.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [component2](component2.md)| [jvm]  <br>Content  <br>operator fun [component2](component2.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [component3](component3.md)| [jvm]  <br>Content  <br>operator fun [component3](component3.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [contains](contains.md)| [jvm]  <br>Content  <br>open override fun [contains](contains.md)(name: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)): [Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html)  <br><br><br>
| [copy](copy.md)| [jvm]  <br>Content  <br>fun [copy](copy.md)(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), columnOffset: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), rowOffset: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [NamedColumnRange](index.md)  <br><br><br>
| [down](down.md)| [jvm]  <br>Content  <br>open override fun [down](down.md)(move: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [NamedColumnRange](index.md)  <br><br><br>
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [left](left.md)| [jvm]  <br>Content  <br>open override fun [left](left.md)(move: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [NamedColumnRange](index.md)  <br><br><br>
| [right](right.md)| [jvm]  <br>Content  <br>open override fun [right](right.md)(move: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [NamedColumnRange](index.md)  <br><br><br>
| [toString](to-string.md)| [jvm]  <br>Content  <br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [up](up.md)| [jvm]  <br>Content  <br>open override fun [up](up.md)(move: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [NamedColumnRange](index.md)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [columnOffset](index.md#com.github.jomof.kane/NamedColumnRange/columnOffset/#/PointingToDeclaration/)|  [jvm] val [columnOffset](index.md#com.github.jomof.kane/NamedColumnRange/columnOffset/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [down](index.md#com.github.jomof.kane/NamedColumnRange/down/#/PointingToDeclaration/)|  [jvm] open override val [down](index.md#com.github.jomof.kane/NamedColumnRange/down/#/PointingToDeclaration/): [SheetRange](../-sheet-range/index.md)   <br>
| [left](index.md#com.github.jomof.kane/NamedColumnRange/left/#/PointingToDeclaration/)|  [jvm] open override val [left](index.md#com.github.jomof.kane/NamedColumnRange/left/#/PointingToDeclaration/): [SheetRange](../-sheet-range/index.md)   <br>
| [name](index.md#com.github.jomof.kane/NamedColumnRange/name/#/PointingToDeclaration/)|  [jvm] val [name](index.md#com.github.jomof.kane/NamedColumnRange/name/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [right](index.md#com.github.jomof.kane/NamedColumnRange/right/#/PointingToDeclaration/)|  [jvm] open override val [right](index.md#com.github.jomof.kane/NamedColumnRange/right/#/PointingToDeclaration/): [SheetRange](../-sheet-range/index.md)   <br>
| [rowOffset](index.md#com.github.jomof.kane/NamedColumnRange/rowOffset/#/PointingToDeclaration/)|  [jvm] val [rowOffset](index.md#com.github.jomof.kane/NamedColumnRange/rowOffset/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [up](index.md#com.github.jomof.kane/NamedColumnRange/up/#/PointingToDeclaration/)|  [jvm] open override val [up](index.md#com.github.jomof.kane/NamedColumnRange/up/#/PointingToDeclaration/): [SheetRange](../-sheet-range/index.md)   <br>

