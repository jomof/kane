//[Kane](../../index.md)/[com.github.jomof.kane.impl](../index.md)/[ValueExpr](index.md)



# ValueExpr  
 [jvm] data class [ValueExpr](index.md)<[E](index.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)>(**value**: [E](index.md), **type**: [KaneType](../../com.github.jomof.kane.impl.types/-kane-type/index.md)<[E](index.md)>) : [TypedExpr](../../com.github.jomof.kane/-typed-expr/index.md)<[E](index.md)>    


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl/ValueExpr/component1/#/PointingToDeclaration/"></a>[component1](component1.md)| <a name="com.github.jomof.kane.impl/ValueExpr/component1/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>operator fun [component1](component1.md)(): [E](index.md)  <br><br><br>
| <a name="com.github.jomof.kane.impl/ValueExpr/component2/#/PointingToDeclaration/"></a>[component2](component2.md)| <a name="com.github.jomof.kane.impl/ValueExpr/component2/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>operator fun [component2](component2.md)(): [KaneType](../../com.github.jomof.kane.impl.types/-kane-type/index.md)<[E](index.md)>  <br><br><br>
| <a name="com.github.jomof.kane.impl/ValueExpr/copy/#TypeParam(bounds=[kotlin.Any])#com.github.jomof.kane.impl.types.KaneType[TypeParam(bounds=[kotlin.Any])]/PointingToDeclaration/"></a>[copy](copy.md)| <a name="com.github.jomof.kane.impl/ValueExpr/copy/#TypeParam(bounds=[kotlin.Any])#com.github.jomof.kane.impl.types.KaneType[TypeParam(bounds=[kotlin.Any])]/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [copy](copy.md)(value: [E](index.md), type: [KaneType](../../com.github.jomof.kane.impl.types/-kane-type/index.md)<[E](index.md)>): [ValueExpr](index.md)<[E](index.md)>  <br><br><br>
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1150653554)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator override fun [equals](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1150653554)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.impl/ValueExpr/getValue/#kotlin.Any?#kotlin.reflect.KProperty[*]/PointingToDeclaration/"></a>[getValue](get-value.md)| <a name="com.github.jomof.kane.impl/ValueExpr/getValue/#kotlin.Any?#kotlin.reflect.KProperty[*]/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>operator fun [getValue](get-value.md)(thisRef: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?, property: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)<*>): [NamedValueExpr](../-named-value-expr/index.md)<[E](index.md)>  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1150653554)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [hashCode](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1150653554)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.impl/ValueExpr/toNamedValueExpr/#kotlin.Any/PointingToDeclaration/"></a>[toNamedValueExpr](to-named-value-expr.md)| <a name="com.github.jomof.kane.impl/ValueExpr/toNamedValueExpr/#kotlin.Any/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [toNamedValueExpr](to-named-value-expr.md)(name: [Id](../index.md#%5Bcom.github.jomof.kane.impl%2FId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-1150653554)): [NamedValueExpr](../-named-value-expr/index.md)<[E](index.md)>  <br><br><br>
| <a name="com.github.jomof.kane.impl/ValueExpr/toString/#/PointingToDeclaration/"></a>[toString](to-string.md)| <a name="com.github.jomof.kane.impl/ValueExpr/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl/ValueExpr/type/#/PointingToDeclaration/"></a>[type](type.md)| <a name="com.github.jomof.kane.impl/ValueExpr/type/#/PointingToDeclaration/"></a> [jvm] open override val [type](type.md): [KaneType](../../com.github.jomof.kane.impl.types/-kane-type/index.md)<[E](index.md)>   <br>
| <a name="com.github.jomof.kane.impl/ValueExpr/value/#/PointingToDeclaration/"></a>[value](value.md)| <a name="com.github.jomof.kane.impl/ValueExpr/value/#/PointingToDeclaration/"></a> [jvm] val [value](value.md): [E](index.md)   <br>


## Extensions  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl//toNamed/com.github.jomof.kane.impl.ValueExpr[TypeParam(bounds=[kotlin.Any])]#kotlin.Any/PointingToDeclaration/"></a>[toNamed](../to-named.md)| <a name="com.github.jomof.kane.impl//toNamed/com.github.jomof.kane.impl.ValueExpr[TypeParam(bounds=[kotlin.Any])]#kotlin.Any/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun <[E](../to-named.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)> [ValueExpr](index.md)<[E](../to-named.md)>.[toNamed](../to-named.md)(name: [Id](../index.md#%5Bcom.github.jomof.kane.impl%2FId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-1150653554)): [NamedValueExpr](../-named-value-expr/index.md)<[E](../to-named.md)>  <br><br><br>

