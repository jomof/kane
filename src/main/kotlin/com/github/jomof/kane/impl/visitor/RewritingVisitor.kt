package com.github.jomof.kane.impl.visitor

import com.github.jomof.kane.AlgebraicExpr
import com.github.jomof.kane.Expr
import com.github.jomof.kane.MatrixExpr
import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.impl.*
import com.github.jomof.kane.impl.functions.*
import com.github.jomof.kane.impl.sheet.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

internal open class RewritingVisitor {
    private var depth = 0
    protected val checkIdentity = false
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

    open fun rewrite(expr: ScalarStatistic): Expr = expr
    open fun rewrite(expr: ConstantScalar): Expr = expr
    open fun rewrite(expr: DiscreteUniformRandomVariable): Expr = expr
    open fun rewrite(expr: SheetRangeExpr): Expr = expr
    open fun rewrite(expr: SheetBuilderRange): Expr = expr
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

    open fun rewrite(expr: ScalarListExpr): Expr = with(expr) {
        var changed = false
        val rewrittenElements = mutableListOf<ScalarExpr>()
        for (value in values) {
            val rewritten = scalar(value)
            rewrittenElements += rewritten
            changed = changed || (rewritten !== value)
        }
        return if (changed) copy(values = rewrittenElements)
        else this
    }

    open fun rewrite(expr: AlgebraicUnaryScalar): Expr = with(expr) {
        val rewritten = scalar(value)
        return if (rewritten === value) this
        else copy(value = rewritten)
    }

    open fun rewrite(expr: AlgebraicUnaryScalarStatistic): Expr = with(expr) {
        val rewritten = algebraic(value)
        return if (rewritten === value) this
        else copy(value = rewritten)
    }

    open fun rewrite(expr: AlgebraicUnaryMatrix): Expr = with(expr) {
        val rewritten = matrix(value)
        return if (rewritten === value) this
        else copy(value = rewritten)
    }

    open fun rewrite(expr: AlgebraicUnaryMatrixScalar): Expr = with(expr) {
        val rewritten = matrix(value)
        return if (rewritten === value) this
        else copy(value = rewritten)
    }

    open fun rewrite(expr: AlgebraicBinaryRangeStatistic): Expr = with(expr) {
        val leftRewritten = range(left)
        val rightRewritten = scalar(right)
        if (leftRewritten === left && rightRewritten === right) return this
        return copy(left = leftRewritten, right = rightRewritten)
    }

    open fun rewrite(expr: AlgebraicBinaryMatrix): Expr = with(expr) {
        val leftRewritten = matrix(left)
        val rightRewritten = matrix(right)
        if (leftRewritten === left && rightRewritten === right) return this
        return copy(left = leftRewritten, right = rightRewritten)
    }

    open fun rewrite(expr: AlgebraicBinaryMatrixScalar): Expr = with(expr) {
        val leftRewritten = matrix(left)
        val rightRewritten = scalar(right)
        if (leftRewritten === left && rightRewritten === right) return this
        return copy(left = leftRewritten, right = rightRewritten)
    }

    open fun rewrite(expr: AlgebraicBinaryScalar): Expr = with(expr) {
        val leftRewritten = scalar(left)
        val rightRewritten = scalar(right)
        if (leftRewritten === left && rightRewritten === right) return this
        return copy(left = leftRewritten, right = rightRewritten)
    }

    open fun rewrite(expr: AlgebraicBinaryScalarMatrix): Expr = with(expr) {
        val leftRewritten = scalar(left)
        val rightRewritten = matrix(right)
        if (leftRewritten === left && rightRewritten === right) return this
        return copy(left = leftRewritten, right = rightRewritten)
    }

    open fun rewrite(expr: AlgebraicBinaryScalarStatistic): Expr = with(expr) {
        val leftRewritten = scalar(left)
        val rightRewritten = scalar(right)
        if (leftRewritten === left && rightRewritten === right) return this
        return copy(left = leftRewritten, right = rightRewritten)
    }

    open fun rewrite(expr: Sheet): Expr = with(expr) {
        var changed = false
        val rewrittenElements = mutableMapOf<Id, Expr>()
        for (cell in cells) {
            val (name, cellExpr) = cell
            val rewritten = rewrite(cellExpr)
            changed = changed || (rewritten !== cellExpr)
            rewrittenElements[name] = rewritten
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

    open fun sheet(expr: Sheet) = rewrite(expr) as Sheet
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
                is ScalarStatistic -> rewrite(expr)
                is AlgebraicBinaryMatrix -> rewrite(expr)
                is AlgebraicBinaryMatrixScalar -> rewrite(expr)
                is AlgebraicBinaryRangeStatistic -> rewrite(expr)
                is AlgebraicBinaryScalar -> rewrite(expr)
                is AlgebraicBinaryScalarMatrix -> rewrite(expr)
                is AlgebraicBinaryScalarStatistic -> rewrite(expr)
                is AlgebraicUnaryMatrix -> rewrite(expr)
                is AlgebraicUnaryMatrixScalar -> rewrite(expr)
                is AlgebraicUnaryScalar -> rewrite(expr)
                is AlgebraicUnaryScalarStatistic -> rewrite(expr)
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
                is ScalarListExpr -> rewrite(expr)
                is ScalarVariable -> rewrite(expr)
                is MatrixVariableElement -> rewrite(expr)
                is NamedScalarAssign -> rewrite(expr)
                is NamedMatrixAssign -> rewrite(expr)
                is Tableau -> rewrite(expr)
                is SheetBuilderRange -> rewrite(expr)
                is RetypeScalar -> rewrite(expr)
                is RetypeMatrix -> rewrite(expr)
                is GroupBy -> rewrite(expr)
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
}