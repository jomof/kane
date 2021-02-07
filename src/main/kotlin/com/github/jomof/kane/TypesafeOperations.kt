package com.github.jomof.kane

import com.github.jomof.kane.functions.*
import com.github.jomof.kane.impl.functions.AggregatableFunction
import com.github.jomof.kane.impl.functions.AlgebraicUnaryScalarFunction
import com.github.jomof.kane.impl.sheet.GroupBy
import com.github.jomof.kane.impl.sheet.Sheet
import com.github.jomof.kane.impl.sheet.SheetRange

// plus typesafe infix operators
operator fun <E : Number> ScalarExpr.plus(right: E) = plus(this, right.toDouble())
operator fun <E : Number> E.plus(right: ScalarExpr) = plus(this.toDouble(), right)
operator fun ScalarExpr.plus(right: ScalarExpr) = plus(this, right)
operator fun <E : Number> MatrixExpr.plus(right: E) = plus(this, right.toDouble())
operator fun MatrixExpr.plus(right: ScalarExpr) = plus(this, right)
operator fun <E : Number> E.plus(right: MatrixExpr) = plus(this.toDouble(), right)
operator fun ScalarExpr.plus(right: MatrixExpr) = plus(this, right)
operator fun MatrixExpr.plus(right: MatrixExpr) = plus(this, right)
operator fun ScalarExpr.plus(right: SheetRange) = plus(this, right)
operator fun SheetRange.plus(right: ScalarExpr) = plus(this, right)
operator fun <E : Number> SheetRange.plus(right: E) = plus(this, right.toDouble())
operator fun <E : Number> E.plus(right: SheetRange) = plus(this.toDouble(), right)
operator fun SheetRange.plus(right: SheetRange) = plus(this, right)
operator fun MatrixExpr.plus(right: SheetRange) = plus(this, right)

// minus typesafe infix operators
operator fun <E : Number> ScalarExpr.minus(right: E) = minus(this, right.toDouble())
operator fun <E : Number> E.minus(right: ScalarExpr) = minus(this.toDouble(), right)
operator fun ScalarExpr.minus(right: ScalarExpr) = minus(this, right)
operator fun <E : Number> MatrixExpr.minus(right: E) = minus(this, right.toDouble())
operator fun MatrixExpr.minus(right: ScalarExpr) = minus(this, right)
operator fun <E : Number> E.minus(right: MatrixExpr) = minus(this.toDouble(), right)
operator fun ScalarExpr.minus(right: MatrixExpr) = minus(this, right)
operator fun MatrixExpr.minus(right: MatrixExpr) = minus(this, right)
operator fun ScalarExpr.minus(right: SheetRange) = minus(this, right)
operator fun SheetRange.minus(right: ScalarExpr) = minus(this, right)
operator fun <E : Number> SheetRange.minus(right: E) = minus(this, right.toDouble())
operator fun <E : Number> E.minus(right: SheetRange) = minus(this.toDouble(), right)
operator fun SheetRange.minus(right: SheetRange) = minus(this, right)
operator fun MatrixExpr.minus(right: SheetRange) = minus(this, right)

// times typesafe infix operators
operator fun <E : Number> ScalarExpr.times(right: E) = times(this, right.toDouble())
operator fun <E : Number> E.times(right: ScalarExpr) = times(this.toDouble(), right)
operator fun ScalarExpr.times(right: ScalarExpr) = times(this, right)
operator fun <E : Number> MatrixExpr.times(right: E) = times(this, right.toDouble())
operator fun MatrixExpr.times(right: ScalarExpr) = times(this, right)
operator fun <E : Number> E.times(right: MatrixExpr) = times(this.toDouble(), right)
operator fun ScalarExpr.times(right: MatrixExpr) = times(this, right)
operator fun MatrixExpr.times(right: MatrixExpr) = times(this, right)
operator fun ScalarExpr.times(right: SheetRange) = times(this, right)
operator fun SheetRange.times(right: ScalarExpr) = times(this, right)
operator fun <E : Number> SheetRange.times(right: E) = times(this, right.toDouble())
operator fun <E : Number> E.times(right: SheetRange) = times(this.toDouble(), right)
operator fun SheetRange.times(right: SheetRange) = times(this, right)
operator fun MatrixExpr.times(right: SheetRange) = times(this, right)

