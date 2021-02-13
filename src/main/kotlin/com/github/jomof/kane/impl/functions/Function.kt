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
interface AlgebraicBinaryScalarFunction {
    val meta: BinaryOp
    fun doubleOp(p1: Double, p2: Double): Double

    operator fun invoke(p1: Double, p2: Double) = doubleOp(p1, p2)

    operator fun invoke(p1: ScalarExpr, p2: ScalarExpr): ScalarExpr =
        AlgebraicBinaryScalar(this, p1, p2)

    operator fun invoke(p1: SheetRange, p2: ScalarExpr): ScalarExpr =
        AlgebraicBinaryScalar(this, CoerceScalar(p1), p2)

    operator fun invoke(p1: SheetRange, p2: SheetRange): ScalarExpr =
        AlgebraicBinaryScalar(this, CoerceScalar(p1), CoerceScalar(p2))

    operator fun invoke(p1: ScalarExpr, p2: SheetRange): ScalarExpr =
        AlgebraicBinaryScalar(this, p1, CoerceScalar(p2))

    operator fun invoke(p1: SheetRange, p2: Double): ScalarExpr =
        AlgebraicBinaryScalar(this, CoerceScalar(p1), constant(p2))

    operator fun invoke(p1: Double, p2: SheetRange): ScalarExpr =
        AlgebraicBinaryScalar(this, constant(p1), CoerceScalar(p2))

    operator fun invoke(p1: MatrixExpr, p2: ScalarExpr): MatrixExpr =
        AlgebraicBinaryMatrixScalar(this, p1, p2)

    operator fun invoke(p1: Double, p2: MatrixExpr): MatrixExpr =
        AlgebraicBinaryScalarMatrix(this, constant(p1), p2)

    operator fun invoke(p1: ScalarExpr, p2: MatrixExpr): MatrixExpr =
        AlgebraicBinaryScalarMatrix(this, p1, p2)

    operator fun invoke(p1: MatrixExpr, p2: MatrixExpr): MatrixExpr =
        AlgebraicBinaryMatrix(this, p1, p2)

    operator fun invoke(p1: MatrixExpr, p2: Double): MatrixExpr =
        AlgebraicBinaryMatrixScalar(this, p1, constant(p2))

    operator fun invoke(p1: Double, p2: ScalarExpr): ScalarExpr =
        invoke(constant(p1), p2)

    operator fun invoke(p1: ScalarExpr, p2: Double): ScalarExpr =
        invoke(p1, constant(p2))

    operator fun invoke(p1: MatrixExpr, p2: SheetRange): MatrixExpr =
        AlgebraicBinaryMatrixScalar(this, p1, CoerceScalar(p2))

    operator fun invoke(p1: SheetRange, p2: MatrixExpr): MatrixExpr =
        AlgebraicBinaryScalarMatrix(this, CoerceScalar(p1), p2)

    fun type(left: AlgebraicType, right: AlgebraicType): AlgebraicType {
        return when {
            left == DollarAlgebraicType.kaneType -> DollarAlgebraicType.kaneType
            right == DollarAlgebraicType.kaneType -> DollarAlgebraicType.kaneType
            left == DollarsAndCentsAlgebraicType.kaneType -> DollarsAndCentsAlgebraicType.kaneType
            right == DollarsAndCentsAlgebraicType.kaneType -> DollarsAndCentsAlgebraicType.kaneType
            else -> left
        }
    }

    fun reduceArithmetic(p1: ScalarExpr, p2: ScalarExpr): ScalarExpr?
    fun differentiate(
        p1: ScalarExpr,
        p1d: ScalarExpr,
        p2: ScalarExpr,
        p2d: ScalarExpr,
        variable: ScalarExpr
    ): ScalarExpr
}

data class AlgebraicBinaryScalar(
    val op: AlgebraicBinaryScalarFunction,
    val left: ScalarExpr,
    val right: ScalarExpr
) : ScalarExpr {
    private val hashCode = op.hashCode() * 3 + left.hashCode() * 7 + right.hashCode() * 9
    override fun hashCode() = hashCode

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is AlgebraicBinaryScalar) return false
        if (op != other.op) return false
        if (left != other.left) return false
        if (right != other.right) return false
        if (algebraicType != other.algebraicType) return false
        return true
    }

    init {
        track()
        ++allocs
    }

    companion object {
        var allocs = 0
    }

    override fun toString() = render()
    fun copy(
        left: ScalarExpr = this.left,
        right: ScalarExpr = this.right
    ): AlgebraicBinaryScalar {
        if (this.left === left && this.right === right) return this
        return AlgebraicBinaryScalar(op, left, right)
    }
}

data class AlgebraicBinaryMatrixScalar(
    val op: AlgebraicBinaryScalarFunction,
    val left: MatrixExpr,
    val right: ScalarExpr
) : MatrixExpr {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toString() = render()
}

