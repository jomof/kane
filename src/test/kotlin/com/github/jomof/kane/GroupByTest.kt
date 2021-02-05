package com.github.jomof.kane

import com.github.jomof.kane.functions.*
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
    private val animals = sheetOfCsv {
        """
        type,class,order,max_speed
        falcon,bird,Falconiformes,389.0
        parrot,bird,Psittaciformes,24.0
        lion,mammal,Carnivora,80.2
        monkey,mammal,Primates,NaN
        leopard,mammal,Carnivora,58.0
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
            date [A]  height [B] weight [C] gender [D] 
           ---------- ---------- ---------- ---------- 
         1 2000-01-01   42.84998  157.50055       male 
         2 2000-01-02   49.60731  177.34041       male 
         3 2000-01-03   56.29353  171.52464       male 
         5 2000-01-05   46.55688  152.52621       male 
         7 2000-01-07    70.7577  136.43147       male 
        10 2000-01-10   45.30612  177.54092       male 
        """.trimIndent()
        )
        gb["female"].assertString(
            """
           date [A]  height [B] weight [C] gender [D] 
          ---------- ---------- ---------- ---------- 
        4 2000-01-04   48.42108  144.25199     female 
        6 2000-01-06   68.44885  168.27297     female 
        8 2000-01-08    58.9095  176.49975     female 
        9 2000-01-09   76.43563   174.0941     female 
        """.trimIndent()
        )
    }

    @Test
    fun `repro range issue`() {
        val bmi = measurements.copy {
            val bmi by range("weight") / pow(range("height"), 2.0)
            listOf(bmi)
        }.showExcelColumnTags(false)
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

    @Test
    fun `animal statistics`() {
        animals.describe().assertString(
            """
                  max_speed  
                 ----------- 
           count           4 
             NaN           1 
            mean       137.8 
             min          24 
             25%          58 
          median        80.2 
             75%         389 
             max         389 
        variance 28579.22667 
          stddev   169.05392 
        skewness     1.08967 
        kurtosis      -0.714 
              cv     1.22681 
             sum       551.2
        """.trimIndent()
        )
    }

    @Test
    fun `measurements statistics`() {
        measurements.describe().assertString(
            """
                   height    weight   
                 --------- ---------- 
           count        10         10 
             NaN         0          0 
            mean  56.35866   163.5983 
             min  42.84998  136.43147 
             25%  46.55688  152.52621 
          median  56.29353  171.52464 
             75%  68.44885  176.49975 
             max  76.43563  177.54092 
        variance 141.37829  224.06903 
          stddev  11.89026   14.96894 
        skewness   0.50841   -0.68671 
        kurtosis  -1.20648   -0.98633 
              cv   0.21097     0.0915 
             sum 563.58659 1635.98301
        """.trimIndent()
        )
    }

    @Test
    fun `measurements group statistics`() {
        measurements
            .groupBy("gender")
            .describe()
            .assertString(
                """
                       count height NaN height mean height min height 25% height median height 75% height max height variance height stddev height skewness height kurtosis height cv height sum height count weight NaN weight mean weight min weight 25% weight median weight 75% weight max weight variance weight stddev weight skewness weight kurtosis weight cv weight sum weight 
                       ------------ ---------- ----------- ---------- ---------- ------------- ---------- ---------- --------------- ------------- --------------- --------------- --------- ---------- ------------ ---------- ----------- ---------- ---------- ------------- ---------- ---------- --------------- ------------- --------------- --------------- --------- ---------- 
                  male            6          0    51.89525   42.84998   45.30612      49.60731   56.29353    70.7577       106.82064      10.33541         1.11246        -0.13151   0.19916  311.37153            6          0   162.14403  136.43147  152.52621     171.52464  177.34041  177.54092       266.23803       16.3168        -0.50456        -1.09242   0.10063  972.86419 
                female            4          0    63.05376   48.42108    58.9095      68.44885   76.43563   76.43563       146.49059      12.10333         -0.1413         -1.3627   0.19195  252.21506            4          0    165.7797  144.25199  168.27297      174.0941  176.49975  176.49975       217.90257      14.76152         -0.9758        -0.82088   0.08904  663.11881 
                """.trimIndent()
            )
    }

    @Test
    fun `basic aggregation`() {
        val gp = measurements.groupBy("gender")
        val agg = gp.aggregate {
            val sumHeight by sum(column("height"))
            val meanHeight by mean(column("height"))
            listOf(sumHeight, meanHeight)
        }
        agg.assertString(
            """
               sumHeight meanHeight 
               --------- ---------- 
          male 311.37153   51.89525 
        female 252.21506   63.05376 
        """.trimIndent()
        )
    }

    @Test
    fun `aggregate list of statistics function`() {
        measurements
            .groupBy("gender")
            .aggregate(mean, sum, cv)
            .assertString(
                """
                       mean height sum height cv height mean weight sum weight cv weight 
                       ----------- ---------- --------- ----------- ---------- --------- 
                  male    51.89525  311.37153   0.19916   162.14403  972.86419   0.10063 
                female    63.05376  252.21506   0.19195    165.7797  663.11881   0.08904 
                """.trimIndent()
            )
    }

//    @Test
    fun `covid-slim`() {
        val gp = readCsv("data/covid-slim.csv")
            .groupBy("hospital_name")
            .describe()
        println(gp)
    }
}