// div typesafe infix operators
operator fun <E : Number> ScalarExpr.div(right: E) = div(this, right.toDouble())
operator fun <E : Number> E.div(right: ScalarExpr) = div(this.toDouble(), right)
operator fun ScalarExpr.div(right: ScalarExpr) = div(this, right)
operator fun <E : Number> MatrixExpr.div(right: E) = div(this, right.toDouble())
operator fun MatrixExpr.div(right: ScalarExpr) = div(this, right)
operator fun <E : Number> E.div(right: MatrixExpr) = div(this.toDouble(), right)
operator fun ScalarExpr.div(right: MatrixExpr) = div(this, right)
operator fun MatrixExpr.div(right: MatrixExpr) = div(this, right)
operator fun ScalarExpr.div(right: SheetRange) = div(this, right)
operator fun SheetRange.div(right: ScalarExpr) = div(this, right)
operator fun <E : Number> SheetRange.div(right: E) = div(this, right.toDouble())
operator fun <E : Number> E.div(right: SheetRange) = div(this.toDouble(), right)
operator fun SheetRange.div(right: SheetRange) = div(this, right)
operator fun MatrixExpr.div(right: SheetRange) = div(this, right)

// typesafe count
private val countFunc = CountFunction()
val count: AggregatableFunction = countFunc
fun count(list: List<ScalarExpr>): ScalarExpr = countFunc(list)
fun count(vararg values: ScalarExpr): ScalarExpr = countFunc(values)
fun count(list: List<Double>): Double = countFunc(list)
fun count(vararg values: Double): Double = countFunc(values)
fun count(matrix: MatrixExpr): ScalarExpr = countFunc(matrix)
fun count(sheet: Sheet): Sheet = countFunc(sheet)
fun count(groupBy: GroupBy): Sheet = countFunc(groupBy)
fun count(algebraic: AlgebraicExpr): ScalarExpr = countFunc(algebraic)
fun count(range: SheetRange): ScalarExpr = countFunc(range)
fun count(expr: Expr): Expr = countFunc(expr)

// typesafe nans
private val nansFunc = NansFunction()
val nans: AggregatableFunction = nansFunc
fun nans(list: List<ScalarExpr>): ScalarExpr = nansFunc(list)
fun nans(vararg values: ScalarExpr): ScalarExpr = nansFunc(values)
fun nans(list: List<Double>): Double = nansFunc(list)
fun nans(vararg values: Double): Double = nansFunc(values)
fun nans(matrix: MatrixExpr): ScalarExpr = nansFunc(matrix)
fun nans(sheet: Sheet): Sheet = nansFunc(sheet)
fun nans(groupBy: GroupBy): Sheet = nansFunc(groupBy)
fun nans(algebraic: AlgebraicExpr): ScalarExpr = nansFunc(algebraic)
fun nans(range: SheetRange): ScalarExpr = nansFunc(range)
fun nans(expr: Expr): Expr = nansFunc(expr)

// typesafe mean
private val meanFunc = MeanFunction()
val mean: AggregatableFunction = meanFunc
fun mean(list: List<ScalarExpr>): ScalarExpr = meanFunc(list)
fun mean(vararg values: ScalarExpr): ScalarExpr = meanFunc(values)
fun mean(list: List<Double>): Double = meanFunc(list)
fun mean(vararg values: Double): Double = meanFunc(values)
fun mean(matrix: MatrixExpr): ScalarExpr = meanFunc(matrix)
fun mean(sheet: Sheet): Sheet = meanFunc(sheet)
fun mean(groupBy: GroupBy): Sheet = meanFunc(groupBy)
fun mean(algebraic: AlgebraicExpr): ScalarExpr = meanFunc(algebraic)
fun mean(range: SheetRange): ScalarExpr = meanFunc(range)
fun mean(expr: Expr): Expr = meanFunc(expr)

