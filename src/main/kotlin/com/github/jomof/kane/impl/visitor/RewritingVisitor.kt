package com.github.jomof.kane.impl.visitor

import com.github.jomof.kane.AlgebraicExpr
import com.github.jomof.kane.*
import com.github.jomof.kane.MatrixExpr
import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.impl.*
import com.github.jomof.kane.impl.functions.*
import com.github.jomof.kane.impl.sheet.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

internal open class RewritingVisitor(protected val checkIdentity: Boolean = false) {
    private var depth = 0
    open fun rewrite(expr: CoerceScalar): Expr = with(expr) {
        val rewritten = rewrite(value)
        return if (rewritten === value) this
        else copy(value = rewritten)
    }

    open fun rewrite(expr: CoerceMatrix): Expr = with(expr) {
        val rewritten = rewrite(value)
        return if (rewritten === value) this
        else copy(value = rewritten)
    }

    open fun rewrite(expr: RetypeScalar): Expr = with(expr) {
        val rewritten = scalar(scalar)
        return if (rewritten === scalar) this
        else copy(scalar = rewritten)
    }

    open fun rewrite(expr: CellIndexedScalar): Expr {
        val rewritten = rewrite(expr.expr)
        return if (rewritten === expr.expr) expr
        else expr.copy(expr = rewritten)
    }

    open fun rewrite(expr: RetypeMatrix): Expr = with(expr) {
        val rewritten = matrix(matrix)
        return if (rewritten === matrix) this
        else copy(matrix = rewritten)
    }

    open fun rewrite(expr: GroupBy): Expr = with(expr) {
        val rewritten = sheet(sheet)
        return if (rewritten === sheet) this
        else copy(sheet = rewritten)
    }

    open fun rewrite(expr: StreamingSampleStatisticExpr): Expr = expr
    open fun rewrite(expr: ConstantScalar): Expr = expr
    open fun rewrite(expr: DiscreteUniformRandomVariable): Expr = expr
    open fun rewrite(expr: SheetRangeExpr): Expr = expr
    open fun rewrite(expr: Tiling<*>): Expr = expr
    open fun rewrite(expr: ValueExpr<*>): Expr = expr
    open fun rewrite(expr: NamedScalarVariable): Expr = expr
    open fun rewrite(expr: NamedMatrixVariable): Expr = expr
    open fun rewrite(expr: ScalarVariable): Expr = expr
    open fun rewrite(expr: MatrixVariableElement): Expr = expr
    open fun rewrite(expr: NamedScalarAssign): Expr {
        val scalar = scalar(expr.right)
        if (scalar === expr.right) return expr
        return expr.copy(right = scalar)
    }

    open fun rewrite(expr: NamedMatrixAssign): Expr {
        val matrix = matrix(expr.right)
        if (matrix === expr.right) return expr
        return expr.copy(right = matrix)
    }

    open fun rewrite(expr: NamedScalar): Expr = with(expr) {
        val rewritten = scalar(scalar)
        return if (rewritten === scalar) this
        else copy(scalar = rewritten)
    }

    open fun rewrite(expr: NamedSheetRangeExpr): Expr = with(expr) {
        val rewritten = range(range)
        return if (rewritten === range) this
        else copy(range = rewritten)
    }

    open fun rewrite(expr: NamedMatrix): Expr = with(expr) {
        val rewritten = matrix(matrix)
        return if (rewritten === matrix) this
        else copy(matrix = rewritten)
    }

    open fun rewrite(expr: AlgebraicDeferredDataMatrix): Expr = with(expr) {
        val leftRewritten = algebraic(expr.left)
        val rightRewritten = algebraic(expr.right)
        val dataRewritten = rewrite(expr.data) as DataMatrix
        if (leftRewritten === left && rightRewritten === right && dataRewritten === data) return this
        else copy(left = leftRewritten, right = rightRewritten, data = dataRewritten)
    }

    open fun rewrite(expr: DataMatrix): Expr = with(expr) {
        var changed = false
        val rewrittenElements = mutableListOf<ScalarExpr>()
        for (element in elements) {
            val rewritten = scalar(element)
            rewrittenElements += rewritten
            changed = changed || (rewritten !== element)
        }
        return if (changed) copy(elements = rewrittenElements)
        else this
    }

    open fun rewrite(expr: AlgebraicUnaryScalarScalar): Expr = expr.dup(value = scalar(expr.value))
    open fun rewrite(expr: AlgebraicSummaryScalarScalar): Expr = expr.dup(value = scalar(expr.value))
    open fun rewrite(expr: AlgebraicSummaryMatrixScalar): Expr = expr.dup(value = matrix(expr.value))
    open fun rewrite(expr: AlgebraicUnaryMatrixMatrix): Expr = expr.dup(value = matrix(expr.value))
    open fun rewrite(expr: AlgebraicBinaryMatrixMatrixMatrix): Expr =
        expr.dup(left = matrix(expr.left), right = matrix(expr.right))

    open fun rewrite(expr: AlgebraicBinaryMatrixScalarMatrix): Expr =
        expr.dup(left = matrix(expr.left), right = scalar(expr.right))

    open fun rewrite(expr: AlgebraicBinaryScalarScalarScalar): Expr =
        expr.dup(left = scalar(expr.left), right = scalar(expr.right))

    open fun rewrite(expr: AlgebraicBinaryScalarMatrixMatrix): Expr =
        expr.dup(left = scalar(expr.left), right = matrix(expr.right))

