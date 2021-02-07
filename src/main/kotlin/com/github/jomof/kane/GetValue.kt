package com.github.jomof.kane

import com.github.jomof.kane.impl.*
import com.github.jomof.kane.impl.sheet.analyzeDataListTypes
import com.github.jomof.kane.impl.sheet.parseAndNameValue
import com.github.jomof.kane.impl.sheet.parseToExpr
import com.github.jomof.kane.impl.types.kaneType
import kotlin.reflect.KProperty

operator fun String.getValue(nothing: Nothing?, property: KProperty<*>) = parseAndNameValue(property.name, this)
operator fun Number.getValue(nothing: Nothing?, property: KProperty<*>) =
    NamedScalar(property.name, constant(this.toDouble()))

operator fun List<Double>.getValue(nothing: Nothing?, property: KProperty<*>) =
    NamedMatrix(property.name, matrixOf(1, size, *toDoubleArray()))

operator fun List<String>.getValue(nothing: Nothing?, property: KProperty<*>): NamedExpr {
    val admissibleType = analyzeDataListTypes(this)
    val result: List<Expr> = map { admissibleType.parseToExpr(it) }
    return when {
        result.all { it is ScalarExpr } -> {
            NamedMatrix(property.name, matrixOf(1, size) { result[it.column] as ScalarExpr })
        }
        else -> NamedTiling(property.name, Tiling(1, size, this, String::class.java.kaneType))
    }
}

