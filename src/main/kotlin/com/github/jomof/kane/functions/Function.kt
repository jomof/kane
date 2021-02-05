package com.github.jomof.kane.functions

import com.github.jomof.kane.*
import com.github.jomof.kane.sheet.*
import com.github.jomof.kane.types.AlgebraicType
import com.github.jomof.kane.types.DollarAlgebraicType
import com.github.jomof.kane.types.DollarsAndCentsAlgebraicType
import com.github.jomof.kane.types.algebraicType
import kotlin.math.min
import kotlin.reflect.KProperty

// f(scalar,scalar)->scalar (extended to matrix 1<->1 mapping)
interface AlgebraicBinaryScalarFunction {
    val meta : BinaryOp
    operator fun invoke(p1 : Double, p2 : Double) = doubleOp(p1, p2)
    fun doubleOp(p1 : Double, p2 : Double) : Double
    operator fun  invoke(p1 : ScalarExpr, p2 : ScalarExpr) : ScalarExpr =
        binaryOf(this, p1, p2)
    operator fun  invoke(p1 : UntypedScalar, p2 : ScalarExpr) : ScalarExpr =
        binaryOf(this, CoerceScalar(p1), p2)
    operator fun  invoke(p1 : UntypedScalar, p2 : UntypedScalar) : ScalarExpr =
        binaryOf(this, CoerceScalar(p1), CoerceScalar(p2))
    operator fun  invoke(p1 : ScalarExpr, p2 : UntypedScalar) : ScalarExpr =
        binaryOf(this, p1, CoerceScalar(p2))
    operator fun  invoke(p1 : UntypedScalar, p2 : Double) : ScalarExpr =
        binaryOf(this, CoerceScalar(p1), constant(p2))
    operator fun  invoke(p1 : Double, p2 : UntypedScalar) : ScalarExpr =
        binaryOf(this, constant(p1), CoerceScalar(p2))
    operator fun  invoke(p1 : MatrixExpr, p2 : ScalarExpr) : MatrixExpr =
        AlgebraicBinaryMatrixScalar(this, p1.columns, p1.rows, p1, p2)
    operator fun  invoke(p1 : Double, p2 : MatrixExpr) : MatrixExpr =
        AlgebraicBinaryScalarMatrix(this, p2.columns, p2.rows, constant(p1), p2)
    operator fun  invoke(p1 : ScalarExpr, p2 : MatrixExpr) : MatrixExpr =
        AlgebraicBinaryScalarMatrix(this, p2.columns, p2.rows, p1, p2)
    operator fun  invoke(p1 : MatrixExpr, p2 : MatrixExpr) : MatrixExpr =
        AlgebraicBinaryMatrix(this, min(p1.columns, p2.columns), min(p1.rows, p2.rows), p1, p2)
    operator fun  invoke(p1 : MatrixExpr, p2 : Double) : MatrixExpr =
        AlgebraicBinaryMatrixScalar(this, p1.columns, p1.rows, p1, constant(p2))
    operator fun  invoke(p1 : Double, p2 : ScalarExpr) : ScalarExpr = invoke(constant(p1), p2)
    operator fun  invoke(p1 : ScalarExpr, p2 : Double) : ScalarExpr = invoke(p1, constant(p2))
    operator fun  invoke(p1 : MatrixExpr, p2 : UntypedScalar) : MatrixExpr =
        AlgebraicBinaryMatrixScalar(this, p1.columns, p1.rows, p1, CoerceScalar(p2))
    fun type(left : AlgebraicType, right : AlgebraicType) : AlgebraicType {
        return when {
            left == DollarAlgebraicType.kaneType -> DollarAlgebraicType.kaneType
            right == DollarAlgebraicType.kaneType -> DollarAlgebraicType.kaneType
            left == DollarsAndCentsAlgebraicType.kaneType -> DollarsAndCentsAlgebraicType.kaneType
            right == DollarsAndCentsAlgebraicType.kaneType -> DollarsAndCentsAlgebraicType.kaneType
            else -> left
        }
    }
    fun  reduceArithmetic(p1 : ScalarExpr, p2 : ScalarExpr) : ScalarExpr?
    fun  differentiate(
        p1 : ScalarExpr,
        p1d : ScalarExpr,
        p2 : ScalarExpr,
        p2d : ScalarExpr,
        variable : ScalarExpr) : ScalarExpr
}

