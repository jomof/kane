package com.github.jomof.kane.types

import com.github.jomof.kane.ConstantScalar
import com.github.jomof.kane.ScalarExpr
import java.text.NumberFormat

class PercentAlgebraicType : AlgebraicType(Double::class.java) {
    override val simpleName = "percent"
    override fun render(value: Double): String {
        if (value.isInfinite()) return "Infinite"
        if (value.isNaN()) return "NaN"
        val result = NumberFormat.getPercentInstance().format(value)
        return if (value < 0.0) "($result)" else result
    }
    override fun coerceFrom(value : Number) = value.toDouble()

    companion object {
        val kaneType = PercentAlgebraicType()
    }
}
fun <E:Number> percent(value : E) : ScalarExpr = ConstantScalar(value.toDouble(), PercentAlgebraicType.kaneType)
