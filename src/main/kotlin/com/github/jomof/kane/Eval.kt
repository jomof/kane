package com.github.jomof.kane

import com.github.jomof.kane.functions.*
import com.github.jomof.kane.sheet.CoerceScalar
import com.github.jomof.kane.sheet.RangeExprProvider
import com.github.jomof.kane.sheet.Sheet
import com.github.jomof.kane.sheet.SheetRangeExpr
import com.github.jomof.kane.visitor.RewritingVisitor

private val reduceNamedMatrix = object : RewritingVisitor() {
    override fun rewrite(expr: NamedMatrix) = expr.matrix
}
private val reduceAlgebraicBinaryScalar = object : RewritingVisitor() {
    override fun rewrite(expr: AlgebraicBinaryScalar): Expr {
        return when (val sub = super.rewrite(expr)) {
            is AlgebraicBinaryScalar -> when {
                sub.left is ScalarListExpr && sub.right is ScalarListExpr ->
                    ScalarListExpr((sub.left.values zip sub.right.values).map { (l, r) ->
                        sub.copy(
                            left = l,
                            right = r
                        )
                    }, sub.type)
                sub.left is ScalarListExpr -> sub.left.copy(sub.left.values.map { sub.copy(left = it) })
                sub.right is ScalarListExpr -> sub.right.copy(sub.right.values.map { sub.copy(right = it) })
                else -> sub.op.reduceArithmetic(sub.left, sub.right) ?: sub
            }
            else -> sub
        }
    }
}
private val reduceAlgebraicUnaryScalar = object : RewritingVisitor() {
    override fun rewrite(expr: AlgebraicUnaryScalar): Expr {
        return when (val sub = super.rewrite(expr)) {
            is AlgebraicUnaryScalar -> when (sub.value) {
                is ScalarListExpr -> sub.value.copy(sub.value.values.map { sub.copy(it) })
                else -> sub.op.reduceArithmetic(sub.value) ?: sub
            }
            else -> sub
        }
    }
}
private val reduceAlgebraicBinaryMatrix = object : RewritingVisitor() {
    override fun rewrite(expr: AlgebraicBinaryMatrix): Expr = with(expr) {
        val left = left.toDataMatrix()
        val right = right.toDataMatrix()
        assert(left.columns == right.columns)
        assert(left.rows == right.rows)
        return DataMatrix(left.columns, left.rows, (left.elements zip right.elements).map { (l, r) ->
            AlgebraicBinaryScalar(op, l, r, op.type(l.type, r.type))
        })
    }
}
private val reduceAlgebraicUnaryMatrix = object : RewritingVisitor() {
    override fun rewrite(expr: AlgebraicUnaryMatrix) = expr.value.map {
        AlgebraicUnaryScalar(expr.op, it, expr.value.type)
    }
}
private val reduceAlgebraicUnaryScalarStatistic = object : RewritingVisitor() {
    override fun rewrite(expr: AlgebraicUnaryScalarStatistic): Expr = with(expr) {
        return op.reduceArithmetic(value) ?: super.rewrite(expr)
    }
}

private val reduceAlgebraicBinaryScalarStatistic = object : RewritingVisitor() {
    override fun rewrite(expr: AlgebraicBinaryScalarStatistic): Expr = with(expr) {

        return when {
            left is ScalarStatistic -> op.reduceArithmetic(left, right)
            left is ScalarListExpr -> op.reduceArithmetic(left.values, right) ?: expr
            left is NamedScalar && left.scalar is ScalarStatistic -> op.reduceArithmetic(left.scalar, right)
            else ->
                error("${left.javaClass}")
        }
    }
}

private val reduceNakedScalarStatistic = object : RewritingVisitor() {
    override fun rewrite(expr: ScalarStatistic) =
        constant(expr.statistic.median, expr.type)
}

private class ReduceRandomVariables(val variables: Map<RandomVariableExpr, ConstantScalar>) :
    RewritingVisitor() {
    override fun rewrite(expr: DiscreteUniformRandomVariable) = variables[expr] ?: expr
}

