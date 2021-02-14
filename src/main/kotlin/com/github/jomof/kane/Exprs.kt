package com.github.jomof.kane

import com.github.jomof.kane.impl.*
import com.github.jomof.kane.impl.types.*
import kotlin.reflect.KProperty


interface IAlgebraicSummaryScalarScalarFunction {
    val meta : SummaryOp
    operator fun invoke(value : ScalarExpr) : ScalarExpr = AlgebraicSummaryScalarScalar(this, value)
    fun reduceArithmetic(value : ScalarExpr) : ScalarExpr?
    fun doubleOp(value : Double) : Double
    fun differentiate(value : ScalarExpr, valued : ScalarExpr, variable : ScalarExpr) : ScalarExpr
    fun type(value : AlgebraicType) : AlgebraicType
}

data class AlgebraicSummaryScalarScalar(
    val op : IAlgebraicSummaryScalarScalarFunction,
    val value: ScalarExpr
) : ScalarExpr {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toString() = render()
    fun dup(
        op: IAlgebraicSummaryScalarScalarFunction = this.op,
        value: ScalarExpr = this.value
    ): AlgebraicSummaryScalarScalar {
        if (op === this.op && value === this.value) return this
        return AlgebraicSummaryScalarScalar(op, value)
    }
}

data class NamedAlgebraicSummaryScalarScalar(
    override val name: Id,
    val expr: AlgebraicSummaryScalarScalar
) : NamedScalarExpr {
    override fun toString() = render()
    fun dup(name: Id = this.name, expr: AlgebraicSummaryScalarScalar = this.expr): NamedAlgebraicSummaryScalarScalar {
        if (name === this.name && expr === this.expr) return this
        return NamedAlgebraicSummaryScalarScalar(name, expr)
    }
}

interface IAlgebraicSummaryMatrixScalarFunction {
    val meta: SummaryOp
    operator fun invoke(value: MatrixExpr): ScalarExpr = AlgebraicSummaryMatrixScalar(this, value)
    fun reduceArithmetic(value: MatrixExpr): ScalarExpr?
    fun doubleOp(value: List<Double>): Double
    fun differentiate(value: MatrixExpr, valued: MatrixExpr, variable: ScalarExpr): ScalarExpr
    fun type(value: AlgebraicType): AlgebraicType
}

data class AlgebraicSummaryMatrixScalar(
    val op : IAlgebraicSummaryMatrixScalarFunction,
    val value: MatrixExpr
) : ScalarExpr {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toString() = render()
    fun dup(
        op: IAlgebraicSummaryMatrixScalarFunction = this.op,
        value: MatrixExpr = this.value
    ): AlgebraicSummaryMatrixScalar {
        if (op === this.op && value === this.value) return this
        return AlgebraicSummaryMatrixScalar(op, value)
    }
}

data class NamedAlgebraicSummaryMatrixScalar(
    override val name: Id,
    val expr: AlgebraicSummaryMatrixScalar
) : NamedScalarExpr {
    override fun toString() = render()
    fun dup(name: Id = this.name, expr: AlgebraicSummaryMatrixScalar = this.expr): NamedAlgebraicSummaryMatrixScalar {
        if (name === this.name && expr === this.expr) return this
        return NamedAlgebraicSummaryMatrixScalar(name, expr)
    }
}

interface IAlgebraicUnaryScalarScalarFunction {
    val meta: UnaryOp
    operator fun invoke(value: ScalarExpr): ScalarExpr = AlgebraicUnaryScalarScalar(this, value)
    fun reduceArithmetic(value: ScalarExpr): ScalarExpr?
    fun doubleOp(value: Double): Double
    fun differentiate(value: ScalarExpr, valued: ScalarExpr, variable: ScalarExpr): ScalarExpr
    fun type(value: AlgebraicType): AlgebraicType
}

data class AlgebraicUnaryScalarScalar(
    val op : IAlgebraicUnaryScalarScalarFunction,
    val value: ScalarExpr
) : ScalarExpr {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toString() = render()
    fun dup(
        op: IAlgebraicUnaryScalarScalarFunction = this.op,
        value: ScalarExpr = this.value
    ): AlgebraicUnaryScalarScalar {
        if (op === this.op && value === this.value) return this
        return AlgebraicUnaryScalarScalar(op, value)
    }
}

