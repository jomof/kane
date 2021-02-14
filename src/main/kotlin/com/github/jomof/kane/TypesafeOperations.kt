package com.github.jomof.kane

import com.github.jomof.kane.impl.functions.*
import com.github.jomof.kane.impl.functions.*
import com.github.jomof.kane.impl.sheet.*
import com.github.jomof.kane.impl.*

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
val count: AggregatableFunction = countFunc
fun count(vararg values: Number): Double = countFunc.invoke(values)
fun count(vararg values: ScalarExpr): ScalarExpr = countFunc.invoke(values)

//fun count(vararg values : ScalarExpr) : ScalarExpr = countFunc.invoke(values)
fun count(vararg values: Any): ScalarExpr = countFunc.invoke(values)
fun count(sheet: Sheet): Sheet = countFunc.invoke(sheet)
fun count(groupBy: GroupBy): Sheet = countFunc.invoke(groupBy)
fun count(scalar: StatisticExpr): ScalarExpr = countFunc.invoke(scalar)
fun count(matrix: MatrixExpr): ScalarExpr = countFunc.invoke(matrix)
fun count(range: SheetRange): ScalarExpr = countFunc.invoke(range)
//fun count(expr : Expr) : Expr = countFunc.invoke(expr)

// typesafe nans
private val nansFunc = NansFunction()
val nans: AggregatableFunction = nansFunc
fun nans(vararg values: Number): Double = nansFunc.invoke(values)
fun nans(vararg values: ScalarExpr): ScalarExpr = nansFunc.invoke(values)

//fun nans(vararg values : ScalarExpr) : ScalarExpr = nansFunc.invoke(values)
fun nans(vararg values: Any): ScalarExpr = nansFunc.invoke(values)
fun nans(sheet: Sheet): Sheet = nansFunc.invoke(sheet)
fun nans(groupBy: GroupBy): Sheet = nansFunc.invoke(groupBy)
fun nans(scalar: StatisticExpr): ScalarExpr = nansFunc.invoke(scalar)
fun nans(matrix: MatrixExpr): ScalarExpr = nansFunc.invoke(matrix)
fun nans(range: SheetRange): ScalarExpr = nansFunc.invoke(range)
//fun nans(expr : Expr) : Expr = nansFunc.invoke(expr)

// typesafe mean
private val meanFunc = MeanFunction()
val mean: AggregatableFunction = meanFunc
fun mean(vararg values: Number): Double = meanFunc.invoke(values)
fun mean(vararg values: ScalarExpr): ScalarExpr = meanFunc.invoke(values)

//fun mean(vararg values : ScalarExpr) : ScalarExpr = meanFunc.invoke(values)
fun mean(vararg values: Any): ScalarExpr = meanFunc.invoke(values)
fun mean(sheet: Sheet): Sheet = meanFunc.invoke(sheet)
fun mean(groupBy: GroupBy): Sheet = meanFunc.invoke(groupBy)
fun mean(scalar: StatisticExpr): ScalarExpr = meanFunc.invoke(scalar)
fun mean(matrix: MatrixExpr): ScalarExpr = meanFunc.invoke(matrix)
fun mean(range: SheetRange): ScalarExpr = meanFunc.invoke(range)
//fun mean(expr : Expr) : Expr = meanFunc.invoke(expr)

// typesafe min
private val minFunc = MinFunction()
val min: AggregatableFunction = minFunc
fun min(vararg values: Number): Double = minFunc.invoke(values)
fun min(vararg values: ScalarExpr): ScalarExpr = minFunc.invoke(values)

//fun min(vararg values : ScalarExpr) : ScalarExpr = minFunc.invoke(values)
fun min(vararg values: Any): ScalarExpr = minFunc.invoke(values)
fun min(sheet: Sheet): Sheet = minFunc.invoke(sheet)
fun min(groupBy: GroupBy): Sheet = minFunc.invoke(groupBy)
fun min(scalar: StatisticExpr): ScalarExpr = minFunc.invoke(scalar)
fun min(matrix: MatrixExpr): ScalarExpr = minFunc.invoke(matrix)
fun min(range: SheetRange): ScalarExpr = minFunc.invoke(range)
//fun min(expr : Expr) : Expr = minFunc.invoke(expr)

// typesafe percentile25
private val percentile25Func = Percentile25Function()
val percentile25: AggregatableFunction = percentile25Func
fun percentile25(vararg values: Number): Double = percentile25Func.invoke(values)
fun percentile25(vararg values: ScalarExpr): ScalarExpr = percentile25Func.invoke(values)

