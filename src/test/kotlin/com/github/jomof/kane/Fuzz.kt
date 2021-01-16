package com.github.jomof.kane

import com.github.jomof.kane.functions.*
import com.github.jomof.kane.types.AlgebraicType
import com.github.jomof.kane.types.DoubleAlgebraicType
import kotlin.random.Random

private val interestingTypes = listOf(
    ConstantScalar::class.java,
    ValueExpr::class.java,
    AlgebraicBinaryScalar::class.java,
    AlgebraicUnaryMatrixScalar::class.java,
    AlgebraicUnaryScalar::class.java,
    NamedScalarAssign::class.java,
    NamedMatrixVariable::class.java,
    NamedMatrix::class.java,
    AlgebraicBinaryMatrixScalar::class.java,
    Tableau::class.java,
    AlgebraicBinaryScalarMatrix::class.java,
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

fun Random.nextAlgebraic(type : AlgebraicType) : Double = nextInstance(type.java)
fun Random.nextAlgebraicType() : AlgebraicType =
    chooseOne(listOf(DoubleAlgebraicType.kaneType))

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

fun Random.nextAlgebraicUnaryMatrixScalarOp() = chooseOne(listOf(summation))
fun Random.nextAlgebraicUnaryOp() = chooseOne(listOf(negate, exp, logit, tanh, relu, step, lrelu, lstep))
fun Random.nextBinaryOp() = chooseOne(listOf())
fun Random.nextAlgebraicBinaryScalarOp() = chooseOne(listOf(pow, multiply, divide, add, subtract))

fun Random.nextAlgebraicUnaryScalar(type : AlgebraicType) = AlgebraicUnaryScalar(nextAlgebraicUnaryOp(), nextScalarExpr(type))

fun Random.nextAlgebraicBinaryScalar(type : AlgebraicType) : AlgebraicBinaryScalar =
    AlgebraicBinaryScalar(nextAlgebraicBinaryScalarOp(), nextScalarExpr(type), nextScalarExpr(type))

fun Random.nextAlgebraicUnaryMatrixScalar(type : AlgebraicType) : AlgebraicUnaryMatrixScalar =
    AlgebraicUnaryMatrixScalar(
        nextAlgebraicUnaryMatrixScalarOp(),
        nextMatrixExpr(nextInt(1,5), nextInt(1,5), type))

fun Random.nextAlgebraicBinaryMatrixScalar(
    columns : Int = nextInt(1, 5),
    rows : Int = nextInt(1,5),
    type : AlgebraicType) : AlgebraicBinaryMatrixScalar {
    return AlgebraicBinaryMatrixScalar(
        nextAlgebraicBinaryScalarOp(),
        columns,
        rows,
        nextMatrixExpr(columns, rows, type),
        nextScalarExpr(type)
    )
}

fun Random.nextAlgebraicBinaryScalarMatrix(
     columns : Int = nextInt(1, 5),
     rows : Int = nextInt(1,5),
     type : AlgebraicType) : AlgebraicBinaryScalarMatrix {
    return AlgebraicBinaryScalarMatrix(
        nextAlgebraicBinaryScalarOp(),
        columns,
        rows,
        nextScalarExpr(type),
        nextMatrixExpr(columns, rows, type)
    )
}

fun Random.nextScalarExpr(type : AlgebraicType) : ScalarExpr {
    if (nextDouble(0.0, 1.0) < 0.4) return constant(nextAlgebraic(type))
    val method = chooseOne(interfaceSpecs.getValue(ScalarExpr::class.java))
    return dispatchExpr(method, type) as ScalarExpr
}

fun Random.nextMatrixExpr(columns : Int, rows : Int, type : AlgebraicType) : MatrixExpr {
    if (nextDouble(0.0, 1.0) < 0.4) return matrixOf(columns, rows) { constant(nextAlgebraic(type)) }
    val method = chooseOne(interfaceSpecs.getValue(MatrixExpr::class.java))
    val result = when(method) {
        NamedMatrix::class.java -> nextNamedMatrix(columns, rows, type)
        AlgebraicBinaryScalarMatrix::class.java -> nextAlgebraicBinaryScalarMatrix(columns, rows, type)
        NamedMatrixVariable::class.java -> nextNamedMatrixVariable(columns, rows, type)
        AlgebraicBinaryMatrixScalar::class.java -> nextAlgebraicBinaryMatrixScalar(columns, rows, type)
        else ->
            error("$method")
    }
    assert(result.javaClass == method) {
        "expected $method but was ${result.javaClass}"
    }
    return result
}

fun Random.nextNamedMatrixVariable(
    columns : Int = nextInt(1, 5),
    rows : Int = nextInt(1, 5),
    type : AlgebraicType) : NamedMatrixVariable {
    return NamedMatrixVariable(nextIdentifier(), columns, type, (0 until rows * columns).map { nextInstance(type.java) })
}

fun Random.nextNamedScalarVariable(type : AlgebraicType) =
    NamedScalarVariable(nextIdentifier(), nextInstance(type.java), type)

fun Random.nextScalarVariable(type : AlgebraicType) =
    ScalarVariable(nextInstance(type.java), type)

fun Random.nextNamedScalar(type : AlgebraicType) =
    NamedScalar(nextIdentifier(), nextScalarExpr(type))

fun Random.nextNamedMatrix(
    columns : Int = nextInt(1, 5),
    rows : Int = nextInt(1,5),
    type : AlgebraicType) =
    NamedMatrix(nextIdentifier(), nextMatrixExpr(
        columns,
        rows,
        type))

fun Random.nextNamedScalarExpr(type : AlgebraicType) : NamedScalarExpr {
    val method = chooseOne(interfaceSpecs.getValue(NamedScalarExpr::class.java))
    return dispatchExpr(method, nextAlgebraicType()) as NamedScalarExpr
}

fun Random.nextNamedScalarAssign(type : AlgebraicType) =
    NamedScalarAssign(
        "assign_scalar_" + nextInt(0, 1000000),
        nextNamedScalarVariable(type),
        nextScalarExpr(type))

fun Random.nextNamedMatrixAssign(
    columns : Int = nextInt(1, 5),
    rows : Int = nextInt(1, 5),
    type : AlgebraicType) : NamedMatrixAssign {
    return NamedMatrixAssign(
        "assign_matrix_" + nextInt(0, 1000000),
        nextNamedMatrixVariable(columns, rows, type),
        nextMatrixExpr(columns, rows, type)
    )
}

fun Random.nextTableau(type : AlgebraicType) =
    Tableau((0 until nextInt(0, 10)).map { nextNamedAlgebraicExpr(type) })

fun Random.nextNamedAlgebraicExpr(type : AlgebraicType) : NamedAlgebraicExpr {
    val method = chooseOne(interfaceSpecs.getValue(NamedAlgebraicExpr::class.java))
    return dispatchExpr(method, type) as NamedAlgebraicExpr
}

fun Random.nextAlgebraicExpr(type : AlgebraicType) : AlgebraicExpr {
    val method = chooseOne(interfaceSpecs.getValue(AlgebraicExpr::class.java))
    return dispatchExpr(method, type) as AlgebraicExpr
}

private fun Random.dispatchExpr(exprType : Class<*>, type : AlgebraicType) : Expr {
    val result = when(exprType) {
        ConstantScalar::class.java -> {
            val constant = nextInstance<Double>(type.java)
            ConstantScalar(constant, type)
        }
        ValueExpr::class.java -> {
            val constant = nextInstance<Double>(type.java)
            ValueExpr(constant, type)
        }
        ScalarVariable::class.java -> nextScalarVariable(type)
        NamedScalarVariable::class.java -> nextNamedScalarVariable(type)
        NamedMatrixVariable::class.java -> nextNamedMatrixVariable(type = type)
        NamedScalarAssign::class.java -> nextNamedScalarAssign(type)
        NamedMatrixAssign::class.java -> nextNamedMatrixAssign(type = type)
        NamedMatrix::class.java -> nextNamedMatrix(type = type)
        AlgebraicBinaryMatrixScalar::class.java -> nextAlgebraicBinaryMatrixScalar(type = type)
        AlgebraicBinaryScalarMatrix::class.java -> nextAlgebraicBinaryScalarMatrix(type = type)
        AlgebraicBinaryScalar::class.java -> nextAlgebraicBinaryScalar(type)
        AlgebraicUnaryMatrixScalar::class.java -> nextAlgebraicUnaryMatrixScalar(type)
        AlgebraicUnaryScalar::class.java -> nextAlgebraicUnaryScalar(type)
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

fun Random.nextMatrixExpr(columns : Int, rows : Int) =
    nextMatrixExpr(columns, rows, DoubleAlgebraicType.kaneType)

fun Random.nextNamedScalarExpr() =
    nextNamedScalarExpr(DoubleAlgebraicType.kaneType)

fun Random.nextNamedAlgebraicExpr() =
    nextNamedAlgebraicExpr(DoubleAlgebraicType.kaneType)

fun Random.nextAlgebraicExpr() =
    nextAlgebraicExpr(DoubleAlgebraicType.kaneType)