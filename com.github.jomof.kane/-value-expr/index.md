//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[ValueExpr](index.md)



# ValueExpr  
 [jvm] data class [ValueExpr](index.md)<[E](index.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)>(**value**: [E](index.md), **type**: [KaneType](../../com.github.jomof.kane.types/-kane-type/index.md)<[E](index.md)>) : [TypedExpr](../-typed-expr/index.md)<[E](index.md)>    


## Functions  
  
|  Name|  Summary| 
|---|---|
| [component1](component1.md)| [jvm]  <br>Content  <br>operator fun [component1](component1.md)(): [E](index.md)  <br><br><br>
| [component2](component2.md)| [jvm]  <br>Content  <br>operator fun [component2](component2.md)(): [KaneType](../../com.github.jomof.kane.types/-kane-type/index.md)<[E](index.md)>  <br><br><br>
| [copy](copy.md)| [jvm]  <br>Content  <br>fun [copy](copy.md)(value: [E](index.md), type: [KaneType](../../com.github.jomof.kane.types/-kane-type/index.md)<[E](index.md)>): [ValueExpr](index.md)<[E](index.md)>  <br><br><br>
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [getValue](get-value.md)| [jvm]  <br>Content  <br>operator fun [getValue](get-value.md)(thisRef: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?, property: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)<*>): [NamedValueExpr](../-named-value-expr/index.md)<[E](index.md)>  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toNamedValueExpr](to-named-value-expr.md)| [jvm]  <br>Content  <br>fun [toNamedValueExpr](to-named-value-expr.md)(name: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)): [NamedValueExpr](../-named-value-expr/index.md)<[E](index.md)>  <br><br><br>
| [toString](to-string.md)| [jvm]  <br>Content  <br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [type](index.md#com.github.jomof.kane/ValueExpr/type/#/PointingToDeclaration/)|  [jvm] open override val [type](index.md#com.github.jomof.kane/ValueExpr/type/#/PointingToDeclaration/): [KaneType](../../com.github.jomof.kane.types/-kane-type/index.md)<[E](index.md)>   <br>
| [value](index.md#com.github.jomof.kane/ValueExpr/value/#/PointingToDeclaration/)|  [jvm] val [value](index.md#com.github.jomof.kane/ValueExpr/value/#/PointingToDeclaration/): [E](index.md)   <br>


## Extensions  
  
|  Name|  Summary| 
|---|---|
| [toNamed](../to-named.md)| [jvm]  <br>Content  <br>fun <[E](../to-named.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)> [ValueExpr](index.md)<[E](../to-named.md)>.[toNamed](../to-named.md)(name: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)): [NamedValueExpr](../-named-value-expr/index.md)<[E](../to-named.md)>  <br><br><br>