//fun percentile25(vararg values : ScalarExpr) : ScalarExpr = percentile25Func.invoke(values)
fun percentile25(vararg values: Any): ScalarExpr = percentile25Func.invoke(values)
fun percentile25(sheet: Sheet): Sheet = percentile25Func.invoke(sheet)
fun percentile25(groupBy: GroupBy): Sheet = percentile25Func.invoke(groupBy)
fun percentile25(scalar: StatisticExpr): ScalarExpr = percentile25Func.invoke(scalar)
fun percentile25(matrix: MatrixExpr): ScalarExpr = percentile25Func.invoke(matrix)
fun percentile25(range: SheetRange): ScalarExpr = percentile25Func.invoke(range)
//fun percentile25(expr : Expr) : Expr = percentile25Func.invoke(expr)

// typesafe median
private val medianFunc = MedianFunction()
val median: AggregatableFunction = medianFunc
fun median(vararg values: Number): Double = medianFunc.invoke(values)
fun median(vararg values: ScalarExpr): ScalarExpr = medianFunc.invoke(values)

//fun median(vararg values : ScalarExpr) : ScalarExpr = medianFunc.invoke(values)
fun median(vararg values: Any): ScalarExpr = medianFunc.invoke(values)
fun median(sheet: Sheet): Sheet = medianFunc.invoke(sheet)
fun median(groupBy: GroupBy): Sheet = medianFunc.invoke(groupBy)
fun median(scalar: StatisticExpr): ScalarExpr = medianFunc.invoke(scalar)
fun median(matrix: MatrixExpr): ScalarExpr = medianFunc.invoke(matrix)
fun median(range: SheetRange): ScalarExpr = medianFunc.invoke(range)
//fun median(expr : Expr) : Expr = medianFunc.invoke(expr)

// typesafe percentile75
private val percentile75Func = Percentile75Function()
val percentile75: AggregatableFunction = percentile75Func
fun percentile75(vararg values: Number): Double = percentile75Func.invoke(values)
fun percentile75(vararg values: ScalarExpr): ScalarExpr = percentile75Func.invoke(values)

//fun percentile75(vararg values : ScalarExpr) : ScalarExpr = percentile75Func.invoke(values)
fun percentile75(vararg values: Any): ScalarExpr = percentile75Func.invoke(values)
fun percentile75(sheet: Sheet): Sheet = percentile75Func.invoke(sheet)
fun percentile75(groupBy: GroupBy): Sheet = percentile75Func.invoke(groupBy)
fun percentile75(scalar: StatisticExpr): ScalarExpr = percentile75Func.invoke(scalar)
fun percentile75(matrix: MatrixExpr): ScalarExpr = percentile75Func.invoke(matrix)
fun percentile75(range: SheetRange): ScalarExpr = percentile75Func.invoke(range)
//fun percentile75(expr : Expr) : Expr = percentile75Func.invoke(expr)

// typesafe max
private val maxFunc = MaxFunction()
val max: AggregatableFunction = maxFunc
fun max(vararg values: Number): Double = maxFunc.invoke(values)
fun max(vararg values: ScalarExpr): ScalarExpr = maxFunc.invoke(values)

//fun max(vararg values : ScalarExpr) : ScalarExpr = maxFunc.invoke(values)
fun max(vararg values: Any): ScalarExpr = maxFunc.invoke(values)
fun max(sheet: Sheet): Sheet = maxFunc.invoke(sheet)
fun max(groupBy: GroupBy): Sheet = maxFunc.invoke(groupBy)
fun max(scalar: StatisticExpr): ScalarExpr = maxFunc.invoke(scalar)
fun max(matrix: MatrixExpr): ScalarExpr = maxFunc.invoke(matrix)
fun max(range: SheetRange): ScalarExpr = maxFunc.invoke(range)
//fun max(expr : Expr) : Expr = maxFunc.invoke(expr)

// typesafe variance
private val varianceFunc = VarianceFunction()
val variance: AggregatableFunction = varianceFunc
fun variance(vararg values: Number): Double = varianceFunc.invoke(values)
fun variance(vararg values: ScalarExpr): ScalarExpr = varianceFunc.invoke(values)

