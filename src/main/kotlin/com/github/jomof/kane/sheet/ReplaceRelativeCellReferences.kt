package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.functions.*
import com.github.jomof.kane.visitor.RewritingVisitor


private fun Expr.replaceRelativeCellReferencesOld(coordinate: Coordinate): Expr {
    fun MatrixExpr.self(coordinate: Coordinate) =
        replaceRelativeCellReferencesOld(coordinate) as MatrixExpr

    fun ScalarExpr.self(coordinate: Coordinate) =
        replaceRelativeCellReferencesOld(coordinate) as ScalarExpr

    fun UntypedScalar.self(coordinate: Coordinate) =
        replaceRelativeCellReferencesOld(coordinate) as UntypedScalar
    return when (this) {
        is NamedUntypedScalar -> {
            val result = if (looksLikeCellName(name)) {
                val new = cellNameToCoordinate(name)
                copy(expr = expr.self(new))
            } else copy(expr = expr.self(coordinate))
            result
        }
        is NamedScalar -> {
            val result: AlgebraicExpr = if (looksLikeCellName(name)) {
                val new = cellNameToCoordinate(name)
                copy(scalar = scalar.self(new))
            } else copy(scalar = scalar.self(coordinate))
            result
        }
        is NamedMatrix -> {
            val new = cellNameToCoordinate(name.toUpperCase())
            copy(matrix = matrix.self(new))
        }
        is AlgebraicUnaryScalar -> copy(value = value.self(coordinate))
        is AlgebraicUnaryMatrix -> copy(value = value.self(coordinate))
        is AlgebraicUnaryScalarStatistic -> copy(value = value.replaceRelativeCellReferencesOld(coordinate) as AlgebraicExpr)
        is AlgebraicUnaryMatrixScalar -> copy(value = value.self(coordinate))
        is AlgebraicBinaryScalarMatrix -> copy(
            left = left.self(coordinate),
            right = right.self(coordinate))
        is AlgebraicBinaryScalar -> copy(
            left = left.self(coordinate),
            right = right.self(coordinate))
        is AlgebraicBinaryMatrixScalar -> copy(
            left = left.self(coordinate),
            right = right.self(coordinate)
        )
        is AlgebraicBinaryMatrix -> copy(
            left = left.self(coordinate),
            right = right.self(coordinate)
        )
        is AlgebraicBinaryScalarStatistic -> copy(
            left = left.self(coordinate),
            right = right.self(coordinate)
        )
        is ConstantScalar -> this
        is DiscreteUniformRandomVariable -> this
        is NamedScalarVariable -> this
        is CoerceScalar -> copy(value = value.replaceRelativeCellReferencesOld(coordinate))
        is SheetRangeExpr -> copy(range = range.rebase(coordinate))
        is SheetBuilderRange -> SheetRangeExpr(range = range.rebase(coordinate))
        is NamedSheetRangeExpr -> copy(range = range.replaceRelativeCellReferencesOld(coordinate) as SheetRangeExpr)
        is AlgebraicBinaryRangeStatistic -> copy(
            left = left.copy(range = left.range.rebase(coordinate)),
            right = right.self(coordinate)
        )
        //is AlgebraicUnaryRangeStatistic -> copy(range = range.replaceRelativeCellReferencesOld(coordinate) as SheetRangeExpr)
        is DataMatrix -> map { it.self(coordinate) }
        is ValueExpr<*> -> this
        is Tiling<*> -> this
        else -> error("$javaClass")
    }
}

private fun Expr.replaceRelativeCellReferencesNew(coordinate: Coordinate): Expr {
    fun MatrixExpr.self(coordinate: Coordinate) = replaceRelativeCellReferencesNew(coordinate) as MatrixExpr
    return object : RewritingVisitor() {
        override fun SheetRangeExpr.rewrite() = copy(range = range.rebase(coordinate))
        override fun NamedMatrix.rewrite() = copy(matrix = matrix.self(cellNameToCoordinate(name)))
    }.rewrite(this)
}

fun Expr.replaceRelativeCellReferences(coordinate: Coordinate): Expr {
    val old = replaceRelativeCellReferencesOld(coordinate)
    val new = replaceRelativeCellReferencesNew(coordinate)

    assert(old == new) {
        "different"
    }
    return new
}
