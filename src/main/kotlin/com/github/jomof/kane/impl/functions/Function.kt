package com.github.jomof.kane.impl.functions

import com.github.jomof.kane.*
import com.github.jomof.kane.api.Row
import com.github.jomof.kane.impl.*
import com.github.jomof.kane.impl.sheet.*
import com.github.jomof.kane.impl.types.*

// f(scalar,scalar)->scalar (extended to matrix 1<->1 mapping)
interface AlgebraicBinaryFunction :
    IAlgebraicBinaryScalarScalarScalarFunction,
    IAlgebraicBinaryMatrixScalarMatrixFunction,
    IAlgebraicBinaryScalarMatrixMatrixFunction,
    IAlgebraicBinaryMatrixMatrixMatrixFunction {
    override val meta: BinaryOp
    override fun doubleOp(p1: Double, p2: Double): Double
    override fun doubleOp(left: List<Double>, right: Double): List<Double> {
        TODO("Not yet implemented")
    }

    override fun doubleOp(left: Double, right: List<Double>): List<Double> {
        TODO("Not yet implemented")
    }

    override fun doubleOp(left: List<Double>, right: List<Double>): List<Double> {
        TODO("Not yet implemented")
    }

    override fun type(left: AlgebraicType, right: AlgebraicType): AlgebraicType {
        return when {
            left == DollarAlgebraicType.kaneType -> DollarAlgebraicType.kaneType
            right == DollarAlgebraicType.kaneType -> DollarAlgebraicType.kaneType
            left == DollarsAndCentsAlgebraicType.kaneType -> DollarsAndCentsAlgebraicType.kaneType
            right == DollarsAndCentsAlgebraicType.kaneType -> DollarsAndCentsAlgebraicType.kaneType
            else -> left
        }
    }

    override fun reduceArithmetic(p1: ScalarExpr, p2: ScalarExpr): ScalarExpr?
    override fun reduceArithmetic(left: ScalarExpr, right: MatrixExpr): MatrixExpr? {
        TODO("Not yet implemented")
    }

    override fun reduceArithmetic(left: MatrixExpr, right: ScalarExpr): MatrixExpr? {
        TODO("Not yet implemented")
    }

    override fun reduceArithmetic(left: MatrixExpr, right: MatrixExpr): MatrixExpr? {
        TODO("Not yet implemented")
    }

    override fun differentiate(
        p1: ScalarExpr,
        p1d: ScalarExpr,
        p2: ScalarExpr,
        p2d: ScalarExpr,
        variable: ScalarExpr
    ): ScalarExpr

    override fun differentiate(
        left: ScalarExpr,
        leftd: ScalarExpr,
        right: MatrixExpr,
        rightd: MatrixExpr,
        variable: ScalarExpr
    ): MatrixExpr {
        TODO("Not yet implemented")
    }

    override fun differentiate(
        left: MatrixExpr,
        leftd: MatrixExpr,
        right: ScalarExpr,
        rightd: ScalarExpr,
        variable: ScalarExpr
    ): MatrixExpr {
        TODO("Not yet implemented")
    }

    override fun differentiate(
        left: MatrixExpr,
        leftd: MatrixExpr,
        right: MatrixExpr,
        rightd: MatrixExpr,
        variable: ScalarExpr
    ): MatrixExpr {
        TODO("Not yet implemented")
    }
}

// f(scalar)->scalar
interface AlgebraicUnaryFunction :
    IAlgebraicUnaryMatrixMatrixFunction,
    IAlgebraicUnaryScalarScalarFunction {
    override val meta: UnaryOp
    val type: AlgebraicType? get() = null
    private fun wrap(expr: ScalarExpr): ScalarExpr {
        if (type == null) return expr
        return RetypeScalar(expr, type!!)
    }

    private fun wrap(expr: MatrixExpr): MatrixExpr {
        if (type == null) return expr
        return RetypeMatrix(expr, type!!)
    }

    //operator fun invoke(value: Double) = doubleOp(value)
    override fun doubleOp(value: Double): Double
    override fun doubleOp(value: List<Double>): List<Double> {
        TODO("Not yet implemented")
    }

    override fun invoke(value: ScalarExpr): ScalarExpr = wrap(AlgebraicUnaryScalarScalar(this, value))
    override fun invoke(value: MatrixExpr): MatrixExpr = wrap(AlgebraicUnaryMatrixMatrix(this, value))
    override fun reduceArithmetic(value: ScalarExpr): ScalarExpr? {
        val isConstValue = value.canGetConstant()
        return when {
            isConstValue -> wrap(constant(doubleOp(value.getConstant())))
            else -> null
        }
    }

    override fun reduceArithmetic(value: MatrixExpr): MatrixExpr? {
        TODO("Not yet implemented")
    }

    override fun differentiate(expr: ScalarExpr, exprd: ScalarExpr, variable: ScalarExpr): ScalarExpr
    override fun differentiate(value: MatrixExpr, valued: MatrixExpr, variable: ScalarExpr): MatrixExpr {
        TODO("Not yet implemented")
    }

    override fun type(value: AlgebraicType) = value
}

