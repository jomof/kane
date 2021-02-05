//[Kane](../index.md)/[com.github.jomof.kane.types](index.md)



# Package com.github.jomof.kane.types  


## Types  
  
|  Name|  Summary| 
|---|---|
| [AlgebraicType](-algebraic-type/index.md)| [jvm]  <br>Content  <br>abstract class [AlgebraicType](-algebraic-type/index.md)(**java**: [Class](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)<[Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)>) : [KaneType](-kane-type/index.md)<[Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)>   <br><br><br>
| [DollarAlgebraicType](-dollar-algebraic-type/index.md)| [jvm]  <br>Content  <br>class [DollarAlgebraicType](-dollar-algebraic-type/index.md) : [AlgebraicType](-algebraic-type/index.md)  <br><br><br>
| [DollarsAndCentsAlgebraicType](-dollars-and-cents-algebraic-type/index.md)| [jvm]  <br>Content  <br>class [DollarsAndCentsAlgebraicType](-dollars-and-cents-algebraic-type/index.md) : [AlgebraicType](-algebraic-type/index.md)  <br><br><br>
| [DoubleAlgebraicType](-double-algebraic-type/index.md)| [jvm]  <br>Content  <br>class [DoubleAlgebraicType](-double-algebraic-type/index.md)(**prefix**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **trimLeastSignificantZeros**: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), **precision**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)) : [AlgebraicType](-algebraic-type/index.md)  <br><br><br>
| [IntegerKaneType](-integer-kane-type/index.md)| [jvm]  <br>Content  <br>class [IntegerKaneType](-integer-kane-type/index.md) : [KaneType](-kane-type/index.md)<[Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)>   <br><br><br>
| [KaneType](-kane-type/index.md)| [jvm]  <br>Content  <br>abstract class [KaneType](-kane-type/index.md)<[E](-kane-type/index.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)>(**java**: [Class](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)<[E](-kane-type/index.md)>)  <br><br><br>
| [ObjectKaneType](-object-kane-type/index.md)| [jvm]  <br>Content  <br>class [ObjectKaneType](-object-kane-type/index.md) : [KaneType](-kane-type/index.md)<[Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)>   <br><br><br>
| [PercentAlgebraicType](-percent-algebraic-type/index.md)| [jvm]  <br>Content  <br>class [PercentAlgebraicType](-percent-algebraic-type/index.md) : [AlgebraicType](-algebraic-type/index.md)  <br><br><br>
| [StringKaneType](-string-kane-type/index.md)| [jvm]  <br>Content  <br>class [StringKaneType](-string-kane-type/index.md) : [KaneType](-kane-type/index.md)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>   <br><br><br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| [dollars](dollars.md)| [jvm]  <br>Content  <br>fun [dollars](dollars.md)(value: [MatrixExpr](../com.github.jomof.kane/-matrix-expr/index.md)): [RetypeMatrix](../com.github.jomof.kane/-retype-matrix/index.md)  <br>fun [dollars](dollars.md)(value: [ScalarExpr](../com.github.jomof.kane/-scalar-expr/index.md)): [RetypeScalar](../com.github.jomof.kane/-retype-scalar/index.md)  <br>fun [dollars](dollars.md)(value: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)): [ScalarExpr](../com.github.jomof.kane/-scalar-expr/index.md)  <br>fun [dollars](dollars.md)(value: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [ScalarExpr](../com.github.jomof.kane/-scalar-expr/index.md)  <br><br><br>
| [percent](percent.md)| [jvm]  <br>Content  <br>fun [percent](percent.md)(value: [MatrixExpr](../com.github.jomof.kane/-matrix-expr/index.md)): [RetypeMatrix](../com.github.jomof.kane/-retype-matrix/index.md)  <br>fun [percent](percent.md)(value: [ScalarExpr](../com.github.jomof.kane/-scalar-expr/index.md)): [RetypeScalar](../com.github.jomof.kane/-retype-scalar/index.md)  <br>fun [percent](percent.md)(value: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)): [ScalarExpr](../com.github.jomof.kane/-scalar-expr/index.md)  <br>fun [percent](percent.md)(value: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [ScalarExpr](../com.github.jomof.kane/-scalar-expr/index.md)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [algebraicType](index.md#com.github.jomof.kane.types//algebraicType/com.github.jomof.kane.AlgebraicExpr#/PointingToDeclaration/)|  [jvm] val [AlgebraicExpr](../com.github.jomof.kane/-algebraic-expr/index.md).[algebraicType](index.md#com.github.jomof.kane.types//algebraicType/com.github.jomof.kane.AlgebraicExpr#/PointingToDeclaration/): [AlgebraicType](-algebraic-type/index.md)   <br>
| [kaneDouble](index.md#com.github.jomof.kane.types//kaneDouble/#/PointingToDeclaration/)|  [jvm] val [kaneDouble](index.md#com.github.jomof.kane.types//kaneDouble/#/PointingToDeclaration/): [DoubleAlgebraicType](-double-algebraic-type/index.md)   <br>
| [kaneType](index.md#com.github.jomof.kane.types//kaneType/java.lang.Class[TypeParam(bounds=[kotlin.Any])]#/PointingToDeclaration/)|  [jvm] val <[T](index.md#com.github.jomof.kane.types//kaneType/java.lang.Class[TypeParam(bounds=[kotlin.Any])]#/PointingToDeclaration/) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)> [Class](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)<[T](index.md#com.github.jomof.kane.types//kaneType/java.lang.Class[TypeParam(bounds=[kotlin.Any])]#/PointingToDeclaration/)>.[kaneType](index.md#com.github.jomof.kane.types//kaneType/java.lang.Class[TypeParam(bounds=[kotlin.Any])]#/PointingToDeclaration/): [KaneType](-kane-type/index.md)<[T](index.md#com.github.jomof.kane.types//kaneType/java.lang.Class[TypeParam(bounds=[kotlin.Any])]#/PointingToDeclaration/)>   <br>
| [type](index.md#com.github.jomof.kane.types//type/com.github.jomof.kane.Expr#/PointingToDeclaration/)|  [jvm] <br><br>Get the type of an expression.<br><br>val [Expr](../com.github.jomof.kane/-expr/index.md).[type](index.md#com.github.jomof.kane.types//type/com.github.jomof.kane.Expr#/PointingToDeclaration/): [KaneType](-kane-type/index.md)<*>   <br>