private class ReduceNamedVariables(
    val reduceVariables: Boolean,
    val excludeVariables: Set<String>,
    val namedVariableLookup: Map<String, Expr>
) : RewritingVisitor() {
    private var recursionDetected = false
    override fun rewrite(expr: NamedScalar): Expr {
        if (excludeVariables.contains(expr.name)) return expr
        val lookup = namedVariableLookup[expr.name] ?: return expr.scalar
        if (lookup is ScalarVariable) return constant(lookup.initial, expr.type)
        return lookup
    }

    override fun rewrite(expr: NamedScalarVariable): Expr {
        if (!reduceVariables) return expr
        if (excludeVariables.contains(expr.name)) return expr
        val lookup = namedVariableLookup[expr.name] ?: return constant(expr.initial, expr.type)
        if (lookup is ScalarVariable) return constant(lookup.initial, expr.type)
        return lookup
    }

    override fun rewrite(expr: MatrixVariableElement): Expr {
        if (!reduceVariables) return expr
        return constant(expr.matrix[expr.column, expr.row].initial, expr.type)
    }

    override fun beginRewrite(expr: Expr, depth: Int) {
        if (depth > 1000) {
            recursionDetected = true
            error("Recursive expression through '$expr'")
        }
    }

    fun rewriteOrNull(expr: Expr): Expr? {
        return try {
            rewrite(expr)
        } catch (e: Throwable) {
            if (recursionDetected) null
            else throw(e)
        }
    }
}

private val convertVariablesToStatistics = object : RewritingVisitor() {
    override fun rewrite(expr: ConstantScalar): Expr {
        val stream = StreamingSamples()
        stream.insert(expr.value)
        return ScalarStatistic(stream, expr.type)
    }
}

private val reduceCoerceScalar = object : RewritingVisitor() {
    override fun rewrite(expr: CoerceScalar): Expr {
        return when (expr.value) {
            is Sheet -> ScalarListExpr(expr.value.cells.map { expr.copy(value = it.value) }, expr.type)
            is SheetRangeExpr -> expr
            else -> error("${expr.value.javaClass}")
        }
    }
}

private fun Expr.expandSheetCells(sheet: Sheet, excludeVariables: Set<String>): Expr {
    return object : RewritingVisitor() {
        override fun rewrite(expr: AlgebraicBinaryRangeStatistic): Expr = with(expr) {
            val leftRewritten = rewrite(left)
            val rightRewritten = scalar(right)
            if (leftRewritten === left && rightRewritten == right) return this
            return when (leftRewritten) {
                is SheetRangeExpr -> copy(left = leftRewritten, right = rightRewritten)
                is ScalarExpr -> AlgebraicBinaryScalarStatistic(expr.op, left = leftRewritten, right = right)
                else -> error("${leftRewritten.javaClass}")
            }
        }

        override fun rewrite(expr: SheetRangeExpr): Expr = with(expr) {
            if (excludeVariables.contains(expr.range.toString()))
                return this
            val list = sheet.cells.filter { range.contains(it.key) }.map {
                when (val value = it.value) {
                    is ScalarExpr -> value
                    is ScalarVariable -> constant(value.initial, value.type)
                    else -> return this
                }
            }
            if (list.isEmpty()) return this
            if (list.size == 1) return list.first()
            return ScalarListExpr(list, list.first().type)

        }
    }.rewrite(this)
}

private class ReduceProvidedSheetRangeExprs(val rangeExprProvider: RangeExprProvider) :
    RewritingVisitor() {
    override fun rewrite(expr: SheetRangeExpr) =
        rangeExprProvider.range(expr)
}

private class ExpandSheetCells(val excludeVariables: Set<String>) : RewritingVisitor() {
    override fun rewrite(expr: Sheet): Expr = with(expr) {
        var changed = false
        val rewrittenElements = mutableMapOf<String, Expr>()
        for (cell in cells) {
            val (name, expr) = cell
            val rewritten = expr.expandSheetCells(this, excludeVariables)
            changed = changed || (rewritten !== expr)
            rewrittenElements[name] = rewritten
        }
        return if (changed) copy(cells = rewrittenElements)
        else this
    }
}

