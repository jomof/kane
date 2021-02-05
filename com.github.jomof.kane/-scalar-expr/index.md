//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[ScalarExpr](index.md)



# ScalarExpr  
 [jvm] interface [ScalarExpr](index.md) : [AlgebraicExpr](../-algebraic-expr/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Inheritors  
  
|  Name| 
|---|
| [ScalarVariableExpr](../-scalar-variable-expr/index.md)
| [NamedScalarExpr](../-named-scalar-expr/index.md)
| [ConstantScalar](../-constant-scalar/index.md)
| [MatrixVariableElement](../-matrix-variable-element/index.md)
| [RetypeScalar](../-retype-scalar/index.md)
| [Slot](../-slot/index.md)
| [ScalarListExpr](../-scalar-list-expr/index.md)
| [RandomVariableExpr](../-random-variable-expr/index.md)
| [DiscreteUniformRandomVariable](../-discrete-uniform-random-variable/index.md)
| [ScalarStatistic](../-scalar-statistic/index.md)
| [AlgebraicBinaryScalar](../../com.github.jomof.kane.functions/-algebraic-binary-scalar/index.md)
| [AlgebraicUnaryScalar](../../com.github.jomof.kane.functions/-algebraic-unary-scalar/index.md)
| [AlgebraicUnaryScalarStatistic](../../com.github.jomof.kane.functions/-algebraic-unary-scalar-statistic/index.md)
| [AlgebraicBinaryScalarStatistic](../../com.github.jomof.kane.functions/-algebraic-binary-scalar-statistic/index.md)
| [AlgebraicUnaryMatrixScalar](../../com.github.jomof.kane.functions/-algebraic-unary-matrix-scalar/index.md)
| [CoerceScalar](../../com.github.jomof.kane.sheet/-coerce-scalar/index.md)


## Extensions  
  
|  Name|  Summary| 
|---|---|
| [div](../../com.github.jomof.kane.functions/div.md)| [jvm]  <br>Content  <br>operator fun <[E](../../com.github.jomof.kane.functions/div.md) : [Number](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-number/index.html)> [ScalarExpr](index.md).[div](../../com.github.jomof.kane.functions/div.md)(right: [E](../../com.github.jomof.kane.functions/div.md)): [ScalarExpr](index.md)  <br>operator fun [ScalarExpr](index.md).[div](../../com.github.jomof.kane.functions/div.md)(right: [ScalarExpr](index.md)): [ScalarExpr](index.md)  <br>operator fun [ScalarExpr](index.md).[div](../../com.github.jomof.kane.functions/div.md)(right: [MatrixExpr](../-matrix-expr/index.md)): [MatrixExpr](../-matrix-expr/index.md)  <br>operator fun [ScalarExpr](index.md).[div](../../com.github.jomof.kane.functions/div.md)(right: [UntypedScalar](../-untyped-scalar/index.md)): [ScalarExpr](index.md)  <br><br><br>
| [eval](../eval.md)| [jvm]  <br>Content  <br>fun [ScalarExpr](index.md).[eval](../eval.md)(rangeExprProvider: [RangeExprProvider](../../com.github.jomof.kane.sheet/-range-expr-provider/index.md), reduceVariables: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), excludeVariables: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)>): [ScalarExpr](index.md)  <br><br><br>
| [getValue](../get-value.md)| [jvm]  <br>Content  <br>operator fun [ScalarExpr](index.md).[getValue](../get-value.md)(thisRef: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?, property: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)<*>): [NamedScalar](../-named-scalar/index.md)  <br><br><br>
| [minus](../../com.github.jomof.kane.functions/minus.md)| [jvm]  <br>Content  <br>operator fun <[E](../../com.github.jomof.kane.functions/minus.md) : [Number](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-number/index.html)> [ScalarExpr](index.md).[minus](../../com.github.jomof.kane.functions/minus.md)(right: [E](../../com.github.jomof.kane.functions/minus.md)): [ScalarExpr](index.md)  <br>operator fun [ScalarExpr](index.md).[minus](../../com.github.jomof.kane.functions/minus.md)(right: [ScalarExpr](index.md)): [ScalarExpr](index.md)  <br>operator fun [ScalarExpr](index.md).[minus](../../com.github.jomof.kane.functions/minus.md)(right: [MatrixExpr](../-matrix-expr/index.md)): [MatrixExpr](../-matrix-expr/index.md)  <br>operator fun [ScalarExpr](index.md).[minus](../../com.github.jomof.kane.functions/minus.md)(right: [UntypedScalar](../-untyped-scalar/index.md)): [ScalarExpr](index.md)  <br><br><br>
| [plus](../../com.github.jomof.kane.functions/plus.md)| [jvm]  <br>Content  <br>operator fun <[E](../../com.github.jomof.kane.functions/plus.md) : [Number](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-number/index.html)> [ScalarExpr](index.md).[plus](../../com.github.jomof.kane.functions/plus.md)(right: [E](../../com.github.jomof.kane.functions/plus.md)): [ScalarExpr](index.md)  <br>operator fun [ScalarExpr](index.md).[plus](../../com.github.jomof.kane.functions/plus.md)(right: [ScalarExpr](index.md)): [ScalarExpr](index.md)  <br>operator fun [ScalarExpr](index.md).[plus](../../com.github.jomof.kane.functions/plus.md)(right: [MatrixExpr](../-matrix-expr/index.md)): [MatrixExpr](../-matrix-expr/index.md)  <br>operator fun [ScalarExpr](index.md).[plus](../../com.github.jomof.kane.functions/plus.md)(right: [UntypedScalar](../-untyped-scalar/index.md)): [ScalarExpr](index.md)  <br><br><br>
| [stack](../../com.github.jomof.kane.functions/stack.md)| [jvm]  <br>Content  <br>infix fun [ScalarExpr](index.md).[stack](../../com.github.jomof.kane.functions/stack.md)(right: [MatrixExpr](../-matrix-expr/index.md)): [MatrixExpr](../-matrix-expr/index.md)  <br><br><br>
| [times](../../com.github.jomof.kane.functions/times.md)| [jvm]  <br>Content  <br>operator fun <[E](../../com.github.jomof.kane.functions/times.md) : [Number](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-number/index.html)> [ScalarExpr](index.md).[times](../../com.github.jomof.kane.functions/times.md)(right: [E](../../com.github.jomof.kane.functions/times.md)): [ScalarExpr](index.md)  <br>operator fun [ScalarExpr](index.md).[times](../../com.github.jomof.kane.functions/times.md)(right: [ScalarExpr](index.md)): [ScalarExpr](index.md)  <br>operator fun [ScalarExpr](index.md).[times](../../com.github.jomof.kane.functions/times.md)(right: [MatrixExpr](../-matrix-expr/index.md)): [MatrixExpr](../-matrix-expr/index.md)  <br>operator fun [ScalarExpr](index.md).[times](../../com.github.jomof.kane.functions/times.md)(right: [UntypedScalar](../-untyped-scalar/index.md)): [ScalarExpr](index.md)  <br><br><br>
| [toNamed](../to-named.md)| [jvm]  <br>Content  <br>fun [ScalarExpr](index.md).[toNamed](../to-named.md)(name: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)): [NamedScalar](../-named-scalar/index.md)  <br><br><br>
| [unaryMinus](../../com.github.jomof.kane.functions/unary-minus.md)| [jvm]  <br>Content  <br>operator fun [ScalarExpr](index.md).[unaryMinus](../../com.github.jomof.kane.functions/unary-minus.md)(): [ScalarExpr](index.md)  <br><br><br>

