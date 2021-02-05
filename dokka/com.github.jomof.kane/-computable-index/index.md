//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[ComputableIndex](index.md)



# ComputableIndex  
 [jvm] sealed class [ComputableIndex](index.md)   


## Types  
  
|  Name|  Summary| 
|---|---|
| [FixedIndex](-fixed-index/index.md)| [jvm]  <br>Content  <br>data class [FixedIndex](-fixed-index/index.md)(**index**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)) : [ComputableIndex](index.md)  <br><br><br>
| [MoveableIndex](-moveable-index/index.md)| [jvm]  <br>Content  <br>data class [MoveableIndex](-moveable-index/index.md)(**index**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)) : [ComputableIndex](index.md)  <br><br><br>
| [RelativeIndex](-relative-index/index.md)| [jvm]  <br>Content  <br>data class [RelativeIndex](-relative-index/index.md)(**index**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)) : [ComputableIndex](index.md)  <br><br><br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [minus](minus.md)| [jvm]  <br>Content  <br>operator fun [minus](minus.md)(move: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [ComputableIndex](index.md)  <br><br><br>
| [plus](plus.md)| [jvm]  <br>Content  <br>operator fun [plus](plus.md)(move: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [ComputableIndex](index.md)  <br><br><br>
| [toColumnName](to-column-name.md)| [jvm]  <br>Content  <br>abstract fun [toColumnName](to-column-name.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [toRowName](to-row-name.md)| [jvm]  <br>Content  <br>abstract fun [toRowName](to-row-name.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [index](index.md#com.github.jomof.kane/ComputableIndex/index/#/PointingToDeclaration/)|  [jvm] abstract val [index](index.md#com.github.jomof.kane/ComputableIndex/index/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>


## Inheritors  
  
|  Name| 
|---|
| [ComputableIndex](-fixed-index/index.md)
| [ComputableIndex](-moveable-index/index.md)
| [ComputableIndex](-relative-index/index.md)

