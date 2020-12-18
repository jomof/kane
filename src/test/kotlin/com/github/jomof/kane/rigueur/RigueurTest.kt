package com.github.jomof.kane.rigueur

import org.junit.Test
import kotlin.math.absoluteValue
import kotlin.math.pow

class RigueurTest {

    private fun Any.assertString(expected : String) {
        val actual = toString().trim('\n')
        val expectedTrimmed = expected.trim('\n')
        assert(actual == expectedTrimmed) {
            "actual:   $actual\nexpected: $expectedTrimmed"
        }
    }

    private fun Double.assertNear(expected:Double) {
        val diff = (this - expected).pow(2.0)
        assert(diff < 0.00000001) {
            "$this was not close enough to $expected"
        }
    }

    @Test
    fun `data matrix basics`() {
        val m1 by matrixOf(3,2,
            1.0, 2.0, 3.0,
            4.0, 5.0, 6.0)
        m1.assertString("""
            m1
            ------
            1|2|3
            4|5|6
        """.trimIndent())
        val m2 by matrixVariable<Double>(3, 4).toDataMatrix()
        m2.assertString("""
            m2
            ------
            [0,0]|[1,0]|[2,0]
            [0,1]|[1,1]|[2,1]
            [0,2]|[1,2]|[2,2]
            [0,3]|[1,3]|[2,3]
        """.trimIndent())
        val tableau = tableauOf(m1,m2)
        tableau.assertString("""
            m1
            ------
            1|2|3
            4|5|6
            
            m2
            ------
            [0,0]|[1,0]|[2,0]
            [0,1]|[1,1]|[2,1]
            [0,2]|[1,2]|[2,2]
            [0,3]|[1,3]|[2,3]
        """.trimIndent())
    }

    @Test
    fun `common sub expressions ordering of associative`() {
        val a by variable<Double>()
        val b by variable<Double>()
        val m1 by matrixOf(3, 4) { a + b + b + a}
        val subs = m1.extractCommonSubExpressions()
        subs.assertString("""
            c0=a+b
            c1=c0+c0
            
            m1
            ------
            c1|c1|c1
            c1|c1|c1
            c1|c1|c1
            c1|c1|c1
        """.trimIndent())

    }

    @Test
    fun `common sub expressions`() {
        val a by variable<Double>()
        val b by variable<Double>()
        val m1 by matrixOf(3, 4) { a + b + a}
        val m2 by matrixOf(3, 4) { b + a + b }
        val count = m1.countSubExpressions()
        val subs = m1.extractCommonSubExpressions()
        subs.assertString("""
            c0=a+b
            c1=c0+a
            
            m1
            ------
            c1|c1|c1
            c1|c1|c1
            c1|c1|c1
            c1|c1|c1
        """.trimIndent())
        val stacked by m1 stack m2
        println(stacked.countSubExpressions())
        val subs2 = stacked.extractCommonSubExpressions()
        subs2.assertString("""
            c0=a+b
            c1=c0+a
            c2=c0+b
            
            stacked
            ------
            c1|c1|c1
            c1|c1|c1
            c1|c1|c1
            c1|c1|c1
            c2|c2|c2
            c2|c2|c2
            c2|c2|c2
            c2|c2|c2
        """.trimIndent())
    }

    @Test
    fun `common sub expressions expand associative`() {
        val a by variable<Double>()
        val b by variable<Double>()
        val m1 by matrixOf(1,1) { a + b }
        val m2 by matrixOf(1,1) { b + a }
        val stacked by m1 stack m2
        println(stacked.countSubExpressions())
        val subs2 = stacked.extractCommonSubExpressions()
        subs2.assertString("""
            c0=a+b

            stacked
            ------
            c0
            c0
        """.trimIndent())
    }

    @Test
    fun `common sub expressions unary`() {
        val a by variable<Double>()
        val b by variable<Double>()
        val m1 by matrixOf(1,3) { logit(a + b) + b + a }
        println(m1.countSubExpressions())
        val common = m1.extractCommonSubExpressions()
        common.assertString("""
            c0=b+a
            c1=c0+logit(c0)
            
            m1
            ------
            c1
            c1
            c1
        """.trimIndent())
    }

