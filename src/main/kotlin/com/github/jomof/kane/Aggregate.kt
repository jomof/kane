package com.github.jomof.kane

import com.github.jomof.kane.impl.Identifier
import com.github.jomof.kane.*
import com.github.jomof.kane.NamedExpr
import com.github.jomof.kane.impl.functions.AggregatableFunction
import com.github.jomof.kane.impl.functions.AlgebraicSummaryFunction
import com.github.jomof.kane.impl.sheet.GroupBy
import com.github.jomof.kane.impl.sheet.Sheet
import com.github.jomof.kane.impl.sheet.aggregate
import com.github.jomof.kane.impl.sheet.fullColumnDescriptor
import com.github.jomof.kane.impl.toNamed

fun GroupBy.aggregate(vararg aggregatables: AggregatableFunction): Sheet {
    val evaled = eval()
    return evaled.aggregate {
        val result = mutableListOf<NamedExpr>()
        val functions = aggregatables.map { it as AlgebraicSummaryFunction }
        for (column in 0 until evaled.sheet.columns) {
            val columnInfo = evaled.sheet.fullColumnDescriptor(column)
            if (columnInfo.type!!.type.java != Double::class.java) continue
            for (function in functions) {
                result += function.invoke(range(Identifier.string(columnInfo.name)))
                    .toNamed(function.meta.op + " " + columnInfo.name)
            }
        }
        result
    }
}