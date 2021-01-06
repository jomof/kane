package com.github.jomof.kane.rigueur.types

import com.github.jomof.kane.rigueur.ConstantScalar
import com.github.jomof.kane.rigueur.ScalarExpr

val dollarAlgebraicType  = DoubleAlgebraicType(prefix = "$", precision = 2, trimLeastSignificantZeros = false)
fun dollars(value : Double) : ScalarExpr<Double> = ConstantScalar(value, dollarAlgebraicType)
fun dollars(value : Int) : ScalarExpr<Double> = ConstantScalar(value.toDouble(), dollarAlgebraicType)