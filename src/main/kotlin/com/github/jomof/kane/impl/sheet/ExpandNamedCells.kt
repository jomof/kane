package com.github.jomof.kane.impl.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.impl.*

private fun AlgebraicExpr.expandNamedCells(lookup: Cells): AlgebraicExpr {
    fun ScalarExpr.self() = expandNamedCells(lookup) as ScalarExpr
    fun StatisticExpr.self() = expandNamedCells(lookup) as StatisticExpr
    fun MatrixExpr.self() = expandNamedCells(lookup) as MatrixExpr
    return when (this) {
        is NamedScalarVariable -> this
        is DataMatrix -> map { it.self() }
        is RetypeScalar -> copy(scalar = scalar.self())
        is NamedMatrix -> copy(matrix = matrix.self())
        is AlgebraicSummaryScalarScalar -> dup(value = value.self())
        is AlgebraicSummaryMatrixScalar -> dup(value = value.self())
        is AlgebraicBinarySummaryScalarScalarScalar -> dup(left = left.self(), right = right.self())
        is AlgebraicUnaryScalarScalar -> dup(value = value.self())
        is AlgebraicBinaryScalarScalarScalar -> {
            val changed = dup(left = left.self(), right = right.self())
            if (changed !== this) changed.eval()
            else this
        }
        is CoerceScalar -> {
            val result: ScalarExpr = when (value) {
                is ScalarVariable -> constant(value.initial)
                is ScalarExpr -> value.self()
                is SheetRangeExpr -> {
                    if (value.rangeRef !is CellRangeRef) error("")
                    val ref = value.rangeRef.toCoordinate()
                    val result = (lookup[ref] ?: constant(0.0)) as ScalarExpr
                    result.self()
                }
                else -> error("${value.javaClass}")
            }
            result
        }
//        is AbsoluteCellReferenceExpr -> {
//            val ref = "$this"
//            val result = lookup.getValue(ref).expandNamedCells(lookup)
//            result as ScalarExpr
//        }
        is ConstantScalar -> this
        is DiscreteUniformRandomVariable -> this
        else -> error("$javaClass")
    }
}

fun Expr.expandNamedCells(lookup: Cells): Expr {
    return when (this) {
        is AlgebraicExpr -> expandNamedCells(lookup)
        else -> error("$javaClass")
    }
}