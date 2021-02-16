@file:Suppress("UNCHECKED_CAST")

package com.github.jomof.kane.impl

import com.github.jomof.kane.*
import com.github.jomof.kane.impl.functions.*
import com.github.jomof.kane.impl.sheet.*
import com.github.jomof.kane.impl.types.AlgebraicType
import com.github.jomof.kane.impl.types.KaneType
import com.github.jomof.kane.impl.types.StringKaneType
import com.github.jomof.kane.impl.types.kaneDouble
import kotlin.reflect.KProperty


interface VariableExpr : AlgebraicExpr
interface ScalarVariableExpr : ScalarExpr, VariableExpr
interface MatrixVariableExpr : MatrixExpr, VariableExpr

data class UnaryOp(val op: String = "", val simpleName: String = "") {
    operator fun getValue(nothing: Nothing?, property: KProperty<*>) =
        (if (op.isBlank()) copy(op = property.name.toLowerCase())
        else this).copy(simpleName = property.name.toLowerCase())
}

data class SummaryOp(val op: String = "", val simpleName: String = "") {
    operator fun getValue(nothing: Nothing?, property: KProperty<*>) =
        (if (op.isBlank()) copy(op = property.name.toLowerCase())
        else this).copy(simpleName = property.name.toLowerCase())
}

data class BinaryOp(
    val op: String = "",
    val precedence: Int,
    val associative: Boolean = false,
    val infix: Boolean = false
) {
    operator fun getValue(nothing: Nothing?, property: KProperty<*>) =
        if (op.isBlank()) copy(op = property.name.toLowerCase())
        else this
}

data class BinarySummaryOp(
    val op: String = "",
    val precedence: Int,
    val associative: Boolean = false,
    val infix: Boolean = false
) {
    operator fun getValue(nothing: Nothing?, property: KProperty<*>) =
        if (op.isBlank()) copy(op = property.name.toLowerCase())
        else this
}

data class ConstantScalar(
    val value: Double,
    override val name: Id = anonymous
) : ScalarExpr, INameableScalar {
    override fun toNamed(name: Id): ScalarExpr {
        if (this.name === anonymous) return dup(name = name)
        return NamedScalar(name, this)
    }

    override fun toUnnamed() = dup(name = anonymous)
    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toString() = render()
    fun dup(name: Id = this.name): ConstantScalar {
        if (name === this.name) return this
        return copy(name = name)
    }
}

data class ValueExpr<E : Any>(
    val value: E,
    override val type: KaneType<E>,
    override val name: Id = anonymous
) : TypedExpr<E>, INameable {
    init {
        assert(value !is Expr) {
            "${value.javaClass}"
        }
    }

    override fun toNamed(name: Id) = dup(name = name)
    override fun toUnnamed() = dup(name = anonymous)
    override fun toString() = type.render(value)
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)

    fun dup(value: E = this.value, type: KaneType<E> = this.type, name: Id = this.name): ValueExpr<E> {
        if (value === this.value && type == this.type && name == this.name) return this
        return copy(value = value, type = type, name = name)
    }
}

data class ScalarVariable(
    val initial: Double,
    override val name: Id = anonymous
) : AlgebraicExpr, INameableScalar {
    override fun toNamed(name: Id) = dup(name = name)
    override fun toUnnamed() = dup(name = anonymous)
    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toString() = render()
    fun dup(initial: Double = this.initial, name: Id = this.name): ScalarVariable {
        if (initial == this.initial && name == this.name) return this
        return copy(initial = initial, name = name)
    }
}


data class MatrixVariable(
    val columns: Int,
    val rows: Int,
    val initial: List<Double>,
    override val name: Id = anonymous
) : AlgebraicExpr, INameableMatrix {
    override fun toNamed(name: Id) = dup(name = name)
    override fun toUnnamed() = dup(name = anonymous)
    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toString() = render()
    fun get(coordinate: Coordinate) = get(coordinate.column, coordinate.row)
    val elements get() = coordinates.map { get(it) }
    fun dup(
        columns: Int = this.columns,
        rows: Int = this.rows,
        initial: List<Double> = this.initial,
        name: Id = this.name
    ): MatrixVariable {
        if (columns == this.columns && rows == this.rows && initial === this.initial && name == this.name) return this
        return copy(initial = initial, name = name)
    }
}


