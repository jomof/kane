package com.github.jomof.kane.sheet

import com.github.jomof.kane.CellRangeRef
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
            return when (rangeRef) {
                is CellRangeRef -> when {
                    rangeRef.column is MoveableIndex && rangeRef.row is MoveableIndex -> {
                        copy(
                            rangeRef = CellRangeRef(
                                column = RelativeIndex(rangeRef.column.index - current.column),
                                row = RelativeIndex(rangeRef.row.index - current.row)
                            )
                        )
                    }
                    else ->
                        TODO("${rangeRef.column.javaClass} ${rangeRef.row.javaClass}")
                }
                else -> TODO("$rangeRef : ${rangeRef.javaClass}")
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
            return when (rangeRef) {
                is CellRangeRef -> when {
                    rangeRef.column is RelativeIndex && rangeRef.row is RelativeIndex -> {
                        copy(
                            rangeRef = CellRangeRef(
                                column = MoveableIndex(rangeRef.column.index + current.column),
                                row = MoveableIndex(rangeRef.row.index + current.row)
                            )
                        )
                    }
                    else ->
                        TODO("${rangeRef.column.javaClass} ${rangeRef.row.javaClass}")
                }
                else -> TODO("$rangeRef : ${rangeRef.javaClass}")
            }
        }
    }.rewrite(this)
}