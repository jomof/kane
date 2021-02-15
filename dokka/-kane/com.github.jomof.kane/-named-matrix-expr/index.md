//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[NamedMatrixExpr](index.md)



# NamedMatrixExpr  
 [jvm] interface [NamedMatrixExpr](index.md) : [NamedAlgebraicExpr](../-named-algebraic-expr/index.md), [MatrixExpr](../-matrix-expr/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-723507148)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-723507148)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="com.github.jomof.kane/MatrixExpr/getValue/#kotlin.Any?#kotlin.reflect.KProperty[*]/PointingToDeclaration/"></a>[getValue](../-matrix-expr/get-value.md)| <a name="com.github.jomof.kane/MatrixExpr/getValue/#kotlin.Any?#kotlin.reflect.KProperty[*]/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [getValue](../-matrix-expr/get-value.md)(thisRef: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?, property: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)<*>): [MatrixExpr](../-matrix-expr/index.md)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-723507148)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-723507148)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-723507148)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-723507148)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane/NamedMatrixExpr/name/#/PointingToDeclaration/"></a>[name](index.md#%5Bcom.github.jomof.kane%2FNamedMatrixExpr%2Fname%2F%23%2FPointingToDeclaration%2F%5D%2FProperties%2F-723507148)| <a name="com.github.jomof.kane/NamedMatrixExpr/name/#/PointingToDeclaration/"></a> [jvm] abstract val [name](index.md#%5Bcom.github.jomof.kane%2FNamedMatrixExpr%2Fname%2F%23%2FPointingToDeclaration%2F%5D%2FProperties%2F-723507148): [Id](../../com.github.jomof.kane.impl/index.md#%5Bcom.github.jomof.kane.impl%2FId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-723507148)   <br>


## Inheritors  
  
|  Name| 
|---|
| <a name="com.github.jomof.kane.impl/NamedMatrixVariable///PointingToDeclaration/"></a>[NamedMatrixVariable](../../com.github.jomof.kane.impl/-named-matrix-variable/index.md)
| <a name="com.github.jomof.kane.impl/NamedMatrix///PointingToDeclaration/"></a>[NamedMatrix](../../com.github.jomof.kane.impl/-named-matrix/index.md)


## Extensions  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane//eval/com.github.jomof.kane.NamedMatrixExpr#com.github.jomof.kane.impl.sheet.RangeExprProvider?#kotlin.Boolean#kotlin.collections.Set[kotlin.Any]/PointingToDeclaration/"></a>[eval](../eval.md)| <a name="com.github.jomof.kane//eval/com.github.jomof.kane.NamedMatrixExpr#com.github.jomof.kane.impl.sheet.RangeExprProvider?#kotlin.Boolean#kotlin.collections.Set[kotlin.Any]/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [NamedMatrixExpr](index.md).[eval](../eval.md)(rangeExprProvider: [RangeExprProvider](../../com.github.jomof.kane.impl.sheet/-range-expr-provider/index.md)? = null, reduceVariables: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = false, excludeVariables: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[Id](../../com.github.jomof.kane.impl/index.md#%5Bcom.github.jomof.kane.impl%2FId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-723507148)> = setOf()): [NamedMatrixExpr](index.md)  <br><br><br>

