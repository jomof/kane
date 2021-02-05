package com.github.jomof.kane.types

import com.github.jomof.kane.*
import java.text.NumberFormat

class PercentAlgebraicType : AlgebraicType(Double::class.java) {
    override val simpleName = "percent"
    override fun render(value: Double): String {
        if (value.isInfinite()) return "Infinite"
        if (value.isNaN()) return "NaN"
        val result = NumberFormat.getPercentInstance().format(value)
        return if (value < 0.0) "($result)" else result
    }

    override fun coerceFrom(value: Number) = value.toDouble()

    companion object {
        val kaneType = PercentAlgebraicType()
    }
}

fun percent(value: Double): ScalarExpr = RetypeScalar(ConstantScalar(value), PercentAlgebraicType.kaneType)
fun percent(value: Int): ScalarExpr = RetypeScalar(ConstantScalar(value.toDouble()), PercentAlgebraicType.kaneType)
fun percent(value: MatrixExpr) = RetypeMatrix(value, PercentAlgebraicType.kaneType)
fun percent(value: ScalarExpr) = RetypeScalar(value, PercentAlgebraicType.kaneType)
