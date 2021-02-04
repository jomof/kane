package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.ComputableIndex.*
import com.github.jomof.kane.functions.*
import com.github.jomof.kane.visitor.RewritingVisitor


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
            AlgebraicUnaryScalar(matrix.op, value, value.type)
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

        is DataMatrix -> matrix[coordinate]
        else ->
            error("${matrix.javaClass}")
    }
}


fun Expr.slideScalarizedCellsRewritingVisitor(upperLeft: Coordinate, offset: Coordinate): Expr {
    val currentCell = upperLeft + offset
    return object : RewritingVisitor() {
        override fun rewrite(expr: SheetRangeExpr): Expr {
            val result: SheetRangeExpr = with(expr) {
                when (range) {
                    is CellRange ->
                        when {
                            range.column is MoveableIndex && range.row is FixedIndex -> {
                                copy(
                                    range = range.copy(
                                        column = MoveableIndex(range.column.index + offset.column),
                                        row = MoveableIndex(range.row.index)
                                    )
                                )
                            }
                            range.column is FixedIndex && range.row is MoveableIndex -> {
                                copy(
                                    range = range.copy(
                                        column = MoveableIndex(range.column.index),
                                        row = MoveableIndex(range.row.index + offset.row - upperLeft.row + 1)
                                    )
                                )
                            }
                            range.column is RelativeIndex && range.row is RelativeIndex -> {
                                copy(
                                    range = range.copy(
                                        column = MoveableIndex(currentCell.column + range.column.index),
                                        row = MoveableIndex(currentCell.row + range.row.index)
                                    )
                                )
                            }
                            range.column is MoveableIndex && range.row is RelativeIndex -> {
                                copy(
                                    range = range.copy(
                                        column = MoveableIndex(range.column.index + offset.column),
                                        row = MoveableIndex(currentCell.row + range.row.index)
                                    )
                                )
                            }
                            else ->
                                TODO("$range")
                        }
                    is ColumnRange ->
                        when {
                            range.first is MoveableIndex && range.second is MoveableIndex -> {
                                copy(
                                    range = CellRange(
                                        column = MoveableIndex(range.first.index + offset.column),
                                        row = MoveableIndex(currentCell.row)
                                    )
                                )
                            }
                            else ->
                                TODO("$left $right")
                        }
                    else ->
                        TODO(range.javaClass.toString())
                }
            }
            return result
        }
    }.rewrite(this)
}