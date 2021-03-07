package com.github.jomof.kane

import com.github.jomof.kane.impl.*
import com.github.jomof.kane.impl.sheet.*
import com.github.jomof.kane.impl.types.*
import kotlin.reflect.KProperty


interface IAlgebraicSummaryScalarScalarFunction {
    val meta : SummaryOp
    operator fun invoke(value : ScalarExpr) : ScalarExpr = AlgebraicSummaryScalarScalar(this, value)
    operator fun invoke(value : Double) : ScalarExpr = AlgebraicSummaryScalarScalar(this, ConstantScalar(value))
    fun reduceArithmetic(value : ScalarExpr) : ScalarExpr?
    fun doubleOp(value : Double) : Double
    fun differentiate(value : ScalarExpr, valued : ScalarExpr, variable : ScalarExpr) : ScalarExpr
    fun type(value : AlgebraicType) : AlgebraicType
}

data class AlgebraicSummaryScalarScalar(
    val op : IAlgebraicSummaryScalarScalarFunction,
    val value : ScalarExpr,
    override val name : Id = anonymous
) : ScalarExpr, INameableScalar {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toNamed(name : Id) : ScalarExpr {
        if(this.name === anonymous) return dup(name = name)
        return NamedScalar(name, this)
    }
    override fun toUnnamed() : AlgebraicSummaryScalarScalar {
        return dup(name = anonymous)
    }
    override fun hasName() : Boolean = name !== anonymous
    override fun toString() = render()
    fun dup(op : IAlgebraicSummaryScalarScalarFunction = this.op, value : ScalarExpr = this.value, name : Id = this.name) : AlgebraicSummaryScalarScalar {
        if (op === this.op && value === this.value && name == this.name) return this
        return AlgebraicSummaryScalarScalar(op, value, name)
    }
    fun copy(value : ScalarExpr = this.value, name : Id = this.name) { error("Use dup instead") }
}

interface IAlgebraicSummaryMatrixScalarFunction {
    val meta : SummaryOp
    operator fun invoke(value : MatrixExpr) : ScalarExpr = AlgebraicSummaryMatrixScalar(this, value)
    operator fun invoke(value : SheetRangeExpr) : ScalarExpr = AlgebraicSummaryMatrixScalar(this, CoerceMatrix(value))
    fun reduceArithmetic(value : MatrixExpr) : ScalarExpr?
    fun doubleOp(value : List<Double>) : Double
    fun differentiate(value : MatrixExpr, valued : MatrixExpr, variable : ScalarExpr) : ScalarExpr
    fun type(value : AlgebraicType) : AlgebraicType
}

data class AlgebraicSummaryMatrixScalar(
    val op : IAlgebraicSummaryMatrixScalarFunction,
    val value : MatrixExpr,
    override val name : Id = anonymous
) : ScalarExpr, INameableScalar {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toNamed(name : Id) : ScalarExpr {
        if(this.name === anonymous) return dup(name = name)
        return NamedScalar(name, this)
    }
    override fun toUnnamed() : AlgebraicSummaryMatrixScalar {
        return dup(name = anonymous)
    }
    override fun hasName() : Boolean = name !== anonymous
    override fun toString() = render()
    fun dup(op : IAlgebraicSummaryMatrixScalarFunction = this.op, value : MatrixExpr = this.value, name : Id = this.name) : AlgebraicSummaryMatrixScalar {
        if (op === this.op && value === this.value && name == this.name) return this
        return AlgebraicSummaryMatrixScalar(op, value, name)
    }
    fun copy(value : MatrixExpr = this.value, name : Id = this.name) { error("Use dup instead") }
}

interface IAlgebraicUnaryScalarScalarFunction {
    val meta : UnaryOp
    operator fun invoke(value : ScalarExpr) : ScalarExpr = AlgebraicUnaryScalarScalar(this, value)
    operator fun invoke(value : Double) : ScalarExpr = AlgebraicUnaryScalarScalar(this, ConstantScalar(value))
    fun reduceArithmetic(value : ScalarExpr) : ScalarExpr?
    fun doubleOp(value : Double) : Double
    fun differentiate(value : ScalarExpr, valued : ScalarExpr, variable : ScalarExpr) : ScalarExpr
    fun type(value : AlgebraicType) : AlgebraicType
}

