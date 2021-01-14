package com.github.jomof.kane.rigueur

import com.github.jomof.kane.rigueur.functions.AlgebraicBinaryScalar

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
        is CoerceScalar<*> -> value.visit(f)
        is ParentExpr<*> -> children.forEach { it.visit(f) }
        else -> error("$javaClass")
    }
}