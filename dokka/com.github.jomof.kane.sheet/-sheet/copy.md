//[Kane](../../index.md)/[com.github.jomof.kane.sheet](../index.md)/[Sheet](index.md)/[copy](copy.md)



# copy  
[jvm]  
Content  
open fun [copy](copy.md)(columnDescriptors: [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), [ColumnDescriptor](../-column-descriptor/index.md)>, rowDescriptors: [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), [RowDescriptor](../-row-descriptor/index.md)>, cells: [Cells](../-cells/index.md), sheetDescriptor: [SheetDescriptor](../-sheet-descriptor/index.md)): [Sheet](index.md)  


[jvm]  
Brief description  


Create a new sheet with cell values changed.

  
Content  
open fun [copy](copy.md)(vararg assignments: [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)<Out [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)>>): [Sheet](index.md)  
open fun [copy](copy.md)(init: [SheetBuilderImpl](../-sheet-builder-impl/index.md).() -> [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[NamedExpr](../../com.github.jomof.kane/-named-expr/index.md)>): [Sheet](index.md)  



