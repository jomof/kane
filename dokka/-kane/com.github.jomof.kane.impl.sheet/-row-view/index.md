//[Kane](../../index.md)/[com.github.jomof.kane.impl.sheet](../index.md)/[RowView](index.md)



# RowView  
 [jvm] class [RowView](index.md)(**sheet**: [Sheet](../-sheet/index.md), **row**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)) : [Row](../../com.github.jomof.kane/-row/index.md), [RangeExprProvider](../-range-expr-provider/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-2059381145)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-2059381145)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.impl.sheet/RowView/get/#kotlin.Int/PointingToDeclaration/"></a>[get](get.md)| <a name="com.github.jomof.kane.impl.sheet/RowView/get/#kotlin.Int/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator override fun [get](get.md)(column: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Expr](../../com.github.jomof.kane/-expr/index.md)?  <br>open operator override fun [get](get.md)(column: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-2059381145)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-2059381145)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.impl.sheet/RowView/range/#com.github.jomof.kane.impl.sheet.SheetRangeExpr/PointingToDeclaration/"></a>[range](range.md)| <a name="com.github.jomof.kane.impl.sheet/RowView/range/#com.github.jomof.kane.impl.sheet.SheetRangeExpr/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [range](range.md)(range: [SheetRangeExpr](../-sheet-range-expr/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br><br><br>
| <a name="com.github.jomof.kane.impl.sheet/RowView/toString/#/PointingToDeclaration/"></a>[toString](to-string.md)| <a name="com.github.jomof.kane.impl.sheet/RowView/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl.sheet/RowView/columnCount/#/PointingToDeclaration/"></a>[columnCount](column-count.md)| <a name="com.github.jomof.kane.impl.sheet/RowView/columnCount/#/PointingToDeclaration/"></a> [jvm] open override val [columnCount](column-count.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| <a name="com.github.jomof.kane.impl.sheet/RowView/columnDescriptors/#/PointingToDeclaration/"></a>[columnDescriptors](column-descriptors.md)| <a name="com.github.jomof.kane.impl.sheet/RowView/columnDescriptors/#/PointingToDeclaration/"></a> [jvm] open override val [columnDescriptors](column-descriptors.md): [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), [ColumnDescriptor](../-column-descriptor/index.md)>   <br>
| <a name="com.github.jomof.kane.impl.sheet/RowView/rowDescriptor/#/PointingToDeclaration/"></a>[rowDescriptor](row-descriptor.md)| <a name="com.github.jomof.kane.impl.sheet/RowView/rowDescriptor/#/PointingToDeclaration/"></a> [jvm] open override val [rowDescriptor](row-descriptor.md): [RowDescriptor](../-row-descriptor/index.md)?   <br>
| <a name="com.github.jomof.kane.impl.sheet/RowView/rowOrdinal/#/PointingToDeclaration/"></a>[rowOrdinal](row-ordinal.md)| <a name="com.github.jomof.kane.impl.sheet/RowView/rowOrdinal/#/PointingToDeclaration/"></a> [jvm] open override val [rowOrdinal](row-ordinal.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| <a name="com.github.jomof.kane.impl.sheet/RowView/sheetDescriptor/#/PointingToDeclaration/"></a>[sheetDescriptor](sheet-descriptor.md)| <a name="com.github.jomof.kane.impl.sheet/RowView/sheetDescriptor/#/PointingToDeclaration/"></a> [jvm] open override val [sheetDescriptor](sheet-descriptor.md): [SheetDescriptor](../-sheet-descriptor/index.md)   <br>

