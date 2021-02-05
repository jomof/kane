//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[StreamingSamples](index.md)



# StreamingSamples  
 [jvm] 

Class that accumulates statistics in a streaming manner.

class [StreamingSamples](index.md)(**samples**: [MutableList](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)<[StreamingSamples.Sample](-sample/index.md)>)   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| [StreamingSamples](-streaming-samples.md)|  [jvm] fun [StreamingSamples](-streaming-samples.md)(samples: [MutableList](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)<[StreamingSamples.Sample](-sample/index.md)>)   <br>


## Types  
  
|  Name|  Summary| 
|---|---|
| [Companion](-companion/index.md)| [jvm]  <br>Content  <br>object [Companion](-companion/index.md)  <br><br><br>
| [Sample](-sample/index.md)| [jvm]  <br>Brief description  <br><br><br>Sample for streaming statistics (from https://www.cs.rutgers.edu/~muthu/bquant.pdf) v - is the value of this sample g - is the difference between lowest possible rank of this item and the     lowest possible rank of the item before it. d - difference between the greatest possible rank of this item and the     lowest possible rank of this item.<br><br>  <br>Content  <br>data class [Sample](-sample/index.md)  <br><br><br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| [compress](compress.md)| [jvm]  <br>Content  <br>fun [compress](compress.md)()  <br><br><br>
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [insert](insert.md)| [jvm]  <br>Content  <br>fun [insert](insert.md)(value: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html))  <br><br><br>
| [percentile](percentile.md)| [jvm]  <br>Content  <br>fun [percentile](percentile.md)(quantile: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [coefficientOfVariation](index.md#com.github.jomof.kane/StreamingSamples/coefficientOfVariation/#/PointingToDeclaration/)|  [jvm] val [coefficientOfVariation](index.md#com.github.jomof.kane/StreamingSamples/coefficientOfVariation/#/PointingToDeclaration/): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)   <br>
| [count](index.md#com.github.jomof.kane/StreamingSamples/count/#/PointingToDeclaration/)|  [jvm] val [count](index.md#com.github.jomof.kane/StreamingSamples/count/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [kurtosis](index.md#com.github.jomof.kane/StreamingSamples/kurtosis/#/PointingToDeclaration/)|  [jvm] val [kurtosis](index.md#com.github.jomof.kane/StreamingSamples/kurtosis/#/PointingToDeclaration/): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)   <br>
| [max](index.md#com.github.jomof.kane/StreamingSamples/max/#/PointingToDeclaration/)|  [jvm] val [max](index.md#com.github.jomof.kane/StreamingSamples/max/#/PointingToDeclaration/): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)   <br>
| [mean](index.md#com.github.jomof.kane/StreamingSamples/mean/#/PointingToDeclaration/)|  [jvm] val [mean](index.md#com.github.jomof.kane/StreamingSamples/mean/#/PointingToDeclaration/): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)   <br>
| [median](index.md#com.github.jomof.kane/StreamingSamples/median/#/PointingToDeclaration/)|  [jvm] val [median](index.md#com.github.jomof.kane/StreamingSamples/median/#/PointingToDeclaration/): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)   <br>
| [min](index.md#com.github.jomof.kane/StreamingSamples/min/#/PointingToDeclaration/)|  [jvm] val [min](index.md#com.github.jomof.kane/StreamingSamples/min/#/PointingToDeclaration/): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)   <br>
| [nans](index.md#com.github.jomof.kane/StreamingSamples/nans/#/PointingToDeclaration/)|  [jvm] val [nans](index.md#com.github.jomof.kane/StreamingSamples/nans/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [sampleCount](index.md#com.github.jomof.kane/StreamingSamples/sampleCount/#/PointingToDeclaration/)|  [jvm] val [sampleCount](index.md#com.github.jomof.kane/StreamingSamples/sampleCount/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [skewness](index.md#com.github.jomof.kane/StreamingSamples/skewness/#/PointingToDeclaration/)|  [jvm] val [skewness](index.md#com.github.jomof.kane/StreamingSamples/skewness/#/PointingToDeclaration/): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)   <br>
| [stddev](index.md#com.github.jomof.kane/StreamingSamples/stddev/#/PointingToDeclaration/)|  [jvm] val [stddev](index.md#com.github.jomof.kane/StreamingSamples/stddev/#/PointingToDeclaration/): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)   <br>
| [sum](index.md#com.github.jomof.kane/StreamingSamples/sum/#/PointingToDeclaration/)|  [jvm] val [sum](index.md#com.github.jomof.kane/StreamingSamples/sum/#/PointingToDeclaration/): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)   <br>
| [variance](index.md#com.github.jomof.kane/StreamingSamples/variance/#/PointingToDeclaration/)|  [jvm] val [variance](index.md#com.github.jomof.kane/StreamingSamples/variance/#/PointingToDeclaration/): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)   <br>