    open fun rewrite(expr: AlgebraicBinarySummaryScalarScalarScalar): Expr =
        expr.dup(left = scalar(expr.left), right = scalar(expr.right))

    open fun rewrite(expr: AlgebraicBinarySummaryMatrixScalarScalar): Expr =
        expr.dup(left = matrix(expr.left), right = scalar(expr.right))

    open fun rewrite(expr: Sheet): Expr = with(expr) {
        var changed = false
        val rewrittenElements = mutableMapOf<Id, Expr>()
        for (cell in cells) {
            val (name, cellExpr) = cell
            val (rewrittenName, rewrittenCell) = cell(name, cellExpr)
            changed = changed || (rewrittenCell !== cellExpr)
            changed = changed || (rewrittenName !== name)
            rewrittenElements[rewrittenName] = rewrittenCell
        }
        return if (changed) copy(cells = rewrittenElements.toCells())
        else this
    }

    open fun rewrite(expr: Tableau): Expr = with(expr) {
        var changed = false
        val rewrittenElements = mutableListOf<NamedAlgebraicExpr>()
        for (child in children) {
            val rewritten = named(child)
            changed = changed || (rewritten !== child)
            rewrittenElements.add(rewritten)
        }
        return if (changed) expr.copy(children = rewrittenElements)
        else this
    }

    open fun scalar(expr: ScalarExpr): ScalarExpr {
        val result = rewrite(expr)
        assert(result is ScalarExpr) {
            rewrite(expr)
            "result '${result.javaClass.simpleName}' was not ScalarExpr"
        }
        return result as ScalarExpr
    }

    open fun cell(name: Id, expr: Expr): Pair<Id, Expr> {
        return name to rewrite(expr)
    }

    open fun sheet(expr: Sheet) = rewrite(expr) as Sheet
    open fun statistic(expr: StatisticExpr) = rewrite(expr) as StatisticExpr
    open fun matrix(expr: MatrixExpr) = rewrite(expr) as MatrixExpr
    open fun range(expr: SheetRange): SheetRange {
        val result = rewrite(expr)
        assert(result is SheetRangeExpr) {
            rewrite(expr)
            "result '${result.javaClass.simpleName}' was not SheetRangeExpr"
        }
        return result as SheetRangeExpr
    }
    open fun algebraic(expr: AlgebraicExpr) = rewrite(expr) as AlgebraicExpr
    open fun named(expr: NamedAlgebraicExpr) = rewrite(expr) as NamedAlgebraicExpr

    open fun beginRewrite(expr: Expr, depth: Int) {}
    open fun endRewrite(expr: Expr, depth: Int) {}

    open fun rewrite(expr: Expr): Expr {
        try {
            ++depth
            beginRewrite(expr, depth)

            val result = when (expr) {
                is ConstantScalar -> rewrite(expr)
                is DiscreteUniformRandomVariable -> rewrite(expr)
                is StreamingSampleStatisticExpr -> rewrite(expr)
                is AlgebraicBinaryMatrixMatrixMatrix -> rewrite(expr)
                is AlgebraicBinaryMatrixScalarMatrix -> rewrite(expr)
                is AlgebraicBinaryScalarScalarScalar -> rewrite(expr)
                is AlgebraicBinaryScalarMatrixMatrix -> rewrite(expr)
                is AlgebraicBinarySummaryScalarScalarScalar -> rewrite(expr)
                is AlgebraicBinarySummaryMatrixScalarScalar -> rewrite(expr)
                is AlgebraicUnaryMatrixMatrix -> rewrite(expr)
                is AlgebraicUnaryScalarScalar -> rewrite(expr)
                is AlgebraicSummaryMatrixScalar -> rewrite(expr)
                is AlgebraicSummaryScalarScalar -> rewrite(expr)
                is CoerceScalar -> rewrite(expr)
                is CoerceMatrix -> rewrite(expr)
                is DataMatrix -> rewrite(expr)
                is AlgebraicDeferredDataMatrix -> rewrite(expr)
                is NamedScalar -> rewrite(expr)
                is NamedScalarVariable -> rewrite(expr)
                is NamedMatrixVariable -> rewrite(expr)
                is NamedSheetRangeExpr -> rewrite(expr)
                is NamedMatrix -> rewrite(expr)
                is SheetRangeExpr -> rewrite(expr)
                is Tiling<*> -> rewrite(expr)
                is ValueExpr<*> -> rewrite(expr)
                is Sheet -> rewrite(expr)
                is ScalarVariable -> rewrite(expr)
                is MatrixVariableElement -> rewrite(expr)
                is NamedScalarAssign -> rewrite(expr)
                is NamedMatrixAssign -> rewrite(expr)
                is Tableau -> rewrite(expr)
                is RetypeScalar -> rewrite(expr)
                is RetypeMatrix -> rewrite(expr)
                is GroupBy -> rewrite(expr)
                is CellIndexedScalar -> rewrite(expr)
                else -> error("${expr.javaClass}")
            }
            if (checkIdentity && result !== expr) {
                assert(result != expr) {
                    rewrite(expr)
                    "Change for no reason"
                }
            }
            return result
        } finally {
            endRewrite(expr, depth)
            --depth
        }
    }

    open operator fun invoke(expr: Expr) = rewrite(expr)
    open operator fun invoke(expr: Sheet) = sheet(expr)
}