data class AlgebraicBinaryScalarMatrix(
    val op: AlgebraicBinaryScalarFunction,
    val left: ScalarExpr,
    val right: MatrixExpr
) : MatrixExpr {
    override fun toString() = render()
}

data class AlgebraicBinaryMatrix(
    val op: AlgebraicBinaryScalarFunction,
    val left: MatrixExpr,
    val right: MatrixExpr
) : MatrixExpr {
    override fun toString() = render()
    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
}

// f(scalar)->scalar
interface AlgebraicUnaryScalarFunction {
    val meta: UnaryOp
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
    fun doubleOp(value: Double): Double
    operator fun invoke(value: ScalarExpr): ScalarExpr = wrap(AlgebraicUnaryScalar(this, value))
    operator fun invoke(value: MatrixExpr): MatrixExpr = wrap(AlgebraicUnaryMatrix(this, value))
    fun reduceArithmetic(value: ScalarExpr): ScalarExpr? {
        val isConstValue = value.canGetConstant()
        return when {
            isConstValue -> wrap(constant(doubleOp(value.getConstant())))
            else -> null
        }
    }

    fun differentiate(expr: ScalarExpr, exprd: ScalarExpr, variable: ScalarExpr): ScalarExpr
}

data class AlgebraicUnaryScalar(
    val op: AlgebraicUnaryScalarFunction,
    val value: ScalarExpr
) : ScalarExpr {
    override fun toString() = render()
    fun copy(value: ScalarExpr): AlgebraicUnaryScalar {
        return if (value === this.value) return this
        else AlgebraicUnaryScalar(op, value)
    }
}

data class AlgebraicUnaryMatrix(
    val op: AlgebraicUnaryScalarFunction,
    val value: MatrixExpr
) : MatrixExpr {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toString() = render()
}

// f(scalar statistic)->scalar
interface AggregatableFunction
interface AlgebraicUnaryScalarStatisticFunction : AggregatableFunction {
    val meta: UnaryOp

    fun call(values: Array<out Number>): Double {
        val statistic = StreamingSamples()
        values.forEach {
            statistic.insert(it.toDouble())
        }
        return lookupStatistic(statistic)
    }

    fun call(value: Sheet): Sheet {
        val filtered = value.describe().filterRows { row -> "$row" == meta.op }
        if (filtered.columns == 1)
            return filtered["A1"]
        return filtered
    }

    fun call(expr: GroupBy): Sheet = expr.aggregate(this)

    fun call(exprs: Array<out ScalarExpr>): ScalarExpr =
        AlgebraicUnaryScalarStatistic(
            this,
            DataMatrix(exprs.size, 1, exprs.toList())
        )

    fun call(exprs: Array<out Any>): ScalarExpr =
        AlgebraicUnaryScalarStatistic(
            this,
            DataMatrix(exprs.size, 1, exprs.toList().map { convertAnyToScalarExpr(it) })
        )

    fun call(expr: SheetRange): ScalarExpr =
        AlgebraicUnaryScalarStatistic(
            this,
            CoerceScalar(expr)
        )

    fun call(expr: ScalarExpr): ScalarExpr = AlgebraicUnaryScalarStatistic(this, expr)
    fun call(expr: MatrixExpr): ScalarExpr = AlgebraicUnaryScalarStatistic(this, expr)

    fun call(expr: Expr): Expr = when (expr) {
        is AlgebraicExpr -> call(expr)
        is SheetRange -> call(expr)
        is Sheet -> call(expr)
        else -> error("${expr.javaClass}")
    }

    fun lookupStatistic(statistic: StreamingSamples): Double
    fun reduceArithmetic(value: ScalarStatistic) = constant(lookupStatistic(value.statistic))
    fun reduceArithmetic(elements: List<ScalarExpr>): ScalarExpr? {
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

    fun reduceArithmetic(value: MatrixExpr) = reduceArithmetic(value.elements)
    fun reduceArithmetic(value: Expr) = when (value) {
        is ScalarStatistic -> reduceArithmetic(value)
        is MatrixExpr -> reduceArithmetic(value)
        is ScalarExpr -> reduceArithmetic(listOf(value))
        else ->
            error("${value.javaClass}")
    }
}

data class AlgebraicUnaryScalarStatistic(
    val op: AlgebraicUnaryScalarStatisticFunction,
    val value: AlgebraicExpr
) : ScalarExpr {
    init {
        track()
    }

    override fun toString() = render()
}


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
interface AlgebraicUnaryMatrixScalarFunction {
    val meta: UnaryOp
    fun doubleOp(values: List<Double>): Double
    operator fun invoke(value: MatrixExpr): ScalarExpr = AlgebraicUnaryMatrixScalar(this, value)
    fun reduceArithmetic(value: MatrixExpr): ScalarExpr?
}

data class AlgebraicUnaryMatrixScalar(
    val op: AlgebraicUnaryMatrixScalarFunction,
    val value: MatrixExpr
) : ScalarExpr {
    override fun toString() = render()
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