data class AlgebraicUnaryScalarScalar(
    val op : IAlgebraicUnaryScalarScalarFunction,
    val value : ScalarExpr,
    override val name : Id = anonymous
) : ScalarExpr, INameableScalar {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toNamed(name : Id) : ScalarExpr {
        if(this.name === anonymous) return dup(name = name)
        return NamedScalar(name, this)
    }
    override fun toUnnamed() : AlgebraicUnaryScalarScalar {
        return dup(name = anonymous)
    }
    override fun hasName() : Boolean = name !== anonymous
    override fun toString() = render()
    fun dup(op : IAlgebraicUnaryScalarScalarFunction = this.op, value : ScalarExpr = this.value, name : Id = this.name) : AlgebraicUnaryScalarScalar {
        if (op === this.op && value === this.value && name == this.name) return this
        return AlgebraicUnaryScalarScalar(op, value, name)
    }
    fun copy(value : ScalarExpr = this.value, name : Id = this.name) { error("Use dup instead") }
}

interface IAlgebraicUnaryMatrixMatrixFunction {
    val meta : UnaryOp
    operator fun invoke(value : MatrixExpr) : MatrixExpr = AlgebraicUnaryMatrixMatrix(this, value)
    operator fun invoke(value : SheetRangeExpr) : MatrixExpr = AlgebraicUnaryMatrixMatrix(this, CoerceMatrix(value))
    fun reduceArithmetic(value : MatrixExpr) : MatrixExpr?
    fun doubleOp(value : List<Double>) : List<Double>
    fun differentiate(value : MatrixExpr, valued : MatrixExpr, variable : ScalarExpr) : MatrixExpr
    fun type(value : AlgebraicType) : AlgebraicType
}

data class AlgebraicUnaryMatrixMatrix(
    val op : IAlgebraicUnaryMatrixMatrixFunction,
    val value : MatrixExpr,
    override val name : Id = anonymous
) : MatrixExpr, INameableMatrix {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toNamed(name : Id) : MatrixExpr {
        if(this.name === anonymous) return dup(name = name)
        return NamedMatrix(name, this)
    }
    override fun toUnnamed() : AlgebraicUnaryMatrixMatrix {
        return dup(name = anonymous)
    }
    override fun hasName() : Boolean = name !== anonymous
    override fun toString() = render()
    fun dup(op : IAlgebraicUnaryMatrixMatrixFunction = this.op, value : MatrixExpr = this.value, name : Id = this.name) : AlgebraicUnaryMatrixMatrix {
        if (op === this.op && value === this.value && name == this.name) return this
        return AlgebraicUnaryMatrixMatrix(op, value, name)
    }
    fun copy(value : MatrixExpr = this.value, name : Id = this.name) { error("Use dup instead") }
}

interface IAlgebraicBinaryScalarScalarScalarFunction {
    val meta : BinaryOp
    operator fun invoke(left : ScalarExpr, right : ScalarExpr) : ScalarExpr = AlgebraicBinaryScalarScalarScalar(this, left, right)
    operator fun invoke(left : ScalarExpr, right : Double) : ScalarExpr = AlgebraicBinaryScalarScalarScalar(this, left, ConstantScalar(right))
    operator fun invoke(left : Double, right : ScalarExpr) : ScalarExpr = AlgebraicBinaryScalarScalarScalar(this, ConstantScalar(left), right)
    operator fun invoke(left : Double, right : Double) : ScalarExpr = AlgebraicBinaryScalarScalarScalar(this, ConstantScalar(left), ConstantScalar(right))
    fun reduceArithmetic(left : ScalarExpr, right : ScalarExpr) : ScalarExpr?
    fun doubleOp(left : Double, right : Double) : Double
    fun differentiate(left : ScalarExpr, leftd : ScalarExpr, right : ScalarExpr, rightd : ScalarExpr, variable : ScalarExpr) : ScalarExpr
    fun type(left : AlgebraicType, right : AlgebraicType) : AlgebraicType
}

