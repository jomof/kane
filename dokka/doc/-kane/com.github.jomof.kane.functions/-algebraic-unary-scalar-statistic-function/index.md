//[Kane](../../index.md)/[com.github.jomof.kane.functions](../index.md)/[AlgebraicUnaryScalarStatisticFunction](index.md)



# AlgebraicUnaryScalarStatisticFunction  
 [jvm] interface [AlgebraicUnaryScalarStatisticFunction](index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [invoke](invoke.md)| [jvm]  <br>Content  <br>open operator fun [invoke](invoke.md)(expr: [AlgebraicExpr](../../com.github.jomof.kane/-algebraic-expr/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br>open operator fun [invoke](invoke.md)(expr: [Expr](../../com.github.jomof.kane/-expr/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br>open operator fun [invoke](invoke.md)(value: [Sheet](../../com.github.jomof.kane.sheet/-sheet/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br>open operator fun [invoke](invoke.md)(expr: [SheetRange](../../com.github.jomof.kane.sheet/-sheet-range/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br><br><br>
| [lookupStatistic](lookup-statistic.md)| [jvm]  <br>Content  <br>abstract fun [lookupStatistic](lookup-statistic.md)(statistic: [StreamingSamples](../../com.github.jomof.kane/-streaming-samples/index.md)): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br><br><br>
| [reduceArithmetic](reduce-arithmetic.md)| [jvm]  <br>Content  <br>open fun [reduceArithmetic](reduce-arithmetic.md)(value: [Expr](../../com.github.jomof.kane/-expr/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)?  <br>open fun [reduceArithmetic](reduce-arithmetic.md)(value: [MatrixExpr](../../com.github.jomof.kane/-matrix-expr/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)?  <br>open fun [reduceArithmetic](reduce-arithmetic.md)(value: [ScalarStatistic](../../com.github.jomof.kane/-scalar-statistic/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br>open fun [reduceArithmetic](reduce-arithmetic.md)(elements: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)>): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)?  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [meta](index.md#com.github.jomof.kane.functions/AlgebraicUnaryScalarStatisticFunction/meta/#/PointingToDeclaration/)|  [jvm] abstract val [meta](index.md#com.github.jomof.kane.functions/AlgebraicUnaryScalarStatisticFunction/meta/#/PointingToDeclaration/): [UnaryOp](../../com.github.jomof.kane/-unary-op/index.md)   <br>


## Inheritors  
  
|  Name| 
|---|
| [CoefficientOfVariationFunction](../-coefficient-of-variation-function/index.md)
| [CountFunction](../-count-function/index.md)
| [KurtosisFunction](../-kurtosis-function/index.md)
| [MaxFunction](../-max-function/index.md)
| [MeanFunction](../-mean-function/index.md)
| [MedianFunction](../-median-function/index.md)
| [MinFunction](../-min-function/index.md)
| [NaNsFunction](../-na-ns-function/index.md)
| [SkewnessFunction](../-skewness-function/index.md)
| [StddevFunction](../-stddev-function/index.md)
| [SummationFunction](../-summation-function/index.md)
| [VarianceFunction](../-variance-function/index.md)

