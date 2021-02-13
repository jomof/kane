package com.github.jomof.kane

import com.github.jomof.kane.impl.functions.AlgebraicUnaryScalarFunction
import com.github.jomof.kane.impl.render

data class AlgebraicUnaryScalar(
    val op: AlgebraicUnaryScalarFunction,
    val value: ScalarExpr
) : ScalarExpr {
    override fun toString() = render()
}