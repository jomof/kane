@file:Suppress("UNCHECKED_CAST")

package com.github.jomof.kane

import com.github.jomof.kane.functions.*
import com.github.jomof.kane.sheet.CoerceScalar
import com.github.jomof.kane.types.AlgebraicType
import com.github.jomof.kane.types.DoubleAlgebraicType
import com.github.jomof.kane.types.KaneType
import com.github.jomof.kane.types.StringKaneType
import java.io.Closeable
import kotlin.reflect.KProperty

interface Expr
interface UntypedScalar : Expr
interface TypedExpr<E:Any> : Expr {
    val type : KaneType<E>
}
interface AlgebraicExpr : TypedExpr<Double> {
    override val type : AlgebraicType
}
interface ScalarExpr : AlgebraicExpr

interface ParentExpr<T:Any> : TypedExpr<T> {
    val children : Iterable<TypedExpr<T>>
}
interface MatrixExpr : AlgebraicExpr, ParentExpr<Double> {
    val columns : Int
    val rows : Int
    operator fun get(column : Int, row : Int) : ScalarExpr
    override val children : Iterable<ScalarExpr> get() = elements.asIterable()
}
interface VariableExpr : AlgebraicExpr
interface ScalarVariableExpr : ScalarExpr, VariableExpr
interface MatrixVariableExpr : MatrixExpr, VariableExpr
interface UnnamedExpr : Expr {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    fun toNamed(name: String): NamedExpr
}

interface NamedExpr : Expr {
    val name: String
    fun toUnnamed(): UnnamedExpr
}

interface UntypedUnnamedExpr : UntypedScalar, UnnamedExpr
interface NamedAlgebraicExpr : NamedExpr, AlgebraicExpr
interface NamedScalarExpr: NamedAlgebraicExpr, ScalarExpr
interface NamedMatrixExpr: NamedAlgebraicExpr, MatrixExpr
data class UnaryOp(val op : String = "") {
    operator fun getValue(nothing: Nothing?, property: KProperty<*>) =
        if (op.isBlank()) copy(op = property.name.toLowerCase())
        else this
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

fun ScalarExpr.toNamed(name: String) = NamedScalar(name, this)
operator fun ScalarExpr.getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
fun MatrixExpr.toNamed(name: String) = NamedMatrix(name, this)
operator fun MatrixExpr.getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)

data class ConstantScalar(
    val value: Double,
    override val type: AlgebraicType
) : UnnamedExpr, ScalarExpr {
    init {
        track()
    }

    override fun toNamed(name: String) = NamedScalar(name, this)
    override fun toString() = render()
}

data class ValueExpr<E : Any>(
    val value: E,
    override val type: KaneType<E>
) : UnnamedExpr, TypedExpr<E> {
    init {
        assert(value !is Expr) {
            "${value.javaClass}"
        }
        track()
    }

    override fun toString() = type.render(value)
    override fun toNamed(name: String) = NamedValueExpr(name, value, type)
}

data class NamedValueExpr<E : Any>(
    override val name: String,
    val value : E,
    override val type : KaneType<E>
) : NamedExpr, TypedExpr<E> {
    init {
        assert(value !is Expr)
        track()
    }

    override fun toUnnamed() = ValueExpr(value, type)
    override fun toString() = type.render(value)
}