data class NamedAlgebraicUnaryScalarScalar(
    override val name: Id,
    val expr: AlgebraicUnaryScalarScalar
) : NamedScalarExpr {
    override fun toString() = render()
    fun dup(name: Id = this.name, expr: AlgebraicUnaryScalarScalar = this.expr): NamedAlgebraicUnaryScalarScalar {
        if (name === this.name && expr === this.expr) return this
        return NamedAlgebraicUnaryScalarScalar(name, expr)
    }
}

interface IAlgebraicUnaryMatrixMatrixFunction {
    val meta: UnaryOp
    operator fun invoke(value: MatrixExpr): MatrixExpr = AlgebraicUnaryMatrixMatrix(this, value)
    fun reduceArithmetic(value: MatrixExpr): MatrixExpr?
    fun doubleOp(value: List<Double>): List<Double>
    fun differentiate(value: MatrixExpr, valued: MatrixExpr, variable: ScalarExpr): MatrixExpr
    fun type(value: AlgebraicType): AlgebraicType
}

data class AlgebraicUnaryMatrixMatrix(
    val op : IAlgebraicUnaryMatrixMatrixFunction,
    val value: MatrixExpr
) : MatrixExpr {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toString() = render()
    fun dup(
        op: IAlgebraicUnaryMatrixMatrixFunction = this.op,
        value: MatrixExpr = this.value
    ): AlgebraicUnaryMatrixMatrix {
        if (op === this.op && value === this.value) return this
        return AlgebraicUnaryMatrixMatrix(op, value)
    }
}

data class NamedAlgebraicUnaryMatrixMatrix(
    override val name: Id,
    val expr: AlgebraicUnaryMatrixMatrix
) : NamedMatrixExpr {
    override fun toString() = render()
    fun dup(name: Id = this.name, expr: AlgebraicUnaryMatrixMatrix = this.expr): NamedAlgebraicUnaryMatrixMatrix {
        if (name === this.name && expr === this.expr) return this
        return NamedAlgebraicUnaryMatrixMatrix(name, expr)
    }
}

interface IAlgebraicBinaryScalarScalarScalarFunction {
    val meta: BinaryOp
    operator fun invoke(left: ScalarExpr, right: ScalarExpr): ScalarExpr =
        AlgebraicBinaryScalarScalarScalar(this, left, right)

    fun reduceArithmetic(left: ScalarExpr, right: ScalarExpr): ScalarExpr?
    fun doubleOp(left: Double, right: Double): Double
    fun differentiate(
        left: ScalarExpr,
        leftd: ScalarExpr,
        right: ScalarExpr,
        rightd: ScalarExpr,
        variable: ScalarExpr
    ): ScalarExpr

    fun type(left: AlgebraicType, right: AlgebraicType): AlgebraicType
}

data class AlgebraicBinaryScalarScalarScalar(
    val op : IAlgebraicBinaryScalarScalarScalarFunction,
    val left : ScalarExpr,
    val right: ScalarExpr
) : ScalarExpr {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toString() = render()
    fun dup(
        op: IAlgebraicBinaryScalarScalarScalarFunction = this.op,
        left: ScalarExpr = this.left,
        right: ScalarExpr = this.right
    ): AlgebraicBinaryScalarScalarScalar {
        if (op === this.op && left === this.left && right === this.right) return this
        return AlgebraicBinaryScalarScalarScalar(op, left, right)
    }
}

data class NamedAlgebraicBinaryScalarScalarScalar(
    override val name: Id,
    val expr: AlgebraicBinaryScalarScalarScalar
) : NamedScalarExpr {
    override fun toString() = render()
    fun dup(
        name: Id = this.name,
        expr: AlgebraicBinaryScalarScalarScalar = this.expr
    ): NamedAlgebraicBinaryScalarScalarScalar {
        if (name === this.name && expr === this.expr) return this
        return NamedAlgebraicBinaryScalarScalarScalar(name, expr)
    }
}

interface IAlgebraicBinaryScalarMatrixMatrixFunction {
    val meta: BinaryOp
    operator fun invoke(left: ScalarExpr, right: MatrixExpr): MatrixExpr =
        AlgebraicBinaryScalarMatrixMatrix(this, left, right)

    fun reduceArithmetic(left: ScalarExpr, right: MatrixExpr): MatrixExpr?
    fun doubleOp(left: Double, right: List<Double>): List<Double>
    fun differentiate(
        left: ScalarExpr,
        leftd: ScalarExpr,
        right: MatrixExpr,
        rightd: MatrixExpr,
        variable: ScalarExpr
    ): MatrixExpr

    fun type(left: AlgebraicType, right: AlgebraicType): AlgebraicType
}

