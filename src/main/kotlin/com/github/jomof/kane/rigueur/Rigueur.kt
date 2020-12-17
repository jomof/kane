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
interface Scalar<T:Any> : Expr<T>
interface Matrix<T:Any> : Expr<T> {
    val columns : Int
    val rows : Int
    operator fun get(column : Int, row : Int) : Scalar<T>
}
interface Named<T:Any> : Expr<T> {
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

operator fun <T:Any> Scalar<T>.getValue(thisRef: Any?, property: KProperty<*>) = NamedScalar(property.name, this)
operator fun <T:Any> Matrix<T>.getValue(thisRef: Any?, property: KProperty<*>) = NamedMatrix(property.name, this)

data class ConvertScalar<E: Any, T:Any>(val value : Scalar<T>, override val type : KClass<E>) : Scalar<E> {
    override fun toString() = render()
}
data class ConstantScalar<E: Any>(val value : E) : Scalar<E> {
    override val type = value.javaClass.kotlin
    override fun toString() = render()
}
data class UnaryScalar<E:Any>(override val op : UnaryOp, override val value : Scalar<E>) : Scalar<E>, UnaryExpr<E> {
    override val type = value.type
    override fun toString() = render()
}
data class UnaryMatrixScalar<E:Any>(override val op : UnaryOp, override val value : Matrix<E>) : Scalar<E>, UnaryExpr<E> {
    override val type = value.type
    override fun toString() = render()
}
data class UnaryMatrix<E:Any>(override val op : UnaryOp, override val value : Matrix<E>) : Matrix<E>, UnaryExpr<E> {
    override val columns = value.columns
    override val rows = value.rows
    override val type = value.type
    override fun get(column: Int, row: Int) = UnaryScalar(op, value[column,row])
    override fun toString() = render()

}
data class BinaryScalar<E:Any>(
    override val op : BinaryOp,
    override val left : Scalar<E>,
    override val right : Scalar<E>) : Scalar<E>, BinaryExpr<E> {
    override val type = left.type
    override fun toString() = render()
}
//data class FuncMatrix<E:Any>(
//    override val columns: Int,
//    override val rows: Int,
//    override val type: KClass<E>,
//    val render : String = "",
//    val func : (column:Int, row:Int) -> Scalar<E>) : Matrix<E> {
//    init {
//        func(0,0) // Fail early
//    }
//    override fun get(column: Int, row: Int) = func(column, row)
//    override fun toString() = render()
//}
data class BinaryScalarMatrix<E:Any>(
    override val op : BinaryOp,
    override val columns: Int,
    override val rows: Int,
    override val left : Scalar<E>,
    override val right : Matrix<E>) : Matrix<E>, BinaryExpr<E>  {
    override val type = left.type
    override fun get(column: Int, row: Int) = BinaryScalar(op, left, right[column, row])
    override fun toString() = render()
}
data class BinaryMatrixScalar<E:Any>(
    override val op : BinaryOp,
    override val columns: Int,
    override val rows: Int,
    override val left : Matrix<E>,
    override val right : Scalar<E>) : Matrix<E>, BinaryExpr<E>  {
    override val type = left.type
    override fun get(column: Int, row: Int) = BinaryScalar(op, left[column, row], right)
    override fun toString() = render()
}
data class BinaryMatrix<E:Any>(
    override val op : BinaryOp,
    override val columns: Int,
    override val rows: Int,
    override val left : Matrix<E>,
    override val right : Matrix<E>,
) : Matrix<E>, BinaryExpr<E> {
    override val type = left.type
    override fun get(column: Int, row: Int) = when(op) {
        STACK -> {
            if (row < left.rows) left[column, row]
            else right[column, row - left.rows]
        }
        TIMES -> {
            assert(left.columns == right.rows)
            (1 until left.columns)
                .fold(left[0, row] * right[column, 0]) { prior : Scalar<E>, i ->
                    prior + left[i, row] * right[column, i]
                }
        }
        else -> BinaryScalar(op, left[column,row], right[column,row])
    }
    override fun toString() = render()
}

data class ScalarVariable<E:Any>(override val type: KClass<E>) : Scalar<E> {
    //override val type = default.javaClass.kotlin
    override fun toString() = render()
}
data class MatrixVariable<E:Any>(
    val default : E,
    override val columns : Int,
    override val rows : Int,
    override val type : KClass<E>) : Matrix<E> {
    override fun get(column: Int, row: Int) = MatrixElement(column, row, this)
    override fun toString() = render()
}
data class NamedScalar<E:Any>(
    override val name : String,
    val scalar : Scalar<E>) : Scalar<E>, Named<E> {
    init {
        if(name == "y" && scalar is ConstantScalar) {
            "hello"
        }
    }
    override val type = scalar.type
    override fun toString() = render()
}
data class NamedMatrix<E:Any>(
    override val name : String,
    val matrix : Matrix<E>) : Matrix<E>, Named<E> {
    override val columns = matrix.columns
    override val rows = matrix.rows
    override val type = matrix.type
    override fun get(column: Int, row: Int) = when(matrix) {
        is MatrixVariable -> NamedMatrixVariableElement(name, column, row, type)
        else -> matrix[column,row]
    }
    override fun toString() = render()
}
data class MatrixElement<E:Any>(val column : Int, val row : Int, val matrix : Matrix<E>) : Scalar<E> {
    override val type = matrix.type
    override fun toString() = render()
}
data class NamedMatrixVariableElement<E:Any>(
    val name : String,
    val column : Int,
    val row : Int,
    override val type : KClass<E>) : Scalar<E> {
    override fun toString() = render()
}
data class DataMatrix<E:Any>(
    override val columns: Int,
    override val rows: Int,
    val elements: List<Scalar<E>>,
    override val type: KClass<E> = elements[0].type
) : Matrix<E> {
    init {
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
    val members : List<Named<E>>,
    override val type: KClass<E>
) : Expr<E> {
    override fun toString() = render()
}

// Pow
fun <E:Any, L : Scalar<E>, R : E> pow(left : L, right : R) = BinaryScalar(POW, left, ConstantScalar(right))
fun <E:Any, L : Matrix<E>, R : E> pow(left : L, right : R) = BinaryMatrixScalar(POW, left.columns, left.rows, left, ConstantScalar(right))
fun <E:Any, L : Scalar<E>, R : Scalar<E>> pow(left : L, right : R) : Scalar<E> = BinaryScalar(POW, left, right)
fun <E:Any, L : Matrix<E>, R : Scalar<E>> pow(left : L, right : R) = BinaryMatrixScalar(POW, left.columns, left.rows, left, right)
// Logit
fun <E:Any, T : E> logit(value : T) : Scalar<E> = UnaryScalar(LOGIT, ConstantScalar(value))
fun <E:Any, T : Scalar<E>> logit(expr : T) : Scalar<E> = UnaryScalar(LOGIT, expr)
fun <E:Any, T : Matrix<E>> logit(expr : T) : Matrix<E> = UnaryMatrix(LOGIT, expr)
// Times
operator fun <E:Any, L : Scalar<E>, R : E> L.times(right : R) = BinaryScalar(TIMES, this, ConstantScalar(right))
operator fun <E:Any, L : Scalar<E>, R : Scalar<E>> L.times(right : R) = BinaryScalar(TIMES, this, right)
operator fun Double.times(right : Scalar<Double>) = BinaryScalar(TIMES, ConstantScalar(this), right)
operator fun <E:Any, L : Matrix<E>, R : E> L.times(right : R) = BinaryMatrixScalar(TIMES, columns, rows, this, ConstantScalar(right))
operator fun <E:Any, L : E, R : Matrix<E>> L.times(right : R) = BinaryScalarMatrix(TIMES, right.columns, right.rows, ConstantScalar(this), right)
operator fun <E:Any, L : Matrix<E>, R : Scalar<E>> L.times(right : R) = BinaryMatrixScalar(TIMES, columns, rows, this, right)
operator fun <E:Any, L : Scalar<E>, R : Matrix<E>> L.times(right : R) = BinaryScalarMatrix(TIMES, right.columns, right.rows, this, right)
operator fun <E:Any, L : Matrix<E>, R : Matrix<E>> L.times(right : R) = run {
    assert(columns == right.rows)
    BinaryMatrix(TIMES, right.columns, rows, this, right)
}
// Div
operator fun <E:Any, L : Scalar<E>, R : E> L.div(right : R) = BinaryScalar(DIV, this, ConstantScalar(right))
operator fun <E:Any, L : Scalar<E>, R : Scalar<E>> L.div(right : R) = BinaryScalar(DIV, this, right)
operator fun Double.div(right : Scalar<Double>) = BinaryScalar(DIV, ConstantScalar(this), right)
operator fun <E:Any, L : Matrix<E>, R : E> L.div(right : R) = BinaryMatrixScalar(DIV, columns, rows, this, ConstantScalar(right))
operator fun <E:Any, L : E, R : Matrix<E>> L.div(right : R) = BinaryScalarMatrix(DIV, right.columns, right.rows, ConstantScalar(this), right)
operator fun <E:Any, L : Matrix<E>, R : Scalar<E>> L.div(right : R) = BinaryMatrixScalar(DIV, columns, rows, this, right)
operator fun <E:Any, L : Scalar<E>, R : Matrix<E>> L.div(right : R) = BinaryScalarMatrix(DIV, right.columns, right.rows, this, right)
// Plus
operator fun <E:Any, L : Scalar<E>, R : E> L.plus(right : R) = BinaryScalar(PLUS, this, ConstantScalar(right))
operator fun <E:Any, L : Scalar<E>, R : Scalar<E>> L.plus(right : R) : Scalar<E> = BinaryScalar(PLUS, this, right)
operator fun Double.plus(right : Scalar<Double>) = BinaryScalar(PLUS, ConstantScalar(this), right)
operator fun <E:Any, L : Matrix<E>, R : E> L.plus(right : R) = BinaryMatrixScalar(PLUS, columns, rows, this, ConstantScalar(right))
operator fun <E:Any, L : E, R : Matrix<E>> L.plus(right : R) = BinaryScalarMatrix(PLUS, right.columns, right.rows, ConstantScalar(this), right)
operator fun <E:Any, L : Matrix<E>, R : Scalar<E>> L.plus(right : R) = BinaryMatrixScalar(PLUS, columns, rows, this, right)
operator fun <E:Any, L : Scalar<E>, R : Matrix<E>> L.plus(right : R) = BinaryScalarMatrix(PLUS, right.columns, right.rows, this, right)
operator fun <E:Any, L : Matrix<E>, R : Matrix<E>> L.plus(right : R) = BinaryMatrix(PLUS, right.columns, right.rows, this, right)
// Minus
operator fun <E:Any, L : Scalar<E>, R : E> L.minus(right : R) = BinaryScalar(MINUS, this, ConstantScalar(right))
operator fun <E:Any, L : Scalar<E>, R : Scalar<E>> L.minus(right : R) = BinaryScalar(MINUS, this, right)
operator fun Double.minus(right : Scalar<Double>) = BinaryScalar(MINUS, ConstantScalar(this), right)
operator fun <E:Any, L : Matrix<E>, R : E> L.minus(right : R) = BinaryMatrixScalar(MINUS, columns, rows, this, ConstantScalar(right))
operator fun <E:Any, L : E, R : Matrix<E>> L.minus(right : R) = BinaryScalarMatrix(MINUS, right.columns, right.rows, ConstantScalar(this), right)
operator fun <E:Any, L : Matrix<E>, R : Scalar<E>> L.minus(right : R) = BinaryMatrixScalar(MINUS, columns, rows, this, right)
operator fun <E:Any, L : Scalar<E>, R : Matrix<E>> L.minus(right : R) = BinaryScalarMatrix(MINUS, right.columns, right.rows, this, right)
operator fun <E:Any, L : Matrix<E>, R : Matrix<E>> L.minus(right : R) = BinaryMatrix(MINUS, right.columns, right.rows, this, right)
// Stack
private fun <E:Any> stackMatrix(left : Matrix<E>, right : Matrix<E>) : Matrix<E> {
    assert(left.columns == right.columns)
    return BinaryMatrix(STACK, left.columns, left.rows + right.rows, left, right)
}
infix fun <E:Any> Matrix<E>.stack(right : E) : Matrix<E> = stackMatrix(this, repeated(columns, 1, right))
infix fun <E:Any> E.stack(right : Matrix<E>) : Matrix<E> = stackMatrix(repeated(right.columns, 1, this), right)
infix fun <E:Any> Matrix<E>.stack(right : Scalar<E>) = stackMatrix(this, repeated(columns, 1, right))
infix fun <E:Any> Scalar<E>.stack(right : Matrix<E>) =stackMatrix(repeated(right.columns, 1, this), right)
infix fun <E:Any> Matrix<E>.stack(right : Matrix<E>) = stackMatrix(this, right)
// Summation
fun <E:Any> summation(expr : Matrix<E>) : Scalar<E> = UnaryMatrixScalar(SUMMATION, expr)
// Default
fun <E:Any> defaultOf(type : KClass<E>) =
    when(type) {
        Double::class -> 0.0 as E
        Int::class -> 0 as E
        String::class -> "" as E
        else ->
            error("${type.java}")
    }
// Variables
inline fun <reified E:Any> matrixVariable(columns : Int, rows : Int) : Matrix<E> = MatrixVariable(defaultOf(E::class), columns, rows, E::class)
inline fun <reified E:Any> variable() : Scalar<E> = ScalarVariable(E::class)
inline fun <reified E:Any> variable(type : KClass<E>) : Scalar<E> = ScalarVariable(type)
// Tableau
fun <E:Any> tableauOf(vararg elements : Named<E>) : Tableau<E> = Tableau(elements.toList(), elements[0].type)
fun <E:Any> tableauOf(elements : List<Named<E>>) : Tableau<E> = Tableau(elements, elements[0].type)
fun <E:Any> Tableau<E>.map(type : KClass<E>, action : (Named<E>) -> Named<E>) : Tableau<E> = Tableau(members.map(action), type)
fun <E:Any> Tableau<E>.map(action : (Named<E>) -> Named<E>) : Tableau<E> = map(type, action)
// Misc
fun <E:Any> constant(value : E) : Scalar<E> = ConstantScalar(value)
fun <E:Any> constant(value : Double, type : KClass<E>) : Scalar<E> = when {
    value.javaClass.kotlin == type -> ConstantScalar(value) as Scalar<E>
    else -> ConvertScalar(ConstantScalar(value), type)
}
fun <E:Any> repeated(columns : Int, rows : Int, value : E) : Matrix<E> =
    repeated(columns, rows, ConstantScalar(value))
fun <E:Any> repeated(columns : Int, rows : Int, value : Scalar<E>) : Matrix<E> =
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
val <E:Any> Matrix<E>.coordinates get() = coordinatesOf(columns, rows)
val <E:Any> Matrix<E>.elements get() = coordinates.map { coord -> get(coord.column, coord.row) }
fun <E:Any,R:Any> Matrix<E>.foldCoordinates(initial: Scalar<R>?, action : (Scalar<R>?, Coordinate)->Scalar<R>) =
    coordinates.fold(initial) { prior, coord -> action(prior, coord) }

// Differentiation
fun <E:Any, T : Scalar<E>> d(expr : T) : Scalar<E> = UnaryScalar(D, expr)
fun <E:Any, T : Matrix<E>> d(expr : T) : Matrix<E> = UnaryMatrix(D, expr)

private fun <E:Any> diff(expr : Scalar<E>, variable : Scalar<E>, entryPoint : Boolean = true) : Scalar<E> {
    if (variable is NamedScalar && variable.scalar !is ScalarVariable) return diff(expr, variable.scalar)
    fun Scalar<E>.self() = diff(this, variable, false)
    val result : Scalar<E> = when (expr) {
        variable -> constant(1.0, expr.type)
        is MatrixElement -> expr
        is UnaryScalar -> when(expr.op) {
            LOGIT -> logit(expr.value) * (constant(1.0, expr.type) - logit(expr.value)) * expr.value.self()
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
                MINUS -> expr.left * diffRight - diffLeft * expr.right
                POW -> expr.right * pow(expr.left, expr.right - constant(1.0, expr.type)) * diffLeft
                else -> error("${expr.op}")
            }
            result
        }
        is NamedMatrixVariableElement -> constant(0.0, expr.type)
        is ScalarVariable -> constant(0.0, expr.type)
        is ConstantScalar -> constant(0.0, expr.type)
        else -> error("${expr.javaClass}")
    }
    return result
}

fun <E:Any> differentiate(expr : Expr<E>) : Expr<E> {
    fun Scalar<E>.self() = differentiate(this as Expr<E>) as Scalar<E>
    fun Matrix<E>.self() = differentiate(this as Expr<E>) as Matrix<E>
    fun Named<E>.self() = differentiate(this as Expr<E>) as Named<E>
    fun Scalar<E>.unwrapName() : Scalar<E> = when(this) {
        is NamedScalar -> scalar.unwrapName()
        else -> this
    }
    fun Matrix<E>.unwrapName() : Matrix<E> = when(this) {
        is NamedMatrix -> matrix.unwrapName()
        else -> this
    }
    fun Scalar<E>.diffOr() : Scalar<E>? {
        if (this !is BinaryScalar<E>) return null
        if (op != DIV) return null
        if (left !is UnaryScalar) return null
        if (left.op != D) return null
        if (right !is UnaryScalar) return null
        if (right.op != D) return null
        return diff(instantiateVariables(left.value), right.value)
    }
    fun Matrix<E>.diffOr() : Matrix<E>? {
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
        is ScalarVariable -> expr
        is MatrixVariable -> expr
        is MatrixElement-> expr
        is DataMatrix -> expr.copy(elements = expr.elements.map { it.self() })
        is BinaryMatrixScalar<E> -> expr.copy(left = expr.left.self(), right = expr.right.self())
        is BinaryScalarMatrix<E> -> expr.diffOr() ?: expr.copy(left = expr.left.self(), right = expr.right.self())
        is BinaryMatrix<E> -> expr.copy(left = expr.left.self(), right = expr.right.self())
        is BinaryScalar<E> -> expr.diffOr() ?: expr.copy(left = expr.left.self(), right = expr.right.self())
        is Tableau<E> -> expr.copy(members = expr.members.map { it.self() })
        else -> error("${expr.javaClass}")
    }
}

fun <E:Any> differentiate(expr : Scalar<E>) : Scalar<E> = (differentiate(expr as Expr<E>) as Scalar<E>).reduceArithmetic()
fun <E:Any> differentiate(expr : Matrix<E>) : Matrix<E> = (differentiate(expr as Expr<E>) as Matrix<E>).reduceArithmetic()
fun <E:Any> differentiate(expr : Tableau<E>) : Tableau<E> = (differentiate(expr as Expr<E>) as Tableau<E>).reduceArithmetic()

// Instantiate variable
private fun <E:Any> instantiateVariables(expr : Matrix<E>, column : Int, row : Int) : Scalar<E> {
    fun Matrix<E>.self(column : Int, row : Int) : Scalar<E> = instantiateVariables(this, column, row)
    fun Matrix<E>.self() : Scalar<E> = self(column, row)
    return when(expr) {
        is NamedMatrix -> when(expr.matrix) {
            is MatrixVariable -> NamedMatrixVariableElement(expr.name, column, row, expr.type)
            else -> expr.matrix.self()
        }
        is MatrixVariable -> MatrixElement(column,row,expr)
        else -> expr[column,row]
    }
}

fun <E:Any> instantiateVariables(expr : Expr<E>, entryPoint : Boolean = true) : Expr<E> {
    fun Scalar<E>.self() : Scalar<E> = instantiateVariables(this as Expr<E>, false) as Scalar<E>
    fun Matrix<E>.self() : Matrix<E> = instantiateVariables(this as Expr<E>, false) as Matrix<E>
    fun Named<E>.self() : Named<E> = instantiateVariables(this as Expr<E>, false) as Named<E>
    val result : Expr<E> = when(expr) {
        is NamedMatrixVariableElement -> expr
        is ScalarVariable -> expr
        is MatrixVariable -> expr
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
        is MatrixElement ->
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
        is Tableau -> expr.copy(members = expr.members.map { it.self() })
        is DataMatrix -> expr.map { it.self() }
        else ->
            error("${expr.javaClass}")
    }
    return result
}

fun <E:Any> instantiateVariables(expr : Scalar<E>) = instantiateVariables(expr as Expr<E>) as Scalar<E>
fun <E:Any> instantiateVariables(expr : Matrix<E>) = instantiateVariables(expr as Expr<E>) as Matrix<E>
fun <E:Any> instantiateVariables(expr : Tableau<E>) = instantiateVariables(expr as Expr<E>) as Tableau<E>
//fun <E:Any> instantiateVariables(expr : Named<E>) = instantiateVariables(expr as Expr<E>) as Named<E>

// Data matrix
inline fun <reified E:Any> matrixOf(columns: Int, rows: Int, vararg elements : E) = DataMatrix(
    columns,
    rows,
    elements.map { ConstantScalar(it) }.toList(),
    E::class
)
fun <E:Any> matrixOf(columns: Int, rows: Int, action:(Coordinate)->Scalar<E>) :DataMatrix<E> =
    DataMatrix(
        columns,
        rows,
        coordinatesOf(columns, rows).map(action).toList()
    )
fun <E:Any> Matrix<E>.toDataMatrix() : Matrix<E> = when(this) {
    is DataMatrix -> this
    is NamedMatrix -> copy(matrix = matrix.toDataMatrix())
    else -> DataMatrix(columns, rows, elements.toList(), type)
}
fun <E:Any> Matrix<E>.map(action:(Scalar<E>)->Scalar<E>) = DataMatrix(columns, rows, elements.map(action).toList(), type)

// Replace subexpressions
fun <E:Any> Expr<E>.replace(find : Expr<E>, replace : Expr<E>, memo : MutableMap<Expr<E>,Expr<E>> = mutableMapOf()) : Expr<E> {
    if (this == find) return replace
    fun Scalar<E>.self() = replace(find, replace, memo) as Scalar<E>
    fun Matrix<E>.self() = replace(find, replace, memo) as Matrix<E>
    fun Named<E>.self() = replace(find, replace, memo) as Named<E>
    when(this) {
        is ConstantScalar,
        is ScalarVariable,
        is MatrixVariable,
        is MatrixElement,
        is NamedMatrixVariableElement -> return this
    }
    if (memo.containsKey(this)) {
        val result = memo.getValue(this)
        return result
    }

    fun reduce(scalar : Scalar<E>) : Expr<E> {
        if (scalar !is BinaryScalar) return scalar
        fun self() =
            scalar.copy(left = scalar.left.self(), right = scalar.right.self())
        if (find !is BinaryScalar) return self()
        if (replace !is Scalar) return self()
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
        is Tableau -> Tableau(members.map { it.self() }, type)
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

// Extract subexpressions
private fun <E:Any> emptyExprTable() = mapOf<Scalar<E>, Int>()

infix operator fun <E:Any> Map<Scalar<E>,Int>.plus(right : Scalar<E>) : Map<Scalar<E>,Int> {
    val result = toMutableMap()
    result[right] = result.computeIfAbsent(right) { 0 } + 1
    return result
}

infix operator fun <E:Any> Map<Scalar<E>,Int>.plus(right : Map.Entry<Scalar<E>, Int>) : Map<Scalar<E>,Int> {
    val result = toMutableMap()
    result[right.key] = result.computeIfAbsent(right.key) { 0 } + right.value
    return result
}

infix operator fun <E:Any> Map<Scalar<E>,Int>.plus(right : Map<Scalar<E>,Int>) : Map<Scalar<E>,Int> {
    return right.entries.fold(this) { prior, entry -> prior + entry }
}

private fun <E:Any> BinaryScalar<E>.expandAssociative() : List<Scalar<E>> {
    if (!op.associative) return listOf()
    val leftExpanded = if (left is BinaryScalar && left.op == op) left.expandAssociative() else listOf(left)
    val rightExpanded = if (right is BinaryScalar && right.op == op) right.expandAssociative() else listOf(right)
    return leftExpanded + rightExpanded
}

data class ExprMap<E:Any>(val data : MutableMap<Scalar<E>, Int> = mutableMapOf()) {
    var count : Int = 0
    fun add(expr : Scalar<E>) {
        ++count
        data[expr] = data.computeIfAbsent(expr) { 0 } + 1
    }
}

fun <E:Any> Expr<E>.countSubExpressions(memo : ExprMap<E> = ExprMap()) : Map<Scalar<E>, Int> {
    //if (memo.count > 256 + 64 + 16 + 8) return memo.data
    fun Expr<E>.self() = countSubExpressions(memo)
    fun <E:Any> Expr<E>.terminal() : Boolean = when(this) {
        is NamedMatrixVariableElement -> true
        is ConstantScalar -> true
        is ScalarVariable -> true
        is UnaryMatrixScalar -> false
        is NamedScalar -> scalar.terminal()
        is UnaryScalar -> value.terminal()
        is BinaryScalar -> false
        is BinaryScalarMatrix -> false
        is MatrixElement -> {
            val result = matrix[column,row]
            if (result == this) true
            else result.terminal()
        }
        else -> error("$javaClass")
    }
    fun Scalar<E>.add() = memo.add(this)
    when(this) {
        is NamedScalar -> scalar.self()
        is ConstantScalar,
        is ScalarVariable,
        is MatrixVariable,
        is MatrixElement,
        is NamedMatrixVariableElement -> {}
        is NamedMatrix -> matrix.self()
        is Tableau -> members.forEach { it.self() }
        is UnaryScalar -> {
            value.self()
            if (terminal()) add()
        }
        is BinaryScalar -> {
            if (left.terminal() && right.terminal()) add() else {
                val (terminal, non) = expandAssociative().partition { it.terminal() }
                coordinatesOf(terminal.size, terminal.size).forEach { (column, row) ->
                    if (row < column) BinaryScalar(op, terminal[row], terminal[column]).add()
                }
                non.forEach { it.self() }
            }
        }
        is Matrix -> elements.forEach { it.self() }
        is UnaryExpr -> {
            value.self()
            if (this is Scalar && value.terminal()) add()
        }
        is BinaryExpr -> {
            left.self()
            right.self()
        }
        else -> error("$javaClass")
    }
    return memo.data
}

fun <E:Any> Named<E>.extractCommonSubExpressions() = tableauOf(this).extractCommonSubExpressions()
fun <E:Any> Tableau<E>.extractCommonSubExpressions(): Tableau<E> {
    val extracted = mutableListOf<Named<E>>()
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
        val replacement = NamedScalar("c${extracted.size}", ScalarVariable(type))
        extracted.add(NamedScalar("c${extracted.size}", max))
        tab = tab.replace(max, replacement)
    }
    return tableauOf(extracted + tab.members)
}

// Reduce arithmetic
fun Expr<Double>.reduceArithmeticDouble(entryPoint : Boolean = true) : Expr<Double> {
    fun Scalar<Double>.self() = reduceArithmeticDouble(false) as Scalar<Double>
    fun Matrix<Double>.self() = reduceArithmeticDouble(false) as Matrix<Double>
    fun Named<Double>.self() = reduceArithmeticDouble(false) as Named<Double>
    fun Scalar<Double>.findConstant() : Double? = when(this) {
        is ConstantScalar -> value
        is NamedScalar -> scalar.findConstant()
        else -> null
    }
    val result = when(this) {
        is ConstantScalar -> this
        is ScalarVariable -> this
        is MatrixElement -> this
        is UnaryMatrixScalar -> copy(value = value.self())
        is NamedScalar -> copy(scalar = scalar.self())
        is NamedMatrix -> copy(matrix = matrix.self())
        is Matrix -> matrixOf(columns, rows) { (column, row) -> this[column,row].self() }
        is BinaryScalar -> {
            if (op.associative) {
                val associates = expandAssociative()
                val (const, non) = associates.partition { it is ConstantScalar }
                when(op) {
                    PLUS -> {
                        fun accumulate(left : Scalar<Double>, right : Scalar<Double>) : Scalar<Double> {
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
                        (const + non).fold(ConstantScalar(0.0) as Scalar<Double>) { left, right ->
                            accumulate(left, right)
                        }
                    }
                    TIMES -> {
                        fun accumulate(left : Scalar<Double>, right : Scalar<Double>) : Scalar<Double> {
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
                        (const + non).fold(ConstantScalar(1.0) as Scalar<Double>) { left, right ->
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
                        else -> error("$op")
                    }
                    rightConst == 0.0 -> when (op) {
                        MINUS -> left
                        DIV -> error("div by zero")
                        else -> error("$op")
                    }
                    leftConst == 0.0 -> when (op) {
                        MINUS -> right
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
        is Tableau -> copy(members = members.map { it.self() })
        else -> error("$javaClass")
    }
    return result
}

fun <E:Any> Scalar<E>.reduceArithmetic() : Scalar<E> {
    return when(type) {
        Double::class -> (this as Scalar<Double>).reduceArithmeticDouble() as Scalar<E>
        else -> error("$javaClass")
    }
}

fun <E:Any> Matrix<E>.reduceArithmetic() : Matrix<E> {
    return when(type) {
        Double::class -> (this as Matrix<Double>).reduceArithmeticDouble() as Matrix<E>
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
    if (parentBinOp != null && childBinOp != null && parentBinOp.infixOrder != null && childBinOp.infixOrder != null ) {
        return if (parentBinOp.infixOrder < childBinOp.infixOrder) true
        else childIsRight && parentBinOp.infixOrder == childBinOp.infixOrder && !parentBinOp.associative
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
        is MatrixElement<*> -> {
            when {
                matrix is NamedMatrix -> "${matrix.name}[$column,$row]"
                matrix is MatrixVariable -> "[$column,$row]"
                else -> {
                    val element = matrix[column, row]
                    assert(element !is MatrixElement) {
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
            val sorted = members.sortedBy {
                when(it) {
                    is Scalar<*> -> 0
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
        is MatrixElement<*> -> "$this"
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
