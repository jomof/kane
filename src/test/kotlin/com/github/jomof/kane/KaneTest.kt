package com.github.jomof.kane

import org.junit.Test
import com.github.jomof.kane.Expr.*

internal class KaneTest {
    private fun Expr.assertString(expected : String) {
        assert("$this" == expected) {
            "actual:   $this\nexpected: $expected"
        }
    }
    private fun Expr.assertStructure(expected : Expr) {
        assert(renderStructure() == expected.renderStructure()) {
            "actual:   ${renderStructure()}\nexpected: ${expected.renderStructure()}"
        }
    }
    @Test
    fun college() {
        val growth = 1.1
        val tuition = dollars(100_000.00)
        val years by listOf(2021, 2022, 2023, 2024).toColumn()
        val sheet by years
            .addColumn {
                if (it.row == 0) tuition * growth
                else (tuition + it[it.column, it.row - 1]) * growth
            }

        println(sheet)

    }

    @Test
    fun `constant 1`() {
        val input by dataFrameOf(1, 1)
        val w0 by dataFrameOf(input.rows + 1, 1)
        val h1 by w0 * (input appendrow 1.0)
        h1.assertString("h1 = (w0[0,0]*input[0,0] + w0[1,0])")
    }

    @Test
    fun mlp() {
        val learningRate by Constant(0.1)
        val input by dataFrameOf(1, 2)
        val w0 by dataFrameOf(input.rows + 1, 2)
        val h1 by logistic(w0 * (input appendrow 1.0))
        val w1 by dataFrameOf(h1.rows + 1, 2)
        val output by logistic(w1 * (h1 appendrow 1.0))
        val target by dataFrameOf(output.columns, output.rows)
        val error by summation(learningRate * pow(target - output, 2.0))
        val gradientW0 by d(error) / d(w0)
        println(learningRate)
        println(input)
        println(w0)
        println(h1)
        println(w1)
        println(output)
        println(target)
        println(error)
        println(gradientW0)
    }

    @Test
    fun multiply() {
        val a by dataFrameOf(3, 1)
        val b by dataFrameOf(1, 3)
        val result by a * b
        println(result)
        result.assertString("result = (a[0,0]*b[0,0] + a[1,0]*b[0,1] + a[2,0]*b[0,2])")
    }

    @Test
    fun `repro stack overflow`() {
        val learningRate by Expr.Constant(0.1)
        val target by dataFrameOf(1, 1)
        val error by summation(learningRate * target)
        println(error)
        error.assertString("error = (0.1*target[0,0])")
    }

    @Test
    fun `expand transitive`() {
        val a by dataFrameOf(1, 2)
        val b by dataFrameOf(1, 2)
        val difference by a - b
        val times by 3.0 * difference
        times.assertString("times = col 3.0*(a[0,0]-b[0,0])|3.0*(a[0,1]-b[0,1])")
    }

    @Test
    fun `simple transitive`() {
        val a by dataFrameOf(1,1)
        val b by a
        a.assertString("a = 0.0")
        b.assertString("b = a[0,0]")
    }

    @Test
    fun `top levels`() {
        val columns by dataFrameOf(1,3)
        columns.assertString("columns = col 0.0|0.0|0.0")
    }

    @Test
    fun `simple minus`() {
        val a by dataFrameOf(1,1)
        val b by dataFrameOf(1,1)
        a.assertStructure(Named("a", NamedHole("a", 0, 0)))
        val c by a
        c.assertStructure(Named("c", NamedHole("a", 0, 0)))
        ((a - b) * 3.0).assertString("(a[0,0]-b[0,0])*3.0")
    }

    @Test
    fun `reductions`() {
        val t0 by Constant(1.0) * Constant(5.0)
        val t1 by Constant(5.0) * Constant(1.0)
        val t2 by Constant(0.0) * Constant(5.0)
        val t3 by Constant(5.0) * Constant(0.0)
        t0.assertString("t0 = 5.0")
        t1.assertString("t1 = 5.0")
        t2.assertString("t2 = 0.0")
        t3.assertString("t3 = 0.0")
    }

    @Test
    fun `table self reference`() {
        val t0 by Constant(1.0)
        val t1 by transpose(t0.addColumns(3) { it.left + 1.0 })
        t1.assertString("t1 = col 1.0|(1.0 + [0,0])|(1.0 + [1,0])|(1.0 + [2,0])")
    }
}