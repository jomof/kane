//[Kane](../../index.md)/[com.github.jomof.kane.sheet](../index.md)/[SheetRange](index.md)



# SheetRange  
 [jvm] interface [SheetRange](index.md) : [Expr](../../com.github.jomof.kane/-expr/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [down](down.md)| [jvm]  <br>Content  <br>abstract fun [down](down.md)(move: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [SheetRange](index.md)  <br><br><br>
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [left](left.md)| [jvm]  <br>Content  <br>abstract fun [left](left.md)(move: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [SheetRange](index.md)  <br><br><br>
| [right](right.md)| [jvm]  <br>Content  <br>abstract fun [right](right.md)(move: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [SheetRange](index.md)  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [up](up.md)| [jvm]  <br>Content  <br>abstract fun [up](up.md)(move: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [SheetRange](index.md)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [down](index.md#com.github.jomof.kane.sheet/SheetRange/down/#/PointingToDeclaration/)|  [jvm] open val [down](index.md#com.github.jomof.kane.sheet/SheetRange/down/#/PointingToDeclaration/): [SheetRange](index.md)   <br>
| [left](index.md#com.github.jomof.kane.sheet/SheetRange/left/#/PointingToDeclaration/)|  [jvm] open val [left](index.md#com.github.jomof.kane.sheet/SheetRange/left/#/PointingToDeclaration/): [SheetRange](index.md)   <br>
| [right](index.md#com.github.jomof.kane.sheet/SheetRange/right/#/PointingToDeclaration/)|  [jvm] open val [right](index.md#com.github.jomof.kane.sheet/SheetRange/right/#/PointingToDeclaration/): [SheetRange](index.md)   <br>
| [up](index.md#com.github.jomof.kane.sheet/SheetRange/up/#/PointingToDeclaration/)|  [jvm] open val [up](index.md#com.github.jomof.kane.sheet/SheetRange/up/#/PointingToDeclaration/): [SheetRange](index.md)   <br>


## Inheritors  
  
|  Name| 
|---|
| [SheetBuilderRange](../-sheet-builder-range/index.md)
| [SheetRangeExpr](../-sheet-range-expr/index.md)
| [NamedSheetRangeExpr](../-named-sheet-range-expr/index.md)


## Extensions  
  
|  Name|  Summary| 
|---|---|
| [div](../../com.github.jomof.kane.functions/div.md)| [jvm]  <br>Content  <br>operator fun [SheetRange](index.md).[div](../../com.github.jomof.kane.functions/div.md)(right: [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br>operator fun <[E](../../com.github.jomof.kane.functions/div.md) : [Number](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-number/index.html)> [SheetRange](index.md).[div](../../com.github.jomof.kane.functions/div.md)(right: [E](../../com.github.jomof.kane.functions/div.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br>operator fun [SheetRange](index.md).[div](../../com.github.jomof.kane.functions/div.md)(right: [SheetRange](index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br><br><br>
| [minus](../../com.github.jomof.kane.functions/minus.md)| [jvm]  <br>Content  <br>operator fun [SheetRange](index.md).[minus](../../com.github.jomof.kane.functions/minus.md)(right: [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br>operator fun <[E](../../com.github.jomof.kane.functions/minus.md) : [Number](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-number/index.html)> [SheetRange](index.md).[minus](../../com.github.jomof.kane.functions/minus.md)(right: [E](../../com.github.jomof.kane.functions/minus.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br>operator fun [SheetRange](index.md).[minus](../../com.github.jomof.kane.functions/minus.md)(right: [SheetRange](index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br><br><br>
| [plus](../../com.github.jomof.kane.functions/plus.md)| [jvm]  <br>Content  <br>operator fun [SheetRange](index.md).[plus](../../com.github.jomof.kane.functions/plus.md)(right: [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br>operator fun <[E](../../com.github.jomof.kane.functions/plus.md) : [Number](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-number/index.html)> [SheetRange](index.md).[plus](../../com.github.jomof.kane.functions/plus.md)(right: [E](../../com.github.jomof.kane.functions/plus.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br>operator fun [SheetRange](index.md).[plus](../../com.github.jomof.kane.functions/plus.md)(right: [SheetRange](index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br><br><br>
| [times](../../com.github.jomof.kane.functions/times.md)| [jvm]  <br>Content  <br>operator fun [SheetRange](index.md).[times](../../com.github.jomof.kane.functions/times.md)(right: [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br>operator fun <[E](../../com.github.jomof.kane.functions/times.md) : [Number](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-number/index.html)> [SheetRange](index.md).[times](../../com.github.jomof.kane.functions/times.md)(right: [E](../../com.github.jomof.kane.functions/times.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br>operator fun [SheetRange](index.md).[times](../../com.github.jomof.kane.functions/times.md)(right: [SheetRange](index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br><br><br>
| [toNamed](../../com.github.jomof.kane/to-named.md)| [jvm]  <br>Content  <br>fun [SheetRange](index.md).[toNamed](../../com.github.jomof.kane/to-named.md)(name: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)): [NamedSheetRangeExpr](../-named-sheet-range-expr/index.md)  <br><br><br>

