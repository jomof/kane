package com.github.jomof.kane

import com.github.jomof.kane.ComputableIndex.MoveableIndex
import com.github.jomof.kane.functions.*
import com.github.jomof.kane.sheet.*
import com.github.jomof.kane.types.algebraicType
import com.github.jomof.kane.types.kaneDouble
import com.github.jomof.kane.visitor.RewritingVisitor

private val reduceNamedMatrix = object : RewritingVisitor() {
    override fun rewrite(expr: NamedMatrix) = expr.matrix
}

private class ReduceAlgebraicBinaryScalar : RewritingVisitor() {
    override fun rewrite(expr: AlgebraicBinaryScalar): Expr {
        return when (val sub = super.rewrite(expr)) {
            is AlgebraicBinaryScalar -> when {
                sub.left is ScalarListExpr && sub.right is ScalarListExpr ->
                    ScalarListExpr((sub.left.values zip sub.right.values).map { (l, r) ->
                        sub.copy(
                            left = l,
                            right = r
                        )
                    })
                sub.left is ScalarListExpr -> sub.left.copy(sub.left.values.map { sub.copy(left = it) })
                sub.right is ScalarListExpr -> sub.right.copy(sub.right.values.map { sub.copy(right = it) })
                else -> {
                    val type = sub.op.type(sub.left.algebraicType, sub.right.algebraicType)
                    val reduced = sub.op.reduceArithmetic(sub.left, sub.right)
                    val typed =
                        if (reduced != null && type != kaneDouble)
                            RetypeScalar(reduced, type)
                        else reduced
                    typed ?: sub
                }
            }
            else -> sub
        }
    }
}

private class ReduceAlgebraicUnaryScalar : RewritingVisitor() {
    override fun rewrite(expr: AlgebraicUnaryScalar): Expr {
        return when (val sub = super.rewrite(expr)) {
            is AlgebraicUnaryScalar -> when (sub.value) {
                is ScalarListExpr -> sub.value.copy(sub.value.values.map { sub.copy(it) })
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
                is SheetRangeExpr -> expr
                else ->
                    error("${scalar.value.javaClass}")
            }
            is ScalarListExpr -> scalar.copy(scalar.values.map { expr.copy(scalar = it) })
            is ConstantScalar ->
                if (expr.scalar === scalar) return expr
                else expr.copy(scalar = scalar)
            else -> if (scalar === expr.scalar) return expr else expr.copy(scalar = scalar)
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
            binaryOf(op, l, r)
        })
    }
}
private val reduceAlgebraicUnaryMatrix = object : RewritingVisitor() {
    override fun rewrite(expr: AlgebraicUnaryMatrix) = expr.value.map {
        AlgebraicUnaryScalar(expr.op, it)
    }
}
private val reduceAlgebraicUnaryScalarStatistic = object : RewritingVisitor() {
    override fun rewrite(expr: AlgebraicUnaryScalarStatistic): Expr = with(expr) {
        return when (expr.value) {
            is RetypeScalar -> rewrite(expr.value.copy(expr.copy(value = expr.value.scalar)))
            else -> op.reduceArithmetic(value) ?: super.rewrite(expr)
        }
    }
}

private val reduceAlgebraicBinaryScalarStatistic = object : RewritingVisitor() {
    override fun rewrite(expr: AlgebraicBinaryScalarStatistic): Expr = with(expr) {

        return when {
            left is ScalarStatistic -> op.reduceArithmetic(left, right)
            left is ScalarListExpr -> op.reduceArithmetic(left.values, right) ?: expr
            left is NamedScalar && left.scalar is ScalarStatistic -> op.reduceArithmetic(left.scalar, right)
            left is RetypeScalar && left.scalar is ScalarStatistic -> {
                val result = left.copy(scalar = op.reduceArithmetic(left.scalar, right))
                result
            }
            else ->
                error("${left.javaClass}")
        }
    }
}

private val reduceNakedScalarStatistic = object : RewritingVisitor() {
    override fun rewrite(expr: ScalarStatistic) = constant(expr.statistic.median)
}

private class ReduceRandomVariables(val variables: Map<RandomVariableExpr, ConstantScalar>) :
    RewritingVisitor() {
    override fun rewrite(expr: DiscreteUniformRandomVariable) =
        variables[expr] ?: expr

    override fun rewrite(expr: AlgebraicBinaryScalar): Expr = with(expr) {
        val leftRewritten = scalar(left)
        val rightRewritten = scalar(right)
        if (leftRewritten === left && rightRewritten === right) return this
        return op.reduceArithmetic(leftRewritten, rightRewritten) ?: copy(leftRewritten, rightRewritten)
    }

    override fun rewrite(expr: AlgebraicUnaryScalar): Expr = with(expr) {
        val rewritten = scalar(value)
        return if (rewritten === value) this
        else op.reduceArithmetic(rewritten) ?: copy(rewritten)
    }
}

private class ReduceNamedVariables(
    val reduceVariables: Boolean,
    val excludeVariables: Set<Id>,
    val namedVariableLookup: Cells
) : RewritingVisitor() {
    private var recursionDetected = false

    override fun rewrite(expr: NamedMatrix): Expr = with(expr) {
        if (excludeVariables.contains(expr.name)) return expr
        return matrix(matrix)
    }

    override fun rewrite(expr: NamedScalar): Expr {
        if (excludeVariables.contains(expr.name)) return expr
        val lookup = namedVariableLookup[expr.name] ?: return expr.scalar
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
        return ScalarStatistic(stream)
    }
}

