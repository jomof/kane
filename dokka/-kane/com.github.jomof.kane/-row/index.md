//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[Row](index.md)



# Row  
 [jvm] abstract class [Row](index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane/Row/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](equals.md)| <a name="com.github.jomof.kane/Row/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="com.github.jomof.kane/Row/get/#kotlin.Int/PointingToDeclaration/"></a>[get](get.md)| <a name="com.github.jomof.kane/Row/get/#kotlin.Int/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract operator fun [get](get.md)(column: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?  <br>abstract operator fun [get](get.md)(column: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?  <br><br><br>
| <a name="com.github.jomof.kane/Row/hashCode/#/PointingToDeclaration/"></a>[hashCode](hash-code.md)| <a name="com.github.jomof.kane/Row/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [hashCode](hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="com.github.jomof.kane/Row/toString/#/PointingToDeclaration/"></a>[toString](to-string.md)| <a name="com.github.jomof.kane/Row/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane/Row/columnCount/#/PointingToDeclaration/"></a>[columnCount](column-count.md)| <a name="com.github.jomof.kane/Row/columnCount/#/PointingToDeclaration/"></a> [jvm] abstract val [columnCount](column-count.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| <a name="com.github.jomof.kane/Row/columnDescriptors/#/PointingToDeclaration/"></a>[columnDescriptors](column-descriptors.md)| <a name="com.github.jomof.kane/Row/columnDescriptors/#/PointingToDeclaration/"></a> [jvm] abstract val [columnDescriptors](column-descriptors.md): [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), [ColumnDescriptor](../../com.github.jomof.kane.impl.sheet/-column-descriptor/index.md)>   <br>
| <a name="com.github.jomof.kane/Row/rowDescriptor/#/PointingToDeclaration/"></a>[rowDescriptor](row-descriptor.md)| <a name="com.github.jomof.kane/Row/rowDescriptor/#/PointingToDeclaration/"></a> [jvm] abstract val [rowDescriptor](row-descriptor.md): [RowDescriptor](../../com.github.jomof.kane.impl.sheet/-row-descriptor/index.md)?   <br>
| <a name="com.github.jomof.kane/Row/rowOrdinal/#/PointingToDeclaration/"></a>[rowOrdinal](row-ordinal.md)| <a name="com.github.jomof.kane/Row/rowOrdinal/#/PointingToDeclaration/"></a> [jvm] abstract val [rowOrdinal](row-ordinal.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| <a name="com.github.jomof.kane/Row/sheetDescriptor/#/PointingToDeclaration/"></a>[sheetDescriptor](sheet-descriptor.md)| <a name="com.github.jomof.kane/Row/sheetDescriptor/#/PointingToDeclaration/"></a> [jvm] abstract val [sheetDescriptor](sheet-descriptor.md): [SheetDescriptor](../../com.github.jomof.kane.impl.sheet/-sheet-descriptor/index.md)   <br>


## Inheritors  
  
|  Name| 
|---|
| <a name="com.github.jomof.kane.impl.sheet/RowView///PointingToDeclaration/"></a>[RowView](../../com.github.jomof.kane.impl.sheet/-row-view/index.md)

