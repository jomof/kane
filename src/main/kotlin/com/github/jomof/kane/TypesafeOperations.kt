package com.github.jomof.kane

import com.github.jomof.kane.functions.*
import com.github.jomof.kane.impl.functions.*
import com.github.jomof.kane.impl.sheet.*

// plus typesafe infix operators
operator fun Number.plus(right: ScalarExpr) = plus(this.toDouble(), right)
operator fun Number.plus(right: MatrixExpr) = plus(this.toDouble(), right)                
operator fun Number.plus(right: SheetRange) = plus(this.toDouble(), right)

operator fun ScalarExpr.plus(right: Number) = plus(this, right.toDouble())
operator fun ScalarExpr.plus(right: ScalarExpr) = plus(this, right)
operator fun ScalarExpr.plus(right: MatrixExpr) = plus(this, right)
operator fun ScalarExpr.plus(right: SheetRange) = plus(this, right)

operator fun MatrixExpr.plus(right: Number) = plus(this, right.toDouble())
operator fun MatrixExpr.plus(right: ScalarExpr) = plus(this, right)
operator fun MatrixExpr.plus(right: MatrixExpr) = plus(this, right)
operator fun MatrixExpr.plus(right: SheetRange) = plus(this, right)

operator fun SheetRange.plus(right: Number) = plus(this, right.toDouble())
operator fun SheetRange.plus(right: ScalarExpr) = plus(this, right)
operator fun SheetRange.plus(right: MatrixExpr) = plus(this, right)
operator fun SheetRange.plus(right: SheetRange) = plus(this, right)

// minus typesafe infix operators
operator fun Number.minus(right: ScalarExpr) = minus(this.toDouble(), right)
operator fun Number.minus(right: MatrixExpr) = minus(this.toDouble(), right)                
operator fun Number.minus(right: SheetRange) = minus(this.toDouble(), right)

operator fun ScalarExpr.minus(right: Number) = minus(this, right.toDouble())
operator fun ScalarExpr.minus(right: ScalarExpr) = minus(this, right)
operator fun ScalarExpr.minus(right: MatrixExpr) = minus(this, right)
operator fun ScalarExpr.minus(right: SheetRange) = minus(this, right)

operator fun MatrixExpr.minus(right: Number) = minus(this, right.toDouble())
operator fun MatrixExpr.minus(right: ScalarExpr) = minus(this, right)
operator fun MatrixExpr.minus(right: MatrixExpr) = minus(this, right)
operator fun MatrixExpr.minus(right: SheetRange) = minus(this, right)

operator fun SheetRange.minus(right: Number) = minus(this, right.toDouble())
operator fun SheetRange.minus(right: ScalarExpr) = minus(this, right)
operator fun SheetRange.minus(right: MatrixExpr) = minus(this, right)
operator fun SheetRange.minus(right: SheetRange) = minus(this, right)

// times typesafe infix operators
operator fun Number.times(right: ScalarExpr) = times(this.toDouble(), right)
operator fun Number.times(right: MatrixExpr) = times(this.toDouble(), right)                
operator fun Number.times(right: SheetRange) = times(this.toDouble(), right)

operator fun ScalarExpr.times(right: Number) = times(this, right.toDouble())
operator fun ScalarExpr.times(right: ScalarExpr) = times(this, right)
operator fun ScalarExpr.times(right: MatrixExpr) = times(this, right)
operator fun ScalarExpr.times(right: SheetRange) = times(this, right)

operator fun MatrixExpr.times(right: Number) = times(this, right.toDouble())
operator fun MatrixExpr.times(right: ScalarExpr) = times(this, right)
operator fun MatrixExpr.times(right: MatrixExpr) = times(this, right)
operator fun MatrixExpr.times(right: SheetRange) = times(this, right)

operator fun SheetRange.times(right: Number) = times(this, right.toDouble())
operator fun SheetRange.times(right: ScalarExpr) = times(this, right)
operator fun SheetRange.times(right: MatrixExpr) = times(this, right)
operator fun SheetRange.times(right: SheetRange) = times(this, right)

// div typesafe infix operators
operator fun Number.div(right: ScalarExpr) = div(this.toDouble(), right)
operator fun Number.div(right: MatrixExpr) = div(this.toDouble(), right)                
operator fun Number.div(right: SheetRange) = div(this.toDouble(), right)

