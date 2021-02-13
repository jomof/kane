//[Kane](../../index.md)/[com.github.jomof.kane.impl.sheet](../index.md)/[Sheet](index.md)/[copy](copy.md)



# copy  
[jvm]  
Content  
open fun [copy](copy.md)(columnDescriptors: [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), [ColumnDescriptor](../-column-descriptor/index.md)> = this.columnDescriptors, rowDescriptors: [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), [RowDescriptor](../-row-descriptor/index.md)> = this.rowDescriptors, cells: [Cells](../-cells/index.md) = this.cells, sheetDescriptor: [SheetDescriptor](../-sheet-descriptor/index.md) = this.sheetDescriptor): [Sheet](index.md)  


[jvm]  
Content  
open fun [copy](copy.md)(vararg assignments: [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)<[Id](../../com.github.jomof.kane.impl/index.md#%5Bcom.github.jomof.kane.impl%2FId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-438281087), [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)>): [Sheet](index.md)  
open fun [copy](copy.md)(init: [SheetBuilderImpl](../-sheet-builder-impl/index.md).() -> [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[NamedExpr](../../com.github.jomof.kane.impl/-named-expr/index.md)>): [Sheet](index.md)  
More info  


Create a new sheet with cell values changed.

  



