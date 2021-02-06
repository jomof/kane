package com.github.jomof.kane

import com.github.jomof.kane.functions.StdevFunction
import com.github.jomof.kane.impl.functions.AlgebraicUnaryScalarStatisticFunction

/**
 * Calculate the sample standard deviation.
 */
val stdev: AlgebraicUnaryScalarStatisticFunction = StdevFunction()