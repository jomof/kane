//[Kane](../../index.md)/[com.github.jomof.kane.types](../index.md)/[AlgebraicType](index.md)



# AlgebraicType  
 [jvm] abstract class [AlgebraicType](index.md)(**java**: [Class](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)<[Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)>) : [KaneType](../-kane-type/index.md)<[Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)>    


## Functions  
  
|  Name|  Summary| 
|---|---|
| [coerceFrom](coerce-from.md)| [jvm]  <br>Content  <br>abstract fun [coerceFrom](coerce-from.md)(value: [Number](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-number/index.html)): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br><br><br>
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| render| [jvm]  <br>Content  <br>abstract override fun render(value: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [toString](../-kane-type/to-string.md)| [jvm]  <br>Content  <br>open override fun [toString](../-kane-type/to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [java](index.md#com.github.jomof.kane.types/AlgebraicType/java/#/PointingToDeclaration/)|  [jvm] override val [java](index.md#com.github.jomof.kane.types/AlgebraicType/java/#/PointingToDeclaration/): [Class](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)<[Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)>   <br>
| [simpleName](index.md#com.github.jomof.kane.types/AlgebraicType/simpleName/#/PointingToDeclaration/)|  [jvm] open override val [simpleName](index.md#com.github.jomof.kane.types/AlgebraicType/simpleName/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>


## Inheritors  
  
|  Name| 
|---|
| [DollarsAndCentsAlgebraicType](../-dollars-and-cents-algebraic-type/index.md)
| [DollarAlgebraicType](../-dollar-algebraic-type/index.md)
| [PercentAlgebraicType](../-percent-algebraic-type/index.md)
| [DoubleAlgebraicType](../-double-algebraic-type/index.md)

