package com.github.jomof.kane.rigueur

import com.github.jomof.kane.rigueur.Expressions.*
import com.github.jomof.kane.rigueur.UnaryFunctionType.*
import com.github.jomof.kane.rigueur.BinaryFunctionType.*
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

interface Expr<T:Any> {
    val type : KClass<T>
}

interface Scalar<T:Any> : Expr<T>

interface MatrixElement<T:Any> : Scalar<T> {
    val column : Int
    val row : Int
}

interface Binary<T:Any> : Expr<T> {
    val left : Expr<T>
    val right : Expr<T>
}

interface InfixBinary<T:Any> : Binary<T> {
    val operator : String
}

interface Matrix<T:Any> : Expr<Matrix<T>> {
    val columns : Int
    val rows : Int
    val elementType : KClass<T>
    operator fun get(column : Int, row : Int) : Scalar<T>
}

operator fun <T:Any> Expr<T>.getValue(nothing: Nothing?, property: KProperty<*>) =
    when (this) {
        is Assign -> Assign(property.name, right)
        else -> Assign(property.name, this)
    }

operator fun <T:Any> Matrix<T>.getValue(nothing: Nothing?, property: KProperty<*>) =
    AssignMatrix(property.name, this)

enum class UnaryFunctionType {
    D,
    LOGIT
}

enum class BinaryFunctionType {
    POW
}

sealed class Expressions {
    data class ScalarVariable<T:Any>(val default : Literal<T>) : Expr<T> {
        override val type = default.type
        override fun toString() = render()
    }

    data class NamedMatrixVariableElement<T:Any>(
        val name : String,
        override val column : Int,
        override val row : Int,
        override val type: KClass<T>
    ) : MatrixElement<T> {
        override fun toString() = render()
    }

    data class MatrixVariableElement<T:Any>(
        override val column : Int,
        override val row : Int,
        override val type: KClass<T>
    ) : MatrixElement<T> {
        override fun toString() = render()
    }

    data class MatrixVariable<T:Any>(
        override val columns : Int,
        override val rows : Int,
        override val elementType: KClass<T>
    ) : Matrix<T> {
        override val type = classOf<Matrix<T>>()
        override fun toString() = render()
        override fun get(column: Int, row: Int) = run {
            assert(column in 0 until columns)
            assert(row in 0 until rows)
            MatrixVariableElement(column, row, elementType)
        }
    }

    data class AssignMatrix<T:Any>(
        val left : String,
        val right : Matrix<T>) : Matrix<T> {
        override val type = right.type
        override val elementType = right.elementType
        override val columns = right.columns
        override val rows = right.rows
        override fun toString() = render()
        override fun get(column: Int, row: Int) = run {
            assert(column in 0 until columns)
            assert(row in 0 until rows)
            NamedMatrixVariableElement(left, column, row, elementType)
        }
    }

    data class Assign<T:Any>(val left : String, val right : Expr<T>) : Expr<T> {
        override val type = right.type
        override fun toString() = render()
    }

    data class Plus<T:Any>(override val left : Expr<T>, override val right : Expr<T>) : InfixBinary<T> {
        init {
            assert(left.type == right.type)
        }
        override val type = right.type
        override val operator = "+"
        override fun toString() = render()
    }

    data class Minus<T:Any>(override val left : Expr<T>, override val right : Expr<T>) : InfixBinary<T> {
        init {
            assert(left.type == right.type)
        }
        override val type = right.type
        override val operator = "-"
        override fun toString() = render()
    }

    data class MatrixTimes<T:Any>(
        override val left : Matrix<T>,
        override val right : Matrix<T>,
        override val elementType: KClass<T>
    ) : InfixBinary<Matrix<T>>, Matrix<T> {
        init {
            assert(left.type == right.type)
            assert(left.columns == right.rows)
        }
        override val type = right.type
        override val columns = right.columns
        override val rows = left.rows
        override val operator = "*"
        override fun toString() = render()
        override fun get(column: Int, row: Int) = error("can't get element of anonymous")
    }

    data class Times<T:Any>(override val left : Expr<T>, override val right : Expr<T>) : InfixBinary<T> {
        init {
            assert(left.type == right.type)
        }
        override val type = right.type
        override val operator = "*"
        override fun toString() = render()
    }

    data class Div<T:Any>(override val left : Expr<T>, override val right : Expr<T>) : InfixBinary<T> {
        init {
            assert(left.type == right.type)
        }
        override val type = right.type
        override val operator = "/"
        override fun toString() = render()
    }

    data class UnaryFunction<T:Any>(val function : UnaryFunctionType, val expr : Expr<T>) : Scalar<T> {
        override val type = expr.type
    }

    data class UnaryMatrixFunction<T:Any>(val function : UnaryFunctionType, val expr : Matrix<T>) : Matrix<T> {
        override val columns = expr.columns
        override val rows = expr.rows
        override val elementType = expr.elementType
        override val type = expr.type
        override fun get(column: Int, row: Int) : Scalar<T> = logit(expr[column, row])
    }

