@file:Suppress("UNCHECKED_CAST")

package com.github.jomof.kane.rigueur

import java.io.Closeable
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

interface UntypedExpr {
    //fun map(f : (UntypedExpr) -> UntypedExpr) = this
}
interface TypedExpr : UntypedExpr {
    val type : AlgebraicType<*>
}
interface Expr<T:Any> : TypedExpr {
    override val type : AlgebraicType<T>
}
interface ScalarExpr<T:Any> : Expr<T>
interface ParentExpr<T:Any> : Expr<T> {
    val children : Iterable<Expr<T>>
}
interface MatrixExpr<T:Any> : ParentExpr<T> {
    val columns : Int
    val rows : Int
    operator fun get(column : Int, row : Int) : ScalarExpr<T>
    override val children : Iterable<ScalarExpr<T>> get() = elements.asIterable()
}
interface VariableExpr<T:Any> : Expr<T>
interface ScalarVariableExpr<T:Any> : ScalarExpr<T>, VariableExpr<T>
interface MatrixVariableExpr<T:Any> : MatrixExpr<T>, VariableExpr<T>
interface NamedExpr<T:Any> : Expr<T> {
    val name : String
}
interface NamedScalarExpr<T:Any> : NamedExpr<T>, ScalarExpr<T>
interface NamedMatrixExpr<T:Any> : NamedExpr<T>, MatrixExpr<T>
interface UnaryExpr<T:Any> : Expr<T> {
    val op : UnaryOp
    val value : Expr<T>
}
interface BinaryExpr<T:Any> : Expr<T> {
    val op : BinaryOp
    val left : Expr<T>
    val right : Expr<T>
}
data class UnaryOp(val op : String = "") {
    operator fun getValue(nothing: Nothing?, property: KProperty<*>) =
        if (op.isBlank()) copy(op = property.name.toLowerCase())
        else this
}
val D by UnaryOp()
val SUMMATION by UnaryOp(op = "∑")
val LOGIT by UnaryOp()
val TANH by UnaryOp()
val RELU by UnaryOp()
val LRELU by UnaryOp()
val EXP by UnaryOp()
val STEP by UnaryOp()
val LSTEP by UnaryOp()
val NEGATE by UnaryOp(op = "-")

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
val POW by BinaryOp(precedence = 0)
val TIMES by BinaryOp(op = "*", precedence = 1, associative = true, infix = true)
val DIV by BinaryOp(op = "/", precedence = 2, infix = true)
val PLUS by BinaryOp(op = "+", precedence = 3, associative = true, infix = true)
val MINUS by BinaryOp(op = "-", precedence = 4, infix = true)
val CROSS by BinaryOp(op = " cross ", precedence = 5, associative = false, infix = true)
val STACK by BinaryOp(op = " stack ", precedence = 5, associative = true, infix = true)

operator fun <T:Any> ScalarExpr<T>.getValue(thisRef: Any?, property: KProperty<*>) = NamedScalar(property.name, this.record().reduceArithmetic())
operator fun <T:Any> MatrixExpr<T>.getValue(thisRef: Any?, property: KProperty<*>) = NamedMatrix(property.name, this.record().reduceArithmetic())

