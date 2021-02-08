package com.github.jomof.kane.impl.sheet

import com.github.jomof.kane.Expr
import com.github.jomof.kane.MatrixExpr
import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.functions.STACK
import com.github.jomof.kane.impl.*
import com.github.jomof.kane.impl.ComputableIndex.*
import com.github.jomof.kane.impl.functions.*
import com.github.jomof.kane.impl.visitor.RewritingVisitor

fun extractScalarizedMatrixElement(
    matrix: MatrixExpr,
    coordinate: Coordinate,
    namedColumns: Map<String, Int>
) : ScalarExpr {
    return when(matrix) {
        is AlgebraicBinaryMatrixScalar -> {
            val left = extractScalarizedMatrixElement(matrix.left, coordinate, namedColumns)
            AlgebraicBinaryScalar(matrix.op, left, matrix.right)
        }
        is AlgebraicBinaryScalarMatrix -> {
            val right = extractScalarizedMatrixElement(matrix.right, coordinate, namedColumns)
            AlgebraicBinaryScalar(matrix.op, matrix.left, right)
        }
        is AlgebraicBinaryMatrix -> {
            val left = extractScalarizedMatrixElement(matrix.left, coordinate, namedColumns)
            val right = extractScalarizedMatrixElement(matrix.right, coordinate, namedColumns)
            AlgebraicBinaryScalar(matrix.op, left, right)
        }
        is AlgebraicUnaryMatrix -> {
            val value = extractScalarizedMatrixElement(matrix.value, coordinate, namedColumns)
            AlgebraicUnaryScalar(matrix.op, value)
        }
        is NamedMatrix -> {
            when (matrix.name) {
                is Coordinate -> {
                    val offsetCoordinate = matrix.name + coordinate
                    val unnamed = matrix[coordinate]
                    NamedScalar(name = offsetCoordinate, unnamed)
                }
                is String -> {
                    val column = namedColumns.getValue(matrix.name)
                    val offsetCoordinate = coordinate.copy(column = column + coordinate.column)
                    val unnamed = matrix[coordinate]
                    NamedScalar(name = offsetCoordinate, unnamed)
                }
                else -> error("")
            }
        }
        is AlgebraicDeferredDataMatrix -> when (matrix.op) {
            STACK -> {
                val left = when (matrix.left) {
                    is MatrixExpr -> matrix.left
                    is ScalarExpr -> repeated(matrix.columns, 1, matrix.left)
                    else -> error("")

                }
                val right = when (matrix.right) {
                    is MatrixExpr -> matrix.right
                    is ScalarExpr -> repeated(matrix.columns, 1, matrix.right)
                    else -> error("")

                }
                val result =
                    if (coordinate.row < left.rows)
                        extractScalarizedMatrixElement(left, coordinate, namedColumns)
                    else
                        extractScalarizedMatrixElement(
                            right,
                            coordinate.copy(row = coordinate.row - left.rows),
                            namedColumns
                        )
                result
            }
            else -> error("")
        }
        is RetypeMatrix -> {
            val value = extractScalarizedMatrixElement(matrix.matrix, coordinate, namedColumns)
            RetypeScalar(value, matrix.type)
        }
        is DataMatrix -> matrix[coordinate]
        is CoerceMatrix -> matrix.getMatrixElement(coordinate.column, coordinate.row)
        else ->
            error("${matrix.javaClass}")
    }
}


fun Expr.slideScalarizedCellsRewritingVisitor(upperLeft: Coordinate, offset: Coordinate): Expr {
    val currentCell = upperLeft + offset
    return object : RewritingVisitor() {
        override fun rewrite(coerce: CoerceScalar): Expr {
            val expr = coerce.value
            if (expr !is SheetRangeExpr) return super.rewrite(coerce)
            val result: SheetRangeExpr = with(expr) {
                when (rangeRef) {
                    is CellRangeRef ->
                        when {
                            rangeRef.column is MoveableIndex && rangeRef.row is FixedIndex -> {
                                copy(
                                    rangeRef = rangeRef.copy(
                                        column = MoveableIndex(rangeRef.column.index + offset.column),
                                        row = MoveableIndex(rangeRef.row.index)
                                    )
                                )
                            }
                            rangeRef.column is FixedIndex && rangeRef.row is MoveableIndex -> {
                                copy(
                                    rangeRef = rangeRef.copy(
                                        column = MoveableIndex(rangeRef.column.index),
                                        row = MoveableIndex(rangeRef.row.index + offset.row - upperLeft.row + 1)
                                    )
                                )
                            }
                            rangeRef.column is RelativeIndex && rangeRef.row is RelativeIndex -> {
                                copy(
                                    rangeRef = rangeRef.copy(
                                        column = MoveableIndex(currentCell.column + rangeRef.column.index),
                                        row = MoveableIndex(currentCell.row + rangeRef.row.index)
                                    )
                                )
                            }
                            rangeRef.column is MoveableIndex && rangeRef.row is RelativeIndex -> {
                                copy(
                                    rangeRef = rangeRef.copy(
                                        column = MoveableIndex(rangeRef.column.index + offset.column),
                                        row = MoveableIndex(currentCell.row + rangeRef.row.index)
                                    )
                                )
                            }
                            rangeRef.column is MoveableIndex && rangeRef.row is MoveableIndex -> this
                            else ->
                                TODO("$rangeRef")
                        }
                    is ColumnRangeRef ->
                        when {
                            rangeRef.first is MoveableIndex && rangeRef.second is MoveableIndex -> {
                                copy(
                                    rangeRef = CellRangeRef(
                                        column = MoveableIndex(rangeRef.first.index + offset.column),
                                        row = MoveableIndex(currentCell.row)
                                    )
                                )
                            }
                            else ->
                                TODO("$left $right")
                        }
                    else ->
                        TODO(rangeRef.javaClass.toString())
                }
            }
            return CoerceScalar(result)
        }
    }.rewrite(this)
}