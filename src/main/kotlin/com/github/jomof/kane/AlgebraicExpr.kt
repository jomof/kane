@file:Suppress("UNCHECKED_CAST")

package com.github.jomof.kane

import com.github.jomof.kane.Coordinate
import com.github.jomof.kane.coordinatesOf
import com.github.jomof.kane.functions.*
import com.github.jomof.kane.types.*
import java.io.Closeable
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

interface Expr
interface UntypedScalar : Expr
interface TypedExpr<E:Any> : Expr {
    val type : KaneType<E>
}
interface AlgebraicExpr<E:Number> : TypedExpr<E> {
    override val type : AlgebraicType<E>
}
interface ScalarExpr<T:Number> : AlgebraicExpr<T>

interface ParentExpr<T:Any> : TypedExpr<T> {
    val children : Iterable<TypedExpr<T>>
}
interface MatrixExpr<T:Number> : AlgebraicExpr<T>, ParentExpr<T> {
    val columns : Int
    val rows : Int
    operator fun get(column : Int, row : Int) : ScalarExpr<T>
    override val children : Iterable<ScalarExpr<T>> get() = elements.asIterable()
}
interface VariableExpr<T:Number> : AlgebraicExpr<T>
interface ScalarVariableExpr<T:Number> : ScalarExpr<T>, VariableExpr<T>
interface MatrixVariableExpr<T:Number> : MatrixExpr<T>, VariableExpr<T>
interface NamedExpr : Expr {
    val name : String
}
interface NamedAlgebraicExpr<T:Number> : NamedExpr, AlgebraicExpr<T>
interface NamedScalarExpr<T:Number> : NamedAlgebraicExpr<T>, ScalarExpr<T>
interface NamedMatrixExpr<T:Number> : NamedAlgebraicExpr<T>, MatrixExpr<T>
data class UnaryOp(val op : String = "") {
    operator fun getValue(nothing: Nothing?, property: KProperty<*>) =
        if (op.isBlank()) copy(op = property.name.toLowerCase())
        else this
}

data class BinaryOp(
    val op : String = "",
    val precedence : Int,
    val associative : Boolean = false,
    val infix : Boolean = false
) {
    operator fun getValue(nothing: Nothing?, property: KProperty<*>) =
        if (op.isBlank()) copy(op = property.name.toLowerCase())
        else this
}

operator fun <T:Number> ScalarExpr<T>.getValue(thisRef: Any?, property: KProperty<*>) = NamedScalar(property.name, this)
operator fun <T:Number> MatrixExpr<T>.getValue(thisRef: Any?, property: KProperty<*>) = NamedMatrix(property.name, this)

data class ConstantScalar<E: Number>(
    val value : E,
    override val type : AlgebraicType<E>
) : ScalarExpr<E>{
    init {
        assert(value !is AlgebraicExpr<*>)
        track()
    }
    override fun toString() = render()
}
data class ValueExpr<E: Any>(
    val value : E,
    override val type : KaneType<E>
) : TypedExpr<E> {
    init {
        assert(value !is Expr) {
            "${value.javaClass}"
        }
        track()
    }
    override fun toString() = value.toString()
    operator fun getValue(e: E?, property: KProperty<*>) = NamedValueExpr(property.name, value, type)
}
data class NamedValueExpr<E: Any>(
    override val name: String,
    val value : E,
    override val type : KaneType<E>
) : NamedExpr, TypedExpr<E> {
    init {
        assert(value !is Expr)
        track()
    }
    fun toValueExpr() = ValueExpr(value, type)
    override fun toString() = value.toString()
}

