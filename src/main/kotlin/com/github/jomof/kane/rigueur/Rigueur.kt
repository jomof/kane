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
data class UnaryScalar<E:Any>(val op : UnaryOp, val value : Scalar<E>) : Scalar<E> {
    override val type = value.type
    override fun toString() = render()
}
data class UnaryMatrixScalar<E:Any>(val op : UnaryOp, val value : Matrix<E>) : Scalar<E> {
    override val type = value.type
    override fun toString() = render()
}
data class UnaryMatrix<E:Any>(val op : UnaryOp, val value : Matrix<E>) : Matrix<E> {
    override val columns = value.columns
    override val rows = value.rows
    override val type = value.type
    override fun get(column: Int, row: Int) = UnaryScalar(op, value[column,row])
    override fun toString() = render()

}
data class BinaryScalar<E:Any>(val op : BinaryOp, val left : Scalar<E>, val right : Scalar<E>) : Scalar<E> {
    override val type = left.type
    override fun toString() = render()
}
data class FuncMatrix<E:Any>(
    override val columns: Int,
    override val rows: Int,
    override val type: KClass<E>,
    val render : String = "",
    val func : (column:Int, row:Int) -> Scalar<E>) : Matrix<E> {
    init {
        func(0,0) // Fail early
    }
    override fun get(column: Int, row: Int) = func(column, row)
    override fun toString() = render()
}
data class BinaryScalarMatrix<E:Any>(
    val op : BinaryOp,
    override val columns: Int,
    override val rows: Int,
    val left : Scalar<E>,
    val right : Matrix<E>) : Matrix<E> {
    override val type = left.type
    override fun get(column: Int, row: Int) = BinaryScalar(op, left, right[column, row])
    override fun toString() = render()
}
data class BinaryMatrixScalar<E:Any>(
    val op : BinaryOp,
    override val columns: Int,
    override val rows: Int,
    val left : Matrix<E>,
    val right : Scalar<E>) : Matrix<E> {
    override val type = left.type
    override fun get(column: Int, row: Int) = BinaryScalar(op, left[column, row], right)
    override fun toString() = render()
}
data class BinaryMatrix<E:Any>(
    val op : BinaryOp,
    override val columns: Int,
    override val rows: Int,
    val left : Matrix<E>,
    val right : Matrix<E>,
) : Matrix<E> {
    override val type = left.type
    override fun get(column: Int, row: Int) = when(op) {
        STACK -> {
            if (row < left.rows) left[column, row]
            else right[column, row - left.rows]
        }
        TIMES -> {
            assert(left.columns == right.rows)
            (1 until left.columns)
                .fold(left[column, 0] * right[0, row]) { prior : Scalar<E>, i ->
                    prior + left[column, i] * right[i, row]
                }
        }
        else -> BinaryScalar(op, left[column,row], right[column,row])
    }
    override fun toString() = render()
}

