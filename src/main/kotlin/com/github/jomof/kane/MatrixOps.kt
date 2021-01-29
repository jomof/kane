package com.github.jomof.kane

import com.github.jomof.kane.sheet.SheetBuilder
import com.github.jomof.kane.sheet.SheetBuilderImpl


fun matrixOf(columns: Int, rows: Int, elements: List<Double>) = DataMatrix(
    columns,
    rows,
    elements.map { constant(it) }.toList()
)

fun matrixOf(columns: Int, rows: Int, vararg elements: Double) = DataMatrix(
    columns,
    rows,
    elements.map { constant(it) }.toList()
)

fun matrixOf(columns: Int, rows: Int, vararg elements: ScalarExpr) = DataMatrix(
    columns,
    rows,
    elements.toList()
)

fun matrixOf(columns: Int, rows: Int, init: SheetBuilder.(Coordinate) -> Any): DataMatrix {
    val sb = SheetBuilderImpl()
    val elements = coordinatesOf(columns, rows).map {
        convertAnyToScalarExpr(init(sb, it))
    }
    return matrixOf(columns, rows, *elements.toTypedArray())
}



