//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[MatrixExpr](index.md)



# MatrixExpr  
 [jvm] interface [MatrixExpr](index.md) : [AlgebraicExpr](../-algebraic-expr/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [get](get.md)| [jvm]  <br>Content  <br>abstract operator fun [get](get.md)(column: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), row: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [ScalarExpr](../-scalar-expr/index.md)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [columns](index.md#com.github.jomof.kane/MatrixExpr/columns/#/PointingToDeclaration/)|  [jvm] abstract val [columns](index.md#com.github.jomof.kane/MatrixExpr/columns/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [rows](index.md#com.github.jomof.kane/MatrixExpr/rows/#/PointingToDeclaration/)|  [jvm] abstract val [rows](index.md#com.github.jomof.kane/MatrixExpr/rows/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>


## Inheritors  
  
|  Name| 
|---|
| [MatrixVariableExpr](../-matrix-variable-expr/index.md)
| [NamedMatrixExpr](../-named-matrix-expr/index.md)
| [RetypeMatrix](../-retype-matrix/index.md)
| [DataMatrix](../-data-matrix/index.md)
| [AlgebraicBinaryMatrixScalar](../../com.github.jomof.kane.functions/-algebraic-binary-matrix-scalar/index.md)
| [AlgebraicBinaryScalarMatrix](../../com.github.jomof.kane.functions/-algebraic-binary-scalar-matrix/index.md)
| [AlgebraicBinaryMatrix](../../com.github.jomof.kane.functions/-algebraic-binary-matrix/index.md)
| [AlgebraicUnaryMatrix](../../com.github.jomof.kane.functions/-algebraic-unary-matrix/index.md)
| [AlgebraicDeferredDataMatrix](../../com.github.jomof.kane.functions/-algebraic-deferred-data-matrix/index.md)


## Extensions  
  
|  Name|  Summary| 
|---|---|
| [coordinates](../index.md#com.github.jomof.kane//coordinates/com.github.jomof.kane.MatrixExpr#/PointingToDeclaration/)| [jvm]  <br>Content  <br>val [MatrixExpr](index.md).[coordinates](../index.md#com.github.jomof.kane//coordinates/com.github.jomof.kane.MatrixExpr#/PointingToDeclaration/): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Coordinate](../-coordinate/index.md)>  <br><br><br>
| [cross](../../com.github.jomof.kane.functions/cross.md)| [jvm]  <br>Content  <br>infix fun [MatrixExpr](index.md).[cross](../../com.github.jomof.kane.functions/cross.md)(right: [MatrixExpr](index.md)): [MatrixExpr](index.md)  <br><br><br>
| [describe](../../com.github.jomof.kane.sheet/describe.md)| [jvm]  <br>Brief description  <br><br><br>Return a new sheet with data summarized into statistics<br><br>  <br>Content  <br>fun [MatrixExpr](index.md).[describe](../../com.github.jomof.kane.sheet/describe.md)(name: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)): [Sheet](../../com.github.jomof.kane.sheet/-sheet/index.md)  <br><br><br>
| [div](../../com.github.jomof.kane.functions/div.md)| [jvm]  <br>Content  <br>operator fun <[E](../../com.github.jomof.kane.functions/div.md) : [Number](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-number/index.html)> [MatrixExpr](index.md).[div](../../com.github.jomof.kane.functions/div.md)(right: [E](../../com.github.jomof.kane.functions/div.md)): [MatrixExpr](index.md)  <br>operator fun [MatrixExpr](index.md).[div](../../com.github.jomof.kane.functions/div.md)(right: [ScalarExpr](../-scalar-expr/index.md)): [MatrixExpr](index.md)  <br>operator fun [MatrixExpr](index.md).[div](../../com.github.jomof.kane.functions/div.md)(right: [MatrixExpr](index.md)): [MatrixExpr](index.md)  <br>operator fun [MatrixExpr](index.md).[div](../../com.github.jomof.kane.functions/div.md)(right: [SheetRange](../../com.github.jomof.kane.sheet/-sheet-range/index.md)): [MatrixExpr](index.md)  <br><br><br>
| [eval](../eval.md)| [jvm]  <br>Content  <br>fun [MatrixExpr](index.md).[eval](../eval.md)(rangeExprProvider: [RangeExprProvider](../../com.github.jomof.kane.sheet/-range-expr-provider/index.md), reduceVariables: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), excludeVariables: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)>): [MatrixExpr](index.md)  <br><br><br>
| [getValue](../get-value.md)| [jvm]  <br>Content  <br>operator fun [MatrixExpr](index.md).[getValue](../get-value.md)(thisRef: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?, property: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)<*>): [NamedMatrix](../-named-matrix/index.md)  <br><br><br>
| [map](../map.md)| [jvm]  <br>Content  <br>fun [MatrixExpr](index.md).[map](../map.md)(action: ([ScalarExpr](../-scalar-expr/index.md)) -> [ScalarExpr](../-scalar-expr/index.md)): [DataMatrix](../-data-matrix/index.md)  <br><br><br>
| [minus](../../com.github.jomof.kane.functions/minus.md)| [jvm]  <br>Content  <br>operator fun <[E](../../com.github.jomof.kane.functions/minus.md) : [Number](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-number/index.html)> [MatrixExpr](index.md).[minus](../../com.github.jomof.kane.functions/minus.md)(right: [E](../../com.github.jomof.kane.functions/minus.md)): [MatrixExpr](index.md)  <br>operator fun [MatrixExpr](index.md).[minus](../../com.github.jomof.kane.functions/minus.md)(right: [ScalarExpr](../-scalar-expr/index.md)): [MatrixExpr](index.md)  <br>operator fun [MatrixExpr](index.md).[minus](../../com.github.jomof.kane.functions/minus.md)(right: [MatrixExpr](index.md)): [MatrixExpr](index.md)  <br>operator fun [MatrixExpr](index.md).[minus](../../com.github.jomof.kane.functions/minus.md)(right: [SheetRange](../../com.github.jomof.kane.sheet/-sheet-range/index.md)): [MatrixExpr](index.md)  <br><br><br>
| [plus](../../com.github.jomof.kane.functions/plus.md)| [jvm]  <br>Content  <br>operator fun <[E](../../com.github.jomof.kane.functions/plus.md) : [Number](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-number/index.html)> [MatrixExpr](index.md).[plus](../../com.github.jomof.kane.functions/plus.md)(right: [E](../../com.github.jomof.kane.functions/plus.md)): [MatrixExpr](index.md)  <br>operator fun [MatrixExpr](index.md).[plus](../../com.github.jomof.kane.functions/plus.md)(right: [ScalarExpr](../-scalar-expr/index.md)): [MatrixExpr](index.md)  <br>operator fun [MatrixExpr](index.md).[plus](../../com.github.jomof.kane.functions/plus.md)(right: [MatrixExpr](index.md)): [MatrixExpr](index.md)  <br>operator fun [MatrixExpr](index.md).[plus](../../com.github.jomof.kane.functions/plus.md)(right: [SheetRange](../../com.github.jomof.kane.sheet/-sheet-range/index.md)): [MatrixExpr](index.md)  <br><br><br>
| [stack](../../com.github.jomof.kane.functions/stack.md)| [jvm]  <br>Content  <br>infix fun [MatrixExpr](index.md).[stack](../../com.github.jomof.kane.functions/stack.md)(right: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)): [MatrixExpr](index.md)  <br>infix fun [MatrixExpr](index.md).[stack](../../com.github.jomof.kane.functions/stack.md)(right: [ScalarExpr](../-scalar-expr/index.md)): [MatrixExpr](index.md)  <br>infix fun [MatrixExpr](index.md).[stack](../../com.github.jomof.kane.functions/stack.md)(right: [MatrixExpr](index.md)): [MatrixExpr](index.md)  <br><br><br>
| [times](../../com.github.jomof.kane.functions/times.md)| [jvm]  <br>Content  <br>operator fun <[E](../../com.github.jomof.kane.functions/times.md) : [Number](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-number/index.html)> [MatrixExpr](index.md).[times](../../com.github.jomof.kane.functions/times.md)(right: [E](../../com.github.jomof.kane.functions/times.md)): [MatrixExpr](index.md)  <br>operator fun [MatrixExpr](index.md).[times](../../com.github.jomof.kane.functions/times.md)(right: [ScalarExpr](../-scalar-expr/index.md)): [MatrixExpr](index.md)  <br>operator fun [MatrixExpr](index.md).[times](../../com.github.jomof.kane.functions/times.md)(right: [MatrixExpr](index.md)): [MatrixExpr](index.md)  <br>operator fun [MatrixExpr](index.md).[times](../../com.github.jomof.kane.functions/times.md)(right: [SheetRange](../../com.github.jomof.kane.sheet/-sheet-range/index.md)): [MatrixExpr](index.md)  <br><br><br>
| [toDataMatrix](../to-data-matrix.md)| [jvm]  <br>Content  <br>fun [MatrixExpr](index.md).[toDataMatrix](../to-data-matrix.md)(): [MatrixExpr](index.md)  <br><br><br>
| [toNamed](../to-named.md)| [jvm]  <br>Content  <br>fun [MatrixExpr](index.md).[toNamed](../to-named.md)(name: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)): [NamedMatrix](../-named-matrix/index.md)  <br><br><br>
| [unaryMinus](../../com.github.jomof.kane.functions/unary-minus.md)| [jvm]  <br>Content  <br>operator fun [MatrixExpr](index.md).[unaryMinus](../../com.github.jomof.kane.functions/unary-minus.md)(): [MatrixExpr](index.md)  <br><br><br>

