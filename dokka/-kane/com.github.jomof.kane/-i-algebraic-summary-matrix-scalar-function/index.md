//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[IAlgebraicSummaryMatrixScalarFunction](index.md)



# IAlgebraicSummaryMatrixScalarFunction  
 [jvm] interface [IAlgebraicSummaryMatrixScalarFunction](index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane/IAlgebraicSummaryMatrixScalarFunction/differentiate/#com.github.jomof.kane.MatrixExpr#com.github.jomof.kane.MatrixExpr#com.github.jomof.kane.ScalarExpr/PointingToDeclaration/"></a>[differentiate](differentiate.md)| <a name="com.github.jomof.kane/IAlgebraicSummaryMatrixScalarFunction/differentiate/#com.github.jomof.kane.MatrixExpr#com.github.jomof.kane.MatrixExpr#com.github.jomof.kane.ScalarExpr/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun [differentiate](differentiate.md)(value: [MatrixExpr](../-matrix-expr/index.md), valued: [MatrixExpr](../-matrix-expr/index.md), variable: [ScalarExpr](../-scalar-expr/index.md)): [ScalarExpr](../-scalar-expr/index.md)  <br><br><br>
| <a name="com.github.jomof.kane/IAlgebraicSummaryMatrixScalarFunction/doubleOp/#kotlin.collections.List[kotlin.Double]/PointingToDeclaration/"></a>[doubleOp](double-op.md)| <a name="com.github.jomof.kane/IAlgebraicSummaryMatrixScalarFunction/doubleOp/#kotlin.collections.List[kotlin.Double]/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun [doubleOp](double-op.md)(value: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)>): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br><br><br>
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-912601781)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-912601781)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-912601781)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-912601781)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="com.github.jomof.kane/IAlgebraicSummaryMatrixScalarFunction/invoke/#com.github.jomof.kane.MatrixExpr/PointingToDeclaration/"></a>[invoke](invoke.md)| <a name="com.github.jomof.kane/IAlgebraicSummaryMatrixScalarFunction/invoke/#com.github.jomof.kane.MatrixExpr/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [invoke](invoke.md)(value: [MatrixExpr](../-matrix-expr/index.md)): [ScalarExpr](../-scalar-expr/index.md)  <br>open operator fun [invoke](invoke.md)(value: [SheetRangeExpr](../../com.github.jomof.kane.impl.sheet/-sheet-range-expr/index.md)): [ScalarExpr](../-scalar-expr/index.md)  <br><br><br>
| <a name="com.github.jomof.kane/IAlgebraicSummaryMatrixScalarFunction/reduceArithmetic/#com.github.jomof.kane.MatrixExpr/PointingToDeclaration/"></a>[reduceArithmetic](reduce-arithmetic.md)| <a name="com.github.jomof.kane/IAlgebraicSummaryMatrixScalarFunction/reduceArithmetic/#com.github.jomof.kane.MatrixExpr/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun [reduceArithmetic](reduce-arithmetic.md)(value: [MatrixExpr](../-matrix-expr/index.md)): [ScalarExpr](../-scalar-expr/index.md)?  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-912601781)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-912601781)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| <a name="com.github.jomof.kane/IAlgebraicSummaryMatrixScalarFunction/type/#com.github.jomof.kane.impl.types.AlgebraicType/PointingToDeclaration/"></a>[type](type.md)| <a name="com.github.jomof.kane/IAlgebraicSummaryMatrixScalarFunction/type/#com.github.jomof.kane.impl.types.AlgebraicType/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun [type](type.md)(value: [AlgebraicType](../../com.github.jomof.kane.impl.types/-algebraic-type/index.md)): [AlgebraicType](../../com.github.jomof.kane.impl.types/-algebraic-type/index.md)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane/IAlgebraicSummaryMatrixScalarFunction/meta/#/PointingToDeclaration/"></a>[meta](meta.md)| <a name="com.github.jomof.kane/IAlgebraicSummaryMatrixScalarFunction/meta/#/PointingToDeclaration/"></a> [jvm] abstract val [meta](meta.md): [SummaryOp](../../com.github.jomof.kane.impl/-summary-op/index.md)   <br>


## Inheritors  
  
|  Name| 
|---|
| <a name="com.github.jomof.kane.impl.functions/AlgebraicSummaryFunction///PointingToDeclaration/"></a>[AlgebraicSummaryFunction](../../com.github.jomof.kane.impl.functions/-algebraic-summary-function/index.md)

