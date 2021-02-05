//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[NamedScalar](index.md)



# NamedScalar  
 [jvm] data class [NamedScalar](index.md)(**name**: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html), **scalar**: [ScalarExpr](../-scalar-expr/index.md)) : [NamedScalarExpr](../-named-scalar-expr/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [component1](component1.md)| [jvm]  <br>Content  <br>operator fun [component1](component1.md)(): [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)  <br><br><br>
| [component2](component2.md)| [jvm]  <br>Content  <br>operator fun [component2](component2.md)(): [ScalarExpr](../-scalar-expr/index.md)  <br><br><br>
| [copy](copy.md)| [jvm]  <br>Content  <br>fun [copy](copy.md)(name: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html), scalar: [ScalarExpr](../-scalar-expr/index.md)): [NamedScalar](index.md)  <br><br><br>
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](to-string.md)| [jvm]  <br>Content  <br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [name](index.md#com.github.jomof.kane/NamedScalar/name/#/PointingToDeclaration/)|  [jvm] open override val [name](index.md#com.github.jomof.kane/NamedScalar/name/#/PointingToDeclaration/): [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)   <br>
| [scalar](index.md#com.github.jomof.kane/NamedScalar/scalar/#/PointingToDeclaration/)|  [jvm] val [scalar](index.md#com.github.jomof.kane/NamedScalar/scalar/#/PointingToDeclaration/): [ScalarExpr](../-scalar-expr/index.md)   <br>


## Extensions  
  
|  Name|  Summary| 
|---|---|
| [eval](../eval.md)| [jvm]  <br>Content  <br>fun [NamedScalar](index.md).[eval](../eval.md)(rangeExprProvider: [RangeExprProvider](../../com.github.jomof.kane.sheet/-range-expr-provider/index.md), reduceVariables: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), excludeVariables: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)>): [NamedScalar](index.md)  <br><br><br>

