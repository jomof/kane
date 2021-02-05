//[Kane](../../index.md)/[com.github.jomof.kane.functions](../index.md)/[AlgebraicUnaryScalarFunction](index.md)



# AlgebraicUnaryScalarFunction  
 [jvm] interface [AlgebraicUnaryScalarFunction](index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [differentiate](differentiate.md)| [jvm]  <br>Content  <br>abstract fun [differentiate](differentiate.md)(expr: [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md), exprd: [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md), variable: [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br><br><br>
| [doubleOp](double-op.md)| [jvm]  <br>Content  <br>abstract fun [doubleOp](double-op.md)(value: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br><br><br>
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [invoke](invoke.md)| [jvm]  <br>Content  <br>open operator fun [invoke](invoke.md)(value: [MatrixExpr](../../com.github.jomof.kane/-matrix-expr/index.md)): [MatrixExpr](../../com.github.jomof.kane/-matrix-expr/index.md)  <br>open operator fun [invoke](invoke.md)(value: [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br>open operator fun [invoke](invoke.md)(value: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br><br><br>
| [reduceArithmetic](reduce-arithmetic.md)| [jvm]  <br>Content  <br>open fun [reduceArithmetic](reduce-arithmetic.md)(value: [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)?  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [meta](index.md#com.github.jomof.kane.functions/AlgebraicUnaryScalarFunction/meta/#/PointingToDeclaration/)|  [jvm] abstract val [meta](index.md#com.github.jomof.kane.functions/AlgebraicUnaryScalarFunction/meta/#/PointingToDeclaration/): [UnaryOp](../../com.github.jomof.kane/-unary-op/index.md)   <br>
| [type](index.md#com.github.jomof.kane.functions/AlgebraicUnaryScalarFunction/type/#/PointingToDeclaration/)|  [jvm] open val [type](index.md#com.github.jomof.kane.functions/AlgebraicUnaryScalarFunction/type/#/PointingToDeclaration/): [AlgebraicType](../../com.github.jomof.kane.types/-algebraic-type/index.md)?   <br>

