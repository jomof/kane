package com.github.jomof.kane

import org.junit.Test

class StreamingStatisticsTest {
    @Test
    fun `one element`() {
        val s = StreamingSamples()
        s.insert(2.0)
        s.percentile(0.5).assertString("2.0")
    }

    @Test
    fun `two element`() {
        val s = StreamingSamples()
        s.insert(2.0)
        s.insert(3.0)
        s.percentile(0.49).assertString("2.0")
        s.percentile(0.51).assertString("3.0")
    }

    @Test
    fun `three element`() {
        val s = StreamingSamples()
        s.insert(4.0)
        s.insert(3.0)
        s.insert(2.0)
        s.percentile(0.10).assertString("2.0")
        s.percentile(0.20).assertString("2.0")
        s.percentile(0.33).assertString("2.0")
        s.percentile(0.34).assertString("3.0")
        s.percentile(0.50).assertString("3.0")
        s.percentile(0.60).assertString("3.0")
        s.percentile(0.65).assertString("3.0")
        s.percentile(0.67).assertString("4.0")
        s.percentile(0.70).assertString("4.0")
        s.percentile(0.80).assertString("4.0")
        s.percentile(0.90).assertString("4.0")
        s.percentile(0.90).assertString("4.0")
    }

    @Test
    fun `repeated three element`() {
        val s = StreamingSamples()
        repeat(3000) {
            s.insert(4.0)
            s.insert(3.0)
            s.insert(2.0)

            s.percentile(0.10).assertString("2.0")
            s.percentile(0.50).assertString("3.0")
            s.percentile(0.90).assertString("4.0")
        }
    }

    @Test
    fun `repeated three element with compress`() {
        var maxElements = 0
        repeat(20) { outer ->
            val s = StreamingSamples()
            repeat(30000) { inner ->
                if (inner % (outer + 1) == outer) {
                    s.compress()
                    if (s.sampleCount > maxElements) {
                        maxElements = s.sampleCount
                        println("New max elements ($outer, $inner): $maxElements")
                    }
                }
                s.insert(4.0)
                s.insert(3.0)
                s.insert(2.0)

                s.percentile(0.0).assertString("2.0")
                s.percentile(0.20).assertString("2.0")
                s.percentile(0.50).assertString("3.0")
                s.percentile(0.80).assertString("4.0")
                s.percentile(1.0).assertString("4.0")
                s.mean.assertString("3.0")
                s.median.assertString("3.0")
            }
        }
    }

    @Test
    fun basic() {
        val s = StreamingSamples()
        s.insert(11.0)
        s.insert(20.0)
        s.insert(18.0)
        s.insert(5.0)
        s.insert(12.0)
        s.insert(6.0)
        s.insert(3.0)
        s.insert(2.0)

        s.percentile(0.5).assertString("11.0")
        s.percentile(0.3).assertString("5.0")
        s.percentile(0.7).assertString("12.0")
        s.mean.assertString("9.625")
        s.min.assertString("2.0")
        s.max.assertString("20.0")
        s.median.assertString("11.0")
        s.count.assertString("8")
        s.sum.assertString("77.0")
        s.variance.assertString("45.982142857142854")
        s.stddev.assertString("6.7810134093026875")
    }

    @Test
    fun `check standard deviation`() {
        val s = StreamingSamples()
        s.insert(4.0)
        s.insert(7.0)
        s.insert(13.0)
        s.insert(16.0)

        s.mean.assertString("10.0")
        s.variance.assertString("30.0")
        s.stddev.assertString("5.477225575051661")
        s.skewness.assertString("0.0")
        s.kurtosis.assertString("-1.64")
    }
}