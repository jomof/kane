//[Kane](../../index.md)/[com.github.jomof.kane.impl](../index.md)/[MutableMatrix](index.md)



# MutableMatrix  
 [jvm] interface [MutableMatrix](index.md) : [Matrix](../-matrix/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1588672227)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1588672227)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.impl/Matrix/get/#com.github.jomof.kane.impl.Coordinate/PointingToDeclaration/"></a>[get](../-matrix/get.md)| <a name="com.github.jomof.kane.impl/Matrix/get/#com.github.jomof.kane.impl.Coordinate/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [get](../-matrix/get.md)(coordinate: [Coordinate](../-coordinate/index.md)): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br>abstract operator fun [get](../-matrix/get.md)(column: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), row: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1588672227)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1588672227)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.impl/MutableMatrix/set/#com.github.jomof.kane.impl.Coordinate#kotlin.Double/PointingToDeclaration/"></a>[set](set.md)| <a name="com.github.jomof.kane.impl/MutableMatrix/set/#com.github.jomof.kane.impl.Coordinate#kotlin.Double/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [set](set.md)(coordinate: [Coordinate](../-coordinate/index.md), value: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html))  <br>abstract operator fun [set](set.md)(column: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), row: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), value: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html))  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../com.github.jomof.kane.impl.types/-object-kane-type/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1588672227)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../com.github.jomof.kane.impl.types/-object-kane-type/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1588672227)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl/MutableMatrix/columns/#/PointingToDeclaration/"></a>[columns](index.md#%5Bcom.github.jomof.kane.impl%2FMutableMatrix%2Fcolumns%2F%23%2FPointingToDeclaration%2F%5D%2FProperties%2F-1588672227)| <a name="com.github.jomof.kane.impl/MutableMatrix/columns/#/PointingToDeclaration/"></a> [jvm] abstract val [columns](index.md#%5Bcom.github.jomof.kane.impl%2FMutableMatrix%2Fcolumns%2F%23%2FPointingToDeclaration%2F%5D%2FProperties%2F-1588672227): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| <a name="com.github.jomof.kane.impl/MutableMatrix/rows/#/PointingToDeclaration/"></a>[rows](index.md#%5Bcom.github.jomof.kane.impl%2FMutableMatrix%2Frows%2F%23%2FPointingToDeclaration%2F%5D%2FProperties%2F-1588672227)| <a name="com.github.jomof.kane.impl/MutableMatrix/rows/#/PointingToDeclaration/"></a> [jvm] abstract val [rows](index.md#%5Bcom.github.jomof.kane.impl%2FMutableMatrix%2Frows%2F%23%2FPointingToDeclaration%2F%5D%2FProperties%2F-1588672227): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| <a name="com.github.jomof.kane.impl/MutableMatrix/type/#/PointingToDeclaration/"></a>[type](index.md#%5Bcom.github.jomof.kane.impl%2FMutableMatrix%2Ftype%2F%23%2FPointingToDeclaration%2F%5D%2FProperties%2F-1588672227)| <a name="com.github.jomof.kane.impl/MutableMatrix/type/#/PointingToDeclaration/"></a> [jvm] abstract val [type](index.md#%5Bcom.github.jomof.kane.impl%2FMutableMatrix%2Ftype%2F%23%2FPointingToDeclaration%2F%5D%2FProperties%2F-1588672227): [AlgebraicType](../../com.github.jomof.kane.impl.types/-algebraic-type/index.md)   <br>


## Inheritors  
  
|  Name| 
|---|
| <a name="com.github.jomof.kane.impl/LinearMatrixRef///PointingToDeclaration/"></a>[LinearMatrixRef](../-linear-matrix-ref/index.md)
| <a name="com.github.jomof.kane.impl/LookupMatrixRef///PointingToDeclaration/"></a>[LookupMatrixRef](../-lookup-matrix-ref/index.md)
| <a name="com.github.jomof.kane.impl/LookupOrConstantMatrixRef///PointingToDeclaration/"></a>[LookupOrConstantMatrixRef](../-lookup-or-constant-matrix-ref/index.md)


## Extensions  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl//divAssign/com.github.jomof.kane.impl.MutableMatrix#kotlin.Double/PointingToDeclaration/"></a>[divAssign](../div-assign.md)| <a name="com.github.jomof.kane.impl//divAssign/com.github.jomof.kane.impl.MutableMatrix#kotlin.Double/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>operator fun [MutableMatrix](index.md).[divAssign](../div-assign.md)(value: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html))  <br><br><br>
| <a name="com.github.jomof.kane.impl//mapAssign/com.github.jomof.kane.impl.MutableMatrix#kotlin.Function1[kotlin.Double,kotlin.Double]/PointingToDeclaration/"></a>[mapAssign](../map-assign.md)| <a name="com.github.jomof.kane.impl//mapAssign/com.github.jomof.kane.impl.MutableMatrix#kotlin.Function1[kotlin.Double,kotlin.Double]/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [MutableMatrix](index.md).[mapAssign](../map-assign.md)(init: ([Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)) -> [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html))  <br><br><br>
| <a name="com.github.jomof.kane.impl//minusAssign/com.github.jomof.kane.impl.MutableMatrix#com.github.jomof.kane.impl.Matrix/PointingToDeclaration/"></a>[minusAssign](../minus-assign.md)| <a name="com.github.jomof.kane.impl//minusAssign/com.github.jomof.kane.impl.MutableMatrix#com.github.jomof.kane.impl.Matrix/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>operator fun [MutableMatrix](index.md).[minusAssign](../minus-assign.md)(value: [Matrix](../-matrix/index.md))  <br><br><br>
| <a name="com.github.jomof.kane.impl//set/com.github.jomof.kane.impl.MutableMatrix#kotlin.Double/PointingToDeclaration/"></a>[set](../set.md)| <a name="com.github.jomof.kane.impl//set/com.github.jomof.kane.impl.MutableMatrix#kotlin.Double/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [MutableMatrix](index.md).[set](../set.md)(value: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html))  <br>fun [MutableMatrix](index.md).[set](../set.md)(value: [Matrix](../-matrix/index.md))  <br><br><br>
| <a name="com.github.jomof.kane.impl//timesAssign/com.github.jomof.kane.impl.MutableMatrix#kotlin.Double/PointingToDeclaration/"></a>[timesAssign](../times-assign.md)| <a name="com.github.jomof.kane.impl//timesAssign/com.github.jomof.kane.impl.MutableMatrix#kotlin.Double/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>operator fun [MutableMatrix](index.md).[timesAssign](../times-assign.md)(value: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html))  <br><br><br>
| <a name="com.github.jomof.kane.impl//zero/com.github.jomof.kane.impl.MutableMatrix#/PointingToDeclaration/"></a>[zero](../zero.md)| <a name="com.github.jomof.kane.impl//zero/com.github.jomof.kane.impl.MutableMatrix#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [MutableMatrix](index.md).[zero](../zero.md)()  <br><br><br>

