//[Kane](../../index.md)/[com.github.jomof.kane](../index.md)/[Expr](index.md)



# Expr  
 [jvm] interface [Expr](index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Inheritors  
  
|  Name| 
|---|
| [UntypedScalar](../-untyped-scalar/index.md)
| [TypedExpr](../-typed-expr/index.md)
| [AlgebraicExpr](../-algebraic-expr/index.md)
| [NamedExpr](../-named-expr/index.md)
| [ScalarAssign](../-scalar-assign/index.md)
| [MatrixAssign](../-matrix-assign/index.md)
| [GroupBy](../../com.github.jomof.kane.sheet/-group-by/index.md)
| [Sheet](../../com.github.jomof.kane.sheet/-sheet/index.md)


## Extensions  
  
|  Name|  Summary| 
|---|---|
| [canGetConstant](../can-get-constant.md)| [jvm]  <br>Content  <br>fun [Expr](index.md).[canGetConstant](../can-get-constant.md)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [convertCellNamesToUpperCase](../../com.github.jomof.kane.sheet/convert-cell-names-to-upper-case.md)| [jvm]  <br>Content  <br>fun [Expr](index.md).[convertCellNamesToUpperCase](../../com.github.jomof.kane.sheet/convert-cell-names-to-upper-case.md)(): [Expr](index.md)  <br><br><br>
| [count](../../com.github.jomof.kane.visitor/count.md)| [jvm]  <br>Content  <br>fun [Expr](index.md).[count](../../com.github.jomof.kane.visitor/count.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [describe](../../com.github.jomof.kane.sheet/describe.md)| [jvm]  <br>Brief description  <br><br><br>Return a new sheet with data summarized into statistics<br><br>  <br>Content  <br>fun [Expr](index.md).[describe](../../com.github.jomof.kane.sheet/describe.md)(): [Sheet](../../com.github.jomof.kane.sheet/-sheet/index.md)  <br><br><br>
| [eval](../eval.md)| [jvm]  <br>Content  <br>fun [Expr](index.md).[eval](../eval.md)(rangeExprProvider: [RangeExprProvider](../../com.github.jomof.kane.sheet/-range-expr-provider/index.md), reduceVariables: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), excludeVariables: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)>): [Expr](index.md)  <br><br><br>
| [expandNamedCells](../../com.github.jomof.kane.sheet/expand-named-cells.md)| [jvm]  <br>Content  <br>fun [Expr](index.md).[expandNamedCells](../../com.github.jomof.kane.sheet/expand-named-cells.md)(lookup: [Cells](../../com.github.jomof.kane.sheet/-cells/index.md)): [Expr](index.md)  <br><br><br>
| [expandUnaryOperations](../../com.github.jomof.kane.sheet/expand-unary-operations.md)| [jvm]  <br>Content  <br>fun [Expr](index.md).[expandUnaryOperations](../../com.github.jomof.kane.sheet/expand-unary-operations.md)(): [Expr](index.md)  <br><br><br>
| [fillna](../../com.github.jomof.kane.sheet/fillna.md)| [jvm]  <br>Content  <br>fun [Expr](index.md).[fillna](../../com.github.jomof.kane.sheet/fillna.md)(value: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)): [Expr](index.md)  <br><br><br>
| [findRandomVariables](../find-random-variables.md)| [jvm]  <br>Content  <br>fun [Expr](index.md).[findRandomVariables](../find-random-variables.md)(): [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[RandomVariableExpr](../-random-variable-expr/index.md)>  <br><br><br>
| [freeze](../../com.github.jomof.kane.sheet/freeze.md)| [jvm]  <br>Brief description  <br><br><br>Convert relative cell references to moveable. current is the cell that this formula occupies.<br><br>  <br>Content  <br>fun [Expr](index.md).[freeze](../../com.github.jomof.kane.sheet/freeze.md)(current: [Coordinate](../-coordinate/index.md)): [Expr](index.md)  <br><br><br>
| [getConstant](../get-constant.md)| [jvm]  <br>Content  <br>fun [Expr](index.md).[getConstant](../get-constant.md)(): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br><br><br>
| [linearize](../linearize.md)| [jvm]  <br>Content  <br>fun [Expr](index.md).[linearize](../linearize.md)(): [LinearModel](../-linear-model/index.md)  <br><br><br>
| [mapDoubles](../../com.github.jomof.kane.sheet/map-doubles.md)| [jvm]  <br>Content  <br>fun [Expr](index.md).[mapDoubles](../../com.github.jomof.kane.sheet/map-doubles.md)(translate: ([Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)) -> [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)): [Expr](index.md)  <br><br><br>
| [render](../render.md)| [jvm]  <br>Content  <br>fun [Expr](index.md).[render](../render.md)(entryPoint: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), outerType: [AlgebraicType](../../com.github.jomof.kane.types/-algebraic-type/index.md)?): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [replaceNamesWithCellReferences](../../com.github.jomof.kane.sheet/replace-names-with-cell-references.md)| [jvm]  <br>Content  <br>fun [Expr](index.md).[replaceNamesWithCellReferences](../../com.github.jomof.kane.sheet/replace-names-with-cell-references.md)(excluding: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)): [Expr](index.md)  <br><br><br>
| [replaceRelativeCellReferences](../../com.github.jomof.kane.sheet/replace-relative-cell-references.md)| [jvm]  <br>Content  <br>fun [Expr](index.md).[replaceRelativeCellReferences](../../com.github.jomof.kane.sheet/replace-relative-cell-references.md)(coordinate: [Coordinate](../-coordinate/index.md)): [Expr](index.md)  <br><br><br>
| [replaceSheetRanges](../replace-sheet-ranges.md)| [jvm]  <br>Content  <br>fun [Expr](index.md).[replaceSheetRanges](../replace-sheet-ranges.md)(): [Expr](index.md)  <br><br><br>
| [slideScalarizedCellsRewritingVisitor](../../com.github.jomof.kane.sheet/slide-scalarized-cells-rewriting-visitor.md)| [jvm]  <br>Content  <br>fun [Expr](index.md).[slideScalarizedCellsRewritingVisitor](../../com.github.jomof.kane.sheet/slide-scalarized-cells-rewriting-visitor.md)(upperLeft: [Coordinate](../-coordinate/index.md), offset: [Coordinate](../-coordinate/index.md)): [Expr](index.md)  <br><br><br>
| [toNamed](../to-named.md)| [jvm]  <br>Content  <br>fun [Expr](index.md).[toNamed](../to-named.md)(name: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)): [NamedExpr](../-named-expr/index.md)  <br><br><br>
| [toUnnamed](../to-unnamed.md)| [jvm]  <br>Content  <br>fun [Expr](index.md).[toUnnamed](../to-unnamed.md)(): [Expr](index.md)  <br><br><br>
| [tryGetBinaryOp](../try-get-binary-op.md)| [jvm]  <br>Content  <br>fun [Expr](index.md).[tryGetBinaryOp](../try-get-binary-op.md)(): [BinaryOp](../-binary-op/index.md)?  <br><br><br>
| [type](../../com.github.jomof.kane.types/index.md#com.github.jomof.kane.types//type/com.github.jomof.kane.Expr#/PointingToDeclaration/)| [jvm]  <br>Brief description  <br><br><br>Get the type of an expression.<br><br>  <br>Content  <br>val [Expr](index.md).[type](../../com.github.jomof.kane.types/index.md#com.github.jomof.kane.types//type/com.github.jomof.kane.Expr#/PointingToDeclaration/): [KaneType](../../com.github.jomof.kane.types/-kane-type/index.md)<*>  <br><br><br>
| [unfreeze](../../com.github.jomof.kane.sheet/unfreeze.md)| [jvm]  <br>Brief description  <br><br><br>Convert moveable cell references to relative. This is so that rows and columns can be moved, added, and deleted before refreezing. current is the cell that this formula occupies.<br><br>  <br>Content  <br>fun [Expr](index.md).[unfreeze](../../com.github.jomof.kane.sheet/unfreeze.md)(current: [Coordinate](../-coordinate/index.md)): [Expr](index.md)  <br><br><br>
| [visit](../../com.github.jomof.kane.visitor/visit.md)| [jvm]  <br>Content  <br>fun [Expr](index.md).[visit](../../com.github.jomof.kane.visitor/visit.md)(f: ([Expr](index.md)) -> [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html))  <br><br><br>

