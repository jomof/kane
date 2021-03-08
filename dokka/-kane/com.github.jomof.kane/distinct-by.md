//[Kane](../index.md)/[com.github.jomof.kane](index.md)/[distinctBy](distinct-by.md)



# distinctBy  
[jvm]  
Content  
fun <[K](distinct-by.md)> [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)<[Row](../com.github.jomof.kane.api/-row/index.md)>.[distinctBy](distinct-by.md)(selector: ([Row](../com.github.jomof.kane.api/-row/index.md)) -> [K](distinct-by.md)): [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)<[Row](../com.github.jomof.kane.api/-row/index.md)>  
More info  


Returns a row sequence containing only elements from the given sequence having distinct keys returned by the given selector function.



Among elements of the given sequence with equal keys, only the first one will be present in the resulting sequence. The elements in the resulting sequence are in the same order as they were in the source sequence.

  



