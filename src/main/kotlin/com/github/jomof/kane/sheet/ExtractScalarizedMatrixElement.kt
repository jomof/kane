package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.functions.*


fun extractScalarizedMatrixElement(
    matrix : MatrixExpr,
    coordinate : Coordinate
) : ScalarExpr {
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
            if(looksLikeCellName(matrix.name)) {
                val baseCoordinate = cellNameToCoordinate(matrix.name).reduceToFixed()
                val offsetCoordinate = baseCoordinate + coordinate
                val offsetCellName = coordinateToCellName(offsetCoordinate)
                val unnamed = matrix[coordinate]
                NamedScalar(name = offsetCellName, unnamed)
            } else matrix[coordinate]
        }
        is AlgebraicDeferredDataMatrix -> extractScalarizedMatrixElement(matrix.data, coordinate)
        is DataMatrix -> matrix[coordinate]
        else ->
            error("${matrix.javaClass}")
    }
}