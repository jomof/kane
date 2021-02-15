package com.github.jomof.kane.impl

import com.github.jomof.kane.*
import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.get
import com.github.jomof.kane.impl.ComputableIndex.MoveableIndex
import com.github.jomof.kane.impl.functions.*
import com.github.jomof.kane.impl.sheet.*
import com.github.jomof.kane.impl.types.algebraicType
import com.github.jomof.kane.impl.types.kaneDouble
import com.github.jomof.kane.impl.visitor.RewritingVisitor
import com.github.jomof.kane.impl.visitor.SheetRewritingVisitor
import com.github.jomof.kane.map
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

private val reduceNamedExprs = object : RewritingVisitor(allowNameChange = true, checkIdentity = true) {
    override fun rewrite(expr: Expr): Expr {
        val result = super.rewrite(expr)
        if (result is NamedScalarVariable) return result
        if (result is NamedMatrixVariable) return result
        if (result is NamedScalarAssign) return result
        if (result is NamedMatrixAssign) return result
        return result.toUnnamed()
    }
}

private class ReduceAlgebraicBinaryScalar : RewritingVisitor(allowNameChange = true, checkIdentity = true) {
    override fun rewrite(expr: AlgebraicBinaryScalarScalarScalar): Expr {
        return when (val sub = super.rewrite(expr)) {
            is AlgebraicBinaryScalarScalarScalar -> {
                val type = sub.op.type(sub.left.algebraicType, sub.right.algebraicType)
                val reduced = sub.op.reduceArithmetic(sub.left, sub.right)
                val typed =
                    if (reduced != null && type != kaneDouble)
                        RetypeScalar(reduced, type)
                    else reduced
                return (typed ?: sub)
            }
            else -> sub
        }
    }
}

private class ReduceAlgebraicUnaryScalar : RewritingVisitor(checkIdentity = true) {
    override fun rewrite(expr: AlgebraicUnaryScalarScalar): Expr {
        return when (val sub = super.rewrite(expr)) {
            is AlgebraicUnaryScalarScalar -> when (sub.value) {
                is RetypeScalar -> {
                    val reduced = sub.op.reduceArithmetic(sub.value)
                    if (reduced == null) sub
                    else sub.value.copy(scalar = reduced)
                }
                else -> sub.op.reduceArithmetic(sub.value) ?: sub
            }
            else -> sub
        }
    }

    override fun rewrite(expr: RetypeScalar): Expr {
        return when (val scalar = scalar(expr.scalar)) {
            is CoerceScalar -> when (scalar.value) {
                is SheetRangeExpr ->
                    expr
                else ->
                    error("${scalar.value.javaClass}")
            }
            is ConstantScalar ->
                if (expr.scalar === scalar) return expr
                else expr.copy(scalar = scalar)
            else ->
                if (scalar === expr.scalar)
                    expr
                else
                    expr.copy(scalar = scalar)
        }
    }
}

private val reduceAlgebraicBinaryMatrix = object : SheetRewritingVisitor() {
    override fun rewrite(expr: AlgebraicBinaryMatrixMatrixMatrix): Expr = with(expr) {
        val (columns, rows) = tryGetDimensions(rowCount())
        if (columns == null || rows == null) error("Could not get column or row count")
        val left = left.toDataMatrix(columns, rows)
        val right = right.toDataMatrix(columns, rows)
        assert(left.columns == right.columns)
        assert(left.rows == right.rows)
        return DataMatrix(columns, rows, (left.elements zip right.elements).map { (l, r) ->
            AlgebraicBinaryScalarScalarScalar(op as AlgebraicBinaryFunction, l, r)
        })
    }
}
private val reduceAlgebraicUnaryMatrix = object : RewritingVisitor() {
    override fun rewrite(expr: AlgebraicUnaryMatrixMatrix) = expr.value.map {
        AlgebraicUnaryScalarScalar(expr.op as IAlgebraicUnaryScalarScalarFunction, it)
    }
}
private val reduceAlgebraicUnaryScalarStatistic = object : RewritingVisitor() {
    override fun rewrite(expr: AlgebraicSummaryScalarScalar): Expr {
        return expr.op.reduceArithmetic(expr.value)?.withNameOf(expr) ?: super.rewrite(expr)
    }

    override fun rewrite(expr: AlgebraicSummaryMatrixScalar): Expr = with(expr) {
        return op.reduceArithmetic(value)?.withNameOf(expr) ?: super.rewrite(expr)
    }
}

