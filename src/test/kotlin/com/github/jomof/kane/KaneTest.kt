package com.github.jomof.kane

import org.junit.Test
import com.github.jomof.kane.Expr.*

internal class KaneTest {
    private fun Expr.assertString(expected : String) {
        assert("$this" == expected) {
            "actual:   $this\nexpected: $expected"
        }
    }
    private fun Expr.assertStructure(expected : String) {
        assert(renderStructure() == expected) {
            "actual:   ${renderStructure()}\nexpected: $expected"
        }
    }

    @Test
    fun `test repro 1`() {
        // original Times(Named("w0",dataFrameOf(2,1,NamedHole("w0",0,0),NamedHole("w0",1,0))), AppendRow(Named("input",NamedHole("input",0,0)), Constant(1.0)))
        //          w0[0,0]|w0[1,0]*(input[0,0] appendrow 1)
        // first    Plus(Times(NamedHole("w0",0,0), NamedHole("input",0,0)), NamedHole("w0",1,0))
        //          w0[0,0]*input[0,0]+w0[1,0]
        // second   Plus(NamedHole("w0",1,0), Times(NamedHole("w0",0,0), NamedHole("input",0,0)))
        //          w0[1,0]+w0[0,0]*input[0,0]
        val original = Times(Named("w0",dataFrameOf(2,1,NamedHole("w0",0,0),NamedHole("w0",1,0))), AppendRow(Named("input",NamedHole("input",0,0)), Constant(1.0)))
        val first = original.reduce("h1")
        val second = first.reduce("h1")
        assert(first == second)
    }

    @Test
    fun `product preserves order`() {
        val a = NamedHole("a",0,0)
        val b = NamedHole("b",0,0)
        val c = NamedHole("c",0,0)
        val expr = a * b * c
        expr.assertStructure("Times(Times(NamedHole(\"a\",0,0), NamedHole(\"b\",0,0)), NamedHole(\"c\",0,0))")
        val elements = expr.getAllTimesElements()
        assert(elements[0] == a)
        assert(elements[1] == b)
        assert(elements[2] == c)
        val expr2 = elements.product()
        expr2.assertStructure("Times(Times(NamedHole(\"a\",0,0), NamedHole(\"b\",0,0)), NamedHole(\"c\",0,0))")
    }

    @Test
    fun `sum preserves order`() {
        val a = NamedHole("a",0,0)
        val b = NamedHole("b",0,0)
        val c = NamedHole("c",0,0)
        val expr = a + b + c
        expr.assertStructure("Plus(Plus(NamedHole(\"a\",0,0), NamedHole(\"b\",0,0)), NamedHole(\"c\",0,0))")
        val elements = expr.getAllPlusElements()
        assert(elements[0] == a)
        assert(elements[1] == b)
        assert(elements[2] == c)
        val expr2 = elements.sum()
        expr2.assertStructure("Plus(Plus(NamedHole(\"a\",0,0), NamedHole(\"b\",0,0)), NamedHole(\"c\",0,0))")
    }

    @Test
    fun college() {
        val growth = 1.1
        val tuition by Variable()
        val years by listOf(2021, 2022, 2023, 2024).toColumn()
        val sheet by years
            .addColumn {
                if (it.row == 0) tuition * growth
                else (tuition + it[it.column, it.row - 1]) * growth
            }
        val total by sheet[1,3]
        println(total)
        println(total.holes())
        println(total.eval { dollars(100.0) })

        println(sheet)
        println(sheet.holes())
        println(total.eval { dollars(100_000.0) })
    }

    @Test
    fun `constant 1`() {
        val input by dataFrameOf(1, 1)
        val w0 by dataFrameOf(input.rows + 1, 1)
        val h1 by w0 * (input appendrow 1.0)
        h1.assertString("h1 = w0[0,0]*input[0,0]+w0[1,0]")
    }

    @Test
    fun `simple differentiate`() {
        val w0 by dataFrameOf(1, 1)
        val target by dataFrameOf(1, 1)
        val error by summation(target - w0)
        val gradientW1 by d(error) / d(w0)
        println(error)
        println(gradientW1)
    }

    @Test
    fun `chain rule`() {
        val x by Variable()
        val y by Variable()
        val f1 by pow(x, 2.0) + pow(y, 3.0)
        f1.assertString("f1 = x[0,0]^2+y[0,0]^3")
        val d1 by d(f1) / d(x)
        d1.assertString("d1 = 2*x[0,0]")
        val d2 by d(f1) / d(y)
        d2.assertString("d2 = 3*y[0,0]^2")
        val f2 by pow(x, 2.0) * pow(y, 3.0)
        f2.assertString("f2 = x[0,0]^2*y[0,0]^3")
        val df2dx by d(f2) / d(x)
        df2dx.assertString("df2dx = 2*x[0,0]*y[0,0]^3")
        val df2dy by d(f2) / d(y)
        df2dy.assertString("df2dy = 3*x[0,0]^2*y[0,0]^2")
        val f3 by logistic(x * y)
        f3.assertString("f3 = logit(x[0,0]*y[0,0])")
        val df3dx by d(f3) / d(x)
        df3dx.assertString("df3dx = logit(x[0,0]*y[0,0])*(1-logit(x[0,0]*y[0,0]))*y[0,0]")
        val df3dy by d(f3) / d(y)
        df3dy.assertString("df3dy = logit(x[0,0]*y[0,0])*(1-logit(x[0,0]*y[0,0]))*x[0,0]")
        println(f1)
    }

