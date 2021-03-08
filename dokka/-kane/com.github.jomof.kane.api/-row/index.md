//[Kane](../../index.md)/[com.github.jomof.kane.api](../index.md)/[Row](index.md)



# Row  
 [jvm] interface [Row](index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1776797766)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1776797766)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.api/Row/get/#kotlin.Int/PointingToDeclaration/"></a>[get](get.md)| <a name="com.github.jomof.kane.api/Row/get/#kotlin.Int/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract operator fun [get](get.md)(column: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?  <br>abstract operator fun [get](get.md)(column: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1776797766)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1776797766)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1776797766)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1776797766)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.api/Row/columnCount/#/PointingToDeclaration/"></a>[columnCount](column-count.md)| <a name="com.github.jomof.kane.api/Row/columnCount/#/PointingToDeclaration/"></a> [jvm] abstract val [columnCount](column-count.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| <a name="com.github.jomof.kane.api/Row/columnDescriptors/#/PointingToDeclaration/"></a>[columnDescriptors](column-descriptors.md)| <a name="com.github.jomof.kane.api/Row/columnDescriptors/#/PointingToDeclaration/"></a> [jvm] abstract val [columnDescriptors](column-descriptors.md): [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), [ColumnDescriptor](../../com.github.jomof.kane.impl.sheet/-column-descriptor/index.md)>   <br>
| <a name="com.github.jomof.kane.api/Row/rowDescriptor/#/PointingToDeclaration/"></a>[rowDescriptor](row-descriptor.md)| <a name="com.github.jomof.kane.api/Row/rowDescriptor/#/PointingToDeclaration/"></a> [jvm] abstract val [rowDescriptor](row-descriptor.md): [RowDescriptor](../-row-descriptor/index.md)?   <br>
| <a name="com.github.jomof.kane.api/Row/rowOrdinal/#/PointingToDeclaration/"></a>[rowOrdinal](row-ordinal.md)| <a name="com.github.jomof.kane.api/Row/rowOrdinal/#/PointingToDeclaration/"></a> [jvm] abstract val [rowOrdinal](row-ordinal.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| <a name="com.github.jomof.kane.api/Row/sheetDescriptor/#/PointingToDeclaration/"></a>[sheetDescriptor](sheet-descriptor.md)| <a name="com.github.jomof.kane.api/Row/sheetDescriptor/#/PointingToDeclaration/"></a> [jvm] abstract val [sheetDescriptor](sheet-descriptor.md): [SheetDescriptor](../-sheet-descriptor/index.md)   <br>


## Inheritors  
  
|  Name| 
|---|
| <a name="com.github.jomof.kane/RowBase///PointingToDeclaration/"></a>[RowBase](../../com.github.jomof.kane/-row-base/index.md)

