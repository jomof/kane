//[Kane](../../index.md)/[com.github.jomof.kane.sheet](../index.md)/[GroupBy](index.md)



# GroupBy  
 [jvm] 

Conceptually, a GroupBy is a dictionary from key to sheet. It initially consists of [sheet](index.md#com.github.jomof.kane.sheet/GroupBy/sheet/#/PointingToDeclaration/) which is the data source and selector which is used to build a key from a row.

class [GroupBy](index.md)(**sheet**: [Sheet](../-sheet/index.md), **keySelector**: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[NamedExpr](../../com.github.jomof.kane/-named-expr/index.md)>) : [Expr](../../com.github.jomof.kane/-expr/index.md)   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| [GroupBy](-group-by.md)|  [jvm] fun [GroupBy](-group-by.md)(sheet: [Sheet](../-sheet/index.md), keySelector: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[NamedExpr](../../com.github.jomof.kane/-named-expr/index.md)>)   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [get](get.md)| [jvm]  <br>Content  <br>operator fun [get](get.md)(vararg keys: [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)<Out [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)>): [Sheet](../-sheet/index.md)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [iterator](iterator.md)| [jvm]  <br>Content  <br>operator fun [iterator](iterator.md)(): [Iterator](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterator/index.html)<[Map.Entry](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/-entry/index.html)<[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Expr](../../com.github.jomof.kane/-expr/index.md)>, [Sheet](../-sheet/index.md)>>  <br><br><br>
| [toString](to-string.md)| [jvm]  <br>Content  <br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [html](index.md#com.github.jomof.kane.sheet/GroupBy/html/#/PointingToDeclaration/)|  [jvm] val [html](index.md#com.github.jomof.kane.sheet/GroupBy/html/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [sheet](index.md#com.github.jomof.kane.sheet/GroupBy/sheet/#/PointingToDeclaration/)|  [jvm] val [sheet](index.md#com.github.jomof.kane.sheet/GroupBy/sheet/#/PointingToDeclaration/): [Sheet](../-sheet/index.md)   <br>


## Extensions  
  
|  Name|  Summary| 
|---|---|
| [aggregate](../aggregate.md)| [jvm]  <br>Content  <br>fun [GroupBy](index.md).[aggregate](../aggregate.md)(selector: [SheetBuilder](../-sheet-builder/index.md).() -> [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[NamedExpr](../../com.github.jomof.kane/-named-expr/index.md)>): [Sheet](../-sheet/index.md)  <br>fun [GroupBy](index.md).[aggregate](../aggregate.md)(vararg functions: [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)<Out [AlgebraicUnaryScalarStatisticFunction](../../com.github.jomof.kane.functions/-algebraic-unary-scalar-statistic-function/index.md)>): [Sheet](../-sheet/index.md)  <br><br><br>
| [describe](../describe.md)| [jvm]  <br>Brief description  <br><br><br>Return a new sheet with columns summarized into statistics<br><br>  <br>Content  <br>fun [GroupBy](index.md).[describe](../describe.md)(): [Sheet](../-sheet/index.md)  <br><br><br>