data class NamedScalar(
    override val name: Id,
    val scalar: ScalarExpr
) : NamedScalarExpr {
    init {
        Identifier.validate(name)
        assert(scalar !is ConstantScalar || scalar.hasName())
        assert(scalar !is RetypeScalar || scalar.hasName())
        assert(scalar !is DiscreteUniformRandomVariable || scalar.hasName())
    }

    override fun toString() = render()

    fun dup(scalar: ScalarExpr = this.scalar, name: Id = this.name): NamedScalar {
        if (scalar === this.scalar && name == this.name) return this
        return NamedScalar(name, scalar)
    }
}

data class NamedMatrix(
    override val name: Id,
    val matrix: MatrixExpr
) : NamedMatrixExpr {
    init {
        track()
        assert(matrix !is NamedMatrix) {
            "Named of named"
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>) = NamedMatrix(property.name, matrix)
    override fun toString() = render()

    fun dup(matrix: MatrixExpr = this.matrix, name: Id = this.name): NamedMatrix {
        if (matrix === this.matrix && name == this.name) return this
        return NamedMatrix(name, matrix)
    }
}

data class MatrixVariableElement(
    val column: Int,
    val row: Int,
    val matrix: MatrixVariable,
    val initial: Double
) : ScalarExpr {
    init {
        track()
    }

    override fun toString() = render()
    override fun hashCode() = ((matrix.name.hashCode() * 3) + column) * 5 + row
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is AlgebraicExpr) return false
        if (other !is MatrixVariableElement) return false
        if (matrix.name != other.matrix.name) return false
        if (column != other.column) return false
        if (row != other.row) return false
        return true
    }

    fun dup(matrix: MatrixVariable = this.matrix): MatrixVariableElement {
        if (matrix === this.matrix) return this
        return MatrixVariableElement(column, row, matrix, initial)
    }
}

/**
 * Change the type of the underlying ScalarExpr
 */
data class RetypeScalar(
    val scalar: ScalarExpr,
    val type: AlgebraicType,
    override val name: Id = anonymous
) : ScalarExpr, INameableScalar {
    override fun toString() = render()
    override fun toNamed(name: Id): ScalarExpr {
        if (this.name === anonymous) return dup(name = name)
        return NamedScalar(name, this)
    }

    override fun toUnnamed() = dup(name = anonymous)

    fun dup(scalar: ScalarExpr = this.scalar, type: AlgebraicType = this.type, name: Id = this.name): RetypeScalar {
        if (scalar === this.scalar && type === this.type && name === this.name) return this
        return RetypeScalar(scalar, type, name)
    }
}

/**
 * Change the type of the underlying MatrixExpr
 */
data class RetypeMatrix(
    val matrix: MatrixExpr,
    val type: AlgebraicType
) : MatrixExpr {
    override fun toString() = render()
    fun dup(matrix: MatrixExpr = this.matrix, type: AlgebraicType = this.type): RetypeMatrix {
        if (matrix === this.matrix && type === this.type) return this
        return RetypeMatrix(matrix, type)
    }
}

