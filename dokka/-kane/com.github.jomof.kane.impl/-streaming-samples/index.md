//[Kane](../../index.md)/[com.github.jomof.kane.impl](../index.md)/[StreamingSamples](index.md)



# StreamingSamples  
 [jvm] class [StreamingSamples](index.md)(**samples**: [MutableList](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)<[StreamingSamples.Sample](-sample/index.md)>)

Class that accumulates statistics in a streaming manner.

   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl/StreamingSamples/StreamingSamples/#kotlin.collections.MutableList[com.github.jomof.kane.impl.StreamingSamples.Sample]/PointingToDeclaration/"></a>[StreamingSamples](-streaming-samples.md)| <a name="com.github.jomof.kane.impl/StreamingSamples/StreamingSamples/#kotlin.collections.MutableList[com.github.jomof.kane.impl.StreamingSamples.Sample]/PointingToDeclaration/"></a> [jvm] fun [StreamingSamples](-streaming-samples.md)(samples: [MutableList](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)<[StreamingSamples.Sample](-sample/index.md)> = mutableListOf())   <br>


## Types  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl/StreamingSamples.Companion///PointingToDeclaration/"></a>[Companion](-companion/index.md)| <a name="com.github.jomof.kane.impl/StreamingSamples.Companion///PointingToDeclaration/"></a>[jvm]  <br>Content  <br>object [Companion](-companion/index.md)  <br><br><br>
| <a name="com.github.jomof.kane.impl/StreamingSamples.Sample///PointingToDeclaration/"></a>[Sample](-sample/index.md)| <a name="com.github.jomof.kane.impl/StreamingSamples.Sample///PointingToDeclaration/"></a>[jvm]  <br>Content  <br>data class [Sample](-sample/index.md)  <br>More info  <br>Sample for streaming statistics (from https://www.cs.rutgers.edu/~muthu/bquant.pdf) v - is the value of this sample g - is the difference between lowest possible rank of this item and the     lowest possible rank of the item before it.  <br><br><br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl/StreamingSamples/compress/#/PointingToDeclaration/"></a>[compress](compress.md)| <a name="com.github.jomof.kane.impl/StreamingSamples/compress/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [compress](compress.md)()  <br><br><br>
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-562016314)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-562016314)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-562016314)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-562016314)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.impl/StreamingSamples/insert/#kotlin.Double/PointingToDeclaration/"></a>[insert](insert.md)| <a name="com.github.jomof.kane.impl/StreamingSamples/insert/#kotlin.Double/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [insert](insert.md)(value: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html))  <br><br><br>
| <a name="com.github.jomof.kane.impl/StreamingSamples/percentile/#kotlin.Double/PointingToDeclaration/"></a>[percentile](percentile.md)| <a name="com.github.jomof.kane.impl/StreamingSamples/percentile/#kotlin.Double/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [percentile](percentile.md)(quantile: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../com.github.jomof.kane.impl.types/-object-kane-type/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-562016314)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../com.github.jomof.kane.impl.types/-object-kane-type/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-562016314)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl/StreamingSamples/coefficientOfVariation/#/PointingToDeclaration/"></a>[coefficientOfVariation](coefficient-of-variation.md)| <a name="com.github.jomof.kane.impl/StreamingSamples/coefficientOfVariation/#/PointingToDeclaration/"></a> [jvm] val [coefficientOfVariation](coefficient-of-variation.md): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)   <br>
| <a name="com.github.jomof.kane.impl/StreamingSamples/count/#/PointingToDeclaration/"></a>[count](count.md)| <a name="com.github.jomof.kane.impl/StreamingSamples/count/#/PointingToDeclaration/"></a> [jvm] val [count](count.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| <a name="com.github.jomof.kane.impl/StreamingSamples/kurtosis/#/PointingToDeclaration/"></a>[kurtosis](kurtosis.md)| <a name="com.github.jomof.kane.impl/StreamingSamples/kurtosis/#/PointingToDeclaration/"></a> [jvm] val [kurtosis](kurtosis.md): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)   <br>
| <a name="com.github.jomof.kane.impl/StreamingSamples/max/#/PointingToDeclaration/"></a>[max](max.md)| <a name="com.github.jomof.kane.impl/StreamingSamples/max/#/PointingToDeclaration/"></a> [jvm] val [max](max.md): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)   <br>
| <a name="com.github.jomof.kane.impl/StreamingSamples/mean/#/PointingToDeclaration/"></a>[mean](mean.md)| <a name="com.github.jomof.kane.impl/StreamingSamples/mean/#/PointingToDeclaration/"></a> [jvm] val [mean](mean.md): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)   <br>
| <a name="com.github.jomof.kane.impl/StreamingSamples/median/#/PointingToDeclaration/"></a>[median](median.md)| <a name="com.github.jomof.kane.impl/StreamingSamples/median/#/PointingToDeclaration/"></a> [jvm] val [median](median.md): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)   <br>
| <a name="com.github.jomof.kane.impl/StreamingSamples/min/#/PointingToDeclaration/"></a>[min](min.md)| <a name="com.github.jomof.kane.impl/StreamingSamples/min/#/PointingToDeclaration/"></a> [jvm] val [min](min.md): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)   <br>
| <a name="com.github.jomof.kane.impl/StreamingSamples/nans/#/PointingToDeclaration/"></a>[nans](nans.md)| <a name="com.github.jomof.kane.impl/StreamingSamples/nans/#/PointingToDeclaration/"></a> [jvm] val [nans](nans.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| <a name="com.github.jomof.kane.impl/StreamingSamples/sampleCount/#/PointingToDeclaration/"></a>[sampleCount](sample-count.md)| <a name="com.github.jomof.kane.impl/StreamingSamples/sampleCount/#/PointingToDeclaration/"></a> [jvm] val [sampleCount](sample-count.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| <a name="com.github.jomof.kane.impl/StreamingSamples/skewness/#/PointingToDeclaration/"></a>[skewness](skewness.md)| <a name="com.github.jomof.kane.impl/StreamingSamples/skewness/#/PointingToDeclaration/"></a> [jvm] val [skewness](skewness.md): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)   <br>
| <a name="com.github.jomof.kane.impl/StreamingSamples/stdev/#/PointingToDeclaration/"></a>[stdev](stdev.md)| <a name="com.github.jomof.kane.impl/StreamingSamples/stdev/#/PointingToDeclaration/"></a> [jvm] val [stdev](stdev.md): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)   <br>
| <a name="com.github.jomof.kane.impl/StreamingSamples/sum/#/PointingToDeclaration/"></a>[sum](sum.md)| <a name="com.github.jomof.kane.impl/StreamingSamples/sum/#/PointingToDeclaration/"></a> [jvm] val [sum](sum.md): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)   <br>
| <a name="com.github.jomof.kane.impl/StreamingSamples/variance/#/PointingToDeclaration/"></a>[variance](variance.md)| <a name="com.github.jomof.kane.impl/StreamingSamples/variance/#/PointingToDeclaration/"></a> [jvm] val [variance](variance.md): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)   <br>