data class ScalarVariable<E:Number>(
    val initial : E,
    override val type: AlgebraicType<E>) : AlgebraicExpr<E> {
    init { track() }
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = NamedScalarVariable(property.name, initial, type)
    override fun toString() = render()
}
data class NamedScalarVariable<E:Number>(
    override val name : String,
    val initial : E,
    override val type : AlgebraicType<E>
) : ScalarVariableExpr<E>, NamedScalarExpr<E> {
    init { track() }
    override fun toString() = render()
}
data class MatrixVariable<E:Number>(
    val columns : Int,
    val rows : Int,
    val type : AlgebraicType<E>,
    val defaults : List<E>) {
    init {
        track()
        assert(rows * columns == defaults.size) {
            "expected ${rows*columns} elements"
        }
    }
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = NamedMatrixVariable(property.name, columns, type, defaults)
    override fun toString() = "matrixVariable(${columns}x$rows)"
}
data class NamedMatrixVariable<E:Number>(
    override val name: String,
    override val columns : Int,
    override val type : AlgebraicType<E>,
    val initial : List<E>) : MatrixVariableExpr<E>, NamedMatrixExpr<E> {
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
    override fun toString() = render()
}
data class NamedScalar<E:Number>(
    override val name : String,
    val scalar : ScalarExpr<E>
) : NamedScalarExpr<E>, ParentExpr<E> {
    init { track() }
    override val type get() = scalar.type
    override val children get() = listOf(scalar)
    override fun toString() = render()
}
data class NamedMatrix<E:Number>(
    override val name : String,
    val matrix : MatrixExpr<E>) : NamedMatrixExpr<E> {
    init { track() }
    override val columns get() = matrix.columns
    override val rows get() = matrix.rows
    override val type get() = matrix.type
    override fun get(column: Int, row: Int) = matrix[column,row]
    override fun toString() = render()
}
data class MatrixVariableElement<E:Number>(
    val column : Int,
    val row : Int,
    val matrix : NamedMatrixVariable<E>,
    val initial : E) : ScalarExpr<E> {
    init { track() }
    override val type get() = matrix.type
    override fun toString() = render()
    override fun hashCode() = ((matrix.name.hashCode() * 3) + column) * 5 + row
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is AlgebraicExpr<*>) return false
        if (other.type != type) return false
        if (other !is MatrixVariableElement<*>) return false
        if (matrix.name != other.matrix.name) return false
        if (column != other.column) return false
        if (row != other.row) return false
        return true
    }
}
data class DataMatrix<E:Number>(
    override val columns: Int,
    override val rows: Int,
    val elements: List<ScalarExpr<E>>
) : MatrixExpr<E> {
    init {
        track()
        assert(columns * rows == elements.size) {
            "found ${elements.size} elements but needed ${rows * columns} for $columns by $rows matrix"
        }
        assert(columns > 0)
        assert(rows > 0)
    }
    override val type: AlgebraicType<E> get() = elements[0].type
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
    override fun toString() = render()
}

data class Tableau<E:Number>(
    override val children: List<NamedAlgebraicExpr<E>>,
) : AlgebraicExpr<E>, ParentExpr<E> {
    init { track() }
    override val type get() = children[0].type
    override fun toString() = render()
}

data class Slot<E:Number>(
    val slot : Int,
    val replaces : ScalarExpr<E>
) : ScalarExpr<E> {
    init { track() }
    override val type get() = replaces.type
    override fun toString() = render()
}

data class ScalarAssign<E:Number>(
    val left : NamedScalarVariable<E>,
    val right : ScalarExpr<E>) {
    init { track() }
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = NamedScalarAssign(property.name, left, right)
}

data class MatrixAssign<E:Number>(
    val left : NamedMatrixVariable<E>,
    val right : MatrixExpr<E>) {
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
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = NamedMatrixAssign(property.name, left, right)
}

data class NamedScalarAssign<E:Number>(
    override val name: String,
    val left : NamedScalarVariable<E>,
    val right : ScalarExpr<E>
) : NamedAlgebraicExpr<E>, ParentExpr<E> {
    override val type get() = left.type
    init { track() }
    override fun toString() = render()
    override val children = listOf(right)
}

data class NamedMatrixAssign<E:Number>(
    override val name: String,
    val left : NamedMatrixVariable<E>,
    val right : MatrixExpr<E>
) : NamedAlgebraicExpr<E>, ParentExpr<E> {
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
    override fun toString() = render()
    override val children = listOf(right)
}

// Assign
inline fun <reified E:Number> assign(assignment : Pair<ScalarExpr<E>, NamedScalarVariable<E>>) = ScalarAssign(assignment.second, assignment.first)
inline fun <reified E:Number> assign(assignment : Pair<MatrixExpr<E>, NamedMatrixVariable<E>>) = MatrixAssign(assignment.second, assignment.first)
// Default
fun <E:Number> defaultOf(type : Class<E>) : E = when (type) {
    java.lang.Double::class.java -> 0.0 as E
    java.lang.Integer::class.java -> 0 as E
    java.lang.String::class.java -> "" as E
    else -> error("$type")
}
fun <E:Number> defaultOf(type : KClass<E>) : E = defaultOf(type.java)
inline fun <reified E:Number> defaultOf() : E = defaultOf(E::class)
// Variables
inline fun <reified E:Number> matrixVariable(columns : Int, rows : Int) = matrixVariable(E::class.java.kaneType, columns, rows)
inline fun <reified E:Number> matrixVariable(type : AlgebraicType<E>, columns : Int, rows : Int) =
    MatrixVariable(columns, rows, type, (0 until rows * columns).map { type.zero })
