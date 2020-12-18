@file:Suppress("UNCHECKED_CAST")

package com.github.jomof.kane.rigueur

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

interface UntypedExpr
interface Expr<T:Any> : UntypedExpr {
    val type : KClass<T>
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
val RAMP by UnaryOp() // From -1,-1 to 1,1
val STEP by UnaryOp() // Derivative of RAMP 1 from -1 to 1 and zero elsewhere

data class BinaryOp(
    val op : String = "",
    val infixOrder : Int? = null,
    val associative : Boolean = false
) {
    operator fun getValue(nothing: Nothing?, property: KProperty<*>) =
        if (op.isBlank()) copy(op = property.name.toLowerCase())
        else this
}
val POW by BinaryOp()
val TIMES by BinaryOp(op = "*", infixOrder = 0, associative = true)
val DIV by BinaryOp(op = "/", infixOrder = 1)
val PLUS by BinaryOp(op = "+", infixOrder = 2, associative = true)
val MINUS by BinaryOp(op = "-", infixOrder = 3)
val STACK by BinaryOp(op = " stack ", infixOrder = 4, associative = true)


operator fun <T:Any> ScalarExpr<T>.getValue(thisRef: Any?, property: KProperty<*>) = NamedScalar(property.name, this)
operator fun <T:Any> MatrixExpr<T>.getValue(thisRef: Any?, property: KProperty<*>) = NamedMatrix(property.name, this)

data class ConvertScalar<E: Any, T:Any>(val value : ScalarExpr<T>, override val type : KClass<E>) : ScalarExpr<E> {
    init { track() }
    override fun toString() = render()
}
data class ConstantScalar<E: Any>(val value : E) : ScalarExpr<E> {
    init { track() }
    override val type = value.javaClass.kotlin
    override fun toString() = render()
}
data class UnaryScalar<E:Any>(override val op : UnaryOp, override val value : ScalarExpr<E>) : ScalarExpr<E>, UnaryExpr<E>, ParentExpr<E> {
    init { track() }
    override val type = value.type
    override val children = listOf(value)
    override fun toString() = render()
}
data class UnaryMatrixScalar<E:Any>(override val op : UnaryOp, override val value : MatrixExpr<E>) : ScalarExpr<E>, UnaryExpr<E>, ParentExpr<E> {
    init { track() }
    override val type = value.type
    override val children : Iterable<ScalarExpr<E>> = value.elements.asIterable()
    override fun toString() = render()
}
data class UnaryMatrix<E:Any>(override val op : UnaryOp, override val value : MatrixExpr<E>) : MatrixExpr<E>, UnaryExpr<E> {
    init { track() }
    override val columns = value.columns
    override val rows = value.rows
    override val type = value.type
    override fun get(column: Int, row: Int) = UnaryScalar(op, value[column,row])
    override fun toString() = render()

}
data class BinaryScalar<E:Any>(
    override val op : BinaryOp,
    override val left : ScalarExpr<E>,
    override val right : ScalarExpr<E>) : ScalarExpr<E>, BinaryExpr<E>, ParentExpr<E> {
    init { track() }
    override val type = left.type
    override val children = listOf(left, right)
    override fun toString() = render()
}
data class BinaryScalarMatrix<E:Any>(
    override val op : BinaryOp,
    override val columns: Int,
    override val rows: Int,
    override val left : ScalarExpr<E>,
    override val right : MatrixExpr<E>) : MatrixExpr<E>, BinaryExpr<E>  {
    init { track() }
    override val type = left.type
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
    override val type = left.type
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
    init { track() }
    override val type = left.type
    override fun get(column: Int, row: Int) = when(op) {
        STACK -> {
            if (row < left.rows) left[column, row]
            else right[column, row - left.rows]
        }
        TIMES -> {
            assert(left.columns == right.rows)
            (1 until left.columns)
                .fold(left[0, row] * right[column, 0]) { prior : ScalarExpr<E>, i ->
                    prior + left[i, row] * right[column, i]
                }
        }
        else -> BinaryScalar(op, left[column,row], right[column,row])
    }
    override fun toString() = render()
}
data class ScalarVariable<E:Any>(override val type: KClass<E>) : ScalarVariableExpr<E> {
    init { track() }
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = NamedScalarVariable(property.name, type)
    override fun toString() = render()
}
data class NamedScalarVariable<E:Any>(
    override val name : String,
    override val type: KClass<E>) : ScalarVariableExpr<E>, NamedExpr<E> {
    init { track() }
    override fun toString() = render()
}
data class MatrixVariable<E:Any>(
    override val columns : Int,
    override val rows : Int,
    override val type : KClass<E>) : MatrixVariableExpr<E> {
    init { track() }
    override fun get(column: Int, row: Int) = MatrixVariableElement(column, row, this)
    override fun toString() = render()
}
data class NamedScalar<E:Any>(
    override val name : String,
    val scalar : ScalarExpr<E>) : ScalarExpr<E>, NamedExpr<E>, ParentExpr<E> {
    init {
        assert(scalar !is ScalarVariable) {
            "Should use NamedScalarVariable instead"
        }
        track()
    }
    override val type = scalar.type
    override val children = listOf(scalar)
    override fun toString() = render()
}
data class NamedMatrix<E:Any>(
    override val name : String,
    val matrix : MatrixExpr<E>) : MatrixExpr<E>, NamedExpr<E> {
    init { track() }
    override val columns = matrix.columns
    override val rows = matrix.rows
    override val type = matrix.type
    override fun get(column: Int, row: Int) = when(matrix) {
        is MatrixVariable -> NamedMatrixVariableElement(name, column, row, type)
        else -> matrix[column,row]
    }
    override fun toString() = render()
}
data class MatrixVariableElement<E:Any>(val column : Int, val row : Int, val matrix : MatrixVariable<E>) : ScalarExpr<E> {
    init { track() }
    override val type = matrix.type
    override fun toString() = render()
}
data class NamedMatrixVariableElement<E:Any>(
    val name : String,
    val column : Int,
    val row : Int,
    override val type : KClass<E>) : ScalarExpr<E> {
    init { track() }
    override fun toString() = render()
}
data class DataMatrix<E:Any>(
    override val columns: Int,
    override val rows: Int,
    val elements: List<ScalarExpr<E>>,
    override val type: KClass<E> = elements[0].type
) : MatrixExpr<E> {
    init {
        track()
        assert(columns * rows == elements.size) {
            "found ${elements.size} elements but needed ${rows * columns} for $columns by $rows matrix"
        }
    }
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
    override val type: KClass<E>
) : ParentExpr<E> {
    init { track() }
    override fun toString() = render()
}

data class Slot<E:Any>(
    val slot : Int,
    val replaces : ScalarExpr<E>
) : ScalarExpr<E> {
    init { track() }
    override val type = replaces.type
    override fun toString() = render()
}

// Pow
fun <E:Any, L : ScalarExpr<E>, R : E> pow(left : L, right : R) = BinaryScalar(POW, left, ConstantScalar(right))
fun <E:Any, L : MatrixExpr<E>, R : E> pow(left : L, right : R) = BinaryMatrixScalar(POW, left.columns, left.rows, left, ConstantScalar(right))
fun <E:Any, L : ScalarExpr<E>, R : ScalarExpr<E>> pow(left : L, right : R) : ScalarExpr<E> = BinaryScalar(POW, left, right)
fun <E:Any, L : MatrixExpr<E>, R : ScalarExpr<E>> pow(left : L, right : R) = BinaryMatrixScalar(POW, left.columns, left.rows, left, right)
// Logit
fun <E:Any, T : E> logit(value : T) : ScalarExpr<E> = UnaryScalar(LOGIT, ConstantScalar(value))
fun <E:Any, T : ScalarExpr<E>> logit(expr : T) : ScalarExpr<E> = UnaryScalar(LOGIT, expr)
fun <E:Any, T : MatrixExpr<E>> logit(expr : T) : MatrixExpr<E> = UnaryMatrix(LOGIT, expr)
// Ramp
fun <E:Any, T : E> ramp(value : T) : ScalarExpr<E> = UnaryScalar(RAMP, ConstantScalar(value))
fun <E:Any, T : ScalarExpr<E>> ramp(expr : T) : ScalarExpr<E> = UnaryScalar(RAMP, expr)
fun <E:Any, T : MatrixExpr<E>> ramp(expr : T) : MatrixExpr<E> = UnaryMatrix(RAMP, expr)
// Step
fun <E:Any, T : E> step(value : T) : ScalarExpr<E> = UnaryScalar(STEP, ConstantScalar(value))
fun <E:Any, T : ScalarExpr<E>> step(expr : T) : ScalarExpr<E> = UnaryScalar(STEP, expr)
fun <E:Any, T : MatrixExpr<E>> step(expr : T) : MatrixExpr<E> = UnaryMatrix(STEP, expr)
// Times
operator fun <E:Any, L : ScalarExpr<E>, R : E> L.times(right : R) = BinaryScalar(TIMES, this, ConstantScalar(right))
operator fun <E:Any, L : ScalarExpr<E>, R : ScalarExpr<E>> L.times(right : R) = BinaryScalar(TIMES, this, right)
operator fun Double.times(right : ScalarExpr<Double>) = BinaryScalar(TIMES, ConstantScalar(this), right)
operator fun <E:Any, L : MatrixExpr<E>, R : E> L.times(right : R) = BinaryMatrixScalar(TIMES, columns, rows, this, ConstantScalar(right))
operator fun <E:Any, L : E, R : MatrixExpr<E>> L.times(right : R) = BinaryScalarMatrix(TIMES, right.columns, right.rows, ConstantScalar(this), right)
operator fun <E:Any, L : MatrixExpr<E>, R : ScalarExpr<E>> L.times(right : R) = BinaryMatrixScalar(TIMES, columns, rows, this, right)
operator fun <E:Any, L : ScalarExpr<E>, R : MatrixExpr<E>> L.times(right : R) = BinaryScalarMatrix(TIMES, right.columns, right.rows, this, right)
operator fun <E:Any, L : MatrixExpr<E>, R : MatrixExpr<E>> L.times(right : R) = run {
    assert(columns == right.rows)
    BinaryMatrix(TIMES, right.columns, rows, this, right)
}
// Div
operator fun <E:Any, L : ScalarExpr<E>, R : E> L.div(right : R) = BinaryScalar(DIV, this, ConstantScalar(right))
operator fun <E:Any, L : ScalarExpr<E>, R : ScalarExpr<E>> L.div(right : R) = BinaryScalar(DIV, this, right)
operator fun Double.div(right : ScalarExpr<Double>) = BinaryScalar(DIV, ConstantScalar(this), right)
operator fun <E:Any, L : MatrixExpr<E>, R : E> L.div(right : R) = BinaryMatrixScalar(DIV, columns, rows, this, ConstantScalar(right))
operator fun <E:Any, L : E, R : MatrixExpr<E>> L.div(right : R) = BinaryScalarMatrix(DIV, right.columns, right.rows, ConstantScalar(this), right)
operator fun <E:Any, L : MatrixExpr<E>, R : ScalarExpr<E>> L.div(right : R) = BinaryMatrixScalar(DIV, columns, rows, this, right)
operator fun <E:Any, L : ScalarExpr<E>, R : MatrixExpr<E>> L.div(right : R) = BinaryScalarMatrix(DIV, right.columns, right.rows, this, right)
// Plus
operator fun <E:Any, L : ScalarExpr<E>, R : E> L.plus(right : R) = BinaryScalar(PLUS, this, ConstantScalar(right))
operator fun <E:Any, L : ScalarExpr<E>, R : ScalarExpr<E>> L.plus(right : R) : ScalarExpr<E> = BinaryScalar(PLUS, this, right)
operator fun Double.plus(right : ScalarExpr<Double>) = BinaryScalar(PLUS, ConstantScalar(this), right)
operator fun <E:Any, L : MatrixExpr<E>, R : E> L.plus(right : R) = BinaryMatrixScalar(PLUS, columns, rows, this, ConstantScalar(right))
operator fun <E:Any, L : E, R : MatrixExpr<E>> L.plus(right : R) = BinaryScalarMatrix(PLUS, right.columns, right.rows, ConstantScalar(this), right)
operator fun <E:Any, L : MatrixExpr<E>, R : ScalarExpr<E>> L.plus(right : R) = BinaryMatrixScalar(PLUS, columns, rows, this, right)
operator fun <E:Any, L : ScalarExpr<E>, R : MatrixExpr<E>> L.plus(right : R) = BinaryScalarMatrix(PLUS, right.columns, right.rows, this, right)
operator fun <E:Any, L : MatrixExpr<E>, R : MatrixExpr<E>> L.plus(right : R) = BinaryMatrix(PLUS, right.columns, right.rows, this, right)
// Minus
operator fun <E:Any, L : ScalarExpr<E>, R : E> L.minus(right : R) = BinaryScalar(MINUS, this, ConstantScalar(right))
operator fun <E:Any, L : ScalarExpr<E>, R : ScalarExpr<E>> L.minus(right : R) = BinaryScalar(MINUS, this, right)
operator fun Double.minus(right : ScalarExpr<Double>) = BinaryScalar(MINUS, ConstantScalar(this), right)
operator fun <E:Any, L : MatrixExpr<E>, R : E> L.minus(right : R) = BinaryMatrixScalar(MINUS, columns, rows, this, ConstantScalar(right))
operator fun <E:Any, L : E, R : MatrixExpr<E>> L.minus(right : R) = BinaryScalarMatrix(MINUS, right.columns, right.rows, ConstantScalar(this), right)
operator fun <E:Any, L : MatrixExpr<E>, R : ScalarExpr<E>> L.minus(right : R) = BinaryMatrixScalar(MINUS, columns, rows, this, right)
operator fun <E:Any, L : ScalarExpr<E>, R : MatrixExpr<E>> L.minus(right : R) = BinaryScalarMatrix(MINUS, right.columns, right.rows, this, right)
operator fun <E:Any, L : MatrixExpr<E>, R : MatrixExpr<E>> L.minus(right : R) = BinaryMatrix(MINUS, right.columns, right.rows, this, right)
// Stack
private fun <E:Any> stackMatrix(left : MatrixExpr<E>, right : MatrixExpr<E>) : MatrixExpr<E> {
    assert(left.columns == right.columns)
    return BinaryMatrix(STACK, left.columns, left.rows + right.rows, left, right)
}
infix fun <E:Any> MatrixExpr<E>.stack(right : E) : MatrixExpr<E> = stackMatrix(this, repeated(columns, 1, right))
infix fun <E:Any> E.stack(right : MatrixExpr<E>) : MatrixExpr<E> = stackMatrix(repeated(right.columns, 1, this), right)
infix fun <E:Any> MatrixExpr<E>.stack(right : ScalarExpr<E>) = stackMatrix(this, repeated(columns, 1, right))
infix fun <E:Any> ScalarExpr<E>.stack(right : MatrixExpr<E>) =stackMatrix(repeated(right.columns, 1, this), right)
infix fun <E:Any> MatrixExpr<E>.stack(right : MatrixExpr<E>) = stackMatrix(this, right)
// Summation
fun <E:Any> summation(expr : MatrixExpr<E>) : ScalarExpr<E> = UnaryMatrixScalar(SUMMATION, expr)
// Assign
inline fun <reified E:Any> assign(assignment : Pair<ScalarExpr<E>, ScalarVariable<E>>) = 0
// Variables
inline fun <reified E:Any> matrixVariable(columns : Int, rows : Int) : MatrixExpr<E> = MatrixVariable(columns, rows, E::class)
inline fun <reified E:Any> variable() = ScalarVariable(E::class)
// Tableau
fun <E:Any> tableauOf(vararg elements : NamedExpr<E>) : Tableau<E> = Tableau(elements.toList(), elements[0].type)
fun <E:Any> tableauOf(elements : List<NamedExpr<E>>) : Tableau<E> = Tableau(elements, elements[0].type)
fun <E:Any> Tableau<E>.map(type : KClass<E>, action : (NamedExpr<E>) -> NamedExpr<E>) : Tableau<E> = Tableau(children.map(action), type)
fun <E:Any> Tableau<E>.map(action : (NamedExpr<E>) -> NamedExpr<E>) : Tableau<E> = map(type, action)
// Misc
fun <E:Any> constant(value : E) : ScalarExpr<E> = ConstantScalar(value)
fun <E:Any> constant(value : Double, type : KClass<E>) : ScalarExpr<E> = when {
    value.javaClass.kotlin == type -> ConstantScalar(value) as ScalarExpr<E>
    else -> ConvertScalar(ConstantScalar(value), type)
}
fun <E:Any> repeated(columns : Int, rows : Int, value : E) : MatrixExpr<E> =
    repeated(columns, rows, ConstantScalar(value))
fun <E:Any> repeated(columns : Int, rows : Int, value : ScalarExpr<E>) : MatrixExpr<E> =
    DataMatrix(columns, rows, (0 until columns * rows).map { value }, value.type)

// Coordinates
data class Coordinate(val column : Int, val row : Int)
fun coordinatesOf(columns: Int, rows: Int) = sequence {
    for(row in 0 until rows) {
        for(column in 0 until columns) {
            yield(Coordinate(column,row))
        }
    }
}
val <E:Any> MatrixExpr<E>.coordinates get() = coordinatesOf(columns, rows)
val <E:Any> MatrixExpr<E>.elements get() = coordinates.map { coord -> get(coord.column, coord.row) }
fun <E:Any,R:Any> MatrixExpr<E>.foldCoordinates(initial: ScalarExpr<R>?, action : (ScalarExpr<R>?, Coordinate)->ScalarExpr<R>) =
    coordinates.fold(initial) { prior, coord -> action(prior, coord) }

// Differentiation
fun <E:Any, T : ScalarExpr<E>> d(expr : T) : ScalarExpr<E> = UnaryScalar(D, expr)
fun <E:Any, T : MatrixExpr<E>> d(expr : T) : MatrixExpr<E> = UnaryMatrix(D, expr)

private fun <E:Any> diff(expr : ScalarExpr<E>, variable : ScalarExpr<E>) : ScalarExpr<E> {
    if (variable is NamedScalar && variable.scalar !is ScalarVariable) return diff(expr, variable.scalar)
    fun ScalarExpr<E>.self() = diff(this, variable)
    val result : ScalarExpr<E> = when (expr) {
        variable -> constant(1.0, expr.type)
        is MatrixVariableElement -> expr
        is UnaryScalar -> when(expr.op) {
            LOGIT -> logit(expr.value) * (constant(1.0, expr.type) - logit(expr.value)) * expr.value.self()
            RAMP -> step(expr.value) * expr.value.self()
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
            val result = when(expr.op) {
                PLUS -> diffLeft + diffRight
                TIMES -> expr.left * diffRight + diffLeft * expr.right
                MINUS -> diffLeft - diffRight
                POW -> expr.right * pow(expr.left, expr.right - constant(1.0, expr.type)) * diffLeft
                else -> error("${expr.op}")
            }
            result
        }
        is NamedMatrixVariableElement -> constant(0.0, expr.type)
        is ScalarVariableExpr -> constant(0.0, expr.type)
        is ConstantScalar -> constant(0.0, expr.type)
        else -> error("${expr.javaClass}")
    }
    return result
}

fun <E:Any> differentiate(expr : Expr<E>) : Expr<E> {
    fun ScalarExpr<E>.self() = differentiate(this as Expr<E>) as ScalarExpr<E>
    fun MatrixExpr<E>.self() = differentiate(this as Expr<E>) as MatrixExpr<E>
    fun NamedExpr<E>.self() = differentiate(this as Expr<E>) as NamedExpr<E>
    fun ScalarExpr<E>.diffOr() : ScalarExpr<E>? {
        if (this !is BinaryScalar<E>) return null
        if (op != DIV) return null
        if (left !is UnaryScalar) return null
        if (left.op != D) return null
        if (right !is UnaryScalar) return null
        if (right.op != D) return null
        return diff(instantiateVariables(left.value), right.value)
    }
    fun MatrixExpr<E>.diffOr() : MatrixExpr<E>? {
        if (this !is BinaryScalarMatrix<E>) return null
        if (op != DIV) return null
        if (left !is UnaryScalar) return null
        if (left.op != D) return null
        if (right !is UnaryMatrix) return null
        if (right.op != D) return null
        return matrixOf(right.columns, right.rows) { (column, row) ->
            diff(instantiateVariables(left.value), instantiateVariables(right.value[column, row]))
        }
    }
    return when(expr) {
        is NamedMatrixVariableElement -> expr
        is NamedScalar -> {
            val diff = expr.scalar.diffOr()
            if (diff != null) expr.copy(name = "${expr.name}'", scalar = diff)
            else expr.copy(scalar = expr.scalar.self())
        }
        is NamedMatrix -> {
            val diff = expr.matrix.diffOr()
            if (diff != null) expr.copy(name = "${expr.name}'", matrix = diff)
            else expr.copy(matrix = expr.matrix.self())
        }
        is UnaryScalar<E> -> {
            assert(expr.op != D)
            expr.copy(value = expr.value.self())
        }
        is UnaryMatrix<E> -> {
            assert(expr.op != D) {
                "differentiation should have been handled before this"
            }
            expr.copy(value = expr.value.self())
        }
        is UnaryMatrixScalar<E> -> {
            assert(expr.op != D) {
                "differentiation should have been handled before this"
            }
            expr.copy(value = expr.value.self())
        }
        is ConstantScalar -> expr
        is VariableExpr -> expr
        is MatrixVariableElement-> expr
        is DataMatrix -> expr.copy(elements = expr.elements.map { it.self() })
        is BinaryMatrixScalar<E> -> expr.copy(left = expr.left.self(), right = expr.right.self())
        is BinaryScalarMatrix<E> -> expr.diffOr() ?: expr.copy(left = expr.left.self(), right = expr.right.self())
        is BinaryMatrix<E> -> expr.copy(left = expr.left.self(), right = expr.right.self())
        is BinaryScalar<E> -> expr.diffOr() ?: expr.copy(left = expr.left.self(), right = expr.right.self())
        is Tableau<E> -> expr.copy(children = expr.children.map { it.self() })
        else -> error("${expr.javaClass}")
    }
}

fun <E:Any> differentiate(expr : ScalarExpr<E>) : ScalarExpr<E> = (differentiate(expr as Expr<E>) as ScalarExpr<E>).reduceArithmetic()
fun <E:Any> differentiate(expr : MatrixExpr<E>) : MatrixExpr<E> = (differentiate(expr as Expr<E>) as MatrixExpr<E>).reduceArithmetic()
fun <E:Any> differentiate(expr : Tableau<E>) : Tableau<E> = (differentiate(expr as Expr<E>) as Tableau<E>).reduceArithmetic()

// Instantiate variable
private fun <E:Any> instantiateVariables(expr : MatrixExpr<E>, column : Int, row : Int) : ScalarExpr<E> {
    fun MatrixExpr<E>.self(column : Int, row : Int) : ScalarExpr<E> = instantiateVariables(this, column, row)
    fun MatrixExpr<E>.self() : ScalarExpr<E> = self(column, row)
    return when(expr) {
        is NamedMatrix -> when(expr.matrix) {
            is MatrixVariable -> NamedMatrixVariableElement(expr.name, column, row, expr.type)
            else -> expr.matrix.self()
        }
        is MatrixVariable -> MatrixVariableElement(column,row,expr)
        else -> expr[column,row]
    }
}

fun <E:Any> instantiateVariables(expr : Expr<E>, entryPoint : Boolean = true) : Expr<E> {
    fun ScalarExpr<E>.self() : ScalarExpr<E> = instantiateVariables(this as Expr<E>, false) as ScalarExpr<E>
    fun MatrixExpr<E>.self() : MatrixExpr<E> = instantiateVariables(this as Expr<E>, false) as MatrixExpr<E>
    fun NamedExpr<E>.self() : NamedExpr<E> = instantiateVariables(this as Expr<E>, false) as NamedExpr<E>
    val result : Expr<E> = when(expr) {
        is VariableExpr -> expr
        is NamedMatrixVariableElement -> expr
        is ConstantScalar -> expr
        is UnaryScalar -> expr.copy(value = expr.value.self())
        is UnaryMatrix -> expr.copy(value = expr.value.self())
        is BinaryScalar -> expr.copy(left = expr.left.self(), right = expr.right.self())
        is BinaryMatrix -> expr.copy(left = expr.left.self(), right = expr.right.self())
        is BinaryScalarMatrix -> expr.copy(left = expr.left.self(), right = expr.right.self())
        is BinaryMatrixScalar -> expr.copy(left = expr.left.self(), right = expr.right.self())
        is NamedScalar -> {
            val scalar = expr.scalar.self()
            when {
                entryPoint || scalar is ScalarVariable -> NamedScalar(expr.name, scalar)
                else -> scalar
            }
        }
        is NamedMatrix -> expr.copy(matrix = expr.matrix.self())
        is MatrixVariableElement ->
            expr.matrix[expr.column, expr.row]
        is UnaryMatrixScalar -> when(expr.op) {
            SUMMATION -> {
                expr.value.foldCoordinates(null) { prior, (column, row) ->
                    val current = instantiateVariables(expr.value, column, row)
                    if (prior == null) current
                    else prior + current
                }!!
            }
            else -> error("${expr.op}")
        }
        is Tableau -> expr.copy(children = expr.children.map { it.self() })
        is DataMatrix -> expr.map { it.self() }
        else ->
            error("${expr.javaClass}")
    }
    return result
}

fun <E:Any> instantiateVariables(expr : ScalarExpr<E>) = instantiateVariables(expr as Expr<E>) as ScalarExpr<E>
fun <E:Any> instantiateVariables(expr : MatrixExpr<E>) = instantiateVariables(expr as Expr<E>) as MatrixExpr<E>
fun <E:Any> instantiateVariables(expr : Tableau<E>) = instantiateVariables(expr as Expr<E>) as Tableau<E>
//fun <E:Any> instantiateVariables(expr : Named<E>) = instantiateVariables(expr as Expr<E>) as Named<E>

// Data matrix
inline fun <reified E:Any> matrixOf(columns: Int, rows: Int, vararg elements : E) = DataMatrix(
    columns,
    rows,
    elements.map { ConstantScalar(it) }.toList(),
    E::class
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
    else -> DataMatrix(columns, rows, elements.toList(), type)
}
fun <E:Any> MatrixExpr<E>.map(action:(ScalarExpr<E>)->ScalarExpr<E>) = DataMatrix(columns, rows, elements.map(action).toList(), type)

// Replace subexpressions
fun <E:Any> Expr<E>.replace(find : Expr<E>, replace : Expr<E>, memo : MutableMap<Expr<E>,Expr<E>> = mutableMapOf()) : Expr<E> {
    if (this == find) return replace
    fun ScalarExpr<E>.self() = replace(find, replace, memo) as ScalarExpr<E>
    fun MatrixExpr<E>.self() = replace(find, replace, memo) as MatrixExpr<E>
    fun NamedExpr<E>.self() = replace(find, replace, memo) as NamedExpr<E>
    when(this) {
        is VariableExpr,
        is ConstantScalar,
        is MatrixVariableElement,
        is NamedMatrixVariableElement -> return this
    }
    if (memo.containsKey(this)) {
        val result = memo.getValue(this)
        return result
    }

    fun reduce(scalar : ScalarExpr<E>) : Expr<E> {
        if (scalar !is BinaryScalar) return scalar
        fun self() =
            scalar.copy(left = scalar.left.self(), right = scalar.right.self())
        if (find !is BinaryScalar) return self()
        if (replace !is ScalarExpr) return self()
        val associated = scalar.expandAssociative()
        if (associated.isEmpty()) return self()
        val leftIndex = associated.indices.firstOrNull { associated[it] == find.left } ?: return self()
        val rightIndex = associated.indices.lastOrNull { associated[it] == find.right } ?: return self()
        if (leftIndex == rightIndex) return self()
        val remainingTerminals = associated.indices
            .filter { it != leftIndex && it != rightIndex }
            .map { associated[it] }
        assert(remainingTerminals.size == associated.size - 2)
        val result = remainingTerminals.fold(replace) { prior, current ->  BinaryScalar(scalar.op, prior, current)}
        return reduce(result)
    }

    val result = when(this) {
        is BinaryScalar -> reduce(this)
        is BinaryMatrixScalar -> copy(left = left.self(), right = right.self())
        is BinaryScalarMatrix -> copy(left = left.self(), right = right.self())
        is UnaryScalar -> copy(value = value.self())
        is UnaryMatrixScalar -> copy(value = value.self())
        is NamedScalar -> copy(scalar = scalar.self())
        is Tableau -> Tableau(children.map { it.self() }, type)
        is NamedMatrix -> copy(matrix = matrix.self())
        is UnaryMatrix -> copy(value = value.self())
        is BinaryMatrix -> copy(left = left.self(), right = right.self())
        is DataMatrix -> map { it.self() }
        else -> error("$javaClass")
    }
    memo[this] = result
    return result
}

fun <E:Any> Tableau<E>.replace(find : Expr<E>, replace : Expr<E>) =
    (this as Expr<E>).replace(find, replace) as Tableau<E>

// Evaluate
fun <E:Any> Expr<E>.eval(map : MutableMap<String, E>) : Expr<E> {
    fun NamedExpr<E>.self() = eval(map) as NamedExpr<E>
    fun ScalarExpr<E>.self() = eval(map) as ScalarExpr<E>
    fun Expr<E>.unwrapConstant() : ConstantScalar<E> = when(this) {
        is ConstantScalar -> this
        is NamedScalar -> scalar.unwrapConstant()
        else -> error("$javaClass")
    }
    val result : Expr<E> = when(this) {
        is ConstantScalar -> this
        is NamedScalarVariable -> {
            val bound = map.getValue(name)
            if (bound is ScalarExpr<*>) bound as ScalarExpr<E>
            else ConstantScalar(bound)
        }
        is NamedScalar -> copy(scalar = scalar.self())
        is BinaryScalar -> copy(left = left.self(), right = right.self())
        is Tableau -> {
            copy(children = children.map { child ->
                val result = child.self().reduceArithmetic().unwrapConstant()

                if(result is ConstantScalar) {
                    map[child.name] = result.value
                } else {
                    assert(false) {
                        "$child -> $result did not reduce to constant"
                    }
                }
                NamedScalar(child.name, result)
            })
        }
        else -> error("$javaClass")
    }
    return result
}

fun <E:Any> Tableau<E>.eval(map : MutableMap<String, E>) = ((this as Expr<E>).eval(map) as Tableau).reduceArithmetic()

// Layout memory
data class MemoryLayout<E:Any>(private val slots : MutableMap<Expr<E>, Slot<E>> = mutableMapOf()) {
    fun slotOf(expr : ScalarExpr<E>) : Slot<E> = slots.computeIfAbsent(expr) { Slot(slots.size, expr) }
}

fun <E:Any> Expr<E>.replaceSlots(layout : MemoryLayout<E> = MemoryLayout()) : Expr<E> {
    fun Expr<E>.self() = replaceSlots(layout)
    fun ScalarExpr<E>.self() = replaceSlots(layout) as ScalarExpr<E>
    fun NamedExpr<E>.self() = replaceSlots(layout) as NamedExpr<E>
    return when (this) {
        is ConstantScalar -> this
        is BinaryScalar -> copy(left = left.self(), right = right.self())
        is NamedScalarVariable -> layout.slotOf(this)
        is NamedScalar -> copy(scalar = scalar.self())
        is Tableau -> copy(children = children.map { it.self() })
        else -> error("$javaClass")
    }
}

fun <E:Any> Expr<E>.slots(map : MutableMap<Expr<E>, Int> = mutableMapOf()) : Map<Expr<E>, Int> {
    fun Expr<E>.self() = slots(map)
    when(this) {
        is Slot -> map[replaces] = slot
        is ParentExpr -> children.forEach { it.self() }
    }
    return map
}

fun <E:Any> Expr<E>.maxSlot() : Slot<E>? {
    fun Expr<E>.self() = maxSlot()
    return when(this) {
        is Slot -> this
        is ParentExpr -> {
            children.drop(1).fold(children.first().self()) { prior, current ->
                val currentSlot = current.self()
                when {
                    prior == null -> currentSlot
                    currentSlot == null -> prior
                    currentSlot.slot > prior.slot -> currentSlot
                    else -> prior
                }
            }
        }
        else -> null
    }
}

// Extract subexpressions
private fun <E:Any> emptyExprTable() = mapOf<ScalarExpr<E>, Int>()

infix operator fun <E:Any> Map<ScalarExpr<E>,Int>.plus(right : ScalarExpr<E>) : Map<ScalarExpr<E>,Int> {
    val result = toMutableMap()
    result[right] = result.computeIfAbsent(right) { 0 } + 1
    return result
}

infix operator fun <E:Any> Map<ScalarExpr<E>,Int>.plus(right : Map.Entry<ScalarExpr<E>, Int>) : Map<ScalarExpr<E>,Int> {
    val result = toMutableMap()
    result[right.key] = result.computeIfAbsent(right.key) { 0 } + right.value
    return result
}

infix operator fun <E:Any> Map<ScalarExpr<E>,Int>.plus(right : Map<ScalarExpr<E>,Int>) : Map<ScalarExpr<E>,Int> {
    return right.entries.fold(this) { prior, entry -> prior + entry }
}

private fun <E:Any> BinaryScalar<E>.expandAssociative() : List<ScalarExpr<E>> {
    if (!op.associative) return listOf()
    val leftExpanded = if (left is BinaryScalar && left.op == op) left.expandAssociative() else listOf(left)
    val rightExpanded = if (right is BinaryScalar && right.op == op) right.expandAssociative() else listOf(right)
    return leftExpanded + rightExpanded
}

data class ExprMap<E:Any>(val data : MutableMap<ScalarExpr<E>, Int> = mutableMapOf()) {
    var count : Int = 0
    fun add(expr : ScalarExpr<E>) {
        ++count
        data[expr] = data.computeIfAbsent(expr) { 0 } + 1
    }
}

fun <E:Any> Expr<E>.countSubExpressions(memo : ExprMap<E> = ExprMap()) : Map<ScalarExpr<E>, Int> {
    //if (memo.count > 256 + 64 + 16 + 8) return memo.data
    fun Expr<E>.self() = countSubExpressions(memo)
    fun <E:Any> Expr<E>.terminal() : Boolean = when(this) {
        is VariableExpr -> true
        is NamedMatrixVariableElement -> true
        is ConstantScalar -> true
        is UnaryMatrixScalar -> false
        is NamedScalar -> scalar.terminal()
        is UnaryScalar -> value.terminal()
        is BinaryScalar -> false
        is BinaryScalarMatrix -> false
        is MatrixVariableElement -> {
            val result = matrix[column,row]
            if (result == this) true
            else result.terminal()
        }
        else -> error("$javaClass")
    }
    fun ScalarExpr<E>.add() = memo.add(this)
    when(this) {
        is NamedScalar -> scalar.self()
        is ConstantScalar,
        is VariableExpr,
        is MatrixVariableElement,
        is NamedMatrixVariableElement -> {}
        is NamedMatrix -> matrix.self()
        is Tableau -> children.forEach { it.self() }
        is UnaryScalar -> {
            value.self()
            if (terminal()) add()
        }
        is BinaryScalar -> {
            when {
                left.terminal() && right.terminal() -> add()
                op.associative -> {
                    val (terminal, non) = expandAssociative().partition { it.terminal() }
                    coordinatesOf(terminal.size, terminal.size).forEach { (column, row) ->
                        if (row < column) BinaryScalar(op, terminal[row], terminal[column]).add()
                    }
                    non.forEach { it.self() }
                }
                else -> {
                    left.self()
                    right.self()
                }
            }
        }
        is MatrixExpr -> elements.forEach { it.self() }
        is UnaryExpr -> {
            value.self()
            if (this is ScalarExpr && value.terminal()) add()
        }
        is BinaryExpr -> {
            left.self()
            right.self()
        }
        else -> error("$javaClass")
    }
    return memo.data
}

fun <E:Any> NamedExpr<E>.extractCommonSubExpressions() = tableauOf(this).extractCommonSubExpressions()
fun <E:Any> Tableau<E>.extractCommonSubExpressions(): Tableau<E> {
    val extracted = mutableListOf<NamedExpr<E>>()
    //val all = (accumulateNamed() - members) + members // Put members at the end
    var tab = map {
        when(it) {
            is NamedMatrix<E> -> it.copy(matrix = it.matrix.toDataMatrix().map { element -> instantiateVariables(element).reduceArithmetic() })
            is NamedScalar<E> -> it.copy(scalar = instantiateVariables(it).reduceArithmetic())
            else -> it
        }
    }
    while(true) {
        val sub = tab.countSubExpressions()
        if (sub.isEmpty()) break
        val maxCount = sub.maxOf { it.value }
        val max = sub.filter { it.value == maxCount }.map { it.key }.first()
        val replacement = NamedScalarVariable("c${extracted.size}", type)
        extracted.add(NamedScalar("c${extracted.size}", max))
        tab = tab.replace(max, replacement)
    }
    return tableauOf(extracted + tab.children)
}

// Allocation tracking
private const val trackingEnabled = false
private var trackingCount = 0
private val traceMap = mutableMapOf<List<StackTraceElement>, Int>()
private var offender : List<StackTraceElement> = listOf()
private fun trackImpl() {
    ++trackingCount
    if (trackingCount % 100 == 0) {
        val stack = try {
            error("")
        } catch (e: Exception) {
            e.stackTrace.toList()
                .drop(1)
                .filter { it.fileName?.contains("Rigueur")?:false }
                .filter { it.lineNumber > 1}
                .take(10)
        }
        traceMap[stack] = traceMap.computeIfAbsent(stack) { 0 } + 1
        if (offender == stack) {
            "breakpoint"
        }
        val count = traceMap[stack]!!
        if (traceMap[offender]?:0 < count) {
            offender = stack
        }

    }
}
private inline fun track() {
    if (trackingEnabled)  trackImpl()
}

// Reduce arithmetic
fun Expr<Double>.hasConstants() : Boolean {
    when(this) {
        is VariableExpr -> return false
        is ConstantScalar -> return true
        is MatrixVariableElement -> return false
        is NamedMatrixVariableElement -> return false
        is ParentExpr -> {
            for(child in children) {
                if (child.hasConstants()) return true
            }
            return false
        }
        else -> error("$javaClass")
    }
}

fun Expr<Double>.reduceArithmeticDouble(memo : MutableMap<Expr<Double>,Expr<Double>> = mutableMapOf()) : Expr<Double> {
    if (memo.contains(this))
        return memo.getValue(this)
    if (!hasConstants()) {
        memo[this] = this
        return this
    }
    fun ScalarExpr<Double>.self() = reduceArithmeticDouble(memo) as ScalarExpr<Double>
    fun MatrixExpr<Double>.self() = reduceArithmeticDouble(memo) as MatrixExpr<Double>
    fun NamedExpr<Double>.self() = reduceArithmeticDouble(memo) as NamedExpr<Double>
    fun ScalarExpr<Double>.findConstant() : Double? = when(this) {
        is ConstantScalar -> value
        is NamedScalar -> scalar.findConstant()
        else -> null
    }
    val result = when(this) {
        is ConstantScalar -> this
        is ScalarVariable -> this
        is MatrixVariableElement -> this
        is UnaryMatrixScalar -> copy(value = value.self())
        is NamedScalar -> {
            val result = scalar.self()
            if (result is ScalarVariable) NamedScalarVariable(name, result.type)
            else copy(scalar = scalar.self())
        }
        is NamedMatrix -> copy(matrix = matrix.self())
        is MatrixExpr -> matrixOf(columns, rows) { (column, row) -> this[column,row].self() }
        is BinaryScalar -> {
            if (op.associative) {
                val associates = expandAssociative()
                val (const, non) = associates.partition { it is ConstantScalar }
                when (op) {
                    PLUS -> {
                        fun accumulate(left: ScalarExpr<Double>, right: ScalarExpr<Double>): ScalarExpr<Double> {
                            val leftSelf by lazy { left.self() }
                            val rightSelf by lazy { right.self() }
                            val leftConst = left.findConstant()
                            if (leftConst == 0.0) return rightSelf
                            val rightConst = right.findConstant()
                            if (rightConst == 0.0) return leftSelf
                            if (leftConst != null && rightConst != null) return ConstantScalar(leftConst + rightConst)
                            val leftSelfConst = leftConst ?: leftSelf.findConstant()
                            if (leftSelfConst == 0.0) return rightSelf
                            val rightSelfConst = rightConst ?: rightSelf.findConstant()
                            if (rightSelfConst == 0.0) return leftSelf
                            if (leftSelfConst != null && rightSelfConst != null) return ConstantScalar(leftSelfConst + rightSelfConst)
                            if (leftSelfConst != null) return left + rightSelf
                            if (rightSelfConst != null) return leftSelf + right
                            return leftSelf + rightSelf
                        }
                        (const + non).fold(ConstantScalar(0.0) as ScalarExpr<Double>) { left, right ->
                            accumulate(left, right)
                        }
                    }
                    TIMES -> {
                        fun accumulate(left: ScalarExpr<Double>, right: ScalarExpr<Double>): ScalarExpr<Double> {
                            val leftSelf = left.self()
                            val rightSelf = right.self()
                            val leftConst = leftSelf.findConstant()
                            val rightConst = rightSelf.findConstant()
                            if (leftConst == 0.0) return leftSelf
                            if (leftConst == 1.0) return rightSelf
                            if (rightConst == 0.0) return rightSelf
                            if (rightConst == 1.0) return leftSelf
                            if (leftConst != null && rightConst != null) return ConstantScalar(leftConst * rightConst)
                            return leftSelf * rightSelf
                        }
                        (const + non).fold(ConstantScalar(1.0) as ScalarExpr<Double>) { left, right ->
                            accumulate(left, right)
                        }
                    }
                    else ->
                        error("$op")
                }

            } else {
                val left = left.self()
                val right = right.self()
                val leftConst = left.findConstant()
                val rightConst = right.findConstant()
                when {
                    leftConst != null && rightConst != null -> when (op) {
                        MINUS -> ConstantScalar(leftConst - rightConst)
                        DIV -> ConstantScalar(leftConst / rightConst)
                        POW -> ConstantScalar(Math.pow(leftConst, rightConst))
                        else -> error("$op")
                    }
                    rightConst == 0.0 -> when (op) {
                        MINUS -> left
                        DIV -> error("div by zero")
                        else -> error("$op")
                    }
                    leftConst == 0.0 -> when (op) {
                        MINUS -> -1.0 * right
                        DIV -> left
                        else -> error("$op")
                    }
                    rightConst == 1.0 -> when (op) {
                        MINUS -> left - right
                        DIV -> left
                        POW -> left
                        else -> error("$op")
                    }
                    leftConst == 1.0 -> when (op) {
                        MINUS -> left - right
                        DIV -> right
                        else -> error("$op")
                    }
                    else -> copy(left = left, right = right)
                }
            }
        }
        is UnaryScalar -> copy(value = value.self())
        is NamedMatrixVariableElement -> this
        is Tableau -> copy(children = children.map { it.self() })
        else -> error("$javaClass")
    }
    memo[this] = result
    return result
}

fun <E:Any> Expr<E>.reduceArithmetic() : Expr<E> {
    return when(type) {
        Double::class -> (this as Expr<Double>).reduceArithmeticDouble() as Expr<E>
        else -> this
    }
}

fun <E:Any> NamedExpr<E>.reduceArithmetic() : NamedExpr<E> {
    return when(type) {
        Double::class -> (this as Expr<Double>).reduceArithmeticDouble() as NamedExpr<E>
        else -> this
    }
}

fun <E:Any> ScalarExpr<E>.reduceArithmetic() : ScalarExpr<E> {
    return when(type) {
        Double::class -> (this as ScalarExpr<Double>).reduceArithmeticDouble() as ScalarExpr<E>
        else -> error("$javaClass")
    }
}

fun <E:Any> MatrixExpr<E>.reduceArithmetic() : MatrixExpr<E> {
    return when(type) {
        Double::class -> (this as MatrixExpr<Double>).reduceArithmeticDouble() as MatrixExpr<E>
        else -> error("$javaClass")
    }
}

fun <E:Any> Tableau<E>.reduceArithmetic() : Tableau<E> {
    return when(type) {
        Double::class -> (this as Tableau<Double>).reduceArithmeticDouble() as Tableau<E>
        else -> error("$javaClass")
    }
}

// Render
fun binaryRequiresParents(parent : BinaryOp, child : BinaryOp, childIsRight: Boolean) : Boolean? {
    val childInfix = child.infixOrder ?: return null
    val parentInfix = parent.infixOrder ?: return null
    return when {
        parent == MINUS && child == PLUS && childIsRight ->
            true
        parent == child && parent.associative ->
            false
        parent == child && !parent.associative ->
            childIsRight
        childInfix > parentInfix ->
            true
        childInfix < parentInfix ->
            false
        else ->
            true
    }
}

private fun requiresParens(parent : UntypedExpr, child : UntypedExpr, childIsRight : Boolean) : Boolean {
    fun UntypedExpr.binop() = when(this) {
            is BinaryMatrixScalar<*> -> op
            is BinaryScalarMatrix<*> -> op
            is BinaryScalar<*> -> op
            is BinaryMatrix<*> -> op
            else -> null
        }
    val parentBinOp = parent.binop()
    val childBinOp = child.binop()
    if (parentBinOp != null && childBinOp != null) {
        return binaryRequiresParents(parentBinOp, childBinOp, childIsRight) ?: false
    }
    return false
}

private fun renderScalarValue(value : Any) = run {
    when (value) {
        is Double -> {
            val result = BigDecimal(value).setScale(5, RoundingMode.HALF_EVEN).toString()
            if (result.contains(".")) result.trimEnd('0').trimEnd('.')
            else result
        }
        else -> "$value"
    }
}

fun UntypedExpr.render(entryPoint : Boolean = true) : String {
    val parent = this
    fun UntypedExpr.self(childIsRight : Boolean = false) =
        when {
            requiresParens(parent, this, childIsRight) -> "(${render(false)})"
            else -> render(false)
        }
    fun binary(op : BinaryOp, left : UntypedExpr, right : UntypedExpr) =
        when  {
            op.infixOrder != null -> "${left.self()}${op.op}${right.self(true)}"
            else -> "${op.op}(${left.self()},${right.self()})"
        }
    return when(this) {
        is Slot<*> -> "\$$slot"
        is NamedScalarVariable<*> -> name
        is ScalarVariable<*> -> "?"
        is BinaryMatrix<*> -> binary(op, left, right)
        is BinaryScalar<*> -> when {
            op == POW && right is ConstantScalar && right.value == 0.0 -> "${left.self()}⁰"
            op == POW && right is ConstantScalar && right.value == 1.0 -> "${left.self()}¹"
            op == POW && right is ConstantScalar && right.value == 2.0 -> "${left.self()}²"
            op == POW && right is ConstantScalar && right.value == 3.0 -> "${left.self()}³"
            op == POW && right is ConstantScalar && right.value == 4.0 -> "${left.self()}⁴"
            op == POW && right is ConstantScalar && right.value == 5.0 -> "${left.self()}⁵"
            op == POW && right is ConstantScalar && right.value == 6.0 -> "${left.self()}⁶"
            op == POW && right is ConstantScalar && right.value == 7.0 -> "${left.self()}⁷"
            op == POW && right is ConstantScalar && right.value == 8.0 -> "${left.self()}⁸"
            op == POW && right is ConstantScalar && right.value == 9.0 -> "${left.self()}⁹"
            else -> binary(op, left, right)
        }
        is BinaryMatrixScalar<*> -> binary(op, left, right)
        is BinaryScalarMatrix<*> -> binary(op, left, right)
        is NamedScalar<*> -> {
            when {
                scalar is ScalarVariable -> name
                !entryPoint -> scalar.self()
                else ->
                    "$name=" + scalar.self()
            }
        }
        is NamedMatrix<*> -> {
            when {
                matrix is MatrixVariable -> name
                matrix is DataMatrix && !entryPoint -> name
                !entryPoint -> matrix.self()
                else ->
                    when {
                        matrix is DataMatrix && rows != 1 -> "\n$name\n------\n${matrix.self()}"
                        else -> "$name=${matrix.self()}"
                    }
            }
        }
        is MatrixVariableElement<*> -> {
            when {
               // matrix is NamedMatrix -> "${matrix.name}[$column,$row]"
                matrix is MatrixVariable -> "[$column,$row]"
                else -> {
                    val element = matrix[column, row]
                    assert(element !is MatrixVariableElement) {
                        "recursion ${matrix.javaClass}"
                    }
                    element.self()
                }
            }
        }
        is ConstantScalar<*> -> renderScalarValue(value)
        is UnaryScalar<*> -> "${op.op}(${value.self()})"
        is UnaryMatrix<*> -> "${op.op}(${value.self()})"
        is UnaryMatrixScalar<*> -> "${op.op}(${value.self()})"
        is ConvertScalar<*,*> -> value.self()
        is MatrixVariable<*> -> "matrix(${columns}x${rows})"
        is NamedMatrixVariableElement<*> -> "$name[$column,$row]"
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
            if (rows == 1) {
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
            op.infixOrder != null -> "${left.self()}${op.op}${right.self(true)}"
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
        is UnaryMatrixScalar<*> -> "${op.op}(${value.self()})"
        is MatrixVariableElement<*> -> "$this"
        is NamedScalar<*> -> {
            when (scalar) {
                is ScalarVariable -> name
                else -> "NamedScalar(\"$name\", ${scalar.self()})"
            }
        }
        is NamedMatrix<*> -> {
            when (matrix) {
                is MatrixVariable -> name
                else -> matrix.self()
            }
        }
        is DataMatrix<*> -> {
            val sb = StringBuilder()
            sb.append("matrixOf($columns,$rows,${elements.joinToString(",") { it.self() }})")
            "$sb"
        }
        else ->
            error("$javaClass")
    }
}
