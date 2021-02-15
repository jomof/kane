//[Kane](../../../index.md)/[com.github.jomof.kane.impl](../../index.md)/[StreamingSamples](../index.md)/[Sample](index.md)



# Sample  
 [jvm] data class [Sample](index.md)

Sample for streaming statistics (from https://www.cs.rutgers.edu/~muthu/bquant.pdf) v - is the value of this sample g - is the difference between lowest possible rank of this item and the     lowest possible rank of the item before it. d - difference between the greatest possible rank of this item and the     lowest possible rank of this item.

   


## Types  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl/StreamingSamples.Sample.Companion///PointingToDeclaration/"></a>[Companion](-companion/index.md)| <a name="com.github.jomof.kane.impl/StreamingSamples.Sample.Companion///PointingToDeclaration/"></a>[jvm]  <br>Content  <br>object [Companion](-companion/index.md)  <br><br><br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl/StreamingSamples.Sample/component1/#/PointingToDeclaration/"></a>[component1](component1.md)| <a name="com.github.jomof.kane.impl/StreamingSamples.Sample/component1/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>operator fun [component1](component1.md)(): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.impl/StreamingSamples.Sample/component2/#/PointingToDeclaration/"></a>[component2](component2.md)| <a name="com.github.jomof.kane.impl/StreamingSamples.Sample/component2/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>operator fun [component2](component2.md)(): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.impl/StreamingSamples.Sample/component3/#/PointingToDeclaration/"></a>[component3](component3.md)| <a name="com.github.jomof.kane.impl/StreamingSamples.Sample/component3/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>operator fun [component3](component3.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.impl/StreamingSamples.Sample/copy/#kotlin.Double#kotlin.Double#kotlin.Int/PointingToDeclaration/"></a>[copy](copy.md)| <a name="com.github.jomof.kane.impl/StreamingSamples.Sample/copy/#kotlin.Double#kotlin.Double#kotlin.Int/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [copy](copy.md)(v: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), g: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), d: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [StreamingSamples.Sample](index.md)  <br><br><br>
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1797850740)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator override fun [equals](../../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1797850740)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1797850740)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [hashCode](../../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1797850740)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../../com.github.jomof.kane.impl.types/-object-kane-type/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1797850740)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [toString](../../../com.github.jomof.kane.impl.types/-object-kane-type/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1797850740)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl/StreamingSamples.Sample/d/#/PointingToDeclaration/"></a>[d](d.md)| <a name="com.github.jomof.kane.impl/StreamingSamples.Sample/d/#/PointingToDeclaration/"></a> [jvm] val [d](d.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| <a name="com.github.jomof.kane.impl/StreamingSamples.Sample/g/#/PointingToDeclaration/"></a>[g](g.md)| <a name="com.github.jomof.kane.impl/StreamingSamples.Sample/g/#/PointingToDeclaration/"></a> [jvm] val [g](g.md): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)   <br>
| <a name="com.github.jomof.kane.impl/StreamingSamples.Sample/v/#/PointingToDeclaration/"></a>[v](v.md)| <a name="com.github.jomof.kane.impl/StreamingSamples.Sample/v/#/PointingToDeclaration/"></a> [jvm] val [v](v.md): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)   <br>

