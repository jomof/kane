//[Kane](../../index.md)/[com.github.jomof.kane.impl.functions](../index.md)/[AlgebraicBinaryScalarStatisticFunction](index.md)



# AlgebraicBinaryScalarStatisticFunction  
 [jvm] interface [AlgebraicBinaryScalarStatisticFunction](index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-972194031)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-972194031)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-972194031)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-972194031)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.impl.functions/AlgebraicBinaryScalarStatisticFunction/invoke/#com.github.jomof.kane.MatrixExpr#com.github.jomof.kane.ScalarExpr/PointingToDeclaration/"></a>[invoke](invoke.md)| <a name="com.github.jomof.kane.impl.functions/AlgebraicBinaryScalarStatisticFunction/invoke/#com.github.jomof.kane.MatrixExpr#com.github.jomof.kane.ScalarExpr/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [invoke](invoke.md)(left: [MatrixExpr](../../com.github.jomof.kane/-matrix-expr/index.md), right: [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)): [AlgebraicBinaryMatrixScalarStatistic](../-algebraic-binary-matrix-scalar-statistic/index.md)  <br>open operator fun [invoke](invoke.md)(left: [MatrixExpr](../../com.github.jomof.kane/-matrix-expr/index.md), right: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)): [AlgebraicBinaryMatrixScalarStatistic](../-algebraic-binary-matrix-scalar-statistic/index.md)  <br>open operator fun [invoke](invoke.md)(left: [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md), right: [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)): [AlgebraicBinaryScalarStatistic](../-algebraic-binary-scalar-statistic/index.md)  <br>open operator fun [invoke](invoke.md)(left: [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md), right: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)): [AlgebraicBinaryScalarStatistic](../-algebraic-binary-scalar-statistic/index.md)  <br><br><br>
| <a name="com.github.jomof.kane.impl.functions/AlgebraicBinaryScalarStatisticFunction/reduceArithmetic/#com.github.jomof.kane.impl.StatsiticExpr#com.github.jomof.kane.ScalarExpr/PointingToDeclaration/"></a>[reduceArithmetic](reduce-arithmetic.md)| <a name="com.github.jomof.kane.impl.functions/AlgebraicBinaryScalarStatisticFunction/reduceArithmetic/#com.github.jomof.kane.impl.StatsiticExpr#com.github.jomof.kane.ScalarExpr/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun [reduceArithmetic](reduce-arithmetic.md)(left: [StatsiticExpr](../../com.github.jomof.kane.impl/-statsitic-expr/index.md), right: [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br>abstract fun [reduceArithmetic](reduce-arithmetic.md)(left: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)>, right: [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)?  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../com.github.jomof.kane.impl.types/-object-kane-type/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-972194031)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../com.github.jomof.kane.impl.types/-object-kane-type/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-972194031)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl.functions/AlgebraicBinaryScalarStatisticFunction/meta/#/PointingToDeclaration/"></a>[meta](meta.md)| <a name="com.github.jomof.kane.impl.functions/AlgebraicBinaryScalarStatisticFunction/meta/#/PointingToDeclaration/"></a> [jvm] abstract val [meta](meta.md): [BinaryOp](../../com.github.jomof.kane.impl/-binary-op/index.md)   <br>


## Inheritors  
  
|  Name| 
|---|
| <a name="com.github.jomof.kane.impl.functions/PercentileFunction///PointingToDeclaration/"></a>[PercentileFunction](../-percentile-function/index.md)

