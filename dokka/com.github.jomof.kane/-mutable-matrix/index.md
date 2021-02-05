//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[MutableMatrix](index.md)



# MutableMatrix  
 [jvm] interface [MutableMatrix](index.md) : [Matrix](../-matrix/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [get](../-matrix/get.md)| [jvm]  <br>Content  <br>open operator override fun [get](../-matrix/get.md)(coordinate: [Coordinate](../-coordinate/index.md)): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br>abstract operator override fun [get](../-matrix/get.md)(column: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), row: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [set](set.md)| [jvm]  <br>Content  <br>open operator fun [set](set.md)(coordinate: [Coordinate](../-coordinate/index.md), value: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html))  <br>abstract operator fun [set](set.md)(column: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), row: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), value: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html))  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [columns](index.md#com.github.jomof.kane/MutableMatrix/columns/#/PointingToDeclaration/)|  [jvm] abstract override val [columns](index.md#com.github.jomof.kane/MutableMatrix/columns/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [rows](index.md#com.github.jomof.kane/MutableMatrix/rows/#/PointingToDeclaration/)|  [jvm] abstract override val [rows](index.md#com.github.jomof.kane/MutableMatrix/rows/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [type](index.md#com.github.jomof.kane/MutableMatrix/type/#/PointingToDeclaration/)|  [jvm] abstract override val [type](index.md#com.github.jomof.kane/MutableMatrix/type/#/PointingToDeclaration/): [AlgebraicType](../../com.github.jomof.kane.types/-algebraic-type/index.md)   <br>


## Inheritors  
  
|  Name| 
|---|
| [LinearMatrixRef](../-linear-matrix-ref/index.md)
| [LookupMatrixRef](../-lookup-matrix-ref/index.md)
| [LookupOrConstantMatrixRef](../-lookup-or-constant-matrix-ref/index.md)


## Extensions  
  
|  Name|  Summary| 
|---|---|
| [divAssign](../div-assign.md)| [jvm]  <br>Content  <br>operator fun [MutableMatrix](index.md).[divAssign](../div-assign.md)(value: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html))  <br><br><br>
| [mapAssign](../map-assign.md)| [jvm]  <br>Content  <br>fun [MutableMatrix](index.md).[mapAssign](../map-assign.md)(init: ([Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)) -> [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html))  <br><br><br>
| [minusAssign](../minus-assign.md)| [jvm]  <br>Content  <br>operator fun [MutableMatrix](index.md).[minusAssign](../minus-assign.md)(value: [Matrix](../-matrix/index.md))  <br><br><br>
| [set](../set.md)| [jvm]  <br>Content  <br>fun [MutableMatrix](index.md).[set](../set.md)(value: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html))  <br>fun [MutableMatrix](index.md).[set](../set.md)(value: [Matrix](../-matrix/index.md))  <br><br><br>
| [timesAssign](../times-assign.md)| [jvm]  <br>Content  <br>operator fun [MutableMatrix](index.md).[timesAssign](../times-assign.md)(value: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html))  <br><br><br>
| [zero](../zero.md)| [jvm]  <br>Content  <br>fun [MutableMatrix](index.md).[zero](../zero.md)()  <br><br><br>

