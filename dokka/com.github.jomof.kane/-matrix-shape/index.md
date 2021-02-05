//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[MatrixShape](index.md)



# MatrixShape  
 [jvm] interface [MatrixShape](index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [owns](owns.md)| [jvm]  <br>Content  <br>abstract fun [owns](owns.md)(index: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [ref](ref.md)| [jvm]  <br>Content  <br>abstract fun [ref](ref.md)(array: [DoubleArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double-array/index.html)): [MutableMatrix](../-mutable-matrix/index.md)  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [columns](index.md#com.github.jomof.kane/MatrixShape/columns/#/PointingToDeclaration/)|  [jvm] abstract val [columns](index.md#com.github.jomof.kane/MatrixShape/columns/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [rows](index.md#com.github.jomof.kane/MatrixShape/rows/#/PointingToDeclaration/)|  [jvm] abstract val [rows](index.md#com.github.jomof.kane/MatrixShape/rows/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [type](index.md#com.github.jomof.kane/MatrixShape/type/#/PointingToDeclaration/)|  [jvm] abstract val [type](index.md#com.github.jomof.kane/MatrixShape/type/#/PointingToDeclaration/): [AlgebraicType](../../com.github.jomof.kane.types/-algebraic-type/index.md)   <br>


## Inheritors  
  
|  Name| 
|---|
| [LinearMatrixShape](../-linear-matrix-shape/index.md)
| [LookupMatrixShape](../-lookup-matrix-shape/index.md)
| [LookupOrConstantsMatrixShape](../-lookup-or-constants-matrix-shape/index.md)