    data class BinaryFunction<T:Any>(val function : BinaryFunctionType, val left : Expr<T>, val right : Expr<T>) : Expr<T> {
        override val type = left.type
    }

    data class Literal<T:Any>(val value : T, override val type : KClass<T>, val format : Format? = null) : Scalar<T> {
        override fun toString() = render()
    }

    data class StackMatrix<T:Any>(
        val top : Matrix<T>,
        val bottom : Matrix<T>,
        override val elementType: KClass<T>) : Matrix<T> {
        init {
            assert(top.type == bottom.type)
            assert(top.columns == bottom.columns)
        }
        override val type = top.type
        override fun toString() = render()
        override val columns = top.columns
        override val rows = top.rows + bottom.rows
        override fun get(column: Int, row: Int) = when {
            row < top.rows -> top[column, row]
            else -> bottom[column, top.rows - row]
        }
    }

    data class LiteralMatrix<T:Any>(
        override val columns: Int,
        override val rows: Int,
        val literal : Literal<T>
    ) : Matrix<T> {
        override val elementType = literal.type
        override val type = classOf<Matrix<T>>()
        override fun get(column: Int, row: Int) = run {
            assert(column in 0 until columns)
            assert(row in 0 until rows)
            literal
        }
    }
}

data class Format(val precision : Int, val prefix : String = "")

private fun defaultFormat(value : Any) = when(value) {
    is Double -> Format(precision = 5)
    is Integer -> Format(precision = 0)
    else -> error("${value.javaClass}")
}

inline fun <reified T:Any> default() : T {
    return when(T::class.java) {
        java.lang.Double::class.java -> 0.0 as T
        java.lang.Integer::class.java -> 0 as T
        else -> error("${T::class.java}")
    }
}

inline fun <reified T:Any> classOf() : KClass<T> = T::class

inline fun <reified T:Any> literal(value : T, format : Format?=null) = Literal(value, T::class, format)
inline fun <reified T:Any> variable(default : T? = null) = ScalarVariable<T>(literal(default ?: default()))
inline fun <reified T:Any> matrixVariable(columns:Int, rows:Int) = MatrixVariable(columns, rows, classOf<T>())
private fun <T:Any> binaryDouble(left : Literal<T>, right : Literal<T>, map:(Double,Double) -> Double) : Expr<T> {
    val leftValue = left.value as Double
    val rightValue = right.value as Double
    val result = map(leftValue, rightValue)
    return Literal(result, Double::class, left.format?:right.format) as Expr<T>
}
operator fun <T:Any> Expr<T>.plus(right : Expr<T>) : Expr<T> = when {
    this is Literal && value == 0.0 -> right
    right is Literal && right.value == 0.0 -> this
    this is Literal && right is Literal -> binaryDouble(this, right) { l,r -> l + r }
    else -> Plus(this, right)
}
inline operator fun <reified T:Any> Expr<T>.plus(right : T) = this + literal<T>(right)
inline operator fun <reified T:Any> T.plus(right : Expr<T>) = literal(this) + right
operator fun <T:Any> Expr<T>.minus(right : Expr<T>) = when {
    right is Literal && right.value == 0.0 -> this
    this is Literal && right is Literal -> binaryDouble(this, right) { l,r -> l - r }
    else -> Minus(this, right)
}
inline operator fun <reified T:Any> Expr<T>.minus(right : T) = this - literal<T>(right)
inline operator fun <reified T:Any> T.minus(right : Expr<T>) = literal(this) - right
fun <T:Any> timesOp(left:Expr<T>, right : Expr<T>) = run {
    when {
        left is Literal && left.value == 1.0 -> right
        left is Literal && left.value == 0.0 -> left
        right is Literal && right.value == 1.0 -> left
        right is Literal && right.value == 0.0 -> right
        left is Literal && right is Literal -> binaryDouble(left, right) { l,r -> l * r }
        else -> Times(left, right)
    }
}
operator fun <T:Any> Matrix<T>.times(right : Matrix<T>) = MatrixTimes(this, right, elementType)
inline operator fun <reified T:Any> Expr<T>.times(right : Expr<T>) = timesOp(this, right)
inline operator fun <reified T:Any> Expr<T>.times(right : T) = timesOp(this, literal(right))
inline operator fun <reified T:Any> T.times(right : Expr<T>) = timesOp(literal(this), right)
operator fun <T:Any> Expr<T>.div(right : Expr<T>) = Div(this, right)
inline operator fun <reified T:Any> Expr<T>.div(right : T) = Div(this, literal(right))
inline operator fun <reified T:Any> T.div(right : Expr<T>) = Div(literal(this), right)
fun <T:Any> pow(left:Expr<T>, right:Expr<T>) : Expr<T> = run {
    when {
        right is Literal && right.value == 1.0 -> left
        right is Literal && right.value == 0.0 -> right.copy(value = 1.0 as T)
        left is Literal && left.value == 0.0 -> left
        else -> BinaryFunction(POW, left, right)
    }
}
fun pow(left:Expr<Double>, right:Double) = pow(left, literal(right))
fun <T:Any> logit(expr:Expr<T>) = UnaryFunction(LOGIT, expr)
fun logit(expr:Matrix<Double>) = UnaryMatrixFunction(LOGIT, expr)
fun <T:Any> d(expr:Expr<T>) = UnaryFunction(D, expr)
infix fun <T:Any> Matrix<T>.stack(bottom : Matrix<T>) = StackMatrix(this, bottom, elementType)
inline fun <reified T:Any> rowOf(value : T, columns : Int) = LiteralMatrix(columns, 1, literal(value))
inline infix fun <reified T:Any> Matrix<T>.stack(bottom : T) = StackMatrix(this, rowOf(bottom, columns), classOf())

