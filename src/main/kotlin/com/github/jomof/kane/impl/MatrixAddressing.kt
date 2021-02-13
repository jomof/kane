package com.github.jomof.kane.impl

import com.github.jomof.kane.*
import com.github.jomof.kane.MatrixExpr
import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.impl.ComputableIndex.MoveableIndex
import com.github.jomof.kane.impl.functions.*
import com.github.jomof.kane.impl.sheet.CoerceMatrix
import com.github.jomof.kane.impl.sheet.CoerceScalar
import com.github.jomof.kane.impl.sheet.Sheet
import com.github.jomof.kane.impl.sheet.SheetRangeExpr
import kotlin.math.min

internal fun NamedMatrixVariable.getMatrixElement(column: Int, row: Int): MatrixVariableElement {
    fun coordinateToIndex(column: Int, row: Int) = row * columns + column
    return MatrixVariableElement(column, row, this, initial[coordinateToIndex(column, row)])
}

internal fun DataMatrix.getMatrixElement(column: Int, row: Int): ScalarExpr {
    fun coordinateToIndex(column: Int, row: Int) = row * columns + column
    return elements[coordinateToIndex(column, row)]
}

internal fun AlgebraicUnaryMatrix.getMatrixElement(column: Int, row: Int) =
    AlgebraicUnaryScalarScalar(op, value.getMatrixElement(column, row))

internal fun AlgebraicBinaryMatrixScalarMatrix.getMatrixElement(column: Int, row: Int) =
    AlgebraicBinaryScalarScalarScalar(
        op as IAlgebraicBinaryScalarScalarScalarFunction,
        left.getMatrixElement(column, row),
        right
    )

internal fun RetypeMatrix.getMatrixElement(column: Int, row: Int) =
    RetypeScalar(matrix.getMatrixElement(column, row), type)

internal fun NamedMatrix.getMatrixElement(column: Int, row: Int) =
    matrix.getMatrixElement(column, row)

internal fun AlgebraicDeferredDataMatrix.getMatrixElement(column: Int, row: Int) =
    data.getMatrixElement(column, row)

internal fun AlgebraicBinaryScalarMatrixMatrix.getMatrixElement(column: Int, row: Int) =
    AlgebraicBinaryScalarScalarScalar(
        op as IAlgebraicBinaryScalarScalarScalarFunction,
        left,
        right.getMatrixElement(column, row)
    )

internal fun AlgebraicBinaryMatrix.getMatrixElement(column: Int, row: Int) =
    AlgebraicBinaryScalarScalarScalar(op, left.getMatrixElement(column, row), right.getMatrixElement(column, row))

internal fun CoerceMatrix.getMatrixElement(column: Int, row: Int): ScalarExpr {
    return when (value) {
        is SheetRangeExpr -> when (val ref = value.rangeRef) {
            is ColumnRangeRef -> when {
                ref.first is MoveableIndex && ref.second is MoveableIndex -> {
                    val coordinate = coordinate(ref.first.index + column, row)
                    val cell = CoerceScalar(SheetRangeExpr(coordinate.toComputableCoordinate()))
                    cell
                }
                else -> error("$ref")
            }
            is RectangleRangeRef -> {
                val coordinate = coordinate(ref.first.column.index + column, ref.first.row.index + row)
                val cell = CoerceScalar(SheetRangeExpr(coordinate.toComputableCoordinate()))
                cell
            }
            else -> error("${ref.javaClass}")
        }
        is Sheet -> value.cells[coordinate(column, row)] as ScalarExpr
        else -> error("${value.javaClass}")
    }
}

internal fun Expr.getMatrixElement(column: Int, row: Int): ScalarExpr {
    return when (this) {
        is NamedMatrixVariable -> getMatrixElement(column, row)
        is NamedMatrix -> getMatrixElement(column, row)
        is DataMatrix -> getMatrixElement(column, row)
        is AlgebraicUnaryMatrix -> getMatrixElement(column, row)
        is AlgebraicBinaryMatrixScalarMatrix -> getMatrixElement(column, row)
        is AlgebraicBinaryScalarMatrixMatrix -> getMatrixElement(column, row)
        is RetypeMatrix -> getMatrixElement(column, row)
        is AlgebraicDeferredDataMatrix -> getMatrixElement(column, row)
        is AlgebraicBinaryMatrix -> getMatrixElement(column, row)
        is CoerceMatrix -> getMatrixElement(column, row)
        else ->
            error("$javaClass")
    }
}

internal fun MatrixExpr.tryGetDimensions(sheetRowCount: Int? = null): Pair<Int?, Int?> {
    return when (this) {
        is CoerceMatrix -> when (value) {
            is SheetRangeExpr -> when (value.rangeRef) {
                is ColumnRangeRef -> {
                    val ref = value.rangeRef
                    val columns = when {
                        ref.first is MoveableIndex && ref.second is MoveableIndex -> (ref.second.index - ref.first.index) + 1
                        else -> error("${value.rangeRef.javaClass}")
                    }
                    columns to sheetRowCount
                }
                is RectangleRangeRef -> {
                    val ref = value.rangeRef
                    val columns = when {
                        ref.first.column is MoveableIndex && ref.second.column is MoveableIndex -> (ref.second.column.index - ref.first.column.index) + 1
                        else -> error("${value.rangeRef.javaClass}")
                    }
                    val rows = when {
                        ref.first.row is MoveableIndex && ref.second.row is MoveableIndex -> (ref.second.row.index - ref.first.row.index) + 1
                        else -> error("${value.rangeRef.javaClass}")
                    }
                    columns to rows
                }
                else -> error("${value.rangeRef.javaClass}")
            }
            is Sheet -> value.columns to value.rows
            else ->
                error("${value.javaClass}")
        }
        is NamedMatrix -> matrix.tryGetDimensions(sheetRowCount)
        is NamedMatrixVariable -> columns to rows
        is DataMatrix -> columns to rows
        is RetypeMatrix -> matrix.tryGetDimensions(sheetRowCount)
        is AlgebraicUnaryMatrix -> value.tryGetDimensions(sheetRowCount)
        is AlgebraicDeferredDataMatrix -> data.tryGetDimensions(sheetRowCount)
        is AlgebraicBinaryMatrixScalarMatrix -> left.tryGetDimensions(sheetRowCount)
        is AlgebraicBinaryScalarMatrixMatrix -> right.tryGetDimensions(sheetRowCount)
        is AlgebraicBinaryMatrix -> {
            val left = left.tryGetDimensions(sheetRowCount)
            val right = right.tryGetDimensions(sheetRowCount)
            val leftColumns = left.first
            val rightColumns = right.first
            val columns = when {
                leftColumns != null && rightColumns != null -> min(leftColumns, rightColumns)
                leftColumns != null -> leftColumns
                rightColumns != null -> rightColumns
                else -> null
            }
            val leftRows = left.second
            val rightRows = right.second
            val rows = when {
                leftRows != null && rightRows != null -> min(leftRows, rightRows)
                leftRows != null -> leftRows
                rightRows != null -> rightRows
                else -> null
            }
            columns to rows
        }
        else ->
            error("$javaClass")
    }
}


val MatrixExpr.columns get() = tryGetDimensions().first!!
val MatrixExpr.rows
    get() : Int {
        val (_, rows) = tryGetDimensions()
        if (rows == null) {
            tryGetDimensions()
            error("Could not get dimensions for ${this.javaClass}")
        }
        return rows
    }