package com.github.jomof.kane.impl.visitor

import com.github.jomof.kane.*
import com.github.jomof.kane.impl.*
import com.github.jomof.kane.impl.functions.AlgebraicDeferredDataMatrix
import com.github.jomof.kane.impl.sheet.*

open class DifferenceVisitor {

    private val leftStack = mutableListOf<Expr>()
    private val rightStack = mutableListOf<Expr>()

    open fun different(e1: Expr, e2: Expr): Expr {
        error("Unhandled difference '$e1' '$e2'")
    }

    open fun missingCellLeft(name: Id, e1: Sheet, e2: Sheet): Expr {
        error("Left sheet is missing cell named '$name'")
    }

    open fun missingCellRight(name: Id, e1: Sheet, e2: Sheet): Expr {
        error("Right sheet is missing cell named '$name'")
    }

    fun cell(name: Id, e1: Expr, e2: Expr): Pair<Id, Expr> {
        return name to visit(e1, e2)
    }

    fun visitTerminal(e1: Expr, e2: Expr): Expr {
        if (e1 != e2) return different(e1, e2)
        return e1
    }

    fun visit(e1: AlgebraicSummaryMatrixScalar, e2: AlgebraicSummaryMatrixScalar): Expr {
        val value = matrix(e1.value, e2.value)
        return e1.dup(value = value)
    }

    fun visit(e1: AlgebraicSummaryScalarScalar, e2: AlgebraicSummaryScalarScalar): Expr {
        val value = scalar(e1.value, e2.value)
        return e1.dup(value = value)
    }

    fun visit(e1: AlgebraicUnaryScalarScalar, e2: AlgebraicUnaryScalarScalar): Expr {
        val value = scalar(e1.value, e2.value)
        return e1.dup(value = value)
    }

    fun visit(e1: AlgebraicUnaryMatrixMatrix, e2: AlgebraicUnaryMatrixMatrix): Expr {
        val matrix = matrix(e1.value, e2.value)
        return e1.dup(value = matrix)
    }

    fun visit(e1: AlgebraicBinarySummaryScalarScalarScalar, e2: AlgebraicBinarySummaryScalarScalarScalar): Expr {
        val left = scalar(e1.left, e2.left)
        val right = scalar(e1.right, e2.right)
        return e1.dup(left = left, right = right)
    }

    fun visit(e1: AlgebraicBinarySummaryMatrixScalarScalar, e2: AlgebraicBinarySummaryMatrixScalarScalar): Expr {
        val left = matrix(e1.left, e2.left)
        val right = scalar(e1.right, e2.right)
        return e1.dup(left = left, right = right)
    }

    fun visit(e1: AlgebraicBinaryScalarScalarScalar, e2: AlgebraicBinaryScalarScalarScalar): Expr {
        val left = scalar(e1.left, e2.left)
        val right = scalar(e1.right, e2.right)
        return e1.dup(left = left, right = right)
    }

    fun visit(e1: AlgebraicBinaryMatrixScalarMatrix, e2: AlgebraicBinaryMatrixScalarMatrix): Expr {
        val left = matrix(e1.left, e2.left)
        val right = scalar(e1.right, e2.right)
        return e1.dup(left = left, right = right)
    }

    fun visit(e1: AlgebraicBinaryScalarMatrixMatrix, e2: AlgebraicBinaryScalarMatrixMatrix): Expr {
        val left = scalar(e1.left, e2.left)
        val right = matrix(e1.right, e2.right)
        return e1.dup(left = left, right = right)
    }

    fun visit(e1: AlgebraicBinaryMatrixMatrixMatrix, e2: AlgebraicBinaryMatrixMatrixMatrix): Expr {
        val left = matrix(e1.left, e2.left)
        val right = matrix(e1.right, e2.right)
        return e1.dup(left = left, right = right)
    }

