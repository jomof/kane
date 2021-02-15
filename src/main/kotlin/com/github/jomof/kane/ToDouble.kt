package com.github.jomof.kane

import com.github.jomof.kane.impl.canGetConstant
import com.github.jomof.kane.impl.getConstant
import com.github.jomof.kane.impl.sheet.Sheet

/**
 * Convert a single celled [Sheet] value to an [Double]
 */
fun Sheet.toDouble(): Double {
    val cell = single()
    if (cell.canGetConstant()) return cell.getConstant()
    val evaled = cell.eval()
    if (evaled.canGetConstant()) return evaled.getConstant()
    cell.eval()
    error("Could not convert to double")
}

/**
 * Convert a single celled [MatrixExpr] value to an [Double]
 */
fun MatrixExpr.toDouble(): Double {
    val cell = single()
    if (cell.canGetConstant()) return cell.getConstant()
    return cell.eval().getConstant()
}

/**
 * Convert a [ScalarExpr] to [Double]
 */
fun ScalarExpr.toDouble(): Double {
    if (canGetConstant()) return getConstant()
    return eval().getConstant()
}