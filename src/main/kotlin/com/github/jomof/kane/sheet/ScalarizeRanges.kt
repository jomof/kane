package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.ComputableIndex.MoveableIndex
import com.github.jomof.kane.functions.*
import com.github.jomof.kane.visitor.RewritingVisitor

private fun Expr.ranges(): Set<SheetRangeRef> {
    return when (this) {
        is DataMatrix -> {
            val set = mutableSetOf<SheetRangeRef>()
            elements.forEach { expr ->
                set += expr.ranges()
            }
            set
        }
        is NamedMatrix -> matrix.ranges()
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
        is SheetRangeExpr -> when (rangeRef) {
            is ColumnRangeRef -> setOf(rangeRef)
            is RectangleRangeRef -> setOf(rangeRef)
            is CellRangeRef -> setOf()
            else -> error("${rangeRef.javaClass}")
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
            when (rangeRef) {
                is ColumnRangeRef -> copy(rangeRef = CellRangeRef(column = rangeRef.first, MoveableIndex(row)))
                is CellRangeRef -> this
                is RectangleRangeRef -> this
                else -> error("${rangeRef.javaClass}")
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
            return when (rangeRef) {
                is NamedColumnRangeRef -> {
                    val columns = columnDescriptors.filter { it.value.name == rangeRef.name }.map { it.key }
                    val columnRange = when {
                        columns.isEmpty() ->
                            error("No column named ${rangeRef.name}")
                        columns.size > 1 -> error("Multiple columns named ${rangeRef.name}")
                        else -> ColumnRangeRef(first = MoveableIndex(columns[0]), second = MoveableIndex(columns[0]))
                    }
                    copy(rangeRef = columnRange)
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