inline fun <reified E:Number> matrixVariable(columns : Int, rows : Int, vararg elements : E) =
    MatrixVariable(columns, rows, E::class.java.kaneType, elements.toList().map { it })
inline fun <reified E:Number> matrixVariable(columns : Int, rows : Int, init : (Coordinate) -> E) =
    MatrixVariable(columns, rows, E::class.java.kaneType, coordinatesOf(columns, rows).toList().map { init(it) })
inline fun <reified E:Number> matrixVariable(columns : Int, rows : Int, type : AlgebraicType<E>, init : (Coordinate) -> Double) =
    MatrixVariable(columns, rows, type, coordinatesOf(columns, rows).toList().map { type.coerceFrom(init(it)) })
inline fun <reified E:Number> variable(initial : E = defaultOf(), type : AlgebraicType<E> = E::class.kaneType) = ScalarVariable(initial, type)
// Constant
fun <E:Number> constant(value : E, type : AlgebraicType<*>) : ScalarExpr<E> = ConstantScalar(value, type as AlgebraicType<E>)
fun <E:Number> constant(value : E) : ScalarExpr<E> = ConstantScalar(value, value.javaClass.kaneType)
inline fun <reified E:Any> constant(value : E) = ValueExpr(value, object : KaneType<E>(E::class.java) { })

// Tableau
fun <E:Number> tableauOf(vararg elements : NamedAlgebraicExpr<E>) : Tableau<E> = Tableau(elements.toList())
// Misc
fun <E:Number> repeated(columns : Int, rows : Int, type : AlgebraicType<E>, value : E) : MatrixExpr<E> =
    repeated(columns, rows, ConstantScalar(value, type))
fun <E:Number> repeated(columns : Int, rows : Int, value : ScalarExpr<E>) : MatrixExpr<E> =
    DataMatrix(columns, rows, (0 until columns * rows).map { value })
fun <E:Number> ScalarExpr<E>.tryFindConstant() : E? = when(this) {
    is ConstantScalar -> value
    is NamedScalar -> scalar.tryFindConstant()
    is CoerceScalar<*> ->
        if (value is ScalarExpr<*> && type.java == value.type.java) value.tryFindConstant() as E?
        else null
    else -> null
}

// Coordinates
inline operator fun <E:Number, reified M:MatrixExpr<E>> M.get(coord : Coordinate) = get(coord.column, coord.row)
val <E:Number> MatrixExpr<E>.coordinates get() = coordinatesOf(columns, rows)
inline val <E:Number, reified M:MatrixExpr<E>> M.elements get() = coordinates.map { get(it) }