data class DataMatrix(
    val columns: Int,
    val rows: Int,
    val elements: List<ScalarExpr>
) : MatrixExpr {
    init {
        assert(!elements.any { it is StatisticExpr }) {
            "unexpected"
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toString() = render()
}

data class Tableau(
    val children: List<AlgebraicExpr>
) : AlgebraicExpr {
    init {
        assert(children.all { it.hasName() })
        track()
    }

    override fun toString() = render()
}

data class Slot(
    val slot: Int,
    val replaces: ScalarExpr
) : ScalarExpr {
    init {
        track()
    }

    override fun toString() = render()
}

data class ScalarAssign(
    val left: ScalarVariable,
    val right: ScalarExpr,
    override val name: Id = anonymous,
) : AlgebraicExpr, INameable {
    fun dup(left: ScalarVariable = this.left, right: ScalarExpr = this.right, name: Id = this.name): ScalarAssign {
        if (left === this.left && right === this.right && name == this.name) return this
        return ScalarAssign(left, right, name)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toNamed(name: Id) = dup(name = name)
    override fun toUnnamed() = dup(name = anonymous)
    override fun toString() = render()
}

data class MatrixAssign(
    val left: MatrixVariable,
    val right: MatrixExpr,
    override val name: Id = anonymous,
) : AlgebraicExpr, INameable {
    fun dup(left: MatrixVariable = this.left, right: MatrixExpr = this.right, name: Id = this.name): MatrixAssign {
        if (left === this.left && right === this.right && name == this.name) return this
        return MatrixAssign(left, right, name)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toNamed(name: Id) = dup(name = name)
    override fun toUnnamed() = dup(name = anonymous)
    override fun toString() = render()
}

// Assign
fun assign(assignment: Pair<ScalarExpr, ScalarVariable>) = ScalarAssign(assignment.second, assignment.first)
fun assign(assignment: Pair<MatrixExpr, MatrixVariable>) = MatrixAssign(assignment.second, assignment.first)

// Variables
fun matrixVariable(columns: Int, rows: Int) =
    MatrixVariable(columns, rows, (0 until rows * columns).map { 0.0 })

fun matrixVariable(columns: Int, rows: Int, vararg elements: Double) =
    MatrixVariable(columns, rows, elements.toList().map { it })

fun matrixVariable(columns: Int, rows: Int, init: (Coordinate) -> Double) =
    MatrixVariable(columns, rows, coordinatesOf(columns, rows).toList().map { init(it) })

fun matrixVariable(columns: Int, rows: Int, type: AlgebraicType, init: (Coordinate) -> Double) =
    MatrixVariable(columns, rows, coordinatesOf(columns, rows).toList().map { type.coerceFrom(init(it)) })

fun variable(initial: Double = 0.0) = ScalarVariable(initial)

// Constant
fun constant(value: Double) = ConstantScalar(value)
fun constant(value: Int) = constant(value.toDouble())
fun constant(value: String) = ValueExpr(value, StringKaneType.kaneType)
//inline fun <reified E:Any> constant(value : E) = ValueExpr(value, object : KaneType<E>(E::class.java) { })

// Tableau
fun tableauOf(vararg elements: AlgebraicExpr): Tableau = Tableau(elements.toList())

// Misc
fun repeated(columns: Int, rows: Int, value: Double): MatrixExpr =
    repeated(columns, rows, ConstantScalar(value))

fun repeated(columns: Int, rows: Int, value: ScalarExpr): MatrixExpr =
    DataMatrix(columns, rows, (0 until columns * rows).map { value })

fun Expr.canGetConstant(): Boolean = when (this) {
    is RetypeScalar -> scalar.canGetConstant()
    is ConstantScalar -> true
    is NamedScalar -> scalar.canGetConstant()
    is CoerceScalar ->
        if (value is ScalarExpr) value.canGetConstant()
        else false
    is ValueExpr<*> -> value is Number
    else -> false
}

fun Expr.getConstant(): Double = when (this) {
    is RetypeScalar -> scalar.getConstant()
    is ConstantScalar -> value
    is NamedScalar -> scalar.getConstant()
    is CoerceScalar ->
        if (value is ScalarExpr) value.getConstant()
        else error("Should call canGetConstant first")
    is ValueExpr<*> -> (value as Number).toDouble()
    else ->
        error("Should call canGetConstant first")
}

// Coordinates
inline operator fun <reified M : MatrixExpr> M.get(coord: Coordinate) = get(coord.column, coord.row)
val MatrixExpr.coordinates get() = coordinatesOf(columns, rows)
inline val <reified M : MatrixExpr> M.elements get() = coordinates.map { get(it) }


// Data matrix

fun MatrixExpr.toDataMatrix(): MatrixExpr = when {
    this is DataMatrix -> this
    else -> DataMatrix(columns, rows, elements.toList()).withNameOf(this)
}

fun MatrixExpr.toDataMatrix(columns: Int, rows: Int): MatrixExpr = when (this) {
    is DataMatrix -> this
    else -> {
        val elements = coordinatesOf(columns to rows).map { coordinate ->
            getMatrixElement(coordinate.column, coordinate.row)
        }
        DataMatrix(columns, rows, elements).withNameOf(this)
    }
}

// Allocation tracking
private var trackingEnabled = false
private var trackingCount = 0
fun errorIfTrackingEnabled() {
    if (trackingEnabled) error("tracking is enabled")
}

private val traceMap = mutableMapOf<List<StackTraceElement>, Int>()
private var offender: List<StackTraceElement> = listOf()
private fun trackImpl() {
    ++trackingCount
    if (trackingCount % 1000 == 0) {
        val stack = try {
            error("")
        } catch (e: Exception) {
            val original = e.stackTrace.toList()
            val noJomof = original.filter { it.toString().contains("jomof") }
            val no1Line = noJomof.filter { it.lineNumber > 1 }
            var result = no1Line.drop(1)
            for (dropText in listOf(
                ".mapChildren(",
                ".dispatchExpr(",
                ".unsafeReplace(",
                "\$unsafeReplace\$",
                "\$wrap\$1",
                ".invoke("
            )) {
                if (result.size < 4) break
                result = result.filter { !it.toString().contains(dropText) }
            }
            if (result.size == 2) {
                var check = no1Line.drop(1)
                for (dropText in listOf(
                    ".mapChildren(",
                    ".dispatchExpr(",
                    ".unsafeReplace(",
                    "\$unsafeReplace\$",
                    "\$wrap\$1",
                    ".invoke("
                )) {
                    if (check.size < 4)
                        break
                    val reduced = check.filter { !it.toString().contains(dropText) }
                    if (reduced.size < 4)
                        break
                    check = reduced
                }
            }
            result
        }
        traceMap[stack] = traceMap.computeIfAbsent(stack) { 0 } + 1
        val count = traceMap[stack]!!
        if (traceMap[offender] ?: 0 < count) {
            offender = stack
        }
    }
}

internal fun track() {
    if (trackingEnabled) trackImpl()
}


// Render
fun binaryRequiresParents(parent: BinaryOp, child: BinaryOp, childIsRight: Boolean): Boolean {
    fun bit(check: Boolean, shift: Int): Int = if (check) (0x1 shl shift) else 0
    val sig =
        bit(parent.precedence < child.precedence, 1) +
                bit(child.precedence < parent.precedence, 2) +
                bit(parent == MINUS, 3) +
                bit(parent == DIV, 4) +
                bit(child == PLUS, 5) +
                bit(child.associative, 6) +
                bit(parent.associative, 7) +
                bit(childIsRight, 8)
    //print("$sig,")
    return when (sig) {
        12, 268, 20, 276, 354, 108, 224, 480, 192, 448, 8, 196, 452, 76, 332, 388, 132 -> false
        else -> true
    }
}

fun Expr.tryGetBinaryOp(): BinaryOp? = when (this) {
    is AlgebraicBinaryScalarScalarScalar -> op.meta
    is AlgebraicBinaryMatrixMatrixMatrix -> op.meta
    is AlgebraicBinaryMatrixScalarMatrix -> op.meta
    is AlgebraicBinaryScalarMatrixMatrix -> op.meta
    is AlgebraicDeferredDataMatrix -> op
    else -> null
}

fun requiresParens(parentBinOp: BinaryOp?, childBinOp: BinaryOp?, childIsRight: Boolean): Boolean {
    if (parentBinOp == null || childBinOp == null) return false
    return binaryRequiresParents(parentBinOp, childBinOp, childIsRight)
}

fun requiresParens(parent: Expr, child: Expr, childIsRight: Boolean): Boolean {
    return requiresParens(parent.tryGetBinaryOp(), child.tryGetBinaryOp(), childIsRight)
}

fun tryConvertToSuperscript(value: String): String? {
    val sb = StringBuilder()
    for (c in value) {
        sb.append(
            when (c) {
                '-' -> '⁻'
                '+' -> '⁺'
                '=' -> '⁼'
                '(' -> '⁽'
                ')' -> '⁾'
                'n' -> 'ⁿ'
                'i' -> 'ⁱ'
                'a' -> 'ª'
                'V' -> 'ⱽ'
                'h' -> 'ʰ'
                'j' -> 'ʲ'
                'r' -> 'ʳ'
                'w' -> 'ʷ'
                'y' -> 'ʸ'
                'l' -> 'ˡ'
                's' -> 'ˢ'
                'x' -> 'ˣ'
                'b' -> 'ᵇ'
                'd' -> 'ᵈ'
                'e' -> 'ᵉ'
                'g' -> 'ᵍ'
                'k' -> 'ᵏ'
                'm' -> 'ᵐ'
                'p' -> 'ᵖ'
                't' -> 'ᵗ'
                'u' -> 'ᵘ'
                'v' -> 'ᵛ'
                'f' -> 'ᶠ'
                'z' -> 'ᶻ'
                '0' -> '⁰'
                '1' -> '¹'
                '2' -> '²'
                '3' -> '³'
                '4' -> '⁴'
                '5' -> '⁵'
                '6' -> '⁶'
                '7' -> '⁷'
                '8' -> '⁸'
                '9' -> '⁹'
                else -> return null
            }
        )
    }
    return sb.toString()
}

fun Expr.render(entryPoint: Boolean = true, outerType: AlgebraicType? = null): String {
    val parent = this

    fun Expr.self(childIsRight: Boolean = false) =
        when {
            requiresParens(parent, this, childIsRight) -> "(${render(false, outerType)})"
            else -> render(false, outerType)
        }

    fun binary(op: BinaryOp, left: Expr, right: Expr): String =
        when {
            op.infix -> "${left.self()}${op.op}${right.self(true)}"
            else -> "${op.op}(${left.self()},${right.self()})"
        }

    fun binary(op: BinarySummaryOp, left: Expr, right: Expr): String =
        when {
            op.infix -> "${left.self()}${op.op}${right.self(true)}"
            else -> "${op.op}(${left.self()},${right.self()})"
        }
    if (!entryPoint && hasName()) return Identifier.string(name)
    if (this is NamedMatrix) {
        return when {
            !entryPoint -> Identifier.string(name)
            matrix is DataMatrix && columns == 1 -> "$name=${matrix.self()}"
            matrix is DataMatrix && rows != 1 -> "\n$name\n------\n${matrix.self()}"
            else -> "$name=${matrix.self()}"
        }
    }
    if ((this is ScalarVariable && hasName()) || (this is MatrixVariable && hasName())) {
        return Identifier.string(name)
    }
    if (this is ScalarAssign) {
        return "${left.self()} <- ${right.self()}"
    }
    if (this is MatrixAssign) {
        return "${left.self()} <- ${right.self()}"
    }
    val result = when (this) {
        is ScalarVariable -> kaneDouble.render(initial)
        is CoerceScalar -> value.self()
        is CoerceMatrix -> value.self()
        is SheetRangeExpr -> rangeRef.toString()
        is CellSheetRangeExpr -> rangeRef.toString()
        is RetypeScalar -> scalar.render(entryPoint, outerType ?: this.type)
        is RetypeMatrix -> matrix.render(entryPoint, outerType ?: this.type)
        is Slot -> "\$$slot"
        is AlgebraicUnaryMatrixMatrix -> when {
            op == exp -> {
                val rightSuper = tryConvertToSuperscript(value.self())
                if (rightSuper == null) "${op.meta.op}(${value.self()})"
                else "e$rightSuper"
            }
            op == d && value is MatrixVariable -> "${op.meta.op}${value.self()}"
            op == negate &&
                    (value is MatrixVariable ||
                            value is AlgebraicBinaryMatrixMatrixMatrix && value.op == times) -> "${op.meta.op}${value.self()}"
            else -> "${op.meta.op}(${value.self()})"
        }
        is AlgebraicBinarySummaryScalarScalarScalar -> {
            when (op) {
                pow -> {
                    val rightSuper = tryConvertToSuperscript(right.self())
                    if (rightSuper == null) binary(POW, left, right)
                    else "${left.self()}$rightSuper"

                }
                else -> binary(op.meta, left, right)
            }
        }
        is AlgebraicBinaryMatrixMatrixMatrix -> {
            when (op) {
                pow -> {
                    val rightSuper = tryConvertToSuperscript(right.self())
                    if (rightSuper == null) binary(POW, left, right)
                    else "${left.self()}$rightSuper"

                }
                else -> binary(op.meta, left, right)
            }
        }
        is AlgebraicBinaryScalarScalarScalar -> {
            when (op) {
                pow -> {
                    val rightSuper = tryConvertToSuperscript(right.self())
                    if (rightSuper == null) binary(POW, left, right)
                    else "${left.self()}$rightSuper"

                }
                else -> binary(op.meta, left, right)
            }
        }
        is AlgebraicBinarySummaryMatrixScalarScalar -> {
            when (op) {
                pow -> {
                    val rightSuper = tryConvertToSuperscript(right.self())
                    if (rightSuper == null) binary(POW, left, right)
                    else "${left}$rightSuper"

                }
                else -> binary(op.meta, left, right)
            }
        }
        is AlgebraicBinaryMatrixScalarMatrix -> binary(op.meta, left, right)
        is AlgebraicBinaryScalarMatrixMatrix -> binary(op.meta, left, right)
        is NamedScalar -> scalar.self()
        is DiscreteUniformRandomVariable -> {
            val min = values.minByOrNull { it } ?: 0.0
            val max = values.maxByOrNull { it } ?: 0.0
            "random($min to $max)"
        }
        is MatrixVariableElement -> "${matrix.name}[$column,$row]"
        is ConstantScalar -> (outerType ?: kaneDouble).render(value)
        is AlgebraicSummaryScalarScalar -> "${op.meta.op}(${value.self()})"
        is AlgebraicSummaryMatrixScalar -> "${op.meta.op}(${value.self()})"
        is AlgebraicUnaryScalarScalar -> when {
            op == exp -> {
                val rightSuper = tryConvertToSuperscript(value.self())
                if (rightSuper == null) "${op.meta.op}(${value.self()})"
                else "e$rightSuper"
            }
            op == d && value is ScalarVariable -> "${op.meta.op}${value.self()}"
            op == negate &&
                    (value is ScalarVariable ||
                            value is AlgebraicBinaryScalarScalarScalar && value.op == times) -> "${op.meta.op}${value.self()}"
            else -> "${op.meta.op}(${value.self()})"
        }
        is MatrixVariable -> "matrixVariable(${columns}x$rows)"
        is ValueExpr<*> -> toString()
        is Tableau -> {
            val sb = StringBuilder()
            val sorted = children.sortedBy {
                when (it) {
                    is ScalarExpr -> 0
                    else -> 1
                }
            }
            sorted.forEach { sb.append("$it\n") }
            "$sb".trimEnd('\n')
        }
        is AlgebraicDeferredDataMatrix -> {
            if (op.infix) "${left.self()}${op.op}${right.self(true)}"
            else "${op.op}(${left.self()}, ${right.self()})"
        }
        is DataMatrix -> {
            if (rows == 1 && columns == 1) {
                "[${elements[0].self()}]"
            } else if (columns == 1) {
                val children = elements.map { it.self() }
                val maxWidth = children.map { it.length }.maxOrNull()!!
                if (maxWidth < 12) {
                    "[" + children.joinToString("|") + "]ᵀ"
                } else {
                    children.joinToString("\n")
                }
            } else if (rows == 1) {
                elements.joinToString("|") { it.self() }
            } else {
                val sb = StringBuilder()
                sb.append("")
                for (row in 0 until rows) {
                    for (column in 0 until columns) {
                        val scalar = this[column, row]
                        sb.append(scalar.self())
                        if (column != columns - 1) sb.append("|")

                    }
                    if (row != rows - 1) sb.append("\n")
                }
                "$sb"
            }
        }
        else -> toString()
    }
    if (entryPoint && hasName())
        return Identifier.string(name) + "=$result"
    return result
}