//fun variance(vararg values : ScalarExpr) : ScalarExpr = varianceFunc.invoke(values)
fun variance(vararg values: Any): ScalarExpr = varianceFunc.invoke(values)
fun variance(sheet: Sheet): Sheet = varianceFunc.invoke(sheet)
fun variance(groupBy: GroupBy): Sheet = varianceFunc.invoke(groupBy)
fun variance(scalar: StatisticExpr): ScalarExpr = varianceFunc.invoke(scalar)
fun variance(matrix: MatrixExpr): ScalarExpr = varianceFunc.invoke(matrix)
fun variance(range: SheetRange): ScalarExpr = varianceFunc.invoke(range)
//fun variance(expr : Expr) : Expr = varianceFunc.invoke(expr)

// typesafe stdev
private val stdevFunc = StdevFunction()
val stdev: AggregatableFunction = stdevFunc
fun stdev(vararg values: Number): Double = stdevFunc.invoke(values)
fun stdev(vararg values: ScalarExpr): ScalarExpr = stdevFunc.invoke(values)

//fun stdev(vararg values : ScalarExpr) : ScalarExpr = stdevFunc.invoke(values)
fun stdev(vararg values: Any): ScalarExpr = stdevFunc.invoke(values)
fun stdev(sheet: Sheet): Sheet = stdevFunc.invoke(sheet)
fun stdev(groupBy: GroupBy): Sheet = stdevFunc.invoke(groupBy)
fun stdev(scalar: StatisticExpr): ScalarExpr = stdevFunc.invoke(scalar)
fun stdev(matrix: MatrixExpr): ScalarExpr = stdevFunc.invoke(matrix)
fun stdev(range: SheetRange): ScalarExpr = stdevFunc.invoke(range)
//fun stdev(expr : Expr) : Expr = stdevFunc.invoke(expr)

// typesafe skewness
private val skewnessFunc = SkewnessFunction()
val skewness: AggregatableFunction = skewnessFunc
fun skewness(vararg values: Number): Double = skewnessFunc.invoke(values)
fun skewness(vararg values: ScalarExpr): ScalarExpr = skewnessFunc.invoke(values)

//fun skewness(vararg values : ScalarExpr) : ScalarExpr = skewnessFunc.invoke(values)
fun skewness(vararg values: Any): ScalarExpr = skewnessFunc.invoke(values)
fun skewness(sheet: Sheet): Sheet = skewnessFunc.invoke(sheet)
fun skewness(groupBy: GroupBy): Sheet = skewnessFunc.invoke(groupBy)
fun skewness(scalar: StatisticExpr): ScalarExpr = skewnessFunc.invoke(scalar)
fun skewness(matrix: MatrixExpr): ScalarExpr = skewnessFunc.invoke(matrix)
fun skewness(range: SheetRange): ScalarExpr = skewnessFunc.invoke(range)
//fun skewness(expr : Expr) : Expr = skewnessFunc.invoke(expr)

// typesafe kurtosis
private val kurtosisFunc = KurtosisFunction()
val kurtosis: AggregatableFunction = kurtosisFunc
fun kurtosis(vararg values: Number): Double = kurtosisFunc.invoke(values)
fun kurtosis(vararg values: ScalarExpr): ScalarExpr = kurtosisFunc.invoke(values)

//fun kurtosis(vararg values : ScalarExpr) : ScalarExpr = kurtosisFunc.invoke(values)
fun kurtosis(vararg values: Any): ScalarExpr = kurtosisFunc.invoke(values)
fun kurtosis(sheet: Sheet): Sheet = kurtosisFunc.invoke(sheet)
fun kurtosis(groupBy: GroupBy): Sheet = kurtosisFunc.invoke(groupBy)
fun kurtosis(scalar: StatisticExpr): ScalarExpr = kurtosisFunc.invoke(scalar)
fun kurtosis(matrix: MatrixExpr): ScalarExpr = kurtosisFunc.invoke(matrix)
fun kurtosis(range: SheetRange): ScalarExpr = kurtosisFunc.invoke(range)
//fun kurtosis(expr : Expr) : Expr = kurtosisFunc.invoke(expr)

// typesafe cv
private val cvFunc = CvFunction()
val cv: AggregatableFunction = cvFunc
fun cv(vararg values: Number): Double = cvFunc.invoke(values)
fun cv(vararg values: ScalarExpr): ScalarExpr = cvFunc.invoke(values)

