package com.github.jomof.kane.rigueur

import com.github.jomof.kane.rigueur.types.AlgebraicType
import com.github.jomof.kane.rigueur.types.DoubleAlgebraicType
import com.github.jomof.kane.rigueur.types.FloatAlgebraicType
import com.github.jomof.kane.rigueur.types.kaneType
import kotlin.random.Random

private val interestingTypes = listOf(
    ConstantScalar::class.java,
    ValueExpr::class.java,
    BinaryScalar::class.java,
    UnaryScalar::class.java,
    UnaryMatrix::class.java,
    UnaryMatrixScalar::class.java,
    NamedScalarAssign::class.java,
    NamedMatrixVariable::class.java,
    NamedMatrix::class.java,
    BinaryMatrixScalar::class.java,
    Tableau::class.java,
    BinaryScalarMatrix::class.java,
    BinaryMatrix::class.java,
    ScalarVariable::class.java,
    NamedMatrixAssign::class.java,
    NamedScalarVariable::class.java,
    NamedScalar::class.java,
)

private fun Class<*>.interfacesTransitive(set : MutableSet<Class<*>> = mutableSetOf()) : Set<Class<*>> {
    if (set.contains(this)) return set
    if (isInterface) set += this
    interfaces.forEach { it.interfacesTransitive(set) }
    return set
}

private val interfaceSpecs = interestingTypes
    .onEach { assert(!it.isInterface) {
        "$it is interface, should be class"
        } }
    .flatMap { type -> type.interfacesTransitive().map { it to type } }
    .groupBy { it.first }
    .map { it.key to it.value.map { x -> x.second } }
    .toMap()

fun <E:Any> Random.chooseOne(choices : List<E>) = choices[nextInt(choices.count())]
fun <E:Any> Random.chooseOne(choices : Array<E>) = choices[nextInt(choices.count())]

fun <E:Number> Random.nextAlgebraic(type : AlgebraicType<E>) : E = nextInstance(type.java)
fun Random.nextAlgebraicType() : AlgebraicType<*> =
    chooseOne(listOf(DoubleAlgebraicType.kaneType, FloatAlgebraicType.kaneType))

fun Random.nextIdentifier() = chooseOne(listOf("a", "b", "c")) + nextInt(0, 2000)

fun <E:Any> Random.nextInstance(type : Class<*>) : E {
    return when(type) {
        java.lang.Double::class.java -> nextDouble()
        Double::class.java -> nextDouble()
        Float::class.java -> nextDouble().toFloat()
        Int::class.java -> nextInt()
        else ->
            error("x")
    } as E
}

fun <E:Any> Random.nextListOf(length : Int, type : Class<*>) : List<E> = (0 until length).map { nextInstance(type) }

fun Random.nextUnaryOp() = chooseOne(listOf(D, SUMMATION, LOGIT, TANH, RELU, LRELU, EXP, STEP, LSTEP, NEGATE))
fun Random.nextBinaryOp() = chooseOne(listOf(POW, TIMES, DIV, PLUS, MINUS, CROSS, STACK))
fun Random.nextBinaryScalarOp() = chooseOne(listOf(POW, TIMES, DIV, PLUS, MINUS))

fun <E:Number> Random.nextUnaryMatrix(
    columns : Int = nextInt(1, 5),
    rows : Int = nextInt(1, 5),
    type : AlgebraicType<E>) =
    UnaryMatrix(nextUnaryOp(), nextMatrixExpr(columns, rows, type))

fun <E:Number> Random.nextBinaryMatrix(
    columns : Int = nextInt(1, 5),
    rows : Int = nextInt(1, 5),
    type : AlgebraicType<E>) : BinaryMatrix<E> {
    val op = nextBinaryOp()
    return when {
        op == CROSS -> {
            val leftColumns = nextInt(1, 5)
            val result = nextMatrixExpr(leftColumns, rows, type) cross nextMatrixExpr(columns, leftColumns, type)
            assert(result.columns == columns) {
                "expected $columns but was ${result.columns}"
            }
            assert(result.rows == rows) {
                "expected $rows but was ${result.rows}"
            }
            result
        }
        op == STACK && rows > 1 -> {
            val bottomRows = nextInt(1, rows)
            val topRows = rows - bottomRows
            nextMatrixExpr(columns, topRows, type) stack nextMatrixExpr(columns, bottomRows, type)
        }
        else -> BinaryMatrix(
            op,
            columns,
            rows,
            nextMatrixExpr(columns, rows, type),
            nextMatrixExpr(columns, rows, type)
        )
    }
}

