package com.github.jomof.kane

import com.github.jomof.kane.functions.count
import com.github.jomof.kane.functions.mean
import com.github.jomof.kane.functions.nans
import com.github.jomof.kane.functions.plus
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
            add(a2)
        }
        println(sheet)
        sheet.assertString("""
                A   
              ----- 
            1     1 
            2 A1+A1 
        """.trimIndent())

        /**
         * As you can see, the formula A1+A1 is preserved in the sheet cell A2.
         *
         * You can evaluate the sheet with the eval() function to see the result of A1+A1.
         */
        println(sheet.eval())
        sheet.eval().assertString("""
              A 
              - 
            1 1 
            2 2 
        """.trimIndent())

        /**
         * The benefit is that you can change the value of a cell to see it"s effect.
         * For example, we can change cell A1 to 2.
         */
        val sheet2 = sheet.copy("A1" to 2.0)
        println(sheet2.eval())
        sheet2.eval().assertString("""
              A 
              - 
            1 2 
            2 4 
        """.trimIndent())

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
        """.trimIndent())

        /**
         * You can get a list of data formats supported by using Kane.dataFormats
         */
        println(Kane.dataFormats)
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
        val zoo = readCsv("zoo.csv", delimiter = ',')
        println(zoo)

        /**
         * Read the pandas tutorial csv. Notice the change in delimiter and also the explicitly specified
         * column names.
         */
        val tutorial = readCsv(
            "pandas_tutorial_read.csv",
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
    fun `advanced reading data section--types and missing data`() {
        val sheet = readCsv("surveys.csv")
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
        sheet.filterRows { row -> row["wgt"] != "NaN" }.rows.assertString("32283")

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
        filled.statistics.assertString(
            """
                      record_id    month     day      year      plot   species sex     wgt    
                     ----------- -------- -------- ---------- -------- ------- --- ---------- 
               count       35549    35549    35549      35549    35549                  35549 
                 NaN           0        0        0          0        0                      0 
                mean       17775  6.47402 16.10597 1990.47523   11.397               38.75198 
                 min           1        1        1       1977        1                      0 
              median       18130        7       16       1991       12                     32 
                 max       35549       12       31       2002       24                    280 
            variance 105313912.5 11.53677 68.17294   56.15037 46.23192             1370.49528 
              stddev  10262.2567  3.39658  8.25669    7.49336  6.79941                37.0202 
            skewness           0  0.05148  0.01806   -0.04414  0.12143                2.22898 
            kurtosis        -1.2 -1.20493 -1.06419   -1.28476 -1.14783                5.99371 
                  cv     0.57734  0.52465  0.51265    0.00376   0.5966                0.95531 
                   ∑   631883475   230145   572551   70759404   405152                1377594 
        """.trimIndent()
        )

    }
}
