package com.github.jomof.kane.impl.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.MatrixExpr
import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.impl.*
import com.github.jomof.kane.impl.ComputableIndex.*
import com.github.jomof.kane.impl.functions.*
import com.github.jomof.kane.impl.visitor.DifferenceVisitor
import com.github.jomof.kane.impl.visitor.SheetRewritingVisitor
import com.github.jomof.kane.impl.visitor.visit

private class LiftEmbeddedNamedExpressionsToCells : SheetRewritingVisitor() {
    override fun rewrite(expr: Expr): Expr {
        if (expr.hasName() && !cellExists(expr.name)) {
            putCell(expr.name, expr.toUnnamed())
        }
        return super.rewrite(expr)
    }
}

private class NameExplicitlyReferencedColumns : SheetRewritingVisitor() {
    override fun cell(name: Id, expr: Expr): Pair<Id, Expr> {
        if (name is Coordinate) return name to expr
        when (expr) {
            is CoerceMatrix -> when (val matrix = expr.value) {
                is SheetRangeExpr -> when (val range = matrix.rangeRef) {
                    is ColumnRangeRef -> {
                        val old = getColumnDescriptor(range.first.index)
                        val new = old.copy(name = name)
                        putColumnDescriptor(range.first.index, new)
                    }
                    is RectangleRangeRef -> {
                    }
                    is NamedColumnRangeRef -> {
                    }
                    else -> error("${range.javaClass}")
                }
                else -> error("${matrix.javaClass}")
            }
            else -> {
            }
        }
        return name to expr
    }
}

private class AllocateColumnsForNamedMatrixes : SheetRewritingVisitor() {
    override fun cell(name: Id, expr: Expr): Pair<Id, Expr> {
        if (name !is String) return name to expr
        if (columnNameExists(name)) return name to expr
        if (expr is CoerceMatrix && expr.value is SheetRangeExpr && expr.value.rangeRef !is ColumnRangeRef) return name to expr

        when (expr) {
            is MatrixExpr -> {
                val width = expr.columns
                allocateColumnRange(name, width)
            }
            is Tiling<*> -> {
                val width = expr.columns
                allocateColumnRange(name, width)
            }
        }

        return name to expr
    }
}

private class ConvertLooksLikeCellNameToUpper : SheetRewritingVisitor() {
    override fun cell(name: Id, expr: Expr): Pair<Id, Expr> {
        if (name !is String) return super.cell(name, expr)
        val upper = name.toUpperCase()
        if (!looksLikeCellName(upper)) return super.cell(name, expr)
        val new = cellNameToCoordinate(upper)
        val result = super.cell(new, expr)
        putCell(new, expr)
        removeCell(name)
        return result
    }

    override fun rewrite(expr: Expr): Expr {
        if (!expr.hasName()) return super.rewrite(expr)
        val name = expr.name
        if (name !is String) return super.rewrite(expr)
        val upper = name.toUpperCase()
        if (!looksLikeCellName(upper)) return super.rewrite(expr)
        val new = cellNameToCoordinate(upper)
        val result = super.rewrite(expr.toUnnamed().toNamed(new))
        return result
    }

}


private class InsertCellNamedExpressions(val literalOnly: Boolean) : SheetRewritingVisitor() {
    override fun cell(name: Id, expr: Expr): Pair<Id, Expr> {
        if (name !is String) return name to expr
        val upper = name.toUpperCase()
        if (looksLikeCellName(upper)) {
            val id = cellNameToCoordinate(upper)
            when (expr) {
                is ValueExpr<*>,
                is CellSheetRangeExpr,
                is SheetRangeExpr,
                is ScalarExpr -> {
                    removeCell(name)
                    putCell(id, expr)
                }
                is MatrixExpr -> {
                    val (columns, rows) = expr.tryGetDimensions(rowCount())
                    if (columns == null) return name to expr
                    if (rows == null) return name to expr

                    val placements = mutableListOf<Pair<Coordinate, Expr>>()
                    for (column in 0 until columns) {
                        for (row in 0 until rows) {
                            val coordinate = coordinate(id.column + column, id.row + row)
                            if (!cellExists(coordinate)) {
                                val element = expr.getMatrixElement(column, row)
                                placements += coordinate to element
                            }
                        }
                    }
                    if (!literalOnly || placements.all { it.second.canGetConstant() }) {
                        removeCell(name)
                        placements.forEach { (coordinate, element) ->
                            putCell(coordinate, element)
                        }
                    }
                }
                is Tiling<*> -> {
                    removeCell(name)
                    for (column in 0 until expr.columns) {
                        for (row in 0 until expr.rows) {
                            val coordinate = coordinate(id.column + column, id.row + row)
                            if (!cellExists(coordinate)) {
                                val element = expr.getUnnamedElement(coordinate(column, row))
                                putCell(coordinate, element)
                            }
                        }
                    }
                }
                else -> error("${expr.javaClass}")
            }
        }
        return super.cell(name, expr)
    }
}