operator fun ScalarExpr.div(right: Number) = div(this, right.toDouble())
operator fun ScalarExpr.div(right: ScalarExpr) = div(this, right)
operator fun ScalarExpr.div(right: MatrixExpr) = div(this, right)
operator fun ScalarExpr.div(right: SheetRange) = div(this, right)

operator fun MatrixExpr.div(right: Number) = div(this, right.toDouble())
operator fun MatrixExpr.div(right: ScalarExpr) = div(this, right)
operator fun MatrixExpr.div(right: MatrixExpr) = div(this, right)
operator fun MatrixExpr.div(right: SheetRange) = div(this, right)

operator fun SheetRange.div(right: Number) = div(this, right.toDouble())
operator fun SheetRange.div(right: ScalarExpr) = div(this, right)
operator fun SheetRange.div(right: MatrixExpr) = div(this, right)
operator fun SheetRange.div(right: SheetRange) = div(this, right)

// typesafe count
private val countFunc = CountFunction()
val count : AggregatableFunction = countFunc
fun count(vararg values : Number) : Double = countFunc(values)
fun count(vararg values : ScalarExpr) : ScalarExpr = countFunc(values)
fun count(vararg values : Any) : ScalarExpr = countFunc(values)
fun count(sheet: Sheet) : Sheet = countFunc(sheet)
fun count(groupBy: GroupBy) : Sheet = countFunc(groupBy)
fun count(scalar : ScalarExpr) : ScalarExpr = countFunc(scalar)
fun count(matrix : MatrixExpr) : ScalarExpr = countFunc(matrix)
fun count(range : SheetRange) : ScalarExpr = countFunc(range)
fun count(expr : Expr) : Expr = countFunc(expr)

// typesafe nans
private val nansFunc = NansFunction()
val nans : AggregatableFunction = nansFunc
fun nans(vararg values : Number) : Double = nansFunc(values)
fun nans(vararg values : ScalarExpr) : ScalarExpr = nansFunc(values)
fun nans(vararg values : Any) : ScalarExpr = nansFunc(values)
fun nans(sheet: Sheet) : Sheet = nansFunc(sheet)
fun nans(groupBy: GroupBy) : Sheet = nansFunc(groupBy)
fun nans(scalar : ScalarExpr) : ScalarExpr = nansFunc(scalar)
fun nans(matrix : MatrixExpr) : ScalarExpr = nansFunc(matrix)
fun nans(range : SheetRange) : ScalarExpr = nansFunc(range)
fun nans(expr : Expr) : Expr = nansFunc(expr)

// typesafe mean
private val meanFunc = MeanFunction()
val mean : AggregatableFunction = meanFunc
fun mean(vararg values : Number) : Double = meanFunc(values)
fun mean(vararg values : ScalarExpr) : ScalarExpr = meanFunc(values)
fun mean(vararg values : Any) : ScalarExpr = meanFunc(values)
fun mean(sheet: Sheet) : Sheet = meanFunc(sheet)
fun mean(groupBy: GroupBy) : Sheet = meanFunc(groupBy)
fun mean(scalar : ScalarExpr) : ScalarExpr = meanFunc(scalar)
fun mean(matrix : MatrixExpr) : ScalarExpr = meanFunc(matrix)
fun mean(range : SheetRange) : ScalarExpr = meanFunc(range)
fun mean(expr : Expr) : Expr = meanFunc(expr)

// typesafe min
private val minFunc = MinFunction()
val min : AggregatableFunction = minFunc
fun min(vararg values : Number) : Double = minFunc(values)
fun min(vararg values : ScalarExpr) : ScalarExpr = minFunc(values)
fun min(vararg values : Any) : ScalarExpr = minFunc(values)
fun min(sheet: Sheet) : Sheet = minFunc(sheet)
fun min(groupBy: GroupBy) : Sheet = minFunc(groupBy)
fun min(scalar : ScalarExpr) : ScalarExpr = minFunc(scalar)
fun min(matrix : MatrixExpr) : ScalarExpr = minFunc(matrix)
fun min(range : SheetRange) : ScalarExpr = minFunc(range)
fun min(expr : Expr) : Expr = minFunc(expr)