private fun Expr.accumulateStatistics(incoming: Expr) {
    when {
        this is NamedScalar && incoming is NamedScalar -> {
            assert(name == incoming.name)
            scalar.accumulateStatistics(incoming.scalar)
        }
        this is NamedMatrix && incoming is NamedMatrix -> {
            assert(name == incoming.name)
            matrix.accumulateStatistics(incoming.matrix)
        }
        this is ScalarStatistic && incoming is ConstantScalar -> {
            statistic.insert(incoming.value)
        }
        this is AlgebraicUnaryScalarStatistic && incoming is AlgebraicUnaryScalarStatistic -> {
            value.accumulateStatistics(incoming.value)
        }
        this is AlgebraicUnaryScalar && incoming is AlgebraicUnaryScalar -> {
            this.value.accumulateStatistics(incoming.value)
        }
        this is AlgebraicBinaryScalar && incoming is AlgebraicBinaryScalar -> {
            this.left.accumulateStatistics(incoming.left)
            this.right.accumulateStatistics(incoming.right)
        }
        this is AlgebraicBinaryScalarStatistic && incoming is AlgebraicBinaryScalarStatistic -> {
            this.left.accumulateStatistics(incoming.left)
            this.right.accumulateStatistics(incoming.right)
        }
        this is Sheet && incoming is Sheet -> {
            cells.forEach { (name, expr) ->
                expr.accumulateStatistics(incoming.cells.getValue(name))
            }
        }
        this is SheetRangeExpr && incoming is SheetRangeExpr -> {
        }
        this is CoerceScalar && incoming is CoerceScalar -> value.accumulateStatistics(incoming.value)
        this is DataMatrix && incoming is DataMatrix -> {
            (elements zip incoming.elements).forEach { (left, incoming) -> left.accumulateStatistics(incoming) }
        }
        this is ValueExpr<*> && incoming is ValueExpr<*> -> {
        }
        this is ScalarListExpr && incoming is ScalarListExpr -> {
            (values zip incoming.values).forEach { (left, incoming) -> left.accumulateStatistics(incoming) }
        }
        else ->
            error("$javaClass : ${incoming.javaClass}")
    }
}

private class NopRangeExprProvider : RangeExprProvider

private fun Expr.evalGradualSingleSample(
    rangeExprProvider: RangeExprProvider,
    reduceVariables: Boolean,
    excludeVariables: Set<String>,
    randomVariableValues: Map<RandomVariableExpr, ConstantScalar>
): Expr {
    var last = this
    var allowedReductions = 10000
    val reduceRandomVariables = ReduceRandomVariables(randomVariableValues)
    val expandSheetCells = ExpandSheetCells(excludeVariables)
    val reduceProvidedSheetRangeExprs = ReduceProvidedSheetRangeExprs(rangeExprProvider)
    while (true) {
        if (--allowedReductions == 0) error("Reduction took too long")
        val namedVariableLookup = if (last is Sheet) last.cells else mapOf()
        val reduceNamedVariables = ReduceNamedVariables(reduceVariables, excludeVariables, namedVariableLookup)
        val reducedNamedVariables = reduceNamedVariables.rewriteOrNull(last) ?: return this
        val reducedNamedMatrix = reduceNamedMatrix(reducedNamedVariables)
        val reducedRandomVariables = reduceRandomVariables(reducedNamedMatrix)
        val reducedAlgebraicUnaryMatrix = reduceAlgebraicUnaryMatrix(reducedRandomVariables)
        val reducedAlgebraicBinaryMatrix = reduceAlgebraicBinaryMatrix(reducedAlgebraicUnaryMatrix)
        val reducedAlgebraicUnaryScalar = reduceAlgebraicUnaryScalar(reducedAlgebraicBinaryMatrix)
        val reducedAlgebraicBinaryScalar = reduceAlgebraicBinaryScalar(reducedAlgebraicUnaryScalar)
        val reducedProvidedSheetRangeExprs = reduceProvidedSheetRangeExprs(reducedAlgebraicBinaryScalar)
        val expandedSheetCells = expandSheetCells(reducedProvidedSheetRangeExprs)
        val reducedCoerceScalar = reduceCoerceScalar(expandedSheetCells)

        if (reducedCoerceScalar === last) {
            if (this is NamedExpr && reducedCoerceScalar !is NamedExpr) {
                return reducedCoerceScalar.toNamed(name)
            }
            return reducedCoerceScalar
        }
        last = reducedCoerceScalar
    }
}