private class ReplaceNamedColumnRangeRef : SheetRewritingVisitor() {
    override fun rewrite(expr: SheetRangeExpr): Expr {
        return when (val range = expr.rangeRef) {
            is NamedColumnRangeRef -> {
                val columnIndex = getNamedColumnIndex(range.name)
                if (columnIndex == null) expr
                else
                    expr.copy(rangeRef = ColumnRangeRef(MoveableIndex(columnIndex)))
            }
            else -> expr
        }
    }
}

private class ReplaceCellNamedMatrixesWithSheetRange : SheetRewritingVisitor() {
    override fun rewrite(expr: Expr): Expr {
        val result = super.rewrite(expr)
        if (result !is MatrixExpr) return result
        if (!result.hasName()) return result
        val name = result.name
        if (name !is String) return convertToRange(result, name as Coordinate)
        val (columns, rows) = result.tryGetDimensions(rowCount())
        if (columns == null || rows == null) return result
        val upper = name.toUpperCase()
        if (looksLikeCellName(upper)) convertToRange(result, cellNameToCoordinate(upper))
        val column = getNamedColumnIndex(name)
        if (column != null) {
            return convertToRange(result, coordinate(column, 0))
        }
        return result
    }

    private fun convertToRange(matrix: MatrixExpr, cell: Coordinate): Expr {
        val (columns, rows) = matrix.tryGetDimensions(rowCount())
        if (columns == null || rows == null) return super.rewrite(matrix)
        return CoerceMatrix(
            value = SheetRangeExpr(
                RectangleRangeRef(
                    first = cell.toComputableCoordinate(),
                    second = (cell + coordinate(columns - 1, rows - 1)).toComputableCoordinate()
                )
            )
        )
    }
}

data class CellIndexedScalar(
    val cell: Coordinate,
    val expr: Expr
) : ScalarExpr {
    override fun toString() = "($expr)[${coordinateToCellName(cell)}]"
    fun dup(cell: Coordinate = this.cell, expr: Expr = this.expr): CellIndexedScalar {
        if (cell === this.cell && expr === this.expr) return this
        return CellIndexedScalar(cell, expr)
    }
}

private class ExpandMatrixElementsIntoSheetCells : SheetRewritingVisitor() {
    override fun cell(name: Id, expr: Expr): Pair<Id, Expr> {
        val result = name to expr
        when (expr) {
            is MatrixExpr -> {
                val (columns, rows) = expr.tryGetDimensions(rowCount())
                if (columns == null || rows == null) return result
                val matrix = expr.toDataMatrix(columns, rows)
                val startColumn = columnIndexOfName(name)
                val startRow = rowIndexOfName(name)
                removeCell(name)
                for (column in 0 until columns) {
                    for (row in 0 until rows) {
                        val element = matrix.getMatrixElement(column, row)
                        val coordinate = coordinate(startColumn + column, startRow + row)
                        if (!cellExists(coordinate) || coordinate == name) {
                            putCell(coordinate, CellIndexedScalar(coordinate, element))
                        }
                    }
                }
            }
            is Tiling<*> -> {
                val columns = expr.columns
                val rows = expr.rows
                val startColumn = columnIndexOfName(name)
                val startRow = rowIndexOfName(name)
                removeCell(name)
                for (column in 0 until columns) {
                    for (row in 0 until rows) {
                        val coordinate = coordinate(startColumn + column, startRow + row)
                        if (!cellExists(coordinate) || coordinate == name) {
                            putCell(coordinate, expr.getUnnamedElement(coordinate(column, row)))
                        }
                    }
                }
            }
        }

        return result
    }
}

