package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.ComputableIndex.MoveableIndex
import com.github.jomof.kane.functions.*

private fun Expr.ranges(): Set<SheetRange> {
    return when (this) {
        is DataMatrix -> {
            val set = mutableSetOf<SheetRange>()
            elements.forEach { expr ->
                set += expr.ranges()
            }
            set
        }
        is NamedMatrix -> matrix.ranges()
        is AlgebraicUnaryMatrix -> value.ranges()
        is AlgebraicUnaryMatrixScalar -> value.ranges()
        is AlgebraicUnaryScalar -> value.ranges()
        is NamedScalar -> scalar.ranges()
        is AlgebraicBinaryMatrix -> left.ranges() + right.ranges()
        is AlgebraicBinaryScalarMatrix -> left.ranges() + right.ranges()
        is AlgebraicBinaryMatrixScalar -> left.ranges() + right.ranges()
        is AlgebraicBinaryScalar -> left.ranges() + right.ranges()
        is CoerceScalar -> value.ranges()
        is NamedSheetRangeExpr -> range.ranges()
        is SheetRangeExpr -> when (range) {
            is ColumnRange -> setOf(range)
            is CellRange -> setOf()
            else -> error("${range.javaClass}")
        }
        is AlgebraicBinaryScalarStatistic,
        is AlgebraicUnaryScalarStatistic,
        is AlgebraicUnaryRangeStatistic,
        is ValueExpr<*>,
        is ConstantScalar,
        is DiscreteUniformRandomVariable -> setOf()
        else ->
            error("$javaClass")
    }
}

private fun Expr.replaceColumnWithCell(row: Int): Expr {
    fun ScalarExpr.self() = replaceColumnWithCell(row) as ScalarExpr
    return when (this) {
        is AlgebraicUnaryScalar -> copy(value = value.self())
        is NamedScalar -> copy(scalar = scalar.self())
        is AlgebraicBinaryScalar -> copy(left = left.self(), right = right.self())
        is CoerceScalar -> copy(value = value.replaceColumnWithCell(row))
        is NamedSheetRangeExpr -> copy(range = range.replaceColumnWithCell(row) as SheetRangeExpr)
        is SheetRangeExpr -> when (range) {
            is ColumnRange -> copy(range = CellRange(column = range.first, MoveableIndex(row)))
            is CellRange -> this
            else -> error("${range.javaClass}")
        }
        is ValueExpr<*>,
        is ConstantScalar,
        is DiscreteUniformRandomVariable -> this
        else ->
            error("$javaClass")
    }
}

private fun Expr.replaceRangesWithCells(cells: Map<String, Expr>): Expr {
    fun ScalarExpr.self() = replaceRangesWithCells(cells) as ScalarExpr
    fun MatrixExpr.self() = replaceRangesWithCells(cells) as MatrixExpr
    return when (this) {
        is AlgebraicUnaryScalar -> copy(value = value.self())
        is NamedScalar -> copy(scalar = scalar.self())
        is NamedMatrix -> copy(matrix = matrix.self())
        is AlgebraicBinaryScalar -> copy(left = left.self(), right = right.self())
        is CoerceScalar -> copy(value = value.replaceRangesWithCells(cells))
        is NamedSheetRangeExpr -> copy(range = range.replaceRangesWithCells(cells) as SheetRangeExpr)
        is SheetRangeExpr ->
            if (range !is CellRange) {
                //   error("$this:${range.javaClass}")
                this
            } else this
        is ValueExpr<*>,
        is ConstantScalar,
        is DiscreteUniformRandomVariable -> this
        is DataMatrix -> map { it.self() }
        is AlgebraicUnaryMatrix -> copy(value = value.self())
        is AlgebraicUnaryMatrixScalar -> copy(value = value.self())
        is AlgebraicUnaryScalarStatistic -> copy(value = value.self())
        is AlgebraicUnaryRangeStatistic -> this
        is AlgebraicBinaryMatrix -> copy(left = left.self(), right = right.self())
        is AlgebraicBinaryScalarStatistic -> copy(left = left.self(), right = right.self())
        is AlgebraicBinaryScalarMatrix -> copy(left = left.self(), right = right.self())
        is AlgebraicBinaryMatrixScalar -> copy(left = left.self(), right = right.self())
        else ->
            error("$javaClass")
    }
}

fun SheetBuilder.scalarizeRanges(cells: Map<String, Expr>): Map<String, Expr> {

    val result = cells.toMutableMap()
    var done = false
    while (!done) {
        done = true
        val cellRanges =
            result.map { (name, expr) -> name to expr.ranges() }.filter { it.second.isNotEmpty() }.reversed()
        val columnRanges = cellRanges.filter { (_, expr) -> expr.all { it is ColumnRange } }
        val cellsCoordinates = result.map { it.key }.filter { looksLikeCellName(it) }.map { cellNameToCoordinate(it) }
        val columns = cellsCoordinates.map { it.column }.toSet()
        val rows = cellsCoordinates.map { it.row }.toSet().sorted()
        for ((name, columnRanges) in cellRanges) {
            val expr = result.getValue(name)
            if (!looksLikeCellName(name)) {
                if (columnDescriptors.any { it.value.name == name }) continue
                // It has column ranges but doesn't have a cell-like name, let's allocate a column for it.
                val existingColumns = (columnDescriptors.keys + columns).sorted()
                done = false
                for (next in 0 until Int.MAX_VALUE) {
                    if (!existingColumns.contains(next)) {
                        columnDescriptors[next] = ColumnDescriptor(name, null)
                        val allocatedColumnBase = coordinateToCellName(next, 0)
                        result[allocatedColumnBase] = expr
                        result.remove(name)
                        break
                    }
                }
                continue
            }
            val base = cellNameToCoordinate(name)

            for (row in rows) {
                if (row < base.row) continue
                val columnReplaced = expr.replaceColumnWithCell(row)
                val newCell = CellRange.moveable(base.column, row)
                val cellName = "$newCell"
                if (cellName == name || !result.containsKey(cellName)) {
                    result[cellName] = columnReplaced
                    done = false
                }
            }
        }
    }
    return result
}
