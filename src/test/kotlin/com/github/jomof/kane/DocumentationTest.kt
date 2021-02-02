package com.github.jomof.kane

import com.github.jomof.kane.functions.*
import com.github.jomof.kane.sheet.*
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
        println(sheet.evalGradual())
        sheet.evalGradual().assertString(
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
        println(sheet2.evalGradual())
        sheet2.evalGradual().assertString(
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
        val dollarSheet = sheet.copy("A1" to "$5.20").evalGradual()
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
        println(Kane.dataFormats)
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
         * Kane doesn't overload indexing operator to filter rows. Instead, you can use [filterRows]
         */
        println(tutorial.filterRows { row ->
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
        var sheet = sheetOfCsv {
            """
            A,B
            1.0,-0.5
            2.0,-1.0
            3.0,-1.5
            4.0,-2.0
            """
        }
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
            val x by range("A")
            val actual by range("B")
            val m by 0.0
            val b by 0.0
            val prediction by m * x + b
            val error by pow(prediction - actual, 2.0)
            val totalError by sum(error)
            listOf(totalError)
        }
        sheet.assertString(
            """
              x actual prediction     error    
              - ------ ---------- ------------ 
            1 1   -0.5     m*A1+b (m*A1+b-B1)² 
            2 2     -1     m*A2+b (m*A2+b-B2)² 
            3 3   -1.5     m*A3+b (m*A3+b-B3)² 
            4 4     -2     m*A4+b (m*A4+b-B4)² 
            b=0
            m=0
            totalError=sum((m*A+b-B)²)
        """.trimIndent()
        )

        /**
         * The formulas all look good. Let's evaluate the sheet to see the starting error.
         */
        sheet.evalGradual().assertString(
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
        sheet.evalGradual().assertString(
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
//        sheet.filterRows { row -> row["wgt"] != "NaN" }.rows.assertString("32283")

//        /**
//         * It's better to explicitly specify a value to be used when NaN.
//         * Let's set these to zero.
//         */
//        val nanIsZero = sheet["wgt"].mapDoubles {
//            if (it.isNaN()) 0.0 else it
//        }
//        count(nanIsZero).assertString("35549")
//        nans(nanIsZero).assertString("0")
//
//        /**
//         * You can also use the fillna() function to fill in values for NaN.
//         * Notice that it changes the values for all of the statistics.
//         */
//        val filled = sheet.fillna(0.0)
//        filled.statistics.assertString(
//            """
//                      record_id    month     day      year      plot   species sex     wgt
//                     ----------- -------- -------- ---------- -------- ------- --- ----------
//               count       35549    35549    35549      35549    35549                  35549
//                 NaN           0        0        0          0        0                      0
//                mean       17775  6.47402 16.10597 1990.47523   11.397               38.75198
//                 min           1        1        1       1977        1                      0
//              median       18130        7       16       1991       12                     32
//                 max       35549       12       31       2002       24                    280
//            variance 105313912.5 11.53677 68.17294   56.15037 46.23192             1370.49528
//              stddev  10262.2567  3.39658  8.25669    7.49336  6.79941                37.0202
//            skewness           0  0.05148  0.01806   -0.04414  0.12143                2.22898
//            kurtosis        -1.2 -1.20493 -1.06419   -1.28476 -1.14783                5.99371
//                  cv     0.57734  0.52465  0.51265    0.00376   0.5966                0.95531
//                   ∑   631883475   230145   572551   70759404   405152                1377594
//        """.trimIndent()
//        )
    }

    @Test
    fun `grouping by columns and expressions`() {
        val zoo = readCsv("data/zoo.csv", delimiter = ',')
        println(zoo)
        //listOf("a").groupBy()
    }
}