data class AlgebraicBinaryScalarScalarScalar(
    val op : IAlgebraicBinaryScalarScalarScalarFunction,
    val left : ScalarExpr,
    val right : ScalarExpr,
    override val name : Id = anonymous
) : ScalarExpr, INameableScalar {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toNamed(name : Id) : ScalarExpr {
        if(this.name === anonymous) return dup(name = name)
        return NamedScalar(name, this)
    }
    override fun toUnnamed() : AlgebraicBinaryScalarScalarScalar {
        return dup(name = anonymous)
    }
    override fun hasName() : Boolean = name !== anonymous
    override fun toString() = render()
    fun dup(op : IAlgebraicBinaryScalarScalarScalarFunction = this.op, left : ScalarExpr = this.left, right : ScalarExpr = this.right, name : Id = this.name) : AlgebraicBinaryScalarScalarScalar {
        if (op === this.op && left === this.left && right === this.right && name == this.name) return this
        return AlgebraicBinaryScalarScalarScalar(op, left, right, name)
    }
    fun copy(left : ScalarExpr = this.left, right : ScalarExpr = this.right, name : Id = this.name) { error("Use dup instead") }
}

interface IAlgebraicBinaryScalarMatrixMatrixFunction {
    val meta : BinaryOp
    operator fun invoke(left : ScalarExpr, right : MatrixExpr) : MatrixExpr = AlgebraicBinaryScalarMatrixMatrix(this, left, right)
    operator fun invoke(left : ScalarExpr, right : SheetRangeExpr) : MatrixExpr = AlgebraicBinaryScalarMatrixMatrix(this, left, CoerceMatrix(right))
    operator fun invoke(left : Double, right : MatrixExpr) : MatrixExpr = AlgebraicBinaryScalarMatrixMatrix(this, ConstantScalar(left), right)
    operator fun invoke(left : Double, right : SheetRangeExpr) : MatrixExpr = AlgebraicBinaryScalarMatrixMatrix(this, ConstantScalar(left), CoerceMatrix(right))
    fun reduceArithmetic(left : ScalarExpr, right : MatrixExpr) : MatrixExpr?
    fun doubleOp(left : Double, right : List<Double>) : List<Double>
    fun differentiate(left : ScalarExpr, leftd : ScalarExpr, right : MatrixExpr, rightd : MatrixExpr, variable : ScalarExpr) : MatrixExpr
    fun type(left : AlgebraicType, right : AlgebraicType) : AlgebraicType
}

data class AlgebraicBinaryScalarMatrixMatrix(
    val op : IAlgebraicBinaryScalarMatrixMatrixFunction,
    val left : ScalarExpr,
    val right : MatrixExpr,
    override val name : Id = anonymous
) : MatrixExpr, INameableMatrix {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toNamed(name : Id) : MatrixExpr {
        if(this.name === anonymous) return dup(name = name)
        return NamedMatrix(name, this)
    }
    override fun toUnnamed() : AlgebraicBinaryScalarMatrixMatrix {
        return dup(name = anonymous)
    }
    override fun hasName() : Boolean = name !== anonymous
    override fun toString() = render()
    fun dup(op : IAlgebraicBinaryScalarMatrixMatrixFunction = this.op, left : ScalarExpr = this.left, right : MatrixExpr = this.right, name : Id = this.name) : AlgebraicBinaryScalarMatrixMatrix {
        if (op === this.op && left === this.left && right === this.right && name == this.name) return this
        return AlgebraicBinaryScalarMatrixMatrix(op, left, right, name)
    }
    fun copy(left : ScalarExpr = this.left, right : MatrixExpr = this.right, name : Id = this.name) { error("Use dup instead") }
}

interface IAlgebraicBinaryMatrixScalarMatrixFunction {
    val meta : BinaryOp
    operator fun invoke(left : MatrixExpr, right : ScalarExpr) : MatrixExpr = AlgebraicBinaryMatrixScalarMatrix(this, left, right)
    operator fun invoke(left : MatrixExpr, right : Double) : MatrixExpr = AlgebraicBinaryMatrixScalarMatrix(this, left, ConstantScalar(right))
    operator fun invoke(left : SheetRangeExpr, right : ScalarExpr) : MatrixExpr = AlgebraicBinaryMatrixScalarMatrix(this, CoerceMatrix(left), right)
    operator fun invoke(left : SheetRangeExpr, right : Double) : MatrixExpr = AlgebraicBinaryMatrixScalarMatrix(this, CoerceMatrix(left), ConstantScalar(right))
    fun reduceArithmetic(left : MatrixExpr, right : ScalarExpr) : MatrixExpr?
    fun doubleOp(left : List<Double>, right : Double) : List<Double>
    fun differentiate(left : MatrixExpr, leftd : MatrixExpr, right : ScalarExpr, rightd : ScalarExpr, variable : ScalarExpr) : MatrixExpr
    fun type(left : AlgebraicType, right : AlgebraicType) : AlgebraicType
}

