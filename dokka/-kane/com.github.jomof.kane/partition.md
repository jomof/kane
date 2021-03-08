//[Kane](../index.md)/[com.github.jomof.kane](index.md)/[partition](partition.md)



# partition  
[jvm]  
Content  
inline fun [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)<[Row](../com.github.jomof.kane.api/-row/index.md)>.[partition](partition.md)(crossinline predicate: ([Row](../com.github.jomof.kane.api/-row/index.md)) -> [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)<[Sheet](../com.github.jomof.kane.impl.sheet/-sheet/index.md), [Sheet](../com.github.jomof.kane.impl.sheet/-sheet/index.md)>  
More info  


Splits the original row sequence into pair of row sequences, where *first* contains elements for which predicate yielded true, while *second* contains elements for which predicate yielded false.



The operation is terminal.

  