// typesafe percentile25
private val percentile25Func = Percentile25Function()
val percentile25 : AggregatableFunction = percentile25Func
fun percentile25(vararg values : Number) : Double = percentile25Func(values)
fun percentile25(vararg values : ScalarExpr) : ScalarExpr = percentile25Func(values)
fun percentile25(vararg values : Any) : ScalarExpr = percentile25Func(values)
fun percentile25(sheet: Sheet) : Sheet = percentile25Func(sheet)
fun percentile25(groupBy: GroupBy) : Sheet = percentile25Func(groupBy)
fun percentile25(scalar : ScalarExpr) : ScalarExpr = percentile25Func(scalar)
fun percentile25(matrix : MatrixExpr) : ScalarExpr = percentile25Func(matrix)
fun percentile25(range : SheetRange) : ScalarExpr = percentile25Func(range)
fun percentile25(expr : Expr) : Expr = percentile25Func(expr)

// typesafe median
private val medianFunc = MedianFunction()
val median : AggregatableFunction = medianFunc
fun median(vararg values : Number) : Double = medianFunc(values)
fun median(vararg values : ScalarExpr) : ScalarExpr = medianFunc(values)
fun median(vararg values : Any) : ScalarExpr = medianFunc(values)
fun median(sheet: Sheet) : Sheet = medianFunc(sheet)
fun median(groupBy: GroupBy) : Sheet = medianFunc(groupBy)
fun median(scalar : ScalarExpr) : ScalarExpr = medianFunc(scalar)
fun median(matrix : MatrixExpr) : ScalarExpr = medianFunc(matrix)
fun median(range : SheetRange) : ScalarExpr = medianFunc(range)
fun median(expr : Expr) : Expr = medianFunc(expr)

// typesafe percentile75
private val percentile75Func = Percentile75Function()
val percentile75 : AggregatableFunction = percentile75Func
fun percentile75(vararg values : Number) : Double = percentile75Func(values)
fun percentile75(vararg values : ScalarExpr) : ScalarExpr = percentile75Func(values)
fun percentile75(vararg values : Any) : ScalarExpr = percentile75Func(values)
fun percentile75(sheet: Sheet) : Sheet = percentile75Func(sheet)
fun percentile75(groupBy: GroupBy) : Sheet = percentile75Func(groupBy)
fun percentile75(scalar : ScalarExpr) : ScalarExpr = percentile75Func(scalar)
fun percentile75(matrix : MatrixExpr) : ScalarExpr = percentile75Func(matrix)
fun percentile75(range : SheetRange) : ScalarExpr = percentile75Func(range)
fun percentile75(expr : Expr) : Expr = percentile75Func(expr)

// typesafe max
private val maxFunc = MaxFunction()
val max : AggregatableFunction = maxFunc
fun max(vararg values : Number) : Double = maxFunc(values)
fun max(vararg values : ScalarExpr) : ScalarExpr = maxFunc(values)
fun max(vararg values : Any) : ScalarExpr = maxFunc(values)
fun max(sheet: Sheet) : Sheet = maxFunc(sheet)
fun max(groupBy: GroupBy) : Sheet = maxFunc(groupBy)
fun max(scalar : ScalarExpr) : ScalarExpr = maxFunc(scalar)
fun max(matrix : MatrixExpr) : ScalarExpr = maxFunc(matrix)
fun max(range : SheetRange) : ScalarExpr = maxFunc(range)
fun max(expr : Expr) : Expr = maxFunc(expr)

// typesafe variance
private val varianceFunc = VarianceFunction()
val variance : AggregatableFunction = varianceFunc
fun variance(vararg values : Number) : Double = varianceFunc(values)
fun variance(vararg values : ScalarExpr) : ScalarExpr = varianceFunc(values)
fun variance(vararg values : Any) : ScalarExpr = varianceFunc(values)
fun variance(sheet: Sheet) : Sheet = varianceFunc(sheet)
fun variance(groupBy: GroupBy) : Sheet = varianceFunc(groupBy)
fun variance(scalar : ScalarExpr) : ScalarExpr = varianceFunc(scalar)
fun variance(matrix : MatrixExpr) : ScalarExpr = varianceFunc(matrix)
fun variance(range : SheetRange) : ScalarExpr = varianceFunc(range)
fun variance(expr : Expr) : Expr = varianceFunc(expr)

// typesafe stdev
private val stdevFunc = StdevFunction()
val stdev : AggregatableFunction = stdevFunc
fun stdev(vararg values : Number) : Double = stdevFunc(values)
fun stdev(vararg values : ScalarExpr) : ScalarExpr = stdevFunc(values)
fun stdev(vararg values : Any) : ScalarExpr = stdevFunc(values)
fun stdev(sheet: Sheet) : Sheet = stdevFunc(sheet)
fun stdev(groupBy: GroupBy) : Sheet = stdevFunc(groupBy)
fun stdev(scalar : ScalarExpr) : ScalarExpr = stdevFunc(scalar)
fun stdev(matrix : MatrixExpr) : ScalarExpr = stdevFunc(matrix)
fun stdev(range : SheetRange) : ScalarExpr = stdevFunc(range)
fun stdev(expr : Expr) : Expr = stdevFunc(expr)