// typesafe min
private val minFunc = MinFunction()
val min: AggregatableFunction = minFunc
fun min(list: List<ScalarExpr>): ScalarExpr = minFunc(list)
fun min(vararg values: ScalarExpr): ScalarExpr = minFunc(values)
fun min(list: List<Double>): Double = minFunc(list)
fun min(vararg values: Double): Double = minFunc(values)
fun min(matrix: MatrixExpr): ScalarExpr = minFunc(matrix)
fun min(sheet: Sheet): Sheet = minFunc(sheet)
fun min(groupBy: GroupBy): Sheet = minFunc(groupBy)
fun min(algebraic: AlgebraicExpr): ScalarExpr = minFunc(algebraic)
fun min(range: SheetRange): ScalarExpr = minFunc(range)
fun min(expr: Expr): Expr = minFunc(expr)

// typesafe percentile25
private val percentile25Func = Percentile25Function()
val percentile25: AggregatableFunction = percentile25Func
fun percentile25(list: List<ScalarExpr>): ScalarExpr = percentile25Func(list)
fun percentile25(vararg values: ScalarExpr): ScalarExpr = percentile25Func(values)
fun percentile25(list: List<Double>): Double = percentile25Func(list)
fun percentile25(vararg values: Double): Double = percentile25Func(values)
fun percentile25(matrix: MatrixExpr): ScalarExpr = percentile25Func(matrix)
fun percentile25(sheet: Sheet): Sheet = percentile25Func(sheet)
fun percentile25(groupBy: GroupBy): Sheet = percentile25Func(groupBy)
fun percentile25(algebraic: AlgebraicExpr): ScalarExpr = percentile25Func(algebraic)
fun percentile25(range: SheetRange): ScalarExpr = percentile25Func(range)
fun percentile25(expr: Expr): Expr = percentile25Func(expr)

// typesafe median
private val medianFunc = MedianFunction()
val median: AggregatableFunction = medianFunc
fun median(list: List<ScalarExpr>): ScalarExpr = medianFunc(list)
fun median(vararg values: ScalarExpr): ScalarExpr = medianFunc(values)
fun median(list: List<Double>): Double = medianFunc(list)
fun median(vararg values: Double): Double = medianFunc(values)
fun median(matrix: MatrixExpr): ScalarExpr = medianFunc(matrix)
fun median(sheet: Sheet): Sheet = medianFunc(sheet)
fun median(groupBy: GroupBy): Sheet = medianFunc(groupBy)
fun median(algebraic: AlgebraicExpr): ScalarExpr = medianFunc(algebraic)
fun median(range: SheetRange): ScalarExpr = medianFunc(range)
fun median(expr: Expr): Expr = medianFunc(expr)

// typesafe percentile75
private val percentile75Func = Percentile75Function()
val percentile75: AggregatableFunction = percentile75Func
fun percentile75(list: List<ScalarExpr>): ScalarExpr = percentile75Func(list)
fun percentile75(vararg values: ScalarExpr): ScalarExpr = percentile75Func(values)
fun percentile75(list: List<Double>): Double = percentile75Func(list)
fun percentile75(vararg values: Double): Double = percentile75Func(values)
fun percentile75(matrix: MatrixExpr): ScalarExpr = percentile75Func(matrix)
fun percentile75(sheet: Sheet): Sheet = percentile75Func(sheet)
fun percentile75(groupBy: GroupBy): Sheet = percentile75Func(groupBy)
fun percentile75(algebraic: AlgebraicExpr): ScalarExpr = percentile75Func(algebraic)
fun percentile75(range: SheetRange): ScalarExpr = percentile75Func(range)
fun percentile75(expr: Expr): Expr = percentile75Func(expr)

