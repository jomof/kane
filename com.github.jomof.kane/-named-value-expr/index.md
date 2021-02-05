//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[NamedValueExpr](index.md)



# NamedValueExpr  
 [jvm] data class [NamedValueExpr](index.md)<[E](index.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)>(**name**: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html), **value**: [E](index.md), **type**: [KaneType](../../com.github.jomof.kane.types/-kane-type/index.md)<[E](index.md)>) : [NamedExpr](../-named-expr/index.md), [TypedExpr](../-typed-expr/index.md)<[E](index.md)>    


## Functions  
  
|  Name|  Summary| 
|---|---|
| [component1](component1.md)| [jvm]  <br>Content  <br>operator fun [component1](component1.md)(): [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)  <br><br><br>
| [component2](component2.md)| [jvm]  <br>Content  <br>operator fun [component2](component2.md)(): [E](index.md)  <br><br><br>
| [component3](component3.md)| [jvm]  <br>Content  <br>operator fun [component3](component3.md)(): [KaneType](../../com.github.jomof.kane.types/-kane-type/index.md)<[E](index.md)>  <br><br><br>
| [copy](copy.md)| [jvm]  <br>Content  <br>fun [copy](copy.md)(name: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html), value: [E](index.md), type: [KaneType](../../com.github.jomof.kane.types/-kane-type/index.md)<[E](index.md)>): [NamedValueExpr](index.md)<[E](index.md)>  <br><br><br>
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](to-string.md)| [jvm]  <br>Content  <br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [toUnnamedValueExpr](to-unnamed-value-expr.md)| [jvm]  <br>Content  <br>fun [toUnnamedValueExpr](to-unnamed-value-expr.md)(): [ValueExpr](../-value-expr/index.md)<[E](index.md)>  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [name](index.md#com.github.jomof.kane/NamedValueExpr/name/#/PointingToDeclaration/)|  [jvm] open override val [name](index.md#com.github.jomof.kane/NamedValueExpr/name/#/PointingToDeclaration/): [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)   <br>
| [type](index.md#com.github.jomof.kane/NamedValueExpr/type/#/PointingToDeclaration/)|  [jvm] open override val [type](index.md#com.github.jomof.kane/NamedValueExpr/type/#/PointingToDeclaration/): [KaneType](../../com.github.jomof.kane.types/-kane-type/index.md)<[E](index.md)>   <br>
| [value](index.md#com.github.jomof.kane/NamedValueExpr/value/#/PointingToDeclaration/)|  [jvm] val [value](index.md#com.github.jomof.kane/NamedValueExpr/value/#/PointingToDeclaration/): [E](index.md)   <br>