data class AlgebraicBinaryScalarMatrixMatrix(
    val op : IAlgebraicBinaryScalarMatrixMatrixFunction,
    val left : ScalarExpr,
    val right: MatrixExpr
) : MatrixExpr {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toString() = render()
    fun dup(
        op: IAlgebraicBinaryScalarMatrixMatrixFunction = this.op,
        left: ScalarExpr = this.left,
        right: MatrixExpr = this.right
    ): AlgebraicBinaryScalarMatrixMatrix {
        if (op === this.op && left === this.left && right === this.right) return this
        return AlgebraicBinaryScalarMatrixMatrix(op, left, right)
    }
}

data class NamedAlgebraicBinaryScalarMatrixMatrix(
    override val name: Id,
    val expr: AlgebraicBinaryScalarMatrixMatrix
) : NamedMatrixExpr {
    override fun toString() = render()
    fun dup(
        name: Id = this.name,
        expr: AlgebraicBinaryScalarMatrixMatrix = this.expr
    ): NamedAlgebraicBinaryScalarMatrixMatrix {
        if (name === this.name && expr === this.expr) return this
        return NamedAlgebraicBinaryScalarMatrixMatrix(name, expr)
    }
}

interface IAlgebraicBinaryMatrixScalarMatrixFunction {
    val meta: BinaryOp
    operator fun invoke(left: MatrixExpr, right: ScalarExpr): MatrixExpr =
        AlgebraicBinaryMatrixScalarMatrix(this, left, right)

    fun reduceArithmetic(left: MatrixExpr, right: ScalarExpr): MatrixExpr?
    fun doubleOp(left: List<Double>, right: Double): List<Double>
    fun differentiate(
        left: MatrixExpr,
        leftd: MatrixExpr,
        right: ScalarExpr,
        rightd: ScalarExpr,
        variable: ScalarExpr
    ): MatrixExpr

    fun type(left: AlgebraicType, right: AlgebraicType): AlgebraicType
}

data class AlgebraicBinaryMatrixScalarMatrix(
    val op : IAlgebraicBinaryMatrixScalarMatrixFunction,
    val left : MatrixExpr,
    val right: ScalarExpr
) : MatrixExpr {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toString() = render()
    fun dup(
        op: IAlgebraicBinaryMatrixScalarMatrixFunction = this.op,
        left: MatrixExpr = this.left,
        right: ScalarExpr = this.right
    ): AlgebraicBinaryMatrixScalarMatrix {
        if (op === this.op && left === this.left && right === this.right) return this
        return AlgebraicBinaryMatrixScalarMatrix(op, left, right)
    }
}

data class NamedAlgebraicBinaryMatrixScalarMatrix(
    override val name: Id,
    val expr: AlgebraicBinaryMatrixScalarMatrix
) : NamedMatrixExpr {
    override fun toString() = render()
    fun dup(
        name: Id = this.name,
        expr: AlgebraicBinaryMatrixScalarMatrix = this.expr
    ): NamedAlgebraicBinaryMatrixScalarMatrix {
        if (name === this.name && expr === this.expr) return this
        return NamedAlgebraicBinaryMatrixScalarMatrix(name, expr)
    }
}

interface IAlgebraicBinaryMatrixMatrixMatrixFunction {
    val meta: BinaryOp
    operator fun invoke(left: MatrixExpr, right: MatrixExpr): MatrixExpr =
        AlgebraicBinaryMatrixMatrixMatrix(this, left, right)

    fun reduceArithmetic(left: MatrixExpr, right: MatrixExpr): MatrixExpr?
    fun doubleOp(left: List<Double>, right: List<Double>): List<Double>
    fun differentiate(
        left: MatrixExpr,
        leftd: MatrixExpr,
        right: MatrixExpr,
        rightd: MatrixExpr,
        variable: ScalarExpr
    ): MatrixExpr

    fun type(left: AlgebraicType, right: AlgebraicType): AlgebraicType
}

data class AlgebraicBinaryMatrixMatrixMatrix(
    val op : IAlgebraicBinaryMatrixMatrixMatrixFunction,
    val left : MatrixExpr,
    val right: MatrixExpr
) : MatrixExpr {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toString() = render()
    fun dup(
        op: IAlgebraicBinaryMatrixMatrixMatrixFunction = this.op,
        left: MatrixExpr = this.left,
        right: MatrixExpr = this.right
    ): AlgebraicBinaryMatrixMatrixMatrix {
        if (op === this.op && left === this.left && right === this.right) return this
        return AlgebraicBinaryMatrixMatrixMatrix(op, left, right)
    }
}

