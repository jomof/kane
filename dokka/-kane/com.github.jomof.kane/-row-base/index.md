//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[RowBase](index.md)



# RowBase  
 [jvm] abstract class [RowBase](index.md) : [Row](../../com.github.jomof.kane.api/-row/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane/RowBase/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](equals.md)| <a name="com.github.jomof.kane/RowBase/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.api/Row/get/#kotlin.Int/PointingToDeclaration/"></a>[get](../../com.github.jomof.kane.api/-row/get.md)| <a name="com.github.jomof.kane.api/Row/get/#kotlin.Int/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract operator fun [get](../../com.github.jomof.kane.api/-row/get.md)(column: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?  <br>abstract operator fun [get](../../com.github.jomof.kane.api/-row/get.md)(column: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?  <br><br><br>
| <a name="com.github.jomof.kane/RowBase/hashCode/#/PointingToDeclaration/"></a>[hashCode](hash-code.md)| <a name="com.github.jomof.kane/RowBase/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [hashCode](hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="com.github.jomof.kane/RowBase/toString/#/PointingToDeclaration/"></a>[toString](to-string.md)| <a name="com.github.jomof.kane/RowBase/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane/RowBase/columnCount/#/PointingToDeclaration/"></a>[columnCount](index.md#%5Bcom.github.jomof.kane%2FRowBase%2FcolumnCount%2F%23%2FPointingToDeclaration%2F%5D%2FProperties%2F-1422180844)| <a name="com.github.jomof.kane/RowBase/columnCount/#/PointingToDeclaration/"></a> [jvm] abstract val [columnCount](index.md#%5Bcom.github.jomof.kane%2FRowBase%2FcolumnCount%2F%23%2FPointingToDeclaration%2F%5D%2FProperties%2F-1422180844): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| <a name="com.github.jomof.kane/RowBase/columnDescriptors/#/PointingToDeclaration/"></a>[columnDescriptors](index.md#%5Bcom.github.jomof.kane%2FRowBase%2FcolumnDescriptors%2F%23%2FPointingToDeclaration%2F%5D%2FProperties%2F-1422180844)| <a name="com.github.jomof.kane/RowBase/columnDescriptors/#/PointingToDeclaration/"></a> [jvm] abstract val [columnDescriptors](index.md#%5Bcom.github.jomof.kane%2FRowBase%2FcolumnDescriptors%2F%23%2FPointingToDeclaration%2F%5D%2FProperties%2F-1422180844): [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), [ColumnDescriptor](../../com.github.jomof.kane.impl.sheet/-column-descriptor/index.md)>   <br>
| <a name="com.github.jomof.kane/RowBase/rowDescriptor/#/PointingToDeclaration/"></a>[rowDescriptor](index.md#%5Bcom.github.jomof.kane%2FRowBase%2FrowDescriptor%2F%23%2FPointingToDeclaration%2F%5D%2FProperties%2F-1422180844)| <a name="com.github.jomof.kane/RowBase/rowDescriptor/#/PointingToDeclaration/"></a> [jvm] abstract val [rowDescriptor](index.md#%5Bcom.github.jomof.kane%2FRowBase%2FrowDescriptor%2F%23%2FPointingToDeclaration%2F%5D%2FProperties%2F-1422180844): [RowDescriptor](../../com.github.jomof.kane.impl.sheet/-row-descriptor/index.md)?   <br>
| <a name="com.github.jomof.kane/RowBase/rowOrdinal/#/PointingToDeclaration/"></a>[rowOrdinal](index.md#%5Bcom.github.jomof.kane%2FRowBase%2FrowOrdinal%2F%23%2FPointingToDeclaration%2F%5D%2FProperties%2F-1422180844)| <a name="com.github.jomof.kane/RowBase/rowOrdinal/#/PointingToDeclaration/"></a> [jvm] abstract val [rowOrdinal](index.md#%5Bcom.github.jomof.kane%2FRowBase%2FrowOrdinal%2F%23%2FPointingToDeclaration%2F%5D%2FProperties%2F-1422180844): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| <a name="com.github.jomof.kane/RowBase/sheetDescriptor/#/PointingToDeclaration/"></a>[sheetDescriptor](index.md#%5Bcom.github.jomof.kane%2FRowBase%2FsheetDescriptor%2F%23%2FPointingToDeclaration%2F%5D%2FProperties%2F-1422180844)| <a name="com.github.jomof.kane/RowBase/sheetDescriptor/#/PointingToDeclaration/"></a> [jvm] abstract val [sheetDescriptor](index.md#%5Bcom.github.jomof.kane%2FRowBase%2FsheetDescriptor%2F%23%2FPointingToDeclaration%2F%5D%2FProperties%2F-1422180844): [SheetDescriptor](../../com.github.jomof.kane.impl.sheet/-sheet-descriptor/index.md)   <br>


## Inheritors  
  
|  Name| 
|---|
| <a name="com.github.jomof.kane.impl.sheet/RowView///PointingToDeclaration/"></a>[RowView](../../com.github.jomof.kane.impl.sheet/-row-view/index.md)

