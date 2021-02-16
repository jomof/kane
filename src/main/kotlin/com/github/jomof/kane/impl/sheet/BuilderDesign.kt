@file:Suppress("UNCHECKED_CAST")

package com.github.jomof.kane.impl.sheet

import com.github.jomof.kane.Expr
import com.github.jomof.kane.MatrixExpr
import com.github.jomof.kane.NamedExpr
import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.impl.*
import com.github.jomof.kane.impl.types.KaneType
import kotlin.reflect.KProperty

interface SheetBuilder {

    fun cell(name: String): ScalarExpr =
        CellSheetRangeExpr(cellNameToComputableCoordinate(name))

    fun range(range: String): MatrixExpr {
        val parsed = parseRange(range)
        return CoerceMatrix(SheetRangeExpr(parsed))
    }

    fun column(range: String): MatrixExpr {
        val parsed = parseRange(range)
        assert(parsed is ColumnRangeRef || parsed is NamedColumnRangeRef)
        return CoerceMatrix(SheetRangeExpr(parsed))
    }

    fun nameColumn(column: Int, name: Id)
    fun nameRow(row: Int, name: Id)
    fun nameRow(row: Int, name: List<Expr>)

    fun up(offset: Int) = CellSheetRangeExpr(CellRangeRef.relative(column = 0, row = -offset))
    fun down(offset: Int) = CellSheetRangeExpr(CellRangeRef.relative(column = 0, row = offset))
    fun left(offset: Int) = CellSheetRangeExpr(CellRangeRef.relative(column = -offset, row = 0))
    fun right(offset: Int) = CellSheetRangeExpr(CellRangeRef.relative(column = offset, row = 0))
    val up get() = up(1)
    val down get() = down(1)
    val left get() = left(1)
    val right get() = right(1)
}

internal fun parseAndNameValue(name: String, value: String): Expr {
    val admissibleType = analyzeDataType(value)
    return when (val parsed = analyzeDataType(value).parseToExpr(value)) {
        is ScalarExpr -> parsed.toNamed(name)
        is ValueExpr<*> -> ValueExpr(value, admissibleType.type as KaneType<Any>, name)
        else -> error("${parsed.javaClass}")
    }
}