// typesafe max
private val maxFunc = MaxFunction()
val max: AggregatableFunction = maxFunc
fun max(list: List<ScalarExpr>): ScalarExpr = maxFunc(list)
fun max(vararg values: ScalarExpr): ScalarExpr = maxFunc(values)
fun max(list: List<Double>): Double = maxFunc(list)
fun max(vararg values: Double): Double = maxFunc(values)
fun max(matrix: MatrixExpr): ScalarExpr = maxFunc(matrix)
fun max(sheet: Sheet): Sheet = maxFunc(sheet)
fun max(groupBy: GroupBy): Sheet = maxFunc(groupBy)
fun max(algebraic: AlgebraicExpr): ScalarExpr = maxFunc(algebraic)
fun max(range: SheetRange): ScalarExpr = maxFunc(range)
fun max(expr: Expr): Expr = maxFunc(expr)

// typesafe variance
private val varianceFunc = VarianceFunction()
val variance: AggregatableFunction = varianceFunc
fun variance(list: List<ScalarExpr>): ScalarExpr = varianceFunc(list)
fun variance(vararg values: ScalarExpr): ScalarExpr = varianceFunc(values)
fun variance(list: List<Double>): Double = varianceFunc(list)
fun variance(vararg values: Double): Double = varianceFunc(values)
fun variance(matrix: MatrixExpr): ScalarExpr = varianceFunc(matrix)
fun variance(sheet: Sheet): Sheet = varianceFunc(sheet)
fun variance(groupBy: GroupBy): Sheet = varianceFunc(groupBy)
fun variance(algebraic: AlgebraicExpr): ScalarExpr = varianceFunc(algebraic)
fun variance(range: SheetRange): ScalarExpr = varianceFunc(range)
fun variance(expr: Expr): Expr = varianceFunc(expr)

// typesafe stdev
private val stdevFunc = StdevFunction()
val stdev: AggregatableFunction = stdevFunc
fun stdev(list: List<ScalarExpr>): ScalarExpr = stdevFunc(list)
fun stdev(vararg values: ScalarExpr): ScalarExpr = stdevFunc(values)
fun stdev(list: List<Double>): Double = stdevFunc(list)
fun stdev(vararg values: Double): Double = stdevFunc(values)
fun stdev(matrix: MatrixExpr): ScalarExpr = stdevFunc(matrix)
fun stdev(sheet: Sheet): Sheet = stdevFunc(sheet)
fun stdev(groupBy: GroupBy): Sheet = stdevFunc(groupBy)
fun stdev(algebraic: AlgebraicExpr): ScalarExpr = stdevFunc(algebraic)
fun stdev(range: SheetRange): ScalarExpr = stdevFunc(range)
fun stdev(expr: Expr): Expr = stdevFunc(expr)

// typesafe skewness
private val skewnessFunc = SkewnessFunction()
val skewness: AggregatableFunction = skewnessFunc
fun skewness(list: List<ScalarExpr>): ScalarExpr = skewnessFunc(list)
fun skewness(vararg values: ScalarExpr): ScalarExpr = skewnessFunc(values)
fun skewness(list: List<Double>): Double = skewnessFunc(list)
fun skewness(vararg values: Double): Double = skewnessFunc(values)
fun skewness(matrix: MatrixExpr): ScalarExpr = skewnessFunc(matrix)
fun skewness(sheet: Sheet): Sheet = skewnessFunc(sheet)
fun skewness(groupBy: GroupBy): Sheet = skewnessFunc(groupBy)
fun skewness(algebraic: AlgebraicExpr): ScalarExpr = skewnessFunc(algebraic)
fun skewness(range: SheetRange): ScalarExpr = skewnessFunc(range)
fun skewness(expr: Expr): Expr = skewnessFunc(expr)

