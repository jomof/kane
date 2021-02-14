//[Kane](../../index.md)/[com.github.jomof.kane.impl.sheet](../index.md)/[GroupBy](index.md)



# GroupBy  
 [jvm] data class [GroupBy](index.md)(**sheet**: [Sheet](../-sheet/index.md), **keySelector**: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[NamedExpr](../../com.github.jomof.kane.impl/-named-expr/index.md)>) : [Expr](../../com.github.jomof.kane/-expr/index.md)

Conceptually, a GroupBy is a dictionary from key to sheet. It initially consists of [sheet](sheet.md) which is the data source and selector which is used to build a key from a row.

   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl.sheet/GroupBy/GroupBy/#com.github.jomof.kane.impl.sheet.Sheet#kotlin.collections.List[com.github.jomof.kane.impl.NamedExpr]/PointingToDeclaration/"></a>[GroupBy](-group-by.md)| <a name="com.github.jomof.kane.impl.sheet/GroupBy/GroupBy/#com.github.jomof.kane.impl.sheet.Sheet#kotlin.collections.List[com.github.jomof.kane.impl.NamedExpr]/PointingToDeclaration/"></a> [jvm] fun [GroupBy](-group-by.md)(sheet: [Sheet](../-sheet/index.md), keySelector: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[NamedExpr](../../com.github.jomof.kane.impl/-named-expr/index.md)>)   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl.sheet/GroupBy/component1/#/PointingToDeclaration/"></a>[component1](component1.md)| <a name="com.github.jomof.kane.impl.sheet/GroupBy/component1/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>operator fun [component1](component1.md)(): [Sheet](../-sheet/index.md)  <br><br><br>
| <a name="com.github.jomof.kane.impl.sheet/GroupBy/copy/#com.github.jomof.kane.impl.sheet.Sheet#kotlin.collections.List[com.github.jomof.kane.impl.NamedExpr]/PointingToDeclaration/"></a>[copy](copy.md)| <a name="com.github.jomof.kane.impl.sheet/GroupBy/copy/#com.github.jomof.kane.impl.sheet.Sheet#kotlin.collections.List[com.github.jomof.kane.impl.NamedExpr]/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [copy](copy.md)(sheet: [Sheet](../-sheet/index.md), keySelector: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[NamedExpr](../../com.github.jomof.kane.impl/-named-expr/index.md)>): [GroupBy](index.md)  <br><br><br>
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-599678334)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator override fun [equals](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-599678334)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.impl.sheet/GroupBy/get/#kotlin.Array[kotlin.Any]/PointingToDeclaration/"></a>[get](get.md)| <a name="com.github.jomof.kane.impl.sheet/GroupBy/get/#kotlin.Array[kotlin.Any]/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>operator fun [get](get.md)(vararg keys: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)): [Sheet](../-sheet/index.md)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-599678334)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [hashCode](../../com.github.jomof.kane.impl.types/-double-algebraic-type/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-599678334)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
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
| <a name="com.github.jomof.kane//aggregate/com.github.jomof.kane.impl.sheet.GroupBy#kotlin.Array[com.github.jomof.kane.impl.functions.AggregatableFunction]/PointingToDeclaration/"></a>[aggregate](../../com.github.jomof.kane/aggregate.md)| <a name="com.github.jomof.kane//aggregate/com.github.jomof.kane.impl.sheet.GroupBy#kotlin.Array[com.github.jomof.kane.impl.functions.AggregatableFunction]/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [GroupBy](index.md).[aggregate](../../com.github.jomof.kane/aggregate.md)(vararg aggregatables: [AggregatableFunction](../../com.github.jomof.kane.impl.functions/-aggregatable-function/index.md)): [Sheet](../-sheet/index.md)  <br><br><br>
| <a name="com.github.jomof.kane//columns/com.github.jomof.kane.impl.sheet.GroupBy#kotlin.Array[kotlin.String]/PointingToDeclaration/"></a>[columns](../../com.github.jomof.kane/columns.md)| <a name="com.github.jomof.kane//columns/com.github.jomof.kane.impl.sheet.GroupBy#kotlin.Array[kotlin.String]/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [GroupBy](index.md).[columns](../../com.github.jomof.kane/columns.md)(vararg columns: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [GroupBy](index.md)  <br>More info  <br>Get a subset of the columns from this [GroupBy](index.md).  <br><br><br>
| <a name="com.github.jomof.kane//describe/com.github.jomof.kane.impl.sheet.GroupBy#/PointingToDeclaration/"></a>[describe](../../com.github.jomof.kane/describe.md)| <a name="com.github.jomof.kane//describe/com.github.jomof.kane.impl.sheet.GroupBy#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [GroupBy](index.md).[describe](../../com.github.jomof.kane/describe.md)(): [Sheet](../-sheet/index.md)  <br>More info  <br>Return a new sheet with columns summarized into statistics  <br><br><br>
| <a name="com.github.jomof.kane//eval/com.github.jomof.kane.impl.sheet.GroupBy#com.github.jomof.kane.impl.sheet.RangeExprProvider?#kotlin.Boolean#kotlin.collections.Set[kotlin.Any]/PointingToDeclaration/"></a>[eval](../../com.github.jomof.kane/eval.md)| <a name="com.github.jomof.kane//eval/com.github.jomof.kane.impl.sheet.GroupBy#com.github.jomof.kane.impl.sheet.RangeExprProvider?#kotlin.Boolean#kotlin.collections.Set[kotlin.Any]/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [GroupBy](index.md).[eval](../../com.github.jomof.kane/eval.md)(rangeExprProvider: [RangeExprProvider](../-range-expr-provider/index.md)? = null, reduceVariables: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = false, excludeVariables: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[Id](../../com.github.jomof.kane.impl/index.md#%5Bcom.github.jomof.kane.impl%2FId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-599678334)> = setOf()): [GroupBy](index.md)  <br><br><br>

