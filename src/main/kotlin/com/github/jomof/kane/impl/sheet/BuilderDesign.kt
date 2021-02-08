@file:Suppress("UNCHECKED_CAST")

package com.github.jomof.kane.impl.sheet

import com.github.jomof.kane.Expr
import com.github.jomof.kane.MatrixExpr
import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.impl.*
import com.github.jomof.kane.impl.types.KaneType
import kotlin.reflect.KProperty

interface SheetBuilder {

    fun cell(name: String): ScalarExpr = CoerceScalar(SheetBuilderRange(this, cellNameToComputableCoordinate(name)))

    fun range(range: String): SheetBuilderRange {
        val parsed = parseRange(range)
        return SheetBuilderRange(this, parsed)
    }

    fun column(range: String): MatrixExpr = CoerceMatrix(range(range))
    fun nameColumn(column: Int, name: Id)
    fun nameRow(row: Int, name: Id)
    fun nameRow(row: Int, name: List<Expr>)

    fun up(offset: Int) = SheetBuilderRange(this, CellRangeRef.relative(column = 0, row = -offset))
    fun down(offset: Int) = SheetBuilderRange(this, CellRangeRef.relative(column = 0, row = offset))
    fun left(offset: Int) = SheetBuilderRange(this, CellRangeRef.relative(column = -offset, row = 0))
    fun right(offset: Int) = SheetBuilderRange(this, CellRangeRef.relative(column = offset, row = 0))
    val up get() = up(1)
    val down get() = down(1)
    val left get() = left(1)
    val right get() = right(1)
}

internal fun parseAndNameValue(name: String, value: String): NamedExpr {
    val admissibleType = analyzeDataType(value)
    return when (val parsed = analyzeDataType(value).parseToExpr(value)) {
        is ScalarExpr -> NamedScalar(name, parsed)
        is ValueExpr<*> -> NamedValueExpr(name, value, admissibleType.type as KaneType<Any>)
        else -> error("${parsed.javaClass}")
    }
}

data class SheetBuilderRange(
    private val builder: SheetBuilder,
    val rangeRef: SheetRangeRef
) : SheetRange {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    fun toNamed(name: String): NamedSheetRangeExpr {
        if (rangeRef is ColumnRangeRef && rangeRef.first == rangeRef.second) {
            builder.nameColumn(rangeRef.first.index, name)
        }
        return NamedSheetRangeExpr(name, SheetRangeExpr(rangeRef))
    }

    override fun toString() = "BUILDER[$rangeRef]"

    override fun up(move: Int) = copy(rangeRef = rangeRef.up(move))
    override fun down(move: Int) = copy(rangeRef = rangeRef.down(move))
    override fun left(move: Int) = copy(rangeRef = rangeRef.left(move))
    override fun right(move: Int) = copy(rangeRef = rangeRef.right(move))
}