fun <E:Number> Random.nextUnaryScalar(type : AlgebraicType<E>) : UnaryScalar<E> =
    UnaryScalar(nextUnaryOp(), nextScalarExpr(type))

fun <E:Number> Random.nextBinaryScalar(type : AlgebraicType<E>) : BinaryScalar<E> =
    BinaryScalar(nextBinaryScalarOp(), nextScalarExpr(type), nextScalarExpr(type))

fun <E:Number> Random.nextUnaryMatrixScalar(type : AlgebraicType<E>) : UnaryMatrixScalar<E> =
    UnaryMatrixScalar(nextUnaryOp(), nextMatrixExpr(
        columns = nextInt(1, 5),
        rows = nextInt(1, 5),
        type))

fun <E:Number> Random.nextBinaryMatrixScalar(
    columns : Int = nextInt(1, 5),
    rows : Int = nextInt(1,5),
    type : AlgebraicType<E>) : BinaryMatrixScalar<E> {
    return BinaryMatrixScalar(
        nextBinaryScalarOp(),
        columns,
        rows,
        nextMatrixExpr(columns, rows, type),
        nextScalarExpr(type)
    )
}

fun <E:Number> Random.nextBinaryScalarMatrix(
     columns : Int = nextInt(1, 5),
     rows : Int = nextInt(1,5),
     type : AlgebraicType<E>) : BinaryScalarMatrix<E> {
    return BinaryScalarMatrix(
        nextBinaryScalarOp(),
        columns,
        rows,
        nextScalarExpr(type),
        nextMatrixExpr(columns, rows, type)
    )
}

fun <E:Number> Random.nextScalarExpr(type : AlgebraicType<E>) : ScalarExpr<E> {
    if (nextDouble(0.0, 1.0) < 0.4) return constant(nextAlgebraic(type))
    val method = chooseOne(interfaceSpecs.getValue(ScalarExpr::class.java))
    return dispatchExpr(method, type) as ScalarExpr<E>
}

fun <E:Number> Random.nextMatrixExpr(columns : Int, rows : Int, type : AlgebraicType<E>) : MatrixExpr<E> {
    if (nextDouble(0.0, 1.0) < 0.4) return matrixOf(columns, rows) { constant(nextAlgebraic(type)) }
    val method = chooseOne(interfaceSpecs.getValue(MatrixExpr::class.java))
    val result = when(method) {
        NamedMatrix::class.java -> nextNamedMatrix(columns, rows, type)
        BinaryScalarMatrix::class.java -> nextBinaryScalarMatrix(columns, rows, type)
        UnaryMatrix::class.java -> nextUnaryMatrix(columns, rows, type)
        BinaryMatrix::class.java -> nextBinaryMatrix(columns, rows, type)
        NamedMatrixVariable::class.java -> nextNamedMatrixVariable(columns, rows, type)
        BinaryMatrixScalar::class.java -> nextBinaryMatrixScalar(columns, rows, type)
        else ->
            error("$method")
    }
    assert(result.javaClass == method) {
        "expected $method but was ${result.javaClass}"
    }
    return result
}

fun <E:Number> Random.nextNamedMatrixVariable(
    columns : Int = nextInt(1, 5),
    rows : Int = nextInt(1, 5),
    type : AlgebraicType<E>) : NamedMatrixVariable<E> {
    return NamedMatrixVariable(nextIdentifier(), columns, type, (0 until rows * columns).map { nextInstance(type.java) })
}

fun <E:Number> Random.nextNamedScalarVariable(type : AlgebraicType<E>) =
    NamedScalarVariable(nextIdentifier(), nextInstance(type.java), type)

fun <E:Number> Random.nextScalarVariable(type : AlgebraicType<E>) =
    ScalarVariable(nextInstance(type.java), type)

fun <E:Number> Random.nextNamedScalar(type : AlgebraicType<E>) =
    NamedScalar(nextIdentifier(), nextScalarExpr(type))

fun <E:Number> Random.nextNamedMatrix(
    columns : Int = nextInt(1, 5),
    rows : Int = nextInt(1,5),
    type : AlgebraicType<E>) =
    NamedMatrix(nextIdentifier(), nextMatrixExpr(
        columns,
        rows,
        type))

