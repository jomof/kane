package com.github.jomof.kane.rigueur.functions

import com.github.jomof.kane.rigueur.MatrixExpr
import com.github.jomof.kane.rigueur.ScalarExpr
import com.github.jomof.kane.rigueur.constant
import com.sun.jdi.DoubleType

fun <E:Number> softmax(expr : MatrixExpr<E>) = exp(expr) / summation(exp(expr))
fun <E:Number> softmax(expr : MatrixExpr<E>, sigma : ScalarExpr<E>) : MatrixExpr<E> {
    val sigmaSquared = sigma * sigma
    return exp(expr / sigmaSquared) / summation(exp(expr / sigmaSquared))
}

fun <E:Number> softmin(expr : MatrixExpr<E>) = exp(-expr) / summation(exp(-expr))
fun <E:Number> softmin(expr : MatrixExpr<E>, sigma : ScalarExpr<E>) : MatrixExpr<E> {
    val sigmaSquared = sigma * sigma
    return exp(-expr / sigmaSquared) / summation(exp(-expr / sigmaSquared))
}

fun <E:Number> count(expr : MatrixExpr<E>) : ScalarExpr<Double> {
    return constant(expr.rows.toDouble() * expr.columns.toDouble())
}

fun mean(expr : MatrixExpr<Double>) : ScalarExpr<Double> {
    return summation(expr) / (expr.rows * expr.columns).toDouble()
}

fun stddev(expr : MatrixExpr<Double>) : ScalarExpr<Double> {
    val mean = mean(expr)
    return pow(summation(pow(expr-mean, 2.0)), 0.5)
}

fun covar(expr : MatrixExpr<Double>) = stddev(expr) / mean(expr)
