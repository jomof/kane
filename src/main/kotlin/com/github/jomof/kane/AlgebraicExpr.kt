package com.github.jomof.kane

import com.github.jomof.kane.impl.MatrixExpr
import com.github.jomof.kane.impl.ScalarExpr
import com.github.jomof.kane.impl.toNamed
import kotlin.reflect.KProperty

operator fun ScalarExpr.getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
operator fun MatrixExpr.getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