data class ConvertScalar<E: Any, T:Any>(
    val value : ScalarExpr<T>,
    override val type : AlgebraicType<E>) : ScalarExpr<E> {
    init { track() }
    override fun toString() = render()
}
data class ConstantScalar<E: Any>(
    val value : E,
    override val type : AlgebraicType<E>
) : ScalarExpr<E> {
    init {
        assert(value !is Expr<*>)
        track()
    }
    override fun toString() = render()
}
data class UnaryScalar<E:Any>(
    override val op : UnaryOp,
    override val value : ScalarExpr<E>) : ScalarExpr<E>, UnaryExpr<E>, ParentExpr<E> {
    init {
        track()
    }
    override val type get() = value.type
    override val children get() = listOf(value)
    override fun toString() = render()
}
data class UnaryMatrixScalar<E:Any>(override val op : UnaryOp, override val value : MatrixExpr<E>) : ScalarExpr<E>, UnaryExpr<E>, ParentExpr<E> {
    init { track() }
    override val type get() = value.type
    override val children : Iterable<ScalarExpr<E>> = value.elements.asIterable()
    override fun toString() = render()
}
data class UnaryMatrix<E:Any>(override val op : UnaryOp, override val value : MatrixExpr<E>) : MatrixExpr<E>, UnaryExpr<E> {
    init { track() }
    override val columns get() = value.columns
    override val rows get() = value.rows
    override val type get() = value.type
    override fun get(column: Int, row: Int) = UnaryScalar(op, value[column,row])
    override fun toString() = render()

}
data class BinaryScalar<E:Any>(
    override val op : BinaryOp,
    override val left : ScalarExpr<E>,
    override val right : ScalarExpr<E>) : ScalarExpr<E>, BinaryExpr<E>, ParentExpr<E> {
    init {
        track()
        assert(op != CROSS)
    }
    override val type get() = left.type
    override val children get() = listOf(left, right)
    override fun toString() = render()
}
data class BinaryScalarMatrix<E:Any>(
    override val op : BinaryOp,
    override val columns: Int,
    override val rows: Int,
    override val left : ScalarExpr<E>,
    override val right : MatrixExpr<E>) : MatrixExpr<E>, BinaryExpr<E>  {
    init { track() }
    override val type get() = left.type
    override fun get(column: Int, row: Int) = BinaryScalar(op, left, right[column, row])
    override fun toString() = render()
}
data class BinaryMatrixScalar<E:Any>(
    override val op : BinaryOp,
    override val columns: Int,
    override val rows: Int,
    override val left : MatrixExpr<E>,
    override val right : ScalarExpr<E>) : MatrixExpr<E>, BinaryExpr<E>  {
    init { track() }
    override val type get() = left.type
    override fun get(column: Int, row: Int) = BinaryScalar(op, left[column, row], right)
    override fun toString() = render()
}
data class BinaryMatrix<E:Any>(
    override val op : BinaryOp,
    override val columns: Int,
    override val rows: Int,
    override val left : MatrixExpr<E>,
    override val right : MatrixExpr<E>,
) : MatrixExpr<E>, BinaryExpr<E> {
    init {
        track()
        fun lm() = if (left is NamedExpr<*>) "matrix '${left.name}'" else "left matrix"
        fun rm() = if (right is NamedExpr<*>) "matrix '${right.name}'" else "right matrix"
        when(op) {
            STACK -> {}
            CROSS -> assert(left.columns == right.rows) {
                "${lm()} columns ${left.columns} did not equal ${rm()} rows ${right.rows}"
            }
            else -> {
                assert(left.rows == right.rows) {
                    "${op.op} : ${lm()} columns ${left.rows} did not equal ${rm()} columns ${right.rows}"
                }
                assert(left.columns == right.columns) {
                    "${op.op} : ${lm()} columns ${left.columns} did not equal ${rm()} columns ${right.columns}"
                }
            }
        }
    }
    override val type get() = left.type
    override fun get(column: Int, row: Int) = when(op) {
        TIMES -> left[column,row] * right[column,row]
        DIV -> left[column,row] / right[column,row]
        MINUS -> left[column,row] - right[column,row]
        PLUS -> left[column,row] + right[column,row]
        STACK -> {
            if (row < left.rows) left[column, row]
            else right[column, row - left.rows]
        }
        CROSS -> {
            (1 until left.columns)
                .fold(left[0, row] * right[column, 0]) { prior : ScalarExpr<E>, i ->
                    prior + left[i, row] * right[column, i]
                }
        }
        else -> error("$op")
    }
    override fun toString() = render()
}
data class ScalarVariable<E:Any>(val initial : E) {
    init { track() }
    val type get() = initial.javaClass.kotlin
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = NamedScalarVariable(property.name, initial)
    override fun toString() = "?"
}
data class NamedScalarVariable<E:Any>(
    override val name : String,
    val initial : E) : ScalarVariableExpr<E>, NamedScalarExpr<E> {
    init { track() }
    override fun toString() = render()
    override val type get() = initial.javaClass.algebraicType
}
data class MatrixVariable<E:Any>(
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
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = NamedMatrixVariable(property.name, columns, rows, type, defaults)
    override fun toString() = "matrixVariable(${columns}x$rows)"
}
data class NamedMatrixVariable<E:Any>(
    override val name: String,
    override val columns : Int,
    override val rows : Int,
    override val type : AlgebraicType<E>,
    val initial : List<E>) : MatrixVariableExpr<E>, NamedMatrixExpr<E> {
    init {
        track()
        assert(initial.size == rows * columns)
    }
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
data class NamedScalar<E:Any>(
    override val name : String,
    val scalar : ScalarExpr<E>) : NamedScalarExpr<E>, ParentExpr<E> {
    init { track() }
    override val type get() = scalar.type
    override val children get() = listOf(scalar)
    override fun toString() = render()
}
data class NamedMatrix<E:Any>(
    override val name : String,
    val matrix : MatrixExpr<E>) : NamedMatrixExpr<E> {
    init { track() }
    override val columns get() = matrix.columns
    override val rows get() = matrix.rows
    override val type get() = matrix.type
    override fun get(column: Int, row: Int) = matrix[column,row]
    override fun toString() = render()
}
data class MatrixVariableElement<E:Any>(
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
        if (other !is Expr<*>) return false
        if (other.type != type) return false
        if (other !is MatrixVariableElement<*>) return false
        if (matrix.name != other.matrix.name) return false
        if (column != other.column) return false
        if (row != other.row) return false
        return true
    }
}
data class DataMatrix<E:Any>(
    override val columns: Int,
    override val rows: Int,
    val elements: List<ScalarExpr<E>>
) : MatrixExpr<E> {
    init {
        track()
        assert(columns * rows == elements.size) {
            "found ${elements.size} elements but needed ${rows * columns} for $columns by $rows matrix"
        }
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

data class Tableau<E:Any>(
    override val children: List<NamedExpr<E>>,
) : ParentExpr<E> {
    init { track() }
    override val type get() = children[0].type
    override fun toString() = render()
}

data class Slot<E:Any>(
    val slot : Int,
    val replaces : ScalarExpr<E>
) : ScalarExpr<E> {
    init { track() }
    override val type get() = replaces.type
    override fun toString() = render()
}

data class ScalarAssign<E:Any>(
    val left : NamedScalarVariable<E>,
    val right : ScalarExpr<E>) {
    init { track() }
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = NamedScalarAssign(property.name, left, right)
}

data class MatrixAssign<E:Any>(
    val left : NamedMatrixVariable<E>,
    val right : MatrixExpr<E>) {
    init {
        track()
        fun lm() = "matrix '${left.name}'"
        fun rm() = if (right is NamedExpr<*>) "matrix '${right.name}'" else "right matrix"
        assert(left.rows == right.rows) {
            "${lm()} columns ${left.rows} did not equal ${rm()} columns ${right.rows}"
        }
        assert(left.columns == right.columns) {
            "${lm()} columns ${left.columns} did not equal ${rm()} columns ${right.columns}"
        }
    }
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = NamedMatrixAssign(property.name, left, right)
}

data class NamedScalarAssign<E:Any>(
    override val name: String,
    val left : NamedScalarVariable<E>,
    val right : ScalarExpr<E>,
) : NamedExpr<E> {
    override val type get() = left.type
    init { track() }
    override fun toString() = render()
}

data class NamedMatrixAssign<E:Any>(
    override val name: String,
    val left : NamedMatrixVariable<E>,
    val right : MatrixExpr<E>
) : NamedExpr<E> {
    override val type get() = left.type
    init { track() }
    override fun toString() = render()
}


// Pow
fun <E:Any> pow(left : ScalarExpr<E>, right : E) = BinaryScalar(POW, left, ConstantScalar(right, left.type))
fun <E:Any> pow(left : MatrixExpr<E>, right : E) = BinaryMatrixScalar(POW, left.columns, left.rows, left, ConstantScalar(right, left.type))
fun <E:Any, L : ScalarExpr<E>, R : ScalarExpr<E>> pow(left : L, right : R) : ScalarExpr<E> = BinaryScalar(POW, left, right)
fun <E:Any, L : MatrixExpr<E>, R : ScalarExpr<E>> pow(left : L, right : R) = BinaryMatrixScalar(POW, left.columns, left.rows, left, right)
// Relu
fun <E:Any, T : ScalarExpr<E>> relu(expr : T) : ScalarExpr<E> = UnaryScalar(RELU, expr)
fun <E:Any, T : MatrixExpr<E>> relu(expr : T) : MatrixExpr<E> = UnaryMatrix(RELU, expr)
fun relu(value : Double) : Double = max(0.0, value)
fun relu(value : Float) : Float = max(0.0f, value)
// Leaky Relu
fun <E:Any, T : ScalarExpr<E>> lrelu(expr : T) : ScalarExpr<E> = UnaryScalar(LRELU, expr)
fun <E:Any, T : MatrixExpr<E>> lrelu(expr : T) : MatrixExpr<E> = UnaryMatrix(LRELU, expr)
fun lrelu(value : Double) : Double = max(0.1 * value, value)
fun lrelu(value : Float) : Float = max(0.1f * value, value)
// Step
fun <E:Any, T : ScalarExpr<E>> step(expr : T) : ScalarExpr<E> = UnaryScalar(STEP, expr)
fun step(value : Double) = if (value < 0.0) 0.0 else 1.0
fun step(value : Float) = if (value < 0.0f) 0.0f else 1.0f
// Lstep
fun <E:Any, T : ScalarExpr<E>> lstep(expr : T) : ScalarExpr<E> = UnaryScalar(LSTEP, expr)
fun lstep(value : Double) = if (value < 0.0) 0.1 else 1.0
fun lstep(value : Float) = if (value < 0.0f) 0.1f else 1.0f
// Expr
fun <E:Any> exp(expr : ScalarExpr<E>) = UnaryScalar(EXP, expr)
fun <E:Any> exp(expr : MatrixExpr<E>) = UnaryMatrix(EXP, expr)
// Softmax
fun <E:Any> softmax(expr : MatrixExpr<E>) : MatrixExpr<E> = exp(expr) / summation(exp(expr))
// Logit
fun <E:Any, T : ScalarExpr<E>> logit(expr : T) : ScalarExpr<E> = UnaryScalar(LOGIT, expr)
fun <E:Any, T : MatrixExpr<E>> logit(expr : T) : MatrixExpr<E> = UnaryMatrix(LOGIT, expr)
fun logit(value : Double) : Double = 1.0 / (1.0 + exp(-value))
fun logit(value : Float) : Float = 1.0f / (1.0f + exp(-value))
// tanh
fun <E:Any, T : ScalarExpr<E>> tanh(expr : T) : ScalarExpr<E> = UnaryScalar(TANH, expr)
fun <E:Any, T : MatrixExpr<E>> tanh(expr : T) : MatrixExpr<E> = UnaryMatrix(TANH, expr)
fun tanh(value : Double) : Double = (exp(2.0 * value) - 1.0) / (exp(2.0 * value) + 1.0)
fun tanh(value : Float) : Float = (exp(2.0f * value) - 1.0f) / (exp(2.0f * value) + 1.0f)
// Times
operator fun <E:Any, L : ScalarExpr<E>, R : E> L.times(right : R) = BinaryScalar(TIMES, this, ConstantScalar(right, type))
operator fun <E:Any, L : ScalarExpr<E>, R : ScalarExpr<E>> L.times(right : R) = BinaryScalar(TIMES, this, right)
operator fun <E:Any> E.times(right : ScalarExpr<E>) = BinaryScalar(TIMES, ConstantScalar(this, right.type), right)
operator fun <E:Any, L : MatrixExpr<E>, R : E> L.times(right : R) = BinaryMatrixScalar(TIMES, columns, rows, this, ConstantScalar(right, type))
operator fun <E:Any, L : E, R : MatrixExpr<E>> L.times(right : R) = BinaryScalarMatrix(TIMES, right.columns, right.rows, ConstantScalar(this, right.type), right)
operator fun <E:Any, L : MatrixExpr<E>, R : ScalarExpr<E>> L.times(right : R) = BinaryMatrixScalar(TIMES, columns, rows, this, right)
operator fun <E:Any, L : ScalarExpr<E>, R : MatrixExpr<E>> L.times(right : R) = BinaryScalarMatrix(TIMES, right.columns, right.rows, this, right)
operator fun <E:Any> MatrixExpr<E>.times(right : MatrixExpr<E>) = run {
    BinaryMatrix(TIMES, right.columns, rows, this, right)
}
infix fun <E:Any> MatrixExpr<E>.cross(right : MatrixExpr<E>) = run {
    BinaryMatrix(CROSS, right.columns, rows, this, right)
}

// Div
operator fun <E:Any, L : ScalarExpr<E>, R : E> L.div(right : R) = BinaryScalar(DIV, this, ConstantScalar(right, type))
operator fun <E:Any, L : ScalarExpr<E>, R : ScalarExpr<E>> L.div(right : R) = BinaryScalar(DIV, this, right)
operator fun <E:Any> E.div(right : ScalarExpr<E>) = BinaryScalar(DIV, ConstantScalar(this, right.type), right)
operator fun <E:Any, L : MatrixExpr<E>, R : E> L.div(right : R) = BinaryMatrixScalar(DIV, columns, rows, this, ConstantScalar(right, type))
operator fun <E:Any, L : E, R : MatrixExpr<E>> L.div(right : R) = BinaryScalarMatrix(DIV, right.columns, right.rows, ConstantScalar(this, right.type), right)
operator fun <E:Any, L : MatrixExpr<E>, R : ScalarExpr<E>> L.div(right : R) = BinaryMatrixScalar(DIV, columns, rows, this, right)
operator fun <E:Any, L : ScalarExpr<E>, R : MatrixExpr<E>> L.div(right : R) = BinaryScalarMatrix(DIV, right.columns, right.rows, this, right)
// Plus
operator fun <E:Any, L : ScalarExpr<E>, R : E> L.plus(right : R) = BinaryScalar(PLUS, this, ConstantScalar(right, type))
operator fun <E:Any, L : ScalarExpr<E>, R : ScalarExpr<E>> L.plus(right : R) : ScalarExpr<E> = BinaryScalar(PLUS, this, right)
operator fun <E:Any> E.plus(right : ScalarExpr<E>) = BinaryScalar(PLUS, ConstantScalar(this, right.type), right)
operator fun <E:Any, L : MatrixExpr<E>, R : E> L.plus(right : R) = BinaryMatrixScalar(PLUS, columns, rows, this, ConstantScalar(right, type))
operator fun <E:Any, L : E, R : MatrixExpr<E>> L.plus(right : R) = BinaryScalarMatrix(PLUS, right.columns, right.rows, ConstantScalar(this, right.type), right)
operator fun <E:Any, L : MatrixExpr<E>, R : ScalarExpr<E>> L.plus(right : R) = BinaryMatrixScalar(PLUS, columns, rows, this, right)
operator fun <E:Any, L : ScalarExpr<E>, R : MatrixExpr<E>> L.plus(right : R) = BinaryScalarMatrix(PLUS, right.columns, right.rows, this, right)
operator fun <E:Any, L : MatrixExpr<E>, R : MatrixExpr<E>> L.plus(right : R) = BinaryMatrix(PLUS, right.columns, right.rows, this, right)
// Minus
operator fun <E:Any, L : ScalarExpr<E>, R : E> L.minus(right : R) = BinaryScalar(MINUS, this, ConstantScalar(right, type))
operator fun <E:Any, L : ScalarExpr<E>, R : ScalarExpr<E>> L.minus(right : R) = BinaryScalar(MINUS, this, right)
operator fun <E:Any> E.minus(right : ScalarExpr<E>) = BinaryScalar(MINUS, ConstantScalar(this, right.type), right)
operator fun <E:Any, L : MatrixExpr<E>, R : E> L.minus(right : R) = BinaryMatrixScalar(MINUS, columns, rows, this, ConstantScalar(right, type))
operator fun <E:Any, L : E, R : MatrixExpr<E>> L.minus(right : R) = BinaryScalarMatrix(MINUS, right.columns, right.rows, ConstantScalar(this, right.type), right)
operator fun <E:Any, L : MatrixExpr<E>, R : ScalarExpr<E>> L.minus(right : R) = BinaryMatrixScalar(MINUS, columns, rows, this, right)
operator fun <E:Any, L : ScalarExpr<E>, R : MatrixExpr<E>> L.minus(right : R) = BinaryScalarMatrix(MINUS, right.columns, right.rows, this, right)
operator fun <E:Any, L : MatrixExpr<E>, R : MatrixExpr<E>> L.minus(right : R) = BinaryMatrix(MINUS, right.columns, right.rows, this, right)
// Unary minus
operator fun <E:Any> ScalarExpr<E>.unaryMinus() = UnaryScalar(NEGATE, this)
operator fun <E:Any> MatrixExpr<E>.unaryMinus() = UnaryMatrix(NEGATE, this)
// Stack
private fun <E:Any> stackMatrix(left : MatrixExpr<E>, right : MatrixExpr<E>) : MatrixExpr<E> {
    assert(left.columns == right.columns)
    return BinaryMatrix(STACK, left.columns, left.rows + right.rows, left, right)
}
infix fun <E:Any> MatrixExpr<E>.stack(right : E) : MatrixExpr<E> = stackMatrix(this, repeated(columns, 1, type, right))
infix fun <E:Any> E.stack(right : MatrixExpr<E>) : MatrixExpr<E> = stackMatrix(repeated(right.columns, 1, right.type, this), right)
infix fun <E:Any> MatrixExpr<E>.stack(right : ScalarExpr<E>) = stackMatrix(this, repeated(columns, 1, right))
infix fun <E:Any> ScalarExpr<E>.stack(right : MatrixExpr<E>) =stackMatrix(repeated(right.columns, 1, this), right)
infix fun <E:Any> MatrixExpr<E>.stack(right : MatrixExpr<E>) = stackMatrix(this, right)
// Summation
fun <E:Any> summation(expr : MatrixExpr<E>) : ScalarExpr<E> = UnaryMatrixScalar(SUMMATION, expr)
// Assign
inline fun <reified E:Any> assign(assignment : Pair<ScalarExpr<E>, NamedScalarVariable<E>>) = ScalarAssign(assignment.second, assignment.first)
inline fun <reified E:Any> assign(assignment : Pair<MatrixExpr<E>, NamedMatrixVariable<E>>) = MatrixAssign(assignment.second, assignment.first)
// Default
fun <E:Any> defaultOf(type : Class<E>) : E = when (type) {
    java.lang.Double::class.java -> 0.0 as E
    java.lang.Integer::class.java -> 0 as E
    java.lang.String::class.java -> "" as E
    else -> error("$type")
}
fun <E:Any> defaultOf(type : KClass<E>) : E = defaultOf(type.java)
inline fun <reified E:Any> defaultOf() : E = defaultOf(E::class)
// Variables
inline fun <reified E:Any> matrixVariable(columns : Int, rows : Int) = matrixVariable(E::class.java.algebraicType, columns, rows)
inline fun <reified E:Any> matrixVariable(type : AlgebraicType<E>, columns : Int, rows : Int) =
    MatrixVariable(columns, rows, type, (0 until rows * columns).map { type.zero })
inline fun <reified E:Any> matrixVariable(columns : Int, rows : Int, vararg elements : E) =
    MatrixVariable(columns, rows, E::class.java.algebraicType, elements.toList().map { it })
inline fun <reified E:Any> matrixVariable(columns : Int, rows : Int, init : (Coordinate) -> E) =
    MatrixVariable(columns, rows, E::class.java.algebraicType, coordinatesOf(columns, rows).toList().map { init(it) })
inline fun <reified E:Any> matrixVariable(columns : Int, rows : Int, type : AlgebraicType<E>, init : (Coordinate) -> Double) =
    MatrixVariable(columns, rows, type, coordinatesOf(columns, rows).toList().map { type.fromDouble(init(it)) })
inline fun <reified E:Any> variable(initial : E = defaultOf()) = ScalarVariable(initial)
inline fun <reified E:Any> columnVariable(vararg elements : E) =
    MatrixVariable(1, elements.size, E::class.java.algebraicType, elements.toList().map { it })
// Fold
fun <E:Any> MatrixExpr<E>.fold(
    seed : ScalarExpr<E>,
    op : (prior:ScalarExpr<E>, current:ScalarExpr<E>) -> ScalarExpr<E>) : ScalarExpr<E> {
    return elements.fold(seed) { prior, current -> op(prior, current) }
}

fun <E:Any> MatrixExpr<E>.foldIndexed(
    seed : ScalarExpr<E>,
    op : (coord : Coordinate, prior:ScalarExpr<E>, current:ScalarExpr<E>) -> ScalarExpr<E>) : ScalarExpr<E> {
    return coordinates.fold(seed) { prior, coord -> op(coord, prior, this[coord]) }
}

fun <E:Any> MatrixExpr<E>.foldIndexedToMatrix(
    seed : ScalarExpr<E>,
    op : (coord : Coordinate, prior:ScalarExpr<E>, current:ScalarExpr<E>) -> ScalarExpr<E>) : MatrixExpr<E> {
    var prior = seed
    return DataMatrix(columns, rows, coordinates.map { coord ->
        prior = op(coord,  prior, this[coord])
        prior
    }.toList())
}

// Tableau
fun <E:Any> tableauOf(vararg elements : NamedExpr<E>) : Tableau<E> = Tableau(elements.toList())
// Misc
//fun <E:Any> constant(value : E) : ScalarExpr<E> = ConstantScalar(value)
fun <E:Any> repeated(columns : Int, rows : Int, type : AlgebraicType<E>, value : E) : MatrixExpr<E> =
    repeated(columns, rows, ConstantScalar(value, type))
fun <E:Any> repeated(columns : Int, rows : Int, value : ScalarExpr<E>) : MatrixExpr<E> =
    DataMatrix(columns, rows, (0 until columns * rows).map { value })
fun <E:Any> ScalarExpr<E>.tryFindConstant() : E? = when(this) {
    is ConstantScalar -> value
    is NamedScalar -> scalar.tryFindConstant()
    else -> null
}
fun <E:Any> ScalarExpr<E>.tryFindNegation(): ScalarExpr<E>? {
    return if (this is UnaryScalar<*> && op == NEGATE) return value as ScalarExpr<E>
    else null
}
// Coordinates
inline operator fun <E:Any, reified M:MatrixExpr<E>> M.get(coord : Coordinate) = get(coord.column, coord.row)
val <E:Any> MatrixExpr<E>.coordinates get() = coordinatesOf(columns, rows)
inline val <E:Any, reified M:MatrixExpr<E>> M.elements get() = coordinates.map { get(it) }
fun <E:Any,R:Any> MatrixExpr<E>.foldCoordinates(initial: ScalarExpr<R>?, action : (ScalarExpr<R>?, Coordinate)->ScalarExpr<R>) =
    coordinates.fold(initial) { prior, coord -> action(prior, coord) }


// Differentiation
fun <E:Any, T : ScalarExpr<E>> d(expr : T) : ScalarExpr<E> = UnaryScalar(D, expr)
fun <E:Any, T : MatrixExpr<E>> d(expr : T) : MatrixExpr<E> = UnaryMatrix(D, expr)

private fun <E:Any> diff(expr : ScalarExpr<E>, variable : ScalarExpr<E>) : ScalarExpr<E> {
    if (variable is NamedScalar) return diff(expr, variable.scalar)
    fun ScalarExpr<E>.self() = diff(this, variable)
    val result : ScalarExpr<E> = when (expr) {
        variable -> ConstantScalar(expr.type.one, expr.type)
        is UnaryScalar -> when(expr.op) {
            LOGIT -> logit(expr.value) * (ConstantScalar(expr.type.one, expr.type) - logit(expr.value)) * expr.value.self()
            RELU -> step(expr.value) * expr.value.self()
            LRELU -> lstep(expr.value) * expr.value.self()
            EXP -> {
                val result = exp(expr.value) * expr.value.self()
                result
            }
            TANH -> ConstantScalar(expr.type.one, expr.type) - pow(tanh(expr.value), expr.type.two) * expr.value.self()
            NEGATE -> -expr.value.self()
            else -> error("${expr.op}")
        }
        is NamedScalar -> expr.scalar.self()
        is UnaryMatrixScalar -> when(expr.op) {
            SUMMATION -> {
                expr.value.foldCoordinates(null) { prior, (column, row) ->
                    val result = expr.value[column, row].self()
                    if (prior == null) result
                    else prior + result
                }!!
            }
            else -> error("${expr.op}")
        }
        is BinaryScalar -> {
            val diffLeft = expr.left.self()
            val diffRight = expr.right.self()
            val result : ScalarExpr<E> = when(expr.op) {
                PLUS -> diffLeft + diffRight
                TIMES -> expr.left * diffRight + diffLeft * expr.right
                DIV -> {
                    val topLeft = diffLeft * expr.right
                    val topRight = expr.left * diffRight
                    val top = topLeft - topRight
                    val bottom = pow(expr.right, expr.type.two)
                    top / bottom
                }
                MINUS -> diffLeft - diffRight
                POW -> expr.right * pow(expr.left, expr.right - expr.type.one) * diffLeft
                else -> error("${expr.op}")
            }
            result
        }
        is MatrixVariableElement -> ConstantScalar(expr.type.zero, expr.type)
        is ScalarVariableExpr -> ConstantScalar(expr.type.zero, expr.type)
        is ConstantScalar -> ConstantScalar(expr.type.zero, expr.type)
        else -> error("${expr.javaClass}")
    }
    return result
}

fun <E:Any> differentiate(expr : Expr<E>) : Expr<E> {
    fun ScalarExpr<E>.diffOr() : ScalarExpr<E>? {
        if (this !is BinaryScalar<E>) return null
        if (op != DIV) return null
        if (left !is UnaryScalar) return null
        if (left.op != D) return null
        if (right !is UnaryScalar) return null
        if (right.op != D) return null
        return diff(left.value, right.value)
    }
    fun MatrixExpr<E>.diffOr() : MatrixExpr<E>? {
        if (this !is BinaryScalarMatrix<E>) return null
        if (op != DIV) return null
        if (left !is UnaryScalar) return null
        if (left.op != D) return null
        if (right !is UnaryMatrix) return null
        if (right.op != D) return null
        return matrixOf(right.columns, right.rows) { (column, row) ->
            diff(left.value, right.value[column, row])
        }
    }
    return expr.replaceExpr {
        when(it) {
            is NamedScalar -> {
                val diff = it.scalar.diffOr()
                if (diff != null) it.copy(name = "${it.name}'", scalar = diff)
                else it
            }
            is NamedMatrix -> {
                val diff = it.matrix.diffOr()
                if (diff != null) it.copy(name = "${it.name}'", matrix = diff)
                else it
            }
            is BinaryScalar<E> -> it.diffOr() ?: it
            is BinaryScalarMatrix<E> -> it.diffOr() ?: it
            else -> it
        }
    }
}

fun <E:Any> differentiate(expr : ScalarExpr<E>) : ScalarExpr<E> = (differentiate(expr as Expr<E>) as ScalarExpr<E>).reduceArithmetic()
fun <E:Any> differentiate(expr : MatrixExpr<E>) : MatrixExpr<E> = (differentiate(expr as Expr<E>) as MatrixExpr<E>).reduceArithmetic()

// Data matrix
inline fun <reified E:Any> matrixOf(columns: Int, rows: Int, vararg elements : E) = DataMatrix(
    columns,
    rows,
    elements.map { ConstantScalar(it, elements[0].javaClass.algebraicType) }.toList()
)
fun <E:Any> matrixOf(columns: Int, rows: Int, action:(Coordinate)->ScalarExpr<E>) :DataMatrix<E> =
    DataMatrix(
        columns,
        rows,
        coordinatesOf(columns, rows).map(action).toList()
    )
fun <E:Any> MatrixExpr<E>.toDataMatrix() : MatrixExpr<E> = when(this) {
    is DataMatrix -> this
    is NamedMatrix -> copy(matrix = matrix.toDataMatrix())
    else -> DataMatrix(columns, rows, elements.toList())
}
fun <E:Any> MatrixExpr<E>.map(action:(ScalarExpr<E>)->ScalarExpr<E>) = DataMatrix(columns, rows, elements.map(action).toList())

// Evaluate
fun <E:Any> Expr<E>.eval(map : MutableMap<String, E>) : Expr<E> {
    fun NamedExpr<E>.self() = eval(map) as NamedExpr<E>
    fun ScalarExpr<E>.self() = eval(map) as ScalarExpr<E>
    fun MatrixExpr<E>.self() = eval(map) as MatrixExpr<E>
    fun Expr<E>.unwrapConstant() : ConstantScalar<E> = when(this) {
        is ConstantScalar -> this
        is NamedScalar -> scalar.unwrapConstant()
        else ->
            error("$javaClass")
    }
    val result : Expr<E> = when(this) {
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
        is BinaryMatrix -> toDataMatrix().self()
        is BinaryScalar -> copy(left = left.self(), right = right.self())
        is UnaryScalar -> copy(value = value.self())
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

fun <E:Any> Tableau<E>.eval(map : MutableMap<String, E>) = ((this as Expr<E>).eval(map) as Tableau).reduceArithmetic()

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
            e.stackTrace.toList()
                .drop(1)
                .filter { it.fileName?.contains("Kane")?:false }
                .filter { it.lineNumber > 1}
                .take(15)
        }
        traceMap[stack] = traceMap.computeIfAbsent(stack) { 0 } + 1
        val count = traceMap[stack]!!
        if (traceMap[offender]?:0 < count) {
            offender = stack
        }
    }
}
private fun track() {
    if (trackingEnabled)  trackImpl()
}

private val recordedExprs = mutableSetOf<UntypedExpr>()
fun getRecordedExprs() : Set<UntypedExpr> = recordedExprs

private fun recordImpl(expr : UntypedExpr) {
    if (recordedExprs.contains(expr)) return
    recordedExprs += expr
    when(expr) {
        is ParentExpr<*> -> expr.children.forEach { recordImpl(it) }
        else -> { }
    }
}

private fun <T:UntypedExpr> T.record() : T {
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
fun <E:Any> Expr<E>.substituteInitial() = replaceExpr {
        with(it) {
            when (this) {
                is NamedScalarVariable -> ConstantScalar(initial, type)
                is MatrixVariableElement -> ConstantScalar(initial, type)
                is NamedMatrixVariable -> toDataMatrix()
                else -> this
            }
        }
    }

fun <E:Any> ScalarExpr<E>.substituteInitial() = (this as Expr<E>).substituteInitial() as ScalarExpr<E>
fun <E:Any> MatrixExpr<E>.substituteInitial() = (this as Expr<E>).substituteInitial() as MatrixExpr<E>

// Reduce arithmetic
fun <E:Any> Expr<E>.memoizeAndReduceArithmetic(
    memo : MutableMap<Expr<E>,Expr<E>> = mutableMapOf(),
    selfMemo : MutableMap<Expr<E>,Expr<E>> = mutableMapOf()) : Expr<E> {
    val thiz = selfMemo.computeIfAbsent(this) { this }
    if (memo.contains(thiz)) {
        val result = memo.getValue(thiz)
        return result
    }
    val result : Expr<E> = with(thiz) {
        fun ScalarExpr<E>.self() = memoizeAndReduceArithmetic(memo, selfMemo) as ScalarExpr<E>
        fun MatrixExpr<E>.self() = memoizeAndReduceArithmetic(memo, selfMemo) as MatrixExpr<E>
        fun NamedMatrixVariable<E>.self() = memoizeAndReduceArithmetic(memo, selfMemo) as NamedMatrixVariable<E>
        fun NamedExpr<E>.self() = memoizeAndReduceArithmetic(memo, selfMemo) as NamedExpr<E>
        when (this) {
            is NamedMatrixVariable -> this
            is ConstantScalar -> this
            is BinaryMatrix -> {
                val dm = toDataMatrix()
                val dmself = dm.self()
                if (dm != dmself) dmself
                else this
            }
            is DataMatrix -> map { it.self() }
            is MatrixVariableElement -> copy(matrix = matrix.self())
            is UnaryMatrix -> copy(value = value.self())
            is NamedMatrix -> copy(matrix = matrix.self())
            is NamedScalar -> copy(scalar = scalar.self())
            is NamedScalarVariable -> this
            is UnaryScalar -> {
                val value = value.self()
                when {
                    value is ConstantScalar -> ConstantScalar(type.unary(op, value.value), type)
                    op == NEGATE && value is UnaryScalar && value.op == NEGATE -> value.value
                    else -> copy(value = value.self())
                }
            }
            is UnaryMatrixScalar -> {
                val result: Expr<E> = when {
                    op == SUMMATION -> children.fold(ConstantScalar(type.zero, type) as ScalarExpr<E>) { prior, current -> prior + current }.self()
                    else -> copy(value = value.self())
                }
                result
            }
            is BinaryMatrixScalar -> copy(left = left.self(), right = right.self())
            is BinaryScalarMatrix -> copy(left = left.self(), right = right.self())
            is BinaryScalar -> {
                val leftConst = left.tryFindConstant()
                val rightConst = right.tryFindConstant()
                val leftAffine = left.affineName()
                val rightAffine = right.affineName()
                val result: Expr<E> = when {
                    leftConst != null && rightConst != null -> ConstantScalar(type.binary(op, leftConst, rightConst), type)
                    leftAffine != null && rightAffine != null && leftAffine.compareTo(rightAffine) == 1 ->
                        BinaryScalar(op, right, left).self()
                    op == TIMES && left is UnaryScalar && left.op == NEGATE && right is UnaryScalar && right.op == NEGATE ->
                        (left.value * right.value).self()
                    op == DIV && left is UnaryScalar && left.op == NEGATE && right is UnaryScalar && right.op == NEGATE ->
                        (left.value / right.value).self()
                    op == TIMES && right == left -> pow(left, type.two).self()
                    op == TIMES && rightConst == type.zero -> right
                    op == TIMES && rightConst == type.one -> left.self()
                    op == TIMES && leftConst == type.zero -> left
                    op == TIMES && leftConst == type.one -> right.self()
                    op == TIMES && leftConst == type.negativeOne -> (-right).self()
                    op == TIMES && rightConst == type.negativeOne -> (-left).self()
                    op == PLUS && rightConst == type.zero -> left.self()
                    op == PLUS && leftConst == type.zero -> right.self()
                    op == MINUS && leftConst == type.zero -> (-right).self()
                    op == MINUS && rightConst == type.zero -> left.self()
                    op == MINUS && right is UnaryScalar && right.op == NEGATE -> (left + right.value).self()
                    op == POW && rightConst == type.one -> left.self()
                    op == POW && rightConst == type.zero -> ConstantScalar(type.one, type)
                    op == TIMES && right is UnaryScalar && right.op == NEGATE -> (-(left * right.value)).self()
                    op == TIMES && left is UnaryScalar && left.op == NEGATE -> (-(left.value * right)).self()
                    op == DIV && right is BinaryScalar && right.op == POW -> {
                        val result = (left * pow(right.left, -right.right))
                        result.self()
                    }
                    op == TIMES && left !is ConstantScalar && right is ConstantScalar -> BinaryScalar(op, right, left).self()
                    op == PLUS && right !is ConstantScalar && left is ConstantScalar -> BinaryScalar(op, right, left).self()
                    op.associative && left !is ConstantScalar && right is BinaryScalar && right.op == op && right.left is ConstantScalar -> {
                        BinaryScalar(op, right.left, BinaryScalar(op, left, right.right)).self()
                    }
                    op.associative && left is BinaryScalar && left.op == op && left.left is ConstantScalar -> {
                        BinaryScalar(op, left.left, BinaryScalar(op, left.right, right)).self()
                    }
                    op == TIMES && left is BinaryScalar && left.op == TIMES && left.right == right -> {
                        (left.left * pow(right, type.two)).self()
                    }
                    op == TIMES && left is BinaryScalar && left.op == TIMES && left.left == right -> {
                        (left.right * pow(right, type.two)).self()
                    }
                    op == TIMES && left is ConstantScalar && right is BinaryScalar && right.op == TIMES && right.left is ConstantScalar -> {
                        (ConstantScalar(type.multiply(left.value, right.left.value), type) * right.right).self()
                    }
                    op == TIMES && left is ConstantScalar && right is BinaryScalar && right.op == TIMES && right.right is ConstantScalar -> {
                        (ConstantScalar(type.multiply(left.value, right.right.value), type) * right.left).self()
                    }
                    op == PLUS && left is ConstantScalar && right is BinaryScalar && right.op == PLUS && right.left is ConstantScalar -> {
                        (ConstantScalar(type.add(left.value, right.left.value), type) + right.right).self()
                    }
                    op == PLUS && left is ConstantScalar && right is BinaryScalar && right.op == PLUS && right.right is ConstantScalar -> {
                        (ConstantScalar(type.add(left.value, right.right.value), type) + right.left).self()
                    }
                    op == MINUS && right is ConstantScalar && left is BinaryScalar && left.op == PLUS && left.right is ConstantScalar -> {
                        (left.left + type.subtract(left.right.value, right.value)).self()
                    }
                    else -> {
                        val left = left.self()
                        val right = right.self()
                        when {
                            this.left != left || this.right != right -> BinaryScalar(op, left, right).self()
                            else -> this
                        }
                    }
                }
                result
            }
            is NamedScalarAssign -> copy(right = right.self())
            is Tableau -> copy(children = children.map { it.self() })
            else -> error("$javaClass")
        }
    }
    memo[thiz] = selfMemo.computeIfAbsent(result) { result }
    return result
}

fun <E:Any> NamedExpr<E>.reduceArithmetic() = (this as Expr<E>).memoizeAndReduceArithmetic() as NamedExpr<E>
fun <E:Any> ScalarExpr<E>.reduceArithmetic() = (this as Expr<E>).memoizeAndReduceArithmetic() as ScalarExpr<E>
fun <E:Any> MatrixExpr<E>.reduceArithmetic() = (this as Expr<E>).memoizeAndReduceArithmetic() as MatrixExpr<E>
fun <E:Any> Tableau<E>.reduceArithmetic() = (this as Expr<E>).memoizeAndReduceArithmetic() as Tableau<E>

private fun <E:Any> Expr<E>.affineName(depth : Int = 2) : String? {
    if (depth == 0) return null
    return when(this) {
        is ConstantScalar -> null
        is NamedScalar -> name
        is MatrixVariableElement -> matrix.name
        is UnaryScalar -> value.affineName(depth - 1) ?: op.op
        is BinaryScalar -> left.affineName(depth - 1) ?: right.affineName(depth - 1) ?: op.op
        is NamedScalarVariable -> name
        else ->
            error("$javaClass")
    }
}

// Render
fun binaryRequiresParents(parent : BinaryOp, child : BinaryOp, childIsRight: Boolean) : Boolean {
    fun bit(check : Boolean, shift : Int) : Int = if (check) (0x1 shl shift) else 0
    val sig =
        bit(parent.precedence < child.precedence, 1) +
        bit(child.precedence < parent.precedence, 2) +
        bit(parent == MINUS, 3) +
        bit(child == PLUS, 4) +
        bit(child.associative, 5) +
        bit(parent.associative, 6) +
        bit(childIsRight, 7)
    //println(sig)
    return when(sig) {
        4,8,44,60,68,96,100,112,132,172,178,196,224,228,240 -> false
        else -> true
    }
}

private fun requiresParens(parent : UntypedExpr, child : UntypedExpr, childIsRight : Boolean) : Boolean {
    fun UntypedExpr.binop() : BinaryOp? = when(this) {
            is BinaryMatrixScalar<*> -> op
            is BinaryScalarMatrix<*> -> op
            is BinaryScalar<*> -> op
            is BinaryMatrix<*> -> op
            else -> null
        }
    val parentBinOp = parent.binop()
    val childBinOp = child.binop()
    if (parentBinOp != null && childBinOp != null) {
        return binaryRequiresParents(parentBinOp, childBinOp, childIsRight)
    }
    return false
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
            'A' -> 'ᴬ'
            'B' -> 'ᴮ'
            'D' -> 'ᴰ'
            'E' -> 'ᴱ'
            'G' -> 'ᴳ'
            'H' -> 'ᴴ'
            'I' -> 'ᴵ'
            'J' -> 'ᴶ'
            'K' -> 'ᴷ'
            'L' -> 'ᴸ'
            'M' -> 'ᴹ'
            'N' -> 'ᴺ'
            'O' -> 'ᴼ'
            'P' -> 'ᴾ'
            'R' -> 'ᴿ'
            'T' -> 'ᵀ'
            'U' -> 'ᵁ'
            'W' -> 'ᵂ'
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

fun UntypedExpr.render(entryPoint : Boolean = true) : String {
    val parent = this
    fun UntypedExpr.self(childIsRight : Boolean = false) =
        when {
            requiresParens(parent, this, childIsRight) -> "(${render(false)})"
            else -> render(false)
        }
    fun binary(op : BinaryOp, left : UntypedExpr, right : UntypedExpr) : String =
        when  {
            right is NamedScalar<*> -> binary(op, left, right.scalar)
            op.infix -> "${left.self()}${op.op}${right.self(true)}"
            else -> "${op.op}(${left.self()},${right.self()})"
        }
    return when(this) {
        is Slot<*> -> "\$$slot"
        is NamedScalarVariable<*> -> name
        is ScalarVariable<*> -> "?"
        is BinaryMatrix<*> -> binary(op, left, right)
        is NamedScalarAssign<*> -> "${left.self()} <- ${right.self()}"
        is NamedMatrixAssign<*> -> "${left.self()} <- ${right.self()}"
        is BinaryScalar<*> -> {
            when {
                op == POW -> {
                    val rightSuper = tryConvertToSuperscript(right.self())
                    if (rightSuper == null) binary(POW, left, right)
                    else "${left.self()}$rightSuper"

                }
                else -> binary(op, left, right)
            }
        }
        is BinaryMatrixScalar<*> -> binary(op, left, right)
        is BinaryScalarMatrix<*> -> binary(op, left, right)
        is NamedScalar<*> -> {
            when {
                !entryPoint -> scalar.self()
                else -> "$name=" + scalar.self()
            }
        }
        is NamedMatrix<*> -> {
            when {
                //matrix is MatrixVariable -> name
                matrix is DataMatrix && !entryPoint -> name
                !entryPoint -> matrix.self()
                else ->
                    when {
                        matrix is DataMatrix && rows != 1 -> "\n$name\n------\n${matrix.self()}"
                        else -> "$name=${matrix.self()}"
                    }
            }
        }
        is MatrixVariableElement<*> -> "${matrix.name}[$column,$row]"
        is ConstantScalar<*> -> type.render(value)
        is UnaryScalar<*> -> when {
            op == EXP -> {
                val rightSuper = tryConvertToSuperscript(value.self())
                if (rightSuper == null) "${op.op}(${value.self()})"
                else "e$rightSuper"
            }
            op == D && value is NamedScalarVariable -> "${op.op}${value.self()}"
            op == NEGATE &&
                (value is NamedScalarVariable ||
                value is BinaryScalar && value.op == TIMES) -> "${op.op}${value.self()}"
            else -> "${op.op}(${value.self()})"
        }
        is UnaryMatrix<*> -> "${op.op}(${value.self()})"
        is UnaryMatrixScalar<*> -> "${op.op}(${value.self()})"
        is ConvertScalar<*,*> -> value.self()
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

// Render Structure
fun UntypedExpr.renderStructure(entryPoint : Boolean = true) : String {
    val parent = this
    fun UntypedExpr.self(childIsRight : Boolean = false) =
        when {
            requiresParens(parent, this, childIsRight) -> "(${renderStructure(false)})"
            else -> renderStructure(false)
        }
    fun binary(op : BinaryOp, left : UntypedExpr, right : UntypedExpr) =
        when  {
            op.infix -> "${left.self()}${op.op}${right.self(true)}"
            else -> "${op.op}(${left.self()},${right.self()})"
        }
    if (entryPoint) {
        return when(this) {
            is NamedScalar<*> -> "val $name by ${scalar.self()}"
            is NamedMatrix<*> -> "val $name by ${matrix.self()}"
            else -> "val expr = ${self()}"
        }
    }
    return when(this) {
        is NamedScalarVariable<*> -> "NamedScalarVariable(\"$name\")"
        is NamedMatrixVariable<*> -> "NamedMatrixVariable(\"$name\",$columns,$rows)"
        is ConstantScalar<*> -> "$value"
        is ScalarVariable<*> -> "variable<${type.simpleName}>()"
        is MatrixVariable<*> -> "matrixVariable<${type.simpleName}>($columns,$rows)"
        is BinaryMatrix<*> -> binary(op, left, right)
        is BinaryScalar<*> -> binary(op, left, right)
        is BinaryMatrixScalar<*> -> binary(op, left, right)
        is BinaryScalarMatrix<*> -> binary(op, left, right)
        is UnaryMatrixScalar<*> ->
            when(op) {
                SUMMATION -> "summation(${value.self()})"
                else -> "${op.op}(${value.self()})"
            }
        is UnaryScalar<*> -> "${op.op}(${value.self()})"
        is UnaryMatrix<*> -> "${op.op}(${value.self()})"
        is MatrixVariableElement<*> -> "$this"
        is NamedScalar<*> -> "NamedScalar(\"$name\", ${scalar.self()})"
        is NamedMatrix<*> -> matrix.self()
        is DataMatrix<*> -> {
            val sb = StringBuilder()
            sb.append("matrixOf($columns,$rows,${elements.joinToString(",") { it.self() }})")
            "$sb"
        }
        is Tableau<*> -> "Tableau(...)"
        else -> error("$javaClass")
    }
}
