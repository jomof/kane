//[Kane](../../index.md)/[com.github.jomof.kane.sheet](../index.md)/[AdmissibleDataType](index.md)



# AdmissibleDataType  
 [jvm] interface [AdmissibleDataType](index.md)<[T](index.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)>   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [tryParse](try-parse.md)| [jvm]  <br>Content  <br>abstract fun [tryParse](try-parse.md)(string: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [T](index.md)?  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [type](index.md#com.github.jomof.kane.sheet/AdmissibleDataType/type/#/PointingToDeclaration/)|  [jvm] abstract val [type](index.md#com.github.jomof.kane.sheet/AdmissibleDataType/type/#/PointingToDeclaration/): [KaneType](../../com.github.jomof.kane.types/-kane-type/index.md)<[T](index.md)>   <br>


## Extensions  
  
|  Name|  Summary| 
|---|---|
| [parseToExpr](../parse-to-expr.md)| [jvm]  <br>Content  <br>fun <[T](../parse-to-expr.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)> [AdmissibleDataType](index.md)<[T](../parse-to-expr.md)>.[parseToExpr](../parse-to-expr.md)(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br><br><br>

