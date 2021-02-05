//[Kane](../../index.md)/[com.github.jomof.kane.functions](../index.md)/[AlgebraicBinaryScalarStatisticFunction](index.md)



# AlgebraicBinaryScalarStatisticFunction  
 [jvm] interface [AlgebraicBinaryScalarStatisticFunction](index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [invoke](invoke.md)| [jvm]  <br>Content  <br>open operator fun [invoke](invoke.md)(left: [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md), right: [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)): [AlgebraicBinaryScalarStatistic](../-algebraic-binary-scalar-statistic/index.md)  <br>open operator fun [invoke](invoke.md)(left: [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md), right: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)): [AlgebraicBinaryScalarStatistic](../-algebraic-binary-scalar-statistic/index.md)  <br>open operator fun [invoke](invoke.md)(value: [NamedSheetRangeExpr](../../com.github.jomof.kane.sheet/-named-sheet-range-expr/index.md), right: [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)): [AlgebraicBinaryRangeStatistic](../-algebraic-binary-range-statistic/index.md)  <br>open operator fun [invoke](invoke.md)(value: [NamedSheetRangeExpr](../../com.github.jomof.kane.sheet/-named-sheet-range-expr/index.md), right: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)): [AlgebraicBinaryRangeStatistic](../-algebraic-binary-range-statistic/index.md)  <br>open operator fun [invoke](invoke.md)(value: [SheetRangeExpr](../../com.github.jomof.kane.sheet/-sheet-range-expr/index.md), right: [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)): [AlgebraicBinaryRangeStatistic](../-algebraic-binary-range-statistic/index.md)  <br>open operator fun [invoke](invoke.md)(value: [SheetRangeExpr](../../com.github.jomof.kane.sheet/-sheet-range-expr/index.md), right: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)): [AlgebraicBinaryRangeStatistic](../-algebraic-binary-range-statistic/index.md)  <br><br><br>
| [reduceArithmetic](reduce-arithmetic.md)| [jvm]  <br>Content  <br>abstract fun [reduceArithmetic](reduce-arithmetic.md)(left: [ScalarStatistic](../../com.github.jomof.kane/-scalar-statistic/index.md), right: [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br>abstract fun [reduceArithmetic](reduce-arithmetic.md)(left: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)>, right: [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)?  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [meta](index.md#com.github.jomof.kane.functions/AlgebraicBinaryScalarStatisticFunction/meta/#/PointingToDeclaration/)|  [jvm] abstract val [meta](index.md#com.github.jomof.kane.functions/AlgebraicBinaryScalarStatisticFunction/meta/#/PointingToDeclaration/): [BinaryOp](../../com.github.jomof.kane/-binary-op/index.md)   <br>


## Inheritors  
  
|  Name| 
|---|
| [PercentileFunction](../-percentile-function/index.md)