// f(scalar statistic)->scalar
interface AggregatableFunction
interface AlgebraicSummaryFunction :
    IAlgebraicSummaryScalarScalarFunction,
    IAlgebraicSummaryMatrixScalarFunction,
    AggregatableFunction {
    override val meta: SummaryOp

    override fun type(value: AlgebraicType) = value

    override fun doubleOp(value: Double): Double {
        TODO("Not yet implemented")
    }

    override fun doubleOp(value: List<Double>): Double {
        TODO("Not yet implemented")
    }

    operator fun invoke(values: Array<out Number>): Double {
        val statistic = StreamingSamples()
        values.forEach {
            statistic.insert(it.toDouble())
        }
        return lookupStatistic(statistic)
    }

    operator fun invoke(value: Sequence<Row>): Sequence<Row> {
        val filtered = value.describe().filter { row ->
            "$row" == meta.op
        }
        if (filtered.columns == 1) {
            return filtered.toSheet()["A1"]
        }
        return filtered
    }

    operator fun invoke(expr: GroupBy): Sheet = expr.aggregate(this)

    operator fun invoke(exprs: Array<out ScalarExpr>): ScalarExpr {
        return when {
            exprs.size == 1 -> AlgebraicSummaryScalarScalar(this, exprs[0])
            else -> AlgebraicSummaryMatrixScalar(this, DataMatrix(exprs.size, 1, exprs.toList()))
        }
    }

    operator fun invoke(exprs: Array<out Any>): ScalarExpr =
        AlgebraicSummaryMatrixScalar(
            this,
            DataMatrix(exprs.size, 1, exprs.toList().map { convertAnyToScalarExpr(it) })
        )

    operator fun invoke(v1: Any, v2: Any, exprs: Array<out Any>): ScalarExpr =
        AlgebraicSummaryMatrixScalar(
            this,
            DataMatrix(exprs.size + 2, 1, (listOf(v1, v2) + exprs.toList()).map { convertAnyToScalarExpr(it) })
        )

    fun lookupStatistic(statistic: StreamingSamples): Double
    fun reduceArithmetic(value: StreamingSampleStatisticExpr) = constant(lookupStatistic(value.statistic))
    fun reduceArithmetic(elements: List<ScalarExpr>): ScalarExpr? {
        if (elements.any { it is StatisticExpr })
            error("")
        if (elements.isEmpty()) return null
        if (!elements.all { it.canGetConstant() }) return null


        val statistic = StreamingSamples()
        elements.forEach {
            statistic.insert(it.getConstant())
        }
        val reduced = reduceArithmetic(StreamingSampleStatisticExpr(statistic))
        val type = elements[0].algebraicType
        return if (type == kaneDouble) reduced
        else RetypeScalar(reduced, type)
    }

    override fun reduceArithmetic(value: MatrixExpr): ScalarExpr? {
        val elements = value.elements
        return reduceArithmetic(elements)
    }

    override fun reduceArithmetic(value: ScalarExpr): ScalarExpr = when (value) {
        is StreamingSampleStatisticExpr -> reduceArithmetic(value)
        is RetypeScalar -> RetypeScalar(reduceArithmetic(value.scalar), value.type)
        is NamedScalar -> value.dup(scalar = reduceArithmetic(value.scalar))
        else ->
            error("${value.javaClass}")
    }

    override fun differentiate(value: ScalarExpr, valued: ScalarExpr, variable: ScalarExpr): ScalarExpr {
        TODO("Not yet implemented")
    }

    override fun differentiate(value: MatrixExpr, valued: MatrixExpr, variable: ScalarExpr): ScalarExpr {
        TODO("Not yet implemented")
    }
}

// f(scalar statistic, scalar)->scalar
interface AlgebraicBinarySummaryFunction :
    IAlgebraicBinarySummaryScalarScalarScalarFunction,
    IAlgebraicBinarySummaryMatrixScalarScalarFunction {
    override val meta: BinarySummaryOp
    override fun type(left: AlgebraicType, right: AlgebraicType): AlgebraicType {
        TODO("Not yet implemented")
    }

    override fun doubleOp(left: Double, right: Double): Double {
        TODO("Not yet implemented")
    }

    override fun doubleOp(left: List<Double>, right: Double): Double {
        TODO("Not yet implemented")
    }

    fun reduceArithmetic(left: StreamingSampleStatisticExpr, right: ScalarExpr): ScalarExpr
    fun reduceArithmetic(left: List<ScalarExpr>, right: ScalarExpr): ScalarExpr?
    override fun reduceArithmetic(left: ScalarExpr, right: ScalarExpr): ScalarExpr? = when {
        left is StreamingSampleStatisticExpr -> reduceArithmetic(left, right)
        else -> error("${left.javaClass} ${right.javaClass}")
    }

    override fun reduceArithmetic(left: MatrixExpr, right: ScalarExpr): ScalarExpr? =
        reduceArithmetic(left.elements, right)

    override fun differentiate(
        left: ScalarExpr,
        leftd: ScalarExpr,
        right: ScalarExpr,
        rightd: ScalarExpr,
        variable: ScalarExpr
    ): ScalarExpr {
        TODO("Not yet implemented")
    }

    override fun differentiate(
        left: MatrixExpr,
        leftd: MatrixExpr,
        right: ScalarExpr,
        rightd: ScalarExpr,
        variable: ScalarExpr
    ): ScalarExpr {
        TODO("Not yet implemented")
    }
}

// f(expr, expr) -> matrix
data class AlgebraicDeferredDataMatrix(
    val op: BinaryOp,
    val left: AlgebraicExpr,
    val right: AlgebraicExpr,
    val data: DataMatrix
) : MatrixExpr {
    override fun toString() = render()
}