fun binaryOf(op: AlgebraicBinaryScalarFunction, left: ScalarExpr, right: ScalarExpr) =
    AlgebraicBinaryScalar(op, left, right)

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
    override val columns: Int,
    override val rows: Int,
    val left: MatrixExpr,
    val right: ScalarExpr
) : MatrixExpr {
    init {
        track()
    }
    override fun get(column: Int, row: Int) = AlgebraicBinaryScalar(op, left[column, row], right)
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toString() = render()
}

data class AlgebraicBinaryScalarMatrix(
    val op: AlgebraicBinaryScalarFunction,
    override val columns: Int,
    override val rows: Int,
    val left: ScalarExpr,
    val right: MatrixExpr
) : MatrixExpr {
    init {
        track()
    }

    override fun get(column: Int, row: Int) = AlgebraicBinaryScalar(op, left, right[column, row])
    override fun toString() = render()
}

data class AlgebraicBinaryMatrix(
    val op: AlgebraicBinaryScalarFunction,
    override val columns: Int,
    override val rows: Int,
    val left: MatrixExpr,
    val right: MatrixExpr
) : MatrixExpr {
    init {
        track()
        assert(left.rows >= rows)
        assert(left.columns >= columns)
        assert(right.rows >= rows)
        assert(right.columns >= columns)
    }

    override fun get(column: Int, row: Int) = AlgebraicBinaryScalar(op, left[column, row], right[column, row])
    override fun toString() = render()
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
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
    init {
        track()
    }

    override val columns get() = value.columns
    override val rows get() = value.rows
    override fun get(column: Int, row: Int) = AlgebraicUnaryScalar(op, value[column, row])
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toString() = render()
}

// f(scalar statistic)->scalar
interface AlgebraicUnaryScalarStatisticFunction {
    val meta: UnaryOp

    operator fun invoke(value: Sheet): Expr {
        val filtered = value.describe().filterRows { row -> "$row" == meta.op }
        if (filtered.columns == 1) return filtered[0, 0] as ScalarExpr
        return filtered
    }

    operator fun invoke(expr: UntypedScalar): ScalarExpr =
        AlgebraicUnaryScalarStatistic(
            this,
            CoerceScalar(expr)
        )

    operator fun invoke(expr: AlgebraicExpr): ScalarExpr =
        AlgebraicUnaryScalarStatistic(this, expr)

    operator fun invoke(expr: Expr): Expr = when (expr) {
        is AlgebraicExpr -> invoke(expr)
        is UntypedScalar -> invoke(expr)
        is Sheet -> invoke(expr)
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
        return reduceArithmetic(ScalarStatistic(statistic))
    }

    fun reduceArithmetic(value: MatrixExpr) = reduceArithmetic(value.elements)
    fun reduceArithmetic(value: Expr) = when (value) {
        is ScalarStatistic -> reduceArithmetic(value)
        is MatrixExpr -> reduceArithmetic(value)
        is ScalarListExpr -> reduceArithmetic(value.values)
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
    operator fun invoke(left: ScalarExpr, right: ScalarExpr) =
        AlgebraicBinaryScalarStatistic(this, left, right)

    operator fun invoke(left: ScalarExpr, right: Double) =
        AlgebraicBinaryScalarStatistic(this, left, constant(right))

    operator fun invoke(value: SheetRangeExpr, right: ScalarExpr) =
        AlgebraicBinaryRangeStatistic(this, value, right)

    operator fun invoke(value: NamedSheetRangeExpr, right: ScalarExpr) =
        AlgebraicBinaryRangeStatistic(this, value.range, right)

    operator fun invoke(value: SheetRangeExpr, right: Double) =
        AlgebraicBinaryRangeStatistic(this, value, constant(right))

    operator fun invoke(value: NamedSheetRangeExpr, right: Double) =
        AlgebraicBinaryRangeStatistic(this, value.range, constant(right))

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

data class AlgebraicBinaryRangeStatistic(
    val op: AlgebraicBinaryScalarStatisticFunction,
    val left: SheetRangeExpr,
    val right: ScalarExpr
) : UntypedScalar {
    init {
        track()
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toString() = render()
}

// f(matrix)->scalar
interface AlgebraicUnaryMatrixScalarFunction {
    val meta: UnaryOp
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
    init { track() }
    override val columns = data.columns
    override val rows = data.rows
    override fun get(column: Int, row: Int) = data[column, row]
    override fun toString() = render()
}
