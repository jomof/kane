package com.github.jomof.kane.sheet

import com.github.jomof.kane.*
import com.github.jomof.kane.types.KaneType
import kotlin.reflect.KProperty

interface SheetBuilder {
    operator fun String.getValue(nothing: Nothing?, property: KProperty<*>) = parseAndNameValue(property.name, this)
    operator fun Number.getValue(nothing: Nothing?, property: KProperty<*>) =
        NamedScalar(property.name, constant(this.toDouble()))

    fun cell(name: String) = SheetBuilderRange(this, cellNameToComputableCoordinate(name))

    fun range(range: String): SheetBuilderRange {
        val parsed = parseRange(range)
        return SheetBuilderRange(this, parsed)
    }

    fun column(range: String): SheetBuilderRange
    fun nameColumn(column: Int, name: Id)
    fun nameRow(row: Int, name: Id)
    fun nameRow(row: Int, name: List<Expr>)

    fun up(offset: Int) = SheetBuilderRange(this, CellRange.relative(column = 0, row = -offset))
    fun down(offset: Int) = SheetBuilderRange(this, CellRange.relative(column = 0, row = offset))
    fun left(offset: Int) = SheetBuilderRange(this, CellRange.relative(column = -offset, row = 0))
    fun right(offset: Int) = SheetBuilderRange(this, CellRange.relative(column = offset, row = 0))
    val up get() = up(1)
    val down get() = down(1)
    val left get() = left(1)
    val right get() = right(1)
}

private fun parseAndNameValue(name: String, value: String): NamedExpr {
    val admissibleType = analyzeDataType(value)
    return when (val parsed = analyzeDataType(value).parseToExpr(value)) {
        is ScalarExpr -> NamedScalar(name, parsed)
        is ValueExpr<*> -> NamedValueExpr(name, value, admissibleType.type as KaneType<Any>)
        else -> error("${parsed.javaClass}")
    }
}

data class SheetBuilderRange(
    private val builder: SheetBuilder,
    val range: SheetRange
) : UntypedScalar {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = toNamed(property.name)
    fun toNamed(name: String): NamedSheetRangeExpr {
        if (range is ColumnRange && range.first == range.second) {
            builder.nameColumn(range.first.index, name)
        }
        return NamedSheetRangeExpr(name, SheetRangeExpr(range))
    }

    override fun toString() = "BUILDER[$range]"

    fun up(move: Int) = copy(range = range.up(move))
    fun down(move: Int) = copy(range = range.down(move))
    fun left(move: Int) = copy(range = range.left(move))
    fun right(move: Int) = copy(range = range.right(move))
    val up get() = up(1)
    val down get() = down(1)
    val left get() = left(1)
    val right get() = right(1)
}

