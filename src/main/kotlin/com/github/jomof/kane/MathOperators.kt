package com.github.jomof.kane

import com.github.jomof.kane.functions.add
import com.github.jomof.kane.functions.divide
import com.github.jomof.kane.functions.multiply
import com.github.jomof.kane.functions.subtract
import com.github.jomof.kane.impl.MatrixExpr
import com.github.jomof.kane.impl.ScalarExpr
import com.github.jomof.kane.impl.sheet.SheetRange

// Times
operator fun <E : Number> ScalarExpr.times(right: E) = multiply(this, right.toDouble())
operator fun <E : Number> E.times(right: ScalarExpr) = multiply(this.toDouble(), right)
operator fun ScalarExpr.times(right: ScalarExpr) = multiply(this, right)
operator fun <E : Number> MatrixExpr.times(right: E) = multiply(this, right.toDouble())
operator fun MatrixExpr.times(right: ScalarExpr) = multiply(this, right)
operator fun <E : Number> E.times(right: MatrixExpr) = multiply(this.toDouble(), right)
operator fun ScalarExpr.times(right: MatrixExpr) = multiply(this, right)
operator fun MatrixExpr.times(right: MatrixExpr) = multiply(this, right)
operator fun ScalarExpr.times(right: SheetRange) = multiply(this, right)
operator fun SheetRange.times(right: ScalarExpr) = multiply(this, right)
operator fun <E : Number> SheetRange.times(right: E) = multiply(this, right.toDouble())
operator fun <E : Number> E.times(right: SheetRange) = multiply(this.toDouble(), right)
operator fun SheetRange.times(right: SheetRange) = multiply(this, right)
operator fun MatrixExpr.times(right: SheetRange) = multiply(this, right)

// Div
operator fun <E : Number> ScalarExpr.div(right: E) = divide(this, right.toDouble())
operator fun <E : Number> E.div(right: ScalarExpr) = divide(this.toDouble(), right)
operator fun ScalarExpr.div(right: ScalarExpr) = divide(this, right)
operator fun <E : Number> MatrixExpr.div(right: E) = divide(this, right.toDouble())
operator fun MatrixExpr.div(right: ScalarExpr) = divide(this, right)
operator fun <E : Number> E.div(right: MatrixExpr) = divide(this.toDouble(), right)
operator fun ScalarExpr.div(right: MatrixExpr) = divide(this, right)
operator fun MatrixExpr.div(right: MatrixExpr) = divide(this, right)
operator fun ScalarExpr.div(right: SheetRange) = divide(this, right)
operator fun SheetRange.div(right: ScalarExpr) = divide(this, right)
operator fun <E : Number> SheetRange.div(right: E) = divide(this, right.toDouble())
operator fun <E : Number> E.div(right: SheetRange) = divide(this.toDouble(), right)
operator fun SheetRange.div(right: SheetRange) = divide(this, right)
operator fun MatrixExpr.div(right: SheetRange) = divide(this, right)

// Plus
operator fun <E : Number> ScalarExpr.plus(right: E) = add(this, right.toDouble())
operator fun <E : Number> E.plus(right: ScalarExpr) = add(this.toDouble(), right)
operator fun ScalarExpr.plus(right: ScalarExpr) = add(this, right)
operator fun <E : Number> MatrixExpr.plus(right: E) = add(this, right.toDouble())
operator fun MatrixExpr.plus(right: ScalarExpr) = add(this, right)
operator fun <E : Number> E.plus(right: MatrixExpr) = add(this.toDouble(), right)
operator fun ScalarExpr.plus(right: MatrixExpr) = add(this, right)
operator fun MatrixExpr.plus(right: MatrixExpr) = add(this, right)
operator fun ScalarExpr.plus(right: SheetRange) = add(this, right)
operator fun SheetRange.plus(right: ScalarExpr) = add(this, right)
operator fun <E : Number> SheetRange.plus(right: E) = add(this, right.toDouble())
operator fun <E : Number> E.plus(right: SheetRange) = add(this.toDouble(), right)
operator fun SheetRange.plus(right: SheetRange) = add(this, right)
operator fun MatrixExpr.plus(right: SheetRange) = add(this, right)

// Minus
operator fun <E : Number> ScalarExpr.minus(right: E) = subtract(this, right.toDouble())
operator fun <E : Number> E.minus(right: ScalarExpr) = subtract(this.toDouble(), right)
operator fun ScalarExpr.minus(right: ScalarExpr) = subtract(this, right)
operator fun <E : Number> MatrixExpr.minus(right: E) = subtract(this, right.toDouble())
operator fun MatrixExpr.minus(right: ScalarExpr) = subtract(this, right)
operator fun <E : Number> E.minus(right: MatrixExpr) = subtract(this.toDouble(), right)
operator fun ScalarExpr.minus(right: MatrixExpr) = subtract(this, right)
operator fun MatrixExpr.minus(right: MatrixExpr) = subtract(this, right)
operator fun ScalarExpr.minus(right: SheetRange) = subtract(this, right)
operator fun SheetRange.minus(right: ScalarExpr) = subtract(this, right)
operator fun <E : Number> SheetRange.minus(right: E) = subtract(this, right.toDouble())
operator fun <E : Number> E.minus(right: SheetRange) = subtract(this.toDouble(), right)
operator fun SheetRange.minus(right: SheetRange) = subtract(this, right)
operator fun MatrixExpr.minus(right: SheetRange) = subtract(this, right)