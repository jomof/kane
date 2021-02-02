package com.github.jomof.kane.functions

import com.github.jomof.kane.*
import com.github.jomof.kane.sheet.*
import com.github.jomof.kane.types.AlgebraicType
import com.github.jomof.kane.types.DollarAlgebraicType
import com.github.jomof.kane.types.DollarsAndCentsAlgebraicType
import com.github.jomof.kane.types.DoubleAlgebraicType
import kotlin.reflect.KProperty

// f(scalar,scalar)->scalar (extended to matrix 1<->1 mapping)
interface AlgebraicBinaryScalarFunction {
    val meta : BinaryOp
    operator fun invoke(p1 : Double, p2 : Double) = doubleOp(p1, p2)
    fun doubleOp(p1 : Double, p2 : Double) : Double
    operator fun  invoke(p1 : ScalarExpr, p2 : ScalarExpr) : ScalarExpr =
        AlgebraicBinaryScalar(this, p1, p2)
    operator fun  invoke(p1 : UntypedScalar, p2 : ScalarExpr) : ScalarExpr =
        AlgebraicBinaryScalar(this, CoerceScalar(p1, p2.type), p2)
    operator fun  invoke(p1 : UntypedScalar, p2 : UntypedScalar) : ScalarExpr =
        AlgebraicBinaryScalar(this, CoerceScalar(p1, DoubleAlgebraicType.kaneType), CoerceScalar(p2, DoubleAlgebraicType.kaneType))
    operator fun  invoke(p1 : ScalarExpr, p2 : UntypedScalar) : ScalarExpr =
        AlgebraicBinaryScalar(this, p1, CoerceScalar(p2, p1.type))
    operator fun  invoke(p1 : UntypedScalar, p2 : Double) : ScalarExpr =
        AlgebraicBinaryScalar(this, CoerceScalar(p1, DoubleAlgebraicType.kaneType), constant(p2))
    operator fun  invoke(p1 : Double, p2 : UntypedScalar) : ScalarExpr =
        AlgebraicBinaryScalar(this, constant(p1), CoerceScalar(p2, DoubleAlgebraicType.kaneType))
    operator fun  invoke(p1 : MatrixExpr, p2 : ScalarExpr) : MatrixExpr =
        AlgebraicBinaryMatrixScalar(this, p1.columns, p1.rows, p1, p2)
    operator fun  invoke(p1 : Double, p2 : MatrixExpr) : MatrixExpr =
        AlgebraicBinaryScalarMatrix(this, p2.columns, p2.rows, constant(p1, p2.type), p2)
    operator fun  invoke(p1 : ScalarExpr, p2 : MatrixExpr) : MatrixExpr =
        AlgebraicBinaryScalarMatrix(this, p2.columns, p2.rows, p1, p2)
    operator fun  invoke(p1 : MatrixExpr, p2 : MatrixExpr) : MatrixExpr =
        AlgebraicBinaryMatrix(this, p2.columns, p2.rows, p1, p2)
    operator fun  invoke(p1 : MatrixExpr, p2 : Double) : MatrixExpr =
        AlgebraicBinaryMatrixScalar(this, p1.columns, p1.rows, p1, constant(p2))
    operator fun  invoke(p1 : Double, p2 : ScalarExpr) : ScalarExpr = invoke(constant(p1), p2)
    operator fun  invoke(p1 : ScalarExpr, p2 : Double) : ScalarExpr = invoke(p1, constant(p2))
    operator fun  invoke(p1 : MatrixExpr, p2 : UntypedScalar) : MatrixExpr =
        AlgebraicBinaryMatrixScalar(this, p1.columns, p1.rows, p1, CoerceScalar(p2, p1.type))
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


fun  binaryScalar(op : AlgebraicBinaryScalarFunction,
                            left : ScalarExpr,
                            right : ScalarExpr) : ScalarExpr {
    return op.reduceArithmetic(left, right) ?: AlgebraicBinaryScalar(op, left, right)
}

data class AlgebraicBinaryScalar(
    val op: AlgebraicBinaryScalarFunction,
    val left: ScalarExpr,
    val right: ScalarExpr,
    override val type: AlgebraicType = op.type(left.type, right.type),
) : ScalarExpr, ParentExpr<Double> {
    private val hashCode = op.hashCode() * 3 + left.hashCode() * 7 + right.hashCode() * 9
    override fun hashCode() = hashCode

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is AlgebraicBinaryScalar) return false
        if (op != other.op) return false
        if (left != other.left) return false
        if (right != other.right) return false
        if (type != other.type) return false
        return true
    }

    init {
        track()
    }

    override val children get() = listOf(left, right)
    override fun toString() = render()
    fun copy(
        left: ScalarExpr = this.left,
        right: ScalarExpr = this.right
    ): AlgebraicBinaryScalar {
        if (this.left === left && this.right === right) return this
        return AlgebraicBinaryScalar(op, left, right)
    }
    fun copyReduce(
        left : ScalarExpr = this.left,
        right : ScalarExpr = this.right) : ScalarExpr {
        if (this.left === left && this.right === right) return this
        return binaryScalar(op, left, right)
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

    override val type get() = op.type(left.type, right.type)
    override fun get(column: Int, row: Int) = AlgebraicBinaryScalar(op, left[column, row], right)
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toString() = render()
}

