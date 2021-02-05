package com.github.jomof.kane.functions

import com.github.jomof.kane.impl.MatrixExpr
import com.github.jomof.kane.impl.ScalarExpr

fun softmax(expr: MatrixExpr) = exp(expr) / sum(exp(expr))
fun softmax(expr: MatrixExpr, sigma: ScalarExpr): MatrixExpr {
    val sigmaSquared = sigma * sigma
    return exp(expr / sigmaSquared) / sum(exp(expr / sigmaSquared))
}

fun softmin(expr: MatrixExpr) = exp(-expr) / sum(exp(-expr))
fun softmin(expr: MatrixExpr, sigma: ScalarExpr): MatrixExpr {
    val sigmaSquared = sigma * sigma
    return exp(-expr / sigmaSquared) / sum(exp(-expr / sigmaSquared))
}

