package com.github.jomof.kane.visitor

import com.github.jomof.kane.*
import com.github.jomof.kane.functions.*
import com.github.jomof.kane.sheet.*

open class RewritingVisitor(
    val memo: MutableMap<Expr, Expr>? = null
) {
    private var depth = 0
    protected val checkIdentity = false
    open fun rewrite(expr: CoerceScalar): Expr = with(expr) {
        val rewritten = rewrite(value)
        return if (rewritten === value) this
        else copy(value = rewritten)
    }

    open fun rewrite(expr: ScalarStatistic): Expr = expr
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

    open fun rewrite(expr: NamedUntypedScalar): Expr = with(expr) {
        val rewritten = untypedScalar(expr)
        return if (rewritten === expr) this
        else copy(expr = rewritten)
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
        val leftRewritten = rewrite(expr.left) as TypedExpr<Double>
        val rightRewritten = rewrite(expr.right) as TypedExpr<Double>
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
            val (name, expr) = cell
            val rewritten = rewrite(expr)
            changed = changed || (rewritten !== expr)
            rewrittenElements[name] = rewritten
        }
        return if (changed) copy(cells = rewrittenElements.toCells())
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

    open fun untypedScalar(expr: UntypedScalar) = rewrite(expr) as UntypedScalar
    open fun matrix(expr: MatrixExpr) = rewrite(expr) as MatrixExpr
    open fun range(expr: SheetRangeExpr): SheetRangeExpr {
        val result = rewrite(expr)
        assert(result is SheetRangeExpr) {
            rewrite(expr)
            "result '${result.javaClass.simpleName}' was not SheetRangeExpr"
        }
        return result as SheetRangeExpr
    }

    open fun algebraic(expr: AlgebraicExpr) = rewrite(expr) as AlgebraicExpr

    open fun beginRewrite(expr: Expr, depth: Int) {}
    open fun endRewrite(expr: Expr, depth: Int) {}

    open fun rewrite(expr: Expr): Expr {
        try {
            ++depth
            beginRewrite(expr, depth)
            when (expr) {
                is ConstantScalar -> return rewrite(expr)
                is DiscreteUniformRandomVariable -> return rewrite(expr)
                is ScalarStatistic -> return rewrite(expr)
            }
//            if (memo != null) {
//                val lookup = memo[expr]
//                if(lookup != null) {
//                    return lookup
//                }
//            }
            val result = when (expr) {
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
                is DataMatrix -> rewrite(expr)
                is AlgebraicDeferredDataMatrix -> rewrite(expr)
                is NamedScalar -> rewrite(expr)
                is NamedScalarVariable -> rewrite(expr)
                is NamedMatrixVariable -> rewrite(expr)
                is NamedSheetRangeExpr -> rewrite(expr)
                is NamedMatrix -> rewrite(expr)
                is NamedUntypedScalar -> rewrite(expr)
                is SheetRangeExpr -> rewrite(expr)
                is Tiling<*> -> rewrite(expr)
                is ValueExpr<*> -> rewrite(expr)
                is Sheet -> rewrite(expr)
                is ScalarListExpr -> rewrite(expr)
                is ScalarVariable -> rewrite(expr)
                is MatrixVariableElement -> rewrite(expr)
                is NamedScalarAssign -> rewrite(expr)
                is NamedMatrixAssign -> rewrite(expr)
                else -> error("${expr.javaClass}")
            }
            if (checkIdentity && result !== expr) {
                assert(result != expr) {
                    rewrite(expr)
                    "Change for no reason"
                }
            }
//            if (memo!=null && result !== expr) {
//                memo[expr] = result
//            }
            return result
        } finally {
            endRewrite(expr, depth)
            --depth
        }
    }

    open operator fun invoke(expr: Expr) = rewrite(expr)
}