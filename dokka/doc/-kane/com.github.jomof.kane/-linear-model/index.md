//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[LinearModel](index.md)



# LinearModel  
 [jvm] class [LinearModel](index.md)(**type**: [AlgebraicType](../../com.github.jomof.kane.types/-algebraic-type/index.md))   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [allocateSpace](allocate-space.md)| [jvm]  <br>Content  <br>fun [allocateSpace](allocate-space.md)(): [DoubleArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double-array/index.html)  <br><br><br>
| [assignBack](assign-back.md)| [jvm]  <br>Content  <br>fun [assignBack](assign-back.md)(name: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html), constant: [ConstantScalar](../-constant-scalar/index.md))  <br>fun [assignBack](assign-back.md)(name: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html), slot: [Slot](../-slot/index.md))  <br><br><br>
| [computeSlot](compute-slot.md)| [jvm]  <br>Content  <br>fun [computeSlot](compute-slot.md)(expr: [ScalarExpr](../-scalar-expr/index.md), compute: ([ScalarExpr](../-scalar-expr/index.md)) -> [ScalarExpr](../-scalar-expr/index.md)): [Slot](../-slot/index.md)  <br><br><br>
| [contains](contains.md)| [jvm]  <br>Content  <br>fun [contains](contains.md)(expr: [ScalarExpr](../-scalar-expr/index.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [eval](eval.md)| [jvm]  <br>Content  <br>fun [eval](eval.md)(space: [DoubleArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double-array/index.html))  <br>fun [eval](eval.md)(space: [DoubleArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double-array/index.html), protect: [MatrixShape](../-matrix-shape/index.md))  <br><br><br>
| [getValue](get-value.md)| [jvm]  <br>Content  <br>fun [getValue](get-value.md)(expr: [AlgebraicExpr](../-algebraic-expr/index.md)): [Slot](../-slot/index.md)  <br>fun [getValue](get-value.md)(expr: [ScalarExpr](../-scalar-expr/index.md)): [Slot](../-slot/index.md)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [knownSlot](known-slot.md)| [jvm]  <br>Content  <br>fun [knownSlot](known-slot.md)(expr: [ScalarExpr](../-scalar-expr/index.md)): [Slot](../-slot/index.md)  <br><br><br>
| [registerMatrixVariableElement](register-matrix-variable-element.md)| [jvm]  <br>Content  <br>fun [registerMatrixVariableElement](register-matrix-variable-element.md)(expr: [MatrixVariableElement](../-matrix-variable-element/index.md)): [Slot](../-slot/index.md)  <br><br><br>
| [registerNamedMatrixShape](register-named-matrix-shape.md)| [jvm]  <br>Content  <br>fun [registerNamedMatrixShape](register-named-matrix-shape.md)(name: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html), init: () -> [MatrixShape](../-matrix-shape/index.md))  <br><br><br>
| [registerNamedScalar](register-named-scalar.md)| [jvm]  <br>Content  <br>fun [registerNamedScalar](register-named-scalar.md)(name: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html), scalar: [ScalarExpr](../-scalar-expr/index.md))  <br><br><br>
| [registerNamedScalarVariable](register-named-scalar-variable.md)| [jvm]  <br>Content  <br>fun [registerNamedScalarVariable](register-named-scalar-variable.md)(expr: [NamedScalarVariable](../-named-scalar-variable/index.md)): [Slot](../-slot/index.md)  <br><br><br>
| [registerRandomVariable](register-random-variable.md)| [jvm]  <br>Content  <br>fun [registerRandomVariable](register-random-variable.md)(expr: [RandomVariableExpr](../-random-variable-expr/index.md)): [Slot](../-slot/index.md)  <br><br><br>
| [setValue](set-value.md)| [jvm]  <br>Content  <br>fun [setValue](set-value.md)(expr: [ScalarExpr](../-scalar-expr/index.md), slot: [Slot](../-slot/index.md))  <br><br><br>
| [shape](shape.md)| [jvm]  <br>Content  <br>fun [shape](shape.md)(expr: [NamedMatrixExpr](../-named-matrix-expr/index.md)): [MatrixShape](../-matrix-shape/index.md)  <br>fun [shape](shape.md)(expr: [NamedMatrixVariable](../-named-matrix-variable/index.md)): [MatrixShape](../-matrix-shape/index.md)  <br>fun [shape](shape.md)(expr: [NamedScalarExpr](../-named-scalar-expr/index.md)): [EmbeddedScalarShape](../-embedded-scalar-shape/index.md)  <br>fun [shape](shape.md)(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [EmbeddedScalarShape](../-embedded-scalar-shape/index.md)  <br><br><br>
| [slotCount](slot-count.md)| [jvm]  <br>Content  <br>fun [slotCount](slot-count.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [slotOfExistingMatrixVariableElement](slot-of-existing-matrix-variable-element.md)| [jvm]  <br>Content  <br>fun [slotOfExistingMatrixVariableElement](slot-of-existing-matrix-variable-element.md)(expr: [MatrixVariableElement](../-matrix-variable-element/index.md)): [Slot](../-slot/index.md)  <br><br><br>
| [slotOfExistingNamedScalarVariable](slot-of-existing-named-scalar-variable.md)| [jvm]  <br>Content  <br>fun [slotOfExistingNamedScalarVariable](slot-of-existing-named-scalar-variable.md)(expr: [NamedScalarVariable](../-named-scalar-variable/index.md)): [Slot](../-slot/index.md)  <br><br><br>
| [slotOfExistingRandomVariable](slot-of-existing-random-variable.md)| [jvm]  <br>Content  <br>fun [slotOfExistingRandomVariable](slot-of-existing-random-variable.md)(expr: [RandomVariableExpr](../-random-variable-expr/index.md)): [Slot](../-slot/index.md)  <br><br><br>
| [toString](to-string.md)| [jvm]  <br>Content  <br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [type](index.md#com.github.jomof.kane/LinearModel/type/#/PointingToDeclaration/)|  [jvm] val [type](index.md#com.github.jomof.kane/LinearModel/type/#/PointingToDeclaration/): [AlgebraicType](../../com.github.jomof.kane.types/-algebraic-type/index.md)   <br>


## Extensions  
  
|  Name|  Summary| 
|---|---|
| [toFunc](../to-func.md)| [jvm]  <br>Content  <br>fun [LinearModel](index.md).[toFunc](../to-func.md)(space: [DoubleArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double-array/index.html), v1: [NamedMatrixExpr](../-named-matrix-expr/index.md), out: [NamedMatrixExpr](../-named-matrix-expr/index.md)): ([Matrix](../-matrix/index.md)) -> [Matrix](../-matrix/index.md)  <br><br><br>

