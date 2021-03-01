package com.github.jomof.kane

import com.github.jomof.kane.impl.getColumnDescriptors
import com.github.jomof.kane.impl.sheet.Sheet
import com.github.jomof.kane.impl.sheet.SheetRangeExpr
import com.github.jomof.kane.impl.sheet.columnType
import com.github.jomof.kane.impl.types.KaneType
import com.github.jomof.kane.impl.types.algebraicType
import com.github.jomof.kane.impl.types.kaneType

/**
 * Get the type of an expression.
 */
val Expr.type: KaneType<*>
    get() = when (this) {
        is Sheet -> if (columns == 1) columnType(0).type else String::class.java.kaneType
        is SheetRangeExpr -> String::class.java.kaneType
        is AlgebraicExpr -> algebraicType
        else -> error("$javaClass")
    }

val Sequence<Row>.type: KaneType<*>
    get() = getColumnDescriptors().toList().single().second.type?.type!!
