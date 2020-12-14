package com.github.jomof.kane.rigueur

import org.junit.Test

class RigueurTest {

    private fun <T:Any> Expr<T>.assertString(expected : String) {
        assert("$this" == expected) {
            "actual:   $this\nexpected: $expected"
        }
    }

    @Test
    fun mlp() {
        val learningRate = 0.1
        val input by matrixVariable<Double>(1, 2)
        val w0 by matrixVariable<Double>(input.rows + 1, 2)
        val h1 : Matrix<Double> by logit(w0 * (input stack 1.0))
        val w1 by matrixVariable<Double>(w0.rows + 1, 2)
        val output by logit(w1 * (h1 stack 1.0))
//        val target by matrixVariable(output.columns, output.rows)
//        val error by learningRate * summation(learningRate * pow(target - output, 2.0))
//        val gradientW0 by d(error) / d(w0)
//        val gradientW1 by d(error) / d(w1)
        println(learningRate)
        println(input)
        println(w0)
        println(h1)
        println(w1)
        println(output)
//        println(target)
//        println(error)
//        println(gradientW0)
//        println(gradientW1)
//        val common by listOf(output, gradientW0, gradientW1).decompose()
//        println()
//        println(common)
    }

    @Test
    fun `variable rendering`() {
        variable<Double>().assertString("0")
        variable<Int>().assertString("0")
        val a by variable(0.0)
        a.assertString("a")
        val b by variable(0.0)
        (a + b).assertString("a+b")
        val c by a + b
        c.assertString("c=a+b")
        (a * b).assertString("a*b")
        (a - b).assertString("a-b")
        (a / b).assertString("a/b")
        (a + a * b).assertString("a+a*b")
        ((a + a) * b).assertString("(a+a)*b")
        ((a - a) * b).assertString("(a-a)*b")
        (a - a * b).assertString("a-a*b")
        (a / a * b).assertString("(a/a)*b")
        (a / (a * b)).assertString("a/a*b")
        ((a / a) * b).assertString("(a/a)*b")
        (a + a / b).assertString("a+a/b")
        ((a + a) / b).assertString("(a+a)/b")
        ((a - a) / b).assertString("(a-a)/b")
        (a + a - b).assertString("a+a-b")
        ((a + a) - b).assertString("a+a-b")
        ((a - a) - b).assertString("a-a-b")
        (a - (a - b)).assertString("a-(a-b)")
        val m by matrixVariable<Double>(5, 2)
        val n by matrixVariable<Double>(3, 5)
        val s by matrixVariable<String>(4, 5)
        m.assertString("m")
        n.assertString("n")
        s.assertString("s")
        val z by m
        z.assertString("z=m")
        val element by z[0,1]
        element.assertString("element=z[0,1]")
        z[1,0].assertString("z[1,0]")
        val mult by m * n
        mult.assertString("mult=m*n")
        (m * n).assertString("m*n")
    }

    @Test
    fun `chain rule`() {
        val x by variable<Double>()
        val y by variable<Double>()
        val f1 by pow(x, 2.0) + pow(y, 3.0)
        f1.assertString("f1=pow(x,2)+pow(y,3)")
        val d1 by d(f1) / d(x)
        d1.assertString("d1=d(pow(x,2)+pow(y,3))/d(x)")
        differentiate(d1).assertString("d1'=2*x")
        val d2 by d(f1) / d(y)
        d2.assertString("d2=d(pow(x,2)+pow(y,3))/d(y)")
        differentiate(d2).assertString("d2'=3*pow(y,2)")
        val f2 by pow(x, 2.0) * pow(y, 3.0)
        f2.assertString("f2=pow(x,2)*pow(y,3)")
        val df2dx by d(f2) / d(x)
        df2dx.assertString("df2dx=d(pow(x,2)*pow(y,3))/d(x)")
        differentiate(df2dx).assertString("df2dx'=2*x*pow(y,3)")
        val df2dy by d(f2) / d(y)
        df2dy.assertString("df2dy=d(pow(x,2)*pow(y,3))/d(y)")
        differentiate(df2dy).assertString("df2dy'=pow(x,2)*3*pow(y,2)")
        val assigned by differentiate(df2dy)
        assigned.assertString("assigned=pow(x,2)*3*pow(y,2)")
        val f3 by logit(x * y)
        f3.assertString("f3=logit(x*y)")
        val df3dx by d(f3) / d(x)
        df3dx.assertString("df3dx=d(logit(x*y))/d(x)")
        differentiate(df3dx).assertString("df3dx'=logit(x*y)*(1-logit(x*y))*y")
        val df3dy by d(f3) / d(y)
        df3dy.assertString("df3dy=d(logit(x*y))/d(y)")
        differentiate(df3dy).assertString("df3dy'=logit(x*y)*(1-logit(x*y))*x")
    }
}

