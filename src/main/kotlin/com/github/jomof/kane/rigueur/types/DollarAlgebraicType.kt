package com.github.jomof.kane.rigueur.types

import com.github.jomof.kane.rigueur.ConstantScalar

val dollarAlgebraicType  = DoubleAlgebraicType(prefix = "$", precision = 2, trimLeastSignificantZeros = false)
fun dollars(value : Double) = ConstantScalar(value, dollarAlgebraicType)
fun dollars(value : Int) = ConstantScalar(value.toDouble(), dollarAlgebraicType)