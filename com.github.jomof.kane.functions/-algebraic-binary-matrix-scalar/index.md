//[Kane](../../index.md)/[com.github.jomof.kane.functions](../index.md)/[AlgebraicBinaryMatrixScalar](index.md)



# AlgebraicBinaryMatrixScalar  
 [jvm] data class [AlgebraicBinaryMatrixScalar](index.md)(**op**: [AlgebraicBinaryScalarFunction](../-algebraic-binary-scalar-function/index.md), **columns**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), **rows**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), **left**: [MatrixExpr](../../com.github.jomof.kane/-matrix-expr/index.md), **right**: [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)) : [MatrixExpr](../../com.github.jomof.kane/-matrix-expr/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [component1](component1.md)| [jvm]  <br>Content  <br>operator fun [component1](component1.md)(): [AlgebraicBinaryScalarFunction](../-algebraic-binary-scalar-function/index.md)  <br><br><br>
| [component2](component2.md)| [jvm]  <br>Content  <br>operator fun [component2](component2.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [component3](component3.md)| [jvm]  <br>Content  <br>operator fun [component3](component3.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [component4](component4.md)| [jvm]  <br>Content  <br>operator fun [component4](component4.md)(): [MatrixExpr](../../com.github.jomof.kane/-matrix-expr/index.md)  <br><br><br>
| [component5](component5.md)| [jvm]  <br>Content  <br>operator fun [component5](component5.md)(): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br><br><br>
| [copy](copy.md)| [jvm]  <br>Content  <br>fun [copy](copy.md)(op: [AlgebraicBinaryScalarFunction](../-algebraic-binary-scalar-function/index.md), columns: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), rows: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), left: [MatrixExpr](../../com.github.jomof.kane/-matrix-expr/index.md), right: [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)): [AlgebraicBinaryMatrixScalar](index.md)  <br><br><br>
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [get](get.md)| [jvm]  <br>Content  <br>open operator override fun [get](get.md)(column: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), row: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [AlgebraicBinaryScalar](../-algebraic-binary-scalar/index.md)  <br><br><br>
| [getValue](get-value.md)| [jvm]  <br>Content  <br>operator fun [getValue](get-value.md)(thisRef: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?, property: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)<*>): [NamedMatrix](../../com.github.jomof.kane/-named-matrix/index.md)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](to-string.md)| [jvm]  <br>Content  <br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [columns](index.md#com.github.jomof.kane.functions/AlgebraicBinaryMatrixScalar/columns/#/PointingToDeclaration/)|  [jvm] open override val [columns](index.md#com.github.jomof.kane.functions/AlgebraicBinaryMatrixScalar/columns/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [left](index.md#com.github.jomof.kane.functions/AlgebraicBinaryMatrixScalar/left/#/PointingToDeclaration/)|  [jvm] val [left](index.md#com.github.jomof.kane.functions/AlgebraicBinaryMatrixScalar/left/#/PointingToDeclaration/): [MatrixExpr](../../com.github.jomof.kane/-matrix-expr/index.md)   <br>
| [op](index.md#com.github.jomof.kane.functions/AlgebraicBinaryMatrixScalar/op/#/PointingToDeclaration/)|  [jvm] val [op](index.md#com.github.jomof.kane.functions/AlgebraicBinaryMatrixScalar/op/#/PointingToDeclaration/): [AlgebraicBinaryScalarFunction](../-algebraic-binary-scalar-function/index.md)   <br>
| [right](index.md#com.github.jomof.kane.functions/AlgebraicBinaryMatrixScalar/right/#/PointingToDeclaration/)|  [jvm] val [right](index.md#com.github.jomof.kane.functions/AlgebraicBinaryMatrixScalar/right/#/PointingToDeclaration/): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)   <br>
| [rows](index.md#com.github.jomof.kane.functions/AlgebraicBinaryMatrixScalar/rows/#/PointingToDeclaration/)|  [jvm] open override val [rows](index.md#com.github.jomof.kane.functions/AlgebraicBinaryMatrixScalar/rows/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>

