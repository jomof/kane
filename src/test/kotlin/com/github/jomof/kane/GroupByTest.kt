package com.github.jomof.kane

import com.github.jomof.kane.functions.div
import com.github.jomof.kane.functions.pow
import com.github.jomof.kane.functions.times
import com.github.jomof.kane.sheet.*
import org.junit.Test

class GroupByTest {
    private val measurements = sheetOfCsv {
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
    }

    @Test
    fun basic() {
        val check = sheetOfCsv {
            """
                cats, dogs, lions
                1,2,5
                3,4,6
            """
        }.groupOf {
            val key1 by 2.0 * column("cats")
            val key2 by column("cats") * column("dogs")
            listOf(key1, key2)
        }
        println(check)
        check.assertString(
            """
                 key selector 
                 ------------ 
            key1       2*cats 
            key2    cats*dogs 
            """.trimIndent()
        )

    }

    @Test
    fun `group by column name`() {
        val check = sheetOfCsv {
            """
                cats, dogs, lions
                1,2,5
                3,4,6
            """
        }.groupBy("cats", "dogs")
        println(check)
        check.assertString(
            """
                 key selector 
                 ------------ 
            cats         cats 
            dogs         dogs
            """.trimIndent()
        )
    }

    @Test
    fun `keys of groups`() {
        val gb = measurements.groupBy("gender")
        gb["male"].assertString(
            """
                  date     height    weight  gender 
               ---------- -------- --------- ------ 
             1 2000-01-01 42.84998 157.50055   male 
             2 2000-01-02 49.60731 177.34041   male 
             3 2000-01-03 56.29353 171.52464   male 
             5 2000-01-05 46.55688 152.52621   male 
             7 2000-01-07  70.7577 136.43147   male 
            10 2000-01-10 45.30612 177.54092   male
        """.trimIndent()
        )
        gb["female"].assertString(
            """
                 date     height    weight  gender 
              ---------- -------- --------- ------ 
            4 2000-01-04 48.42108 144.25199 female 
            6 2000-01-06 68.44885 168.27297 female 
            8 2000-01-08  58.9095 176.49975 female 
            9 2000-01-09 76.43563  174.0941 female
        """.trimIndent()
        )
    }

    @Test
    fun `repro range issue`() {
        val bmi = measurements.copy {
            val bmi by range("weight") / pow(range("height"), 2.0)
            listOf(bmi)
        }
        bmi.head().assertString(
            """
                 date     height    weight  gender   bmi  
              ---------- -------- --------- ------ ------ 
            1 2000-01-01 42.84998 157.50055   male C1/B1² 
            2 2000-01-02 49.60731 177.34041   male C2/B2² 
            3 2000-01-03 56.29353 171.52464   male C3/B3² 
            4 2000-01-04 48.42108 144.25199 female C4/B4² 
            5 2000-01-05 46.55688 152.52621   male C5/B5²
        """.trimIndent()
        )
    }

    @Test
    fun `repro formula adjustment in filtering`() {
        val bmi = measurements.copy {
            val bmi by range("weight") / pow(range("height"), 2.0)
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
                  date     height    weight  gender   bmi  
               ---------- -------- --------- ------ ------ 
             1 2000-01-01 42.84998 157.50055   male C1/B1² 
             2 2000-01-02 49.60731 177.34041   male C2/B2² 
             3 2000-01-03 56.29353 171.52464   male C3/B3² 
             5 2000-01-05 46.55688 152.52621   male C4/B4² 
             7 2000-01-07  70.7577 136.43147   male C5/B5² 
            10 2000-01-10 45.30612 177.54092   male C6/B6²
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

    @Test
    fun `repro formula adjustment in grouping`() {
        val bmi = measurements.copy {
            val bmi by range("weight") / pow(range("height"), 2.0)
            listOf(bmi)
        }
        val evalThenGroup = bmi.eval().groupBy("gender")["female"]
        val groupThenEval = bmi.groupBy("gender")["female"].eval()

        evalThenGroup.assertString(groupThenEval.toString())
    }
}