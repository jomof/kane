package com.github.jomof.kane.impl.functions

import com.github.jomof.kane.*
import com.github.jomof.kane.impl.*
import com.github.jomof.kane.impl.sheet.CoerceScalar
import com.github.jomof.kane.impl.sheet.GroupBy
import com.github.jomof.kane.impl.sheet.Sheet
import com.github.jomof.kane.impl.sheet.SheetRange
import com.github.jomof.kane.impl.types.*
import kotlin.reflect.KProperty

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

    operator fun invoke(p1: Double, p2: Double) = doubleOp(p1, p2)

    operator fun invoke(p1: SheetRange, p2: ScalarExpr): ScalarExpr =
        AlgebraicBinaryScalarScalarScalar(this, CoerceScalar(p1), p2)

    operator fun invoke(p1: SheetRange, p2: SheetRange): ScalarExpr =
        AlgebraicBinaryScalarScalarScalar(this, CoerceScalar(p1), CoerceScalar(p2))

    operator fun invoke(p1: ScalarExpr, p2: SheetRange): ScalarExpr =
        AlgebraicBinaryScalarScalarScalar(this, p1, CoerceScalar(p2))

    operator fun invoke(p1: SheetRange, p2: Double): ScalarExpr =
        AlgebraicBinaryScalarScalarScalar(this, CoerceScalar(p1), constant(p2))

    operator fun invoke(p1: Double, p2: SheetRange): ScalarExpr =
        AlgebraicBinaryScalarScalarScalar(this, constant(p1), CoerceScalar(p2))

    operator fun invoke(p1: Double, p2: MatrixExpr): MatrixExpr =
        AlgebraicBinaryScalarMatrixMatrix(this, constant(p1), p2)

    override fun invoke(p1: MatrixExpr, p2: MatrixExpr): MatrixExpr =
        AlgebraicBinaryMatrixMatrixMatrix(this, p1, p2)

    operator fun invoke(p1: MatrixExpr, p2: Double): MatrixExpr =
        AlgebraicBinaryMatrixScalarMatrix(this, p1, constant(p2))

    operator fun invoke(p1: Double, p2: ScalarExpr): ScalarExpr =
        invoke(constant(p1), p2)

    operator fun invoke(p1: ScalarExpr, p2: Double): ScalarExpr =
        invoke(p1, constant(p2))

    operator fun invoke(p1: MatrixExpr, p2: SheetRange): MatrixExpr =
        AlgebraicBinaryMatrixScalarMatrix(this, p1, CoerceScalar(p2))

    operator fun invoke(p1: SheetRange, p2: MatrixExpr): MatrixExpr =
        AlgebraicBinaryScalarMatrixMatrix(this, CoerceScalar(p1), p2)

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

    operator fun invoke(value: Double) = doubleOp(value)
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

    override fun doubleOp(value: Double): Double {
        TODO("Not yet implemented")
    }

    override fun doubleOp(value: List<Double>): Double {
        TODO("Not yet implemented")
    }

    override fun type(value: AlgebraicType) = value

    operator fun invoke(values: Array<out Number>): Double {
        val statistic = StreamingSamples()
        values.forEach {
            statistic.insert(it.toDouble())
        }
        return lookupStatistic(statistic)
    }

    operator fun invoke(value: Sheet): Sheet {
        val filtered = value.describe().filterRows { row -> "$row" == meta.op }
        if (filtered.columns == 1)
            return filtered["A1"]
        return filtered
    }

    operator fun invoke(expr: GroupBy): Sheet = expr.aggregate(this)

    operator fun invoke(exprs: Array<out ScalarExpr>): ScalarExpr =
        AlgebraicSummaryMatrixScalar(
            this,
            DataMatrix(exprs.size, 1, exprs.toList())
        )

    operator fun invoke(exprs: Array<out Any>): ScalarExpr =
        AlgebraicSummaryMatrixScalar(
            this,
            DataMatrix(exprs.size, 1, exprs.toList().map { convertAnyToScalarExpr(it) })
        )

    operator fun invoke(expr: SheetRange): ScalarExpr =
        AlgebraicSummaryScalarScalar(
            this as IAlgebraicSummaryScalarScalarFunction,
            CoerceScalar(expr)
        )

    operator fun invoke(expr: Expr): Expr = when (expr) {
        is AlgebraicExpr -> invoke(expr)
        is SheetRange -> invoke(expr)
        is Sheet -> invoke(expr)
        else -> error("${expr.javaClass}")
    }

    fun lookupStatistic(statistic: StreamingSamples): Double
    fun reduceArithmetic(value: ScalarStatistic) = constant(lookupStatistic(value.statistic))
    fun reduceArithmetic(elements: List<ScalarExpr>): ScalarExpr? {
        if (elements.any { it is ScalarStatistic }) error("")
        if (elements.isEmpty()) return null
        if (!elements.all { it.canGetConstant() }) return null


        val statistic = StreamingSamples()
        elements.forEach {
            statistic.insert(it.getConstant())
        }
        val reduced = reduceArithmetic(ScalarStatistic(statistic))
        val type = elements[0].algebraicType
        return if (type == kaneDouble) reduced
        else RetypeScalar(reduced, type)
    }

    override fun reduceArithmetic(value: MatrixExpr) = reduceArithmetic(value.elements)
    fun reduceArithmetic(value: Expr) = when (value) {
        is ScalarStatistic -> reduceArithmetic(value)
        is MatrixExpr -> reduceArithmetic(value)
        is ScalarExpr -> reduceArithmetic(listOf(value))
        else ->
            error("${value.javaClass}")
    }

    override fun reduceArithmetic(value: ScalarExpr) = when (value) {
        is ScalarStatistic -> reduceArithmetic(value)
        is ScalarExpr -> reduceArithmetic(listOf(value))
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

//data class AlgebraicSummaryMatrixScalar(
//    val op: AlgebraicSummaryFunction,
//    val value: MatrixExpr
//) : ScalarExpr {
//    override fun toString() = render()
//}


// f(scalar statistic, scalar)->scalar
interface AlgebraicBinaryScalarStatisticFunction {
    val meta: BinaryOp

    operator fun invoke(left: MatrixExpr, right: ScalarExpr) =
        AlgebraicBinaryMatrixScalarStatistic(this, left, right)

    operator fun invoke(left: MatrixExpr, right: Double) =
        AlgebraicBinaryMatrixScalarStatistic(this, left, constant(right))

    operator fun invoke(left: ScalarExpr, right: ScalarExpr) =
        AlgebraicBinaryScalarStatistic(this, left, right)

    operator fun invoke(left: ScalarExpr, right: Double) =
        AlgebraicBinaryScalarStatistic(this, left, constant(right))

    fun reduceArithmetic(left: ScalarStatistic, right: ScalarExpr): ScalarExpr
    fun reduceArithmetic(left: List<ScalarExpr>, right: ScalarExpr): ScalarExpr?
}

data class AlgebraicBinaryScalarStatistic(
    val op: AlgebraicBinaryScalarStatisticFunction,
    val left: ScalarExpr,
    val right: ScalarExpr
) : ScalarExpr {
    init {
        track()
    }
    override fun toString() = render()
}

data class AlgebraicBinaryMatrixScalarStatistic(
    val op: AlgebraicBinaryScalarStatisticFunction,
    val left: MatrixExpr,
    val right: ScalarExpr
) : ScalarExpr {
    init {
        track()
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toString() = render()
}

// f(matrix)->scalar
interface AlgebraicUnaryMatrixScalarFunction :
    IAlgebraicUnaryScalarScalarFunction,
    IAlgebraicUnaryMatrixMatrixFunction {
    override val meta: UnaryOp
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