private val reduceAlgebraicBinaryScalarStatistic = object : RewritingVisitor() {
    override fun rewrite(expr: AlgebraicBinarySummaryScalarScalarScalar): Expr = with(expr) {

        return when {
            left is StreamingSampleStatisticExpr -> op.reduceArithmetic(left, right)!!
            left is NamedScalar && left.scalar is StreamingSampleStatisticExpr -> op.reduceArithmetic(
                left.scalar,
                right
            )!!
            left is RetypeScalar && left.scalar is StreamingSampleStatisticExpr -> {
                val result = left.copy(scalar = op.reduceArithmetic(left.scalar, right)!!)
                result
            }
            else ->
                error("${left.javaClass}")
        }
    }

    override fun rewrite(expr: AlgebraicBinarySummaryMatrixScalarScalar): Expr = with(expr) {

        return when {
            left is DataMatrix -> expr.op.reduceArithmetic(expr.left, expr.right) ?: expr
            else ->
                error("${left.javaClass}")
        }
    }
}

private val reduceNakedScalarStatistic = object : RewritingVisitor() {
    override fun rewrite(expr: StreamingSampleStatisticExpr) =
        constant(expr.statistic.median).withNameOf(expr)
}

private class ReduceRandomVariables(
    val variables: Map<RandomVariableExpr, ConstantScalar>
) :
    RewritingVisitor() {
    override fun rewrite(expr: DiscreteUniformRandomVariable) =
        variables[expr] ?: expr

    override fun rewrite(expr: AlgebraicBinaryScalarScalarScalar): Expr = with(expr) {
        val leftRewritten = scalar(left)
        val rightRewritten = scalar(right)
        if (leftRewritten === left && rightRewritten === right) return this
        val type = expr.op.type(expr.left.algebraicType, expr.right.algebraicType)
        val reduced = expr.op.reduceArithmetic(leftRewritten, rightRewritten)
        val typed =
            if (reduced != null && type != kaneDouble) RetypeScalar(reduced, type)
            else reduced
        return (typed ?: dup(left = leftRewritten, right = rightRewritten)).withNameOf(expr)
    }

    override fun rewrite(expr: AlgebraicUnaryScalarScalar): Expr = with(expr) {
        val rewritten = scalar(value)
        if (rewritten === value) return this
        val reduced = op.reduceArithmetic(rewritten)
        val type = expr.algebraicType
        val typed =
            if (reduced != null && type != kaneDouble) RetypeScalar(reduced, type)
            else reduced
        return typed ?: dup(value = rewritten)
    }
}

private class ReduceNamedVariables(
    val reduceVariables: Boolean,
    val excludeVariables: Set<Id>,
    val namedVariableLookup: Cells
) : RewritingVisitor(allowNameChange = true) {
    private var recursionDetected = false

    override fun rewrite(expr: NamedMatrix): Expr = with(expr) {
        if (excludeVariables.contains(expr.name)) return expr
        return matrix(matrix)
    }

    override fun rewrite(expr: NamedScalar): Expr {
        if (excludeVariables.contains(expr.name)) return expr
        val lookup = namedVariableLookup[expr.name] ?: return expr
        if (lookup is ScalarVariable) return constant(lookup.initial)
        return lookup
    }

    override fun rewrite(expr: NamedScalarVariable): Expr {
        if (!reduceVariables) return expr
        if (excludeVariables.contains(expr.name)) return expr
        val lookup = namedVariableLookup[expr.name] ?: return constant(expr.initial)
        if (lookup is ScalarVariable) return constant(lookup.initial)
        return lookup
    }

    override fun rewrite(expr: MatrixVariableElement): Expr {
        if (!reduceVariables) return expr
        return constant(expr.matrix[expr.column, expr.row].initial)
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
        return StreamingSampleStatisticExpr(stream).withNameOf(expr)
    }
}

