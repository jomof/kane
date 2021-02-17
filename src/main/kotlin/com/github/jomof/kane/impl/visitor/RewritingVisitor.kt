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

internal open class RewritingVisitor(
    private val allowNameChange: Boolean = false,
    private val checkIdentity: Boolean = true
) {
    private var depth = 0
    private val enableDiagnostics = false
    private val stack = mutableListOf<Expr>()
    open fun rewrite(expr: ScalarReference): Expr = expr
    open fun rewrite(expr: MatrixReference): Expr = expr
    open fun rewrite(expr: ExogenousSheetScalar): Expr = expr
    open fun rewrite(expr: StreamingSampleStatisticExpr): Expr = expr
    open fun rewrite(expr: ConstantScalar): Expr = expr
    open fun rewrite(expr: DiscreteUniformRandomVariable): Expr = expr
    open fun rewrite(expr: SheetRangeExpr): Expr = expr
    open fun rewrite(expr: CellSheetRangeExpr): Expr = expr
    open fun rewrite(expr: Tiling<*>): Expr = expr
    open fun rewrite(expr: ValueExpr<*>): Expr = expr
    open fun rewrite(expr: MatrixVariable): Expr = expr
    open fun rewrite(expr: ScalarVariable): Expr = expr
    open fun rewrite(expr: MatrixVariableElement): Expr = expr
    open fun rewrite(expr: CoerceScalar): Expr = expr.dup(value = rewrite(expr.value))
    open fun rewrite(expr: CoerceMatrix): Expr = expr.dup(value = rewrite(expr.value))
    open fun rewrite(expr: RetypeScalar): Expr = expr.dup(scalar = scalar(expr.scalar))
    open fun rewrite(expr: CellIndexedScalar): Expr = expr.dup(expr = rewrite(expr.expr))
    open fun rewrite(expr: RetypeMatrix): Expr = expr.dup(matrix = matrix(expr.matrix))
    open fun rewrite(expr: ScalarAssign): Expr = expr.dup(right = scalar(expr.right))
    open fun rewrite(expr: MatrixAssign): Expr = expr.dup(right = matrix(expr.right))
    open fun rewrite(expr: NamedScalar): Expr = expr.dup(scalar = scalar(expr.scalar))
    open fun rewrite(expr: NamedMatrix): Expr = expr.dup(matrix = matrix(expr.matrix))
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

    open fun rewrite(expr: GroupBy): Expr = with(expr) {
        val rewritten = sheet(sheet)
        return if (rewritten === sheet) this
        else copy(sheet = rewritten)
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
        return if (changed) dup(cells = rewrittenElements.toCells())
        else this
    }

    open fun rewrite(expr: Tableau): Expr = with(expr) {
        var changed = false
        val rewrittenElements = mutableListOf<AlgebraicExpr>()
        for (child in children) {
            val rewritten = algebraic(child)
            changed = changed || (rewritten !== child)
            rewrittenElements.add(rewritten)
        }
        return if (changed) expr.copy(children = rewrittenElements)
        else this
    }

    open fun cell(name: Id, expr: Expr): Pair<Id, Expr> {
        return name to rewrite(expr)
    }

    open fun scalar(expr: ScalarExpr) = rewrite(expr) as ScalarExpr
    open fun sheet(expr: Sheet) = rewrite(expr) as Sheet
    open fun statistic(expr: StatisticExpr) = rewrite(expr) as StatisticExpr
    open fun matrix(expr: MatrixExpr) = rewrite(expr) as MatrixExpr
    open fun algebraic(expr: AlgebraicExpr) = rewrite(expr) as AlgebraicExpr
    open fun named(expr: NamedAlgebraicExpr) = rewrite(expr) as NamedAlgebraicExpr

    open fun beginRewrite(expr: Expr, depth: Int) {}
    open fun endRewrite(expr: Expr, depth: Int) {}

    open fun rewrite(expr: Expr): Expr {
        try {
            ++depth
            if (enableDiagnostics) stack.add(0, expr)
            beginRewrite(expr, depth)
            val result = when (expr) {
                is ConstantScalar -> rewrite(expr)
                is ScalarReference -> rewrite(expr)
                is MatrixReference -> rewrite(expr)
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
                is MatrixVariable -> rewrite(expr)
                is NamedMatrix -> rewrite(expr)
                is SheetRangeExpr -> rewrite(expr)
                is CellSheetRangeExpr -> rewrite(expr)
                is Tiling<*> -> rewrite(expr)
                is ValueExpr<*> -> rewrite(expr)
                is Sheet -> rewrite(expr)
                is ScalarVariable -> rewrite(expr)
                is MatrixVariableElement -> rewrite(expr)
                is ScalarAssign -> rewrite(expr)
                is MatrixAssign -> rewrite(expr)
                is Tableau -> rewrite(expr)
                is RetypeScalar -> rewrite(expr)
                is RetypeMatrix -> rewrite(expr)
                is GroupBy -> rewrite(expr)
                is CellIndexedScalar -> rewrite(expr)
                is ExogenousSheetScalar -> rewrite(expr)
                else -> error("${expr.javaClass}")
            }
            if (enableDiagnostics && checkIdentity && result !== expr) {
                if (result == expr) {
                    if (depth < 200)
                        rewrite(expr)
                    error("Change for no reason")
                }
            }
            if (enableDiagnostics && !allowNameChange && result.hasName() != expr.hasName()) {
                if (depth < 200)
                    rewrite(expr)
                error("Name changed unexpectedly")
            }
            return result
        } finally {
            endRewrite(expr, depth)
            if (enableDiagnostics) stack.removeAt(0)
            --depth
        }
    }

    open operator fun invoke(expr: Expr) = rewrite(expr)
    open operator fun invoke(expr: Sheet) = sheet(expr)
}