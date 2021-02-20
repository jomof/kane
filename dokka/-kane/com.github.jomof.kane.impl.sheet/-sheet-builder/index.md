//[Kane](../../index.md)/[com.github.jomof.kane.impl.sheet](../index.md)/[SheetBuilder](index.md)



# SheetBuilder  
 [jvm] interface [SheetBuilder](index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl.sheet/SheetBuilder/cell/#kotlin.String/PointingToDeclaration/"></a>[cell](cell.md)| <a name="com.github.jomof.kane.impl.sheet/SheetBuilder/cell/#kotlin.String/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [cell](cell.md)(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br><br><br>
| <a name="com.github.jomof.kane.impl.sheet/SheetBuilder/column/#kotlin.String/PointingToDeclaration/"></a>[column](column.md)| <a name="com.github.jomof.kane.impl.sheet/SheetBuilder/column/#kotlin.String/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [column](column.md)(range: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [MatrixExpr](../../com.github.jomof.kane/-matrix-expr/index.md)  <br><br><br>
| <a name="com.github.jomof.kane.impl.sheet/SheetBuilder/down/#kotlin.Int/PointingToDeclaration/"></a>[down](down.md)| <a name="com.github.jomof.kane.impl.sheet/SheetBuilder/down/#kotlin.Int/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [down](down.md)(offset: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [CellSheetRangeExpr](../-cell-sheet-range-expr/index.md)  <br><br><br>
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-2059381145)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-2059381145)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-2059381145)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-2059381145)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.impl.sheet/SheetBuilder/left/#kotlin.Int/PointingToDeclaration/"></a>[left](left.md)| <a name="com.github.jomof.kane.impl.sheet/SheetBuilder/left/#kotlin.Int/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [left](left.md)(offset: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [CellSheetRangeExpr](../-cell-sheet-range-expr/index.md)  <br><br><br>
| <a name="com.github.jomof.kane.impl.sheet/SheetBuilder/nameColumn/#kotlin.Int#kotlin.Any/PointingToDeclaration/"></a>[nameColumn](name-column.md)| <a name="com.github.jomof.kane.impl.sheet/SheetBuilder/nameColumn/#kotlin.Int#kotlin.Any/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun [nameColumn](name-column.md)(column: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), name: [Id](../../com.github.jomof.kane.impl/index.md#%5Bcom.github.jomof.kane.impl%2FId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-2059381145))  <br><br><br>
| <a name="com.github.jomof.kane.impl.sheet/SheetBuilder/nameRow/#kotlin.Int#kotlin.Any/PointingToDeclaration/"></a>[nameRow](name-row.md)| <a name="com.github.jomof.kane.impl.sheet/SheetBuilder/nameRow/#kotlin.Int#kotlin.Any/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun [nameRow](name-row.md)(row: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), name: [Id](../../com.github.jomof.kane.impl/index.md#%5Bcom.github.jomof.kane.impl%2FId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-2059381145))  <br>abstract fun [nameRow](name-row.md)(row: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), name: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Expr](../../com.github.jomof.kane/-expr/index.md)>)  <br><br><br>
| <a name="com.github.jomof.kane.impl.sheet/SheetBuilder/range/#kotlin.String/PointingToDeclaration/"></a>[range](range.md)| <a name="com.github.jomof.kane.impl.sheet/SheetBuilder/range/#kotlin.String/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [range](range.md)(range: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [MatrixExpr](../../com.github.jomof.kane/-matrix-expr/index.md)  <br><br><br>
| <a name="com.github.jomof.kane.impl.sheet/SheetBuilder/right/#kotlin.Int/PointingToDeclaration/"></a>[right](right.md)| <a name="com.github.jomof.kane.impl.sheet/SheetBuilder/right/#kotlin.Int/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [right](right.md)(offset: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [CellSheetRangeExpr](../-cell-sheet-range-expr/index.md)  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-2059381145)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../com.github.jomof.kane.impl.visitor/-difference-visitor/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-2059381145)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.impl.sheet/SheetBuilder/up/#kotlin.Int/PointingToDeclaration/"></a>[up](up.md)| <a name="com.github.jomof.kane.impl.sheet/SheetBuilder/up/#kotlin.Int/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [up](up.md)(offset: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [CellSheetRangeExpr](../-cell-sheet-range-expr/index.md)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl.sheet/SheetBuilder/down/#/PointingToDeclaration/"></a>[down](down.md)| <a name="com.github.jomof.kane.impl.sheet/SheetBuilder/down/#/PointingToDeclaration/"></a> [jvm] open val [down](down.md): [CellSheetRangeExpr](../-cell-sheet-range-expr/index.md)   <br>
| <a name="com.github.jomof.kane.impl.sheet/SheetBuilder/left/#/PointingToDeclaration/"></a>[left](left.md)| <a name="com.github.jomof.kane.impl.sheet/SheetBuilder/left/#/PointingToDeclaration/"></a> [jvm] open val [left](left.md): [CellSheetRangeExpr](../-cell-sheet-range-expr/index.md)   <br>
| <a name="com.github.jomof.kane.impl.sheet/SheetBuilder/right/#/PointingToDeclaration/"></a>[right](right.md)| <a name="com.github.jomof.kane.impl.sheet/SheetBuilder/right/#/PointingToDeclaration/"></a> [jvm] open val [right](right.md): [CellSheetRangeExpr](../-cell-sheet-range-expr/index.md)   <br>
| <a name="com.github.jomof.kane.impl.sheet/SheetBuilder/up/#/PointingToDeclaration/"></a>[up](up.md)| <a name="com.github.jomof.kane.impl.sheet/SheetBuilder/up/#/PointingToDeclaration/"></a> [jvm] open val [up](up.md): [CellSheetRangeExpr](../-cell-sheet-range-expr/index.md)   <br>


## Inheritors  
  
|  Name| 
|---|
| <a name="com.github.jomof.kane.impl.sheet/SheetBuilderImpl///PointingToDeclaration/"></a>[SheetBuilderImpl](../-sheet-builder-impl/index.md)

