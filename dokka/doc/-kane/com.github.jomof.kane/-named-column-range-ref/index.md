//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[NamedColumnRangeRef](index.md)



# NamedColumnRangeRef  
 [jvm] data class [NamedColumnRangeRef](index.md)(**name**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **columnOffset**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), **rowOffset**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)) : [SheetRangeRef](../-sheet-range-ref/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [component1](component1.md)| [jvm]  <br>Content  <br>operator fun [component1](component1.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [component2](component2.md)| [jvm]  <br>Content  <br>operator fun [component2](component2.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [component3](component3.md)| [jvm]  <br>Content  <br>operator fun [component3](component3.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [contains](contains.md)| [jvm]  <br>Content  <br>open override fun [contains](contains.md)(name: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)): [Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html)  <br><br><br>
| [copy](copy.md)| [jvm]  <br>Content  <br>fun [copy](copy.md)(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), columnOffset: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), rowOffset: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [NamedColumnRangeRef](index.md)  <br><br><br>
| [down](down.md)| [jvm]  <br>Content  <br>open override fun [down](down.md)(move: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [NamedColumnRangeRef](index.md)  <br><br><br>
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [left](left.md)| [jvm]  <br>Content  <br>open override fun [left](left.md)(move: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [NamedColumnRangeRef](index.md)  <br><br><br>
| [right](right.md)| [jvm]  <br>Content  <br>open override fun [right](right.md)(move: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [NamedColumnRangeRef](index.md)  <br><br><br>
| [toString](to-string.md)| [jvm]  <br>Content  <br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [up](up.md)| [jvm]  <br>Content  <br>open override fun [up](up.md)(move: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [NamedColumnRangeRef](index.md)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [columnOffset](index.md#com.github.jomof.kane/NamedColumnRangeRef/columnOffset/#/PointingToDeclaration/)|  [jvm] val [columnOffset](index.md#com.github.jomof.kane/NamedColumnRangeRef/columnOffset/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [down](index.md#com.github.jomof.kane/NamedColumnRangeRef/down/#/PointingToDeclaration/)|  [jvm] open override val [down](index.md#com.github.jomof.kane/NamedColumnRangeRef/down/#/PointingToDeclaration/): [SheetRangeRef](../-sheet-range-ref/index.md)   <br>
| [left](index.md#com.github.jomof.kane/NamedColumnRangeRef/left/#/PointingToDeclaration/)|  [jvm] open override val [left](index.md#com.github.jomof.kane/NamedColumnRangeRef/left/#/PointingToDeclaration/): [SheetRangeRef](../-sheet-range-ref/index.md)   <br>
| [name](index.md#com.github.jomof.kane/NamedColumnRangeRef/name/#/PointingToDeclaration/)|  [jvm] val [name](index.md#com.github.jomof.kane/NamedColumnRangeRef/name/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [right](index.md#com.github.jomof.kane/NamedColumnRangeRef/right/#/PointingToDeclaration/)|  [jvm] open override val [right](index.md#com.github.jomof.kane/NamedColumnRangeRef/right/#/PointingToDeclaration/): [SheetRangeRef](../-sheet-range-ref/index.md)   <br>
| [rowOffset](index.md#com.github.jomof.kane/NamedColumnRangeRef/rowOffset/#/PointingToDeclaration/)|  [jvm] val [rowOffset](index.md#com.github.jomof.kane/NamedColumnRangeRef/rowOffset/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [up](index.md#com.github.jomof.kane/NamedColumnRangeRef/up/#/PointingToDeclaration/)|  [jvm] open override val [up](index.md#com.github.jomof.kane/NamedColumnRangeRef/up/#/PointingToDeclaration/): [SheetRangeRef](../-sheet-range-ref/index.md)   <br>

