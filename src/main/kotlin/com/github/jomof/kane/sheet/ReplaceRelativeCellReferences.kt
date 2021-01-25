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
            right = right.self(coordinate))
        is AlgebraicBinaryMatrix -> copy(
            left = left.self(coordinate),
            right = right.self(coordinate))
        is AlgebraicBinaryScalarStatistic -> copy(
            left = left.self(coordinate),
            right = right.self(coordinate))
        is ConstantScalar -> this
        is DiscreteUniformRandomVariable -> this
        is CoerceScalar -> {
            val result : AlgebraicExpr = when (value) {
                is SheetRangeExpr -> {
                    val moveable = computeMoveableCoordinate(coordinate, value.range as ComputableCoordinate)
                    copy(value = value.copy(moveable))
                }
                is NamedSheetRangeExpr -> {
                    when (value.range) {
                        is ComputableCoordinate -> {
                            val moveable = computeMoveableCoordinate(coordinate, value.range)
                            copy(value = SheetRangeExpr(range = moveable))
                        }
                        is ColumnSheetRange -> {
                            val moveable = computeMoveableCoordinate(coordinate, value.range)
                            copy(value = SheetRangeExpr(range = moveable))
                        }
                        else -> error("${value.range.javaClass}")
                    }

                }
                is AlgebraicUnaryRangeStatistic -> this
                else -> error("${value.javaClass}")
            }
            result
        }
        is DataMatrix -> map { it.self(coordinate) }
        is SheetRangeExpr ->
            when (range) {
                is ComputableCoordinate -> copy(range = computeMoveableCoordinate(Coordinate(0, 0), range))
                is ColumnSheetRange -> copy(range = computeMoveableCoordinate(Coordinate(0, 0), range))
                else -> error("${range.javaClass}")
            }
        is ValueExpr<*> -> this
        is AlgebraicUnaryRangeStatistic -> this
        else -> error("$javaClass")
    }
}
//
//fun Expr.replaceRelativeCellReferences() : Expr {
//    return when(this) {
////        is NamedScalar -> {
////            val upper = name.toUpperCase()
////            if (looksLikeCellName(upper)) {
////                val coordinate = cellNameToCoordinate(name.toUpperCase())
////                replaceRelativeCellReferences(coordinate)
////            } else this
////        }
////        is NamedValueExpr<*> -> this
////        is ComputableCellReference ->
////            when(range) {
////                is ComputableCoordinate -> copy(range = computeMoveableCoordinate(Coordinate(0,0), range))
////                is ColumnSheetRange -> copy(range = computeMoveableCoordinate(Coordinate(0,0), range))
////                else -> error("${range.javaClass}")
////            }
//
//        is AlgebraicBinaryMatrixScalar -> replaceRelativeCellReferences(Coordinate(0,0))
//        else -> error("$javaClass")
//    }
//}