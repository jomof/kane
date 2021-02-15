package com.github.jomof.kane

import com.github.jomof.kane.impl.StreamingSamples
import com.github.jomof.kane.impl.constant
import com.github.jomof.kane.impl.findRandomVariables
import com.github.jomof.kane.impl.functions.percentile
import com.github.jomof.kane.impl.functions.sp500
import com.github.jomof.kane.impl.randomOf
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
    fun canary() {
        val roll by randomOf(1.0 to 6.0)
        val mean by mean(roll)
        roll.assertString("roll=random(1.0 to 6.0)")
        mean.assertString("mean=mean(roll)")
        mean.eval().assertString("mean=3.5")
    }

    @Test
    fun `same variable multiple times`() {
        val roll by randomOf(1.0 to 6.0)
        val health by roll + roll + roll + roll + roll
        health.eval().assertString("health=20")
        median(health).eval().assertString("20")
        stdev(health).eval().assertString("9.35414")
        count(health).eval().assertString("6") // Default loops in evalGradual()
        cv(health).eval().assertString("0.53452")
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
            listOf(c1, d1)
        }
        sheet.assertString(
            """
                          A                B         C        D    
              ------------------------ --------- -------- -------- 
            1 random(1928.0 to 2019.0) sp500(A1) mean(A1) mean(B1) 
        """.trimIndent()
        )
        sheet.eval().assertString(
            """
                A   B     C    D  
              ---- --- ------ --- 
            1 1974 14% 1973.5 12% 
        """.trimIndent()
        )
    }

    @Test
    fun `sheet with multiple variables`() {
        val sheet = sheetOf {
            val a1 by randomOf(1.0 to 6.0)
            val a2 by randomOf(1.0 to 6.0)
            val a3 by randomOf(1.0 to 6.0)
            val b1 by mean(a1 + a2 + a3)
            val b2 by median(a1 + a2 + a3)
            val b3 by stdev(a1 + a2 + a3)
            val c1 by mean(a1 + a1 + a1)
            val c2 by median(a1 + a1 + a1)
            val c3 by stdev(a1 + a1 + a1)
            val d1 by mean(pow(a1, 2.0))
            val d2 by count(a1)
            listOf(a1, a2, a3, b1, b2, b3, c1, c2, c3, d1, d2)
        }
        sheet.eval().assertString(
            """
              A    B       C        D    
              - ------- ------- -------- 
            1 4    10.5    10.5 15.16667 
            2 4      11      12      216 
            3 3 2.96491 5.13538          
        """.trimIndent()
        )
    }


    @Test
    fun `repro scalar stat issue`() {
        val b1 by randomOf(1928.0 to 1929.0)
        val c1 by randomOf(1928.0 to 1929.0)
        step(sp500(b1) * sp500(c1)).eval().assertString("1")
        step(sp500(b1) / sp500(c1)).eval().assertString("1")
        step(sp500(b1) + sp500(c1)).eval().assertString("1")
        step(sp500(b1) - sp500(c1)).eval().assertString("1")
    }


    @Test
    fun `sheet with accumulated step`() {
        val sample = StreamingSamples()
        for (i in 1928..2019) {
            sample.insert(sp500.doubleOp(i.toDouble()))
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
            val a7 by constant("50th percentile")
            val b7 by percentile(b2, 0.50)
            val c7 by percentile(c2, 0.50)
            val a8 by constant("95th percentile")
            val b8 by percentile(b2, 0.95)
            val c8 by percentile(c2, 0.95)
            listOf(
                a1, a2, b2, a3, b3, a4, b4, a5, b5, a6, b6, a7, b7, a8, b8,
                c1, c2, c3, c4, c5, c6, c7, c8
            )
        }
        sheet.assertString(
            """
                 A                    B                        C            
          --------------- ------------------------ ------------------------ 
        1          random random(1928.0 to 2019.0) random(1928.0 to 2019.0) 
        2         s&p 500                sp500(B1)      sp500(B1)*sp500(C1) 
        3            mean                 mean(B2)                 mean(C2) 
        4          median               median(B2)               median(C2) 
        5        positive           mean(step(B2))           mean(step(C2)) 
        6  5th percentile      percentile(B2,0.05)      percentile(C2,0.05) 
        7 50th percentile       percentile(B2,0.5)       percentile(C2,0.5) 
        8 95th percentile      percentile(B2,0.95)      percentile(C2,0.95) 
        """.trimIndent()
        )
        val eval = sheet.eval()
        eval.assertString(
            """
                 A           B       C    
          --------------- ------- ------- 
        1          random    1974    1975 
        2         s&p 500     14%      1% 
        3            mean     12%      1% 
        4          median     14%      1% 
        5        positive 0.72826 0.60421 
        6  5th percentile  (-25%)   (-5%) 
        7 50th percentile     14%      1% 
        8 95th percentile     47%     10% 
        """.trimIndent()
        )
        eval["B3"].assertString("12%")
        eval["B4"].assertString("14%")
        eval["B5"].assertString("0.72826")
        eval["B6"].assertString("(-25%)")
        eval["B7"].assertString("14%")
        eval["B8"].assertString("47%")
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
        health.findRandomVariables().assertString("[d6=random(1.0 to 6.0), d6b=random(1.0 to 6.0)]")
    }
}