//fun cv(vararg values : ScalarExpr) : ScalarExpr = cvFunc.invoke(values)
fun cv(vararg values: Any): ScalarExpr = cvFunc.invoke(values)
fun cv(sheet: Sheet): Sheet = cvFunc.invoke(sheet)
fun cv(groupBy: GroupBy): Sheet = cvFunc.invoke(groupBy)
fun cv(scalar: StatisticExpr): ScalarExpr = cvFunc.invoke(scalar)
fun cv(matrix: MatrixExpr): ScalarExpr = cvFunc.invoke(matrix)
fun cv(range: SheetRange): ScalarExpr = cvFunc.invoke(range)
//fun cv(expr : Expr) : Expr = cvFunc.invoke(expr)

// typesafe sum
private val sumFunc = SumFunction()
val sum: AggregatableFunction = sumFunc
fun sum(vararg values: Number): Double = sumFunc.invoke(values)
fun sum(vararg values: ScalarExpr): ScalarExpr = sumFunc.invoke(values)

//fun sum(vararg values : ScalarExpr) : ScalarExpr = sumFunc.invoke(values)
fun sum(vararg values: Any): ScalarExpr = sumFunc.invoke(values)
fun sum(sheet: Sheet): Sheet = sumFunc.invoke(sheet)
fun sum(groupBy: GroupBy): Sheet = sumFunc.invoke(groupBy)
fun sum(scalar: StatisticExpr): ScalarExpr = sumFunc.invoke(scalar)
fun sum(matrix: MatrixExpr): ScalarExpr = sumFunc.invoke(matrix)
fun sum(range: SheetRange): ScalarExpr = sumFunc.invoke(range)
//fun sum(expr : Expr) : Expr = sumFunc.invoke(expr)

// typesafe sin
private val sinFunc = SinFunction()
val sin: AlgebraicUnaryFunction = sinFunc

/**
 * sin function
 * [![](https://jomof.github.io/kane/figures/sin-profile.svg)]
 */
fun sin(matrix: NamedMatrix): MatrixExpr = sinFunc(matrix as MatrixExpr)
fun sin(matrix: DataMatrix): MatrixExpr = sinFunc(matrix as MatrixExpr)
fun sin(matrix: MatrixExpr): MatrixExpr = sinFunc(matrix)
fun sin(scalar: ScalarExpr): ScalarExpr = sinFunc(scalar)
fun sin(scalar: Double): Double = sinFunc(scalar)

// typesafe cos
private val cosFunc = CosFunction()
val cos: AlgebraicUnaryFunction = cosFunc

/**
 * cos function
 * [![](https://jomof.github.io/kane/figures/cos-profile.svg)]
 */
fun cos(matrix: NamedMatrix): MatrixExpr = cosFunc(matrix as MatrixExpr)
fun cos(matrix: DataMatrix): MatrixExpr = cosFunc(matrix as MatrixExpr)
fun cos(matrix : MatrixExpr) : MatrixExpr = cosFunc(matrix)
fun cos(scalar : ScalarExpr) : ScalarExpr = cosFunc(scalar)
fun cos(scalar : Double) : Double = cosFunc(scalar)

// typesafe lrelu
private val lreluFunc = LreluFunction()
val lrelu: AlgebraicUnaryFunction = lreluFunc
/**
 * lrelu function
 * [![](https://jomof.github.io/kane/figures/lrelu-profile.svg)]
 */
fun lrelu(matrix : NamedMatrix) : MatrixExpr = lreluFunc(matrix as MatrixExpr)
fun lrelu(matrix : DataMatrix) : MatrixExpr = lreluFunc(matrix as MatrixExpr)
fun lrelu(matrix : MatrixExpr) : MatrixExpr = lreluFunc(matrix)
fun lrelu(scalar : ScalarExpr) : ScalarExpr = lreluFunc(scalar)
fun lrelu(scalar : Double) : Double = lreluFunc(scalar)

// typesafe lstep
private val lstepFunc = LstepFunction()
val lstep: AlgebraicUnaryFunction = lstepFunc
/**
 * lstep function
 * [![](https://jomof.github.io/kane/figures/lstep-profile.svg)]
 */
fun lstep(matrix : NamedMatrix) : MatrixExpr = lstepFunc(matrix as MatrixExpr)
fun lstep(matrix : DataMatrix) : MatrixExpr = lstepFunc(matrix as MatrixExpr)
fun lstep(matrix : MatrixExpr) : MatrixExpr = lstepFunc(matrix)
fun lstep(scalar : ScalarExpr) : ScalarExpr = lstepFunc(scalar)
fun lstep(scalar : Double) : Double = lstepFunc(scalar)

