//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[IAlgebraicSummaryScalarScalarFunction](index.md)



# IAlgebraicSummaryScalarScalarFunction  
 [jvm] interface [IAlgebraicSummaryScalarScalarFunction](index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane/IAlgebraicSummaryScalarScalarFunction/differentiate/#com.github.jomof.kane.ScalarExpr#com.github.jomof.kane.ScalarExpr#com.github.jomof.kane.ScalarExpr/PointingToDeclaration/"></a>[differentiate](differentiate.md)| <a name="com.github.jomof.kane/IAlgebraicSummaryScalarScalarFunction/differentiate/#com.github.jomof.kane.ScalarExpr#com.github.jomof.kane.ScalarExpr#com.github.jomof.kane.ScalarExpr/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun [differentiate](differentiate.md)(value: [ScalarExpr](../-scalar-expr/index.md), valued: [ScalarExpr](../-scalar-expr/index.md), variable: [ScalarExpr](../-scalar-expr/index.md)): [ScalarExpr](../-scalar-expr/index.md)  <br><br><br>
| <a name="com.github.jomof.kane/IAlgebraicSummaryScalarScalarFunction/doubleOp/#kotlin.Double/PointingToDeclaration/"></a>[doubleOp](double-op.md)| <a name="com.github.jomof.kane/IAlgebraicSummaryScalarScalarFunction/doubleOp/#kotlin.Double/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun [doubleOp](double-op.md)(value: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br><br><br>
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1712679262)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1712679262)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1712679262)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1712679262)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="com.github.jomof.kane/IAlgebraicSummaryScalarScalarFunction/invoke/#com.github.jomof.kane.ScalarExpr/PointingToDeclaration/"></a>[invoke](invoke.md)| <a name="com.github.jomof.kane/IAlgebraicSummaryScalarScalarFunction/invoke/#com.github.jomof.kane.ScalarExpr/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [invoke](invoke.md)(value: [ScalarExpr](../-scalar-expr/index.md)): [ScalarExpr](../-scalar-expr/index.md)  <br>open operator fun [invoke](invoke.md)(value: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)): [ScalarExpr](../-scalar-expr/index.md)  <br><br><br>
| <a name="com.github.jomof.kane/IAlgebraicSummaryScalarScalarFunction/reduceArithmetic/#com.github.jomof.kane.ScalarExpr/PointingToDeclaration/"></a>[reduceArithmetic](reduce-arithmetic.md)| <a name="com.github.jomof.kane/IAlgebraicSummaryScalarScalarFunction/reduceArithmetic/#com.github.jomof.kane.ScalarExpr/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun [reduceArithmetic](reduce-arithmetic.md)(value: [ScalarExpr](../-scalar-expr/index.md)): [ScalarExpr](../-scalar-expr/index.md)?  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1712679262)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1712679262)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| <a name="com.github.jomof.kane/IAlgebraicSummaryScalarScalarFunction/type/#com.github.jomof.kane.impl.types.AlgebraicType/PointingToDeclaration/"></a>[type](type.md)| <a name="com.github.jomof.kane/IAlgebraicSummaryScalarScalarFunction/type/#com.github.jomof.kane.impl.types.AlgebraicType/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun [type](type.md)(value: [AlgebraicType](../../com.github.jomof.kane.impl.types/-algebraic-type/index.md)): [AlgebraicType](../../com.github.jomof.kane.impl.types/-algebraic-type/index.md)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane/IAlgebraicSummaryScalarScalarFunction/meta/#/PointingToDeclaration/"></a>[meta](meta.md)| <a name="com.github.jomof.kane/IAlgebraicSummaryScalarScalarFunction/meta/#/PointingToDeclaration/"></a> [jvm] abstract val [meta](meta.md): [SummaryOp](../../com.github.jomof.kane.impl/-summary-op/index.md)   <br>


## Inheritors  
  
|  Name| 
|---|
| <a name="com.github.jomof.kane.impl.functions/AlgebraicSummaryFunction///PointingToDeclaration/"></a>[AlgebraicSummaryFunction](../../com.github.jomof.kane.impl.functions/-algebraic-summary-function/index.md)

