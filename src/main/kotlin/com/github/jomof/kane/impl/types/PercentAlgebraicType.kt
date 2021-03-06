package com.github.jomof.kane.impl.types

import com.github.jomof.kane.MatrixExpr
import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.impl.ConstantScalar
import com.github.jomof.kane.impl.RetypeMatrix
import com.github.jomof.kane.impl.RetypeScalar
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
