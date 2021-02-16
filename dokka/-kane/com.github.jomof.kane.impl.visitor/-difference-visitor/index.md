//[Kane](../../index.md)/[com.github.jomof.kane.impl.visitor](../index.md)/[DifferenceVisitor](index.md)



# DifferenceVisitor  
 [jvm] open class [DifferenceVisitor](index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="com.github.jomof.kane.impl.visitor/DifferenceVisitor/algebraic/#com.github.jomof.kane.Expr#com.github.jomof.kane.Expr/PointingToDeclaration/"></a>[algebraic](algebraic.md)| <a name="com.github.jomof.kane.impl.visitor/DifferenceVisitor/algebraic/#com.github.jomof.kane.Expr#com.github.jomof.kane.Expr/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [algebraic](algebraic.md)(e1: [Expr](../../com.github.jomof.kane/-expr/index.md), e2: [Expr](../../com.github.jomof.kane/-expr/index.md)): [AlgebraicExpr](../../com.github.jomof.kane/-algebraic-expr/index.md)  <br><br><br>
| <a name="com.github.jomof.kane.impl.visitor/DifferenceVisitor/cell/#kotlin.Any#com.github.jomof.kane.Expr#com.github.jomof.kane.Expr/PointingToDeclaration/"></a>[cell](cell.md)| <a name="com.github.jomof.kane.impl.visitor/DifferenceVisitor/cell/#kotlin.Any#com.github.jomof.kane.Expr#com.github.jomof.kane.Expr/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [cell](cell.md)(name: [Id](../../com.github.jomof.kane.impl/index.md#%5Bcom.github.jomof.kane.impl%2FId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-704583245), e1: [Expr](../../com.github.jomof.kane/-expr/index.md), e2: [Expr](../../com.github.jomof.kane/-expr/index.md)): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)<[Id](../../com.github.jomof.kane.impl/index.md#%5Bcom.github.jomof.kane.impl%2FId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-704583245), [Expr](../../com.github.jomof.kane/-expr/index.md)>  <br><br><br>
| <a name="com.github.jomof.kane.impl.visitor/DifferenceVisitor/different/#com.github.jomof.kane.Expr#com.github.jomof.kane.Expr/PointingToDeclaration/"></a>[different](different.md)| <a name="com.github.jomof.kane.impl.visitor/DifferenceVisitor/different/#com.github.jomof.kane.Expr#com.github.jomof.kane.Expr/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [different](different.md)(e1: [Expr](../../com.github.jomof.kane/-expr/index.md), e2: [Expr](../../com.github.jomof.kane/-expr/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br><br><br>
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-704583245)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-704583245)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-704583245)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-704583245)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.impl.visitor/DifferenceVisitor/matrix/#com.github.jomof.kane.Expr#com.github.jomof.kane.Expr/PointingToDeclaration/"></a>[matrix](matrix.md)| <a name="com.github.jomof.kane.impl.visitor/DifferenceVisitor/matrix/#com.github.jomof.kane.Expr#com.github.jomof.kane.Expr/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [matrix](matrix.md)(e1: [Expr](../../com.github.jomof.kane/-expr/index.md), e2: [Expr](../../com.github.jomof.kane/-expr/index.md)): [MatrixExpr](../../com.github.jomof.kane/-matrix-expr/index.md)  <br><br><br>
| <a name="com.github.jomof.kane.impl.visitor/DifferenceVisitor/missingCellLeft/#kotlin.Any#com.github.jomof.kane.impl.sheet.Sheet#com.github.jomof.kane.impl.sheet.Sheet/PointingToDeclaration/"></a>[missingCellLeft](missing-cell-left.md)| <a name="com.github.jomof.kane.impl.visitor/DifferenceVisitor/missingCellLeft/#kotlin.Any#com.github.jomof.kane.impl.sheet.Sheet#com.github.jomof.kane.impl.sheet.Sheet/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [missingCellLeft](missing-cell-left.md)(name: [Id](../../com.github.jomof.kane.impl/index.md#%5Bcom.github.jomof.kane.impl%2FId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-704583245), e1: [Sheet](../../com.github.jomof.kane.impl.sheet/-sheet/index.md), e2: [Sheet](../../com.github.jomof.kane.impl.sheet/-sheet/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br><br><br>
| <a name="com.github.jomof.kane.impl.visitor/DifferenceVisitor/missingCellRight/#kotlin.Any#com.github.jomof.kane.impl.sheet.Sheet#com.github.jomof.kane.impl.sheet.Sheet/PointingToDeclaration/"></a>[missingCellRight](missing-cell-right.md)| <a name="com.github.jomof.kane.impl.visitor/DifferenceVisitor/missingCellRight/#kotlin.Any#com.github.jomof.kane.impl.sheet.Sheet#com.github.jomof.kane.impl.sheet.Sheet/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [missingCellRight](missing-cell-right.md)(name: [Id](../../com.github.jomof.kane.impl/index.md#%5Bcom.github.jomof.kane.impl%2FId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-704583245), e1: [Sheet](../../com.github.jomof.kane.impl.sheet/-sheet/index.md), e2: [Sheet](../../com.github.jomof.kane.impl.sheet/-sheet/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br><br><br>
| <a name="com.github.jomof.kane.impl.visitor/DifferenceVisitor/scalar/#com.github.jomof.kane.Expr#com.github.jomof.kane.Expr/PointingToDeclaration/"></a>[scalar](scalar.md)| <a name="com.github.jomof.kane.impl.visitor/DifferenceVisitor/scalar/#com.github.jomof.kane.Expr#com.github.jomof.kane.Expr/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [scalar](scalar.md)(e1: [Expr](../../com.github.jomof.kane/-expr/index.md), e2: [Expr](../../com.github.jomof.kane/-expr/index.md)): [ScalarExpr](../../com.github.jomof.kane/-scalar-expr/index.md)  <br><br><br>
| <a name="com.github.jomof.kane.impl.visitor/DifferenceVisitor/sheet/#com.github.jomof.kane.impl.sheet.Sheet#com.github.jomof.kane.impl.sheet.Sheet/PointingToDeclaration/"></a>[sheet](sheet.md)| <a name="com.github.jomof.kane.impl.visitor/DifferenceVisitor/sheet/#com.github.jomof.kane.impl.sheet.Sheet#com.github.jomof.kane.impl.sheet.Sheet/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [sheet](sheet.md)(e1: [Sheet](../../com.github.jomof.kane.impl.sheet/-sheet/index.md), e2: [Sheet](../../com.github.jomof.kane.impl.sheet/-sheet/index.md)): [Sheet](../../com.github.jomof.kane.impl.sheet/-sheet/index.md)  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-704583245)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-704583245)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| <a name="com.github.jomof.kane.impl.visitor/DifferenceVisitor/visit/#com.github.jomof.kane.AlgebraicBinaryMatrixMatrixMatrix#com.github.jomof.kane.AlgebraicBinaryMatrixMatrixMatrix/PointingToDeclaration/"></a>[visit](visit.md)| <a name="com.github.jomof.kane.impl.visitor/DifferenceVisitor/visit/#com.github.jomof.kane.AlgebraicBinaryMatrixMatrixMatrix#com.github.jomof.kane.AlgebraicBinaryMatrixMatrixMatrix/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [visit](visit.md)(e1: [AlgebraicBinaryMatrixMatrixMatrix](../../com.github.jomof.kane/-algebraic-binary-matrix-matrix-matrix/index.md), e2: [AlgebraicBinaryMatrixMatrixMatrix](../../com.github.jomof.kane/-algebraic-binary-matrix-matrix-matrix/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br>fun [visit](visit.md)(e1: [AlgebraicBinaryMatrixScalarMatrix](../../com.github.jomof.kane/-algebraic-binary-matrix-scalar-matrix/index.md), e2: [AlgebraicBinaryMatrixScalarMatrix](../../com.github.jomof.kane/-algebraic-binary-matrix-scalar-matrix/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br>fun [visit](visit.md)(e1: [AlgebraicBinaryScalarMatrixMatrix](../../com.github.jomof.kane/-algebraic-binary-scalar-matrix-matrix/index.md), e2: [AlgebraicBinaryScalarMatrixMatrix](../../com.github.jomof.kane/-algebraic-binary-scalar-matrix-matrix/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br>fun [visit](visit.md)(e1: [AlgebraicBinaryScalarScalarScalar](../../com.github.jomof.kane/-algebraic-binary-scalar-scalar-scalar/index.md), e2: [AlgebraicBinaryScalarScalarScalar](../../com.github.jomof.kane/-algebraic-binary-scalar-scalar-scalar/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br>fun [visit](visit.md)(e1: [AlgebraicBinarySummaryMatrixScalarScalar](../../com.github.jomof.kane/-algebraic-binary-summary-matrix-scalar-scalar/index.md), e2: [AlgebraicBinarySummaryMatrixScalarScalar](../../com.github.jomof.kane/-algebraic-binary-summary-matrix-scalar-scalar/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br>fun [visit](visit.md)(e1: [AlgebraicBinarySummaryScalarScalarScalar](../../com.github.jomof.kane/-algebraic-binary-summary-scalar-scalar-scalar/index.md), e2: [AlgebraicBinarySummaryScalarScalarScalar](../../com.github.jomof.kane/-algebraic-binary-summary-scalar-scalar-scalar/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br>fun [visit](visit.md)(e1: [AlgebraicSummaryMatrixScalar](../../com.github.jomof.kane/-algebraic-summary-matrix-scalar/index.md), e2: [AlgebraicSummaryMatrixScalar](../../com.github.jomof.kane/-algebraic-summary-matrix-scalar/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br>fun [visit](visit.md)(e1: [AlgebraicSummaryScalarScalar](../../com.github.jomof.kane/-algebraic-summary-scalar-scalar/index.md), e2: [AlgebraicSummaryScalarScalar](../../com.github.jomof.kane/-algebraic-summary-scalar-scalar/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br>fun [visit](visit.md)(e1: [AlgebraicUnaryMatrixMatrix](../../com.github.jomof.kane/-algebraic-unary-matrix-matrix/index.md), e2: [AlgebraicUnaryMatrixMatrix](../../com.github.jomof.kane/-algebraic-unary-matrix-matrix/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br>fun [visit](visit.md)(e1: [AlgebraicUnaryScalarScalar](../../com.github.jomof.kane/-algebraic-unary-scalar-scalar/index.md), e2: [AlgebraicUnaryScalarScalar](../../com.github.jomof.kane/-algebraic-unary-scalar-scalar/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br>fun [visit](visit.md)(e1: [Expr](../../com.github.jomof.kane/-expr/index.md), e2: [Expr](../../com.github.jomof.kane/-expr/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br>fun [visit](visit.md)(e1: [DataMatrix](../../com.github.jomof.kane.impl/-data-matrix/index.md), e2: [DataMatrix](../../com.github.jomof.kane.impl/-data-matrix/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br>fun [visit](visit.md)(e1: [MatrixVariableElement](../../com.github.jomof.kane.impl/-matrix-variable-element/index.md), e2: [MatrixVariableElement](../../com.github.jomof.kane.impl/-matrix-variable-element/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br>fun [visit](visit.md)(e1: [NamedMatrix](../../com.github.jomof.kane.impl/-named-matrix/index.md), e2: [NamedMatrix](../../com.github.jomof.kane.impl/-named-matrix/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br>fun [visit](visit.md)(e1: [NamedScalar](../../com.github.jomof.kane.impl/-named-scalar/index.md), e2: [NamedScalar](../../com.github.jomof.kane.impl/-named-scalar/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br>fun [visit](visit.md)(e1: [RetypeMatrix](../../com.github.jomof.kane.impl/-retype-matrix/index.md), e2: [RetypeMatrix](../../com.github.jomof.kane.impl/-retype-matrix/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br>fun [visit](visit.md)(e1: [RetypeScalar](../../com.github.jomof.kane.impl/-retype-scalar/index.md), e2: [RetypeScalar](../../com.github.jomof.kane.impl/-retype-scalar/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br>fun [visit](visit.md)(e1: [Tableau](../../com.github.jomof.kane.impl/-tableau/index.md), e2: [Tableau](../../com.github.jomof.kane.impl/-tableau/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br>fun [visit](visit.md)(e1: [AlgebraicDeferredDataMatrix](../../com.github.jomof.kane.impl.functions/-algebraic-deferred-data-matrix/index.md), e2: [AlgebraicDeferredDataMatrix](../../com.github.jomof.kane.impl.functions/-algebraic-deferred-data-matrix/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br>fun [visit](visit.md)(e1: [CellIndexedScalar](../../com.github.jomof.kane.impl.sheet/-cell-indexed-scalar/index.md), e2: [CellIndexedScalar](../../com.github.jomof.kane.impl.sheet/-cell-indexed-scalar/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br>fun [visit](visit.md)(e1: [CoerceMatrix](../../com.github.jomof.kane.impl.sheet/-coerce-matrix/index.md), e2: [CoerceMatrix](../../com.github.jomof.kane.impl.sheet/-coerce-matrix/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br>fun [visit](visit.md)(e1: [CoerceScalar](../../com.github.jomof.kane.impl.sheet/-coerce-scalar/index.md), e2: [CoerceScalar](../../com.github.jomof.kane.impl.sheet/-coerce-scalar/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br>fun [visit](visit.md)(e1: [GroupBy](../../com.github.jomof.kane.impl.sheet/-group-by/index.md), e2: [GroupBy](../../com.github.jomof.kane.impl.sheet/-group-by/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br>fun [visit](visit.md)(e1: [Sheet](../../com.github.jomof.kane.impl.sheet/-sheet/index.md), e2: [Sheet](../../com.github.jomof.kane.impl.sheet/-sheet/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br><br><br>
| <a name="com.github.jomof.kane.impl.visitor/DifferenceVisitor/visitTerminal/#com.github.jomof.kane.Expr#com.github.jomof.kane.Expr/PointingToDeclaration/"></a>[visitTerminal](visit-terminal.md)| <a name="com.github.jomof.kane.impl.visitor/DifferenceVisitor/visitTerminal/#com.github.jomof.kane.Expr#com.github.jomof.kane.Expr/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [visitTerminal](visit-terminal.md)(e1: [Expr](../../com.github.jomof.kane/-expr/index.md), e2: [Expr](../../com.github.jomof.kane/-expr/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  <br><br><br>
