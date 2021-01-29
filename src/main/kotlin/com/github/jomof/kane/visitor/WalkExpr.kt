package com.github.jomof.kane.visitor

import com.github.jomof.kane.*
import com.github.jomof.kane.functions.*
import com.github.jomof.kane.sheet.*

fun Expr.count() : Int {
    var count = 0
    visit { count++ }
    return count
}

fun Expr.visit(f: (expr : Expr) -> Unit) {
    f(this)
    when(this) {
        is Slot,
        is NamedMatrixVariable,
        is MatrixVariableElement,
        is ScalarVariable,
        is ConstantScalar,
        is ValueExpr<*>,
        is NamedValueExpr<*>,
        is SheetRangeExpr,
        is DiscreteUniformRandomVariable,
        is AlgebraicUnaryRangeStatistic,
        is NamedScalarVariable -> {
        }
        is NamedSheetRangeExpr -> range.visit(f)
        is AlgebraicBinaryScalar -> {
            left.visit(f)
            right.visit(f)
        }
        is AlgebraicBinaryMatrixScalar -> {
            left.visit(f)
            right.visit(f)
        }
        is AlgebraicBinaryMatrix -> {
            left.visit(f)
            right.visit(f)
        }
        is AlgebraicBinaryScalarMatrix -> {
            left.visit(f)
            right.visit(f)
        }
        is AlgebraicBinaryScalarStatistic -> {
            left.visit(f)
            right.visit(f)
        }
        is AlgebraicUnaryScalarStatistic -> value.visit(f)
        is AlgebraicBinaryRangeStatistic -> right.visit(f)
        is AlgebraicUnaryScalar -> value.visit(f)
        is AlgebraicUnaryMatrix -> value.visit(f)
        is AlgebraicUnaryMatrixScalar -> value.visit(f)
        is CoerceScalar -> value.visit(f)
        is NamedScalar -> scalar.visit(f)
        is NamedScalarAssign -> right.visit(f)
        is NamedMatrixAssign -> right.visit(f)
        is NamedMatrix -> matrix.visit(f)
        is DataMatrix -> elements.forEach { it.visit(f) }
        is AlgebraicDeferredDataMatrix -> data.visit(f)
        is Tableau -> children.forEach { it.visit(f) }
        is NamedTiling<*> -> {
        }
        is Tiling<*> -> {
        }
        is Sheet -> cells.forEach { (_, expr) -> expr.visit(f) }
        is SheetBuilderRange -> {
        }
        else ->
            error("$javaClass")
    }
}

