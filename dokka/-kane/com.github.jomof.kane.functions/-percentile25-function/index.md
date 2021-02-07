//[Kane](../../index.md)/[com.github.jomof.kane.functions](../index.md)/[Percentile25Function](index.md)



# Percentile25Function  
 [jvm] class [Percentile25Function](index.md) : [AlgebraicUnaryScalarStatisticFunction](../../com.github.jomof.kane.impl.functions/-algebraic-unary-scalar-statistic-function/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1958197075)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1958197075)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1958197075)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1958197075)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.impl.functions/AlgebraicUnaryScalarStatisticFunction/invoke/#com.github.jomof.kane.AlgebraicExpr/PointingToDeclaration/"></a>[invoke](../../com.github.jomof.kane.impl.functions/-algebraic-unary-scalar-statistic-function/invoke.md)| <a name="com.github.jomof.kane.impl.functions/AlgebraicUnaryScalarStatisticFunction/invoke/#com.github.jomof.kane.AlgebraicExpr/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [invoke](../../com.github.jomof.kane.impl.functions/-algebraic-unary-scalar-statistic-function/invoke.md)(expr: [AlgebraicExpr](../../com.github.jomof.kane/-algebraic-expr/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br>open operator fun [invoke](../../com.github.jomof.kane.impl.functions/-algebraic-unary-scalar-statistic-function/invoke.md)(expr: [Expr](../../com.github.jomof.kane/-expr/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br>open operator fun [invoke](../../com.github.jomof.kane.impl.functions/-algebraic-unary-scalar-statistic-function/invoke.md)(expr: [GroupBy](../../com.github.jomof.kane.impl.sheet/-group-by/index.md)): [Sheet](../../com.github.jomof.kane.impl.sheet/-sheet/index.md)  <br>open operator fun [invoke](../../com.github.jomof.kane.impl.functions/-algebraic-unary-scalar-statistic-function/invoke.md)(value: [Sheet](../../com.github.jomof.kane.impl.sheet/-sheet/index.md)): [Sheet](../../com.github.jomof.kane.impl.sheet/-sheet/index.md)  <br>open operator fun [invoke](../../com.github.jomof.kane.impl.functions/-algebraic-unary-scalar-statistic-function/invoke.md)(expr: [SheetRange](../../com.github.jomof.kane.impl.sheet/-sheet-range/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br>open operator fun [invoke](../../com.github.jomof.kane.impl.functions/-algebraic-unary-scalar-statistic-function/invoke.md)(values: [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)<out [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)>): [AlgebraicUnaryScalarStatistic](../../com.github.jomof.kane.impl.functions/-algebraic-unary-scalar-statistic/index.md)  <br>open operator fun [invoke](../../com.github.jomof.kane.impl.functions/-algebraic-unary-scalar-statistic-function/invoke.md)(values: [DoubleArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double-array/index.html)): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br>open operator fun [invoke](../../com.github.jomof.kane.impl.functions/-algebraic-unary-scalar-statistic-function/invoke.md)(values: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)>): [AlgebraicUnaryScalarStatistic](../../com.github.jomof.kane.impl.functions/-algebraic-unary-scalar-statistic/index.md)  <br>open operator fun [invoke](../../com.github.jomof.kane.impl.functions/-algebraic-unary-scalar-statistic-function/invoke.md)(values: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)>): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.functions/Percentile25Function/lookupStatistic/#com.github.jomof.kane.impl.StreamingSamples/PointingToDeclaration/"></a>[lookupStatistic](lookup-statistic.md)| <a name="com.github.jomof.kane.functions/Percentile25Function/lookupStatistic/#com.github.jomof.kane.impl.StreamingSamples/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [lookupStatistic](lookup-statistic.md)(statistic: [StreamingSamples](../../com.github.jomof.kane.impl/-streaming-samples/index.md)): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.impl.functions/AlgebraicUnaryScalarStatisticFunction/reduceArithmetic/#com.github.jomof.kane.Expr/PointingToDeclaration/"></a>[reduceArithmetic](../../com.github.jomof.kane.impl.functions/-algebraic-unary-scalar-statistic-function/reduce-arithmetic.md)| <a name="com.github.jomof.kane.impl.functions/AlgebraicUnaryScalarStatisticFunction/reduceArithmetic/#com.github.jomof.kane.Expr/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [reduceArithmetic](../../com.github.jomof.kane.impl.functions/-algebraic-unary-scalar-statistic-function/reduce-arithmetic.md)(value: [Expr](../../com.github.jomof.kane/-expr/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)?  <br>open fun [reduceArithmetic](../../com.github.jomof.kane.impl.functions/-algebraic-unary-scalar-statistic-function/reduce-arithmetic.md)(value: [MatrixExpr](../../com.github.jomof.kane/-matrix-expr/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)?  <br>open fun [reduceArithmetic](../../com.github.jomof.kane.impl.functions/-algebraic-unary-scalar-statistic-function/reduce-arithmetic.md)(value: [ScalarStatistic](../../com.github.jomof.kane.impl/-scalar-statistic/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br>open fun [reduceArithmetic](../../com.github.jomof.kane.impl.functions/-algebraic-unary-scalar-statistic-function/reduce-arithmetic.md)(elements: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)>): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)?  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../com.github.jomof.kane.impl.types/-object-kane-type/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1958197075)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../com.github.jomof.kane.impl.types/-object-kane-type/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1958197075)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.functions/Percentile25Function/meta/#/PointingToDeclaration/"></a>[meta](meta.md)| <a name="com.github.jomof.kane.functions/Percentile25Function/meta/#/PointingToDeclaration/"></a> [jvm] open override val [meta](meta.md): [UnaryOp](../../com.github.jomof.kane.impl/-unary-op/index.md)   <br>
