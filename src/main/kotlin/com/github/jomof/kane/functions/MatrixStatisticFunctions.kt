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