    fun visit(e1: CoerceMatrix, e2: CoerceMatrix): Expr {
        val value = visit(e1.value, e2.value)
        return e1.dup(value = value)
    }

    fun visit(e1: MatrixVariableElement, e2: MatrixVariableElement): Expr {
        if (e1.column != e2.column) return different(e1, e2)
        if (e1.row != e2.row) return different(e1, e2)
        if (e1.initial != e2.initial) return different(e1, e2)
        val matrix = visit(e1.matrix, e2.matrix) as NamedMatrixVariable
        return e1.dup(matrix = matrix)
    }

    fun visit(e1: CoerceScalar, e2: CoerceScalar): Expr {
        val value = visit(e1.value, e2.value)
        return e1.dup(value = value)
    }

    fun visit(e1: NamedScalar, e2: NamedScalar): Expr {
        val scalar = scalar(e1.scalar, e2.scalar)
        return e1.dup(scalar = scalar)
    }

    fun visit(e1: NamedMatrix, e2: NamedMatrix): Expr {
        val matrix = matrix(e1.matrix, e2.matrix)
        return e1.dup(matrix = matrix)
    }

    fun visit(e1: RetypeScalar, e2: RetypeScalar): Expr {
        if (e1.type != e2.type) different(e1, e2)
        val scalar = scalar(e1.scalar, e2.scalar)
        return e1.dup(scalar = scalar)
    }

    fun visit(e1: RetypeMatrix, e2: RetypeMatrix): Expr {
        if (e1.type != e2.type) different(e1, e2)
        val matrix = matrix(e1.matrix, e2.matrix)
        return e1.dup(matrix = matrix)
    }

    fun visit(e1: CellIndexedScalar, e2: CellIndexedScalar): Expr {
        if (e1.cell != e2.cell) return different(e1, e2)
        val expr = scalar(e1.expr, e2.expr)
        return e1.dup(expr = expr)
    }

    fun visit(e1: Sheet, e2: Sheet): Expr {
        val allNames: Set<Id> = e1.cells.keys + e2.cells.keys
        val cells = mutableMapOf<Id, Expr>()
        for (name in allNames) {
            val c1 = e1.cells[name] ?: missingCellLeft(name, e1, e2)
            val c2 = e2.cells[name] ?: missingCellRight(name, e1, e2)
            val (rewrittenName, rewrittenCell) = cell(name, c1, c2)
            cells[rewrittenName] = rewrittenCell
        }
        return e1.copy(cells = cells.toCells())
    }

    fun visit(e1: Tableau, e2: Tableau): Expr {
        val children = (e1.children zip e2.children).map { (l, r) -> visit(l, r) as AlgebraicExpr }
        return Tableau(children)
    }

    fun visit(e1: GroupBy, e2: GroupBy): Expr {
        val sheet = sheet(e1.sheet, e2.sheet)
        val selector = (e1.keySelector zip e2.keySelector).map { (l, r) -> visit(l, r) }
        return GroupBy(sheet, selector)
    }

    fun visit(e1: DataMatrix, e2: DataMatrix): Expr {
        if (e1.columns != e2.columns) return different(e1, e2)
        if (e1.rows != e2.rows) return different(e1, e2)
        val elements = (e1.elements zip e2.elements).map { (l, r) -> scalar(l, r) }
        return DataMatrix(e1.columns, e1.rows, elements)
    }

    fun visit(e1: AlgebraicDeferredDataMatrix, e2: AlgebraicDeferredDataMatrix): Expr {
        if (e1.op != e2.op) return different(e1, e2)
        return AlgebraicDeferredDataMatrix(
            op = e1.op,
            left = algebraic(e1.left, e2.left),
            right = algebraic(e1.right, e2.right),
            data = visit(e1.data, e2.data) as DataMatrix
        )
    }

    fun matrix(e1: Expr, e2: Expr): MatrixExpr {
        val matrix = visit(e1, e2) as MatrixExpr
        return matrix
    }

