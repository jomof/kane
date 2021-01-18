package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.functions.*


private fun AlgebraicExpr.replaceRelativeCellReferences(
    coordinate : Coordinate
) : AlgebraicExpr {
    fun MatrixExpr.self(coordinate : Coordinate) =
        replaceRelativeCellReferences(coordinate) as MatrixExpr
    fun ScalarExpr.self(coordinate : Coordinate) =
        replaceRelativeCellReferences(coordinate) as ScalarExpr
    return when(this) {
        is NamedScalar -> {
            val upper = name.toUpperCase()
            val result : AlgebraicExpr = if (looksLikeCellName(upper)) {
                val new = cellNameToCoordinate(upper)
                copy(scalar = scalar.self(new.reduceToFixed()))
            } else copy(scalar = scalar.self(coordinate))
            result
        }
        is NamedMatrix -> {
            val new = cellNameToCoordinate(name.toUpperCase())
            copy(matrix = matrix.self(new.reduceToFixed()))
        }
        is AlgebraicUnaryScalar -> copy(value = value.self(coordinate))
        is AlgebraicUnaryMatrix -> copy(value = value.self(coordinate))
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
        is ConstantScalar -> this
        is CoerceScalar -> {
            val result : AlgebraicExpr = when (value) {
                //is AbsoluteCellReferenceExpr -> this
                is ComputableCellReference -> {
                    val fixed = computeAbsoluteCoordinate(coordinate, value.coordinate)
                    copy(value = value.copy(fixed))
                }
                is NamedComputableCellReference -> {
                    val fixed = computeAbsoluteCoordinate(coordinate, value.coordinate)
                    copy(value = ComputableCellReference(coordinate = fixed))
                }
                else -> error("${value.javaClass}")
            }
            result
        }
        is DataMatrix -> map { it.self(coordinate)}
        else -> error("$javaClass")
    }
}

private fun Expr.replaceRelativeCellReferences() : Expr {
    return when(this) {
        is NamedScalar -> {
            val upper = name.toUpperCase()
            if (looksLikeCellName(upper)) {
                val coordinate = cellNameToCoordinate(name.toUpperCase())
                replaceRelativeCellReferences(coordinate.reduceToFixed())
            } else this
        }
        is NamedValueExpr<*> -> this
        is NamedComputableCellReference -> copy(coordinate = computeAbsoluteCoordinate(Coordinate(0,0), coordinate))
        is NamedTiling<*> -> this
        is NamedMatrix -> this
        else -> error("$javaClass")
    }
}

fun NamedExpr.replaceRelativeCellReferences() : NamedExpr =
    (this as Expr).replaceRelativeCellReferences() as NamedExpr