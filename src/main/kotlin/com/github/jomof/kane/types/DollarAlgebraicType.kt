package com.github.jomof.kane.types

import com.github.jomof.kane.ConstantScalar
import com.github.jomof.kane.ScalarExpr
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import kotlin.math.abs

class DollarAlgebraicType() : AlgebraicType(Double::class.java) {
    override val simpleName = "double"
    override fun render(value: Double): String {
        if (value.isInfinite()) return "Infinite"
        if (value.isNaN()) return "NaN"
        val result = NumberFormat.getCurrencyInstance().format(abs(value)) //BigDecimal(abs(value)).setScale(2, RoundingMode.HALF_EVEN).toString()
        return if (value < 0.0) "($result)" else result
    }
    override fun coerceFrom(value : Number) = value.toDouble()

    companion object {
        val kaneType = DollarAlgebraicType()
    }
}
fun dollars(value : Double) : ScalarExpr = ConstantScalar(value, DollarAlgebraicType.kaneType)
fun dollars(value : Int) : ScalarExpr = ConstantScalar(value.toDouble(), DollarAlgebraicType.kaneType)