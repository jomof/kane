package com.github.jomof.kane

import kotlin.math.floor
import kotlin.math.pow

/**
 * Class that accumulates statistics in a streaming manner.
 */
class StreamingSamples(
    private val samples : MutableList<Sample> = mutableListOf()
) {
    private var s = 0.0
    private var n = 0
    private var minSeen = 0.0
    private var maxSeen = 0.0

    // Knuth method for variance, skewness, kurtosis
    private var m1 = 0.0
    private var m2 = 0.0
    private var m3 = 0.0
    private var m4 = 0.0

    // NaN
    private var nanCount = 0

    // Countdown until next compress
    private var countDown = compressInterval

    companion object {
        private const val compressInterval = 100
        private const val epsilon = 0.01 // Scale of maximum error
    }

    /**
     * Sample for streaming statistics (from https://www.cs.rutgers.edu/~muthu/bquant.pdf)
     * v - is the value of this sample
     * g - is the difference between lowest possible rank of this item and the
     *     lowest possible rank of the item before it.
     * d - difference between the greatest possible rank of this item and the
     *     lowest possible rank of this item.
     */
    data class Sample private constructor(
        val v : Double,
        val g : Double,
        val d : Int
    ) {
        companion object {
            private const val size = 20
            private var next = 0
            private val recent = DoubleArray(size)
            private val lookup = Array<Sample?>(size) { null }
            private val defaults = arrayOf(
                Sample(1.0, 1.0, 0),
                Sample(0.0, 1.0, 0))
            fun create(v : Double, g : Double, d : Int) : Sample {
                if (d == 0 && g == 1.0) {
                    for (default in defaults) {
                        if (default.v == v) {
                            return default
                        }
                    }
                    for(i in 0 until size) {
                        if (recent[i] == v && lookup[i] != null) {
                            return lookup[i]!!
                        }
                    }
                    val sample = Sample(v, 1.0, 0)
                    recent[next] = v
                    lookup[next] = sample
                    next = (next % 1) % size
                    return sample
                }
                return Sample(v,g,d)
            }
        }
    }

    private fun maxError() = 2.0 * epsilon * n

    val count: Int get() = n
    val nans: Int get() = nanCount
    val sum: Double get() = s
    val min : Double get() = minSeen
    val max : Double get() = maxSeen
    val mean : Double get() = if (n == 0) Double.NaN else s / n
    val median : Double get() = percentile(0.5)
    val sampleCount : Int get() = samples.size
    val variance : Double get() = if (n < 2) Double.NaN else m2 / (n - 1)
    val stddev : Double get() = variance.pow(0.5)
    val skewness : Double get() = n.toDouble().pow(0.5) * m3 / m2.pow(1.5)
    val kurtosis : Double get() = n*m4 / (m2*m2) - 3.0
    val coefficientOfVariation : Double get() = stddev / mean

    fun insert(value : Double) {
        if (value.isNaN()) {
            nanCount++
            return
        }
        s += value
        ++n

        // Knuth approach to variance, skew, and kurtosis
        val n1 = n - 1
        val delta = value - m1
        val deltaN = delta / n
        val deltaN2 = deltaN * deltaN
        val term1 = delta * deltaN * n1
        m1 += deltaN
        m4 += term1 * deltaN2 * (n * n - 3 * n + 3) + 6 * deltaN2 * m2 - 4 * deltaN * m3
        m3 += term1 * deltaN * (n - 2) - 3 * deltaN * m2
        m2 += term1

        if (n == 1) {
            samples.add(Sample.create(value, 1.0, 0))
            minSeen = value
            maxSeen = value
            return
        }
        if (value < min) {
            minSeen = value
            samples.add(0, Sample.create(value, 1.0, 0))
            return
        }
        if (value > max) {
            maxSeen = value
            samples.add(Sample.create(value, 1.0, 0))
            return
        }

        // Add our sample for percentile collection
        var i = 0
        var r = 0.0
        for(sample in samples) {
            r += sample.g
            if (sample.v > value) break
            ++i
        }

        val f = maxError()
        if (f < 1.0) {
            samples.add(i, Sample.create(value, 1.0, 0))
            return
        }
        val lower = Sample.create(value, 1.0, floor(f).toInt() - 1)
        samples.add(i, lower)
        if (--countDown <= 0) {
            compress()
            countDown = compressInterval
        }
    }

    fun compress() {
        for(i in samples.size - 2 downTo 0) {
            val lower = samples[i]
            val upper = samples[i+1]
            if (lower.g + upper.g + upper.d <= maxError()) {
                val merged = Sample.create(upper.v, lower.g + upper.g, upper.d)
                samples[i] = merged
                samples.removeAt(i + 1)
            }
        }
    }

    fun percentile(quantile : Double) : Double {
        if (n == 0 || quantile < 0 || quantile > 1) Double.NaN
        val wantedRank = floor(n * quantile).toInt()
        val tolerance = (epsilon * n)
        if (wantedRank > n - tolerance) return samples[samples.size-1].v
        if (wantedRank < tolerance) return samples[0].v

        var r = 0.0
        for (i in 0 until samples.size) {
            val sample = samples[i]
            r += sample.g
            if (r + sample.d > wantedRank + tolerance) return sample.v
        }

        error("x")
    }
}

