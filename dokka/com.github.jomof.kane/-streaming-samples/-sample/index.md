//[Kane](../../../index.md)/[com.github.jomof.kane](../../index.md)/[StreamingSamples](../index.md)/[Sample](index.md)



# Sample  
 [jvm] 

Sample for streaming statistics (from https://www.cs.rutgers.edu/~muthu/bquant.pdf) v - is the value of this sample g - is the difference between lowest possible rank of this item and the     lowest possible rank of the item before it. d - difference between the greatest possible rank of this item and the     lowest possible rank of this item.

data class [Sample](index.md)   


## Types  
  
|  Name|  Summary| 
|---|---|
| [Companion](-companion/index.md)| [jvm]  <br>Content  <br>object [Companion](-companion/index.md)  <br><br><br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| [component1](component1.md)| [jvm]  <br>Content  <br>operator fun [component1](component1.md)(): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br><br><br>
| [component2](component2.md)| [jvm]  <br>Content  <br>operator fun [component2](component2.md)(): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br><br><br>
| [component3](component3.md)| [jvm]  <br>Content  <br>operator fun [component3](component3.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [copy](copy.md)| [jvm]  <br>Content  <br>fun [copy](copy.md)(v: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), g: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), d: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [StreamingSamples.Sample](index.md)  <br><br><br>
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [d](index.md#com.github.jomof.kane/StreamingSamples.Sample/d/#/PointingToDeclaration/)|  [jvm] val [d](index.md#com.github.jomof.kane/StreamingSamples.Sample/d/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [g](index.md#com.github.jomof.kane/StreamingSamples.Sample/g/#/PointingToDeclaration/)|  [jvm] val [g](index.md#com.github.jomof.kane/StreamingSamples.Sample/g/#/PointingToDeclaration/): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)   <br>
| [v](index.md#com.github.jomof.kane/StreamingSamples.Sample/v/#/PointingToDeclaration/)|  [jvm] val [v](index.md#com.github.jomof.kane/StreamingSamples.Sample/v/#/PointingToDeclaration/): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)   <br>

