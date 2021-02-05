//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[UntypedScalar](index.md)



# UntypedScalar  
 [jvm] interface [UntypedScalar](index.md) : [Expr](../-expr/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Inheritors  
  
|  Name| 
|---|
| [NamedUntypedScalar](../-named-untyped-scalar/index.md)
| [AlgebraicBinaryRangeStatistic](../../com.github.jomof.kane.functions/-algebraic-binary-range-statistic/index.md)
| [SheetBuilderRange](../../com.github.jomof.kane.sheet/-sheet-builder-range/index.md)
| [SheetRangeExpr](../../com.github.jomof.kane.sheet/-sheet-range-expr/index.md)
| [NamedSheetRangeExpr](../../com.github.jomof.kane.sheet/-named-sheet-range-expr/index.md)


## Extensions  
  
|  Name|  Summary| 
|---|---|
| [div](../../com.github.jomof.kane.functions/div.md)| [jvm]  <br>Content  <br>operator fun [UntypedScalar](index.md).[div](../../com.github.jomof.kane.functions/div.md)(right: [ScalarExpr](../-scalar-expr/index.md)): [ScalarExpr](../-scalar-expr/index.md)  <br>operator fun <[E](../../com.github.jomof.kane.functions/div.md) : [Number](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-number/index.html)> [UntypedScalar](index.md).[div](../../com.github.jomof.kane.functions/div.md)(right: [E](../../com.github.jomof.kane.functions/div.md)): [ScalarExpr](../-scalar-expr/index.md)  <br>operator fun [UntypedScalar](index.md).[div](../../com.github.jomof.kane.functions/div.md)(right: [UntypedScalar](index.md)): [ScalarExpr](../-scalar-expr/index.md)  <br><br><br>
| [minus](../../com.github.jomof.kane.functions/minus.md)| [jvm]  <br>Content  <br>operator fun [UntypedScalar](index.md).[minus](../../com.github.jomof.kane.functions/minus.md)(right: [ScalarExpr](../-scalar-expr/index.md)): [ScalarExpr](../-scalar-expr/index.md)  <br>operator fun <[E](../../com.github.jomof.kane.functions/minus.md) : [Number](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-number/index.html)> [UntypedScalar](index.md).[minus](../../com.github.jomof.kane.functions/minus.md)(right: [E](../../com.github.jomof.kane.functions/minus.md)): [ScalarExpr](../-scalar-expr/index.md)  <br>operator fun [UntypedScalar](index.md).[minus](../../com.github.jomof.kane.functions/minus.md)(right: [UntypedScalar](index.md)): [ScalarExpr](../-scalar-expr/index.md)  <br><br><br>
| [plus](../../com.github.jomof.kane.functions/plus.md)| [jvm]  <br>Content  <br>operator fun [UntypedScalar](index.md).[plus](../../com.github.jomof.kane.functions/plus.md)(right: [ScalarExpr](../-scalar-expr/index.md)): [ScalarExpr](../-scalar-expr/index.md)  <br>operator fun <[E](../../com.github.jomof.kane.functions/plus.md) : [Number](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-number/index.html)> [UntypedScalar](index.md).[plus](../../com.github.jomof.kane.functions/plus.md)(right: [E](../../com.github.jomof.kane.functions/plus.md)): [ScalarExpr](../-scalar-expr/index.md)  <br>operator fun [UntypedScalar](index.md).[plus](../../com.github.jomof.kane.functions/plus.md)(right: [UntypedScalar](index.md)): [ScalarExpr](../-scalar-expr/index.md)  <br><br><br>
| [times](../../com.github.jomof.kane.functions/times.md)| [jvm]  <br>Content  <br>operator fun [UntypedScalar](index.md).[times](../../com.github.jomof.kane.functions/times.md)(right: [ScalarExpr](../-scalar-expr/index.md)): [ScalarExpr](../-scalar-expr/index.md)  <br>operator fun <[E](../../com.github.jomof.kane.functions/times.md) : [Number](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-number/index.html)> [UntypedScalar](index.md).[times](../../com.github.jomof.kane.functions/times.md)(right: [E](../../com.github.jomof.kane.functions/times.md)): [ScalarExpr](../-scalar-expr/index.md)  <br>operator fun [UntypedScalar](index.md).[times](../../com.github.jomof.kane.functions/times.md)(right: [UntypedScalar](index.md)): [ScalarExpr](../-scalar-expr/index.md)  <br><br><br>
| [toNamed](../to-named.md)| [jvm]  <br>Content  <br>fun [UntypedScalar](index.md).[toNamed](../to-named.md)(name: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)): [NamedUntypedScalar](../-named-untyped-scalar/index.md)  <br><br><br>

