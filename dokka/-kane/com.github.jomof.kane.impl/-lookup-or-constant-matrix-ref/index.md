//[Kane](../../index.md)/[com.github.jomof.kane.impl](../index.md)/[LookupOrConstantMatrixRef](index.md)



# LookupOrConstantMatrixRef  
 [jvm] class [LookupOrConstantMatrixRef](index.md)(**columns**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), **rows**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), **type**: [AlgebraicType](../../com.github.jomof.kane.impl.types/-algebraic-type/index.md), **elements**: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)>, **array**: [DoubleArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double-array/index.html)) : [MutableMatrix](../-mutable-matrix/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1958197075)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1958197075)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.impl/Matrix/get/#com.github.jomof.kane.impl.Coordinate/PointingToDeclaration/"></a>[get](../-matrix/get.md)| <a name="com.github.jomof.kane.impl/Matrix/get/#com.github.jomof.kane.impl.Coordinate/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [get](../-matrix/get.md)(coordinate: [Coordinate](../-coordinate/index.md)): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br>open operator override fun [get](get.md)(column: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), row: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1958197075)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1958197075)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.impl/MutableMatrix/set/#com.github.jomof.kane.impl.Coordinate#kotlin.Double/PointingToDeclaration/"></a>[set](../-mutable-matrix/set.md)| <a name="com.github.jomof.kane.impl/MutableMatrix/set/#com.github.jomof.kane.impl.Coordinate#kotlin.Double/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [set](../-mutable-matrix/set.md)(coordinate: [Coordinate](../-coordinate/index.md), value: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html))  <br>open operator override fun [set](set.md)(column: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), row: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), value: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html))  <br><br><br>
| <a name="com.github.jomof.kane.impl/LookupOrConstantMatrixRef/toString/#/PointingToDeclaration/"></a>[toString](to-string.md)| <a name="com.github.jomof.kane.impl/LookupOrConstantMatrixRef/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl/LookupOrConstantMatrixRef/columns/#/PointingToDeclaration/"></a>[columns](columns.md)| <a name="com.github.jomof.kane.impl/LookupOrConstantMatrixRef/columns/#/PointingToDeclaration/"></a> [jvm] open override val [columns](columns.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| <a name="com.github.jomof.kane.impl/LookupOrConstantMatrixRef/elements/#/PointingToDeclaration/"></a>[elements](elements.md)| <a name="com.github.jomof.kane.impl/LookupOrConstantMatrixRef/elements/#/PointingToDeclaration/"></a> [jvm] val [elements](elements.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)>   <br>
| <a name="com.github.jomof.kane.impl/LookupOrConstantMatrixRef/rows/#/PointingToDeclaration/"></a>[rows](rows.md)| <a name="com.github.jomof.kane.impl/LookupOrConstantMatrixRef/rows/#/PointingToDeclaration/"></a> [jvm] open override val [rows](rows.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| <a name="com.github.jomof.kane.impl/LookupOrConstantMatrixRef/type/#/PointingToDeclaration/"></a>[type](type.md)| <a name="com.github.jomof.kane.impl/LookupOrConstantMatrixRef/type/#/PointingToDeclaration/"></a> [jvm] open override val [type](type.md): [AlgebraicType](../../com.github.jomof.kane.impl.types/-algebraic-type/index.md)   <br>
