//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[NamedMatrixVariable](index.md)



# NamedMatrixVariable  
 [jvm] data class [NamedMatrixVariable](index.md)(**name**: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html), **columns**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), **initial**: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)>) : [MatrixVariableExpr](../-matrix-variable-expr/index.md), [NamedMatrixExpr](../-named-matrix-expr/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [component1](component1.md)| [jvm]  <br>Content  <br>operator fun [component1](component1.md)(): [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)  <br><br><br>
| [component2](component2.md)| [jvm]  <br>Content  <br>operator fun [component2](component2.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [component3](component3.md)| [jvm]  <br>Content  <br>operator fun [component3](component3.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)>  <br><br><br>
| [copy](copy.md)| [jvm]  <br>Content  <br>fun [copy](copy.md)(name: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html), columns: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), initial: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)>): [NamedMatrixVariable](index.md)  <br><br><br>
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [get](get.md)| [jvm]  <br>Content  <br>fun [get](get.md)(coordinate: [Coordinate](../-coordinate/index.md)): [MatrixVariableElement](../-matrix-variable-element/index.md)  <br>open operator override fun [get](get.md)(column: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), row: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [MatrixVariableElement](../-matrix-variable-element/index.md)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](to-string.md)| [jvm]  <br>Content  <br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [columns](index.md#com.github.jomof.kane/NamedMatrixVariable/columns/#/PointingToDeclaration/)|  [jvm] open override val [columns](index.md#com.github.jomof.kane/NamedMatrixVariable/columns/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [elements](index.md#com.github.jomof.kane/NamedMatrixVariable/elements/#/PointingToDeclaration/)|  [jvm] val [elements](index.md#com.github.jomof.kane/NamedMatrixVariable/elements/#/PointingToDeclaration/): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[MatrixVariableElement](../-matrix-variable-element/index.md)>   <br>
| [initial](index.md#com.github.jomof.kane/NamedMatrixVariable/initial/#/PointingToDeclaration/)|  [jvm] val [initial](index.md#com.github.jomof.kane/NamedMatrixVariable/initial/#/PointingToDeclaration/): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)>   <br>
| [name](index.md#com.github.jomof.kane/NamedMatrixVariable/name/#/PointingToDeclaration/)|  [jvm] open override val [name](index.md#com.github.jomof.kane/NamedMatrixVariable/name/#/PointingToDeclaration/): [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)   <br>
| [rows](index.md#com.github.jomof.kane/NamedMatrixVariable/rows/#/PointingToDeclaration/)|  [jvm] open override val [rows](index.md#com.github.jomof.kane/NamedMatrixVariable/rows/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>