fun Expr<Double>.differentiate(variable : Expr<Double>) : Expr<Double> = run {
    if (this == variable) return literal(1.0)
    return when(this) {
        is UnaryFunction ->
            when(function) {
                LOGIT -> logit(expr) * (1.0 - logit(expr)) * expr.differentiate(variable)
                else -> error("$function")
            }
        is BinaryFunction ->
            when(function) {
                POW -> right * pow(left, right - 1.0) * left.differentiate(variable)
            }

        is Plus -> left.differentiate(variable) + right.differentiate(variable)
        is Times -> left * right.differentiate(variable) + left.differentiate(variable)*right
        is Assign -> right.differentiate(variable)
        is ScalarVariable -> literal(0.0)
        else ->
            error("$javaClass")
    }
}
fun differentiate(expr : Expr<Double>) : Expr<Double> = run {
    return when {
        expr is Div &&
        expr.left is UnaryFunction && expr.left.function == D &&
        expr.right is UnaryFunction && expr.right.function == D -> {
            expr.left.expr.differentiate(expr.right.expr)
        }
        expr is Assign -> Assign("${expr.left}'", differentiate(expr.right))
        else -> error("${expr.javaClass}")
    }
}


private fun <T:Any> render(value : T, exprFormat : Format?) : String {
    assert(value !is Expr<*>)
    val format = exprFormat ?: defaultFormat(value)
    when(value) {
        is Double -> {
            val result = format.prefix + BigDecimal(value).setScale(format.precision, RoundingMode.HALF_EVEN).toString()
            if (result.contains(".")) return result.trimEnd('0').trimEnd('.')
            return result
        }
        is Integer -> return render(value.toDouble(), format)
        else -> error(value.javaClass.toString())
    }
}

private fun String.withParens(parent : Expr<*>, child : Expr<*>, right : Boolean) : String {
    return when(parent) {
        is MatrixTimes<*> -> when(child) {
            is StackMatrix<*> -> "($this)"
            else -> this
        }
        is Times<*> -> when(child) {
            is Div<*>,
            is Minus<*>,
            is Plus<*> -> "($this)"
            else ->
                this
        }
        is Div<*> -> when(child) {
            is Div<*>,
            is Minus<*>,
            is Plus<*> -> "($this)"
            else -> this
        }
        is Plus<*> -> when(child) {
            is Minus<*> -> "($this)"
            else -> this
        }
        is Minus<*> -> when(child) {
            is Minus<*> -> if (right) "($this)" else this
            else -> this
        }
        else ->
            this
    }
}

private fun <T:Any> Expr<T>.render(entryPoint : Boolean = true) : String {
    val parent = this
    fun <T:Any> Expr<T>.right() = run {
        val inner = render(false).withParens(parent, this, true)
        inner
    }
    fun <T:Any> Expr<T>.left() = run {
        val inner = render(false).withParens(parent, this, false)
        inner
    }
    return when(this) {
        is LiteralMatrix<*> -> this.literal.left()
        is ScalarVariable -> render(default.value, default.format)
        is MatrixVariable<*> -> "matrix(${columns}x$rows)"
        is Assign -> {
            when {
                right is ScalarVariable -> left
                right is MatrixVariableElement -> left
                entryPoint -> "$left="+right.right()
                !entryPoint -> right.right()
                else -> error("")
            }
        }
        is AssignMatrix<*> ->
            when {
                right is MatrixVariable ->
                    left
                entryPoint ->
                    "$left="+right.right()
                !entryPoint ->
                    right.right()
                else -> error("")
            }
        is InfixBinary -> left.left()+operator+right.right()
        is UnaryFunction -> function.name.toLowerCase() + "("+expr.left()+")"
        is UnaryMatrixFunction<*> -> function.name.toLowerCase() + "("+expr.left()+")"
        is BinaryFunction -> function.name.toLowerCase() + "("+left.left()+","+right.right()+")"
        is Literal -> render(value, format)
        is NamedMatrixVariableElement -> "$name[$column,$row]"
        is StackMatrix<*> -> "${this.top.left()} stack ${bottom.right()}"
        else -> error("$javaClass")
    }
}
