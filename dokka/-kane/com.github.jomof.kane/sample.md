//[Kane](../index.md)/[com.github.jomof.kane](index.md)/[sample](sample.md)



# sample  
[jvm]  
Content  
fun [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)<[Row](-row/index.md)>.[sample](sample.md)(n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)? = null, fraction: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)? = null, random: [Random](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.random/-random/index.html) = Random(7)): [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)<[Row](-row/index.md)>  
More info  


Return a random sample from a row sequence n is number of samples to return. Cannot be used with fraction. fraction is fraction of samples to return. Cannot be used with n. Default is n=5. Using fraction may be more efficient in some cases because it can operate sequentially without first counting the number of rows.

  