data class ScalarVariable(
    val initial: Double,
    override val type: AlgebraicType
) : UnnamedExpr, AlgebraicExpr {
    init {
        track()
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toNamed(name: String) = NamedScalarVariable(name, initial, type)
    override fun toString() = render()
}

data class NamedScalarVariable(
    override val name: String,
    val initial: Double,
    override val type: AlgebraicType
) : ScalarVariableExpr, NamedScalarExpr {
    init {
        track()
    }

    override fun toUnnamed() = ScalarVariable(initial, type)
    override fun toString() = render()
}

data class MatrixVariable(
    val columns: Int,
    val rows: Int,
    override val type: AlgebraicType,
    val initial: List<Double>
) : UnnamedExpr, AlgebraicExpr {
    init {
        track()
        assert(rows * columns == initial.size) {
            "expected ${rows * columns} elements"
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toNamed(name: String) = NamedMatrixVariable(name, columns, type, initial)
    override fun toString() = "matrixVariable(${columns}x$rows)"
}
data class NamedMatrixVariable(
    override val name: String,
    override val columns : Int,
    override val type : AlgebraicType,
    val initial : List<Double>) : MatrixVariableExpr, NamedMatrixExpr {
    init { track() }
    override val rows : Int = initial.size / columns
    private fun coordinateToIndex(column: Int, row: Int) = row * columns + column
    override fun get(column: Int, row: Int) = run {
        assert(column >= 0)
        assert(column < columns) { "$column greater than columns $columns of matrix $name" }
        assert(row >= 0)
        assert(row < rows) { "$row greater than columns $rows of matrix $name" }
        MatrixVariableElement(column, row, this, initial[coordinateToIndex(column, row)])
    }
    fun get(coordinate : Coordinate) = get(coordinate.column, coordinate.row)
    val elements get() = coordinates.map { get(it) }
    override fun toUnnamed() = MatrixVariable(columns, rows, type, initial)
    override fun toString() = render()
}
data class NamedScalar(
    override val name : String,
    val scalar : ScalarExpr
) : NamedScalarExpr, ParentExpr<Double> {
    init {
        track()
    }

    override val type get() = scalar.type
    override val children get() = listOf(scalar)
    override fun toUnnamed() = when (scalar) {
        is NamedExpr -> scalar.toUnnamed()
        is UnnamedExpr -> scalar
        else -> error("${scalar.javaClass}")
    }

    override fun toString() = render()
}
data class NamedMatrix(
    override val name : String,
    val matrix : MatrixExpr) : NamedMatrixExpr {
    init {
        track()
    }

    override val columns get() = matrix.columns
    override val rows get() = matrix.rows
    override val type get() = matrix.type
    override fun get(column: Int, row: Int) = matrix[column, row]
    override fun toUnnamed() = when (matrix) {
        is NamedExpr -> matrix.toUnnamed()
        is UnnamedExpr -> matrix
        else -> error("${matrix.javaClass}")
    }

    override fun toString() = render()
}
data class MatrixVariableElement(
    val column : Int,
    val row : Int,
    val matrix : NamedMatrixVariable,
    val initial : Double) : ScalarExpr {
    init { track() }
    override val type get() = matrix.type
    override fun toString() = render()
    override fun hashCode() = ((matrix.name.hashCode() * 3) + column) * 5 + row
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is AlgebraicExpr) return false
        if (other.type != type) return false
        if (other !is MatrixVariableElement) return false
        if (matrix.name != other.matrix.name) return false
        if (column != other.column) return false
        if (row != other.row) return false
        return true
    }
}

data class DataMatrix(
    override val columns: Int,
    override val rows: Int,
    val elements: List<ScalarExpr>
) : UnnamedExpr, MatrixExpr {
    init {
        track()
        assert(columns * rows == elements.size) {
            "found ${elements.size} elements but needed ${rows * columns} for $columns by $rows matrix"
        }
        assert(columns > 0)
        assert(rows > 0)
    }

    override val type: AlgebraicType get() = elements[0].type
    private fun coordinateToIndex(column: Int, row: Int) = row * columns + column
    override fun get(column: Int, row: Int) = run {
        assert(row >= 0)
        assert(row < rows) {
            "row $row was not less that $rows"
        }
        assert(column >= 0)
        assert(column < columns)
        elements[coordinateToIndex(column, row)]
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toNamed(name: String) = NamedMatrix(name, this)
    override fun toString() = render()
}

data class Tableau(
    override val children: List<NamedAlgebraicExpr>,
    override val type : AlgebraicType
) : AlgebraicExpr, ParentExpr<Double> {
    init { track() }
    override fun toString() = render()
}

data class Slot(
    val slot : Int,
    val replaces : ScalarExpr
) : ScalarExpr {
    init { track() }
    override val type get() = replaces.type
    override fun toString() = render()
}

data class ScalarAssign(
    val left: NamedScalarVariable,
    val right: ScalarExpr
) : UnnamedExpr {
    init {
        track()
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toNamed(name: String) = NamedScalarAssign(name, left, right)
}

data class MatrixAssign(
    val left: NamedMatrixVariable,
    val right: MatrixExpr
) : UnnamedExpr {
    init {
        track()
        fun lm() = "matrix '${left.name}'"
        fun rm() = if (right is NamedExpr) "matrix '${right.name}'" else "right matrix"
        assert(left.rows == right.rows) {
            "${lm()} columns ${left.rows} did not equal ${rm()} columns ${right.rows}"
        }
        assert(left.columns == right.columns) {
            "${lm()} columns ${left.columns} did not equal ${rm()} columns ${right.columns}"
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    override fun toNamed(name: String) = NamedMatrixAssign(name, left, right)
}

data class NamedScalarAssign(
    override val name: String,
    val left : NamedScalarVariable,
    val right : ScalarExpr
) : NamedAlgebraicExpr, ParentExpr<Double> {
    override val type get() = left.type

    init {
        track()
    }

    override fun toUnnamed() = ScalarAssign(left, right)
    override fun toString() = render()
    override val children = listOf(right)
}

data class NamedMatrixAssign(
    override val name: String,
    val left : NamedMatrixVariable,
    val right : MatrixExpr
) : NamedAlgebraicExpr, ParentExpr<Double> {
    override val type get() = left.type
    init {
        track()
        fun lm() = "matrix '${left.name}'"
        fun rm() = if (right is NamedExpr) "matrix '${right.name}'" else "right matrix"
        assert(left.rows == right.rows) {
            "${lm()} columns ${left.rows} did not equal ${rm()} columns ${right.rows}"
        }
        assert(left.columns == right.columns) {
            "${lm()} columns ${left.columns} did not equal ${rm()} columns ${right.columns}"
        }
    }

    override fun toUnnamed() = MatrixAssign(left, right)
    override fun toString() = render()
    override val children = listOf(right)
}

data class NamedUntypedScalar(
    override val name: String,
    val expr: UntypedUnnamedExpr
) : NamedExpr, UntypedScalar {
    override fun toUnnamed() = expr
}

data class ScalarListExpr(
    val values: List<ScalarExpr>,
    override val type: AlgebraicType
) : ScalarExpr {
    init {
        values.forEach {
            assert(it !is ScalarListExpr) {
                "list of list?"
            }
        }
    }
}

// Assign
fun assign(assignment: Pair<ScalarExpr, NamedScalarVariable>) = ScalarAssign(assignment.second, assignment.first)
fun assign(assignment: Pair<MatrixExpr, NamedMatrixVariable>) = MatrixAssign(assignment.second, assignment.first)

// Variables
fun matrixVariable(columns: Int, rows: Int) = matrixVariable(DoubleAlgebraicType.kaneType, columns, rows)
fun matrixVariable(type: AlgebraicType, columns: Int, rows: Int) =
    MatrixVariable(columns, rows, type, (0 until rows * columns).map { 0.0 })

fun matrixVariable(columns: Int, rows: Int, vararg elements: Double) =
    MatrixVariable(columns, rows, DoubleAlgebraicType.kaneType, elements.toList().map { it })
fun matrixVariable(columns : Int, rows : Int, init : (Coordinate) -> Double) =
    MatrixVariable(columns, rows, DoubleAlgebraicType.kaneType, coordinatesOf(columns, rows).toList().map { init(it) })
fun matrixVariable(columns : Int, rows : Int, type : AlgebraicType, init : (Coordinate) -> Double) =
    MatrixVariable(columns, rows, type, coordinatesOf(columns, rows).toList().map { type.coerceFrom(init(it)) })
fun variable(initial : Double = 0.0, type : AlgebraicType = DoubleAlgebraicType.kaneType) = ScalarVariable(initial, type)
// Constant
fun constant(value : Double, type : AlgebraicType) : ScalarExpr = ConstantScalar(value, type)
fun constant(value : Double) : ScalarExpr = ConstantScalar(value, DoubleAlgebraicType.kaneType)
fun constant(value : Int) : ScalarExpr = constant(value.toDouble())
fun constant(value : String) = ValueExpr(value, StringKaneType.kaneType)
//inline fun <reified E:Any> constant(value : E) = ValueExpr(value, object : KaneType<E>(E::class.java) { })

// Tableau
fun tableauOf(type : AlgebraicType, vararg elements : NamedAlgebraicExpr) : Tableau = Tableau(elements.toList(), type)
// Misc
fun repeated(columns : Int, rows : Int, type : AlgebraicType, value : Double) : MatrixExpr =
    repeated(columns, rows, ConstantScalar(value, type))

fun repeated(columns: Int, rows: Int, value: ScalarExpr): MatrixExpr =
    DataMatrix(columns, rows, (0 until columns * rows).map { value })

fun Expr.tryFindConstant(): Double? = when (this) {
    is ConstantScalar -> value
    is NamedScalar -> scalar.tryFindConstant()
    is CoerceScalar ->
        if (value is ScalarExpr && type.java == value.type.java) value.tryFindConstant()
        else null
    is ValueExpr<*> ->
        if (value is Number) value.toDouble() else null
    else -> null
}

// Coordinates
inline operator fun <reified M:MatrixExpr> M.get(coord : Coordinate) = get(coord.column, coord.row)
val MatrixExpr.coordinates get() = coordinatesOf(columns, rows)
inline val <reified M:MatrixExpr> M.elements get() = coordinates.map { get(it) }

// Differentiation
fun diff(expr : ScalarExpr, variable : ScalarExpr) : ScalarExpr {
    if (variable is NamedScalar) return diff(expr, variable.scalar)
    val result: ScalarExpr = when (expr) {
        variable -> ConstantScalar(1.0, expr.type)
        is NamedScalar -> diff(expr.scalar, variable)
        is AlgebraicUnaryMatrixScalar -> diff(expr.reduceArithmetic(), variable)
        is MatrixVariableElement -> ConstantScalar(0.0, expr.type)
        is ScalarVariableExpr -> ConstantScalar(0.0, expr.type)
        is ConstantScalar -> ConstantScalar(0.0, expr.type)
        is AlgebraicUnaryScalar -> {
            val reduced = expr.op.reduceArithmetic(expr.value)
            if (reduced != null) diff(reduced, variable)
            else {
                val vd = diff(expr.value, variable)
                expr.op.differentiate(expr.value, vd, variable)
            }
        }
        is AlgebraicBinaryScalar -> {
            val p1d = diff(expr.left, variable)
            val p2d = diff(expr.right, variable)
            expr.op.differentiate(expr.left, p1d, expr.right, p2d, variable)
        }
        else ->
            error("${expr.javaClass}")
    }
    return result
}

fun differentiate(expr : AlgebraicExpr) : AlgebraicExpr {
    fun ScalarExpr.tryD() = when {
        this is AlgebraicUnaryScalar && op == d -> value
        else -> null
    }
    fun MatrixExpr.tryD() = when {
        this is AlgebraicUnaryMatrix && op == d -> value
        else -> null
    }
    fun ScalarExpr.diffOr() : ScalarExpr? {
        when(this) {
            is AlgebraicBinaryScalar -> {
                if (op != divide) return null
                val dleft = left.tryD() ?: return null
                val dright = right.tryD() ?: return null
                return diff(dleft, dright)
            }
            else -> return null
        }
    }

    fun MatrixExpr.diffOr(): MatrixExpr? {
        if (this !is AlgebraicBinaryScalarMatrix) return null
        if (op != divide) return null
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
            is AlgebraicUnaryScalar -> {
                val value = value.self()
                if (this.value !== value) copy(value = value)
                else this
            }
            is AlgebraicBinaryScalar -> {
                diffOr() ?: copy(left = left, right = right)
            }
            is AlgebraicBinaryScalarMatrix -> {
                diffOr() ?: run {
                    val left = left.self()
                    val right = right.self()
                    if (this.left !== left || this.right !== right) copy(left = left, right = right)
                    else this
                }
            }
            else -> error("${expr.javaClass}")
        }
    }
}

fun differentiate(expr : ScalarExpr) : ScalarExpr = (differentiate(expr as AlgebraicExpr) as ScalarExpr).reduceArithmetic()
fun differentiate(expr : MatrixExpr) : MatrixExpr = (differentiate(expr as AlgebraicExpr) as MatrixExpr).reduceArithmetic()

// Data matrix

fun MatrixExpr.toDataMatrix() : MatrixExpr = when(this) {
    is DataMatrix -> this
    is NamedMatrix -> copy(matrix = matrix.toDataMatrix())
    else -> DataMatrix(columns, rows, elements.toList())
}
fun MatrixExpr.map(action:(ScalarExpr)->ScalarExpr) = DataMatrix(columns, rows, elements.map(action).toList())

// Allocation tracking
private var trackingEnabled = false
private var trackingCount = 0
fun errorIfTrackingEnabled() {
    if (trackingEnabled) error("tracking is enabled")
}
private val traceMap = mutableMapOf<List<StackTraceElement>, Int>()
private var offender : List<StackTraceElement> = listOf()
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
            for(dropText in listOf(".mapChildren(", ".dispatchExpr(", ".unsafeReplace(", "\$unsafeReplace\$", "\$wrap\$1", ".invoke(")) {
                if (result.size < 4) break
                result = result.filter { !it.toString().contains(dropText) }
            }
            if (result.size == 2) {
                var check = no1Line.drop(1)
                for(dropText in listOf(".mapChildren(", ".dispatchExpr(", ".unsafeReplace(", "\$unsafeReplace\$", "\$wrap\$1", ".invoke(")) {
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
        if (traceMap[offender]?:0 < count) {
            offender = stack
        }
    }
}
internal fun track() {
    if (trackingEnabled)  trackImpl()
}

private val recordedExprs = mutableSetOf<Expr>()
fun getRecordedExprs() : Set<Expr> = recordedExprs

private fun recordImpl(expr : Expr) {
    if (recordedExprs.contains(expr)) return
    recordedExprs += expr
    when(expr) {
        is ParentExpr<*> -> expr.children.forEach { recordImpl(it) }
        else -> { }
    }
}

private fun <T:Expr> T.record() : T {
    if (trackingEnabled)  recordImpl(this)
    return this
}

private class TrackingScope : Closeable {
    val prior = trackingEnabled
    init { trackingEnabled = true }
    override fun close() {
        trackingEnabled = prior
    }
}

fun trackExprs() : Closeable = TrackingScope()

// Substitute variable initial values
fun AlgebraicExpr.substituteInitial() : AlgebraicExpr {
    val result = when (this) {
        is ConstantScalar -> this
        is NamedScalar -> copy(scalar = scalar.substituteInitial())
        is NamedMatrix -> copy(matrix = matrix.substituteInitial())
        is AlgebraicUnaryScalar -> copy(value = value.substituteInitial())
        is AlgebraicUnaryMatrix -> copy(value = value.substituteInitial())
        is AlgebraicUnaryMatrixScalar -> copy(value = value.substituteInitial())
        is AlgebraicBinaryScalar -> copyReduce(left = left.substituteInitial(), right = right.substituteInitial())
        is AlgebraicBinaryScalarMatrix -> copy(left = left.substituteInitial(), right = right.substituteInitial())
        is AlgebraicBinaryMatrixScalar -> copy(left = left.substituteInitial(), right = right.substituteInitial())
        is AlgebraicBinaryMatrix -> copy(left = left.substituteInitial(), right = right.substituteInitial())
        is AlgebraicDeferredDataMatrix -> data.substituteInitial()
        is NamedScalarVariable -> ConstantScalar(initial, type)
        is MatrixVariableElement -> ConstantScalar(initial, type)
        is DataMatrix -> map { it.substituteInitial() }
        is NamedMatrixVariable -> toDataMatrix().substituteInitial()
        else -> error("$javaClass")
    }
    return result
}

fun ScalarExpr.substituteInitial() = (this as AlgebraicExpr).substituteInitial() as ScalarExpr
fun MatrixExpr.substituteInitial() = (this as AlgebraicExpr).substituteInitial() as MatrixExpr

// Reduce arithmetic
fun AlgebraicExpr.memoizeAndReduceArithmetic(
    entryPoint : Boolean = true,
    memo : MutableMap<TypedExpr<*>,TypedExpr<*>> = mutableMapOf(),
    selfMemo : MutableMap<TypedExpr<*>,TypedExpr<*>> = mutableMapOf()) : AlgebraicExpr {
    if (this is ConstantScalar) return this
    if (memo.contains(this)) {
        val result = memo.getValue(this)
        return result as AlgebraicExpr
    }
    val result : AlgebraicExpr =
        when (this) {
            is NamedMatrixVariable -> this
            is ConstantScalar -> this
            is AlgebraicBinaryMatrix -> {
                val dm = toDataMatrix()
                val dmself = dm.memoizeAndReduceArithmetic(false, memo, selfMemo) as MatrixExpr
                if (dm != dmself) dmself
                else this
            }
            is DataMatrix -> map { it.memoizeAndReduceArithmetic(false, memo, selfMemo) as ScalarExpr }
            is MatrixVariableElement -> copy(
                matrix = matrix.memoizeAndReduceArithmetic(
                    false,
                    memo,
                    selfMemo
                ) as NamedMatrixVariable
            )
            is AlgebraicUnaryMatrix -> copy(
                value = value.memoizeAndReduceArithmetic(
                    false,
                    memo,
                    selfMemo
                ) as MatrixExpr
            )
            is NamedMatrix -> copy(matrix = matrix.memoizeAndReduceArithmetic(false, memo, selfMemo) as MatrixExpr)
            is NamedScalar ->
                if (entryPoint) copy(scalar = scalar.memoizeAndReduceArithmetic(false, memo, selfMemo) as ScalarExpr)
                else scalar.memoizeAndReduceArithmetic(false, memo, selfMemo) as ScalarExpr
            is NamedScalarVariable -> this
            is AlgebraicBinaryScalarMatrix -> copy(
                left = left.memoizeAndReduceArithmetic(
                    false,
                    memo,
                    selfMemo
                ) as ScalarExpr, right = right.memoizeAndReduceArithmetic(false, memo, selfMemo) as MatrixExpr
            )
            is AlgebraicBinaryMatrixScalar -> copy(
                left = left.memoizeAndReduceArithmetic(false, memo, selfMemo) as MatrixExpr,
                right = right.memoizeAndReduceArithmetic(false, memo, selfMemo) as ScalarExpr
            )
            is NamedScalarAssign -> copy(right = right.memoizeAndReduceArithmetic(false, memo, selfMemo) as ScalarExpr)
            is NamedMatrixAssign -> copy(right = right.memoizeAndReduceArithmetic(false, memo, selfMemo) as MatrixExpr)
            is Tableau -> copy(children = children.map {
                it.memoizeAndReduceArithmetic(
                    true,
                    memo,
                    selfMemo
                ) as NamedAlgebraicExpr
            })
            is CoerceScalar -> {
                val value = when (value) {
                    is AlgebraicExpr -> value.memoizeAndReduceArithmetic(false, memo, selfMemo)
                    else -> value
                }
                when {
                    value is AlgebraicExpr && value.type == type -> value
                    value is ConstantScalar -> {
                        assert(type != value.type)
                        constant(type.coerceFrom(value.value), type)
                    }
                    else ->
                        if (this.value !== value)
                            copy(value = value)
                        else
                            this
                }
            }
            is ScalarVariable -> this
            is AlgebraicBinaryScalar -> {
                val result = run {
                    val leftSelf = left.memoizeAndReduceArithmetic(false, memo, selfMemo) as ScalarExpr
                    val rightSelf = right.memoizeAndReduceArithmetic(false, memo, selfMemo) as ScalarExpr
                    when {
                        leftSelf is ScalarListExpr && rightSelf !is ScalarListExpr -> {
                            ScalarListExpr(leftSelf.values.map {
                                copy(left = it).memoizeAndReduceArithmetic(false, memo, selfMemo) as ScalarExpr
                            }, type)
                        }
                        leftSelf !is ScalarListExpr && rightSelf is ScalarListExpr -> {
                            val reduced = rightSelf.values.map {
                                copy(right = it).memoizeAndReduceArithmetic(false, memo, selfMemo) as ScalarExpr
                            }
                            ScalarListExpr(reduced, type)
                        }
                        leftSelf is ScalarListExpr && rightSelf is ScalarListExpr -> {
                            val zip = (leftSelf.values zip rightSelf.values).map { (l, r) ->
                                copy(left = l, right = r).memoizeAndReduceArithmetic(
                                    false,
                                    memo,
                                    selfMemo
                                ) as ScalarExpr
                            }
                            ScalarListExpr(zip, type)
                        }
                        else -> {

                            val leftAffine = left.affineName()
                            val rightAffine = right.affineName()
                            if (left != leftSelf || right != right.memoizeAndReduceArithmetic(
                                    false,
                                    memo,
                                    selfMemo
                                ) as ScalarExpr
                            ) binaryScalar(op, leftSelf, rightSelf).memoizeAndReduceArithmetic(
                                false,
                                memo,
                                selfMemo
                            ) as ScalarExpr
                            else if (leftAffine != null && rightAffine != null && leftAffine.compareTo(rightAffine) == 1 && op.meta.associative) {
                                val result: ScalarExpr =
                                    op(right, left).memoizeAndReduceArithmetic(false, memo, selfMemo) as ScalarExpr
                                result
                            } else {
                                val leftConst = left.tryFindConstant()
                                val rightConst = right.tryFindConstant()
                                when {
                                    leftConst != null && rightConst != null -> ConstantScalar(
                                        op(leftConst, rightConst),
                                        type
                                    )
                                    else -> op.reduceArithmetic(left, right)
                                        ?.let { it.memoizeAndReduceArithmetic(false, memo, selfMemo) as ScalarExpr }
                                        ?: this
                                }
                            }
                        }
                    }
                }
                result.withType(type)
            }
            is AlgebraicUnaryScalar -> {
                when (value) {
                    is ScalarListExpr -> ScalarListExpr(value.values.map {
                        it.memoizeAndReduceArithmetic(false, memo, selfMemo) as ScalarExpr
                    }, type)
                    else -> {

                        val valueSelf = value.memoizeAndReduceArithmetic(false, memo, selfMemo) as ScalarExpr
                        if (value != valueSelf) copy(value = valueSelf).memoizeAndReduceArithmetic(
                            false,
                            memo,
                            selfMemo
                        ) as ScalarExpr
                        else {
                            val valueConst = value.tryFindConstant()
                            when {
                                valueConst != null -> ConstantScalar(op(valueConst), type)
                                else -> op.reduceArithmetic(value)
                                    ?.let { it.memoizeAndReduceArithmetic(false, memo, selfMemo) as ScalarExpr } ?: this
                            }
                        }
                    }
                }
            }
            is AlgebraicBinaryScalarStatistic -> copy(
                left = left.memoizeAndReduceArithmetic(
                    false,
                    memo,
                    selfMemo
                ) as ScalarExpr, right = right.memoizeAndReduceArithmetic(false, memo, selfMemo) as ScalarExpr
            )
            is AlgebraicUnaryScalarStatistic -> copy(
                value = value.memoizeAndReduceArithmetic(
                    false,
                    memo,
                    selfMemo
                ) as ScalarExpr
            )
            is AlgebraicUnaryMatrixScalar -> op.reduceArithmetic(value)
                ?.let { it.memoizeAndReduceArithmetic(false, memo, selfMemo) as ScalarExpr } ?: value
            is AlgebraicDeferredDataMatrix -> data.memoizeAndReduceArithmetic(false, memo, selfMemo) as MatrixExpr
            is DiscreteUniformRandomVariable -> this
            is ScalarListExpr -> copy(values = values.map { it })
            else ->
                error("$javaClass")
        }

    memo[this] = result
    return result
}

fun NamedScalar.reduceArithmetic() = (this as AlgebraicExpr).memoizeAndReduceArithmetic() as NamedScalar
fun NamedAlgebraicExpr.reduceArithmetic() = (this as AlgebraicExpr).memoizeAndReduceArithmetic() as NamedAlgebraicExpr
fun NamedScalarExpr.reduceArithmetic() = (this as AlgebraicExpr).memoizeAndReduceArithmetic() as NamedScalarExpr
fun ScalarExpr.reduceArithmetic() = (this as AlgebraicExpr).memoizeAndReduceArithmetic() as ScalarExpr
fun MatrixExpr.reduceArithmetic() = (this as AlgebraicExpr).memoizeAndReduceArithmetic() as MatrixExpr
fun Tableau.reduceArithmetic() = (this as AlgebraicExpr).memoizeAndReduceArithmetic() as Tableau

private fun AlgebraicExpr.affineName(depth : Int = 2) : String? {
    if (depth == 0) return null
    return when(this) {
        is AlgebraicBinaryScalar -> left.affineName(depth - 1) ?: right.affineName(depth - 1) ?: op.meta.op
        is NamedScalar -> name
        is MatrixVariableElement -> matrix.name
        is AlgebraicUnaryScalar -> value.affineName(depth - 1) ?: op.meta.op
        is NamedScalarVariable -> name
        is NamedScalarExpr -> name
        else -> null
    }
}

// Render
fun binaryRequiresParents(parent : BinaryOp, child : BinaryOp, childIsRight: Boolean) : Boolean {
    fun bit(check : Boolean, shift : Int) : Int = if (check) (0x1 shl shift) else 0
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
    return when(sig) {
        12,268,20,276,354,108,224,480,192,448,8,196,452,76,332,388,132 -> false
        else -> true
    }
}

fun Expr.tryGetBinaryOp() : BinaryOp? = when(this) {
    is AlgebraicBinaryScalar -> op.meta
    is AlgebraicBinaryMatrix -> op.meta
    is AlgebraicBinaryMatrixScalar -> op.meta
    is AlgebraicBinaryScalarMatrix -> op.meta
    is AlgebraicDeferredDataMatrix -> op
    else -> null
}

fun requiresParens(parentBinOp : BinaryOp?, childBinOp : BinaryOp?, childIsRight : Boolean) : Boolean {
    if (parentBinOp == null || childBinOp == null) return false
    return binaryRequiresParents(parentBinOp, childBinOp, childIsRight)
}

fun requiresParens(parent : Expr, child : Expr, childIsRight : Boolean) : Boolean {
    return requiresParens(parent.tryGetBinaryOp(), child.tryGetBinaryOp(), childIsRight)
}

fun tryConvertToSuperscript(value : String) : String? {
    val sb = StringBuilder()
    for(c in value) {
        sb.append(when(c) {
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
        })
    }
    return sb.toString()
}

fun Expr.render(entryPoint : Boolean = true) : String {
    val parent = this
    fun Expr.self(childIsRight : Boolean = false) =
        when {
            requiresParens(parent, this, childIsRight) -> "(${render(false)})"
            else -> render(false)
        }
    fun binary(op : BinaryOp, left : Expr, right : Expr) : String =
        when  {
            op.infix -> "${left.self()}${op.op}${right.self(true)}"
            else -> "${op.op}(${left.self()},${right.self()})"
        }
    return when(this) {
        is Slot -> "\$$slot"
        is NamedScalarVariable-> name
        is ScalarVariable ->
            type.render(initial)
        is NamedScalarAssign -> "${left.self()} <- ${right.self()}"
        is NamedMatrixAssign -> "${left.self()} <- ${right.self()}"
        is AlgebraicUnaryMatrix -> when {
            op == exp -> {
                val rightSuper = tryConvertToSuperscript(value.self())
                if (rightSuper == null) "${op.meta.op}(${value.self()})"
                else "e$rightSuper"
            }
            op == d && value is NamedMatrixVariable -> "${op.meta.op}${value.self()}"
            op == negate &&
                    (value is NamedMatrixVariable ||
                    value is AlgebraicBinaryMatrix && value.op == multiply) -> "${op.meta.op}${value.self()}"
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
        is AlgebraicBinaryScalar -> {
            when (op) {
                pow -> {
                    val rightSuper = tryConvertToSuperscript(right.self())
                    if (rightSuper == null) binary(POW, left, right)
                    else "${left.self()}$rightSuper"

                }
                else -> binary(op.meta, left, right)
            }
        }
        is AlgebraicBinaryRangeStatistic -> {
            when (op) {
                pow -> {
                    val rightSuper = tryConvertToSuperscript(right.self())
                    if (rightSuper == null) binary(POW, left, right)
                    else "${left}$rightSuper"

                }
                else -> binary(op.meta, left, right)
            }
        }
        is AlgebraicBinaryMatrixScalar -> binary(op.meta, left, right)
        is AlgebraicBinaryScalarMatrix -> binary(op.meta, left, right)
        is NamedScalar ->
            if (entryPoint) "$name=${scalar.self()}"
            else name
        is NamedMatrix -> {
            when {
                !entryPoint -> name
                matrix is DataMatrix && rows != 1 -> "\n$name\n------\n${matrix.self()}"
                else -> "$name=${matrix.self()}"
            }
        }
        is MatrixVariableElement -> "${matrix.name}[$column,$row]"
        is ConstantScalar -> type.render(value)
        is AlgebraicUnaryMatrixScalar -> "${op.meta.op}(${value.self()})"
        is AlgebraicUnaryScalarStatistic -> "${op.meta.op}(${value.self()})"
        is AlgebraicUnaryScalar -> when {
            op == exp -> {
                val rightSuper = tryConvertToSuperscript(value.self())
                if (rightSuper == null) "${op.meta.op}(${value.self()})"
                else "e$rightSuper"
            }
            op == d && value is NamedScalarVariable -> "${op.meta.op}${value.self()}"
            op == negate &&
                    (value is NamedScalarVariable ||
                            value is AlgebraicBinaryScalar && value.op == multiply) -> "${op.meta.op}${value.self()}"
            else -> "${op.meta.op}(${value.self()})"
        }
        is NamedMatrixVariable -> name
        is Tableau -> {
            val sb = StringBuilder()
            val sorted = children.sortedBy {
                when(it) {
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
                "[${elements[0]}]"
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
        else -> "$this"
    }
}

fun ScalarExpr.withType(target: AlgebraicType): ScalarExpr {
    return when {
        type == target -> this
        this is AlgebraicBinaryScalar -> copy(type = target)
        this is ConstantScalar -> copy(type = target)
        else ->
            error("$javaClass")
    }
}

fun Expr.toNamed(name: String): Expr {
    return when (this) {
        is UnnamedExpr -> toNamed(name)
        is NamedExpr -> toUnnamed().toNamed(name)
        else -> error("$javaClass")
    }
}

fun Expr.toUnnamed(): Expr {
    return when (this) {
        is UnnamedExpr -> this
        is NamedExpr -> toUnnamed()
        else -> error("$javaClass")
    }
}
