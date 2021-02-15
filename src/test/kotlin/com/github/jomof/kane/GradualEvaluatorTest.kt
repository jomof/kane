package com.github.jomof.kane

import com.github.jomof.kane.impl.constant
import com.github.jomof.kane.impl.functions.percentile
import com.github.jomof.kane.impl.functions.sp500
import com.github.jomof.kane.impl.functions.unaryMinus
import com.github.jomof.kane.impl.randomOf
import org.junit.Test

class GradualEvaluatorTest {
    private val measurements = sheetOfCsv(
        """
        date,height,weight,gender
        2000-01-01,42.849980,157.500553,male
        2000-01-02,49.607315,177.340407,male
        2000-01-03,56.293531,171.524640,male
        2000-01-04,48.421077,144.251986,female
        2000-01-05,46.556882,152.526206,male
        2000-01-06,68.448851,168.272968,female
        2000-01-07,70.757698,136.431469,male
        2000-01-08,58.909500,176.499753,female
        2000-01-09,76.435631,174.094104,female
        2000-01-10,45.306120,177.540920,male
        """
    )


    @Test
    fun `basic scalar`() {
        val x by constant(1.0)
        (x + x).eval().assertString("2")
        (-x).eval().assertString("-1")
    }

    @Test
    fun `basic matrix`() {
        val m by rowOf(1.0, 2.0)
        (m * m).eval().assertString("1|4")
        (-m).eval().assertString("-1|-2")
    }

    @Test
    fun `basic random variable`() {
        val roll1 by randomOf(1.0 to 6.0)
        val roll2 by randomOf(1.0 to 6.0)
        val roll3 by randomOf(1.0 to 6.0)
        val x = roll1
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
        stdev(health).eval().assertString("9.35414")
        count(health).eval().assertString("6") // Default loops in evalGradual()
        cv(health).eval().assertString("0.53452")
        percentile(health, 0.05).eval().assertString("5")
        percentile(health, 0.95).eval().assertString("30")
        percentile(health, 0.80).eval().assertString("25")
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
    fun `mean of random`() {
        val roll by randomOf(1.0 to 6.0)
        val health by mean(roll + roll + roll + roll + roll)
        health.eval().assertString("health=17.5")
    }

    @Test
    fun `sum of mean of random`() {
        val roll by randomOf(1.0 to 6.0)
        val health by mean(roll) + mean(roll)
        val x = health.eval()
        health.eval().assertString("health=7")
    }

    @Test
    fun `sum of mean of single random`() {
        val roll by randomOf(1.0 to 10.0)
        val health by sum(roll) + sum(roll)
        health.eval().assertString("health=110")
    }

    @Test
    fun `anonymous random`() {
        val health by randomOf(1.0 to 6.0) + randomOf(1.0 to 6.0) + randomOf(1.0 to 6.0)
        health.eval().assertString("health=11")
    }

    @Test
    fun `accumulated step`() {
        val health by randomOf(-1.0 to 3.0)
        val step by step(health)
        val mean by mean(step)
        mean.eval().assertString("mean=0.8")
    }

    @Test
    fun `repro random issue`() {
        val sheet = sheetOf {
            val a1 by randomOf(1928.0 to 2019.0)
            val b2 by a1
            val x = b2
            listOf(b2)
        }
        sheet.assertString(
            """
                      A             B 
          ------------------------ -- 
        1 random(1928.0 to 2019.0)    
        2                          A1 
        """.trimIndent()
        )
    }

    @Test
    fun `repro random issue 2`() {
        val sheet = sheetOf {
            val a1 by randomOf(1928.0 to 2019.0)
            val a2 by sp500(a1)
            val a3 by mean(a2)
            listOf(a3)
        }
        sheet.assertString(
            """
                          A            
              ------------------------ 
            1 random(1928.0 to 2019.0) 
            2                sp500(A1) 
            3                 mean(A2) 
        """.trimIndent()
        )
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
    fun `repro NamedMatrix issue`() {
        val sheet = sheetOfCsv(
            """
            A,B
            1.0,-0.5
            """
        )
            .copy {
                val x by column("A")
                val error by x - x
                listOf(error)
            }
    }

    @Test
    fun `binary of sheet ranges`() {
        val sheet = sheetOfCsv(
            """
            A,B
            1.0,-0.5
            2.0,-1.0
            3.0,-1.5
            4.0,-2.0
            """
        )
            .copy {
                val x by column("A")
                val actual by column("B")
                val m by 0.0
                val b by 0.0
                val prediction by m * x + b
                val error by pow(prediction - actual, 2.0)
                val totalError by sum(error)
                listOf(prediction, totalError)
            }
        val evaled = sheet.eval()
        evaled.assertString(
            """
              x actual prediction error 
              - ------ ---------- ----- 
            1 1   -0.5          0  0.25 
            2 2     -1          0     1 
            3 3   -1.5          0  2.25 
            4 4     -2          0     4 
            b=0
            m=0
            totalError=7.5            
        """.trimIndent()
        )
    }

    @Test
    fun `repro formula adjustment in filtering`() {
        val bmi = measurements.copy {
            val bmi by column("weight") / pow(column("height"), 2.0)
            listOf(bmi)
        }
        val expected = bmi.eval().filterRows { row -> row["gender"] == "male" }
        expected.assertString(
            """
                  date     height    weight  gender   bmi   
               ---------- -------- --------- ------ ------- 
             1 2000-01-01 42.84998 157.50055   male 0.08578 
             2 2000-01-02 49.60731 177.34041   male 0.07206 
             3 2000-01-03 56.29353 171.52464   male 0.05413 
             5 2000-01-05 46.55688 152.52621   male 0.07037 
             7 2000-01-07  70.7577 136.43147   male 0.02725 
            10 2000-01-10 45.30612 177.54092   male 0.08649
            """.trimIndent()
        )
        val filtered = bmi.filterRows { row -> row["gender"] == "male" }
        filtered.assertString(
            """
                date [A]  height [B] weight [C] gender [D] bmi [E] 
               ---------- ---------- ---------- ---------- ------- 
             1 2000-01-01   42.84998  157.50055       male  C1/B1² 
             2 2000-01-02   49.60731  177.34041       male  C2/B2² 
             3 2000-01-03   56.29353  171.52464       male  C3/B3² 
             5 2000-01-05   46.55688  152.52621       male  C4/B4² 
             7 2000-01-07    70.7577  136.43147       male  C5/B5² 
            10 2000-01-10   45.30612  177.54092       male  C6/B6² 
            """.trimIndent()
        )
        filtered.eval().assertString(
            """
                  date     height    weight  gender   bmi   
               ---------- -------- --------- ------ ------- 
             1 2000-01-01 42.84998 157.50055   male 0.08578 
             2 2000-01-02 49.60731 177.34041   male 0.07206 
             3 2000-01-03 56.29353 171.52464   male 0.05413 
             5 2000-01-05 46.55688 152.52621   male 0.07037 
             7 2000-01-07  70.7577 136.43147   male 0.02725 
            10 2000-01-10 45.30612 177.54092   male 0.08649
            """.trimIndent()
        )
        // This is the real test. Filter then eval should be the same as eval then filter.
        filtered.eval().assertString(expected.toString())
    }
}