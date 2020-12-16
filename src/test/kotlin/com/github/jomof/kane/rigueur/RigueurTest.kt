package com.github.jomof.kane.rigueur

import org.junit.Test
import kotlin.math.absoluteValue

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
        val h1 by logit(w0 * (input stack 1.0))
        val w1 by matrixVariable<Double>(w0.rows + 1, 2)
        val output by logit(w1 * (h1 stack 1.0))
        val target by matrixVariable<Double>(output.columns, output.rows)
        val error by summation(learningRate * pow(target - output, 2.0))
        val gradientW0 by d(error) / d(w0)
        val gradientW1 by d(error) / d(w1)
//        println(learningRate)
//        println(input)
//        println(w0)
//        println(h1)
//        println(w1)
//        println(output)
//        println(target)
//        println(error)
//        println(gradientW0)
//        println(gradientW1)
        val i : Matrix<Double> = differentiate(instantiateVariables(gradientW0))
        for(column in 0 until i.columns) {
            for(row in 0 until i.rows) {
                println(instantiateVariables(i[column, row]))
            }

        }
          println(instantiateVariables(gradientW0))
//        println(instantiateMatrixElements(gradientW1))
//        val common by listOf(output, gradientW0, gradientW1).decompose()
//        println()
//        println(common)
    }

    @Test
    fun `summation expands on element access`() {
        val a by matrixVariable<Double>(1,3)
        val b by matrixVariable<Double>(3,2)
        val s by summation(a) / b
        instantiateVariables(s[0,0]).assertString("(a[0,0]+a[0,1]+a[0,2])/b[0,0]")
    }

    @Test
    fun `matrix element rendering`() {
        val m by matrixVariable<Double>(2,3)
        val e by m[1,1]
        e.assertString("e=m[1,1]")
        (matrixVariable<Double>(2,3)).assertString("matrix(2x3)")
        val p by pow(m, 2.0)
        p.assertString("p=pow(m,2)")
        val pe by p[1,2]
        instantiateVariables(pe).assertString("pe=pow(m[1,2],2)")
        instantiateVariables(p[1,2]).assertString("pow(m[1,2],2)")
        val column by matrixVariable<Double>(1,3)
        val s by 1.0 stack column
        s.assertString("s=1 stack column")
        instantiateVariables(s[0,0]).assertString("1")
        instantiateVariables(s[0,1]).assertString("column[0,0]")
        val a1 by s[0,0]
        instantiateVariables(a1).assertString("a1=1")
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
        val x = c
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
        val m : Matrix<Double> by matrixVariable<Double>(5, 2)
        val n : Matrix<Double> by matrixVariable<Double>(3, 5)
        val s by matrixVariable<String>(4, 5)
        m.assertString("m")
        n.assertString("n")
        s.assertString("s")
        val z by m
        z.assertString("z=m")
        val element by z[0,1]
        element.assertString("element=m[0,1]")
        z[1,0].assertString("m[1,0]")
        val mult by m * n
        mult.assertString("mult=m*n")
        (m * n).assertString("m*n")
        (m stack 1.0).assertString("m stack 1")
        (m stack m stack m).assertString("m stack m stack m")
        ((m stack m) stack m).assertString("m stack m stack m")
        (m stack (m stack m)).assertString("m stack m stack m")
    }

    @Test
    fun `matrix chain rule`() {
        val m by matrixVariable<Double>(1, 2)
        val x by m[0,0]
        val y by m[0,1]
        val f1 by pow(x, 2.0) + pow(y, 3.0)
        f1.assertString("f1=pow(m[0,0],2)+pow(m[0,1],3)")
        val d1 by d(f1) / d(x)
        d1.assertString("d1=d(pow(m[0,0],2)+pow(m[0,1],3))/d(m[0,0])")
        differentiate(d1).assertString("d1'=2*m[0,0]")
        val d2 by d(f1) / d(y)
        d2.assertString("d2=d(pow(m[0,0],2)+pow(m[0,1],3))/d(m[0,1])")
        differentiate(d2).assertString("d2'=3*pow(m[0,1],2)")
        val f2 by pow(x, 2.0) * pow(y, 3.0)
        f2.assertString("f2=pow(m[0,0],2)*pow(m[0,1],3)")
        val df2dx by d(f2) / d(x)
        df2dx.assertString("df2dx=d(pow(m[0,0],2)*pow(m[0,1],3))/d(m[0,0])")
        differentiate(df2dx).assertString("df2dx'=2*m[0,0]*pow(m[0,1],3)")
        val df2dy by d(f2) / d(y)
        df2dy.assertString("df2dy=d(pow(m[0,0],2)*pow(m[0,1],3))/d(m[0,1])")
        differentiate(df2dy).assertString("df2dy'=pow(m[0,0],2)*3*pow(m[0,1],2)")
        val assigned by differentiate(df2dy)
        assigned.assertString("assigned=pow(m[0,0],2)*3*pow(m[0,1],2)")
        val f3 by logit(x * y)
        f3.assertString("f3=logit(m[0,0]*m[0,1])")
        val df3dx by d(f3) / d(x)
        df3dx.assertString("df3dx=d(logit(m[0,0]*m[0,1]))/d(m[0,0])")
        differentiate(df3dx).assertString("df3dx'=logit(m[0,0]*m[0,1])*(1-logit(m[0,0]*m[0,1]))*m[0,1]")
        val df3dy by d(f3) / d(y)
        df3dy.assertString("df3dy=d(logit(m[0,0]*m[0,1]))/d(m[0,1])")
        differentiate(df3dy).assertString("df3dy'=logit(m[0,0]*m[0,1])*(1-logit(m[0,0]*m[0,1]))*m[0,0]")
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

    @Test
    fun `test matrix element case 31504720`() {
        val expr = matrixVariable<Double>(1,1)*matrixVariable<Double>(1,1)
        instantiateVariables(expr[0,0]).assertString("[0,0]*[0,0]")
    }

    @Test
    fun `test expand matrix elements case 208388574`() {
        val x2 by pow(variable<Double>(0.0),1.0)
        instantiateVariables(x2).assertString("x2=pow(0,1)")
    }

    @Test
    fun `test expand matrix elements case 2119630920`() {
        val x8 by logit(0.0)
        instantiateVariables(x8).assertString("x8=logit(0)")
    }

    @Test
    fun `test expand matrix elements case 1741878727`() {
        val learningRate = 0.1
        val input by matrixVariable<Double>(1, 2)
        val w0 by matrixVariable<Double>(input.rows + 1, 2)
        val h1 by logit(w0 * (input stack 1.0))
        val w1 by matrixVariable<Double>(w0.rows + 1, 1)
        val output by logit(w1 * (h1 stack 1.0))
        val target by matrixVariable<Double>(output.columns, output.rows)
        val error by summation(learningRate * pow(target - output, 2.0))
        instantiateVariables(error)
    }

    @Test
    fun `test expand matrix elements case 263083254`() {
        val x4 by pow(matrixVariable<Double>(1,1),variable<Double>(0.0))
        instantiateVariables(x4).assertString("x4=pow(matrix(1x1),0)")
    }

    @Test
    fun `test expand matrix elements case 562689906`() {
        val x13 by 1.0*matrixVariable<Double>(1,1)
        instantiateVariables(x13).assertString("x13=1*matrix(1x1)")
    }

    @Test
    fun `test expand matrix elements case 2008884022`() {
        val x15 by matrixVariable<Double>(1,1)*matrixVariable<Double>(1,1)
        instantiateVariables(x15).assertString("x15=matrix(1x1)*matrix(1x1)")
    }

    @Test
    fun `test expand matrix elements case 77198392`() {
        val x16: Matrix<Double> by matrixVariable<Double>(1,1) stack 1.0
        instantiateVariables(x16).assertString("x16=matrix(1x1) stack 1")
    }

    @Test
    fun `test expand matrix elements case 588180263`() {
        val x24 by logit(matrixVariable<Double>(1,1))
        instantiateVariables(x24).assertString("x24=logit(matrix(1x1))")
    }

    @Test
    fun `test differentiate case 263083254`() {
        val x4 by pow(matrixVariable<Double>(1,1),variable<Double>(0.0))
        differentiate(x4).assertString("x4'=pow([0,0],0)")
    }

    @Test
    fun `test differentiate case 1704765119`() {
        val x4: Matrix<Double> by pow(matrixVariable<Double>(1,1), variable<Double>())
        val x7 by x4[0,0]
        differentiate(x7).assertString("x7'=pow([0,0],0)")
    }

    @Test
    fun `test differentiate case 2008884022`() {
        val x15 by matrixVariable<Double>(1,1)*matrixVariable<Double>(1,1)
        differentiate(x15).assertString("x15'=[0,0]*[0,0]")
    }

    @Test
    fun `test differentiate case 774036348`() {
        val learningRate = 0.1
        val input by matrixVariable<Double>(1, 2)
        val w0 by matrixVariable<Double>(input.rows + 1, 2)
        val h1 by logit(w0 * (input stack 1.0))
        val w1 by matrixVariable<Double>(w0.rows + 1, 2)
        val output by logit(w1 * (h1 stack 1.0))
        val target by matrixVariable<Double>(output.columns, output.rows)
        val error by summation(learningRate * pow(target - output, 2.0))
        val gradientW0 by d(error) / d(w0)
        differentiate(gradientW0)
    }


    @Test
    fun `generate new tests`() {
        val x2: Scalar<Double> by pow(variable<Double>(), 1.0)
        val x3: Scalar<Double> by pow(variable<Double>(), variable<Double>())
        val x4: Matrix<Double> by pow(matrixVariable<Double>(1,1), variable<Double>())
        val x7: Scalar<Double> by x4[0, 0]
        val x8: Scalar<Double> by logit(0.0)
        val x9: Scalar<Double> by logit(variable<Double>())
        val x10: Scalar<Double> by variable<Double>() * 1.0
        val x11: Scalar<Double> by 1.0 * variable<Double>()
        val x12: Scalar<Double> by variable<Double>() * variable<Double>()
        val x13: Matrix<Double> by 1.0 * matrixVariable<Double>(1,1)
        val x14: Matrix<Double> by matrixVariable<Double>(1,1) * 1.0
        val x15: Matrix<Double> by matrixVariable<Double>(1,1) * matrixVariable<Double>(1,1)
        val x16: Matrix<Double> by matrixVariable<Double>(1,1) stack 1.0
        val x17: Matrix<Double> by 1.0 stack matrixVariable<Double>(1,1)
        val x18: Matrix<Double> by matrixVariable<Double>(1,1) stack matrixVariable<Double>(1,1)
        val x19: Matrix<Double> by matrixVariable<Double>(1,1) stack variable<Double>()
        val x20: Matrix<Double> by variable<Double>() stack matrixVariable<Double>(1,1)
        val x22: Matrix<Double> by variable<Double>() * matrixVariable<Double>(1,1)
        val x23: Matrix<Double> by matrixVariable<Double>(1,1) * variable<Double>()
        val x24: Matrix<Double> by logit(matrixVariable<Double>(1,1))
        val x25: Scalar<Double> by variable<Double>()
        val x26: Scalar<Double> by variable(0.0)
        val x27: Scalar<String> by variable("snake")
        val x28: Scalar<Double> by variable(0.0) + variable(0.0)
        val x29: Scalar<Double> by variable(0.0) * variable(0.0)
        val learningRate = 0.1
        val input by matrixVariable<Double>(1, 2)
        val w0 by matrixVariable<Double>(input.rows + 1, 2)
        val h1 by logit(w0 * (input stack 1.0))
        val w1 by matrixVariable<Double>(w0.rows + 1, 2)
        val output by logit(w1 * (h1 stack 1.0))
        val target by matrixVariable<Double>(output.columns, output.rows)
        val error by summation(learningRate * pow(target - output, 2.0))
        val gradientW0 by d(error) / d(w0)
        val gradientW1 by d(error) / d(w1)
        val all = listOf(x2, x3, x4, x7, x8, x9, x10, x11, x12, x13, x14, x15, x16, x17, x18,
                         x19, x20, x22, x23, x24, x25, x26, x27, x28, x29,
                         input, w0, h1, w1, output, target, error, gradientW0, gradientW1)
        fun identifierOf(structure : String) = structure.substringAfter("val ").substringBefore(" ")
        for(expr in all) {
            expr.toString()
            val structure = expr.renderStructure()
            val id = identifierOf(structure)
            try {
                instantiateVariables(expr)
            } catch (e : Exception) {
                val sb = StringBuilder()
                expr.renderStructure()
                sb.append("@Test\n")
                sb.append("fun `test instantiate variables case ${structure.hashCode().absoluteValue}`() {\n")
                sb.append("    $structure\n")
                sb.append("    instantiateVariables($id).assertString(\"\")\n")
                sb.append("}\n")
                println(sb)
                throw e
            }
            try {
                differentiate(expr)
            } catch (e : Exception) {
                val sb = StringBuilder()
                expr.renderStructure()
                sb.append("@Test\n")
                sb.append("fun `test differentiate case ${structure.hashCode().absoluteValue}`() {\n")
                sb.append("    $structure\n")
                sb.append("    differentiate($id).assertString(\"\")\n")
                sb.append("}\n")
                println(sb)
                throw e
            }
            try {
                differentiate(expr).render()
            } catch (e : Exception) {
                val sb = StringBuilder()
                expr.renderStructure()
                sb.append("@Test\n")
                sb.append("fun `test differentiate case ${structure.hashCode().absoluteValue}`() {\n")
                sb.append("    $structure\n")
                sb.append("    differentiate($id).assertString(\"\")\n")
                sb.append("}\n")
                println(sb)
                throw e
            }

            if(expr is Matrix) {
                for(column in 0 until expr.columns) {
                    for (row in 0 until expr.rows) {
                        try {
                            expr[column, row]
                        } catch (e : Exception) {
                            val sb = StringBuilder()
                            sb.append("@Test\n")
                            sb.append("fun `test matrix element case ${structure.hashCode().absoluteValue}`() {\n")
                            sb.append("    $structure\n")
                            sb.append("    $id[$column,$row].assertString(\"\")\n")
                            sb.append("}\n")
                            println(sb)
                            throw e
                        }
                    }
                }
            }
        }

    }
}