private class IndexCellsWithNonMoveableCoordinates : SheetRewritingVisitor() {
    override fun cell(name: Id, expr: Expr): Pair<Id, Expr> {
        val result = name to expr
        if (name !is Coordinate) return result
        if (needsIndexing(expr)) {
            return name to CellIndexedScalar(name, expr)
        }
        return result
    }

    private fun needsIndexing(expr: Expr): Boolean {
        var needsIndexing = false
        expr.visit {
            if (!needsIndexing && it is CellSheetRangeExpr) {
                val range = it.rangeRef
                if (range.column !is MoveableIndex) needsIndexing = true
                if (range.row !is MoveableIndex) needsIndexing = true
            }
            if (!needsIndexing && it is SheetRangeExpr) {
                val range = it.rangeRef
                if (range is CellRangeRef) error("")
                if (range is RectangleRangeRef) {
                    if (range.first.column !is MoveableIndex) needsIndexing = true
                    if (range.first.row !is MoveableIndex) needsIndexing = true
                    if (range.second.column !is MoveableIndex) needsIndexing = true
                    if (range.second.row !is MoveableIndex) needsIndexing = true
                }
                if (range is ColumnRangeRef) {
                    if (range.first !is MoveableIndex) needsIndexing = true
                    if (range.second !is MoveableIndex) needsIndexing = true
                }
            }
        }
        return needsIndexing
    }
}

private class CollapseCellIndexedScalar : SheetRewritingVisitor() {
    val indexes = mutableListOf<Coordinate>()
    override fun cell(name: Id, expr: Expr): Pair<Id, Expr> {
        if (name !is Coordinate) return name to expr
        try {
            indexes.add(0, name)
            val result = super.cell(name, expr)
            return result
        } finally {
            indexes.removeAt(0)
        }
    }

    override fun rewrite(expr: CellSheetRangeExpr): Expr {
        return collapseCellRangeRef(expr.rangeRef) ?: expr
    }

    private fun collapseCellRangeRef(range: CellRangeRef): Expr? {
        return when {
            range.column is MoveableIndex && range.row is MoveableIndex -> null
            range.column is RelativeIndex && range.row is RelativeIndex -> {
                val result =
                    CellSheetRangeExpr(
                        CellRangeRef(
                            MoveableIndex(indexes[0].column + range.column.index),
                            MoveableIndex(indexes[0].row + range.row.index)
                        )
                    )
                result
            }
            range.column is MoveableIndex && range.row is FixedIndex -> {
                val result =
                    CellSheetRangeExpr(
                        CellRangeRef(
                            MoveableIndex(indexes[0].column + range.column.index - 1),
                            MoveableIndex(range.row.index)
                        )
                    )
                result
            }
            range.column is FixedIndex && range.row is MoveableIndex -> {
                val result =
                    CellSheetRangeExpr(
                        CellRangeRef(
                            MoveableIndex(range.column.index),
                            MoveableIndex(indexes[0].row + range.row.index - 1)
                        )
                    )
                result
            }
            else ->
                error("$range")
        }
    }

    override fun rewrite(expr: CoerceScalar): Expr {
        val rewritten = rewrite(expr.value)
        return when {
            rewritten === expr.value -> expr
            rewritten is CoerceScalar -> rewritten
            else -> expr.dup(value = rewritten)
        }
    }

    override fun rewrite(expr: NamedMatrix): Expr = with(expr) {
        val rewritten = rewrite(matrix)
        return if (rewritten === matrix) this
        else when (rewritten) {
            is ScalarExpr -> rewritten
            is MatrixExpr -> expr.dup(matrix = rewritten)
            else -> error("${rewritten.javaClass}")
        }
    }