fun <E:Number> Random.nextNamedScalarExpr(type : AlgebraicType<E>) : NamedScalarExpr<E> {
    val method = chooseOne(interfaceSpecs.getValue(NamedScalarExpr::class.java))
    return dispatchExpr(method, nextAlgebraicType()) as NamedScalarExpr<E>
}

fun <E:Number> Random.nextNamedScalarAssign(type : AlgebraicType<E>) =
    NamedScalarAssign(
        "assign_scalar_" + nextInt(0, 1000000),
        nextNamedScalarVariable(type),
        nextScalarExpr(type))

fun <E:Number> Random.nextNamedMatrixAssign(
    columns : Int = nextInt(1, 5),
    rows : Int = nextInt(1, 5),
    type : AlgebraicType<E>) : NamedMatrixAssign<E> {
    return NamedMatrixAssign(
        "assign_matrix_" + nextInt(0, 1000000),
        nextNamedMatrixVariable(columns, rows, type),
        nextMatrixExpr(columns, rows, type)
    )
}

fun <E:Number> Random.nextTableau(type : AlgebraicType<E>) =
    Tableau((0 until nextInt(0, 10)).map { nextNamedAlgebraicExpr(type) })

fun <E:Number> Random.nextNamedAlgebraicExpr(type : AlgebraicType<E>) : NamedAlgebraicExpr<E> {
    val method = chooseOne(interfaceSpecs.getValue(NamedAlgebraicExpr::class.java))
    return dispatchExpr(method, type) as NamedAlgebraicExpr<E>
}

fun <E:Number> Random.nextAlgebraicExpr(type : AlgebraicType<E>) : AlgebraicExpr<E> {
    val method = chooseOne(interfaceSpecs.getValue(AlgebraicExpr::class.java))
    return dispatchExpr(method, type) as AlgebraicExpr<E>
}

private fun <E:Number> Random.dispatchExpr(exprType : Class<*>, type : AlgebraicType<E>) : Expr {
    val result = when(exprType) {
        ConstantScalar::class.java -> {
            val constant = nextInstance<E>(type.java)
            ConstantScalar(constant, type)
        }
        ValueExpr::class.java -> {
            val constant = nextInstance<E>(type.java)
            ValueExpr(constant, type)
        }
        ScalarVariable::class.java -> nextScalarVariable(type)
        NamedScalarVariable::class.java -> nextNamedScalarVariable(type)
        NamedMatrixVariable::class.java -> nextNamedMatrixVariable(type = type)
        NamedScalarAssign::class.java -> nextNamedScalarAssign(type)
        NamedMatrixAssign::class.java -> nextNamedMatrixAssign(type = type)
        NamedMatrix::class.java -> nextNamedMatrix(type = type)
        UnaryMatrix::class.java -> nextUnaryMatrix(type = type)
        UnaryMatrixScalar::class.java -> nextUnaryMatrixScalar(type)
        BinaryMatrixScalar::class.java -> nextBinaryMatrixScalar(type = type)
        BinaryScalarMatrix::class.java -> nextBinaryScalarMatrix(type = type)
        UnaryScalar::class.java -> nextUnaryScalar(type)
        BinaryScalar::class.java -> nextBinaryScalar(type)
        BinaryMatrix::class.java -> nextBinaryMatrix(type = type)
        Tableau::class.java -> nextTableau(type)
        NamedScalar::class.java -> nextNamedScalar(type)
        else -> error("$exprType")
    }
    assert(result.javaClass == exprType) {
        "expected $exprType but was ${result.javaClass}"
    }
    return result
}

fun Random.nextExpr() : Expr {
    val exprType = chooseOne(interfaceSpecs.getValue(Expr::class.java))
    return dispatchExpr(exprType, nextAlgebraicType())
}


inline fun <reified E:Number> Random.nextMatrixExpr(columns : Int, rows : Int) =
    nextMatrixExpr(columns, rows, E::class.kaneType)

inline fun <reified E:Number> Random.nextNamedScalarExpr() =
    nextNamedScalarExpr( E::class.kaneType)

inline fun <reified E:Number> Random.nextNamedAlgebraicExpr() =
    nextNamedAlgebraicExpr( E::class.kaneType)

inline fun <reified E:Number> Random.nextAlgebraicExpr() =
    nextAlgebraicExpr( E::class.kaneType)