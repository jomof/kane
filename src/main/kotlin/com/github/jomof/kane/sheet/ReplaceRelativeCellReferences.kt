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
            val new = cellNameToCoordinate(name.toUpperCase())
            copy(scalar = scalar.self(new))
        }
        is NamedMatrix -> {
            val new = cellNameToCoordinate(name.toUpperCase())
            copy(matrix = matrix.self(new))
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
        is ConstantScalar -> this
        is CoerceScalar -> {
            val result : AlgebraicExpr = when (value) {
                is UntypedRelativeCellReference -> {
                    copy(value = AbsoluteCellReferenceExpr(coordinate + value.offset))
                }
                is AbsoluteCellReferenceExpr -> this
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
            val coordinate = cellNameToCoordinate(name.toUpperCase())
            replaceRelativeCellReferences(coordinate)
        }
        is NamedValueExpr<*> -> this
        is NamedUntypedAbsoluteCellReference -> this
        is NamedTiling<*> -> this
        else -> error("$javaClass")
    }
}

fun NamedExpr.replaceRelativeCellReferences() : NamedExpr =
    (this as Expr).replaceRelativeCellReferences() as NamedExpr