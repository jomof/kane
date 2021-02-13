//[Kane](../../index.md)/[com.github.jomof.kane.impl.functions](../index.md)/[AlgebraicUnaryScalarStatisticFunction](index.md)



# AlgebraicUnaryScalarStatisticFunction  
 [jvm] interface [AlgebraicUnaryScalarStatisticFunction](index.md) : [AggregatableFunction](../-aggregatable-function/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl.functions/AlgebraicUnaryScalarStatisticFunction/call/#com.github.jomof.kane.Expr/PointingToDeclaration/"></a>[call](call.md)| <a name="com.github.jomof.kane.impl.functions/AlgebraicUnaryScalarStatisticFunction/call/#com.github.jomof.kane.Expr/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [call](call.md)(expr: [Expr](../../com.github.jomof.kane/-expr/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br>open fun [call](call.md)(expr: [MatrixExpr](../../com.github.jomof.kane/-matrix-expr/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br>open fun [call](call.md)(expr: [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br>open fun [call](call.md)(expr: [GroupBy](../../com.github.jomof.kane.impl.sheet/-group-by/index.md)): [Sheet](../../com.github.jomof.kane.impl.sheet/-sheet/index.md)  <br>open fun [call](call.md)(value: [Sheet](../../com.github.jomof.kane.impl.sheet/-sheet/index.md)): [Sheet](../../com.github.jomof.kane.impl.sheet/-sheet/index.md)  <br>open fun [call](call.md)(expr: [SheetRange](../../com.github.jomof.kane.impl.sheet/-sheet-range/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br>open fun [call](call.md)(exprs: [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)<out [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)>): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br>open fun [call](call.md)(exprs: [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)<out [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)>): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br>open fun [call](call.md)(values: [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)<out [Number](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-number/index.html)>): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br><br><br>
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-301451995)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-301451995)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-301451995)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-301451995)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.impl.functions/AlgebraicUnaryScalarStatisticFunction/lookupStatistic/#com.github.jomof.kane.impl.StreamingSamples/PointingToDeclaration/"></a>[lookupStatistic](lookup-statistic.md)| <a name="com.github.jomof.kane.impl.functions/AlgebraicUnaryScalarStatisticFunction/lookupStatistic/#com.github.jomof.kane.impl.StreamingSamples/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun [lookupStatistic](lookup-statistic.md)(statistic: [StreamingSamples](../../com.github.jomof.kane.impl/-streaming-samples/index.md)): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.impl.functions/AlgebraicUnaryScalarStatisticFunction/reduceArithmetic/#com.github.jomof.kane.Expr/PointingToDeclaration/"></a>[reduceArithmetic](reduce-arithmetic.md)| <a name="com.github.jomof.kane.impl.functions/AlgebraicUnaryScalarStatisticFunction/reduceArithmetic/#com.github.jomof.kane.Expr/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [reduceArithmetic](reduce-arithmetic.md)(value: [Expr](../../com.github.jomof.kane/-expr/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)?  <br>open fun [reduceArithmetic](reduce-arithmetic.md)(value: [MatrixExpr](../../com.github.jomof.kane/-matrix-expr/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)?  <br>open fun [reduceArithmetic](reduce-arithmetic.md)(value: [ScalarStatistic](../../com.github.jomof.kane.impl/-scalar-statistic/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br>open fun [reduceArithmetic](reduce-arithmetic.md)(elements: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)>): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)?  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../com.github.jomof.kane.impl.types/-object-kane-type/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-301451995)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../com.github.jomof.kane.impl.types/-object-kane-type/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-301451995)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl.functions/AlgebraicUnaryScalarStatisticFunction/meta/#/PointingToDeclaration/"></a>[meta](meta.md)| <a name="com.github.jomof.kane.impl.functions/AlgebraicUnaryScalarStatisticFunction/meta/#/PointingToDeclaration/"></a> [jvm] abstract val [meta](meta.md): [UnaryOp](../../com.github.jomof.kane.impl/-unary-op/index.md)   <br>


## Inheritors  
  
|  Name| 
|---|
| <a name="com.github.jomof.kane.impl.functions/CountFunction///PointingToDeclaration/"></a>[CountFunction](../-count-function/index.md)
| <a name="com.github.jomof.kane.impl.functions/CvFunction///PointingToDeclaration/"></a>[CvFunction](../-cv-function/index.md)
| <a name="com.github.jomof.kane.impl.functions/KurtosisFunction///PointingToDeclaration/"></a>[KurtosisFunction](../-kurtosis-function/index.md)
| <a name="com.github.jomof.kane.impl.functions/MaxFunction///PointingToDeclaration/"></a>[MaxFunction](../-max-function/index.md)
| <a name="com.github.jomof.kane.impl.functions/MeanFunction///PointingToDeclaration/"></a>[MeanFunction](../-mean-function/index.md)
| <a name="com.github.jomof.kane.impl.functions/MedianFunction///PointingToDeclaration/"></a>[MedianFunction](../-median-function/index.md)
| <a name="com.github.jomof.kane.impl.functions/MinFunction///PointingToDeclaration/"></a>[MinFunction](../-min-function/index.md)
| <a name="com.github.jomof.kane.impl.functions/NansFunction///PointingToDeclaration/"></a>[NansFunction](../-nans-function/index.md)
| <a name="com.github.jomof.kane.impl.functions/Percentile25Function///PointingToDeclaration/"></a>[Percentile25Function](../-percentile25-function/index.md)
| <a name="com.github.jomof.kane.impl.functions/Percentile75Function///PointingToDeclaration/"></a>[Percentile75Function](../-percentile75-function/index.md)
| <a name="com.github.jomof.kane.impl.functions/SkewnessFunction///PointingToDeclaration/"></a>[SkewnessFunction](../-skewness-function/index.md)
| <a name="com.github.jomof.kane.impl.functions/SumFunction///PointingToDeclaration/"></a>[SumFunction](../-sum-function/index.md)
| <a name="com.github.jomof.kane.impl.functions/VarianceFunction///PointingToDeclaration/"></a>[VarianceFunction](../-variance-function/index.md)

