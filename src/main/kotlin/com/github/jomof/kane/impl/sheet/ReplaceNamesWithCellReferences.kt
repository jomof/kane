package com.github.jomof.kane.impl.sheet

import com.github.jomof.kane.Expr
import com.github.jomof.kane.impl.*
import com.github.jomof.kane.impl.visitor.RewritingVisitor

fun Expr.replaceNamesWithCellReferences(excluding: Id): Expr {
    return object : RewritingVisitor() {
        override fun rewrite(expr: NamedSheetRangeExpr): Expr = with(expr) {
            when (name) {
                excluding -> copy(range = range(range))
                is Coordinate -> {
                    CoerceScalar(
                        SheetRangeExpr(
                            name.toComputableCoordinate()
                        )
                    )
                }
                else -> rewrite(range)
            }
        }

        override fun rewrite(expr: NamedScalar): Expr = with(expr) {
            when {
                name == excluding -> copy(scalar = scalar(scalar))
                name is Coordinate -> {
                    CoerceScalar(
                        SheetRangeExpr(
                            name.toComputableCoordinate()
                        )
                    )
                }
                scalar is ConstantScalar -> this
                scalar is DiscreteUniformRandomVariable -> this
                else -> scalar(scalar)
            }
        }
    }.rewrite(this)
}

fun replaceNamesWithCellReferences(sheet: Sheet) = sheet.copy(cells = sheet.cells
    .mapExprs { name, expr ->
        expr.replaceNamesWithCellReferences(name)
    })
