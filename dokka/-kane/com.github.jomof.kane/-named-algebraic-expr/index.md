//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[NamedAlgebraicExpr](index.md)



# NamedAlgebraicExpr  
 [jvm] interface [NamedAlgebraicExpr](index.md) : [NamedExpr](../-named-expr/index.md), [AlgebraicExpr](../-algebraic-expr/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1533330156)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1533330156)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1533330156)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1533330156)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1533330156)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1533330156)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane/NamedAlgebraicExpr/name/#/PointingToDeclaration/"></a>[name](index.md#%5Bcom.github.jomof.kane%2FNamedAlgebraicExpr%2Fname%2F%23%2FPointingToDeclaration%2F%5D%2FProperties%2F-1533330156)| <a name="com.github.jomof.kane/NamedAlgebraicExpr/name/#/PointingToDeclaration/"></a> [jvm] abstract val [name](index.md#%5Bcom.github.jomof.kane%2FNamedAlgebraicExpr%2Fname%2F%23%2FPointingToDeclaration%2F%5D%2FProperties%2F-1533330156): [Id](../../com.github.jomof.kane.impl/index.md#%5Bcom.github.jomof.kane.impl%2FId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-1533330156)   <br>


## Inheritors  
  
|  Name| 
|---|
| <a name="com.github.jomof.kane/NamedScalarExpr///PointingToDeclaration/"></a>[NamedScalarExpr](../-named-scalar-expr/index.md)
| <a name="com.github.jomof.kane/NamedMatrixExpr///PointingToDeclaration/"></a>[NamedMatrixExpr](../-named-matrix-expr/index.md)


## Extensions  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane//eval/com.github.jomof.kane.NamedAlgebraicExpr#com.github.jomof.kane.impl.sheet.RangeExprProvider?#kotlin.Boolean#kotlin.collections.Set[kotlin.Any]/PointingToDeclaration/"></a>[eval](../eval.md)| <a name="com.github.jomof.kane//eval/com.github.jomof.kane.NamedAlgebraicExpr#com.github.jomof.kane.impl.sheet.RangeExprProvider?#kotlin.Boolean#kotlin.collections.Set[kotlin.Any]/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [NamedAlgebraicExpr](index.md).[eval](../eval.md)(rangeExprProvider: [RangeExprProvider](../../com.github.jomof.kane.impl.sheet/-range-expr-provider/index.md)? = null, reduceVariables: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = false, excludeVariables: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[Id](../../com.github.jomof.kane.impl/index.md#%5Bcom.github.jomof.kane.impl%2FId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-1533330156)> = setOf()): [NamedAlgebraicExpr](index.md)  <br><br><br>

