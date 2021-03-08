//[Kane](../index.md)/[com.github.jomof.kane](index.md)/[groupBy](group-by.md)



# groupBy  
[jvm]  
Content  
fun [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)<[Row](../com.github.jomof.kane.api/-row/index.md)>.[groupBy](group-by.md)(vararg columns: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [GroupBy](../com.github.jomof.kane.impl.sheet/-group-by/index.md)  
More info  


Group a [Sheet](../com.github.jomof.kane.impl.sheet/-sheet/index.md) by columns. The result is a [GroupBy](../com.github.jomof.kane.impl.sheet/-group-by/index.md) object that is effectively a map from key (the columns) to a [Sheet](../com.github.jomof.kane.impl.sheet/-sheet/index.md) that has those column values.

  



