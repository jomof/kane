//[Kane](../../index.md)/[com.github.jomof.kane.functions](../index.md)/[AlgebraicUnaryMatrixScalarFunction](index.md)



# AlgebraicUnaryMatrixScalarFunction  
 [jvm] interface [AlgebraicUnaryMatrixScalarFunction](index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [invoke](invoke.md)| [jvm]  <br>Content  <br>open operator fun [invoke](invoke.md)(value: [MatrixExpr](../../com.github.jomof.kane/-matrix-expr/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br><br><br>
| [reduceArithmetic](reduce-arithmetic.md)| [jvm]  <br>Content  <br>abstract fun [reduceArithmetic](reduce-arithmetic.md)(value: [MatrixExpr](../../com.github.jomof.kane/-matrix-expr/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)?  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [meta](index.md#com.github.jomof.kane.functions/AlgebraicUnaryMatrixScalarFunction/meta/#/PointingToDeclaration/)|  [jvm] abstract val [meta](index.md#com.github.jomof.kane.functions/AlgebraicUnaryMatrixScalarFunction/meta/#/PointingToDeclaration/): [UnaryOp](../../com.github.jomof.kane/-unary-op/index.md)   <br>


## Inheritors  
  
|  Name| 
|---|
| [CountFunction](../-count-function/index.md)

