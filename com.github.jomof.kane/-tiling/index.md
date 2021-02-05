//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[Tiling](index.md)



# Tiling  
 [jvm] data class [Tiling](index.md)<[E](index.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)>(**columns**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), **rows**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), **data**: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[E](index.md)>, **type**: [KaneType](../../com.github.jomof.kane.types/-kane-type/index.md)<[E](index.md)>) : [TypedExpr](../-typed-expr/index.md)<[E](index.md)>    


## Functions  
  
|  Name|  Summary| 
|---|---|
| [component1](component1.md)| [jvm]  <br>Content  <br>operator fun [component1](component1.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [component2](component2.md)| [jvm]  <br>Content  <br>operator fun [component2](component2.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [component3](component3.md)| [jvm]  <br>Content  <br>operator fun [component3](component3.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[E](index.md)>  <br><br><br>
| [component4](component4.md)| [jvm]  <br>Content  <br>operator fun [component4](component4.md)(): [KaneType](../../com.github.jomof.kane.types/-kane-type/index.md)<[E](index.md)>  <br><br><br>
| [copy](copy.md)| [jvm]  <br>Content  <br>fun [copy](copy.md)(columns: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), rows: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), data: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[E](index.md)>, type: [KaneType](../../com.github.jomof.kane.types/-kane-type/index.md)<[E](index.md)>): [Tiling](index.md)<[E](index.md)>  <br><br><br>
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [getUnnamedElement](get-unnamed-element.md)| [jvm]  <br>Content  <br>fun [getUnnamedElement](get-unnamed-element.md)(coordinate: [Coordinate](../-coordinate/index.md)): [Expr](../-expr/index.md)  <br><br><br>
| [getValue](get-value.md)| [jvm]  <br>Content  <br>operator fun [getValue](get-value.md)(thisRef: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?, property: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)<*>): [NamedTiling](../-named-tiling/index.md)<[E](index.md)>  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toNamedTilingExpr](to-named-tiling-expr.md)| [jvm]  <br>Content  <br>fun [toNamedTilingExpr](to-named-tiling-expr.md)(name: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)): [NamedTiling](../-named-tiling/index.md)<[E](index.md)>  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [columns](index.md#com.github.jomof.kane/Tiling/columns/#/PointingToDeclaration/)|  [jvm] val [columns](index.md#com.github.jomof.kane/Tiling/columns/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [data](index.md#com.github.jomof.kane/Tiling/data/#/PointingToDeclaration/)|  [jvm] val [data](index.md#com.github.jomof.kane/Tiling/data/#/PointingToDeclaration/): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[E](index.md)>   <br>
| [rows](index.md#com.github.jomof.kane/Tiling/rows/#/PointingToDeclaration/)|  [jvm] val [rows](index.md#com.github.jomof.kane/Tiling/rows/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [type](index.md#com.github.jomof.kane/Tiling/type/#/PointingToDeclaration/)|  [jvm] open override val [type](index.md#com.github.jomof.kane/Tiling/type/#/PointingToDeclaration/): [KaneType](../../com.github.jomof.kane.types/-kane-type/index.md)<[E](index.md)>   <br>


## Extensions  
  
|  Name|  Summary| 
|---|---|
| [toNamed](../to-named.md)| [jvm]  <br>Content  <br>fun <[E](../to-named.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)> [Tiling](index.md)<[E](../to-named.md)>.[toNamed](../to-named.md)(name: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)): [NamedTiling](../-named-tiling/index.md)<[E](../to-named.md)>  <br><br><br>

