package com.github.jomof.kane.rigueur

import com.github.jomof.kane.rigueur.functions.*

fun Expr.visit(f: (expr : Expr) -> Unit) {
    f(this)
    when(this) {
        is NamedUntypedAbsoluteCellReference,
        is UntypedRelativeCellReference,
        is Slot<*>,
        is NamedMatrixVariable<*>,
        is MatrixVariableElement<*>,
        is ScalarVariable<*>,
        is ConstantScalar<*>,
        is ValueExpr<*>,
        is NamedValueExpr<*>,
        is AbsoluteCellReferenceExpr,
        is NamedScalarVariable<*> -> {}
        is AlgebraicBinaryScalar<*> -> {
            left.visit(f)
            right.visit(f)
        }
        is AlgebraicBinaryMatrixScalar<*> -> {
            left.visit(f)
            right.visit(f)
        }
        is AlgebraicBinaryMatrix<*> -> {
            left.visit(f)
            right.visit(f)
        }
        is AlgebraicBinaryScalarMatrix<*> -> {
            left.visit(f)
            right.visit(f)
        }
        is AlgebraicUnaryScalar<*> -> value.visit(f)
        is AlgebraicUnaryMatrix<*> -> value.visit(f)
        is AlgebraicUnaryMatrixScalar<*> -> value.visit(f)
        is CoerceScalar<*> -> value.visit(f)
        is NamedScalar<*> -> scalar.visit(f)
        is NamedScalarAssign<*> -> right.visit(f)
        is NamedMatrixAssign<*> -> right.visit(f)
        is NamedMatrix<*> -> matrix.visit(f)
        is DataMatrix<*> -> elements.forEach { it.visit(f) }
        is AlgebraicDeferredDataMatrix<*> -> data.visit(f)
        is Tableau<*> -> children.forEach { it.visit(f) }
//        is ParentExpr<*> ->
//            children.forEach { it.visit(f) }
        else -> error("$javaClass")
    }
}