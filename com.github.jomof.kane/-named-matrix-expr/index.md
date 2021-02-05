//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[NamedMatrixExpr](index.md)



# NamedMatrixExpr  
 [jvm] interface [NamedMatrixExpr](index.md) : [NamedAlgebraicExpr](../-named-algebraic-expr/index.md), [MatrixExpr](../-matrix-expr/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [get](../-matrix-expr/get.md)| [jvm]  <br>Content  <br>abstract operator override fun [get](../-matrix-expr/get.md)(column: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), row: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [ScalarExpr](../-scalar-expr/index.md)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [columns](index.md#com.github.jomof.kane/NamedMatrixExpr/columns/#/PointingToDeclaration/)|  [jvm] abstract override val [columns](index.md#com.github.jomof.kane/NamedMatrixExpr/columns/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [name](index.md#com.github.jomof.kane/NamedMatrixExpr/name/#/PointingToDeclaration/)|  [jvm] abstract override val [name](index.md#com.github.jomof.kane/NamedMatrixExpr/name/#/PointingToDeclaration/): [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)   <br>
| [rows](index.md#com.github.jomof.kane/NamedMatrixExpr/rows/#/PointingToDeclaration/)|  [jvm] abstract override val [rows](index.md#com.github.jomof.kane/NamedMatrixExpr/rows/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>


## Inheritors  
  
|  Name| 
|---|
| [NamedMatrixVariable](../-named-matrix-variable/index.md)
| [NamedMatrix](../-named-matrix/index.md)