// Differentiation
fun <E:Number> diff(expr : ScalarExpr<E>, variable : ScalarExpr<E>) : ScalarExpr<E> {
    if (variable is NamedScalar) return diff(expr, variable.scalar)
    val result: ScalarExpr<E> = when (expr) {
        variable -> ConstantScalar(expr.type.one, expr.type)
        is NamedScalar -> diff(expr.scalar, variable)
        is AlgebraicUnaryMatrixScalar -> diff(expr.reduceArithmetic(), variable)
        is MatrixVariableElement -> ConstantScalar(expr.type.zero, expr.type)
        is ScalarVariableExpr -> ConstantScalar(expr.type.zero, expr.type)
        is ConstantScalar -> ConstantScalar(expr.type.zero, expr.type)
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

fun <E:Number> differentiate(expr : AlgebraicExpr<E>) : AlgebraicExpr<E> {
    fun ScalarExpr<E>.tryD() = when {
        this is AlgebraicUnaryScalar && op == d -> value
        else -> null
    }
    fun MatrixExpr<E>.tryD() = when {
        this is AlgebraicUnaryMatrix && op == d -> value
        else -> null
    }
    fun ScalarExpr<E>.diffOr() : ScalarExpr<E>? {
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
    fun MatrixExpr<E>.diffOr() : MatrixExpr<E>? {
        if (this !is AlgebraicBinaryScalarMatrix<E>) return null
        if (op != divide) return null
        val dleft = left.tryD() ?: return null
        val dright = right.tryD() ?: return null

        return matrixOf(right.columns, right.rows) { (column, row) ->
            diff(dleft, dright[column, row])
        }
    }
    fun ScalarExpr<E>.self() = differentiate(this) as ScalarExpr
    fun MatrixExpr<E>.self() = differentiate(this) as MatrixExpr
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
            is AlgebraicBinaryScalarMatrix<E> -> {
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
//    return expr.replaceExprTopDown {
//        when(it) {
//            is NamedScalar -> {
//                val diff = it.scalar.diffOr()
//                if (diff != null) it.copy(name = "${it.name}'", scalar = diff)
//                else it
//            }
//            is NamedMatrix -> {
//                val diff = it.matrix.diffOr()
//                if (diff != null) it.copy(name = "${it.name}'", matrix = diff)
//                else it
//            }
//            is AlgebraicBinaryScalar<E> ->
//                it.diffOr() ?: it
//            is AlgebraicBinaryScalarMatrix<E> -> it.diffOr() ?: it
//            else -> it
//        }
//    }
}

fun <E:Number> differentiate(expr : ScalarExpr<E>) : ScalarExpr<E> = (differentiate(expr as AlgebraicExpr<E>) as ScalarExpr<E>).reduceArithmetic()
fun <E:Number> differentiate(expr : MatrixExpr<E>) : MatrixExpr<E> = (differentiate(expr as AlgebraicExpr<E>) as MatrixExpr<E>).reduceArithmetic()

// Data matrix

fun <E:Number> MatrixExpr<E>.toDataMatrix() : MatrixExpr<E> = when(this) {
    is DataMatrix -> this
    is NamedMatrix -> copy(matrix = matrix.toDataMatrix())
    else -> DataMatrix(columns, rows, elements.toList())
}
fun <E:Number> MatrixExpr<E>.map(action:(ScalarExpr<E>)->ScalarExpr<E>) = DataMatrix(columns, rows, elements.map(action).toList())

// Evaluate
fun <E:Number> AlgebraicExpr<E>.eval(map : MutableMap<String, E>) : AlgebraicExpr<E> {
    fun NamedAlgebraicExpr<E>.self() = eval(map) as NamedAlgebraicExpr<E>
    fun ScalarExpr<E>.self() = eval(map) as ScalarExpr<E>
    fun MatrixExpr<E>.self() = eval(map) as MatrixExpr<E>
    fun AlgebraicExpr<E>.unwrapConstant() : ConstantScalar<E> = when(this) {
        is ConstantScalar -> this
        is NamedScalar -> scalar.unwrapConstant()
        else ->
            error("$javaClass")
    }
    val result : AlgebraicExpr<E> = when(this) {
        is ConstantScalar -> this
        is MatrixVariableElement -> {
            val name = "${matrix.name}[$column,$row]"
            val bound = map.getValue(name)
            if (bound is ScalarExpr<*>) bound as ScalarExpr<E>
            else ConstantScalar(bound, type)
        }
        is NamedScalarVariable -> {
            val bound = map.getValue(name)
            if (bound is ScalarExpr<*>) bound as ScalarExpr<E>
            else ConstantScalar(bound, type)
        }
        is NamedMatrix -> copy(matrix = matrix.self())
        is NamedScalar -> copy(scalar = scalar.self())
        is DataMatrix -> map { it.self() }
        is AlgebraicBinaryScalar -> copyReduce(left = left.self(), right = right.self())
        is AlgebraicUnaryScalar -> copy(value = value.self())
        is Tableau -> {
            copy(children = children.map { child ->
                when(val result = child.self().reduceArithmetic()) {
                    is NamedScalarAssign -> {
                        val write = result.right.unwrapConstant().value
                        map[result.left.name] = write
                        result
                    }
                    is NamedMatrixAssign -> {
                        result.right.coordinates.forEach { (column, row) ->
                            val name = "${result.left.name}[$column,$row]"
                            val write = result.right[column,row].unwrapConstant().value
                            map[name] = write
                        }
                        result
                    }
                    is NamedScalar -> {
                        map[child.name] = result.unwrapConstant().value
                        result
                    }
                    is NamedMatrix -> {
                        result.matrix.coordinates.forEach { (column, row) ->
                            val name = "${result.name}[$column,$row]"
                            val write = result.matrix[column,row].unwrapConstant().value
                            map[name] = write
                        }
                        result
                    }
                    else -> error("${result.javaClass}")
                }

            })
        }
        is NamedScalarAssign -> copy(right = right.self())
        is NamedMatrixAssign -> copy(right = right.self())
        else ->
            error("$javaClass")
    }
    return result
}

fun <E:Number> Tableau<E>.eval(map : MutableMap<String, E>) = ((this as AlgebraicExpr<E>).eval(map) as Tableau).reduceArithmetic()

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
fun <E:Number> AlgebraicExpr<E>.substituteInitial() : AlgebraicExpr<E> {
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

fun <E:Number> ScalarExpr<E>.substituteInitial() = (this as AlgebraicExpr<E>).substituteInitial() as ScalarExpr<E>
fun <E:Number> MatrixExpr<E>.substituteInitial() = (this as AlgebraicExpr<E>).substituteInitial() as MatrixExpr<E>

// Reduce arithmetic
fun <E:Number> AlgebraicExpr<E>.memoizeAndReduceArithmetic(
    entryPoint : Boolean = true,
    memo : MutableMap<TypedExpr<*>,TypedExpr<*>> = mutableMapOf(),
    selfMemo : MutableMap<TypedExpr<*>,TypedExpr<*>> = mutableMapOf()) : AlgebraicExpr<E> {
    val thiz = selfMemo.computeIfAbsent(this) { this }
    if (memo.contains(thiz)) {
        val result = memo.getValue(thiz)
        return result as AlgebraicExpr<E>
    }
    val result : AlgebraicExpr<E> = with(thiz as AlgebraicExpr<E>) {
        fun <O:Number> AlgebraicExpr<O>.self() = this.memoizeAndReduceArithmetic(false, memo, selfMemo)
        fun ScalarExpr<E>.self() = memoizeAndReduceArithmetic(false, memo, selfMemo) as ScalarExpr<E>
        fun MatrixExpr<E>.self() = memoizeAndReduceArithmetic(false, memo, selfMemo) as MatrixExpr<E>
        fun NamedMatrixVariable<E>.self() = memoizeAndReduceArithmetic(false, memo, selfMemo) as NamedMatrixVariable<E>
        //fun NamedAlgebraicExpr<E>.self() = memoizeAndReduceArithmetic(false, memo, selfMemo) as AlgebraicExpr<E>
        when (this) {
            is NamedMatrixVariable -> this
            is ConstantScalar -> this
            is AlgebraicBinaryMatrix -> {
                val dm = toDataMatrix()
                val dmself = dm.self()
                if (dm != dmself) dmself
                else this
            }
            is DataMatrix -> map { it.self() }
            is MatrixVariableElement -> copy(matrix = matrix.self())
            is AlgebraicUnaryMatrix -> copy(value = value.self())
            is NamedMatrix -> copy(matrix = matrix.self())
            is NamedScalar ->
                if (entryPoint) copy(scalar = scalar.self())
                else scalar.self()
            is NamedScalarVariable -> this
            is AlgebraicBinaryScalarMatrix -> copy(left = left.self(), right = right.self())
            is AlgebraicBinaryMatrixScalar -> copy(left = left.self(), right = right.self())
            is NamedScalarAssign -> copy(right = right.self())
            is NamedMatrixAssign -> copy(right = right.self())
            is Tableau -> copy(children = children.map { it.memoizeAndReduceArithmetic(true, memo, selfMemo) as NamedAlgebraicExpr<E>})
            is CoerceScalar -> {
                val value = when(value) {
                    is AlgebraicExpr<*> -> value.self()
                    else -> value
                }
                when {
                    value is AlgebraicExpr<*> && value.type == type ->  value as AlgebraicExpr<E>
                    value is ConstantScalar<*> -> {
                        assert (type != value.type)
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
                val leftSelf = left.self()
                val rightSelf = right.self()
                val leftAffine = left.affineName()
                val rightAffine = right.affineName()
                if (left != leftSelf || right != right.self()) binaryScalar(op, leftSelf, rightSelf).self()
                else if (leftAffine != null && rightAffine != null && leftAffine.compareTo(rightAffine) == 1 && op.meta.associative) {
                    val result : ScalarExpr<E> = op(right, left).self()
                    result
                }
                else {
                    val leftConst = left.tryFindConstant()
                    val rightConst = right.tryFindConstant()
                    when {
                        leftConst != null && rightConst != null -> ConstantScalar(op(leftConst, rightConst), type)
                        else -> op.reduceArithmetic(left, right)?.self() ?: this
                    }
                }
            }
            is AlgebraicUnaryScalar -> {
                val valueSelf = value.self()
                if (value != valueSelf) copy(value = valueSelf).self()
                else {
                    val valueConst = value.tryFindConstant()
                    when {
                        valueConst != null -> ConstantScalar(op(valueConst), type)
                        else -> op.reduceArithmetic(value)?.self() ?: this
                    }
                }
            }
            is AlgebraicUnaryMatrixScalar -> op.reduceArithmetic(value).self()
            is AlgebraicDeferredDataMatrix -> data.self()
            else ->
                error("$javaClass")
        }
    }
    memo[thiz] = selfMemo.computeIfAbsent(result) { result }
    return result
}

fun <E:Number> NamedScalar<E>.reduceArithmetic() = (this as AlgebraicExpr<E>).memoizeAndReduceArithmetic() as NamedScalar<E>
fun <E:Number> NamedAlgebraicExpr<E>.reduceArithmetic() = (this as AlgebraicExpr<E>).memoizeAndReduceArithmetic() as NamedAlgebraicExpr<E>
fun <E:Number> NamedScalarExpr<E>.reduceArithmetic() = (this as AlgebraicExpr<E>).memoizeAndReduceArithmetic() as NamedScalarExpr<E>
fun <E:Number> ScalarExpr<E>.reduceArithmetic() = (this as AlgebraicExpr<E>).memoizeAndReduceArithmetic() as ScalarExpr<E>
fun <E:Number> MatrixExpr<E>.reduceArithmetic() = (this as AlgebraicExpr<E>).memoizeAndReduceArithmetic() as MatrixExpr<E>
fun <E:Number> Tableau<E>.reduceArithmetic() = (this as AlgebraicExpr<E>).memoizeAndReduceArithmetic() as Tableau<E>

private fun <E:Number> AlgebraicExpr<E>.affineName(depth : Int = 2) : String? {
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
    is AlgebraicBinaryScalar<*> -> op.meta
    is AlgebraicBinaryMatrix<*> -> op.meta
    is AlgebraicBinaryMatrixScalar<*> -> op.meta
    is AlgebraicBinaryScalarMatrix<*> -> op.meta
    is AlgebraicDeferredDataMatrix<*> -> op
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
        is Slot<*> -> "\$$slot"
        is NamedScalarVariable<*> -> name
        is ScalarVariable<*> ->
            type.render(initial)
        is NamedScalarAssign<*> -> "${left.self()} <- ${right.self()}"
        is NamedMatrixAssign<*> -> "${left.self()} <- ${right.self()}"
        is AlgebraicUnaryMatrix<*> -> when {
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
        is AlgebraicBinaryScalar<*> -> {
            when {
                op == pow -> {
                    val rightSuper = tryConvertToSuperscript(right.self())
                    if (rightSuper == null) binary(POW, left, right)
                    else "${left.self()}$rightSuper"

                }
                else -> binary(op.meta, left, right)
            }
        }
        is AlgebraicBinaryMatrixScalar<*> -> binary(op.meta, left, right)
        is AlgebraicBinaryScalarMatrix<*> -> binary(op.meta, left, right)
        is NamedScalar<*> ->
            if (entryPoint) "$name=${scalar.self()}"
            else name
        is NamedMatrix<*> -> {
            when {
                !entryPoint -> name
                matrix is DataMatrix && rows != 1 -> "\n$name\n------\n${matrix.self()}"
                else -> "$name=${matrix.self()}"
            }
        }
        is MatrixVariableElement<*> -> "${matrix.name}[$column,$row]"
        is ConstantScalar<*> -> type.render(value)
        is AlgebraicUnaryMatrixScalar<*> -> "${op.meta.op}(${value.self()})"
        is AlgebraicUnaryScalar<*> -> when {
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
        is NamedMatrixVariable<*> -> name
        is Tableau<*> -> {
            val sb = StringBuilder()
            val sorted = children.sortedBy {
                when(it) {
                    is ScalarExpr<*> -> 0
                    else -> 1
                }
            }
            sorted.forEach { sb.append("$it\n") }
            "$sb".trimEnd('\n')
        }
        is AlgebraicDeferredDataMatrix<*> -> {
            if (op.infix) "${left.self()}${op.op}${right.self(true)}"
            else "${op.op}(${left.self()}, ${right.self()})"
        }
        is DataMatrix<*> -> {
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