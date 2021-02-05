//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[RetypeMatrix](index.md)



# RetypeMatrix  
 [jvm] 

Change the type of the underlying MatrixExpr

data class [RetypeMatrix](index.md)(**matrix**: [MatrixExpr](../-matrix-expr/index.md), **type**: [AlgebraicType](../../com.github.jomof.kane.types/-algebraic-type/index.md)) : [MatrixExpr](../-matrix-expr/index.md)   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| [RetypeMatrix](-retype-matrix.md)|  [jvm] fun [RetypeMatrix](-retype-matrix.md)(matrix: [MatrixExpr](../-matrix-expr/index.md), type: [AlgebraicType](../../com.github.jomof.kane.types/-algebraic-type/index.md))   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| [component1](component1.md)| [jvm]  <br>Content  <br>operator fun [component1](component1.md)(): [MatrixExpr](../-matrix-expr/index.md)  <br><br><br>
| [component2](component2.md)| [jvm]  <br>Content  <br>operator fun [component2](component2.md)(): [AlgebraicType](../../com.github.jomof.kane.types/-algebraic-type/index.md)  <br><br><br>
| [copy](copy.md)| [jvm]  <br>Content  <br>fun [copy](copy.md)(matrix: [MatrixExpr](../-matrix-expr/index.md), type: [AlgebraicType](../../com.github.jomof.kane.types/-algebraic-type/index.md)): [RetypeMatrix](index.md)  <br><br><br>
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [get](get.md)| [jvm]  <br>Content  <br>open operator override fun [get](get.md)(column: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), row: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [RetypeScalar](../-retype-scalar/index.md)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](to-string.md)| [jvm]  <br>Content  <br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [columns](index.md#com.github.jomof.kane/RetypeMatrix/columns/#/PointingToDeclaration/)|  [jvm] open override val [columns](index.md#com.github.jomof.kane/RetypeMatrix/columns/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [matrix](index.md#com.github.jomof.kane/RetypeMatrix/matrix/#/PointingToDeclaration/)|  [jvm] val [matrix](index.md#com.github.jomof.kane/RetypeMatrix/matrix/#/PointingToDeclaration/): [MatrixExpr](../-matrix-expr/index.md)   <br>
| [rows](index.md#com.github.jomof.kane/RetypeMatrix/rows/#/PointingToDeclaration/)|  [jvm] open override val [rows](index.md#com.github.jomof.kane/RetypeMatrix/rows/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [type](index.md#com.github.jomof.kane/RetypeMatrix/type/#/PointingToDeclaration/)|  [jvm] val [type](index.md#com.github.jomof.kane/RetypeMatrix/type/#/PointingToDeclaration/): [AlgebraicType](../../com.github.jomof.kane.types/-algebraic-type/index.md)   <br>

