//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[AlgebraicExpr](index.md)



# AlgebraicExpr  
 [jvm] interface [AlgebraicExpr](index.md) : [Expr](../-expr/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Inheritors  
  
|  Name| 
|---|
| [ScalarExpr](../-scalar-expr/index.md)
| [MatrixExpr](../-matrix-expr/index.md)
| [VariableExpr](../-variable-expr/index.md)
| [NamedAlgebraicExpr](../-named-algebraic-expr/index.md)
| [ScalarVariable](../-scalar-variable/index.md)
| [MatrixVariable](../-matrix-variable/index.md)
| [Tableau](../-tableau/index.md)


## Extensions  
  
|  Name|  Summary| 
|---|---|
| [algebraicType](../../com.github.jomof.kane.types/index.md#com.github.jomof.kane.types//algebraicType/com.github.jomof.kane.AlgebraicExpr#/PointingToDeclaration/)| [jvm]  <br>Content  <br>val [AlgebraicExpr](index.md).[algebraicType](../../com.github.jomof.kane.types/index.md#com.github.jomof.kane.types//algebraicType/com.github.jomof.kane.AlgebraicExpr#/PointingToDeclaration/): [AlgebraicType](../../com.github.jomof.kane.types/-algebraic-type/index.md)  <br><br><br>
| [rollUpCommonSubexpressions](../roll-up-common-subexpressions.md)| [jvm]  <br>Content  <br>fun [AlgebraicExpr](index.md).[rollUpCommonSubexpressions](../roll-up-common-subexpressions.md)(model: [LinearModel](../-linear-model/index.md)): [AlgebraicExpr](index.md)  <br><br><br>
| [toNamed](../to-named.md)| [jvm]  <br>Content  <br>fun [AlgebraicExpr](index.md).[toNamed](../to-named.md)(name: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)): [NamedAlgebraicExpr](../-named-algebraic-expr/index.md)  <br><br><br>

