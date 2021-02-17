package com.github.jomof.kane

import org.junit.Test

/**
 * Kane is a command-line spreadsheet with a Kotlin DSL suitable for using in
 * Jupyter notebook.
 */
class DocumentationTest {

    @Test
    fun `introductory section`() {
        /**
         * Unlike, for example, a Pandas dataframe a Kane sheet can hold formulas in cells not just data.
         * Here"s an example of constructing a very simple Kane sheet.
         */
        val sheet = sheetOf {
            val a1 by 1.0
            val a2 by a1 + a1
            listOf(a2)
        }
        println(sheet)
        sheet.assertString("""
                A   
              ----- 
            1     1 
            2 A1+A1 
        """.trimIndent()
        )

        /**
         * As you can see, the formula A1+A1 is preserved in the sheet cell A2.
         *
         * You can evaluate the sheet with the eval() function to see the result of A1+A1.
         */
        println(sheet.eval())
        sheet.eval().assertString(
            """
              A 
              - 
            1 1 
            2 2 
        """.trimIndent()
        )

        /**
         * The benefit is that you can change the value of a cell to see it"s effect.
         * For example, we can change cell A1 to 2.
         */
        val sheet2 = sheet.copy("A1" to 2.0)
        println(sheet2.eval())
        sheet2.eval().assertString(
            """
              A 
              - 
            1 2 
            2 4 
        """.trimIndent()
        )

        /**
         * Sheet instances are immutable, so a new sheet was created with the copy(...) function.
         * While the semantics are immutable, copy didn't duplicate any memory unnecessarily. So
         * new sheets are inexpensive to create and destroy.
         *
         * Sheet cells support formatting. For example, we can set cell "A1" to a value in
         * dollars.
         */
        val dollarSheet = sheet.copy("A1" to "$5.20").eval()
        println(dollarSheet)
        dollarSheet.assertString("""
                 A   
              ------ 
            1  ${"$"}5.20 
            2 ${"$"}10.40 
        """.trimIndent()
        )

        /**
         * You can get a list of data formats supported by using Kane.dataFormats
         */
        Kane.dataFormats.assertString(
            """
                         format               type       
                  -------------------- ----------------- 
                1               double            double 
                2    currency (${'$'}1,000)            dollar 
                3 currency (${'$'}1,000.12) dollars and cents 
                4  yyyy-MM-dd HH:mm:ss              date 
                5           yyyy-MM-dd              date 
                6               string            String 
            """.trimIndent()
        )
    }

    @Test
    fun `reading data section--dealing with large CSV files`() {
        /**
         * When a CSV file is very large it can be useful to reduce it to a useful subset.
         * Let's look at a table of US COVID-19 hospital statistics (found at data.gov).
         * This data is pretty large, so let's first read a smale sample to see what's in
         * there.
         */
        val peek = readCsv("data/covid.csv", sample = 0.001)
        //println(peek.html)

        /**
         * As you can see, we sampled around 75 rows out of 87,000+ and there are 93 columns.
         *
         * We can get a more readable list of columns with the 'types' field on the sheet.
         */
        println(peek.describe())
        println(peek.describe().html)

        /**
         * So there are 90+ columns as well.
         *
         * Let's keep all of the rows, but filter down to just a few interesting columns.
         */
        val filtered = readCsv(
            "data/covid.csv",
            keep = listOf("collection_week", "hospital_name", "all_adult_hospital_inpatient_bed_occupied_7_day_avg")
        )
        println(filtered)

        /**
         * That's a little bit more manageable.
         *
         * Let's look at some summary statistics to see what this data looks like.
         */
        println(filtered.describe())

        /**
         * Hmm, that 'min' value looks a little suspicious. This data uses '-999999' to represent the case when the
         * value isn't known for this row. That's definitely going to skew other statistics as well.
         *
         * When Kane sees a value of Double.NaN it ignores that value in statistics. So let's replace the special value
         * '-999999' with Double.NaN.
         */
        val covid = filtered.mapDoubles { if (it.toInt() == -999999) Double.NaN else it }
        println(covid.describe())

        /**
         * That's better. We can see there were 12,750 missing values, leaving 74,619 rows with values.
         * Also notices the mean, min, and other statistics have changed reflect the removed -999999 values.
         *
         * Finally, let's save this to a new .csv file so that we can use it for other demos.
         */
        covid.writeCsv("data/covid-slim.csv")
    }

