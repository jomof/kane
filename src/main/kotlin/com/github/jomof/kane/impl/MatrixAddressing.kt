package com.github.jomof.kane.impl

import com.github.jomof.kane.Expr
import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.impl.functions.*

internal fun NamedMatrixVariable.getMatrixElement(column: Int, row: Int): MatrixVariableElement {
    fun coordinateToIndex(column: Int, row: Int) = row * columns + column
    assert(column >= 0)
    assert(column < columns) { "$column greater than columns $columns of matrix $name" }
    assert(row >= 0)
    assert(row < rows) { "$row greater than columns $rows of matrix $name" }
    return MatrixVariableElement(column, row, this, initial[coordinateToIndex(column, row)])
}

internal fun DataMatrix.getMatrixElement(column: Int, row: Int): ScalarExpr {
    fun coordinateToIndex(column: Int, row: Int) = row * columns + column
    assert(row >= 0)
    assert(row < rows) {
        "row $row was not less that $rows"
    }
    assert(column >= 0)
    assert(column < columns)
    return elements[coordinateToIndex(column, row)]
}

internal fun AlgebraicUnaryMatrix.getMatrixElement(column: Int, row: Int) =
    AlgebraicUnaryScalar(op, value.getMatrixElement(column, row))

internal fun AlgebraicBinaryMatrixScalar.getMatrixElement(column: Int, row: Int) =
    AlgebraicBinaryScalar(op, left.getMatrixElement(column, row), right)

internal fun RetypeMatrix.getMatrixElement(column: Int, row: Int) =
    RetypeScalar(matrix.getMatrixElement(column, row), type)

internal fun NamedMatrix.getMatrixElement(column: Int, row: Int) =
    matrix.getMatrixElement(column, row)

internal fun AlgebraicDeferredDataMatrix.getMatrixElement(column: Int, row: Int) =
    data.getMatrixElement(column, row)

internal fun AlgebraicBinaryScalarMatrix.getMatrixElement(column: Int, row: Int) =
    AlgebraicBinaryScalar(op, left, right.getMatrixElement(column, row))

internal fun AlgebraicBinaryMatrix.getMatrixElement(column: Int, row: Int) =
    AlgebraicBinaryScalar(op, left.getMatrixElement(column, row), right.getMatrixElement(column, row))

internal fun Expr.getMatrixElement(column: Int, row: Int): ScalarExpr {
    return when (this) {
        is NamedMatrixVariable -> getMatrixElement(column, row)
        is NamedMatrix -> getMatrixElement(column, row)
        is DataMatrix -> getMatrixElement(column, row)
        is AlgebraicUnaryMatrix -> getMatrixElement(column, row)
        is AlgebraicBinaryMatrixScalar -> getMatrixElement(column, row)
        is AlgebraicBinaryScalarMatrix -> getMatrixElement(column, row)
        is RetypeMatrix -> getMatrixElement(column, row)
        is AlgebraicDeferredDataMatrix -> getMatrixElement(column, row)
        is AlgebraicBinaryMatrix -> getMatrixElement(column, row)
        else -> error("$javaClass")
    }
}