// typesafe kurtosis
private val kurtosisFunc = KurtosisFunction()
val kurtosis: AggregatableFunction = kurtosisFunc
fun kurtosis(list: List<ScalarExpr>): ScalarExpr = kurtosisFunc(list)
fun kurtosis(vararg values: ScalarExpr): ScalarExpr = kurtosisFunc(values)
fun kurtosis(list: List<Double>): Double = kurtosisFunc(list)
fun kurtosis(vararg values: Double): Double = kurtosisFunc(values)
fun kurtosis(matrix: MatrixExpr): ScalarExpr = kurtosisFunc(matrix)
fun kurtosis(sheet: Sheet): Sheet = kurtosisFunc(sheet)
fun kurtosis(groupBy: GroupBy): Sheet = kurtosisFunc(groupBy)
fun kurtosis(algebraic: AlgebraicExpr): ScalarExpr = kurtosisFunc(algebraic)
fun kurtosis(range: SheetRange): ScalarExpr = kurtosisFunc(range)
fun kurtosis(expr: Expr): Expr = kurtosisFunc(expr)

// typesafe cv
private val cvFunc = CvFunction()
val cv: AggregatableFunction = cvFunc
fun cv(list: List<ScalarExpr>): ScalarExpr = cvFunc(list)
fun cv(vararg values: ScalarExpr): ScalarExpr = cvFunc(values)
fun cv(list: List<Double>): Double = cvFunc(list)
fun cv(vararg values: Double): Double = cvFunc(values)
fun cv(matrix: MatrixExpr): ScalarExpr = cvFunc(matrix)
fun cv(sheet: Sheet): Sheet = cvFunc(sheet)
fun cv(groupBy: GroupBy): Sheet = cvFunc(groupBy)
fun cv(algebraic: AlgebraicExpr): ScalarExpr = cvFunc(algebraic)
fun cv(range: SheetRange): ScalarExpr = cvFunc(range)
fun cv(expr: Expr): Expr = cvFunc(expr)

// typesafe sum
private val sumFunc = SumFunction()
val sum: AggregatableFunction = sumFunc
fun sum(list: List<ScalarExpr>): ScalarExpr = sumFunc(list)
fun sum(vararg values: ScalarExpr): ScalarExpr = sumFunc(values)
fun sum(list: List<Double>): Double = sumFunc(list)
fun sum(vararg values: Double): Double = sumFunc(values)
fun sum(matrix: MatrixExpr): ScalarExpr = sumFunc(matrix)
fun sum(sheet: Sheet): Sheet = sumFunc(sheet)
fun sum(groupBy: GroupBy): Sheet = sumFunc(groupBy)
fun sum(algebraic: AlgebraicExpr): ScalarExpr = sumFunc(algebraic)
fun sum(range: SheetRange): ScalarExpr = sumFunc(range)
fun sum(expr: Expr): Expr = sumFunc(expr)

// typesafe sin
private val sinFunc = SinFunction()
val sin: AlgebraicUnaryScalarFunction = sinFunc

/**
 * sin function
 * [![](https://jomof.github.io/kane/figures/sin-profile.svg)]
 */
fun sin(matrix: MatrixExpr): MatrixExpr = sinFunc(matrix)
fun sin(scalar: ScalarExpr): ScalarExpr = sinFunc(scalar)
fun sin(scalar: Double): Double = sinFunc(scalar)

// typesafe cos
private val cosFunc = CosFunction()
val cos: AlgebraicUnaryScalarFunction = cosFunc

/**
 * cos function
 * [![](https://jomof.github.io/kane/figures/cos-profile.svg)]
 */
fun cos(matrix: MatrixExpr): MatrixExpr = cosFunc(matrix)
fun cos(scalar: ScalarExpr): ScalarExpr = cosFunc(scalar)
fun cos(scalar: Double): Double = cosFunc(scalar)

