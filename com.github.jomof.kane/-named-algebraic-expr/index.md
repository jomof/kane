//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[NamedAlgebraicExpr](index.md)



# NamedAlgebraicExpr  
 [jvm] interface [NamedAlgebraicExpr](index.md) : [NamedExpr](../-named-expr/index.md), [AlgebraicExpr](../-algebraic-expr/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [name](index.md#com.github.jomof.kane/NamedAlgebraicExpr/name/#/PointingToDeclaration/)|  [jvm] abstract override val [name](index.md#com.github.jomof.kane/NamedAlgebraicExpr/name/#/PointingToDeclaration/): [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)   <br>


## Inheritors  
  
|  Name| 
|---|
| [NamedScalarExpr](../-named-scalar-expr/index.md)
| [NamedMatrixExpr](../-named-matrix-expr/index.md)
| [NamedScalarAssign](../-named-scalar-assign/index.md)
| [NamedMatrixAssign](../-named-matrix-assign/index.md)


## Extensions  
  
|  Name|  Summary| 
|---|---|
| [eval](../eval.md)| [jvm]  <br>Content  <br>fun [NamedAlgebraicExpr](index.md).[eval](../eval.md)(rangeExprProvider: [RangeExprProvider](../../com.github.jomof.kane.sheet/-range-expr-provider/index.md), reduceVariables: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), excludeVariables: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)>): [NamedAlgebraicExpr](index.md)  <br><br><br>

