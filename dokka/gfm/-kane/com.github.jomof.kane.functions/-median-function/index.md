//[Kane](../../index.md)/[com.github.jomof.kane.functions](../index.md)/[MedianFunction](index.md)



# MedianFunction  
 [jvm] class [MedianFunction](index.md) : [AlgebraicUnaryScalarStatisticFunction](../-algebraic-unary-scalar-statistic-function/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [invoke](../-algebraic-unary-scalar-statistic-function/invoke.md)| [jvm]  <br>Content  <br>open operator override fun [invoke](../-algebraic-unary-scalar-statistic-function/invoke.md)(expr: [AlgebraicExpr](../../com.github.jomof.kane/-algebraic-expr/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br>open operator override fun [invoke](../-algebraic-unary-scalar-statistic-function/invoke.md)(expr: [Expr](../../com.github.jomof.kane/-expr/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br>open operator override fun [invoke](../-algebraic-unary-scalar-statistic-function/invoke.md)(expr: [UntypedScalar](../../com.github.jomof.kane/-untyped-scalar/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br>open operator override fun [invoke](../-algebraic-unary-scalar-statistic-function/invoke.md)(value: [Sheet](../../com.github.jomof.kane.sheet/-sheet/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br><br><br>
| [lookupStatistic](lookup-statistic.md)| [jvm]  <br>Content  <br>open override fun [lookupStatistic](lookup-statistic.md)(statistic: [StreamingSamples](../../com.github.jomof.kane/-streaming-samples/index.md)): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br><br><br>
| [reduceArithmetic](../-algebraic-unary-scalar-statistic-function/reduce-arithmetic.md)| [jvm]  <br>Content  <br>open override fun [reduceArithmetic](../-algebraic-unary-scalar-statistic-function/reduce-arithmetic.md)(value: [Expr](../../com.github.jomof.kane/-expr/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)?  <br>open override fun [reduceArithmetic](../-algebraic-unary-scalar-statistic-function/reduce-arithmetic.md)(value: [MatrixExpr](../../com.github.jomof.kane/-matrix-expr/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)?  <br>open override fun [reduceArithmetic](../-algebraic-unary-scalar-statistic-function/reduce-arithmetic.md)(value: [ScalarStatistic](../../com.github.jomof.kane/-scalar-statistic/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br>open override fun [reduceArithmetic](../-algebraic-unary-scalar-statistic-function/reduce-arithmetic.md)(elements: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)>): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)?  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [meta](index.md#com.github.jomof.kane.functions/MedianFunction/meta/#/PointingToDeclaration/)|  [jvm] open override val [meta](index.md#com.github.jomof.kane.functions/MedianFunction/meta/#/PointingToDeclaration/): [UnaryOp](../../com.github.jomof.kane/-unary-op/index.md)   <br>