    @Test
    fun mult() {
        val m1 by matrixOf(10, 1) { constant(1.0) }
        val m2 by matrixOf(1, 10) { constant(2.0) }
        val m3 by m1 * m2
        m3.assertString("m3=m1*m2")
        assert(m3.rows == 1 && m3.columns == 1)
        val e1 by m3[0,0]
        println(e1)
    }

    @Test
    fun `multiplication bug`() {
        val input by matrixVariable<Double>(1, 1)
        val w0 by matrixVariable<Double>(input.rows + 1, 1)
        val h1 by logit(w0 * (input stack 1.0))
        h1.toDataMatrix().assertString("h1=logit(w0[0,0]*input[0,0]+w0[1,0]*1)")
    }

    @Test
    fun `repro tiny diff problem`() {
        val x by variable<Double>()
        val m by variable<Double>()
        val b by variable<Double>()
        val t by variable<Double>()
        val error by 0.5 * pow(t - ((m * x) + b), 2.0)
        val dm by differentiate(d(error)/d(m))
        dm.assertString("dm=-1*(t-(m*x+b))*x")
    }

    @Test
    fun `tiny linear regression`() {
        val x by variable<Double>()
        val m by variable<Double>()
        val b by variable<Double>()
        val y by m * x + b
        val target by variable<Double>()
        val error by 0.5 * pow(target - y, 2.0)
        val dm by differentiate(d(error)/d(m))
        val db by differentiate(d(error)/d(b))
        println(error)
        println(dm)
        println(db)
        val tab = tableauOf(dm,db).extractCommonSubExpressions()
        tab.assertString("""
            c0=m*x
            c1=c0+b
            c2=target-c1
            c3=-1*c2
            c4=c3*x
            dm=c4
            db=c3
        """.trimIndent())
        val map = mutableMapOf(
            "b" to 0.0,
            "m" to 0.0,
        )
        val learningRate = 0.001
        (0 until 10000).forEach {
            (-5 until 5).forEach { point ->
                map["x"] = point.toDouble()
                map["target"] = -500 * point.toDouble() + 0.2
                tab.eval(map)
                map["b"] = map.getValue("b") - learningRate * map.getValue("db")
                map["m"] = map.getValue("m") - learningRate * map.getValue("dm")
            }
        }
        map["m"]!!.assertNear(-500.0)
        map["b"]!!.assertNear(0.2)
        println(map)
    }

    @Test
    fun mlp() {
        val learningRate = 0.1
        for(count0 in 5 until 5) {
            val inputs = 2
            //val count0 = 20
            val outputs = 2
            val input by matrixVariable<Double>(1, inputs)
            val w0 by matrixVariable<Double>(input.rows + 1, count0)
            val h1 by logit(w0 * (input stack 1.0))
            val w1 by matrixVariable<Double>(w0.rows + 1, outputs)
            val output by logit(w1 * (h1 stack 1.0))
            val target by matrixVariable<Double>(output.columns, output.rows)
            val error by summation(0.5 * pow(target - output, 2.0))
            val gradientW0 by differentiate(d(error) / d(w0))
            val gradientW1 by differentiate(d(error) / d(w1))
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
//        println(instantiateVariables(h1.toDataMatrix()))
//        println(h1)
//        println(h1.toDataMatrix())
            //println(differentiate(d(h1[0,2])/d(w0[0,0])))
//        println(error)
//        println(gradientW1)
            val tab = tableauOf(
                output,
                error,
                gradientW0,
                gradientW1
            )
            val common = tab.extractCommonSubExpressions()
            val size = common.children.size
            //println(common)
            println("$count0 => $size ${(size + 0.0) / count0}")
            //         println(instantiateVariables(gradientW0))
//        println(instantiateMatrixElements(gradientW1))
//        val common by listOf(output, gradientW0, gradientW1).decompose()
//        println()
//        println(common)
        }
    }


