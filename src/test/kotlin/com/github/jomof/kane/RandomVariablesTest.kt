package com.github.jomof.kane

import com.github.jomof.kane.functions.plus
import org.junit.Test

class RandomVariablesTest {
    @Test
    fun basic() {
        val d6 by randomOf(1.0 to 6.0)
        d6.assertString("d6=random(1.0 to 6.0)")
        val health by d6 + d6 + d6
        health.assertString("health=d6+d6+d6")
       // health.eval().assertString("health=d6+d6+d6")
    }

    @Test
    fun `find random variable`() {
        val d6 by randomOf(1.0 to 6.0)
        val d6b by randomOf(1.0 to 6.0)
        val health by d6 + d6 + d6b
        health.findRandomVariables().assertString("{d6=random(1.0 to 6.0), d6b=random(1.0 to 6.0)}")
    }
}