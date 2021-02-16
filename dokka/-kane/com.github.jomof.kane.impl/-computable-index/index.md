//[Kane](../../index.md)/[com.github.jomof.kane.impl](../index.md)/[ComputableIndex](index.md)



# ComputableIndex  
 [jvm] sealed class [ComputableIndex](index.md)   


## Types  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl/ComputableIndex.FixedIndex///PointingToDeclaration/"></a>[FixedIndex](-fixed-index/index.md)| <a name="com.github.jomof.kane.impl/ComputableIndex.FixedIndex///PointingToDeclaration/"></a>[jvm]  <br>Content  <br>data class [FixedIndex](-fixed-index/index.md)(**index**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)) : [ComputableIndex](index.md)  <br><br><br>
| <a name="com.github.jomof.kane.impl/ComputableIndex.MoveableIndex///PointingToDeclaration/"></a>[MoveableIndex](-moveable-index/index.md)| <a name="com.github.jomof.kane.impl/ComputableIndex.MoveableIndex///PointingToDeclaration/"></a>[jvm]  <br>Content  <br>data class [MoveableIndex](-moveable-index/index.md)(**index**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)) : [ComputableIndex](index.md)  <br><br><br>
| <a name="com.github.jomof.kane.impl/ComputableIndex.RelativeIndex///PointingToDeclaration/"></a>[RelativeIndex](-relative-index/index.md)| <a name="com.github.jomof.kane.impl/ComputableIndex.RelativeIndex///PointingToDeclaration/"></a>[jvm]  <br>Content  <br>data class [RelativeIndex](-relative-index/index.md)(**index**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)) : [ComputableIndex](index.md)  <br><br><br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-704583245)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-704583245)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-704583245)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-704583245)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.impl/ComputableIndex/minus/#kotlin.Int/PointingToDeclaration/"></a>[minus](minus.md)| <a name="com.github.jomof.kane.impl/ComputableIndex/minus/#kotlin.Int/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>operator fun [minus](minus.md)(move: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [ComputableIndex](index.md)  <br><br><br>
| <a name="com.github.jomof.kane.impl/ComputableIndex/plus/#kotlin.Int/PointingToDeclaration/"></a>[plus](plus.md)| <a name="com.github.jomof.kane.impl/ComputableIndex/plus/#kotlin.Int/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>operator fun [plus](plus.md)(move: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [ComputableIndex](index.md)  <br><br><br>
| <a name="com.github.jomof.kane.impl/ComputableIndex/toColumnName/#/PointingToDeclaration/"></a>[toColumnName](to-column-name.md)| <a name="com.github.jomof.kane.impl/ComputableIndex/toColumnName/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun [toColumnName](to-column-name.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.impl/ComputableIndex/toRowName/#/PointingToDeclaration/"></a>[toRowName](to-row-name.md)| <a name="com.github.jomof.kane.impl/ComputableIndex/toRowName/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun [toRowName](to-row-name.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-704583245)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-704583245)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl/ComputableIndex/index/#/PointingToDeclaration/"></a>[index](--index--.md)| <a name="com.github.jomof.kane.impl/ComputableIndex/index/#/PointingToDeclaration/"></a> [jvm] abstract val [index](--index--.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>


## Inheritors  
  
|  Name| 
|---|
| <a name="com.github.jomof.kane.impl/ComputableIndex.FixedIndex///PointingToDeclaration/"></a>[ComputableIndex](-fixed-index/index.md)
| <a name="com.github.jomof.kane.impl/ComputableIndex.MoveableIndex///PointingToDeclaration/"></a>[ComputableIndex](-moveable-index/index.md)
| <a name="com.github.jomof.kane.impl/ComputableIndex.RelativeIndex///PointingToDeclaration/"></a>[ComputableIndex](-relative-index/index.md)

