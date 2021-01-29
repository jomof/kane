package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.ComputableIndex.MoveableIndex
import com.github.jomof.kane.functions.*
import com.github.jomof.kane.visitor.RewritingVisitor

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
            is RectangleRange -> setOf(range)
            is CellRange -> setOf()
            else -> error("${range.javaClass}")
        }
        is AlgebraicBinaryScalarStatistic,
        is AlgebraicUnaryScalarStatistic,
        is AlgebraicUnaryRangeStatistic,
        is AlgebraicBinaryRangeStatistic,
        is Tiling<*>,
        is ValueExpr<*>,
        is ConstantScalar,
        is DiscreteUniformRandomVariable -> setOf()
        else ->
            error("$javaClass")
    }
}

private fun Expr.replaceColumnWithCell(row: Int): Expr {
    return object : RewritingVisitor() {
        override fun SheetRangeExpr.rewrite(): SheetRangeExpr = when (range) {
            is ColumnRange -> copy(range = CellRange(column = range.first, MoveableIndex(row)))
            is CellRange -> this
            else -> error("${range.javaClass}")
        }
    }.rewrite(this)
}

fun SheetBuilderImpl.scalarizeRanges(cells: Map<String, Expr>): Map<String, Expr> {
    val result = cells.toMutableMap()
    var done = false
    while (!done) {
        done = true
        val cellsWithRanges = result
            .map { (name, expr) -> name to expr.ranges() }
            .filter { it.second.isNotEmpty() }
            .map { it.first }
            .reversed()
        val cellsCoordinates = result.map { it.key }.filter { looksLikeCellName(it) }.map { cellNameToCoordinate(it) }
        val columns = cellsCoordinates.map { it.column }.toSet()
        val rows = cellsCoordinates.map { it.row }.toSet().sorted()
        for (name in cellsWithRanges) {
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
