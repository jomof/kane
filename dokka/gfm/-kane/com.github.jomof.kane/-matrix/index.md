//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[Matrix](index.md)



# Matrix  
 [jvm] interface [Matrix](index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [get](get.md)| [jvm]  <br>Content  <br>open operator fun [get](get.md)(coordinate: [Coordinate](../-coordinate/index.md)): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br>abstract operator fun [get](get.md)(column: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), row: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [columns](index.md#com.github.jomof.kane/Matrix/columns/#/PointingToDeclaration/)|  [jvm] abstract val [columns](index.md#com.github.jomof.kane/Matrix/columns/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [rows](index.md#com.github.jomof.kane/Matrix/rows/#/PointingToDeclaration/)|  [jvm] abstract val [rows](index.md#com.github.jomof.kane/Matrix/rows/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [type](index.md#com.github.jomof.kane/Matrix/type/#/PointingToDeclaration/)|  [jvm] abstract val [type](index.md#com.github.jomof.kane/Matrix/type/#/PointingToDeclaration/): [AlgebraicType](../../com.github.jomof.kane.types/-algebraic-type/index.md)   <br>


## Inheritors  
  
|  Name| 
|---|
| [MutableMatrix](../-mutable-matrix/index.md)
| [ValueMatrix](../-value-matrix/index.md)


## Extensions  
  
|  Name|  Summary| 
|---|---|
| [forEach](../for-each.md)| [jvm]  <br>Content  <br>fun [Matrix](index.md).[forEach](../for-each.md)(call: ([Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)) -> [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html))  <br><br><br>
| [render](../render.md)| [jvm]  <br>Content  <br>fun [Matrix](index.md).[render](../render.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>