    override fun rewrite(expr: AlgebraicBinaryScalarMatrixMatrix): Expr = with(expr) {
        val leftRewritten = scalar(left)
        val rightRewritten = rewrite(right)
        if (leftRewritten === left && rightRewritten === right) return this
        return when (rightRewritten) {
            is ScalarExpr -> AlgebraicBinaryScalarScalarScalar(
                op as IAlgebraicBinaryScalarScalarScalarFunction,
                leftRewritten,
                rightRewritten
            )
            is MatrixExpr -> expr.dup(left = leftRewritten, right = rightRewritten)
            else -> error("${rightRewritten.javaClass}")
        }
    }

    override fun rewrite(expr: AlgebraicBinaryMatrixScalarMatrix): Expr = with(expr) {
        val leftRewritten = rewrite(left)
        val rightRewritten = scalar(right)
        if (leftRewritten === left && rightRewritten === right) return this
        return when (leftRewritten) {
            is ScalarExpr -> AlgebraicBinaryScalarScalarScalar(
                op as IAlgebraicBinaryScalarScalarScalarFunction,
                leftRewritten,
                rightRewritten
            )
            is MatrixExpr -> expr.dup(left = leftRewritten, right = rightRewritten)
            else -> error("${leftRewritten.javaClass}")
        }
    }

    override fun rewrite(expr: AlgebraicBinaryMatrixMatrixMatrix): Expr = with(expr) {
        val leftRewritten = rewrite(left)
        val rightRewritten = rewrite(right)
        if (leftRewritten === left && rightRewritten === right) return this
        return when {
            leftRewritten is ScalarExpr && rightRewritten is ScalarExpr -> AlgebraicBinaryScalarScalarScalar(
                op as AlgebraicBinaryFunction,
                leftRewritten,
                rightRewritten
            )
            leftRewritten is ScalarExpr && rightRewritten is MatrixExpr -> AlgebraicBinaryScalarMatrixMatrix(
                op as AlgebraicBinaryFunction,
                leftRewritten,
                rightRewritten
            )
            leftRewritten is MatrixExpr && rightRewritten is ScalarExpr -> AlgebraicBinaryMatrixScalarMatrix(
                op as AlgebraicBinaryFunction,
                leftRewritten,
                rightRewritten
            )
            leftRewritten is MatrixExpr && rightRewritten is MatrixExpr -> AlgebraicBinaryMatrixMatrixMatrix(
                op as AlgebraicBinaryFunction,
                leftRewritten,
                rightRewritten
            )
            else -> error("${leftRewritten.javaClass} ${rightRewritten.javaClass}")
        }
    }

    override fun rewrite(expr: CellIndexedScalar): Expr {
        try {
            indexes.add(0, expr.cell)
            val result = rewrite(expr.expr)
            assert(result !is CellIndexedScalar) {
                rewrite(expr.expr)
                "Did not rewrite CellIndexedScalar"
            }
            return result.withNameOf(expr)
        } finally {
            indexes.removeAt(0)
        }
    }
}

private class RemoveNamesOfMatrixes : SheetRewritingVisitor(allowNameChange = true) {
    override fun cell(name: Id, expr: Expr): Pair<Id, Expr> {
        if (expr is CoerceMatrix && expr.value is SheetRangeExpr) removeCell(name)
        return super.cell(name, expr)
    }

    override fun rewrite(expr: NamedMatrix): Expr {
        return rewrite(expr.matrix)
    }
}

private class CheckVerboten : SheetRewritingVisitor() {
    private fun ComputableIndex.assertMoveable() = assert(this is MoveableIndex) {
        "$javaClass"
    }

    override fun rewrite(expr: CellSheetRangeExpr): Expr {
        expr.rangeRef.column.assertMoveable()
        expr.rangeRef.row.assertMoveable()
        return expr
    }

    override fun rewrite(expr: SheetRangeExpr): Expr {
        val range = expr.rangeRef
        if (range is CellRangeRef) error("")
        if (range is RectangleRangeRef) {
            range.first.column.assertMoveable()
            range.first.row.assertMoveable()
            range.second.column.assertMoveable()
            range.second.row.assertMoveable()
            return expr
        }
        if (range is ColumnRangeRef) {
            range.first.assertMoveable()
            range.second.assertMoveable()
            return expr
        }
        error("${expr.rangeRef.javaClass}")
    }

    override fun rewrite(expr: CellIndexedScalar) =
        error("unexpected")