    @Test
    fun `reading data section--Pandas comparison`() {
        /**
         * This section shows how to read and manipulate csv files with Kane.
         * It follows along with an excellent Pandas tutorial at
         * [https://data36.com/pandas-tutorial-1-basics-reading-data-files-dataframes-data-selection/]
         * so you can see the Kane equivalent to Pandas commands.
         *
         * First, read "zoo.csv".
         */
        val zoo = readCsv("data/zoo.csv", delimiter = ',')
        println(zoo)

        /**
         * Read the pandas tutorial csv. Notice the change in delimiter and also the explicitly specified
         * column names.
         */
        val tutorial = readCsv(
            "data/pandas_tutorial_read.csv",
            delimiter = ';',
            names = listOf("my_datetime", "event", "country", "user_id", "source", "topic")
        )
        println(tutorial)

        /**
         * You can information about the data types.
         */
        println(tutorial.types)

        /**
         * You can show the first few lines of the sheet with head()
         */
        println(tutorial.head())

        /**
         * You can show the last few lines of the sheet with tail()
         */
        println(tutorial.tail())

        /**
         * You can select a few items at random with sample()
         */
        println(tutorial.sample())

        /**
         * You can select columns from the sheet by indexing on column names
         */
        println(tutorial["user_id", "country"])

        /**
         * You can also select by Excel-like column name ("A", "B", etc)
         */
        println(tutorial["B", "D"])

        /**
         * Kane doesn't overload indexing operator to filter rows. Instead, you can use [filter]
         */
        println(tutorial.filter { row ->
            row["source"] == "SEO"
        })

        /**
         * Functions can be used after each other to do multiple things at once.
         */
        println(tutorial.head()["country", "user_id"])
    }

