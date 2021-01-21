package com.github.jomof.kane

import kotlin.math.floor
import kotlin.math.pow

/**
 * Class that accumulates statistics in a streaming manner.
 */
class StreamingSamples(
    private val samples : MutableList<Sample> = mutableListOf()
) {
    private val epsilon = 0.01 // Scale of maximum error
    private var s = 0.0
    private var n = 0
    private var minSeen = 0.0
    private var maxSeen = 0.0
    // Knuth method for variance, skewness, kurtosis
    private var m1 = 0.0
    private var m2 = 0.0
    private var m3 = 0.0
    private var m4 = 0.0
    // Countdown until next compress
    private var countDown = 100

    /**
     * Sample for streaming statistics (from https://www.cs.rutgers.edu/~muthu/bquant.pdf)
     * v - is the value of this sample
     * g - is the difference between lowest possible rank of this item and the
     *     lowest possible rank of the item before it.
     * d - difference between the greatest possible rank of this item and the
     *     lowest possible rank of this item.
     */
    data class Sample(
        val v : Double,
        val g : Double,
        val d : Int
    )

    private fun maxError() : Double {
        return 2.0 * epsilon * n
    }

    val count : Int get() = n
    val sum : Double get() = s
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
        s += value
        ++n

        // Knuth approach to variance, skew, and kurtosis
        val n1 = n - 1
        val delta = value - m1
        val deltaN = delta / n
        val deltaN2 = deltaN * deltaN
        val term1 = delta * deltaN * n1
        m1 += deltaN;
        m4 += term1 * deltaN2 * (n*n - 3*n + 3) + 6 * deltaN2 * m2 - 4 * deltaN * m3
        m3 += term1 * deltaN * (n - 2) - 3 * deltaN * m2
        m2 += term1

        if (n == 1) {
            samples.add(Sample(value, 1.0, 0))
            minSeen = value
            maxSeen = value
            return
        }
        if (value < min) {
            minSeen = value
            samples.add(0, Sample(value, 1.0, 0))
            return
        }
        if (value > max) {
            maxSeen = value
            samples.add(Sample(value, 1.0, 0))
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
            samples.add(i, Sample(value, 1.0, 0))
            return
        }
        val lower = Sample(value, 1.0, floor(f).toInt() - 1)
        samples.add(i, lower)
        if (--countDown <= 0) {
            compress()
            countDown = 100
        }
    }

    fun compress() {
        for(i in samples.size - 2 downTo 0) {
            val lower = samples[i]
            val upper = samples[i+1]
            if (lower.g + upper.g + upper.d <= maxError()) {
                val merged = Sample(upper.v, lower.g + upper.g, upper.d)
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