private val reduceCoerceScalar = object : RewritingVisitor() {
    override fun rewrite(expr: CoerceScalar): Expr {
        return when (expr.value) {
            is SheetRangeExpr -> expr
            else -> error("${expr.value.javaClass}")
        }
    }
}

private val reduceRetypeOfRetype = object : RewritingVisitor() {
    override fun rewrite(expr: RetypeScalar): Expr {
        if (expr.scalar is RetypeScalar)
            return RetypeScalar(scalar = expr.scalar.scalar, type = expr.type)
        return super.rewrite(expr)
    }
}

private val expandExogenousSheetCells = object : RewritingVisitor() {
    override fun rewrite(expr: ExogenousSheetScalar): Expr {
        return expr.sheet.cells.getValue(expr.lookup)
    }
}


private fun Expr.expandSheetCells(sheet: Sheet, excludeVariables: Set<Id>): Expr {
    return object : RewritingVisitor() {
        override fun rewrite(expr: AlgebraicBinarySummaryMatrixScalarScalar): Expr = with(expr) {
            val leftRewritten = rewrite(left)
            val rightRewritten = scalar(right)
            if (leftRewritten === left && rightRewritten == right) return this
            return when (leftRewritten) {
                is ScalarExpr -> AlgebraicBinarySummaryScalarScalarScalar(
                    expr.op as AlgebraicBinarySummaryFunction,
                    left = leftRewritten,
                    right = right
                )
                is DataMatrix -> dup(left = leftRewritten, right = rightRewritten)
                else ->
                    error("${leftRewritten.javaClass}")
            }
        }

        override fun rewrite(expr: AlgebraicBinaryScalarScalarScalar): Expr = with(expr) {
            val leftRewritten = scalar(left)
            val rightRewritten = scalar(right)
            if (leftRewritten === left && rightRewritten === right) return this
            val op = dup(
                left = leftRewritten,
                right = rightRewritten
            )
            val newImpliedType = expr.op.type(leftRewritten.algebraicType, rightRewritten.algebraicType)
            if (newImpliedType == kaneDouble) return op
            return RetypeScalar(op, newImpliedType)
        }

        override fun rewrite(expr: CoerceScalar): Expr = with(expr) {
            val rewritten = rewrite(value)
            if (rewritten === value) return this
            return rewritten
        }

        override fun rewrite(expr: CoerceMatrix): Expr = with(expr) {
            val rewritten = rewrite(value)
            if (rewritten === value) return this
            return when (rewritten) {
                is ScalarExpr -> DataMatrix(1, 1, listOf(rewritten))
                is DataMatrix -> rewritten
                else ->
                    error("${rewritten.javaClass}")
            }
        }

        override fun rewrite(expr: SheetRangeExpr): Expr = with(expr) {
            if (expr.rangeRef is CellRangeRef && excludeVariables.contains(expr.rangeRef.toCoordinate()))
                return this
            if (expr.rangeRef is CellRangeRef && expr.rangeRef.column is MoveableIndex && expr.rangeRef.row is MoveableIndex) {
                val coordinate = expr.rangeRef.toCoordinate()
                return when (val result = sheet.cells[coordinate]) {
                    null -> expr
                    // is ScalarExpr -> return result
                    is ConstantScalar -> result
                    is ScalarVariable -> constant(result.initial)
                    is RetypeScalar -> if (result.canGetConstant()) result else this
                    else -> this
                }
            }
            val list = sheet.cells.toMap().filter { rangeRef.contains(it.key) }.map {
                val result = when (val value = it.value) {
                    is ConstantScalar -> value
                    is ScalarVariable -> constant(value.initial)
                    else ->
                        return this
                }
                result
            }
            if (list.isEmpty()) return this
            return DataMatrix(list.size, 1, list)

        }
    }.rewrite(this)
}

