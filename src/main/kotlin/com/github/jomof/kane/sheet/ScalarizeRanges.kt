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
        is NamedUntypedScalar -> expr.ranges()
        is AlgebraicUnaryMatrix -> value.ranges()
        is AlgebraicDeferredDataMatrix -> {
            left.ranges()
            right.ranges()
            data.ranges()
        }
        is AlgebraicUnaryMatrixScalar -> value.ranges()
        is AlgebraicUnaryScalar -> value.ranges()
        is NamedScalar -> scalar.ranges()
        is AlgebraicBinaryMatrix -> left.ranges() + right.ranges()
        is AlgebraicBinaryScalarMatrix -> left.ranges() + right.ranges()
        is AlgebraicBinaryMatrixScalar -> left.ranges() + right.ranges()
        is AlgebraicBinaryScalar -> left.ranges() + right.ranges()
        is CoerceScalar -> value.ranges()
        is RetypeScalar -> scalar.ranges()
        is RetypeMatrix -> matrix.ranges()
        is NamedSheetRangeExpr -> range.ranges()
        is SheetRangeExpr -> when (range) {
            is ColumnRange -> setOf(range)
            is RectangleRange -> setOf(range)
            is CellRange -> setOf()
            else -> error("${range.javaClass}")
        }
        is AlgebraicBinaryScalarStatistic,
        is AlgebraicUnaryScalarStatistic,
            //is AlgebraicUnaryRangeStatistic,
        is AlgebraicBinaryRangeStatistic,
        is Tiling<*>,
        is ValueExpr<*>,
        is ConstantScalar,
        is NamedScalarVariable,
        is DiscreteUniformRandomVariable -> setOf()
        else ->
            error("$javaClass")
    }
}

private fun Expr.replaceColumnWithCell(row: Int): Expr {
    return object : RewritingVisitor() {
        override fun rewrite(expr: AlgebraicUnaryScalarStatistic) = expr
        override fun rewrite(expr: AlgebraicBinaryRangeStatistic) = expr.copy(right = scalar(expr.right))
        override fun rewrite(expr: SheetRangeExpr) = with(expr) {
            when (range) {
                is ColumnRange -> copy(range = CellRange(column = range.first, MoveableIndex(row)))
                is CellRange -> this
                is RectangleRange -> this
                else -> error("${range.javaClass}")
            }
        }
    }.rewrite(this)
}

fun SheetBuilderImpl.scalarizeRanges(cells: Cells): Cells {
    val result = cells.toMutableMap()
    var done = false
    while (!done) {
        done = true
        val cellsWithRanges = result
            .map { (name, expr) -> name to expr.ranges() }
            .filter { it.second.isNotEmpty() }
            .map { it.first }
            .reversed()
        val cellsCoordinates = result.map { it.key }.filterIsInstance<Coordinate>()
        val columns = cellsCoordinates.map { it.column }.toSet()
        val rows = cellsCoordinates.map { it.row }.toSet().sorted()
        for (name in cellsWithRanges) {
            val expr = result.getValue(name)
            if (name !is Coordinate) {
                if (columnDescriptors.any { it.value.name == name }) continue
                // It has column ranges but doesn't have a cell-like name, let's allocate a column for it.
                val existingColumns = (columnDescriptors.keys + columns).sorted()
                done = false
                for (next in 0 until Int.MAX_VALUE) {
                    if (!existingColumns.contains(next)) {
                        columnDescriptors[next] = ColumnDescriptor(name, null)
                        val allocatedColumnBase = coordinate(next, 0)
                        result[allocatedColumnBase] = expr
                        result.remove(name)
                        break
                    }
                }
                continue
            }

            for (row in rows) {
                if (row < name.row) continue
                val columnReplaced = expr.replaceColumnWithCell(row)
                val newCell = coordinate(name.column, row)
                if (result[newCell] != columnReplaced) {
                    result[newCell] = columnReplaced
                    done = false
                }
            }
        }
    }

    // Remove any cells that have ranges
    val cellsWithRanges = result
        .map { (name, expr) -> name to expr.ranges() }
        .filter { it.second.isNotEmpty() }


    cellsWithRanges.forEach { (name, expr) ->
        result.remove(name)
    }

    return result.toCells()
}

fun SheetBuilderImpl.convertNamedColumnsToColumnRanges(cells: Cells): Cells {
    val converter = object : RewritingVisitor() {
        override fun rewrite(expr: SheetRangeExpr): Expr = with(expr) {
            return when (range) {
                is NamedColumnRange -> {
                    val columns = columnDescriptors.filter { it.value.name == range.name }.map { it.key }
                    val columnRange = when {
                        columns.isEmpty() ->
                            error("No column named ${range.name}")
                        columns.size > 1 -> error("Multiple columns named ${range.name}")
                        else -> ColumnRange(first = MoveableIndex(columns[0]), second = MoveableIndex(columns[0]))
                    }
                    copy(range = columnRange)
                }
                else -> this
            }
        }
    }
    val result = cells.toMutableMap()
    cells.forEach { (name, expr) ->
        result[name] = converter(expr)
    }
    return result.toCells()
}