    @Test
    fun mlp() {
        val learningRate = 0.1
        val input by dataFrameOf(1, 2)
        val w0 by dataFrameOf(input.rows + 1, 2)
        val h1 by logistic(w0 * (input appendrow 1.0))
        val w1 by dataFrameOf(h1.rows + 1, 2)
        val output by logistic(w1 * (h1 appendrow 1.0))
        val target by dataFrameOf(output.columns, output.rows)
        val error by learningRate * summation(learningRate * pow(target - output, 2.0))
        val gradientW0 by d(error) / d(w0)
        val gradientW1 by d(error) / d(w1)
        println(learningRate)
        println(input)
        println(w0)
        println(h1)
        println(w1)
        println(output)
        println(target)
        println(error)
        println(gradientW0)
        println(gradientW1)
        val common by listOf(output, gradientW0, gradientW1).decompose()
        println()
        println(common)
    }

    @Test
    fun `multiplication reduction`() {
        val learningRate = 0.1
        val input by dataFrameOf(1, 1)
        val w0 by dataFrameOf(input.rows, 1)
        val h1 by w0 * input
        val w1 by dataFrameOf(h1.rows, 1)
        val output by w1 * h1
        val target by dataFrameOf(output.columns, output.rows)
        val error by 0.5 * summation(learningRate * pow(target - output, 2.0))
        val grad by d(error) / d(w0)
        println(grad.reduce())
        grad.assertString("grad = -0.1*(target[0,0]-w1[0,0]*w0[0,0]*input[0,0])*w1[0,0]*input[0,0]")
    }

    @Test
    fun multiply() {
        val a by dataFrameOf(3, 1)
        val b by dataFrameOf(1, 3)
        val result by a * b
        println(result)
        result.assertString("result = a[0,0]*b[0,0]+a[1,0]*b[0,1]+a[2,0]*b[0,2]")
    }

    @Test
    fun `repro stack overflow`() {
        val learningRate by Constant(0.1)
        val target by dataFrameOf(1, 1)
        val error by summation(learningRate * target)
        println(error)
        error.assertString("error = 0.1*target[0,0]")
    }

    @Test
    fun `expand transitive`() {
        val a by dataFrameOf(1, 2)
        val b by dataFrameOf(1, 2)
        val difference by a - b
        difference.assertString("difference = col a[0,0]-b[0,0]|a[0,1]-b[0,1]")
        val times by 3.0 * difference
        val x = times
        times.assertString("times = col 3*a[0,0]-3*b[0,0]|3*a[0,1]-3*b[0,1]")
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
        ((a - b) * 3.0).assertString("(a[0,0]-b[0,0])*3")
    }

    @Test
    fun `reductions`() {
        val hole by dataFrameOf(1,1)[0,0]
        val t0 by Constant(1.0) * Constant(5.0)
        val t1 by Constant(5.0) * Constant(1.0)
        val t2 by Constant(0.0) * Constant(5.0)
        val t3 by Constant(5.0) * Constant(0.0)
        val t4 by Constant(5.0) * (hole * Constant(2.0))
        val t5 by Constant(5.0) * (Constant(2.0) * hole)
        t0.assertString("t0 = 5")
        t1.assertString("t1 = 5")
        t2.assertString("t2 = 0")
        t3.assertString("t3 = 0")
        t4.assertString("t4 = 10*hole[0,0]")
        t5.assertString("t5 = 10*hole[0,0]")
    }

    @Test
    fun `table self reference`() {
        val t0 by Constant(1.0)
        val t1 by transpose(t0.addColumns(3) { it.left + 1.0 })
        t1.assertString("t1 = col 1|[0,0]+1|[1,0]+1|[2,0]+1")
    }

    @Test
    fun `precedence checks`() {
        val holes by dataFrameOf(1, 3)
        val expr1 by (holes[0,0] + holes[0,1]) * holes[0,2]
        val expr2 by (holes[0,0] + holes[0,1]) + holes[0,2]
        val expr3 by holes[0,0] - holes[0,1] + holes[0,2]
        val expr4 by (holes[0,0] - holes[0,1]) * holes[0,2]
        expr1.assertString("expr1 = (holes[0,0]+holes[0,1])*holes[0,2]")
        expr2.assertString("expr2 = holes[0,0]+holes[0,1]+holes[0,2]")
        expr3.assertString("expr3 = (holes[0,0]-holes[0,1])+holes[0,2]")
        expr4.assertString("expr4 = (holes[0,0]-holes[0,1])*holes[0,2]")

        println(expr4.holes())
    }

    @Test
    fun `order is preserved`() {
        val a by Variable()
        val b by Variable()
        val c by Variable()
        val expr by (a + b) * c
        val x = expr
        expr.assertString("expr1 = (holes[0,0]+holes[0,1])*holes[0,2]")
    }

    @Test
    fun `combine products and sums`() {
        val a by dataFrameOf(1,1)
        val b by dataFrameOf(1,1)
        val c by dataFrameOf(1,1)
        val expr by Summation(dataFrameOf(2, 2) { a * b + c })
        expr.assertString("expr = 4*a[0,0]*b[0,0]+4*c[0,0]")
    }

    @Test
    fun `simplest decompose`() {
        val a by dataFrameOf(1,1)
        val b by dataFrameOf(1,1)
        val c by dataFrameOf(1,1)
        val expr by a * b + a * b * c
        expr.assertString("expr = a[0,0]*b[0,0]+a[0,0]*b[0,0]*c[0,0]")
        val table by expr.decompose()
        table.assertString("expr = table[0,0]+c[0,0]*table[0,0]\n\n" +
                "table\n" +
                "-----\n" +
                "0|a[0,0]*b[0,0]")
    }

    @Test
    fun `expr table`() {
        val a by dataFrameOf(1,1)
        val b by dataFrameOf(1,1)
        val c by dataFrameOf(1,1)
        val expr by Summation(dataFrameOf(2, 2) { a * b + c })
        println(expr)
        val table by expr.decompose()
        println(table)
    }
}