    @Test
    fun `repro multiplication bug`() {
        val input by matrixVariable<Double>(1, 1)
        val w0 by matrixVariable<Double>(input.rows + 1, 3)
        val h1 by logit(w0 * (input stack 1.0))
        val w1 by matrixVariable<Double>(w0.rows + 1, 1)
        val output by logit(w1 * (h1 stack 1.0))
        val target by matrixVariable<Double>(output.columns, output.rows)
        val error by summation(0.5 * pow(target - output, 2.0))
        val gradientW0 by d(error) / d(w0)
        differentiate(gradientW0)
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
        instantiateVariables(pe).assertString("pe=m[1,2]²")
        instantiateVariables(p[1,2]).assertString("m[1,2]²")
        val column by matrixVariable<Double>(1,3)
        val s by 1.0 stack column
        s.assertString("s=1 stack column")
        instantiateVariables(s[0,0]).assertString("1")
        instantiateVariables(s[0,1]).assertString("column[0,0]")
        val a1 by s[0,0]
        instantiateVariables(a1).assertString("a1=1")
    }

    @Test
    fun `requires parens`() {
        fun check(parent : BinaryOp, child : BinaryOp, childIsRight: Boolean, expect : Boolean?) {
            val result = binaryRequiresParents(parent, child, childIsRight)
            assert(result == expect) {
                binaryRequiresParents(parent, child, childIsRight)
                "$result != $expect"
            }
        }
        check(MINUS, PLUS, childIsRight = false, expect = false)
        check(MINUS, PLUS, childIsRight = true, expect = true)
        check(PLUS, PLUS, childIsRight = false, expect = false)
        check(PLUS, PLUS, childIsRight = true, expect = false)
        check(TIMES, TIMES, childIsRight = false, expect = false)
        check(TIMES, TIMES, childIsRight = true, expect = false)
        check(MINUS, MINUS, childIsRight = false, expect = false)
        check(MINUS, MINUS, childIsRight = true, expect = true)
        check(PLUS, MINUS, childIsRight = false, expect = true)
        check(PLUS, MINUS, childIsRight = true, expect = true)
        check(PLUS, TIMES, childIsRight = false, expect = false)
        check(PLUS, TIMES, childIsRight = true, expect = false)
        check(MINUS, TIMES, childIsRight = false, expect = false)
        check(MINUS, TIMES, childIsRight = true, expect = false)

    }

    @Test
    fun `variable rendering`() {
        variable<Double>().assertString("?")
        variable<Int>().assertString("?")
        val a by variable<Double>()
        a.assertString("a")
        val b by variable<Double>()
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
        (m stack 1.0).assertString("m stack 1|1|1|1|1")
        (m stack m stack m).assertString("m stack m stack m")
        ((m stack m) stack m).assertString("m stack m stack m")
        (m stack (m stack m)).assertString("m stack m stack m")
    }

    @Test
    fun `matrix chain rule case 8668`() {
        val m by matrixVariable<Double>(1, 2)
        val x by m[0, 0]
        val y by m[0, 1]
        val f1 by pow(x, 2.0) + pow(y, 3.0)
        val d1 by d(f1) / d(x)
        differentiate(d1).assertString("d1'=2*m[0,0]")
    }