data class AlgebraicBinaryMatrixScalarMatrix(
    val op : IAlgebraicBinaryMatrixScalarMatrixFunction,
    val left : MatrixExpr,
    val right : ScalarExpr,
    override val name : Id = anonymous
) : MatrixExpr, INameableMatrix {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toNamed(name : Id) : MatrixExpr {
        if(this.name === anonymous) return dup(name = name)
        return NamedMatrix(name, this)
    }
    override fun toUnnamed() : AlgebraicBinaryMatrixScalarMatrix {
        return dup(name = anonymous)
    }
    override fun hasName() : Boolean = name !== anonymous
    override fun toString() = render()
    fun dup(op : IAlgebraicBinaryMatrixScalarMatrixFunction = this.op, left : MatrixExpr = this.left, right : ScalarExpr = this.right, name : Id = this.name) : AlgebraicBinaryMatrixScalarMatrix {
        if (op === this.op && left === this.left && right === this.right && name == this.name) return this
        return AlgebraicBinaryMatrixScalarMatrix(op, left, right, name)
    }
    fun copy(left : MatrixExpr = this.left, right : ScalarExpr = this.right, name : Id = this.name) { error("Use dup instead") }
}

interface IAlgebraicBinaryMatrixMatrixMatrixFunction {
    val meta : BinaryOp
    operator fun invoke(left : MatrixExpr, right : MatrixExpr) : MatrixExpr = AlgebraicBinaryMatrixMatrixMatrix(this, left, right)
    operator fun invoke(left : MatrixExpr, right : SheetRangeExpr) : MatrixExpr = AlgebraicBinaryMatrixMatrixMatrix(this, left, CoerceMatrix(right))
    operator fun invoke(left : SheetRangeExpr, right : MatrixExpr) : MatrixExpr = AlgebraicBinaryMatrixMatrixMatrix(this, CoerceMatrix(left), right)
    operator fun invoke(left : SheetRangeExpr, right : SheetRangeExpr) : MatrixExpr = AlgebraicBinaryMatrixMatrixMatrix(this, CoerceMatrix(left), CoerceMatrix(right))
    fun reduceArithmetic(left : MatrixExpr, right : MatrixExpr) : MatrixExpr?
    fun doubleOp(left : List<Double>, right : List<Double>) : List<Double>
    fun differentiate(left : MatrixExpr, leftd : MatrixExpr, right : MatrixExpr, rightd : MatrixExpr, variable : ScalarExpr) : MatrixExpr
    fun type(left : AlgebraicType, right : AlgebraicType) : AlgebraicType
}

data class AlgebraicBinaryMatrixMatrixMatrix(
    val op : IAlgebraicBinaryMatrixMatrixMatrixFunction,
    val left : MatrixExpr,
    val right : MatrixExpr,
    override val name : Id = anonymous
) : MatrixExpr, INameableMatrix {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toNamed(name : Id) : MatrixExpr {
        if(this.name === anonymous) return dup(name = name)
        return NamedMatrix(name, this)
    }
    override fun toUnnamed() : AlgebraicBinaryMatrixMatrixMatrix {
        return dup(name = anonymous)
    }
    override fun hasName() : Boolean = name !== anonymous
    override fun toString() = render()
    fun dup(op : IAlgebraicBinaryMatrixMatrixMatrixFunction = this.op, left : MatrixExpr = this.left, right : MatrixExpr = this.right, name : Id = this.name) : AlgebraicBinaryMatrixMatrixMatrix {
        if (op === this.op && left === this.left && right === this.right && name == this.name) return this
        return AlgebraicBinaryMatrixMatrixMatrix(op, left, right, name)
    }
    fun copy(left : MatrixExpr = this.left, right : MatrixExpr = this.right, name : Id = this.name) { error("Use dup instead") }
}

