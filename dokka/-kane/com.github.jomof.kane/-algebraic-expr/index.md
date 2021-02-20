//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[AlgebraicExpr](index.md)



# AlgebraicExpr  
 [jvm] interface [AlgebraicExpr](index.md) : [Expr](../-expr/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-2059381145)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-2059381145)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-2059381145)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-2059381145)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-2059381145)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-2059381145)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Inheritors  
  
|  Name| 
|---|
| <a name="com.github.jomof.kane/NamedAlgebraicExpr///PointingToDeclaration/"></a>[NamedAlgebraicExpr](../-named-algebraic-expr/index.md)
| <a name="com.github.jomof.kane/ScalarExpr///PointingToDeclaration/"></a>[ScalarExpr](../-scalar-expr/index.md)
| <a name="com.github.jomof.kane/MatrixExpr///PointingToDeclaration/"></a>[MatrixExpr](../-matrix-expr/index.md)
| <a name="com.github.jomof.kane.impl/VariableExpr///PointingToDeclaration/"></a>[VariableExpr](../../com.github.jomof.kane.impl/-variable-expr/index.md)
| <a name="com.github.jomof.kane.impl/ScalarVariable///PointingToDeclaration/"></a>[ScalarVariable](../../com.github.jomof.kane.impl/-scalar-variable/index.md)
| <a name="com.github.jomof.kane.impl/MatrixVariable///PointingToDeclaration/"></a>[MatrixVariable](../../com.github.jomof.kane.impl/-matrix-variable/index.md)
| <a name="com.github.jomof.kane.impl/Tableau///PointingToDeclaration/"></a>[Tableau](../../com.github.jomof.kane.impl/-tableau/index.md)
| <a name="com.github.jomof.kane.impl/ScalarAssign///PointingToDeclaration/"></a>[ScalarAssign](../../com.github.jomof.kane.impl/-scalar-assign/index.md)
| <a name="com.github.jomof.kane.impl/MatrixAssign///PointingToDeclaration/"></a>[MatrixAssign](../../com.github.jomof.kane.impl/-matrix-assign/index.md)


## Extensions  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl.types//algebraicType/com.github.jomof.kane.AlgebraicExpr#/PointingToDeclaration/"></a>[algebraicType](../../com.github.jomof.kane.impl.types/algebraic-type.md)| <a name="com.github.jomof.kane.impl.types//algebraicType/com.github.jomof.kane.AlgebraicExpr#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>val [AlgebraicExpr](index.md).[algebraicType](../../com.github.jomof.kane.impl.types/algebraic-type.md): [AlgebraicType](../../com.github.jomof.kane.impl.types/-algebraic-type/index.md)  <br><br><br>
| <a name="com.github.jomof.kane//eval/com.github.jomof.kane.AlgebraicExpr#com.github.jomof.kane.impl.sheet.RangeExprProvider?#kotlin.Boolean#kotlin.collections.Set[kotlin.Any]/PointingToDeclaration/"></a>[eval](../eval.md)| <a name="com.github.jomof.kane//eval/com.github.jomof.kane.AlgebraicExpr#com.github.jomof.kane.impl.sheet.RangeExprProvider?#kotlin.Boolean#kotlin.collections.Set[kotlin.Any]/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [AlgebraicExpr](index.md).[eval](../eval.md)(rangeExprProvider: [RangeExprProvider](../../com.github.jomof.kane.impl.sheet/-range-expr-provider/index.md)? = null, reduceVariables: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = false, excludeVariables: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[Id](../../com.github.jomof.kane.impl/index.md#%5Bcom.github.jomof.kane.impl%2FId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-2059381145)> = setOf()): [AlgebraicExpr](index.md)  <br><br><br>
| <a name="com.github.jomof.kane.impl//rollUpCommonSubexpressions/com.github.jomof.kane.AlgebraicExpr#com.github.jomof.kane.impl.LinearModel/PointingToDeclaration/"></a>[rollUpCommonSubexpressions](../../com.github.jomof.kane.impl/roll-up-common-subexpressions.md)| <a name="com.github.jomof.kane.impl//rollUpCommonSubexpressions/com.github.jomof.kane.AlgebraicExpr#com.github.jomof.kane.impl.LinearModel/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [AlgebraicExpr](index.md).[rollUpCommonSubexpressions](../../com.github.jomof.kane.impl/roll-up-common-subexpressions.md)(model: [LinearModel](../../com.github.jomof.kane.impl/-linear-model/index.md)): [AlgebraicExpr](index.md)  <br><br><br>
| <a name="com.github.jomof.kane.impl//toNamed/com.github.jomof.kane.AlgebraicExpr#kotlin.Any/PointingToDeclaration/"></a>[toNamed](../../com.github.jomof.kane.impl/to-named.md)| <a name="com.github.jomof.kane.impl//toNamed/com.github.jomof.kane.AlgebraicExpr#kotlin.Any/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [AlgebraicExpr](index.md).[toNamed](../../com.github.jomof.kane.impl/to-named.md)(name: [Id](../../com.github.jomof.kane.impl/index.md#%5Bcom.github.jomof.kane.impl%2FId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-2059381145)): [NamedAlgebraicExpr](../-named-algebraic-expr/index.md)  <br><br><br>
| <a name="com.github.jomof.kane.impl//withNameOf/com.github.jomof.kane.AlgebraicExpr#com.github.jomof.kane.Expr#kotlin.Function0[kotlin.Any]/PointingToDeclaration/"></a>[withNameOf](../../com.github.jomof.kane.impl/with-name-of.md)| <a name="com.github.jomof.kane.impl//withNameOf/com.github.jomof.kane.AlgebraicExpr#com.github.jomof.kane.Expr#kotlin.Function0[kotlin.Any]/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [AlgebraicExpr](index.md).[withNameOf](../../com.github.jomof.kane.impl/with-name-of.md)(other: [Expr](../-expr/index.md), name: () -> [Id](../../com.github.jomof.kane.impl/index.md#%5Bcom.github.jomof.kane.impl%2FId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-2059381145) = { other.name }): [AlgebraicExpr](index.md)  <br><br><br>

