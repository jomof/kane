//[Kane](../../index.md)/[com.github.jomof.kane.impl.functions](../index.md)/[AlgebraicUnaryScalarStatisticFunction](index.md)



# AlgebraicUnaryScalarStatisticFunction  
 [jvm] interface [AlgebraicUnaryScalarStatisticFunction](index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1957593320)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1957593320)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1957593320)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1957593320)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.impl.functions/AlgebraicUnaryScalarStatisticFunction/invoke/#com.github.jomof.kane.impl.AlgebraicExpr/PointingToDeclaration/"></a>[invoke](invoke.md)| <a name="com.github.jomof.kane.impl.functions/AlgebraicUnaryScalarStatisticFunction/invoke/#com.github.jomof.kane.impl.AlgebraicExpr/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [invoke](invoke.md)(expr: [AlgebraicExpr](../../com.github.jomof.kane.impl/-algebraic-expr/index.md)): [ScalarExpr](../../com.github.jomof.kane.impl/-scalar-expr/index.md)  <br>open operator fun [invoke](invoke.md)(expr: [Expr](../../com.github.jomof.kane.impl/-expr/index.md)): [Expr](../../com.github.jomof.kane.impl/-expr/index.md)  <br>open operator fun [invoke](invoke.md)(value: [Sheet](../../com.github.jomof.kane.impl.sheet/-sheet/index.md)): [Expr](../../com.github.jomof.kane.impl/-expr/index.md)  <br>open operator fun [invoke](invoke.md)(expr: [SheetRange](../../com.github.jomof.kane.impl.sheet/-sheet-range/index.md)): [ScalarExpr](../../com.github.jomof.kane.impl/-scalar-expr/index.md)  <br><br><br>
| <a name="com.github.jomof.kane.impl.functions/AlgebraicUnaryScalarStatisticFunction/lookupStatistic/#com.github.jomof.kane.impl.StreamingSamples/PointingToDeclaration/"></a>[lookupStatistic](lookup-statistic.md)| <a name="com.github.jomof.kane.impl.functions/AlgebraicUnaryScalarStatisticFunction/lookupStatistic/#com.github.jomof.kane.impl.StreamingSamples/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun [lookupStatistic](lookup-statistic.md)(statistic: [StreamingSamples](../../com.github.jomof.kane.impl/-streaming-samples/index.md)): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.impl.functions/AlgebraicUnaryScalarStatisticFunction/reduceArithmetic/#com.github.jomof.kane.impl.Expr/PointingToDeclaration/"></a>[reduceArithmetic](reduce-arithmetic.md)| <a name="com.github.jomof.kane.impl.functions/AlgebraicUnaryScalarStatisticFunction/reduceArithmetic/#com.github.jomof.kane.impl.Expr/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [reduceArithmetic](reduce-arithmetic.md)(value: [Expr](../../com.github.jomof.kane.impl/-expr/index.md)): [ScalarExpr](../../com.github.jomof.kane.impl/-scalar-expr/index.md)?  <br>open fun [reduceArithmetic](reduce-arithmetic.md)(value: [MatrixExpr](../../com.github.jomof.kane.impl/-matrix-expr/index.md)): [ScalarExpr](../../com.github.jomof.kane.impl/-scalar-expr/index.md)?  <br>open fun [reduceArithmetic](reduce-arithmetic.md)(value: [ScalarStatistic](../../com.github.jomof.kane.impl/-scalar-statistic/index.md)): [ScalarExpr](../../com.github.jomof.kane.impl/-scalar-expr/index.md)  <br>open fun [reduceArithmetic](reduce-arithmetic.md)(elements: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[ScalarExpr](../../com.github.jomof.kane.impl/-scalar-expr/index.md)>): [ScalarExpr](../../com.github.jomof.kane.impl/-scalar-expr/index.md)?  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../com.github.jomof.kane.impl.types/-object-kane-type/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1957593320)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../com.github.jomof.kane.impl.types/-object-kane-type/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1957593320)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl.functions/AlgebraicUnaryScalarStatisticFunction/meta/#/PointingToDeclaration/"></a>[meta](meta.md)| <a name="com.github.jomof.kane.impl.functions/AlgebraicUnaryScalarStatisticFunction/meta/#/PointingToDeclaration/"></a> [jvm] abstract val [meta](meta.md): [UnaryOp](../../com.github.jomof.kane.impl/-unary-op/index.md)   <br>


## Inheritors  
  
|  Name| 
|---|
| <a name="com.github.jomof.kane.functions/CoefficientOfVariationFunction///PointingToDeclaration/"></a>[CoefficientOfVariationFunction](../../com.github.jomof.kane.functions/-coefficient-of-variation-function/index.md)
| <a name="com.github.jomof.kane.functions/CountFunction///PointingToDeclaration/"></a>[CountFunction](../../com.github.jomof.kane.functions/-count-function/index.md)
| <a name="com.github.jomof.kane.functions/KurtosisFunction///PointingToDeclaration/"></a>[KurtosisFunction](../../com.github.jomof.kane.functions/-kurtosis-function/index.md)
| <a name="com.github.jomof.kane.functions/MaxFunction///PointingToDeclaration/"></a>[MaxFunction](../../com.github.jomof.kane.functions/-max-function/index.md)
| <a name="com.github.jomof.kane.functions/MeanFunction///PointingToDeclaration/"></a>[MeanFunction](../../com.github.jomof.kane.functions/-mean-function/index.md)
| <a name="com.github.jomof.kane.functions/MedianFunction///PointingToDeclaration/"></a>[MedianFunction](../../com.github.jomof.kane.functions/-median-function/index.md)
| <a name="com.github.jomof.kane.functions/MinFunction///PointingToDeclaration/"></a>[MinFunction](../../com.github.jomof.kane.functions/-min-function/index.md)
| <a name="com.github.jomof.kane.functions/NaNsFunction///PointingToDeclaration/"></a>[NaNsFunction](../../com.github.jomof.kane.functions/-na-ns-function/index.md)
| <a name="com.github.jomof.kane.functions/SkewnessFunction///PointingToDeclaration/"></a>[SkewnessFunction](../../com.github.jomof.kane.functions/-skewness-function/index.md)
| <a name="com.github.jomof.kane.functions/StddevFunction///PointingToDeclaration/"></a>[StddevFunction](../../com.github.jomof.kane.functions/-stddev-function/index.md)
| <a name="com.github.jomof.kane.functions/SummationFunction///PointingToDeclaration/"></a>[SummationFunction](../../com.github.jomof.kane.functions/-summation-function/index.md)
| <a name="com.github.jomof.kane.functions/VarianceFunction///PointingToDeclaration/"></a>[VarianceFunction](../../com.github.jomof.kane.functions/-variance-function/index.md)

