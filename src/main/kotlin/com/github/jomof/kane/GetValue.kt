package com.github.jomof.kane

import com.github.jomof.kane.impl.*
import com.github.jomof.kane.impl.sheet.parseAndNameValue
import com.github.jomof.kane.impl.types.kaneType
import kotlin.reflect.KProperty


operator fun IntRange.getValue(nothing: Nothing?, property: KProperty<*>) =
    toList().getValue(nothing, property)

operator fun String.getValue(nothing: Nothing?, property: KProperty<*>) =
    parseAndNameValue(property.name, this)

operator fun Number.getValue(nothing: Nothing?, property: KProperty<*>) =
    NamedScalar(property.name, constant(this.toDouble()))

operator fun <E : Number> List<E>.getValue(nothing: Nothing?, property: KProperty<*>) =
    NamedMatrix(property.name, DataMatrix(1, size, map { constant(it.toDouble()) }))

inline operator fun <reified E : Any> List<E>.getValue(nothing: Nothing?, property: KProperty<*>) =
    NamedTiling(property.name, Tiling(1, size, this, E::class.java.kaneType))

@JvmName("getValueScalarExpr")
operator fun List<ScalarExpr>.getValue(nothing: Nothing?, property: KProperty<*>) =
    NamedMatrix(property.name, DataMatrix(1, size, this))


