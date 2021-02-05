//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[LookupOrConstantsMatrixShape](index.md)



# LookupOrConstantsMatrixShape  
 [jvm] data class [LookupOrConstantsMatrixShape](index.md)(**columns**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), **rows**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), **elements**: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[ScalarExpr](../-scalar-expr/index.md)>, **type**: [AlgebraicType](../../com.github.jomof.kane.types/-algebraic-type/index.md)) : [MatrixShape](../-matrix-shape/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [component1](component1.md)| [jvm]  <br>Content  <br>operator fun [component1](component1.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [component2](component2.md)| [jvm]  <br>Content  <br>operator fun [component2](component2.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [component3](component3.md)| [jvm]  <br>Content  <br>operator fun [component3](component3.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[ScalarExpr](../-scalar-expr/index.md)>  <br><br><br>
| [component4](component4.md)| [jvm]  <br>Content  <br>operator fun [component4](component4.md)(): [AlgebraicType](../../com.github.jomof.kane.types/-algebraic-type/index.md)  <br><br><br>
| [copy](copy.md)| [jvm]  <br>Content  <br>fun [copy](copy.md)(columns: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), rows: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), elements: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[ScalarExpr](../-scalar-expr/index.md)>, type: [AlgebraicType](../../com.github.jomof.kane.types/-algebraic-type/index.md)): [LookupOrConstantsMatrixShape](index.md)  <br><br><br>
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [owns](owns.md)| [jvm]  <br>Content  <br>open override fun [owns](owns.md)(index: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [ref](ref.md)| [jvm]  <br>Content  <br>open override fun [ref](ref.md)(array: [DoubleArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double-array/index.html)): [LookupOrConstantMatrixRef](../-lookup-or-constant-matrix-ref/index.md)  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [columns](index.md#com.github.jomof.kane/LookupOrConstantsMatrixShape/columns/#/PointingToDeclaration/)|  [jvm] open override val [columns](index.md#com.github.jomof.kane/LookupOrConstantsMatrixShape/columns/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [elements](index.md#com.github.jomof.kane/LookupOrConstantsMatrixShape/elements/#/PointingToDeclaration/)|  [jvm] val [elements](index.md#com.github.jomof.kane/LookupOrConstantsMatrixShape/elements/#/PointingToDeclaration/): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[ScalarExpr](../-scalar-expr/index.md)>   <br>
| [rows](index.md#com.github.jomof.kane/LookupOrConstantsMatrixShape/rows/#/PointingToDeclaration/)|  [jvm] open override val [rows](index.md#com.github.jomof.kane/LookupOrConstantsMatrixShape/rows/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [type](index.md#com.github.jomof.kane/LookupOrConstantsMatrixShape/type/#/PointingToDeclaration/)|  [jvm] open override val [type](index.md#com.github.jomof.kane/LookupOrConstantsMatrixShape/type/#/PointingToDeclaration/): [AlgebraicType](../../com.github.jomof.kane.types/-algebraic-type/index.md)   <br>

