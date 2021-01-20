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
        health.eval(samples = 1000).assertString("health=10")
    }

    @Test
    fun `same variable multiple times`() {
        val roll by randomOf(1.0 to 6.0)
        val health by roll + roll + roll + roll + roll
        health.eval(samples = 1000).assertString("health=15")
        median(health).eval(samples = 1000).assertString("15")
        stddev(health).eval().assertString("8.6164")
        count(health).eval().assertString("100") // Default loops in eval()
        coefficientOfVariation(health).eval().assertString("0.47869")
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
    fun `sheet with accumulated step`() {
        val sheet = sheetOf {
            val a1 by randomOf(-4.0 to 5.0)
            val b1 by mean(a1)
            val c1 by mean(step(a1))

            add(a1, b1, c1)
        }
        println(sheet)
        println(sheet.eval())
    }

    @Test
    fun `mean of random`() {
        val roll by randomOf(1.0 to 6.0)
        val health by mean(roll + roll + roll + roll + roll)
        health.eval().assertString("health=18")
    }

    @Test
    fun `sum of mean of random`() {
        val roll by randomOf(1.0 to 6.0)
        val health by mean(roll) + mean(roll)
        health.eval().assertString("health=7.2")
    }

    @Test
    fun `anonymous random`() {
        val health by randomOf(1.0 to 6.0) + randomOf(1.0 to 6.0) + randomOf(1.0 to 6.0)
        health.eval().assertString("health=13")
    }

    @Test
    fun `find random variable`() {
        val d6 by randomOf(1.0 to 6.0)
        val d6b by randomOf(1.0 to 6.0)
        val health by d6 + d6 + d6b
        health.findRandomVariables().assertString("{d6=random(1.0 to 6.0), d6b=random(1.0 to 6.0)}")
    }
}