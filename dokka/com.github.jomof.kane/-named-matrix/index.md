//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[NamedMatrix](index.md)



# NamedMatrix  
 [jvm] data class [NamedMatrix](index.md)(**name**: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html), **matrix**: [MatrixExpr](../-matrix-expr/index.md)) : [NamedMatrixExpr](../-named-matrix-expr/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [component1](component1.md)| [jvm]  <br>Content  <br>operator fun [component1](component1.md)(): [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)  <br><br><br>
| [component2](component2.md)| [jvm]  <br>Content  <br>operator fun [component2](component2.md)(): [MatrixExpr](../-matrix-expr/index.md)  <br><br><br>
| [copy](copy.md)| [jvm]  <br>Content  <br>fun [copy](copy.md)(name: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html), matrix: [MatrixExpr](../-matrix-expr/index.md)): [NamedMatrix](index.md)  <br><br><br>
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [get](get.md)| [jvm]  <br>Content  <br>open operator override fun [get](get.md)(column: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), row: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [ScalarExpr](../-scalar-expr/index.md)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](to-string.md)| [jvm]  <br>Content  <br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [columns](index.md#com.github.jomof.kane/NamedMatrix/columns/#/PointingToDeclaration/)|  [jvm] open override val [columns](index.md#com.github.jomof.kane/NamedMatrix/columns/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [matrix](index.md#com.github.jomof.kane/NamedMatrix/matrix/#/PointingToDeclaration/)|  [jvm] val [matrix](index.md#com.github.jomof.kane/NamedMatrix/matrix/#/PointingToDeclaration/): [MatrixExpr](../-matrix-expr/index.md)   <br>
| [name](index.md#com.github.jomof.kane/NamedMatrix/name/#/PointingToDeclaration/)|  [jvm] open override val [name](index.md#com.github.jomof.kane/NamedMatrix/name/#/PointingToDeclaration/): [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)   <br>
| [rows](index.md#com.github.jomof.kane/NamedMatrix/rows/#/PointingToDeclaration/)|  [jvm] open override val [rows](index.md#com.github.jomof.kane/NamedMatrix/rows/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>


## Extensions  
  
|  Name|  Summary| 
|---|---|
| [describe](../../com.github.jomof.kane.sheet/describe.md)| [jvm]  <br>Brief description  <br><br><br>Return a new sheet with data summarized into statistics<br><br>  <br>Content  <br>fun [NamedMatrix](index.md).[describe](../../com.github.jomof.kane.sheet/describe.md)(): [Sheet](../../com.github.jomof.kane.sheet/-sheet/index.md)  <br><br><br>
| [eval](../eval.md)| [jvm]  <br>Content  <br>fun [NamedMatrix](index.md).[eval](../eval.md)(rangeExprProvider: [RangeExprProvider](../../com.github.jomof.kane.sheet/-range-expr-provider/index.md), reduceVariables: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), excludeVariables: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)>): [NamedMatrix](index.md)  <br><br><br>

