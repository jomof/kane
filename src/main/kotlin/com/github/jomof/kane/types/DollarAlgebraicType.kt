package com.github.jomof.kane.types

import com.github.jomof.kane.*
import java.text.NumberFormat
import java.util.*
import kotlin.math.abs

class DollarsAndCentsAlgebraicType : AlgebraicType(Double::class.java) {
    override val simpleName = "dollars and cents"
    override fun render(value: Double): String {
        if (value.isInfinite()) return "Infinite"
        if (value.isNaN()) return ""
        val result = NumberFormat.getCurrencyInstance(Locale.US).format(abs(value))
        return if (value < 0.0) "($result)" else result
    }
    override fun coerceFrom(value : Number) = value.toDouble()

    companion object {
        val kaneType = DollarsAndCentsAlgebraicType()
    }
}

class DollarAlgebraicType : AlgebraicType(Double::class.java) {
    override val simpleName = "dollar"
    override fun render(value: Double): String {
        if (value.isInfinite()) return "Infinite"
        if (value.isNaN()) return ""
        val result = NumberFormat.getCurrencyInstance(Locale.US).format(abs(value)).substringBefore(".")
        return if (value < 0.0) "($result)" else result
    }

    override fun coerceFrom(value: Number) = value.toDouble()

    companion object {
        val kaneType = DollarAlgebraicType()
    }
}

fun dollars(value: Double): ScalarExpr = RetypeScalar(ConstantScalar(value), DollarsAndCentsAlgebraicType.kaneType)
fun dollars(value: Int): ScalarExpr =
    RetypeScalar(ConstantScalar(value.toDouble()), DollarsAndCentsAlgebraicType.kaneType)

fun dollars(value: MatrixExpr) = RetypeMatrix(value, DollarsAndCentsAlgebraicType.kaneType)
fun dollars(value: ScalarExpr) = RetypeScalar(value, DollarsAndCentsAlgebraicType.kaneType)
