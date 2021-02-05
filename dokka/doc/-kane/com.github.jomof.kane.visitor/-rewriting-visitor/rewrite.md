//[Kane](../../index.md)/[com.github.jomof.kane.visitor](../index.md)/[RewritingVisitor](index.md)/[rewrite](rewrite.md)



# rewrite  
[jvm]  
Content  
open fun [rewrite](rewrite.md)(expr: [CoerceScalar](../../com.github.jomof.kane.sheet/-coerce-scalar/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [RetypeScalar](../../com.github.jomof.kane/-retype-scalar/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [RetypeMatrix](../../com.github.jomof.kane/-retype-matrix/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [ScalarStatistic](../../com.github.jomof.kane/-scalar-statistic/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [ConstantScalar](../../com.github.jomof.kane/-constant-scalar/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [DiscreteUniformRandomVariable](../../com.github.jomof.kane/-discrete-uniform-random-variable/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [SheetRangeExpr](../../com.github.jomof.kane.sheet/-sheet-range-expr/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [SheetBuilderRange](../../com.github.jomof.kane.sheet/-sheet-builder-range/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [Tiling](../../com.github.jomof.kane/-tiling/index.md)<*>): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [ValueExpr](../../com.github.jomof.kane/-value-expr/index.md)<*>): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [NamedScalarVariable](../../com.github.jomof.kane/-named-scalar-variable/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [NamedMatrixVariable](../../com.github.jomof.kane/-named-matrix-variable/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [ScalarVariable](../../com.github.jomof.kane/-scalar-variable/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [MatrixVariableElement](../../com.github.jomof.kane/-matrix-variable-element/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [NamedScalarAssign](../../com.github.jomof.kane/-named-scalar-assign/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [NamedMatrixAssign](../../com.github.jomof.kane/-named-matrix-assign/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [NamedScalar](../../com.github.jomof.kane/-named-scalar/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [NamedSheetRangeExpr](../../com.github.jomof.kane.sheet/-named-sheet-range-expr/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [NamedMatrix](../../com.github.jomof.kane/-named-matrix/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [AlgebraicDeferredDataMatrix](../../com.github.jomof.kane.functions/-algebraic-deferred-data-matrix/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [DataMatrix](../../com.github.jomof.kane/-data-matrix/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [ScalarListExpr](../../com.github.jomof.kane/-scalar-list-expr/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [AlgebraicUnaryScalar](../../com.github.jomof.kane.functions/-algebraic-unary-scalar/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [AlgebraicUnaryScalarStatistic](../../com.github.jomof.kane.functions/-algebraic-unary-scalar-statistic/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [AlgebraicUnaryMatrix](../../com.github.jomof.kane.functions/-algebraic-unary-matrix/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [AlgebraicUnaryMatrixScalar](../../com.github.jomof.kane.functions/-algebraic-unary-matrix-scalar/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [AlgebraicBinaryRangeStatistic](../../com.github.jomof.kane.functions/-algebraic-binary-range-statistic/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [AlgebraicBinaryMatrix](../../com.github.jomof.kane.functions/-algebraic-binary-matrix/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [AlgebraicBinaryMatrixScalar](../../com.github.jomof.kane.functions/-algebraic-binary-matrix-scalar/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [AlgebraicBinaryScalar](../../com.github.jomof.kane.functions/-algebraic-binary-scalar/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [AlgebraicBinaryScalarMatrix](../../com.github.jomof.kane.functions/-algebraic-binary-scalar-matrix/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [AlgebraicBinaryScalarStatistic](../../com.github.jomof.kane.functions/-algebraic-binary-scalar-statistic/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [Sheet](../../com.github.jomof.kane.sheet/-sheet/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [Tableau](../../com.github.jomof.kane/-tableau/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  
open fun [rewrite](rewrite.md)(expr: [Expr](../../com.github.jomof.kane/-expr/index.md)): [Expr](../../com.github.jomof.kane/-expr/index.md)  



