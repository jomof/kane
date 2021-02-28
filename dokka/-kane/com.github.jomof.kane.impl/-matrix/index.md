//[Kane](../../index.md)/[com.github.jomof.kane.impl](../index.md)/[Matrix](index.md)



# Matrix  
 [jvm] interface [Matrix](index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1565197970)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1565197970)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.impl/Matrix/get/#com.github.jomof.kane.impl.Coordinate/PointingToDeclaration/"></a>[get](get.md)| <a name="com.github.jomof.kane.impl/Matrix/get/#com.github.jomof.kane.impl.Coordinate/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [get](get.md)(coordinate: [Coordinate](../-coordinate/index.md)): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br>abstract operator fun [get](get.md)(column: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), row: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1565197970)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1565197970)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1565197970)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1565197970)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl/Matrix/columns/#/PointingToDeclaration/"></a>[columns](columns.md)| <a name="com.github.jomof.kane.impl/Matrix/columns/#/PointingToDeclaration/"></a> [jvm] abstract val [columns](columns.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| <a name="com.github.jomof.kane.impl/Matrix/rows/#/PointingToDeclaration/"></a>[rows](rows.md)| <a name="com.github.jomof.kane.impl/Matrix/rows/#/PointingToDeclaration/"></a> [jvm] abstract val [rows](rows.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| <a name="com.github.jomof.kane.impl/Matrix/type/#/PointingToDeclaration/"></a>[type](type.md)| <a name="com.github.jomof.kane.impl/Matrix/type/#/PointingToDeclaration/"></a> [jvm] abstract val [type](type.md): [AlgebraicType](../../com.github.jomof.kane.impl.types/-algebraic-type/index.md)   <br>


## Inheritors  
  
|  Name| 
|---|
| <a name="com.github.jomof.kane.impl/MutableMatrix///PointingToDeclaration/"></a>[MutableMatrix](../-mutable-matrix/index.md)
| <a name="com.github.jomof.kane.impl/ValueMatrix///PointingToDeclaration/"></a>[ValueMatrix](../-value-matrix/index.md)


## Extensions  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl//forEach/com.github.jomof.kane.impl.Matrix#kotlin.Function1[kotlin.Double,kotlin.Unit]/PointingToDeclaration/"></a>[forEach](../for-each.md)| <a name="com.github.jomof.kane.impl//forEach/com.github.jomof.kane.impl.Matrix#kotlin.Function1[kotlin.Double,kotlin.Unit]/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [Matrix](index.md).[forEach](../for-each.md)(call: ([Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)) -> [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html))  <br><br><br>
| <a name="com.github.jomof.kane.impl//render/com.github.jomof.kane.impl.Matrix#/PointingToDeclaration/"></a>[render](../render.md)| <a name="com.github.jomof.kane.impl//render/com.github.jomof.kane.impl.Matrix#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [Matrix](index.md).[render](../render.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>