private fun Expr.evalGradualReduceStatistics(
    rangeExprProvider: RangeExprProvider,
    reduceVariables: Boolean,
    excludeVariables: Set<String>
): Expr {
    var last = this
    var allowedReductions = 10000
    while (true) {
        if (--allowedReductions == 0) error("Reduction took too long")
        val reduced = last.evalGradualSingleSample(rangeExprProvider, reduceVariables, excludeVariables, mapOf())
        val reducedAlgebraicBinaryScalarStatistic = reduceAlgebraicBinaryScalarStatistic(reduced)
        val reducedAlgebraicUnaryScalarStatistic =
            reduceAlgebraicUnaryScalarStatistic(reducedAlgebraicBinaryScalarStatistic)
        val reducedNakedScalarStatistic = reduceNakedScalarStatistic(reducedAlgebraicUnaryScalarStatistic)
        if (reducedNakedScalarStatistic == last) {
            return last
        }
        last = reducedNakedScalarStatistic
    }
}

fun Expr.eval(
    rangeExprProvider: RangeExprProvider = NopRangeExprProvider(),
    reduceVariables: Boolean = false,
    excludeVariables: Set<String> = setOf()
): Expr {
    if (excludeVariables.isNotEmpty()) {
        assert(reduceVariables) {
            "Variable exclusion without reducing variables"
        }
    }
    val randomVariables = findRandomVariables()
    if (randomVariables.isEmpty()) return evalGradualReduceStatistics(
        rangeExprProvider,
        reduceVariables,
        excludeVariables
    )
    var stats: Expr? = null
    val randomVariableElements = randomVariables.map { variable ->
        (variable as DiscreteUniformRandomVariable).values.map { value ->
            constant(value, variable.type)
        }
    }
    cartesianOf(randomVariableElements) { randomVariableValues ->
        val variableValues: Map<RandomVariableExpr, ConstantScalar> = (randomVariables zip randomVariableValues)
            .map { (variable, value) -> variable to (value as ConstantScalar) }
            .toMap()
        val sample = evalGradualSingleSample(rangeExprProvider, reduceVariables, excludeVariables, variableValues)
        if (stats == null) {
            stats = convertVariablesToStatistics(sample)
        } else {
            stats!!.accumulateStatistics(sample)
        }
    }
    return stats!!.evalGradualReduceStatistics(rangeExprProvider, reduceVariables, excludeVariables)
}

fun NamedMatrix.eval(
    rangeExprProvider: RangeExprProvider = NopRangeExprProvider(),
    reduceVariables: Boolean = false,
    excludeVariables: Set<String> = setOf()
) =
    (this as Expr).eval(rangeExprProvider, reduceVariables, excludeVariables) as NamedMatrix

fun NamedScalar.eval(
    rangeExprProvider: RangeExprProvider = NopRangeExprProvider(),
    reduceVariables: Boolean = false,
    excludeVariables: Set<String> = setOf()
) =
    (this as Expr).eval(rangeExprProvider, reduceVariables, excludeVariables) as NamedScalar

fun NamedAlgebraicExpr.eval(
    rangeExprProvider: RangeExprProvider = NopRangeExprProvider(),
    reduceVariables: Boolean = false,
    excludeVariables: Set<String> = setOf()
) =
    (this as Expr).eval(rangeExprProvider, reduceVariables, excludeVariables) as NamedAlgebraicExpr

fun Sheet.eval(
    rangeExprProvider: RangeExprProvider = NopRangeExprProvider(),
    reduceVariables: Boolean = false,
    excludeVariables: Set<String> = setOf()
) =
    (this as Expr).eval(rangeExprProvider, reduceVariables, excludeVariables) as Sheet

fun ScalarExpr.eval(
    rangeExprProvider: RangeExprProvider = NopRangeExprProvider(),
    reduceVariables: Boolean = false,
    excludeVariables: Set<String> = setOf()
) =
    (this as Expr).eval(rangeExprProvider, reduceVariables, excludeVariables) as ScalarExpr

fun MatrixExpr.eval(
    rangeExprProvider: RangeExprProvider = NopRangeExprProvider(),
    reduceVariables: Boolean = false,
    excludeVariables: Set<String> = setOf()
) =
    (this as Expr).eval(rangeExprProvider, reduceVariables, excludeVariables) as MatrixExpr