// typesafe lrelu
private val lreluFunc = LreluFunction()
val lrelu: AlgebraicUnaryScalarFunction = lreluFunc

/**
 * lrelu function
 * [![](https://jomof.github.io/kane/figures/lrelu-profile.svg)]
 */
fun lrelu(matrix: MatrixExpr): MatrixExpr = lreluFunc(matrix)
fun lrelu(scalar: ScalarExpr): ScalarExpr = lreluFunc(scalar)
fun lrelu(scalar: Double): Double = lreluFunc(scalar)

// typesafe lstep
private val lstepFunc = LstepFunction()
val lstep: AlgebraicUnaryScalarFunction = lstepFunc

/**
 * lstep function
 * [![](https://jomof.github.io/kane/figures/lstep-profile.svg)]
 */
fun lstep(matrix: MatrixExpr): MatrixExpr = lstepFunc(matrix)
fun lstep(scalar: ScalarExpr): ScalarExpr = lstepFunc(scalar)
fun lstep(scalar: Double): Double = lstepFunc(scalar)

// typesafe logit
private val logitFunc = LogitFunction()
val logit: AlgebraicUnaryScalarFunction = logitFunc

/**
 * logit function
 * [![](https://jomof.github.io/kane/figures/logit-profile.svg)]
 */
fun logit(matrix: MatrixExpr): MatrixExpr = logitFunc(matrix)
fun logit(scalar: ScalarExpr): ScalarExpr = logitFunc(scalar)
fun logit(scalar: Double): Double = logitFunc(scalar)

// typesafe exp
private val expFunc = ExpFunction()
val exp: AlgebraicUnaryScalarFunction = expFunc

/**
 * exp function
 * [![](https://jomof.github.io/kane/figures/exp-profile.svg)]
 */
fun exp(matrix: MatrixExpr): MatrixExpr = expFunc(matrix)
fun exp(scalar: ScalarExpr): ScalarExpr = expFunc(scalar)
fun exp(scalar: Double): Double = expFunc(scalar)

// typesafe relu
private val reluFunc = ReluFunction()
val relu: AlgebraicUnaryScalarFunction = reluFunc

/**
 * relu function
 * [![](https://jomof.github.io/kane/figures/relu-profile.svg)]
 */
fun relu(matrix: MatrixExpr): MatrixExpr = reluFunc(matrix)
fun relu(scalar: ScalarExpr): ScalarExpr = reluFunc(scalar)
fun relu(scalar: Double): Double = reluFunc(scalar)

// typesafe tanh
private val tanhFunc = TanhFunction()
val tanh: AlgebraicUnaryScalarFunction = tanhFunc

/**
 * tanh function
 * [![](https://jomof.github.io/kane/figures/tanh-profile.svg)]
 */
fun tanh(matrix: MatrixExpr): MatrixExpr = tanhFunc(matrix)
fun tanh(scalar: ScalarExpr): ScalarExpr = tanhFunc(scalar)
fun tanh(scalar: Double): Double = tanhFunc(scalar)

// typesafe step
private val stepFunc = StepFunction()
val step: AlgebraicUnaryScalarFunction = stepFunc

/**
 * step function
 * [![](https://jomof.github.io/kane/figures/step-profile.svg)]
 */
fun step(matrix: MatrixExpr): MatrixExpr = stepFunc(matrix)
fun step(scalar: ScalarExpr): ScalarExpr = stepFunc(scalar)
fun step(scalar: Double): Double = stepFunc(scalar)

// typesafe negate
private val negateFunc = NegateFunction()
val negate: AlgebraicUnaryScalarFunction = negateFunc

/**
 * negate function
 * [![](https://jomof.github.io/kane/figures/negate-profile.svg)]
 */
fun negate(matrix: MatrixExpr): MatrixExpr = negateFunc(matrix)
fun negate(scalar: ScalarExpr): ScalarExpr = negateFunc(scalar)
fun negate(scalar: Double): Double = negateFunc(scalar)

