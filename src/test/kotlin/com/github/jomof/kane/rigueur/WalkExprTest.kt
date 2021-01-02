package com.github.jomof.kane.rigueur

import org.junit.Test

class WalkExprTest {

    private fun replacePlan(expr : ScalarExpr<Double>) : String {
        val sb = StringBuilder()
        expr.replaceTopDown {
            sb.append("$it\n")
            it
        }
        return "$sb"
    }

    private fun replaceBottomUpPlan(expr : ScalarExpr<Double>) : String {
        val sb = StringBuilder()
        expr.replaceBottomUp {
            sb.append("$it\n")
            it
        }
        return "$sb"
    }

    @Test
    fun `replace bottom up plan`() {
        val c by constant(1.0)
        val b by constant(2.0)
        replaceBottomUpPlan(c).assertString("""
            1
            c=1
        """.trimIndent())
        replaceBottomUpPlan(b + c).assertString("""
            2
            b=2
            1
            c=1
            b+c
        """.trimIndent())
    }

    @Test
    fun `replace top down plan`() {
        val c by constant(1.0)
        val b by constant(2.0)
        replacePlan(c).assertString("""
            c=1
            1
        """.trimIndent())
        replacePlan(b + c).assertString("""
            b+c
            b=2
            2
            c=1
            1
        """.trimIndent())
    }
}