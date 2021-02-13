@file:Suppress("UNCHECKED_CAST")

package com.github.jomof.kane.impl

import com.github.jomof.kane.*
import com.github.jomof.kane.impl.functions.*
import com.github.jomof.kane.impl.sheet.CoerceMatrix
import com.github.jomof.kane.impl.sheet.CoerceScalar
import com.github.jomof.kane.impl.sheet.NamedSheetRangeExpr
import com.github.jomof.kane.impl.sheet.SheetRangeExpr
import com.github.jomof.kane.impl.types.AlgebraicType
import com.github.jomof.kane.impl.types.KaneType
import com.github.jomof.kane.impl.types.StringKaneType
import com.github.jomof.kane.impl.types.kaneDouble
import kotlin.reflect.KProperty


interface VariableExpr : AlgebraicExpr
interface ScalarVariableExpr : ScalarExpr, VariableExpr
interface MatrixVariableExpr : MatrixExpr, VariableExpr

interface NamedExpr : Expr {
    val name: Id
}

interface NamedAlgebraicExpr : NamedExpr, AlgebraicExpr
interface NamedScalarExpr : NamedAlgebraicExpr, ScalarExpr
interface NamedMatrixExpr : NamedAlgebraicExpr, MatrixExpr
data class UnaryOp(val op: String = "", val simpleName: String = "") {
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

data class ConstantScalar(
    val value: Double
) : ScalarExpr {
    init {
        track()
    }

    override fun toString() = render()
}

data class ValueExpr<E : Any>(
    val value: E,
    override val type: KaneType<E>
) : TypedExpr<E> {
    init {
        assert(value !is Expr) {
            "${value.javaClass}"
        }
        track()
    }

    override fun toString() = type.render(value)
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    fun toNamedValueExpr(name: Id) = NamedValueExpr(name, value, type)
}

data class NamedValueExpr<E : Any>(
    override val name: Id,
    val value: E,
    override val type: KaneType<E>
) : NamedExpr, TypedExpr<E> {
    init {
        Identifier.validate(name)
        assert(value !is Expr)
        track()
    }

    fun toUnnamedValueExpr() = ValueExpr(value, type)
    override fun toString() = type.render(value)
}

data class ScalarVariable(
    val initial: Double
) : AlgebraicExpr {
    init {
        track()
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toString() = render()
}

data class NamedScalarVariable(
    override val name: Id,
    val initial: Double
) : ScalarVariableExpr, NamedScalarExpr {
    init {
        track()
        Identifier.validate(name)
    }

    override fun toString() = render()
}

data class MatrixVariable(
    val columns: Int,
    val rows: Int,
    val initial: List<Double>
) : AlgebraicExpr {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toString() = "matrixVariable(${columns}x$rows)"
}

data class NamedMatrixVariable(
    override val name: Id,
    val columns: Int,
    val initial: List<Double>
) : MatrixVariableExpr, NamedMatrixExpr {
    fun get(coordinate: Coordinate) = get(coordinate.column, coordinate.row)
    val elements get() = coordinates.map { get(it) }
    val rows get() = initial.size / columns
    override fun toString() = render()
}

data class NamedScalar(
    override val name: Id,
    val scalar: ScalarExpr
) : NamedScalarExpr {
    init {
        track()
        if (scalar is NamedScalar && scalar.name == name) {
            error("Nested named scalar?")
        }
        Identifier.validate(name)
    }

    override fun toString() = render()
}

data class NamedMatrix(
    override val name: Id,
    val matrix: MatrixExpr
) : NamedMatrixExpr {
    override fun toString() = render()
}

data class MatrixVariableElement(
    val column: Int,
    val row: Int,
    val matrix: NamedMatrixVariable,
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
}

/**
 * Change the type of the underlying ScalarExpr
 */
data class RetypeScalar(
    val scalar: ScalarExpr,
    val type: AlgebraicType
) : ScalarExpr {
    init {
        track()
    }

    override fun toString() = render()
}

/**
 * Change the type of the underlying MatrixExpr
 */
data class RetypeMatrix(
    val matrix: MatrixExpr,
    val type: AlgebraicType
) : MatrixExpr {
    override fun toString() = render()
}

data class DataMatrix(
    val columns: Int,
    val rows: Int,
    val elements: List<ScalarExpr>
) : MatrixExpr {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toString() = render()
}

data class Tableau(
    val children: List<NamedAlgebraicExpr>
) : AlgebraicExpr {
    init {
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
    val left: NamedScalarVariable,
    val right: ScalarExpr
) : Expr {
    init {
        track()
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
}

data class MatrixAssign(
    val left: NamedMatrixVariable,
    val right: MatrixExpr
) : Expr {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
}

data class NamedScalarAssign(
    override val name: Id,
    val left: NamedScalarVariable,
    val right: ScalarExpr
) : NamedAlgebraicExpr {
    override fun toString() = render()
}

data class NamedMatrixAssign(
    override val name: Id,
    val left: NamedMatrixVariable,
    val right: MatrixExpr
) : NamedAlgebraicExpr {
    override fun toString() = render()
}

// Assign
fun assign(assignment: Pair<ScalarExpr, NamedScalarVariable>) = ScalarAssign(assignment.second, assignment.first)
fun assign(assignment: Pair<MatrixExpr, NamedMatrixVariable>) = MatrixAssign(assignment.second, assignment.first)

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
fun constant(value: Double): ScalarExpr = ConstantScalar(value)
fun constant(value: Int): ScalarExpr = constant(value.toDouble())
fun constant(value: String) = ValueExpr(value, StringKaneType.kaneType)
//inline fun <reified E:Any> constant(value : E) = ValueExpr(value, object : KaneType<E>(E::class.java) { })

// Tableau
fun tableauOf(vararg elements: NamedAlgebraicExpr): Tableau = Tableau(elements.toList())

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

// Differentiation
fun diff(expr: ScalarExpr, variable: ScalarExpr): ScalarExpr {
    if (variable is NamedScalar) return diff(expr, variable.scalar)
    val result: ScalarExpr = when (expr) {
        variable -> ConstantScalar(1.0)
        is NamedScalar -> {
            diff(expr.scalar, variable)
        }
        is MatrixVariableElement -> ConstantScalar(0.0)
        is ScalarVariableExpr -> ConstantScalar(0.0)
        is ConstantScalar -> ConstantScalar(0.0)
        is AlgebraicUnaryScalarScalar -> {
            val reduced = expr.op.reduceArithmetic(expr.value)
            if (reduced != null) diff(reduced, variable)
            else {
                val vd = diff(expr.value, variable)
                expr.op.differentiate(expr.value, vd, variable)
            }
        }
        is AlgebraicBinaryScalarScalarScalar -> {
            val p1d = diff(expr.left, variable)
            val p2d = diff(expr.right, variable)
            expr.op.differentiate(expr.left, p1d, expr.right, p2d, variable)
        }
        is AlgebraicUnaryScalarStatistic -> {
            diff(expr.eval(), variable)
        }
        is RetypeScalar -> diff(expr.scalar, variable)
        else ->
            error("${expr.javaClass}")
    }
    return result
}

fun differentiate(expr: AlgebraicExpr): AlgebraicExpr {
    fun ScalarExpr.tryD() = when {
        this is AlgebraicUnaryScalarScalar && op == d -> value
        else -> null
    }

    fun MatrixExpr.tryD() = when {
        this is AlgebraicUnaryMatrixMatrix && op == d -> value
        else -> null
    }

    fun ScalarExpr.diffOr(): ScalarExpr? {
        when (this) {
            is AlgebraicBinaryScalarScalarScalar -> {
                if (op != div) return null
                val dleft = left.tryD() ?: return null
                val dright = right.tryD() ?: return null
                return diff(dleft, dright)
            }
            else -> return null
        }
    }

    fun MatrixExpr.diffOr(): MatrixExpr? {
        if (this !is AlgebraicBinaryScalarMatrixMatrix) return null
        if (op != div) return null
        val dleft = left.tryD() ?: return null
        val dright = right.tryD() ?: return null

        return matrixOf(right.columns, right.rows) { (column, row) ->
            diff(dleft, dright[column, row])
        }
    }

    fun ScalarExpr.self() = differentiate(this)
    fun MatrixExpr.self() = differentiate(this)
    with(expr) {
        return when (this) {
            is NamedScalarAssign -> {
                val right = right.self()
                if (this.right !== right) copy(right = right)
                else expr
            }
            is NamedMatrixAssign -> {
                val right = right.self()
                if (this.right !== right) copy(right = right)
                else this
            }
            is NamedMatrix -> {
                val diff = matrix.diffOr()
                if (diff != null) copy(name = "${name}'", matrix = diff)
                else {
                    val matrix = matrix.self()
                    if (this.matrix !== matrix) copy(matrix = matrix)
                    else this
                }
            }
            is NamedScalar -> {
                val diff = scalar.diffOr()
                if (diff != null) copy(name = "${name}'", scalar = diff)
                else {
                    val scalar = scalar.self()
                    if (this.scalar !== scalar) copy(scalar = scalar)
                    else this
                }
            }
            is AlgebraicUnaryScalarScalar -> {
                val value = value.self()
                if (this.value !== value) copy(value = value)
                else this
            }
            is AlgebraicBinaryScalarScalarScalar -> {
                diffOr() ?: copy(left = left, right = right)
            }
            is AlgebraicBinaryMatrix -> {
                diffOr() ?: copy(left = left, right = right)
            }
            is AlgebraicBinaryScalarMatrixMatrix -> {
                diffOr() ?: run {
                    val left = left.self()
                    val right = right.self()
                    if (this.left !== left || this.right !== right) copy(left = left, right = right)
                    else this
                }
            }
            else ->
                error("${expr.javaClass}")
        }
    }
}

fun differentiate(expr: ScalarExpr): ScalarExpr = (differentiate(expr as AlgebraicExpr) as ScalarExpr).eval()
fun differentiate(expr: MatrixExpr): MatrixExpr = (differentiate(expr as AlgebraicExpr) as MatrixExpr).eval()

// Data matrix

fun MatrixExpr.toDataMatrix(): MatrixExpr = when (this) {
    is DataMatrix -> this
    is NamedMatrix -> copy(matrix = matrix.toDataMatrix())
    else -> DataMatrix(columns, rows, elements.toList())
}

fun MatrixExpr.toDataMatrix(columns: Int, rows: Int): MatrixExpr = when (this) {
    is DataMatrix -> this
    is NamedMatrix -> copy(matrix = matrix.toDataMatrix(columns, rows))
    else -> {
        val elements = coordinatesOf(columns to rows).map { coordinate ->
            getMatrixElement(coordinate.column, coordinate.row)
        }
        DataMatrix(columns, rows, elements)
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
    is AlgebraicBinaryMatrix -> op.meta
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
    return when (this) {
        is CoerceScalar -> value.self()
        is CoerceMatrix -> value.self()
        is SheetRangeExpr -> rangeRef.toString()
        is RetypeScalar -> scalar.render(entryPoint, outerType ?: this.type)
        is RetypeMatrix -> matrix.render(entryPoint, outerType ?: this.type)
        is Slot -> "\$$slot"
        is NamedScalarVariable -> Identifier.string(name)
        is NamedSheetRangeExpr ->
            if (entryPoint) "$name=${range.self()}"
            else Identifier.string(name)
        is ScalarVariable ->
            (outerType ?: kaneDouble).render(initial)
        is NamedScalarAssign -> "${left.self()} <- ${right.self()}"
        is NamedMatrixAssign -> "${left.self()} <- ${right.self()}"
        is AlgebraicUnaryMatrixMatrix -> when {
            op == exp -> {
                val rightSuper = tryConvertToSuperscript(value.self())
                if (rightSuper == null) "${op.meta.op}(${value.self()})"
                else "e$rightSuper"
            }
            op == d && value is NamedMatrixVariable -> "${op.meta.op}${value.self()}"
            op == negate &&
                    (value is NamedMatrixVariable ||
                            value is AlgebraicBinaryMatrix && value.op == times) -> "${op.meta.op}${value.self()}"
            else -> "${op.meta.op}(${value.self()})"
        }
        is AlgebraicBinaryScalarStatistic -> {
            when (op) {
                pow -> {
                    val rightSuper = tryConvertToSuperscript(right.self())
                    if (rightSuper == null) binary(POW, left, right)
                    else "${left.self()}$rightSuper"

                }
                else -> binary(op.meta, left, right)
            }
        }
        is AlgebraicBinaryMatrix -> {
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
        is AlgebraicBinaryMatrixScalarStatistic -> {
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
        is NamedScalar ->
            if (entryPoint) "$name=${scalar.self()}"
            else Identifier.string(name)
        is NamedMatrix -> {
            when {
                !entryPoint -> Identifier.string(name)
                matrix is DataMatrix && columns == 1 -> "$name=${matrix.self()}"
                matrix is DataMatrix && rows != 1 -> "\n$name\n------\n${matrix.self()}"
                else -> "$name=${matrix.self()}"
            }
        }
        is DiscreteUniformRandomVariable -> {
            val min = values.minByOrNull { it } ?: 0.0
            val max = values.maxByOrNull { it } ?: 0.0
            return "random($min to $max)"
        }
        is MatrixVariableElement -> "${matrix.name}[$column,$row]"
        is ConstantScalar -> (outerType ?: kaneDouble).render(value)
        is AlgebraicUnaryScalarStatistic -> "${op.meta.op}(${value.self()})"
        is AlgebraicUnaryScalarScalar -> when {
            op == exp -> {
                val rightSuper = tryConvertToSuperscript(value.self())
                if (rightSuper == null) "${op.meta.op}(${value.self()})"
                else "e$rightSuper"
            }
            op == d && value is NamedScalarVariable -> "${op.meta.op}${value.self()}"
            op == negate &&
                    (value is NamedScalarVariable ||
                            value is AlgebraicBinaryScalarScalarScalar && value.op == times) -> "${op.meta.op}${value.self()}"
            else -> "${op.meta.op}(${value.self()})"
        }
        is NamedMatrixVariable -> Identifier.string(name)
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
        else -> error("$javaClass")
    }
}
