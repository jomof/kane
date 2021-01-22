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
         * Here's an example of constructing a very simple Kane sheet.
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
         * The benefit is that you can change the value of a cell to see it's effect.
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
            1  ${'$'}5.20 
            2 ${'$'}10.40 
        """.trimIndent())

        /**
         * You can get a list of data formats supported by using Kane.dataFormats
         */
        println(Kane.dataFormats)
    }


}