data class AlgebraicBinaryScalarMatrix(
    val op : AlgebraicBinaryScalarFunction,
    override val columns: Int,
    override val rows: Int,
    val left : ScalarExpr,
    val right : MatrixExpr) : MatrixExpr {
    init { track() }
    override val type get() = op.type(left.type, right.type)
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
    }

    override val type get() = op.type(left.type, right.type)
    override fun get(column: Int, row: Int) = AlgebraicBinaryScalar(op, left[column, row], right[column, row])
    override fun toString() = render()
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
}

// f(scalar)->scalar
interface AlgebraicUnaryScalarFunction {
    val meta: UnaryOp
    operator fun invoke(value: Double) = doubleOp(value)
    fun doubleOp(value: Double): Double
    operator fun invoke(value: ScalarExpr): ScalarExpr = AlgebraicUnaryScalar(this, value, value.type)
    operator fun invoke(value: MatrixExpr): MatrixExpr = AlgebraicUnaryMatrix(this, value)
    fun reduceArithmetic(value: ScalarExpr): ScalarExpr? {
        val constValue = value.tryFindConstant()
        return when {
            constValue != null -> constant(doubleOp(constValue), value.type)
            else -> null
        }
    }

    fun differentiate(expr: ScalarExpr, exprd: ScalarExpr, variable: ScalarExpr): ScalarExpr
}

data class AlgebraicUnaryScalar(
    val op: AlgebraicUnaryScalarFunction,
    val value: ScalarExpr,
    override val type: AlgebraicType
) : ScalarExpr, ParentExpr<Double> {
    init {
        track()
    }

    override val children get() = listOf(value)

    override fun toString() = render()
    fun copy(value: ScalarExpr): AlgebraicUnaryScalar {
        return if (value === this.value) return this
        else AlgebraicUnaryScalar(op, value, type)
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
    override val type get() = value.type
    override fun get(column: Int, row: Int) = AlgebraicUnaryScalar(op, value[column, row], type)
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
            CoerceScalar(expr, DoubleAlgebraicType.kaneType),
            DoubleAlgebraicType.kaneType
        )

    operator fun invoke(expr: AlgebraicExpr): ScalarExpr =
        AlgebraicUnaryScalarStatistic(this, expr, DoubleAlgebraicType.kaneType)

    operator fun invoke(expr: Expr): Expr = when (expr) {
        is AlgebraicExpr -> invoke(expr)
        is UntypedScalar -> invoke(expr)
        is Sheet -> invoke(expr)
        else -> error("${expr.javaClass}")
    }

    fun lookupStatistic(statistic: StreamingSamples): Double
    fun reduceArithmetic(value: ScalarStatistic) = constant(lookupStatistic(value.statistic), value.type)
    fun reduceArithmetic(elements: List<ScalarExpr>): ScalarExpr? {
        if (elements.isEmpty()) return null
        if (!elements.all { it.tryFindConstant() != null }) return null
        val statistic = StreamingSamples()
        elements.forEach {
            statistic.insert(it.tryFindConstant()!!)
        }
        return reduceArithmetic(ScalarStatistic(statistic, elements[0].type))
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
    val value: AlgebraicExpr,
    override val type: AlgebraicType
) : ScalarExpr {
    init {
        track()
    }

    override fun toString() = render()
}

//data class AlgebraicUnaryRangeStatistic(
//    val op: AlgebraicUnaryScalarStatisticFunction,
//    val range: SheetRangeExpr
//) : UntypedScalar {
//    init {
//        track()
//    }
//
//    operator fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
//    override fun toString() = "${op.meta.op}($range)"
//}

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

    override val type get() = left.type
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
) : ScalarExpr, ParentExpr<Double> {
    init {
        track()
    }

    override val type get() = value.type
    override val children: Iterable<ScalarExpr> get() = value.elements.asIterable()
    override fun toString() = render()
}

// f(expr, expr) -> matrix
data class AlgebraicDeferredDataMatrix(
    val op : BinaryOp,
    val left : TypedExpr<Double>,
    val right : TypedExpr<Double>,
    val data : DataMatrix
) : MatrixExpr {
    init { track() }
    override val type = data.type
    override val columns = data.columns
    override val rows = data.rows
    override fun get(column: Int, row: Int) = data[column, row]
    override fun toString() = render()
}
