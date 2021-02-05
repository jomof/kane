//[Kane](../../index.md)/[com.github.jomof.kane.impl](../index.md)/[AlgebraicExpr](index.md)



# AlgebraicExpr  
 [jvm] interface [AlgebraicExpr](index.md) : [Expr](../-expr/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1754281934)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1754281934)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1754281934)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1754281934)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../com.github.jomof.kane.impl.types/-object-kane-type/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1754281934)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../com.github.jomof.kane.impl.types/-object-kane-type/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1754281934)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Inheritors  
  
|  Name| 
|---|
| <a name="com.github.jomof.kane.impl/ScalarExpr///PointingToDeclaration/"></a>[ScalarExpr](../-scalar-expr/index.md)
| <a name="com.github.jomof.kane.impl/MatrixExpr///PointingToDeclaration/"></a>[MatrixExpr](../-matrix-expr/index.md)
| <a name="com.github.jomof.kane.impl/VariableExpr///PointingToDeclaration/"></a>[VariableExpr](../-variable-expr/index.md)
| <a name="com.github.jomof.kane.impl/NamedAlgebraicExpr///PointingToDeclaration/"></a>[NamedAlgebraicExpr](../-named-algebraic-expr/index.md)
| <a name="com.github.jomof.kane.impl/ScalarVariable///PointingToDeclaration/"></a>[ScalarVariable](../-scalar-variable/index.md)
| <a name="com.github.jomof.kane.impl/MatrixVariable///PointingToDeclaration/"></a>[MatrixVariable](../-matrix-variable/index.md)
| <a name="com.github.jomof.kane.impl/Tableau///PointingToDeclaration/"></a>[Tableau](../-tableau/index.md)


## Extensions  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl.types//algebraicType/com.github.jomof.kane.impl.AlgebraicExpr#/PointingToDeclaration/"></a>[algebraicType](../../com.github.jomof.kane.impl.types/algebraic-type.md)| <a name="com.github.jomof.kane.impl.types//algebraicType/com.github.jomof.kane.impl.AlgebraicExpr#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>val [AlgebraicExpr](index.md).[algebraicType](../../com.github.jomof.kane.impl.types/algebraic-type.md): [AlgebraicType](../../com.github.jomof.kane.impl.types/-algebraic-type/index.md)  <br><br><br>
| <a name="com.github.jomof.kane.impl//rollUpCommonSubexpressions/com.github.jomof.kane.impl.AlgebraicExpr#com.github.jomof.kane.impl.LinearModel/PointingToDeclaration/"></a>[rollUpCommonSubexpressions](../roll-up-common-subexpressions.md)| <a name="com.github.jomof.kane.impl//rollUpCommonSubexpressions/com.github.jomof.kane.impl.AlgebraicExpr#com.github.jomof.kane.impl.LinearModel/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [AlgebraicExpr](index.md).[rollUpCommonSubexpressions](../roll-up-common-subexpressions.md)(model: [LinearModel](../-linear-model/index.md)): [AlgebraicExpr](index.md)  <br><br><br>
| <a name="com.github.jomof.kane.impl//toNamed/com.github.jomof.kane.impl.AlgebraicExpr#kotlin.Any/PointingToDeclaration/"></a>[toNamed](../to-named.md)| <a name="com.github.jomof.kane.impl//toNamed/com.github.jomof.kane.impl.AlgebraicExpr#kotlin.Any/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [AlgebraicExpr](index.md).[toNamed](../to-named.md)(name: [Id](../index.md#%5Bcom.github.jomof.kane.impl%2FId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-1754281934)): [NamedAlgebraicExpr](../-named-algebraic-expr/index.md)  <br><br><br>

