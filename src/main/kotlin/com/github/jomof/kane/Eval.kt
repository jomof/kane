package com.github.jomof.kane

import com.github.jomof.kane.impl.*
import com.github.jomof.kane.impl.sheet.GroupBy
import com.github.jomof.kane.impl.sheet.RangeExprProvider
import com.github.jomof.kane.impl.sheet.Sheet
import com.github.jomof.kane.impl.sheet.showExcelColumnTags


fun Expr.eval(
    rangeExprProvider: RangeExprProvider? = null,
    reduceVariables: Boolean = false,
    excludeVariables: Set<Id> = setOf(),
) = evalImpl(rangeExprProvider, reduceVariables, excludeVariables)

fun NamedMatrix.eval(
    rangeExprProvider: RangeExprProvider? = null,
    reduceVariables: Boolean = false,
    excludeVariables: Set<Id> = setOf(),
) = (this as Expr).evalImpl(rangeExprProvider, reduceVariables, excludeVariables) as NamedMatrix

fun NamedScalar.eval(
    rangeExprProvider: RangeExprProvider? = null,
    reduceVariables: Boolean = false,
    excludeVariables: Set<Id> = setOf(),
) = (this as Expr).evalImpl(rangeExprProvider, reduceVariables, excludeVariables) as NamedScalar

fun NamedScalarExpr.eval(
    rangeExprProvider: RangeExprProvider? = null,
    reduceVariables: Boolean = false,
    excludeVariables: Set<Id> = setOf(),
) = (this as Expr).evalImpl(rangeExprProvider, reduceVariables, excludeVariables) as NamedScalarExpr

fun NamedMatrixExpr.eval(
    rangeExprProvider: RangeExprProvider? = null,
    reduceVariables: Boolean = false,
    excludeVariables: Set<Id> = setOf(),
) = (this as Expr).evalImpl(rangeExprProvider, reduceVariables, excludeVariables) as NamedMatrixExpr

fun NamedAlgebraicExpr.eval(
    rangeExprProvider: RangeExprProvider? = null,
    reduceVariables: Boolean = false,
    excludeVariables: Set<Id> = setOf(),
) = (this as Expr).evalImpl(rangeExprProvider, reduceVariables, excludeVariables) as NamedAlgebraicExpr

fun Sheet.eval(
    rangeExprProvider: RangeExprProvider? = null,
    reduceVariables: Boolean = false,
    excludeVariables: Set<Id> = setOf(),
) = ((this as Expr).evalImpl(rangeExprProvider, reduceVariables, excludeVariables) as Sheet).showExcelColumnTags(false)

fun ScalarExpr.eval(
    rangeExprProvider: RangeExprProvider? = null,
    reduceVariables: Boolean = false,
    excludeVariables: Set<Id> = setOf(),
) = (this as Expr).evalImpl(rangeExprProvider, reduceVariables, excludeVariables) as ScalarExpr

fun MatrixExpr.eval(
    rangeExprProvider: RangeExprProvider? = null,
    reduceVariables: Boolean = false,
    excludeVariables: Set<Id> = setOf(),
) = (this as Expr).evalImpl(rangeExprProvider, reduceVariables, excludeVariables) as MatrixExpr

fun GroupBy.eval(
    rangeExprProvider: RangeExprProvider? = null,
    reduceVariables: Boolean = false,
    excludeVariables: Set<Id> = setOf(),
) = ((this as Expr).evalImpl(
    rangeExprProvider,
    reduceVariables,
    excludeVariables
) as GroupBy).showExcelColumnTags(false)