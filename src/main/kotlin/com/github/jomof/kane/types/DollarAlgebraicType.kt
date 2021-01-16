package com.github.jomof.kane.types

import com.github.jomof.kane.ConstantScalar
import com.github.jomof.kane.ScalarExpr

val dollarAlgebraicType  = DoubleAlgebraicType(prefix = "$", precision = 2, trimLeastSignificantZeros = false)
fun dollars(value : Double) : ScalarExpr = ConstantScalar(value, dollarAlgebraicType)
fun dollars(value : Int) : ScalarExpr = ConstantScalar(value.toDouble(), dollarAlgebraicType)