private val reduceCoerceScalar = object : RewritingVisitor() {
    override fun rewrite(expr: CoerceScalar): Expr {
        return when (expr.value) {
            is Sheet ->
                ScalarListExpr(expr.value.cells.map { it.value as ScalarExpr })
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


private fun Expr.expandSheetCells(sheet: Sheet, excludeVariables: Set<Id>): Expr {
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

        override fun rewrite(expr: AlgebraicBinaryScalar): Expr = with(expr) {
            val leftRewritten = scalar(left)
            val rightRewritten = scalar(right)
            if (leftRewritten === left && rightRewritten === right) return this
            val op = copy(
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

        override fun rewrite(expr: SheetRangeExpr): Expr = with(expr) {
            if (expr.rangeRef is CellRangeRef && excludeVariables.contains(expr.rangeRef.toCoordinate()))
                return this
            if (checkIdentity && excludeVariables.contains(expr.rangeRef.toString())) {
                excludeVariables.contains(expr.rangeRef.toString())
                error("Invalid range lookup")
            }
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
            if (list.size == 1) return list.first()
            return ScalarListExpr(list)

        }
    }.rewrite(this)
}

private class ReduceProvidedSheetRangeExprs(val rangeExprProvider: RangeExprProvider) :
    RewritingVisitor() {
    override fun rewrite(expr: SheetRangeExpr): Expr {
        return rangeExprProvider.range(expr)
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
        this is ScalarStatistic && incoming is ConstantScalar -> {
            statistic.insert(incoming.value)
        }
        this is AlgebraicUnaryScalarStatistic && incoming is AlgebraicUnaryScalarStatistic -> {
            value.accumulateStatistics(incoming.value)
        }
        this is RetypeScalar && incoming is RetypeScalar -> {
            this.scalar.accumulateStatistics(incoming.scalar)
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
        this is NamedScalarVariable && incoming is NamedScalarVariable -> {
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
    excludeVariables: Set<Id>,
    randomVariableValues: Map<RandomVariableExpr, ConstantScalar>
): Expr {
    excludeVariables.forEach { Identifier.validate(it) }
    var last = this
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
        val reducedNamedMatrix = reduceNamedMatrix(reducedNamedVariables)
        val reducedRandomVariables = reduceRandomVariables(reducedNamedMatrix)
        val reducedAlgebraicUnaryMatrix = reduceAlgebraicUnaryMatrix(reducedRandomVariables)
        val reducedAlgebraicBinaryMatrix = reduceAlgebraicBinaryMatrix(reducedAlgebraicUnaryMatrix)
        val reducedAlgebraicUnaryScalar = reduceAlgebraicUnaryScalar(reducedAlgebraicBinaryMatrix)
        val reducedAlgebraicBinaryScalar = reduceAlgebraicBinaryScalar(reducedAlgebraicUnaryScalar)
        val reducedProvidedSheetRangeExprs = reduceProvidedSheetRangeExprs(reducedAlgebraicBinaryScalar)
        val expandedSheetCells = expandSheetCells(reducedProvidedSheetRangeExprs)
        val reducedRetypeOfRetype = reduceRetypeOfRetype(expandedSheetCells)
        val reducedCoerceScalar = reduceCoerceScalar(reducedRetypeOfRetype)

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
    excludeVariables: Set<Id>
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
    excludeVariables: Set<Id> = setOf()
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

fun NamedMatrix.eval(
    rangeExprProvider: RangeExprProvider = NopRangeExprProvider(),
    reduceVariables: Boolean = false,
    excludeVariables: Set<Id> = setOf(),
) = (this as Expr).eval(rangeExprProvider, reduceVariables, excludeVariables) as NamedMatrix

fun NamedScalar.eval(
    rangeExprProvider: RangeExprProvider = NopRangeExprProvider(),
    reduceVariables: Boolean = false,
    excludeVariables: Set<Id> = setOf(),
) = (this as Expr).eval(rangeExprProvider, reduceVariables, excludeVariables) as NamedScalar

fun NamedAlgebraicExpr.eval(
    rangeExprProvider: RangeExprProvider = NopRangeExprProvider(),
    reduceVariables: Boolean = false,
    excludeVariables: Set<Id> = setOf(),
) = (this as Expr).eval(rangeExprProvider, reduceVariables, excludeVariables) as NamedAlgebraicExpr

fun Sheet.eval(
    rangeExprProvider: RangeExprProvider = NopRangeExprProvider(),
    reduceVariables: Boolean = false,
    excludeVariables: Set<Id> = setOf(),
) = ((this as Expr).eval(rangeExprProvider, reduceVariables, excludeVariables) as Sheet).showExcelColumnTags(false)

fun ScalarExpr.eval(
    rangeExprProvider: RangeExprProvider = NopRangeExprProvider(),
    reduceVariables: Boolean = false,
    excludeVariables: Set<Id> = setOf(),
) = (this as Expr).eval(rangeExprProvider, reduceVariables, excludeVariables) as ScalarExpr

fun MatrixExpr.eval(
    rangeExprProvider: RangeExprProvider = NopRangeExprProvider(),
    reduceVariables: Boolean = false,
    excludeVariables: Set<Id> = setOf(),
) = (this as Expr).eval(rangeExprProvider, reduceVariables, excludeVariables) as MatrixExpr