    fun scalar(e1: Expr, e2: Expr): ScalarExpr {
        val scalar = visit(e1, e2) as ScalarExpr
        return scalar
    }

    fun algebraic(e1: Expr, e2: Expr): AlgebraicExpr {
        val algebraic = visit(e1, e2) as AlgebraicExpr
        return algebraic
    }

    fun sheet(e1: Sheet, e2: Sheet): Sheet {
        val sheet = visit(e1, e2) as Sheet
        return sheet
    }

    fun visit(e1: Expr, e2: Expr): Expr {
        try {
            leftStack.add(0, e1)
            rightStack.add(0, e2)
            if (e1 === e2) return e1
            if (e1.javaClass != e2.javaClass) return different(e1, e2)
            if (e1.hasName() != e2.hasName()) return different(e1, e2)
            if (e1.hasName() && e1.name != e2.name) return different(e1, e2)
            return when (e1) {
                is ConstantScalar,
                is SheetRangeExpr,
                is NamedScalarVariable,
                is NamedScalarAssign,
                is NamedMatrixAssign,
                is NamedMatrixVariable,
                is ValueExpr<*>,
                is Tiling<*>,
                is StreamingSampleStatisticExpr,
                is CellSheetRangeExpr,
                is ScalarReference,
                is MatrixReference,
                is DiscreteUniformRandomVariable -> visitTerminal(e1, e2)
                is MatrixVariableElement -> visit(e1, e2 as MatrixVariableElement)
                is CoerceMatrix -> visit(e1, e2 as CoerceMatrix)
                is CoerceScalar -> visit(e1, e2 as CoerceScalar)
                is NamedScalar -> visit(e1, e2 as NamedScalar)
                is NamedMatrix -> visit(e1, e2 as NamedMatrix)
                is RetypeScalar -> visit(e1, e2 as RetypeScalar)
                is RetypeMatrix -> visit(e1, e2 as RetypeMatrix)
                is Sheet -> visit(e1, e2 as Sheet)
                is Tableau -> visit(e1, e2 as Tableau)
                is GroupBy -> visit(e1, e2 as GroupBy)
                is DataMatrix -> visit(e1, e2 as DataMatrix)
                is AlgebraicDeferredDataMatrix -> visit(e1, e2 as AlgebraicDeferredDataMatrix)
                is CellIndexedScalar -> visit(e1, e2 as CellIndexedScalar)
                is AlgebraicSummaryMatrixScalar -> visit(e1, e2 as AlgebraicSummaryMatrixScalar)
                is AlgebraicBinarySummaryScalarScalarScalar -> visit(e1, e2 as AlgebraicBinarySummaryScalarScalarScalar)
                is AlgebraicBinarySummaryMatrixScalarScalar -> visit(e1, e2 as AlgebraicBinarySummaryMatrixScalarScalar)
                is AlgebraicBinaryScalarScalarScalar -> visit(e1, e2 as AlgebraicBinaryScalarScalarScalar)
                is AlgebraicBinaryMatrixScalarMatrix -> visit(e1, e2 as AlgebraicBinaryMatrixScalarMatrix)
                is AlgebraicBinaryScalarMatrixMatrix -> visit(e1, e2 as AlgebraicBinaryScalarMatrixMatrix)
                is AlgebraicBinaryMatrixMatrixMatrix -> visit(e1, e2 as AlgebraicBinaryMatrixMatrixMatrix)
                is AlgebraicSummaryScalarScalar -> visit(e1, e2 as AlgebraicSummaryScalarScalar)
                is AlgebraicUnaryScalarScalar -> visit(e1, e2 as AlgebraicUnaryScalarScalar)
                is AlgebraicUnaryMatrixMatrix -> visit(e1, e2 as AlgebraicUnaryMatrixMatrix)
                else -> error("${e1.javaClass}")
            }
        } finally {
            leftStack.removeAt(0)
            rightStack.removeAt(0)
        }
    }
}