data class NamedAlgebraicBinaryMatrixMatrixMatrix(
    override val name: Id,
    val expr: AlgebraicBinaryMatrixMatrixMatrix
) : NamedMatrixExpr {
    override fun toString() = render()
    fun dup(
        name: Id = this.name,
        expr: AlgebraicBinaryMatrixMatrixMatrix = this.expr
    ): NamedAlgebraicBinaryMatrixMatrixMatrix {
        if (name === this.name && expr === this.expr) return this
        return NamedAlgebraicBinaryMatrixMatrixMatrix(name, expr)
    }
}

interface IAlgebraicBinarySummaryScalarScalarScalarFunction {
    val meta: BinarySummaryOp
    operator fun invoke(left: ScalarExpr, right: ScalarExpr): ScalarExpr =
        AlgebraicBinarySummaryScalarScalarScalar(this, left, right)

    fun reduceArithmetic(left: ScalarExpr, right: ScalarExpr): ScalarExpr?
    fun doubleOp(left: Double, right: Double): Double
    fun differentiate(
        left: ScalarExpr,
        leftd: ScalarExpr,
        right: ScalarExpr,
        rightd: ScalarExpr,
        variable: ScalarExpr
    ): ScalarExpr

    fun type(left: AlgebraicType, right: AlgebraicType): AlgebraicType
}

data class AlgebraicBinarySummaryScalarScalarScalar(
    val op : IAlgebraicBinarySummaryScalarScalarScalarFunction,
    val left : ScalarExpr,
    val right: ScalarExpr
) : ScalarExpr {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toString() = render()
    fun dup(
        op: IAlgebraicBinarySummaryScalarScalarScalarFunction = this.op,
        left: ScalarExpr = this.left,
        right: ScalarExpr = this.right
    ): AlgebraicBinarySummaryScalarScalarScalar {
        if (op === this.op && left === this.left && right === this.right) return this
        return AlgebraicBinarySummaryScalarScalarScalar(op, left, right)
    }
}

data class NamedAlgebraicBinarySummaryScalarScalarScalar(
    override val name: Id,
    val expr: AlgebraicBinarySummaryScalarScalarScalar
) : NamedScalarExpr {
    override fun toString() = render()
    fun dup(
        name: Id = this.name,
        expr: AlgebraicBinarySummaryScalarScalarScalar = this.expr
    ): NamedAlgebraicBinarySummaryScalarScalarScalar {
        if (name === this.name && expr === this.expr) return this
        return NamedAlgebraicBinarySummaryScalarScalarScalar(name, expr)
    }
}

interface IAlgebraicBinarySummaryMatrixScalarScalarFunction {
    val meta: BinarySummaryOp
    operator fun invoke(left: MatrixExpr, right: ScalarExpr): ScalarExpr =
        AlgebraicBinarySummaryMatrixScalarScalar(this, left, right)

    fun reduceArithmetic(left: MatrixExpr, right: ScalarExpr): ScalarExpr?
    fun doubleOp(left: List<Double>, right: Double): Double
    fun differentiate(
        left: MatrixExpr,
        leftd: MatrixExpr,
        right: ScalarExpr,
        rightd: ScalarExpr,
        variable: ScalarExpr
    ): ScalarExpr

    fun type(left: AlgebraicType, right: AlgebraicType): AlgebraicType
}

data class AlgebraicBinarySummaryMatrixScalarScalar(
    val op : IAlgebraicBinarySummaryMatrixScalarScalarFunction,
    val left : MatrixExpr,
    val right: ScalarExpr
) : ScalarExpr {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toString() = render()
    fun dup(
        op: IAlgebraicBinarySummaryMatrixScalarScalarFunction = this.op,
        left: MatrixExpr = this.left,
        right: ScalarExpr = this.right
    ): AlgebraicBinarySummaryMatrixScalarScalar {
        if (op === this.op && left === this.left && right === this.right) return this
        return AlgebraicBinarySummaryMatrixScalarScalar(op, left, right)
    }
}

data class NamedAlgebraicBinarySummaryMatrixScalarScalar(
    override val name: Id,
    val expr: AlgebraicBinarySummaryMatrixScalarScalar
) : NamedScalarExpr {
    override fun toString() = render()
    fun dup(
        name: Id = this.name,
        expr: AlgebraicBinarySummaryMatrixScalarScalar = this.expr
    ): NamedAlgebraicBinarySummaryMatrixScalarScalar {
        if (name === this.name && expr === this.expr) return this
        return NamedAlgebraicBinarySummaryMatrixScalarScalar(name, expr)
    }
}