// typesafe logit
private val logitFunc = LogitFunction()
val logit: AlgebraicUnaryFunction = logitFunc
/**
 * logit function
 * [![](https://jomof.github.io/kane/figures/logit-profile.svg)]
 */
fun logit(matrix : NamedMatrix) : MatrixExpr = logitFunc(matrix as MatrixExpr)
fun logit(matrix : DataMatrix) : MatrixExpr = logitFunc(matrix as MatrixExpr)
fun logit(matrix : MatrixExpr) : MatrixExpr = logitFunc(matrix)
fun logit(scalar : ScalarExpr) : ScalarExpr = logitFunc(scalar)
fun logit(scalar : Double) : Double = logitFunc(scalar)

// typesafe exp
private val expFunc = ExpFunction()
val exp: AlgebraicUnaryFunction = expFunc
/**
 * exp function
 * [![](https://jomof.github.io/kane/figures/exp-profile.svg)]
 */
fun exp(matrix : NamedMatrix) : MatrixExpr = expFunc(matrix as MatrixExpr)
fun exp(matrix : DataMatrix) : MatrixExpr = expFunc(matrix as MatrixExpr)
fun exp(matrix : MatrixExpr) : MatrixExpr = expFunc(matrix)
fun exp(scalar : ScalarExpr) : ScalarExpr = expFunc(scalar)
fun exp(scalar : Double) : Double = expFunc(scalar)

// typesafe relu
private val reluFunc = ReluFunction()
val relu: AlgebraicUnaryFunction = reluFunc
/**
 * relu function
 * [![](https://jomof.github.io/kane/figures/relu-profile.svg)]
 */
fun relu(matrix : NamedMatrix) : MatrixExpr = reluFunc(matrix as MatrixExpr)
fun relu(matrix : DataMatrix) : MatrixExpr = reluFunc(matrix as MatrixExpr)
fun relu(matrix : MatrixExpr) : MatrixExpr = reluFunc(matrix)
fun relu(scalar : ScalarExpr) : ScalarExpr = reluFunc(scalar)
fun relu(scalar : Double) : Double = reluFunc(scalar)

// typesafe tanh
private val tanhFunc = TanhFunction()
val tanh: AlgebraicUnaryFunction = tanhFunc
/**
 * tanh function
 * [![](https://jomof.github.io/kane/figures/tanh-profile.svg)]
 */
fun tanh(matrix : NamedMatrix) : MatrixExpr = tanhFunc(matrix as MatrixExpr)
fun tanh(matrix : DataMatrix) : MatrixExpr = tanhFunc(matrix as MatrixExpr)
fun tanh(matrix : MatrixExpr) : MatrixExpr = tanhFunc(matrix)
fun tanh(scalar : ScalarExpr) : ScalarExpr = tanhFunc(scalar)
fun tanh(scalar : Double) : Double = tanhFunc(scalar)

// typesafe step
private val stepFunc = StepFunction()
val step: AlgebraicUnaryFunction = stepFunc
/**
 * step function
 * [![](https://jomof.github.io/kane/figures/step-profile.svg)]
 */
fun step(matrix : NamedMatrix) : MatrixExpr = stepFunc(matrix as MatrixExpr)
fun step(matrix : DataMatrix) : MatrixExpr = stepFunc(matrix as MatrixExpr)
fun step(matrix : MatrixExpr) : MatrixExpr = stepFunc(matrix)
fun step(scalar : ScalarExpr) : ScalarExpr = stepFunc(scalar)
fun step(scalar : Double) : Double = stepFunc(scalar)

// typesafe negate
private val negateFunc = NegateFunction()
val negate: AlgebraicUnaryFunction = negateFunc
/**
 * negate function
 * [![](https://jomof.github.io/kane/figures/negate-profile.svg)]
 */
fun negate(matrix : NamedMatrix) : MatrixExpr = negateFunc(matrix as MatrixExpr)
fun negate(matrix : DataMatrix) : MatrixExpr = negateFunc(matrix as MatrixExpr)
fun negate(matrix : MatrixExpr) : MatrixExpr = negateFunc(matrix)
fun negate(scalar : ScalarExpr) : ScalarExpr = negateFunc(scalar)
fun negate(scalar : Double) : Double = negateFunc(scalar)