// typesafe skewness
private val skewnessFunc = SkewnessFunction()
val skewness : AggregatableFunction = skewnessFunc
fun skewness(vararg values : Number) : Double = skewnessFunc(values)
fun skewness(vararg values : ScalarExpr) : ScalarExpr = skewnessFunc(values)
fun skewness(vararg values : Any) : ScalarExpr = skewnessFunc(values)
fun skewness(sheet: Sheet) : Sheet = skewnessFunc(sheet)
fun skewness(groupBy: GroupBy) : Sheet = skewnessFunc(groupBy)
fun skewness(scalar : ScalarExpr) : ScalarExpr = skewnessFunc(scalar)
fun skewness(matrix : MatrixExpr) : ScalarExpr = skewnessFunc(matrix)
fun skewness(range : SheetRange) : ScalarExpr = skewnessFunc(range)
fun skewness(expr : Expr) : Expr = skewnessFunc(expr)

// typesafe kurtosis
private val kurtosisFunc = KurtosisFunction()
val kurtosis : AggregatableFunction = kurtosisFunc
fun kurtosis(vararg values : Number) : Double = kurtosisFunc(values)
fun kurtosis(vararg values : ScalarExpr) : ScalarExpr = kurtosisFunc(values)
fun kurtosis(vararg values : Any) : ScalarExpr = kurtosisFunc(values)
fun kurtosis(sheet: Sheet) : Sheet = kurtosisFunc(sheet)
fun kurtosis(groupBy: GroupBy) : Sheet = kurtosisFunc(groupBy)
fun kurtosis(scalar : ScalarExpr) : ScalarExpr = kurtosisFunc(scalar)
fun kurtosis(matrix : MatrixExpr) : ScalarExpr = kurtosisFunc(matrix)
fun kurtosis(range : SheetRange) : ScalarExpr = kurtosisFunc(range)
fun kurtosis(expr : Expr) : Expr = kurtosisFunc(expr)

// typesafe cv
private val cvFunc = CvFunction()
val cv : AggregatableFunction = cvFunc
fun cv(vararg values : Number) : Double = cvFunc(values)
fun cv(vararg values : ScalarExpr) : ScalarExpr = cvFunc(values)
fun cv(vararg values : Any) : ScalarExpr = cvFunc(values)
fun cv(sheet: Sheet) : Sheet = cvFunc(sheet)
fun cv(groupBy: GroupBy) : Sheet = cvFunc(groupBy)
fun cv(scalar : ScalarExpr) : ScalarExpr = cvFunc(scalar)
fun cv(matrix : MatrixExpr) : ScalarExpr = cvFunc(matrix)
fun cv(range : SheetRange) : ScalarExpr = cvFunc(range)
fun cv(expr : Expr) : Expr = cvFunc(expr)

// typesafe sum
private val sumFunc = SumFunction()
val sum : AggregatableFunction = sumFunc
fun sum(vararg values : Number) : Double = sumFunc(values)
fun sum(vararg values : ScalarExpr) : ScalarExpr = sumFunc(values)
fun sum(vararg values : Any) : ScalarExpr = sumFunc(values)
fun sum(sheet: Sheet) : Sheet = sumFunc(sheet)
fun sum(groupBy: GroupBy) : Sheet = sumFunc(groupBy)
fun sum(scalar : ScalarExpr) : ScalarExpr = sumFunc(scalar)
fun sum(matrix : MatrixExpr) : ScalarExpr = sumFunc(matrix)
fun sum(range : SheetRange) : ScalarExpr = sumFunc(range)
fun sum(expr : Expr) : Expr = sumFunc(expr)

// typesafe sin
private val sinFunc = SinFunction()
val sin : AlgebraicUnaryScalarFunction = sinFunc
/**
 * sin function
 * [![](https://jomof.github.io/kane/figures/sin-profile.svg)]
 */
fun sin(matrix : MatrixExpr) : MatrixExpr = sinFunc(matrix)
fun sin(scalar : ScalarExpr) : ScalarExpr = sinFunc(scalar)
fun sin(scalar : Double) : Double = sinFunc(scalar)

