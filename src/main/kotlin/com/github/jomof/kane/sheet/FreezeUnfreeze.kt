package com.github.jomof.kane.sheet

import com.github.jomof.kane.CellRange
import com.github.jomof.kane.ComputableIndex.MoveableIndex
import com.github.jomof.kane.ComputableIndex.RelativeIndex
import com.github.jomof.kane.Coordinate
import com.github.jomof.kane.Expr
import com.github.jomof.kane.visitor.RewritingVisitor

/**
 * Convert moveable cell references to relative. This is so that rows and columns can be moved, added, and deleted
 * before refreezing.
 * [current] is the cell that this formula occupies.
 */
fun Expr.unfreeze(current: Coordinate): Expr {
    return object : RewritingVisitor() {
        override fun rewrite(expr: SheetRangeExpr): Expr = with(expr) {
            return when (range) {
                is CellRange -> when {
                    range.column is MoveableIndex && range.row is MoveableIndex -> {
                        copy(
                            range = CellRange(
                                column = RelativeIndex(range.column.index - current.column),
                                row = RelativeIndex(range.row.index - current.row)
                            )
                        )
                    }
                    else ->
                        TODO("${range.column.javaClass} ${range.row.javaClass}")
                }
                else -> TODO("$range : ${range.javaClass}")
            }
        }
    }.rewrite(this)
}

/**
 * Convert relative cell references to moveable.
 * [current] is the cell that this formula occupies.
 */
fun Expr.freeze(current: Coordinate): Expr {
    return object : RewritingVisitor() {
        override fun rewrite(expr: SheetRangeExpr): Expr = with(expr) {
            return when (range) {
                is CellRange -> when {
                    range.column is RelativeIndex && range.row is RelativeIndex -> {
                        copy(
                            range = CellRange(
                                column = MoveableIndex(range.column.index + current.column),
                                row = MoveableIndex(range.row.index + current.row)
                            )
                        )
                    }
                    else ->
                        TODO("${range.column.javaClass} ${range.row.javaClass}")
                }
                else -> TODO("$range : ${range.javaClass}")
            }
        }
    }.rewrite(this)
}