    @Test
    fun `matrix chain rule`() {
        val m by matrixVariable<Double>(1, 2)
        val x by m[0,0]
        val y by m[0,1]
        val f1 by pow(x, 2.0) + pow(y, 3.0)
        f1.assertString("f1=m[0,0]²+m[0,1]³")
        val d1 by d(f1) / d(x)
        d1.assertString("d1=d(m[0,0]²+m[0,1]³)/d(m[0,0])")
        differentiate(d1).assertString("d1'=2*m[0,0]")
        val d2 by d(f1) / d(y)
        d2.assertString("d2=d(m[0,0]²+m[0,1]³)/d(m[0,1])")
        differentiate(d2).assertString("d2'=3*m[0,1]²")
        val f2 by pow(x, 2.0) * pow(y, 3.0)
        f2.assertString("f2=m[0,0]²*m[0,1]³")
        val df2dx by d(f2) / d(x)
        df2dx.assertString("df2dx=d(m[0,0]²*m[0,1]³)/d(m[0,0])")
        differentiate(df2dx).assertString("df2dx'=2*m[0,0]*m[0,1]³")
        val df2dy by d(f2) / d(y)
        df2dy.assertString("df2dy=d(m[0,0]²*m[0,1]³)/d(m[0,1])")
        differentiate(df2dy).assertString("df2dy'=3*m[0,0]²*m[0,1]²")
        val assigned by differentiate(df2dy)
        assigned.assertString("assigned=3*m[0,0]²*m[0,1]²")
        val f3 by logit(x * y)
        f3.assertString("f3=logit(m[0,0]*m[0,1])")
        val df3dx by d(f3) / d(x)
        df3dx.assertString("df3dx=d(logit(m[0,0]*m[0,1]))/d(m[0,0])")
        differentiate(df3dx).assertString("df3dx'=logit(m[0,0]*m[0,1])*(1-logit(m[0,0]*m[0,1]))*m[0,1]")
        val df3dy by d(f3) / d(y)
        df3dy.assertString("df3dy=d(logit(m[0,0]*m[0,1]))/d(m[0,1])")
        differentiate(df3dy).assertString("df3dy'=logit(m[0,0]*m[0,1])*(1-logit(m[0,0]*m[0,1]))*m[0,0]")

//        val xyz by d(f3) / d(m)
//        differentiate(xyz).assertString("")
    }

    @Test
    fun `differentiate named`() {
        val x by variable<Double>()
        val y by x
        instantiateVariables(y * y).renderStructure().assertString("val expr = x*x")
        val z by y * y
        z.renderStructure().assertString("val z by NamedScalar(\"y\", x)*NamedScalar(\"y\", x)")
        instantiateVariables(z).renderStructure().assertString("val z by x*x")
        differentiate(d(y * y) / d(x)).renderStructure().assertString("val expr = x+x")
        differentiate(d(pow(y, 3.0)) / d(x)).renderStructure().assertString("val expr = 3.0*pow(x,2.0)")
    }

    @Test
    fun `chain rule structure`() {
        val x by variable<Double>()
        val y by variable<Double>()
        val f1 by pow(x, 2.0) + pow(y, 3.0)
        val d1 by d(f1) / d(x)
        differentiate(d1).renderStructure().assertString("val d1' by 2.0*x")
    }

    @Test
    fun `chain rule`() {
        val x by variable<Double>()
        val y by variable<Double>()
        val f1 by pow(x, 2.0) + pow(y, 3.0)
        f1.assertString("f1=x²+y³")
        val d1 by d(f1) / d(x)
        d1.assertString("d1=d(x²+y³)/d(x)")
        differentiate(d1).assertString("d1'=2*x")
        val d2 by d(f1) / d(y)
        d2.assertString("d2=d(x²+y³)/d(y)")
        differentiate(d2).assertString("d2'=3*y²")
        val f2 by pow(x, 2.0) * pow(y, 3.0)
        f2.assertString("f2=x²*y³")
        val df2dx by d(f2) / d(x)
        df2dx.assertString("df2dx=d(x²*y³)/d(x)")
        differentiate(df2dx).assertString("df2dx'=2*x*y³")
        val df2dy by d(f2) / d(y)
        df2dy.assertString("df2dy=d(x²*y³)/d(y)")
        differentiate(df2dy).assertString("df2dy'=3*x²*y²")
        val assigned by differentiate(df2dy)
        assigned.assertString("assigned=3*x²*y²")
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
        val x2 by pow(variable<Double>(),1.0)
        instantiateVariables(x2).assertString("x2=?¹")
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
        val x4 by pow(matrixVariable<Double>(1,1),variable<Double>())
        instantiateVariables(x4).assertString("x4=pow(matrix(1x1),?)")
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
        val x4 by pow(matrixVariable<Double>(1,1),variable<Double>())
        differentiate(x4).assertString("x4=pow(matrix(1x1),?)")
    }

    @Test
    fun `test differentiate case 1704765119`() {
        val x4: Matrix<Double> by pow(matrixVariable<Double>(1,1), variable<Double>())
        val x7 by x4[0,0]
        differentiate(x7).assertString("x7=pow([0,0],?)")
    }

