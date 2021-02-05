//[Kane](../../index.md)/[com.github.jomof.kane.types](../index.md)/[KaneType](index.md)



# KaneType  
 [jvm] abstract class [KaneType](index.md)<[E](index.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)>(**java**: [Class](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)<[E](index.md)>)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [render](render.md)| [jvm]  <br>Content  <br>abstract fun [render](render.md)(value: [E](index.md)): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [toString](to-string.md)| [jvm]  <br>Content  <br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [java](index.md#com.github.jomof.kane.types/KaneType/java/#/PointingToDeclaration/)|  [jvm] val [java](index.md#com.github.jomof.kane.types/KaneType/java/#/PointingToDeclaration/): [Class](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)<[E](index.md)>   <br>
| [simpleName](index.md#com.github.jomof.kane.types/KaneType/simpleName/#/PointingToDeclaration/)|  [jvm] open val [simpleName](index.md#com.github.jomof.kane.types/KaneType/simpleName/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>


## Inheritors  
  
|  Name| 
|---|
| [AlgebraicType](../-algebraic-type/index.md)
| [StringKaneType](../-string-kane-type/index.md)
| [IntegerKaneType](../-integer-kane-type/index.md)
| [ObjectKaneType](../-object-kane-type/index.md)

