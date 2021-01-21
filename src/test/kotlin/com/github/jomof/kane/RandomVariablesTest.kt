package com.github.jomof.kane

import com.github.jomof.kane.functions.*
import com.github.jomof.kane.sheet.sheetOf
import org.junit.Test

class RandomVariablesTest {
    @Test
    fun basic() {
        val roll1 by randomOf(1.0 to 6.0)
        val roll2 by randomOf(1.0 to 6.0)
        val roll3 by randomOf(1.0 to 6.0)
        roll1.assertString("roll1=random(1.0 to 6.0)")
        val health by roll1 + roll2 + roll3
        health.assertString("health=roll1+roll2+roll3")
        health.eval().assertString("health=11")
    }

    @Test
    fun `same variable multiple times`() {
        val roll by randomOf(1.0 to 6.0)
        val health by roll + roll + roll + roll + roll
        health.eval().assertString("health=20")
        median(health).eval().assertString("20")
        stddev(health).eval().assertString("9.35414")
        count(health).eval().assertString("6") // Default loops in eval()
        coefficientOfVariation(health).eval().assertString("0.53452")
        percentile(health, 0.05).eval().assertString("5")
        percentile(health, 0.95).eval().assertString("30")
        percentile(health, 0.80).eval().assertString("25")
    }

    @Test
    fun `basic sheet`() {
        val sheet = sheetOf {
            val a1 by randomOf(1928.0 to 2019.0)
            val b1 by sp500(a1)
            val c1 by mean(a1)
            val d1 by mean(b1)
            add(c1, d1)
        }
        println(sheet)
        println(sheet.eval())
    }

    @Test
    fun `sheet with multiple variables`() {
        val sheet = sheetOf {
            val a1 by randomOf(1.0 to 6.0)
            val a2 by randomOf(1.0 to 6.0)
            val a3 by randomOf(1.0 to 6.0)
            val b1 by mean(a1 + a2 + a3)
            val b2 by median(a1 + a2 + a3)
            val b3 by stddev(a1 + a2 + a3)
            val c1 by mean(a1 + a1 + a1)
            val c2 by median(a1 + a1 + a1)
            val c3 by stddev(a1 + a1 + a1)
            val d1 by mean(pow(a1, 2.0))
            val d2 by count(a1)
            add(a1, a2, a3, b1, b2, b3, c1, c2, c3, d1, d2)
        }
        println(sheet.eval())
    }

    @Test
    fun `sheet with accumulated step`() {
        val sample = StreamingSamples()
        for(i in 1928 .. 2019) {
            sample.insert(sp500(i.toDouble()))
        }
        println("mean=${sample.mean}")
        println("median=${sample.median}")

        val sheet = sheetOf {
            val a1 by constant("random")
            val b1 by randomOf(1928.0 to 2019.0)
            val c1 by randomOf(1928.0 to 2019.0)
            val a2 by constant("s&p 500")
            val b2 by sp500(b1)
            val c2 by sp500(b1) * sp500(c1)
            val a3 by constant("mean")
            val b3 by mean(b2)
            val c3 by mean(c2)
            val a4 by constant("median")
            val b4 by median(b2)
            val c4 by median(c2)
            val a5 by constant("positive")
            val b5 by mean(step(b2))
            val c5 by mean(step(c2))
            val a6 by constant("5th percentile")
            val b6 by percentile(b2, 0.05)
            val c6 by percentile(c2, 0.05)
            val a7 by constant("95th percentile")
            val b7 by percentile(b2, 0.95)
            val c7 by percentile(c2, 0.95)
            val a8 by constant("95th percentile")
            val b8 by percentile(b2, 0.255)
            val c8 by percentile(c2, 0.255)
            add(a1, a2, b2, a3, b3, a4, b4, a5, b5, a6, b6, a7, b7, a8, b8,
                c1, c2, c3, c4, c5, c6, c7, c8)
        }
        println(sheet)
        val eval = sheet.eval()
        println(eval)
        eval["B3"].assertString("0.11572")
        eval["B4"].assertString("0.1352")
        eval["B5"].assertString("0.72826")
        eval["B6"].assertString("-0.2512")
        eval["B7"].assertString("0.4372")
        eval["B8"].assertString("-0.0119")
    }

    @Test
    fun `mean of random`() {
        val roll by randomOf(1.0 to 6.0)
        val health by mean(roll + roll + roll + roll + roll)
        health.eval().assertString("health=17.5")
    }

    @Test
    fun `sum of mean of random`() {
        val roll by randomOf(1.0 to 6.0)
        val health by mean(roll) + mean(roll)
        health.eval().assertString("health=7")
    }

    @Test
    fun `anonymous random`() {
        val health by randomOf(1.0 to 6.0) + randomOf(1.0 to 6.0) + randomOf(1.0 to 6.0)
        health.eval().assertString("health=11")
    }

    @Test
    fun `find random variable`() {
        val d6 by randomOf(1.0 to 6.0)
        val d6b by randomOf(1.0 to 6.0)
        val health by d6 + d6 + d6b
        health.findRandomVariables().assertString("[random(1.0 to 6.0), random(1.0 to 6.0)]")
    }
}