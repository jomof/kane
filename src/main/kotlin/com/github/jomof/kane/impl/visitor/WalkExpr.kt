package com.github.jomof.kane.impl.visitor

import com.github.jomof.kane.*
import com.github.jomof.kane.impl.*
import com.github.jomof.kane.impl.functions.*
import com.github.jomof.kane.impl.sheet.*

internal fun Expr.visit(f: (expr: Expr) -> Unit) {
    f(this)
    when (this) {
        is Slot,
        is NamedMatrixVariable,
        is MatrixVariableElement,
        is ScalarVariable,
        is ConstantScalar,
        is ValueExpr<*>,
        is NamedValueExpr<*>,
        is SheetRangeExpr,
        is DiscreteUniformRandomVariable,
        is NamedScalarVariable -> {
        }
        is NamedSheetRangeExpr -> range.visit(f)
        is AlgebraicBinaryScalarScalarScalar -> {
            left.visit(f)
            right.visit(f)
        }
        is AlgebraicBinaryMatrixScalarMatrix -> {
            left.visit(f)
            right.visit(f)
        }
        is AlgebraicBinaryMatrixMatrixMatrix -> {
            left.visit(f)
            right.visit(f)
        }
        is AlgebraicBinaryScalarMatrixMatrix -> {
            left.visit(f)
            right.visit(f)
        }
        is AlgebraicBinaryScalarStatistic -> {
            left.visit(f)
            right.visit(f)
        }
        is AlgebraicBinaryMatrixScalarStatistic -> {
            left.visit(f)
            right.visit(f)
        }
        is AlgebraicUnaryScalarStatistic -> value.visit(f)
        is AlgebraicUnaryScalarScalar -> value.visit(f)
        is AlgebraicUnaryMatrixMatrix -> value.visit(f)
        is CoerceScalar -> value.visit(f)
        is CoerceMatrix -> value.visit(f)
        is RetypeScalar -> scalar.visit(f)
        is CellIndexedScalar -> expr.visit(f)
        is RetypeMatrix -> matrix.visit(f)
        is NamedScalar -> scalar.visit(f)
        is NamedScalarAssign -> right.visit(f)
        is NamedMatrixAssign -> right.visit(f)
        is NamedMatrix -> matrix.visit(f)
        is GroupBy -> sheet.visit(f)
        is DataMatrix -> elements.forEach { it.visit(f) }
        is AlgebraicDeferredDataMatrix -> {
            left.visit(f)
            right.visit(f)
            data.visit(f)
        }
        is Tableau -> children.forEach { it.visit(f) }
        is NamedTiling<*> -> {
        }
        is Tiling<*> -> {
        }
        is Sheet -> cells.forEach { (_, expr) -> expr.visit(f) }
        else ->
            error("$javaClass")
    }
}

