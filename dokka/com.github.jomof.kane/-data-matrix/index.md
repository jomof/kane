//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[DataMatrix](index.md)



# DataMatrix  
 [jvm] data class [DataMatrix](index.md)(**columns**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), **rows**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), **elements**: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[ScalarExpr](../-scalar-expr/index.md)>) : [MatrixExpr](../-matrix-expr/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [component1](component1.md)| [jvm]  <br>Content  <br>operator fun [component1](component1.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [component2](component2.md)| [jvm]  <br>Content  <br>operator fun [component2](component2.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [component3](component3.md)| [jvm]  <br>Content  <br>operator fun [component3](component3.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[ScalarExpr](../-scalar-expr/index.md)>  <br><br><br>
| [copy](copy.md)| [jvm]  <br>Content  <br>fun [copy](copy.md)(columns: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), rows: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), elements: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[ScalarExpr](../-scalar-expr/index.md)>): [DataMatrix](index.md)  <br><br><br>
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [get](get.md)| [jvm]  <br>Content  <br>open operator override fun [get](get.md)(column: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), row: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [ScalarExpr](../-scalar-expr/index.md)  <br><br><br>
| [getValue](get-value.md)| [jvm]  <br>Content  <br>operator fun [getValue](get-value.md)(thisRef: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?, property: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)<*>): [NamedMatrix](../-named-matrix/index.md)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](to-string.md)| [jvm]  <br>Content  <br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [columns](index.md#com.github.jomof.kane/DataMatrix/columns/#/PointingToDeclaration/)|  [jvm] open override val [columns](index.md#com.github.jomof.kane/DataMatrix/columns/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [elements](index.md#com.github.jomof.kane/DataMatrix/elements/#/PointingToDeclaration/)|  [jvm] val [elements](index.md#com.github.jomof.kane/DataMatrix/elements/#/PointingToDeclaration/): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[ScalarExpr](../-scalar-expr/index.md)>   <br>
| [rows](index.md#com.github.jomof.kane/DataMatrix/rows/#/PointingToDeclaration/)|  [jvm] open override val [rows](index.md#com.github.jomof.kane/DataMatrix/rows/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>