private class ReduceProvidedSheetRangeExprs(val rangeExprProvider: RangeExprProvider) :
    RewritingVisitor() {
    override fun rewrite(expr: SheetRangeExpr): Expr {
        return rangeExprProvider.range(expr).withNameOf(expr)
    }

}

private class ExpandSheetCells(val excludeVariables: Set<Id>) : RewritingVisitor() {
    override fun rewrite(expr: Sheet): Expr = with(expr) {
        var changed = false
        val rewrittenElements = mutableMapOf<Id, Expr>()
        for (cell in cells) {
            val (name, cellExpr) = cell
            val rewritten = cellExpr.expandSheetCells(this, excludeVariables)
            changed = changed || (rewritten !== cellExpr)
            rewrittenElements[name] = rewritten
        }
        return if (changed) copy(cells = rewrittenElements.toCells())
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
        this is StreamingSampleStatisticExpr && incoming is ConstantScalar -> {
            statistic.insert(incoming.value)
        }
        this is AlgebraicSummaryScalarScalar && incoming is AlgebraicSummaryScalarScalar -> {
            value.accumulateStatistics(incoming.value)
        }
        this is AlgebraicSummaryMatrixScalar && incoming is AlgebraicSummaryMatrixScalar -> {
            value.accumulateStatistics(incoming.value)
        }
        this is RetypeScalar && incoming is RetypeScalar -> {
            this.scalar.accumulateStatistics(incoming.scalar)
        }
        this is AlgebraicUnaryScalarScalar && incoming is AlgebraicUnaryScalarScalar -> {
            this.value.accumulateStatistics(incoming.value)
        }
        this is AlgebraicBinaryScalarScalarScalar && incoming is AlgebraicBinaryScalarScalarScalar -> {
            this.left.accumulateStatistics(incoming.left)
            this.right.accumulateStatistics(incoming.right)
        }
        this is AlgebraicBinarySummaryScalarScalarScalar && incoming is AlgebraicBinarySummaryScalarScalarScalar -> {
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
        this is NamedScalarVariable && incoming is NamedScalarVariable -> {
        }
        this is CoerceScalar && incoming is CoerceScalar -> value.accumulateStatistics(incoming.value)
        this is DataMatrix && incoming is DataMatrix -> {
            (elements zip incoming.elements).forEach { (left, incoming) -> left.accumulateStatistics(incoming) }
        }
        this is ValueExpr<*> && incoming is ValueExpr<*> -> {
        }
        else ->
            error("$javaClass : ${incoming.javaClass}")
    }
}

private class NopRangeExprProvider : RangeExprProvider

private fun Expr.evalGradualSingleSample(
    rangeExprProvider: RangeExprProvider,
    reduceVariables: Boolean,
    excludeVariables: Set<Id>,
    randomVariableValues: Map<RandomVariableExpr, ConstantScalar>
): Expr {
    excludeVariables.forEach { Identifier.validate(it) }
    var last = this.makeDereferenced()
    var allowedReductions = 10000
    val reduceRandomVariables = ReduceRandomVariables(randomVariableValues)
    val expandSheetCells = ExpandSheetCells(excludeVariables)
    val reduceProvidedSheetRangeExprs = ReduceProvidedSheetRangeExprs(rangeExprProvider)
    val reduceAlgebraicBinaryScalar = ReduceAlgebraicBinaryScalar()
    val reduceAlgebraicUnaryScalar = ReduceAlgebraicUnaryScalar()
    while (true) {
        if (--allowedReductions == 0) error("Reduction took too long")
        val namedVariableLookup = if (last is Sheet) last.cells else Cells(mapOf())
        val reduceNamedVariables = ReduceNamedVariables(reduceVariables, excludeVariables, namedVariableLookup)
        val reducedNamedVariables = reduceNamedVariables.rewriteOrNull(last) ?: return this
        val reducedNamedExprs = reduceNamedExprs(reducedNamedVariables)
        val reducedRandomVariables = reduceRandomVariables(reducedNamedExprs)
        val reducedAlgebraicUnaryMatrix = reduceAlgebraicUnaryMatrix(reducedRandomVariables)
        val reducedAlgebraicBinaryMatrix = reduceAlgebraicBinaryMatrix(reducedAlgebraicUnaryMatrix)
        val reducedAlgebraicUnaryScalar = reduceAlgebraicUnaryScalar(reducedAlgebraicBinaryMatrix)
        val reducedAlgebraicBinaryScalar = reduceAlgebraicBinaryScalar(reducedAlgebraicUnaryScalar)
        val reducedProvidedSheetRangeExprs = reduceProvidedSheetRangeExprs(reducedAlgebraicBinaryScalar)
        val expandedSheetCells = expandSheetCells(reducedProvidedSheetRangeExprs)
        val reducedRetypeOfRetype = reduceRetypeOfRetype(expandedSheetCells)
        val expandedExogenousSheetCells = expandExogenousSheetCells(reducedRetypeOfRetype)
        val reducedCoerceScalar = reduceCoerceScalar(expandedExogenousSheetCells)

        if (reducedCoerceScalar === last) {
            if (last is CoerceMatrix) last = last.value
            if (hasName()) {
                last = last.toUnnamed().toNamed(name)
            }
            return last.makeReferenced()
        }
        last = reducedCoerceScalar
    }
}

private fun Expr.evalGradualReduceStatistics(
    rangeExprProvider: RangeExprProvider,
    reduceVariables: Boolean,
    excludeVariables: Set<Id>
): Expr {
    var last = this.makeDereferenced()
    var allowedReductions = 10000
    while (true) {
        if (--allowedReductions == 0) error("Reduction took too long")
        val reduced = last.evalGradualSingleSample(rangeExprProvider, reduceVariables, excludeVariables, mapOf())
        val reducedAlgebraicBinaryScalarStatistic = reduceAlgebraicBinaryScalarStatistic(reduced)
        val reducedAlgebraicUnaryScalarStatistic =
            reduceAlgebraicUnaryScalarStatistic(reducedAlgebraicBinaryScalarStatistic)
        val reducedNakedScalarStatistic = reduceNakedScalarStatistic(reducedAlgebraicUnaryScalarStatistic)
        if (reducedNakedScalarStatistic == last) {
            return last.makeReferenced()
        }
        last = reducedNakedScalarStatistic
    }
}

internal fun Expr.evalImpl(
    rangeExprProviderOrNull: RangeExprProvider?,
    reduceVariables: Boolean,
    excludeVariables: Set<Id>
): Expr {
    val rangeExprProvider = rangeExprProviderOrNull ?: NopRangeExprProvider()
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
        (variable as DiscreteUniformRandomVariable).values.map { value -> constant(value) }
    }

    val reduced = evalGradualSingleSample(
        rangeExprProvider,
        reduceVariables,
        excludeVariables,
        mapOf()
    )
//    val model = reduced.linearize()
    cartesianOf(randomVariableElements) { randomVariableValues ->
        val variableValues: Map<RandomVariableExpr, ConstantScalar> = (randomVariables zip randomVariableValues)
            .map { (variable, value) -> variable to (value as ConstantScalar) }
            .toMap()
        val sample =
            reduced.evalGradualSingleSample(rangeExprProvider, reduceVariables, excludeVariables, variableValues)
        if (stats == null) {
            stats = convertVariablesToStatistics(sample)
        } else {
            stats!!.accumulateStatistics(sample)
        }
    }
    return stats!!.evalGradualReduceStatistics(rangeExprProvider, reduceVariables, excludeVariables)
}