package com.github.jomof.kane

import com.github.jomof.kane.functions.plus
import com.github.jomof.kane.types.dollars
import com.github.jomof.kane.types.percent
import org.junit.Test

class WithTypeTest {
    @Test
    fun scalar() {
        val money by dollars(100)
        val pct by percent(100)
        val x = percent(money)
        (percent(money) + percent(money)).eval().assertString("20,000%")
        percent(money).assertString("money=10,000%")
        dollars(pct).assertString("pct=\$100.00")
        (dollars(pct) + dollars(pct)).eval().assertString("\$200.00")
    }

    @Test
    fun matrix() {
        val money by rowOf(dollars(100))
        val pct by rowOf(percent(100))
        val doublePct by percent(money + money)

        doublePct.eval().assertString("doublePct=[20,000%]")
        (percent(money) + percent(money)).eval().assertString("[20,000%]")
        percent(money).assertString("money=[10,000%]")
        dollars(pct).assertString("pct=[\$100.00]")
        (dollars(pct) + dollars(pct)).eval().assertString("[\$200.00]")
    }
}