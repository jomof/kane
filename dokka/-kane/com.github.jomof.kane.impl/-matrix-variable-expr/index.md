//[Kane](../../index.md)/[com.github.jomof.kane.impl](../index.md)/[MatrixVariableExpr](index.md)



# MatrixVariableExpr  
 [jvm] interface [MatrixVariableExpr](index.md) : [MatrixExpr](../../com.github.jomof.kane/-matrix-expr/index.md), [VariableExpr](../-variable-expr/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-636154559)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-636154559)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="com.github.jomof.kane/MatrixExpr/get/#kotlin.Int#kotlin.Int/PointingToDeclaration/"></a>[get](../../com.github.jomof.kane/-matrix-expr/get.md)| <a name="com.github.jomof.kane/MatrixExpr/get/#kotlin.Int#kotlin.Int/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract operator fun [get](../../com.github.jomof.kane/-matrix-expr/get.md)(column: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), row: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br><br><br>
| <a name="com.github.jomof.kane/MatrixExpr/getValue/#kotlin.Any?#kotlin.reflect.KProperty[*]/PointingToDeclaration/"></a>[getValue](../../com.github.jomof.kane/-matrix-expr/get-value.md)| <a name="com.github.jomof.kane/MatrixExpr/getValue/#kotlin.Any?#kotlin.reflect.KProperty[*]/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [getValue](../../com.github.jomof.kane/-matrix-expr/get-value.md)(thisRef: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?, property: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)<*>): [NamedMatrix](../-named-matrix/index.md)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-636154559)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-636154559)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../com.github.jomof.kane.impl.types/-object-kane-type/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-636154559)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../com.github.jomof.kane.impl.types/-object-kane-type/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-636154559)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl/MatrixVariableExpr/columns/#/PointingToDeclaration/"></a>[columns](index.md#%5Bcom.github.jomof.kane.impl%2FMatrixVariableExpr%2Fcolumns%2F%23%2FPointingToDeclaration%2F%5D%2FProperties%2F-636154559)| <a name="com.github.jomof.kane.impl/MatrixVariableExpr/columns/#/PointingToDeclaration/"></a> [jvm] abstract val [columns](index.md#%5Bcom.github.jomof.kane.impl%2FMatrixVariableExpr%2Fcolumns%2F%23%2FPointingToDeclaration%2F%5D%2FProperties%2F-636154559): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| <a name="com.github.jomof.kane.impl/MatrixVariableExpr/rows/#/PointingToDeclaration/"></a>[rows](index.md#%5Bcom.github.jomof.kane.impl%2FMatrixVariableExpr%2Frows%2F%23%2FPointingToDeclaration%2F%5D%2FProperties%2F-636154559)| <a name="com.github.jomof.kane.impl/MatrixVariableExpr/rows/#/PointingToDeclaration/"></a> [jvm] abstract val [rows](index.md#%5Bcom.github.jomof.kane.impl%2FMatrixVariableExpr%2Frows%2F%23%2FPointingToDeclaration%2F%5D%2FProperties%2F-636154559): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>


## Inheritors  
  
|  Name| 
|---|
| <a name="com.github.jomof.kane.impl/NamedMatrixVariable///PointingToDeclaration/"></a>[NamedMatrixVariable](../-named-matrix-variable/index.md)