    @Test
    fun `test differentiate case 2008884022`() {
        val x15 by matrixVariable<Double>(1,1)*matrixVariable<Double>(1,1)
        differentiate(x15).assertString("x15=matrix(1x1)*matrix(1x1)")
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
    fun `expr replace identity 650393921`() {
        val x16 by matrixVariable<Double>(1,1) stack 1.0
        val v = variable<Double>()
        val replaced = x16.replace(v,v)
        assert(x16 == x16)
        assert(replaced == replaced)
        assert(x16 == replaced) {
            "[$x16] != [$replaced]"
        }
    }

    @Test
    fun `test extract common sub expressions case 62950920`() {
        val learningRate = 0.1
        val input by matrixVariable<Double>(1, 2)
        val w0 by matrixVariable<Double>(input.rows + 1, 2)
        val h1 by logit(w0 * (input stack 1.0))
        val w1 by matrixVariable<Double>(w0.rows + 1, 2)
        val output by logit(w1 * (h1 stack 1.0))
        val target by matrixVariable<Double>(output.columns, output.rows)
        val error by summation(learningRate * pow(target - output, 2.0))
        val gradientW0 by d(error) / d(w0)
        gradientW0.extractCommonSubExpressions()
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
        val x26: Scalar<Double> by variable<Double>()
        val x27: Scalar<String> by variable<String>()
        val x28: Scalar<Double> by variable<Double>() + variable<Double>()
        val x29: Scalar<Double> by variable<Double>() * variable<Double>()
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
        val x by variable<Double>()
        val q by variable<Double>()
        val m by variable<Double>()
        val b by variable<Double>()
        val y by q * pow(x,2.0) + m * x + b
        val polyTarget by variable<Double>()
        val polyError = pow(y - polyTarget, 2.0)
        val dq by differentiate(d(polyError)/d(q))
        val dm by differentiate(d(polyError)/d(m))
        val db by differentiate(d(polyError)/d(b))
        val all = listOf(x2, x3, x4, x7, x8, x9, x10, x11, x12, x13, x14, x15, x16, x17, x18,
                         x19, x20, x22, x23, x24, x25, x26, x27, x28, x29,
                         input, w0, h1, w1, output, target, error, gradientW0, gradientW1,
                         x, q, m, b, y, polyTarget, polyError, dq, dm, db)
        fun identifierOf(structure : String) = structure.substringAfter("val ").substringBefore(" ")
        for(expr in all) {
            expr.toString()
            expr.countSubExpressions()

            val structure = expr.renderStructure()
            val id = identifierOf(structure)
            assert(expr == expr)
            expr.reduceArithmetic()
            if (expr.type == Double::class) {
                if (expr is Named) {
                    try {
                        expr.extractCommonSubExpressions()
                    } catch (e : Exception) {
                        val sb = StringBuilder()
                        expr.renderStructure()
                        sb.append("@Test\n")
                        sb.append("fun `test extract common sub expressions case ${structure.hashCode().absoluteValue}`() {\n")
                        sb.append("    $structure\n")
                        sb.append("    $id.extractCommonSubExpressions().assertString(\"\")\n")
                        sb.append("}\n")
                        println(sb)
                        throw e
                    }
                }
                if (expr is Matrix) {
                    val named by (expr as Matrix<Double>)
                    named.extractCommonSubExpressions()
                }
                if (expr is Scalar) {
                    val named by (expr as Scalar<Double>)
                    named.extractCommonSubExpressions()
                }
                val v = variable<Double>()
                val replaced = (expr as Expr<Double>).replace(v, v)
                if (replaced != expr) {
                    val sb = StringBuilder()
                    sb.append("@Test\n")
                    sb.append("fun `expr replace identity ${structure.hashCode().absoluteValue}`() {\n")
                    sb.append("    $structure\n")
                    sb.append("    val v = variable<Double>()\n")
                    sb.append("    val replaced = $id.replace(v,v)\n")
                    sb.append("    assert($id == $id)\n")
                    sb.append("    assert(replaced == replaced)\n")
                    sb.append("    assert($id == replaced) { \"[\$$id] != [\$replaced]\" }\n")
                    sb.append("}\n")
                    println(sb)
                }
            } else if (expr.type == String::class) {
            } else {
                assert(false)
            }

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