    override fun cell(name: Id, expr: Expr): Pair<Id, Expr> {
        val result = super.cell(name, expr)
        if (name !is Coordinate) return result
        if (expr is ScalarExpr) return result
        // TODO reenable
        //    error("cell elements should be scalar")
        return result
    }
}

private val convertLooksLikeCellNameToUpper = ConvertLooksLikeCellNameToUpper()
private val insertLiteralCellNamedExpressions = InsertCellNamedExpressions(true)
private val insertAllCellNamedExpressions = InsertCellNamedExpressions(false)
private val replaceNamedColumnRangeRef = ReplaceNamedColumnRangeRef()
private val liftEmbeddedNamedExpressionsToCells = LiftEmbeddedNamedExpressionsToCells()
private val collapseCellIndexedScalar = CollapseCellIndexedScalar()
private val nameExplicitlyReferencedColumns = NameExplicitlyReferencedColumns()
private val allocateColumnsForNamedMatrixes = AllocateColumnsForNamedMatrixes()
private val expandMatrixElementsIntoSheetCells = ExpandMatrixElementsIntoSheetCells()
private val indexCellsWithNonMoveableCoordinates = IndexCellsWithNonMoveableCoordinates()
private val replaceCellNamedMatrixesWithSheetRange = ReplaceCellNamedMatrixesWithSheetRange()
private val removeNamesOfMatrixes = RemoveNamesOfMatrixes()
private val checkVerboten = CheckVerboten()

private fun Sheet.checkRefDeref(): Sheet {
    val ref = makeReferenced()
    val deref = makeDereferenced()
    val check = object : DifferenceVisitor() {
        override fun missingCellLeft(name: Id, e1: Sheet, e2: Sheet): Expr {
            return e2.cells[name]!!
        }
    }
    val wasRef = this === ref
    val wasDeref = this === deref
    assert(wasDeref || wasRef) {
        "ref deref was not idempotent"
    }
    if (wasRef) {
        val phase1 = deref
        val phase2 = phase1.makeReferenced()
        check.visit(this, phase2)
    }
    if (wasDeref) {
        val phase1 = ref
        val phase2 = phase1.makeDereferenced()
        check.visit(this, phase2)
    }
    return this
}

fun Sheet.build(): Sheet {
    var maxIterations = 10000
    var last = this
    while (true) {
        if (--maxIterations == 0) error("Could not build Sheet")
        val convertedLooksLikeCellNameToUpper = convertLooksLikeCellNameToUpper(last)
        val replacedNamedColumnRangeRef = replaceNamedColumnRangeRef(convertedLooksLikeCellNameToUpper)
        val liftedEmbeddedNamedExpressionsToCells = liftEmbeddedNamedExpressionsToCells(replacedNamedColumnRangeRef)
        val namedExplicitlyReferencedColumns = nameExplicitlyReferencedColumns(liftedEmbeddedNamedExpressionsToCells)
        val allocatedColumnsForNamedMatrixes = allocateColumnsForNamedMatrixes(namedExplicitlyReferencedColumns)
        val insertedLiteralCellNamedExpressions = insertLiteralCellNamedExpressions(allocatedColumnsForNamedMatrixes)
        val replacedCellNamedMatrixesWithSheetRange =
            replaceCellNamedMatrixesWithSheetRange(insertedLiteralCellNamedExpressions)
        val insertedAllCellNamedExpressions = insertAllCellNamedExpressions(replacedCellNamedMatrixesWithSheetRange)
        val expandedMatrixElementsIntoSheetCells = expandMatrixElementsIntoSheetCells(insertedAllCellNamedExpressions)
        val collapsedCellIndexedScalar = collapseCellIndexedScalar(expandedMatrixElementsIntoSheetCells)
        val indexedCellsWithNonMoveableCoordinates = indexCellsWithNonMoveableCoordinates(collapsedCellIndexedScalar)
        val removedNamesOfMatrixes = removeNamesOfMatrixes(indexedCellsWithNonMoveableCoordinates)

        if (last == removedNamesOfMatrixes) {
            checkVerboten(last)
            return last.makeReferenced()
        }
        last = removedNamesOfMatrixes
    }
}