package com.github.jomof.kane

import com.github.jomof.kane.sheet.*
import com.github.jomof.kane.functions.*
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
         * While the semantics are immutable, copy didn"t duplicate any memory unnecessarily. So
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
}