// typesafe cos
private val cosFunc = CosFunction()
val cos : AlgebraicUnaryScalarFunction = cosFunc
/**
 * cos function
 * [![](https://jomof.github.io/kane/figures/cos-profile.svg)]
 */
fun cos(matrix : MatrixExpr) : MatrixExpr = cosFunc(matrix)
fun cos(scalar : ScalarExpr) : ScalarExpr = cosFunc(scalar)
fun cos(scalar : Double) : Double = cosFunc(scalar)

// typesafe lrelu
private val lreluFunc = LreluFunction()
val lrelu : AlgebraicUnaryScalarFunction = lreluFunc
/**
 * lrelu function
 * [![](https://jomof.github.io/kane/figures/lrelu-profile.svg)]
 */
fun lrelu(matrix : MatrixExpr) : MatrixExpr = lreluFunc(matrix)
fun lrelu(scalar : ScalarExpr) : ScalarExpr = lreluFunc(scalar)
fun lrelu(scalar : Double) : Double = lreluFunc(scalar)

// typesafe lstep
private val lstepFunc = LstepFunction()
val lstep : AlgebraicUnaryScalarFunction = lstepFunc
/**
 * lstep function
 * [![](https://jomof.github.io/kane/figures/lstep-profile.svg)]
 */
fun lstep(matrix : MatrixExpr) : MatrixExpr = lstepFunc(matrix)
fun lstep(scalar : ScalarExpr) : ScalarExpr = lstepFunc(scalar)
fun lstep(scalar : Double) : Double = lstepFunc(scalar)

// typesafe logit
private val logitFunc = LogitFunction()
val logit : AlgebraicUnaryScalarFunction = logitFunc
/**
 * logit function
 * [![](https://jomof.github.io/kane/figures/logit-profile.svg)]
 */
fun logit(matrix : MatrixExpr) : MatrixExpr = logitFunc(matrix)
fun logit(scalar : ScalarExpr) : ScalarExpr = logitFunc(scalar)
fun logit(scalar : Double) : Double = logitFunc(scalar)

// typesafe exp
private val expFunc = ExpFunction()
val exp : AlgebraicUnaryScalarFunction = expFunc
/**
 * exp function
 * [![](https://jomof.github.io/kane/figures/exp-profile.svg)]
 */
fun exp(matrix : MatrixExpr) : MatrixExpr = expFunc(matrix)
fun exp(scalar : ScalarExpr) : ScalarExpr = expFunc(scalar)
fun exp(scalar : Double) : Double = expFunc(scalar)

// typesafe relu
private val reluFunc = ReluFunction()
val relu : AlgebraicUnaryScalarFunction = reluFunc
/**
 * relu function
 * [![](https://jomof.github.io/kane/figures/relu-profile.svg)]
 */
fun relu(matrix : MatrixExpr) : MatrixExpr = reluFunc(matrix)
fun relu(scalar : ScalarExpr) : ScalarExpr = reluFunc(scalar)
fun relu(scalar : Double) : Double = reluFunc(scalar)

// typesafe tanh
private val tanhFunc = TanhFunction()
val tanh : AlgebraicUnaryScalarFunction = tanhFunc
/**
 * tanh function
 * [![](https://jomof.github.io/kane/figures/tanh-profile.svg)]
 */
fun tanh(matrix : MatrixExpr) : MatrixExpr = tanhFunc(matrix)
fun tanh(scalar : ScalarExpr) : ScalarExpr = tanhFunc(scalar)
fun tanh(scalar : Double) : Double = tanhFunc(scalar)

// typesafe step
private val stepFunc = StepFunction()
val step : AlgebraicUnaryScalarFunction = stepFunc
/**
 * step function
 * [![](https://jomof.github.io/kane/figures/step-profile.svg)]
 */
fun step(matrix : MatrixExpr) : MatrixExpr = stepFunc(matrix)
fun step(scalar : ScalarExpr) : ScalarExpr = stepFunc(scalar)
fun step(scalar : Double) : Double = stepFunc(scalar)

// typesafe negate
private val negateFunc = NegateFunction()
val negate : AlgebraicUnaryScalarFunction = negateFunc
/**
 * negate function
 * [![](https://jomof.github.io/kane/figures/negate-profile.svg)]
 */
fun negate(matrix : MatrixExpr) : MatrixExpr = negateFunc(matrix)
fun negate(scalar : ScalarExpr) : ScalarExpr = negateFunc(scalar)
fun negate(scalar : Double) : Double = negateFunc(scalar)