data class ScalarVariable<E:Any>(val default : E) : Scalar<E> {
    override val type = default.javaClass.kotlin
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
data class NamedScalar<E:Any>(val name : String, val scalar : Scalar<E>) : Scalar<E> {
    override val type = scalar.type
    override fun toString() = render()
}
data class NamedMatrix<E:Any>(val name : String, val matrix : Matrix<E>) : Matrix<E> {
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
    return  BinaryMatrix(STACK, left.columns, left.rows + right.rows, left, right)
}
infix fun <E:Any> Matrix<E>.stack(right : E) : Matrix<E> = stackMatrix(this, repeated(columns, 1, right))
infix fun <E:Any> E.stack(right : Matrix<E>) : Matrix<E> = stackMatrix(repeated(right.columns, 1, this), right)
infix fun <E:Any> Matrix<E>.stack(right : Scalar<E>) = stackMatrix(this, repeated(columns, 1, right))
infix fun <E:Any> Scalar<E>.stack(right : Matrix<E>) =stackMatrix(repeated(right.columns, 1, this), right)
infix fun <E:Any> Matrix<E>.stack(right : Matrix<E>) = stackMatrix(this, right)
// Summation
fun <E:Any> summation(expr : Matrix<E>) : Scalar<E> = UnaryMatrixScalar(SUMMATION, expr)
// Default
inline fun <reified E:Any> defaultOf() =
    when(E::class) {
        Double::class -> 0.0 as E
        Int::class -> 0 as E
        String::class -> "" as E
        else -> error("${E::class.java}")
    }
// Variables
inline fun <reified E:Any> matrixVariable(columns : Int, rows : Int) : Matrix<E> = MatrixVariable(defaultOf(), columns, rows, E::class)
inline fun <reified E:Any> variable() : Scalar<E> = ScalarVariable(defaultOf())
fun <E:Any> variable(default : E) : Scalar<E> = ScalarVariable(default)
// Misc
fun <E:Any> constant(value : Double, type : KClass<E>) : Scalar<E> = when {
    value.javaClass.kotlin == type -> ConstantScalar(value) as Scalar<E>
    else -> ConvertScalar(ConstantScalar(value), type)
}
fun <E:Any> Matrix<E>.foldCoordinates(action : (Scalar<E>?, Int, Int)->Scalar<E>) : Scalar<E>? {
    var prior : Scalar<E>? = null
    for(column in 0 until columns) {
        for(row in 0 until rows) {
            prior = action(prior, column, row)
        }
    }
    return prior
}
fun <E:Any> repeated(columns : Int, rows : Int, value : E) : Matrix<E> =
    repeated(columns, rows, ConstantScalar(value))
fun <E:Any> repeated(columns : Int, rows : Int, value : Scalar<E>) : Matrix<E> =
    FuncMatrix(columns, rows, value.type, render = value.toString()) { _,_ -> value }
// Differentiation
fun <E:Any, T : Scalar<E>> d(expr : T) : Scalar<E> = UnaryScalar(D, expr)
fun <E:Any, T : Matrix<E>> d(expr : T) : Matrix<E> = UnaryMatrix(D, expr)

private fun <E:Any> diff(expr : Scalar<E>, variable : Scalar<E>) : Scalar<E> {
    fun Scalar<E>.self() = diff(this, variable)
    return when {
        expr == variable -> constant(1.0, expr.type)
        expr is MatrixElement -> expr
        expr is UnaryScalar -> when(expr.op) {
            LOGIT -> logit(expr.value) * (constant(1.0, expr.type) - logit(expr.value)) * expr.value.self()
            else -> error("${expr.op}")
        }
        expr is NamedScalar -> expr.copy(scalar = expr.scalar.self())
        expr is UnaryMatrixScalar -> when(expr.op) {
            SUMMATION -> {
                expr.value.foldCoordinates { prior, column, row ->
                    val result = expr.value[column, row].self()
                    if (prior == null) result
                    else prior + result
                }!!
            }
            else -> error("${expr.op}")
        }
        expr is BinaryScalar -> {
            when(expr.op) {
                PLUS -> expr.left.self() + expr.right.self()
                TIMES -> expr.left * expr.right.self() + expr.left.self() * expr.right
                MINUS -> expr.left * expr.right.self() - expr.left.self() * expr.right
                POW -> expr.right * pow(expr.left, expr.right - constant(1.0, expr.type)) * expr.left.self()
                else -> error("${expr.op}")
            }
        }
        expr is NamedMatrixVariableElement-> constant(0.0, expr.type)
        expr is ScalarVariable -> constant(0.0, expr.type)
        expr is ConstantScalar -> constant(0.0, expr.type)
        else ->
            error("${expr.javaClass}")
    }
}

fun <E:Any> differentiate(expr : Expr<E>) : Expr<E> {
    fun Scalar<E>.self() = differentiate(this as Expr<E>) as Scalar<E>
    fun Matrix<E>.self() = differentiate(this as Expr<E>) as Matrix<E>
    return when(expr) {
        is NamedScalar -> expr.copy(scalar = expr.scalar.self())
        is UnaryScalar<E> -> {
            assert(expr.op != D)
            expr.copy(value = expr.value.self())
        }
        is UnaryMatrix<E> -> {
            assert(expr.op != D)
            expr.copy(value = expr.value.self())
        }
        is UnaryMatrixScalar<E> -> {
            assert(expr.op != D)
            expr.copy(value = expr.value.self())
        }
        is ConstantScalar -> expr
        is ScalarVariable -> expr
        is MatrixVariable -> expr
        is MatrixElement-> expr
        is NamedMatrix -> expr.copy(matrix = expr.matrix.self())
        is BinaryMatrixScalar<E> -> expr.copy(left = expr.left.self(), right = expr.right.self())
        is BinaryScalarMatrix<E> -> {
            if (expr.op == DIV &&
                expr.left is UnaryScalar && expr.left.op == D &&
                expr.right is UnaryMatrix && expr.right.op == D
            ) {
                FuncMatrix(expr.right.columns, expr.right.rows, expr.type) { column, row ->
                    diff(expr.left.value, expr.right[column, row])
                }
            } else {
                expr.copy(left = expr.left.self(), right = expr.right.self())
            }
        }
        is BinaryMatrix<E> -> expr.copy(left = expr.left.self(), right = expr.right.self())
        is BinaryScalar<E> -> {
            if (expr.op == DIV &&
                expr.left is UnaryScalar && expr.left.op == D &&
                expr.right is UnaryScalar && expr.right.op == D
            ) {
                val result = diff(expr.left.value, expr.right.value)
                result
            } else {
                expr.copy(left = expr.left.self(), right = expr.right.self())
            }
        }
        is FuncMatrix<E> -> expr.copy(func = {column, row -> expr[column, row].self() })
        else -> error("${expr.javaClass}")
    }
}

fun <E:Any> differentiate(expr : Scalar<E>) : Scalar<E> = (differentiate(expr as Expr<E>) as Scalar<E>).reduceArithmetic()
fun <E:Any> differentiate(expr : Matrix<E>) : Matrix<E> = (differentiate(expr as Expr<E>) as Matrix<E>).reduceArithmetic()

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

fun <E:Any> instantiateVariables(expr : Expr<E>) : Expr<E> {
    fun Scalar<E>.self() : Scalar<E> = instantiateVariables(this)
    fun Matrix<E>.self() : Matrix<E> = instantiateVariables(this)
    return when(expr) {
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
        is NamedScalar -> expr.copy(scalar = expr.scalar.self())
        is NamedMatrix -> expr.copy(matrix = expr.matrix.self())
        is MatrixElement ->
            expr.matrix[expr.column, expr.row]
        is UnaryMatrixScalar -> when(expr.op) {
            SUMMATION -> {
                expr.value.foldCoordinates { prior, column, row ->
                    val current = instantiateVariables(expr.value, column, row)
                    if (prior == null) current
                    else prior + current
                }!!
            }
            else -> error("${expr.op}")
        }
        is FuncMatrix -> expr.copy(func = { column, row ->  instantiateVariables(expr.func(column, row)) })
        else ->
            error("${expr.javaClass}")
    }
}

fun <E:Any> instantiateVariables(expr : Scalar<E>) = instantiateVariables(expr as Expr<E>) as Scalar<E>
fun <E:Any> instantiateVariables(expr : Matrix<E>) = instantiateVariables(expr as Expr<E>) as Matrix<E>

// Reduce arithmetic
fun Expr<Double>.reduceArithmeticDouble(entryPoint : Boolean = true) : Expr<Double> {
    fun Scalar<Double>.self() = reduceArithmeticDouble(false) as Scalar<Double>
    fun Matrix<Double>.self() = reduceArithmeticDouble(false) as Matrix<Double>
    val result = when(this) {
        is ConstantScalar -> this
        is ScalarVariable -> this
        is MatrixElement -> this
        is NamedScalar -> run {
            val scalar = scalar.self()
            when {
                entryPoint -> copy(name = "$name'", scalar = scalar)
                scalar is ScalarVariable -> copy(scalar = scalar)
                else -> scalar
            }
        }
        is NamedMatrix -> run {
            val matrix = matrix.self()
            when {
                entryPoint -> copy(name = "$name'", matrix = matrix)
                matrix is MatrixVariable -> copy(matrix = matrix)
                else -> matrix
            }
        }
        is Matrix -> FuncMatrix(columns, rows, type) { column, row -> this[column,row].self() }
        is BinaryScalar -> {
            val left = left.self()
            val right = right.self()
            when {
                left is ConstantScalar && right is ConstantScalar -> when(op) {
                    PLUS -> ConstantScalar(left.value + right.value)
                    MINUS -> ConstantScalar(left.value - right.value)
                    TIMES -> ConstantScalar(left.value * right.value)
                    DIV -> ConstantScalar(left.value / right.value)
                    else -> error("$op")
                }
                right is ConstantScalar && right.value == 0.0 -> when(op) {
                    PLUS -> left
                    MINUS -> left
                    TIMES -> right
                    DIV -> error("div by zero")
                    else -> error("$op")
                }
                left is ConstantScalar && left.value == 0.0 ->  when(op) {
                    PLUS -> right
                    MINUS -> right
                    TIMES -> left
                    DIV -> left
                    else -> error("$op")
                }
                right is ConstantScalar && right.value == 1.0 -> when(op) {
                    PLUS -> left + right
                    MINUS -> left - right
                    TIMES -> left
                    DIV -> left
                    POW -> left
                    else -> error("$op")
                }
                left is ConstantScalar && left.value == 1.0 ->  when(op) {
                    PLUS -> left + right
                    MINUS -> left - right
                    TIMES -> right
                    DIV -> right
                    else -> error("$op")
                }
                else -> copy(left = left, right = right)
            }
        }
        is UnaryScalar -> copy(value = value.self())
        is FuncMatrix -> copy(func = { column, row -> this[column, row].self() })
        is NamedMatrixVariableElement -> this
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
        is ScalarVariable<*> -> renderScalarValue(default)
        is BinaryMatrix<*> -> binary(op, left, right)
        is BinaryScalar<*> -> binary(op, left, right)
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
                !entryPoint -> matrix.self()
                else ->
                    "$name=" + matrix.self()
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
                //else -> error("${matrix.javaClass}")
            }
        }
        is ConstantScalar<*> -> renderScalarValue(value)
        is UnaryScalar<*> -> "${op.op}(${value.self()})"
        is UnaryMatrix<*> -> "${op.op}(${value.self()})"
        is UnaryMatrixScalar<*> -> "${op.op}(${value.self()})"
        is ConvertScalar<*,*> -> value.self()
        is MatrixVariable<*> -> "matrix(${columns}x${rows})"
        is NamedMatrixVariableElement<*> -> "$name[$column,$row]"
        is FuncMatrix<*> -> {
            if (render.isNotBlank()) render
            else {
                val sb = StringBuilder()
                for (column in 0 until columns) {
                    for (row in 0 until rows) {
                        val scalar = func(column, row)
                        sb.append(scalar.self())
                        if (column != columns - 1) sb.append("|")
                    }
                    if (column != columns - 1) sb.append("\n")
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
        is ScalarVariable<*> -> "variable<${type.simpleName}>($default)"
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
                else -> scalar.self()
            }
        }
        is NamedMatrix<*> -> {
            when (matrix) {
                is MatrixVariable -> name
                else -> matrix.self()
            }
        }
        is FuncMatrix<*> -> "$this"
        else ->
            error("$javaClass")
    }
}
