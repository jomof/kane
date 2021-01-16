package com.github.jomof.kane.functions

import com.github.jomof.kane.MatrixExpr
import com.github.jomof.kane.ScalarExpr
import com.github.jomof.kane.constant

fun softmax(expr : MatrixExpr) = exp(expr) / summation(exp(expr))
fun softmax(expr : MatrixExpr, sigma : ScalarExpr) : MatrixExpr {
    val sigmaSquared = sigma * sigma
    return exp(expr / sigmaSquared) / summation(exp(expr / sigmaSquared))
}

fun softmin(expr : MatrixExpr) = exp(-expr) / summation(exp(-expr))
fun softmin(expr : MatrixExpr, sigma : ScalarExpr) : MatrixExpr {
    val sigmaSquared = sigma * sigma
    return exp(-expr / sigmaSquared) / summation(exp(-expr / sigmaSquared))
}

fun count(expr : MatrixExpr) : ScalarExpr {
    return constant(expr.rows.toDouble() * expr.columns.toDouble())
}

fun mean(expr : MatrixExpr) : ScalarExpr {
    return summation(expr) / (expr.rows * expr.columns).toDouble()
}

fun stddev(expr : MatrixExpr) : ScalarExpr {
    val mean = mean(expr)
    return pow(summation(pow(expr-mean, 2.0)), 0.5)
}

fun covar(expr : MatrixExpr) = stddev(expr) / mean(expr)
