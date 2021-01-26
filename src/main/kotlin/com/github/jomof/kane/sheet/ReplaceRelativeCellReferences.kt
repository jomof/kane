package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.functions.*


fun Expr.replaceRelativeCellReferences(coordinate: Coordinate): Expr {
    fun MatrixExpr.self(coordinate: Coordinate) =
        replaceRelativeCellReferences(coordinate) as MatrixExpr

    fun ScalarExpr.self(coordinate: Coordinate) =
        replaceRelativeCellReferences(coordinate) as ScalarExpr
    return when (this) {
        is NamedScalar -> {
            val upper = name.toUpperCase()
            val result: AlgebraicExpr = if (looksLikeCellName(upper)) {
                val new = cellNameToCoordinate(upper)
                copy(scalar = scalar.self(new))
            } else copy(scalar = scalar.self(coordinate))
            result
        }
        is NamedMatrix -> {
            val new = cellNameToCoordinate(name.toUpperCase())
            copy(matrix = matrix.self(new))
        }
        is AlgebraicUnaryScalar -> copy(value = value.self(coordinate))
        is AlgebraicUnaryMatrix -> copy(value = value.self(coordinate))
        is AlgebraicUnaryScalarStatistic -> copy(value = value.self(coordinate))
        is AlgebraicUnaryMatrixScalar -> copy(value = value.self(coordinate))
        is AlgebraicBinaryScalarMatrix -> copy(
            left = left.self(coordinate),
            right = right.self(coordinate))
        is AlgebraicBinaryScalar -> copy(
            left = left.self(coordinate),
            right = right.self(coordinate))
        is AlgebraicBinaryMatrixScalar -> copy(
            left = left.self(coordinate),
            right = right.self(coordinate)
        )
        is AlgebraicBinaryMatrix -> copy(
            left = left.self(coordinate),
            right = right.self(coordinate)
        )
        is AlgebraicBinaryScalarStatistic -> copy(
            left = left.self(coordinate),
            right = right.self(coordinate)
        )
        is ConstantScalar -> this
        is DiscreteUniformRandomVariable -> this
        is CoerceScalar -> copy(value = value.replaceRelativeCellReferences(coordinate))
        is SheetRangeExpr -> copy(range = range.rebase(coordinate))
        is SheetBuilder.SheetBuilderRange -> SheetRangeExpr(range = range.rebase(coordinate))
        is NamedSheetRangeExpr -> copy(range = range.rebase(coordinate))
        is AlgebraicUnaryRangeStatistic -> copy(range = range.rebase(coordinate))
        is DataMatrix -> map { it.self(coordinate) }
        is ValueExpr<*> -> this
        else -> error("$javaClass")
    }
}