interface IAlgebraicBinarySummaryScalarScalarScalarFunction {
    val meta : BinarySummaryOp
    operator fun invoke(left : ScalarExpr, right : ScalarExpr) : ScalarExpr = AlgebraicBinarySummaryScalarScalarScalar(this, left, right)
    operator fun invoke(left : ScalarExpr, right : Double) : ScalarExpr = AlgebraicBinarySummaryScalarScalarScalar(this, left, ConstantScalar(right))
    operator fun invoke(left : Double, right : ScalarExpr) : ScalarExpr = AlgebraicBinarySummaryScalarScalarScalar(this, ConstantScalar(left), right)
    operator fun invoke(left : Double, right : Double) : ScalarExpr = AlgebraicBinarySummaryScalarScalarScalar(this, ConstantScalar(left), ConstantScalar(right))
    fun reduceArithmetic(left : ScalarExpr, right : ScalarExpr) : ScalarExpr?
    fun doubleOp(left : Double, right : Double) : Double
    fun differentiate(left : ScalarExpr, leftd : ScalarExpr, right : ScalarExpr, rightd : ScalarExpr, variable : ScalarExpr) : ScalarExpr
    fun type(left : AlgebraicType, right : AlgebraicType) : AlgebraicType
}

data class AlgebraicBinarySummaryScalarScalarScalar(
    val op : IAlgebraicBinarySummaryScalarScalarScalarFunction,
    val left : ScalarExpr,
    val right : ScalarExpr,
    override val name : Id = anonymous
) : ScalarExpr, INameableScalar {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toNamed(name : Id) : ScalarExpr {
        if(this.name === anonymous) return dup(name = name)
        return NamedScalar(name, this)
    }
    override fun toUnnamed() : AlgebraicBinarySummaryScalarScalarScalar {
        return dup(name = anonymous)
    }
    override fun hasName() : Boolean = name !== anonymous
    override fun toString() = render()
    fun dup(op : IAlgebraicBinarySummaryScalarScalarScalarFunction = this.op, left : ScalarExpr = this.left, right : ScalarExpr = this.right, name : Id = this.name) : AlgebraicBinarySummaryScalarScalarScalar {
        if (op === this.op && left === this.left && right === this.right && name == this.name) return this
        return AlgebraicBinarySummaryScalarScalarScalar(op, left, right, name)
    }
    fun copy(left : ScalarExpr = this.left, right : ScalarExpr = this.right, name : Id = this.name) { error("Use dup instead") }
}

interface IAlgebraicBinarySummaryMatrixScalarScalarFunction {
    val meta : BinarySummaryOp
    operator fun invoke(left : MatrixExpr, right : ScalarExpr) : ScalarExpr = AlgebraicBinarySummaryMatrixScalarScalar(this, left, right)
    operator fun invoke(left : MatrixExpr, right : Double) : ScalarExpr = AlgebraicBinarySummaryMatrixScalarScalar(this, left, ConstantScalar(right))
    operator fun invoke(left : SheetRangeExpr, right : ScalarExpr) : ScalarExpr = AlgebraicBinarySummaryMatrixScalarScalar(this, CoerceMatrix(left), right)
    operator fun invoke(left : SheetRangeExpr, right : Double) : ScalarExpr = AlgebraicBinarySummaryMatrixScalarScalar(this, CoerceMatrix(left), ConstantScalar(right))
    fun reduceArithmetic(left : MatrixExpr, right : ScalarExpr) : ScalarExpr?
    fun doubleOp(left : List<Double>, right : Double) : Double
    fun differentiate(left : MatrixExpr, leftd : MatrixExpr, right : ScalarExpr, rightd : ScalarExpr, variable : ScalarExpr) : ScalarExpr
    fun type(left : AlgebraicType, right : AlgebraicType) : AlgebraicType
}

data class AlgebraicBinarySummaryMatrixScalarScalar(
    val op : IAlgebraicBinarySummaryMatrixScalarScalarFunction,
    val left : MatrixExpr,
    val right : ScalarExpr,
    override val name : Id = anonymous
) : ScalarExpr, INameableScalar {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toNamed(name : Id) : ScalarExpr {
        if(this.name === anonymous) return dup(name = name)
        return NamedScalar(name, this)
    }
    override fun toUnnamed() : AlgebraicBinarySummaryMatrixScalarScalar {
        return dup(name = anonymous)
    }
    override fun hasName() : Boolean = name !== anonymous
    override fun toString() = render()
    fun dup(op : IAlgebraicBinarySummaryMatrixScalarScalarFunction = this.op, left : MatrixExpr = this.left, right : ScalarExpr = this.right, name : Id = this.name) : AlgebraicBinarySummaryMatrixScalarScalar {
        if (op === this.op && left === this.left && right === this.right && name == this.name) return this
        return AlgebraicBinarySummaryMatrixScalarScalar(op, left, right, name)
    }
    fun copy(left : MatrixExpr = this.left, right : ScalarExpr = this.right, name : Id = this.name) { error("Use dup instead") }
}

