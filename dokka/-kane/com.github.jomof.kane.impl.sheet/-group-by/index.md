//[Kane](../../index.md)/[com.github.jomof.kane.impl.sheet](../index.md)/[GroupBy](index.md)



# GroupBy  
 [jvm] class [GroupBy](index.md)(**sheet**: [Sheet](../-sheet/index.md), **keySelector**: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[NamedExpr](../../com.github.jomof.kane.impl/-named-expr/index.md)>) : [Expr](../../com.github.jomof.kane/-expr/index.md)

Conceptually, a GroupBy is a dictionary from key to sheet. It initially consists of [sheet](sheet.md) which is the data source and selector which is used to build a key from a row.

   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl.sheet/GroupBy/GroupBy/#com.github.jomof.kane.impl.sheet.Sheet#kotlin.collections.List[com.github.jomof.kane.impl.NamedExpr]/PointingToDeclaration/"></a>[GroupBy](-group-by.md)| <a name="com.github.jomof.kane.impl.sheet/GroupBy/GroupBy/#com.github.jomof.kane.impl.sheet.Sheet#kotlin.collections.List[com.github.jomof.kane.impl.NamedExpr]/PointingToDeclaration/"></a> [jvm] fun [GroupBy](-group-by.md)(sheet: [Sheet](../-sheet/index.md), keySelector: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[NamedExpr](../../com.github.jomof.kane.impl/-named-expr/index.md)>)   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1172341673)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1172341673)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.impl.sheet/GroupBy/get/#kotlin.Array[kotlin.Any]/PointingToDeclaration/"></a>[get](get.md)| <a name="com.github.jomof.kane.impl.sheet/GroupBy/get/#kotlin.Array[kotlin.Any]/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>operator fun [get](get.md)(vararg keys: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)): [Sheet](../-sheet/index.md)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1172341673)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-1172341673)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.impl.sheet/GroupBy/iterator/#/PointingToDeclaration/"></a>[iterator](iterator.md)| <a name="com.github.jomof.kane.impl.sheet/GroupBy/iterator/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>operator fun [iterator](iterator.md)(): [Iterator](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterator/index.html)<[Map.Entry](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/-entry/index.html)<[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Expr](../../com.github.jomof.kane/-expr/index.md)>, [Sheet](../-sheet/index.md)>>  <br><br><br>
| <a name="com.github.jomof.kane.impl.sheet/GroupBy/toString/#/PointingToDeclaration/"></a>[toString](to-string.md)| <a name="com.github.jomof.kane.impl.sheet/GroupBy/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl.sheet/GroupBy/html/#/PointingToDeclaration/"></a>[html](html.md)| <a name="com.github.jomof.kane.impl.sheet/GroupBy/html/#/PointingToDeclaration/"></a> [jvm] val [html](html.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| <a name="com.github.jomof.kane.impl.sheet/GroupBy/sheet/#/PointingToDeclaration/"></a>[sheet](sheet.md)| <a name="com.github.jomof.kane.impl.sheet/GroupBy/sheet/#/PointingToDeclaration/"></a> [jvm] val [sheet](sheet.md): [Sheet](../-sheet/index.md)   <br>


## Extensions  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl.sheet//aggregate/com.github.jomof.kane.impl.sheet.GroupBy#kotlin.Function1[com.github.jomof.kane.impl.sheet.SheetBuilder,kotlin.collections.List[com.github.jomof.kane.impl.NamedExpr]]/PointingToDeclaration/"></a>[aggregate](../aggregate.md)| <a name="com.github.jomof.kane.impl.sheet//aggregate/com.github.jomof.kane.impl.sheet.GroupBy#kotlin.Function1[com.github.jomof.kane.impl.sheet.SheetBuilder,kotlin.collections.List[com.github.jomof.kane.impl.NamedExpr]]/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [GroupBy](index.md).[aggregate](../aggregate.md)(selector: [SheetBuilder](../-sheet-builder/index.md).() -> [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[NamedExpr](../../com.github.jomof.kane.impl/-named-expr/index.md)>): [Sheet](../-sheet/index.md)  <br>fun [GroupBy](index.md).[aggregate](../aggregate.md)(vararg functions: [AlgebraicUnaryScalarStatisticFunction](../../com.github.jomof.kane.impl.functions/-algebraic-unary-scalar-statistic-function/index.md)): [Sheet](../-sheet/index.md)  <br><br><br>
| <a name="com.github.jomof.kane//describe/com.github.jomof.kane.impl.sheet.GroupBy#/PointingToDeclaration/"></a>[describe](../../com.github.jomof.kane/describe.md)| <a name="com.github.jomof.kane//describe/com.github.jomof.kane.impl.sheet.GroupBy#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [GroupBy](index.md).[describe](../../com.github.jomof.kane/describe.md)(): [Sheet](../-sheet/index.md)  <br>More info  <br>Return a new sheet with columns summarized into statistics  <br><br><br>

