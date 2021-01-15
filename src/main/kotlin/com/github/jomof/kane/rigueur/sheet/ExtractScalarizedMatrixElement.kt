package com.github.jomof.kane.rigueur.sheet

import com.github.jomof.kane.rigueur.*
import com.github.jomof.kane.rigueur.functions.*


fun <E:Number> extractScalarizedMatrixElement(
    matrix : MatrixExpr<E>,
    coordinate : Coordinate
) : ScalarExpr<E> {
    return when(matrix) {
        is AlgebraicBinaryMatrixScalar -> {
            val left = extractScalarizedMatrixElement(matrix.left, coordinate)
            AlgebraicBinaryScalar(matrix.op, left, matrix.right)
        }
        is AlgebraicBinaryScalarMatrix -> {
            val right = extractScalarizedMatrixElement(matrix.right, coordinate)
            AlgebraicBinaryScalar(matrix.op, matrix.left, right)
        }
        is AlgebraicBinaryMatrix -> {
            val left = extractScalarizedMatrixElement(matrix.left, coordinate)
            val right = extractScalarizedMatrixElement(matrix.right, coordinate)
            AlgebraicBinaryScalar(matrix.op, left, right)
        }
        is AlgebraicUnaryMatrix -> {
            val value = extractScalarizedMatrixElement(matrix.value, coordinate)
            AlgebraicUnaryScalar(matrix.op, value)
        }
        is NamedMatrix -> {
            assert(looksLikeCellName(matrix.name))
            val baseCoordinate = cellNameToCoordinate(matrix.name)
            val offsetCoordinate = baseCoordinate + coordinate
            val offsetCellName = coordinateToCellName(offsetCoordinate)
            val unnamed = matrix[coordinate]
            NamedScalar(name = offsetCellName, unnamed)
        }
        is DataMatrix -> matrix[coordinate]
        else ->
            error("${matrix.javaClass}")
    }
}