    @Test
    fun `Kane Goal Seeking`() {
        /**
         * Because Kane sheets can contain formulas in addition to plain data, it's possible to seek a goal. You can
         * minimize a cell in the sheet by changing other cells that you choose. This is done with the minimize(...)
         * function.
         *
         * Under the covers, Kane uses gradient descent to try to reach the goal so the formulas need to be
         * differentiable.
         *
         * This walk-through will show a very simple case of using minimize(...).
         *
         * Topics covered:
         * - Using sheetOfCsv(...) to construct a sheet from a string in CSV format.
         * - Adding rows, columns, and cells to an existing sheet.
         * - Using minimize(...) to seek a goal
         */
        var sheet = sheetOfCsv(
            """
            A,B
            1.0,-0.5
            2.0,-1.0
            3.0,-1.5
            4.0,-2.0
            """
        )
        sheet.assertString(
            """
              A   B  
              - ---- 
            1 1 -0.5 
            2 2   -1 
            3 3 -1.5 
            4 4   -2 
        """.trimIndent()
        )

        /**
         * We'd like to know if the data in column A is related to column B linearly. That is, what is the best y=mx+b
         * relationship?
         *
         * Let's define 'm' and 'b' with starting values of 0. These are the change variables that will be modified
         * in order to minimize the error function we're about to create.
         *
         * Next, we'll define 'prediction' which is column A times m plus b. I've called column A 'x' to increase
         * readability.
         *
         * After that, we'll define 'error' which is the per row error function. The function needs to be differentiable
         * and it needs to be positive or zero since we're going to minimize it. I chose (prediction - actual)².
         *
         * Lastly, we'll define 'totalError' which is the sum of all of the 'error' rows.
         */
        sheet = sheet.copy {
            val x by column("A")
            val actual by column("B")
            val m by 0.0
            val b by 0.0
            val prediction by m * x + b
            val error by pow(prediction - actual, 2.0)
            val totalError by sum(error)
            listOf(prediction, totalError)
        }
        sheet.assertString(
            """
              x [A] actual [B] prediction [C] error [D] 
              ----- ---------- -------------- --------- 
            1     1       -0.5         m*A1+b  (C1-B1)² 
            2     2         -1         m*A2+b  (C2-B2)² 
            3     3       -1.5         m*A3+b  (C3-B3)² 
            4     4         -2         m*A4+b  (C4-B4)² 
            b=0
            m=0
            totalError=sum(D1:D4)
        """.trimIndent()
        )

        /**
         * The formulas all look good. Let's evaluate the sheet to see the starting error.
         */
        sheet.eval().assertString(
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

        /**
         * So the total error is 7.5 when m=0 and b=0.
         *
         * Now, let's minimize 'totalError' by changing the values of 'm' and 'b'
         */
        sheet = sheet.minimize("totalError", listOf("m", "b"))

        /**
         * Okay, the sheet was minimized. Notice that 'm' is now set to -0.5 and 'b' was left at 0.
         */
        sheet.eval().assertString(
            """
              x actual prediction error 
              - ------ ---------- ----- 
            1 1   -0.5       -0.5     0 
            2 2     -1         -1     0 
            3 3   -1.5       -1.5     0 
            4 4     -2         -2     0 
            b=0
            m=-0.5
            totalError=0
        """.trimIndent()
        )

        println(sheet.html)
    }

    @Test
    fun `advanced reading data section--types and missing data`() {
        val sheet = readCsv("data/surveys.csv")
        /**
         * Get the type of a single column.
         */
        println(sheet["sex"].type)
        println(sheet["record_id"].type)

        /**
         * Print the types for all columns.
         */
        println(sheet.types)

        /**
         * Print the mean of a column.
         */
        println(mean(sheet["wgt"]))
        mean(sheet["wgt"]).assertString("42.67243")

        /**
         * There are missing values in this column. You can see them with the nans() function.
         */
        println(nans(sheet["wgt"]))
        nans(sheet["wgt"]).assertString("3266")

        /**
         * How many rows do have a value for 'wgt'? There are a few ways
         * (1) The count() function only counts real values, not NaNs
         * (2) Check filter for rows where 'wgt' != "NaN"
         */
        count(sheet["wgt"]).assertString("32283")
        sheet.filter { row -> row["wgt"] != "NaN" }.rows.assertString("35549")

        /**
         * It's better to explicitly specify a value to be used when NaN.
         * Let's set these to zero.
         */
        val nanIsZero = sheet["wgt"].mapDoubles {
            if (it.isNaN()) 0.0 else it
        }
        count(nanIsZero).assertString("35549")
        nans(nanIsZero).assertString("0")

        /**
         * You can also use the fillna() function to fill in values for NaN.
         * Notice that it changes the values for all of the statistics.
         */
        val filled = sheet.fillna(0.0)
        filled.describe().assertString(
            """
                      record_id    month     day      year      plot       wgt    
                     ----------- -------- -------- ---------- -------- ---------- 
               count       35549    35549    35549      35549    35549      35549 
                 NaN           0        0        0          0        0          0 
                mean       17775  6.47402 16.10597 1990.47523   11.397   38.75198 
                 min           1        1        1       1977        1          0 
                 25%        9243        4       10       1984        5         17 
              median       18130        7       16       1991       12         32 
                 75%       27017       10       23       1997       17         47 
                 max       35549       12       31       2002       24        280 
            variance 105313912.5 11.53677 68.17294   56.15037 46.23192 1370.49528 
               stdev  10262.2567  3.39658  8.25669    7.49336  6.79941    37.0202 
            skewness           0  0.05148  0.01806   -0.04414  0.12143    2.22898 
            kurtosis        -1.2 -1.20493 -1.06419   -1.28476 -1.14783    5.99371 
                  cv     0.57734  0.52465  0.51265    0.00376   0.5966    0.95531 
                 sum   631883475   230145   572551   70759404   405152    1377594 
        """.trimIndent()
        )
    }

    @Test
    fun `grouping by columns and expressions`() {
        val measurements = sheetOfCsv(
            """
            date,height,weight,gender
            2000-01-01,183,77,male
            2000-01-02,180,80,male
            2000-01-03,177,76,male
            2000-01-04,162,63,female
            2000-01-05,163,72,male
            2000-01-06,165,64,female
            2000-01-07,185,81,male
            2000-01-08,158,55,female
            2000-01-09,150,59,female
            2000-01-10,181,78,male
            """
        )

        val group = measurements.groupBy("gender")

        // The result is a GroupBy object
        group.assertString(
            """
                       key selector 
                       ------------ 
                gender       gender 
            """.trimIndent()
        )

        // Access a single group
        group["female"].assertString(
            """
               date [A]  height [B] weight [C] gender [D] 
              ---------- ---------- ---------- ---------- 
            4 2000-01-04        162         63     female 
            6 2000-01-06        165         64     female 
            8 2000-01-08        158         55     female 
            9 2000-01-09        150         59     female 
            """.trimIndent()
        )

        // Describe a groupBy
        group.describe().assertString(
            """
                  count height NaN height mean height min height 25% height median height 75% height max height variance height stdev height skewness height kurtosis height cv height sum height count weight NaN weight mean weight min weight 25% weight median weight 75% weight max weight variance weight stdev weight skewness weight kurtosis weight cv weight sum weight 
                   ------------ ---------- ----------- ---------- ---------- ------------- ---------- ---------- --------------- ------------ --------------- --------------- --------- ---------- ------------ ---------- ----------- ---------- ---------- ------------- ---------- ---------- --------------- ------------ --------------- --------------- --------- ---------- 
              male            6          0   178.16667        163        177           181        183        185        62.56667      7.90991        -1.34112         0.41593    0.0444       1069            6          0    77.33333         72         76            78         80         81        10.26667      3.20416         -0.5698        -0.62338   0.04143        464 
            female            4          0      158.75        150        158           162        165        165           42.25          6.5        -0.54923        -1.13276   0.04094        635            4          0       60.25         55         59            63         64         64        16.91667      4.11299        -0.40452        -1.42042   0.06827        241 
            """.trimIndent()
        )

        // Statistic of groupBy
        median(group).assertString(
            """
                   median height median weight 
                   ------------- ------------- 
              male           181            78 
            female           162            63 
            """.trimIndent()
        )

        // Multiple statistics of groupBy
        group.aggregate(mean, median).assertString(
            """
               mean height median height mean weight median weight 
               ----------- ------------- ----------- ------------- 
          male   178.16667           181    77.33333            78 
        female      158.75           162       60.25            63 
        """.trimIndent()
        )

        // Add formula
        val bmi = measurements.copy {
            val bmi by (column("weight")) /
                    pow(column("height") / 100.0, 2.0)
            listOf(bmi)
        }
        bmi.assertString(
            """
                date [A]  height [B] weight [C] gender [D]     bmi [E]    
               ---------- ---------- ---------- ---------- -------------- 
             1 2000-01-01        183         77       male   C1/(B1/100)² 
             2 2000-01-02        180         80       male   C2/(B2/100)² 
             3 2000-01-03        177         76       male   C3/(B3/100)² 
             4 2000-01-04        162         63     female   C4/(B4/100)² 
             5 2000-01-05        163         72       male   C5/(B5/100)² 
             6 2000-01-06        165         64     female   C6/(B6/100)² 
             7 2000-01-07        185         81       male   C7/(B7/100)² 
             8 2000-01-08        158         55     female   C8/(B8/100)² 
             9 2000-01-09        150         59     female   C9/(B9/100)² 
            10 2000-01-10        181         78       male C10/(B10/100)²  
            """.trimIndent()
        )
        bmi.eval().assertString(
            """
                  date    height weight gender    bmi   
               ---------- ------ ------ ------ -------- 
             1 2000-01-01    183     77   male 22.99262 
             2 2000-01-02    180     80   male 24.69136 
             3 2000-01-03    177     76   male 24.25867 
             4 2000-01-04    162     63 female 24.00549 
             5 2000-01-05    163     72   male 27.09925 
             6 2000-01-06    165     64 female 23.50781 
             7 2000-01-07    185     81   male 23.66691 
             8 2000-01-08    158     55 female 22.03173 
             9 2000-01-09    150     59 female 26.22222 
            10 2000-01-10    181     78   male  23.8088 
            """.trimIndent()
        )

        // Group by a sheet with formulas
        val formulaGroup = bmi.groupBy("gender")
        formulaGroup["male"].assertString(
            """
                date [A]  height [B] weight [C] gender [D]    bmi [E]   
               ---------- ---------- ---------- ---------- ------------ 
             1 2000-01-01        183         77       male C1/(B1/100)² 
             2 2000-01-02        180         80       male C2/(B2/100)² 
             3 2000-01-03        177         76       male C3/(B3/100)² 
             5 2000-01-05        163         72       male C4/(B4/100)² 
             7 2000-01-07        185         81       male C5/(B5/100)² 
            10 2000-01-10        181         78       male C6/(B6/100)² 
        """.trimIndent()
        )

        // Evaluate a groupBy and filter by certain columns
        val evaluatedBmi = formulaGroup.eval().columns("gender", "bmi")
        evaluatedBmi["male"].assertString(
            """
               gender    bmi   
               ------ -------- 
             1   male 22.99262 
             2   male 24.69136 
             3   male 24.25867 
             5   male 27.09925 
             7   male 23.66691 
            10   male  23.8088 
            """.trimIndent()
        )

        stdev(evaluatedBmi).assertString(
            """
                   stdev bmi 
                   --------- 
              male   1.43221 
            female     1.736 
            """.trimIndent()
        )
    }
}
